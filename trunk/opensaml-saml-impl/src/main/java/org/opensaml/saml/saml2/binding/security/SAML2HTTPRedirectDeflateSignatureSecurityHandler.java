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

import java.io.UnsupportedEncodingException;

import net.shibboleth.utilities.java.support.net.UriSupport;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.security.BaseSAMLSimpleSignatureSecurityHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * Message handler which evaluates simple "blob" signatures according to the SAML 2 HTTP-Redirect DEFLATE binding.
 */
public class SAML2HTTPRedirectDeflateSignatureSecurityHandler extends BaseSAMLSimpleSignatureSecurityHandler {

    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(SAML2HTTPRedirectDeflateSignatureSecurityHandler.class);

    /** {@inheritDoc} */
    protected boolean ruleHandles(MessageContext<SAMLObject> messgaeContext)
            throws MessageHandlerException {
        return "GET".equals(getHttpServletRequest().getMethod());
    }

    /** {@inheritDoc} */
    protected byte[] getSignedContent() throws MessageHandlerException {
        // We need the raw non-URL-decoded query string param values for HTTP-Redirect DEFLATE simple signature
        // validation.
        // We have to construct a string containing the signature input by accessing the
        // request directly. We can't use the decoded parameters because we need the raw
        // data and URL-encoding isn't canonical.
        String queryString = getHttpServletRequest().getQueryString();
        log.debug("Constructing signed content string from URL query string {}", queryString);

        String constructed = buildSignedContentString(queryString);
        if (Strings.isNullOrEmpty(constructed)) {
            log.warn("Could not extract signed content string from query string");
            return null;
        }
        log.debug("Constructed signed content string for HTTP-Redirect DEFLATE {}", constructed);

        try {
            return constructed.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            // JVM is required to support UTF-8
        }
        return null;
    }

    /**
     * Extract the raw request parameters and build a string representation of the content that was signed.
     * 
     * @param queryString the raw HTTP query string from the request
     * @return a string representation of the signed content
     * @throws MessageHandlerException thrown if there is an error during request processing
     */
    private String buildSignedContentString(String queryString) throws MessageHandlerException {
        StringBuilder builder = new StringBuilder();

        // One of these two is mandatory
        if (!appendParameter(builder, queryString, "SAMLRequest")) {
            if (!appendParameter(builder, queryString, "SAMLResponse")) {
                log.warn("Could not extract either a SAMLRequest or a SAMLResponse from the query string");
                throw new MessageHandlerException("Extract of SAMLRequest or SAMLResponse from query string failed");
            }
        }
        // This is optional
        appendParameter(builder, queryString, "RelayState");
        // This is mandatory, but has already been checked in superclass
        appendParameter(builder, queryString, "SigAlg");

        return builder.toString();
    }

    /**
     * Find the raw query string parameter indicated and append it to the string builder.
     * 
     * The appended value will be in the form 'paramName=paramValue' (minus the quotes).
     * 
     * @param builder string builder to which to append the parameter
     * @param queryString the URL query string containing parameters
     * @param paramName the name of the parameter to append
     * @return true if parameter was found, false otherwise
     */
    private boolean appendParameter(StringBuilder builder, String queryString, String paramName) {
        String rawParam = UriSupport.getRawQueryStringParameter(queryString, paramName);
        if (rawParam == null) {
            return false;
        }

        if (builder.length() > 0) {
            builder.append('&');
        }

        builder.append(rawParam);

        return true;
    }
}
