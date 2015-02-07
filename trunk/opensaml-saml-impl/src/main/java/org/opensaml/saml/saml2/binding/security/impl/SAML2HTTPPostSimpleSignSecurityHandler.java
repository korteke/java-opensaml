/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.saml.saml2.binding.security.impl;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.codec.Base64Support;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import net.shibboleth.utilities.java.support.xml.ParserPool;
import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.binding.security.impl.BaseSAMLSimpleSignatureSecurityHandler;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoCriterion;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.google.common.base.Strings;

/**
 * Message handler which evaluates simple "blob" signatures according to the SAML 2 HTTP-POST-SimpleSign binding.
 */
public class SAML2HTTPPostSimpleSignSecurityHandler extends BaseSAMLSimpleSignatureSecurityHandler {

    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(SAML2HTTPPostSimpleSignSecurityHandler.class);

    /** Parser pool to use to process KeyInfo request parameter. */
    @NonnullAfterInit private ParserPool parserPool;

    /** KeyInfo resolver to use to process KeyInfo request parameter. */
    @NonnullAfterInit private KeyInfoCredentialResolver keyInfoResolver;
    
    /**
     * Get the parser pool.
     * 
     * @return Returns the parser pool.
     */
    @NonnullAfterInit public ParserPool getParserPool() {
        return parserPool;
    }

    /**
     * Set the parser pool.
     * 
     * @param newParserPool The parser to set.
     */
    public void setParser(@Nonnull final ParserPool newParserPool) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        parserPool = Constraint.isNotNull(newParserPool, "ParserPool cannot be null");
    }

    /**
     * Get the KeyInfo credential resolver.
     * 
     * @return Returns the keyInfoResolver.
     */
    @NonnullAfterInit public KeyInfoCredentialResolver getKeyInfoResolver() {
        return keyInfoResolver;
    }

    /**
     * Set the KeyInfo credential resolver.
     * 
     * @param newKeyInfoResolver The keyInfoResolver to set.
     */
    public void setKeyInfoResolver(@Nonnull final KeyInfoCredentialResolver newKeyInfoResolver) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        keyInfoResolver = Constraint.isNotNull(newKeyInfoResolver, "KeyInfoCredentialResolver cannot be null");
    }

    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        Constraint.isNotNull(parserPool, "ParserPool cannot be null");
        Constraint.isNotNull(keyInfoResolver, "KeyInfoCredentialResolver cannot be null");
    }

    /** {@inheritDoc} */
    @Override
    protected boolean ruleHandles(@Nonnull final MessageContext messageContext) {
        return "POST".equals(getHttpServletRequest().getMethod());
    }

    /** {@inheritDoc} */
    @Override
    @Nullable protected byte[] getSignedContent() throws MessageHandlerException {
        final HttpServletRequest request = getHttpServletRequest();
        
        final StringBuilder builder = new StringBuilder();
        String samlMsg;
        try {
            if (request.getParameter("SAMLRequest") != null) {
                samlMsg = new String(Base64Support.decode(request.getParameter("SAMLRequest")), "UTF-8");
                builder.append("SAMLRequest=" + samlMsg);
            } else if (request.getParameter("SAMLResponse") != null) {
                samlMsg = new String(Base64Support.decode(request.getParameter("SAMLResponse")), "UTF-8");
                builder.append("SAMLResponse=" + samlMsg);
            } else {
                log.warn("Could not extract either a SAMLRequest or a SAMLResponse from the form control data");
                throw new MessageHandlerException("Extract of SAMLRequest or SAMLResponse from form control data");
            }
        } catch (final UnsupportedEncodingException e) {
            // All JVM's required to support UTF-8
        }

        if (request.getParameter("RelayState") != null) {
            builder.append("&RelayState=" + request.getParameter("RelayState"));
        }

        builder.append("&SigAlg=" + request.getParameter("SigAlg"));

        String constructed = builder.toString();
        if (Strings.isNullOrEmpty(constructed)) {
            log.warn("Could not construct signed content string from form control data");
            return null;
        }
        log.debug("Constructed signed content string for HTTP-Post-SimpleSign {}", constructed);

        try {
            return constructed.getBytes("UTF-8");
        } catch (final UnsupportedEncodingException e) {
            // All JVM's required to support UTF-8
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull @NonnullElements protected List<Credential> getRequestCredentials(
            @Nonnull final MessageContext samlContext) throws MessageHandlerException {

        final String kiBase64 = getHttpServletRequest().getParameter("KeyInfo");
        if (Strings.isNullOrEmpty(kiBase64)) {
            log.debug("Form control data did not contain a KeyInfo");
            return Collections.emptyList();
        } else {
            log.debug("Found a KeyInfo in form control data, extracting validation credentials");
        }

        final Unmarshaller unmarshaller =
                XMLObjectProviderRegistrySupport.getUnmarshallerFactory().getUnmarshaller(KeyInfo.DEFAULT_ELEMENT_NAME);
        if (unmarshaller == null) {
            throw new MessageHandlerException("Could not obtain a KeyInfo unmarshaller");
        }

        final ByteArrayInputStream is = new ByteArrayInputStream(Base64Support.decode(kiBase64));
        KeyInfo keyInfo = null;
        try {
            final Document doc = getParserPool().parse(is);
            keyInfo = (KeyInfo) unmarshaller.unmarshall(doc.getDocumentElement());
        } catch (final XMLParserException e) {
            log.warn("Error parsing KeyInfo data", e);
            throw new MessageHandlerException("Error parsing KeyInfo data", e);
        } catch (final UnmarshallingException e) {
            log.warn("Error unmarshalling KeyInfo data", e);
            throw new MessageHandlerException("Error unmarshalling KeyInfo data", e);
        }

        if (keyInfo == null) {
            log.warn("Could not successfully extract KeyInfo object from the form control data");
            return Collections.emptyList();
        }

        final List<Credential> credentials = new ArrayList<>();
        final CriteriaSet criteriaSet = new CriteriaSet(new KeyInfoCriterion(keyInfo));
        try {
            for (final Credential cred : keyInfoResolver.resolve(criteriaSet)) {
                credentials.add(cred);
            }
        } catch (final ResolverException e) {
            log.warn("Error resolving credentials from KeyInfo", e);
            throw new MessageHandlerException("Error resolving credentials from KeyInfo", e);
        }

        return credentials;
    }

}