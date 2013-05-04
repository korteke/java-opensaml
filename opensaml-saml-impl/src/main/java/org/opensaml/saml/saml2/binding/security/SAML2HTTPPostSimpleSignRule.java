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

package org.opensaml.saml.saml2.binding.security;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.shibboleth.utilities.java.support.codec.Base64Support;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
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
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.security.BaseSAMLSimpleSignatureSecurityPolicyRule;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoCriterion;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.google.common.base.Strings;

/**
 * Security policy which evaluates simple "blob" signatures according to the SAML 2 HTTP-POST-SimpleSign binding.
 */
public class SAML2HTTPPostSimpleSignRule extends BaseSAMLSimpleSignatureSecurityPolicyRule {

    /** Logger. */
    private Logger log = LoggerFactory.getLogger(SAML2HTTPPostSimpleSignRule.class);

    /** Parser pool to use to process KeyInfo request parameter. */
    private ParserPool parserPool;

    /** KeyInfo resolver to use to process KeyInfo request parameter. */
    private KeyInfoCredentialResolver keyInfoResolver;
    
    /**
     * Get the parser pool.
     * 
     * @return Returns the parser pool.
     */
    public ParserPool getParserPool() {
        return parserPool;
    }

    /**
     * Set the parser pool.
     * 
     * @param newParserPool The parser to set.
     */
    public void setParser(ParserPool newParserPool) {
        parserPool = newParserPool;
    }

    /**
     * Get the KeyInfo credential resolver.
     * 
     * @return Returns the keyInfoResolver.
     */
    public KeyInfoCredentialResolver getKeyInfoResolver() {
        return keyInfoResolver;
    }

    /**
     * Set the KeyInfo credential resolver.
     * 
     * @param newKeyInfoResolver The keyInfoResolver to set.
     */
    public void setKeyInfoResolver(KeyInfoCredentialResolver newKeyInfoResolver) {
        keyInfoResolver = newKeyInfoResolver;
    }

    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        Constraint.isNotNull(parserPool, "ParserPool must be supplied");
        Constraint.isNotNull(keyInfoResolver, "KeyInfoCredentialResolver must be supplied");
    }

    /** {@inheritDoc} */
    protected boolean ruleHandles(HttpServletRequest request, MessageContext<SAMLObject> messageContext) {
        return "POST".equals(request.getMethod());
    }

    /** {@inheritDoc} */
    protected byte[] getSignedContent(HttpServletRequest request) throws MessageHandlerException {
        StringBuilder builder = new StringBuilder();
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
        } catch (UnsupportedEncodingException e) {
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
        } catch (UnsupportedEncodingException e) {
            // All JVM's required to support UTF-8
        }
        return null;
    }

    /** {@inheritDoc} */
    protected List<Credential> getRequestCredentials(HttpServletRequest request, MessageContext<SAMLObject> samlContext)
            throws MessageHandlerException {

        String kiBase64 = request.getParameter("KeyInfo");
        if (Strings.isNullOrEmpty(kiBase64)) {
            log.debug("Form control data did not contain a KeyInfo");
            return null;
        } else {
            log.debug("Found a KeyInfo in form control data, extracting validation credentials");
        }

        Unmarshaller unmarshaller =
                XMLObjectProviderRegistrySupport.getUnmarshallerFactory().getUnmarshaller(KeyInfo.DEFAULT_ELEMENT_NAME);
        if (unmarshaller == null) {
            throw new MessageHandlerException("Could not obtain a KeyInfo unmarshaller");
        }

        ByteArrayInputStream is = new ByteArrayInputStream(Base64Support.decode(kiBase64));
        KeyInfo keyInfo = null;
        try {
            Document doc = getParserPool().parse(is);
            keyInfo = (KeyInfo) unmarshaller.unmarshall(doc.getDocumentElement());
        } catch (XMLParserException e) {
            log.warn("Error parsing KeyInfo data", e);
            throw new MessageHandlerException("Error parsing KeyInfo data", e);
        } catch (UnmarshallingException e) {
            log.warn("Error unmarshalling KeyInfo data", e);
            throw new MessageHandlerException("Error unmarshalling KeyInfo data", e);
        }

        if (keyInfo == null) {
            log.warn("Could not successfully extract KeyInfo object from the form control data");
            return null;
        }

        List<Credential> credentials = new ArrayList<Credential>();
        CriteriaSet criteriaSet = new CriteriaSet(new KeyInfoCriterion(keyInfo));
        try {
            for (Credential cred : keyInfoResolver.resolve(criteriaSet)) {
                credentials.add(cred);
            }
        } catch (ResolverException e) {
            log.warn("Error resolving credentials from KeyInfo", e);
            throw new MessageHandlerException("Error resolving credentials from KeyInfo", e);
        }

        return credentials;
    }


}
