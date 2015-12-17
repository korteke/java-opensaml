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

package org.opensaml.security.httpclient;

import static org.opensaml.security.httpclient.HttpClientSecurityConstants.CONTEXT_KEY_CLIENT_TLS_CREDENTIAL;
import static org.opensaml.security.httpclient.HttpClientSecurityConstants.CONTEXT_KEY_CRITERIA_SET;
import static org.opensaml.security.httpclient.HttpClientSecurityConstants.CONTEXT_KEY_HOSTNAME_VERIFIER;
import static org.opensaml.security.httpclient.HttpClientSecurityConstants.CONTEXT_KEY_SERVER_TLS_CREDENTIAL_TRUSTED;
import static org.opensaml.security.httpclient.HttpClientSecurityConstants.CONTEXT_KEY_TLS_CIPHER_SUITES;
import static org.opensaml.security.httpclient.HttpClientSecurityConstants.CONTEXT_KEY_TLS_PROTOCOLS;
import static org.opensaml.security.httpclient.HttpClientSecurityConstants.CONTEXT_KEY_TRUST_ENGINE;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.SSLPeerUnverifiedException;

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.apache.http.client.protocol.HttpClientContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Support class for working with {@link org.apache.http.client.HttpClient} security features.
 */
public final class HttpClientSecuritySupport {
    
    /** Logger. */
    private static final Logger LOG = LoggerFactory.getLogger(HttpClientSecuritySupport.class);
    
    /** Constructor. */
    private HttpClientSecuritySupport() {}
    
    /**
     * Check that trust engine evaluation of the server TLS credential was actually performed when the 
     * scheme is HTTPS.
     * 
     * @param context the current HTTP context instance in use
     * @param scheme the HTTP request scheme
     * @throws SSLPeerUnverifiedException thrown if the TLS credential was not actually evaluated by the trust engine
     */
    public static void checkTLSCredentialEvaluated(@Nonnull final HttpClientContext context, 
            @Nonnull final String scheme) throws SSLPeerUnverifiedException {
        if (context.getAttribute(CONTEXT_KEY_TRUST_ENGINE) != null 
                && "https".equalsIgnoreCase(scheme)) {
            if (context.getAttribute(CONTEXT_KEY_SERVER_TLS_CREDENTIAL_TRUSTED) == null) {
                LOG.warn("Configured TLS trust engine was not used to verify server TLS credential, " 
                        + "the appropriate socket factory was likely not configured");
                throw new SSLPeerUnverifiedException(
                        "Evaluation of server TLS credential with configured TrustEngine was not performed");
            }
        }
    }
    
    /**
     * Marshal the supplied {@link HttpClientSecurityParameters} to the supplied {@link HttpClientContext}.
     * 
     * <p>Existing context values will NOT be replaced by non-null parameter values.</p>
     * 
     * @param context the client context instance
     * @param securityParameters the security parameters instance
     */
    public static void marshalSecurityParameters(@Nonnull final HttpClientContext context, 
            @Nullable final HttpClientSecurityParameters securityParameters) {
        marshalSecurityParameters(context, securityParameters, false);
    }
    
    /**
     * Marshal the supplied {@link HttpClientSecurityParameters} to the supplied {@link HttpClientContext}.
     * 
     * @param context the client context instance
     * @param securityParameters the security parameters instance
     * @param replace whether a non-null security parameter value should replace an existing context value
     */
    public static void marshalSecurityParameters(@Nonnull final HttpClientContext context, 
            @Nullable final HttpClientSecurityParameters securityParameters, boolean replace) {
        if (securityParameters == null) {
            return;
        }
        Constraint.isNotNull(context, "HttpClientContext was null");
        
        if (securityParameters.getCredentialsProvider() != null) {
            if (replace || context.getCredentialsProvider() == null) {
                context.setCredentialsProvider(securityParameters.getCredentialsProvider());
            }
        }
        
        setContextValue(context, CONTEXT_KEY_TRUST_ENGINE, 
                securityParameters.getTLSTrustEngine(), replace);
        
        setContextValue(context, CONTEXT_KEY_CRITERIA_SET,
                securityParameters.getTLSCriteriaSet(), replace);
        
        setContextValue(context, CONTEXT_KEY_TLS_PROTOCOLS,
                securityParameters.getTLSProtocols(), replace);
        
        setContextValue(context, CONTEXT_KEY_TLS_CIPHER_SUITES, 
                securityParameters.getTLSCipherSuites(), replace);
        
        setContextValue(context, CONTEXT_KEY_HOSTNAME_VERIFIER, 
                securityParameters.getHostnameVerifier(), replace);
        
        setContextValue(context, CONTEXT_KEY_CLIENT_TLS_CREDENTIAL,
                securityParameters.getClientTLSCredential(), replace);
        
    }
    
    /**
     * Set the supplied attribute value in the client context.
     * 
     * @param context the client context instance
     * @param attributeName the context attribute name to 
     * @param attributeValue the context attribute value to set, may be null
     * @param replace whether a non-null argument value should replace an existing context value
     */
    public static void setContextValue(@Nonnull final HttpClientContext context, 
            @Nonnull final String attributeName, @Nullable Object attributeValue, boolean replace) {
        if (attributeValue == null) {
            return;
        }
        Constraint.isNotNull(context, "HttpClientContext was null");
        Constraint.isNotNull(attributeName, "Context attribute name was null");
        
        if (replace || context.getAttribute(attributeName) == null) {
            context.setAttribute(attributeName, attributeValue);
        }
    }

}
