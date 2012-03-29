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

package org.opensaml.saml2.binding.security;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.shibboleth.utilities.java.support.codec.Base64Support;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import net.shibboleth.utilities.java.support.xml.ParserPool;
import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.opensaml.common.binding.security.BaseSAMLSimpleSignatureSecurityPolicyRule;
import org.opensaml.core.xml.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.binding.SAMLMessageContext;
import org.opensaml.security.credential.Credential;
import org.opensaml.ws.security.SecurityPolicyException;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoCriterion;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
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
    private ParserPool parser;

    /** KeyInfo resolver to use to process KeyInfo request parameter. */
    private KeyInfoCredentialResolver keyInfoResolver;

    /**
     * Constructor.
     * 
     * @param engine the trust engine to use
     * @param parserPool the parser pool used to parse the KeyInfo request parameter
     * @param keyInfoCredResolver the KeyInfo credential resovler to use to extract credentials from the KeyInfo request
     *            parameter
     */
    public SAML2HTTPPostSimpleSignRule(SignatureTrustEngine engine, ParserPool parserPool,
            KeyInfoCredentialResolver keyInfoCredResolver) {
        super(engine);
        parser = parserPool;
        keyInfoResolver = keyInfoCredResolver;
    }

    /** {@inheritDoc} */
    protected boolean ruleHandles(HttpServletRequest request, SAMLMessageContext samlMsgCtx) {
        return "POST".equals(request.getMethod());
    }

    /** {@inheritDoc} */
    protected byte[] getSignedContent(HttpServletRequest request) throws SecurityPolicyException {
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
                throw new SecurityPolicyException("Extract of SAMLRequest or SAMLResponse from form control data");
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
    protected List<Credential> getRequestCredentials(HttpServletRequest request, SAMLMessageContext samlContext)
            throws SecurityPolicyException {

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
            throw new SecurityPolicyException("Could not obtain a KeyInfo unmarshaller");
        }

        ByteArrayInputStream is = new ByteArrayInputStream(Base64Support.decode(kiBase64));
        KeyInfo keyInfo = null;
        try {
            Document doc = parser.parse(is);
            keyInfo = (KeyInfo) unmarshaller.unmarshall(doc.getDocumentElement());
        } catch (XMLParserException e) {
            log.warn("Error parsing KeyInfo data", e);
            throw new SecurityPolicyException("Error parsing KeyInfo data", e);
        } catch (UnmarshallingException e) {
            log.warn("Error unmarshalling KeyInfo data", e);
            throw new SecurityPolicyException("Error unmarshalling KeyInfo data", e);
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
            throw new SecurityPolicyException("Error resolving credentials from KeyInfo", e);
        }

        return credentials;
    }

}
