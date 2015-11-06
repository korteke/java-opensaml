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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensaml.security.trust.TrustEngine;
import org.opensaml.security.x509.X509Credential;

/**
 * Parameters related to HttpClient request security features.
 */
public class HttpClientSecurityParameters {
    
    /** HttpClient credentials provider. */
    private CredentialsProvider credentialsProvider;
    
    /** Optional trust engine used in evaluating server TLS credentials. */
    private TrustEngine<? super X509Credential> tlsTrustEngine;
    
    /** Optional criteria set used in evaluating server TLS credentials. */
    private CriteriaSet tlsCriteriaSet;
    
    /** TLS Protocols. */
    private List<String> tlsProtocols;
    
    /** TLS cipher suites. */
    private List<String> tlsCipherSuites;
    
    /** The hostname verifier. */
    private X509HostnameVerifier hostnameVerifier;
    
    /** The X509 credential used for client TLS. */
    private X509Credential clientTLSCredential;
    
    /**
     * Get an instance of {@link CredentialsProvider} used for authentication by the HttpClient instance.
     * 
     * @return the credentials provider, or null
     */
    @Nullable public CredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }
    
    /**
     * Set an instance of {@link CredentialsProvider} used for authentication by the HttpClient instance.
     * 
     * @param provider the credentials provider
     */
    public void setCredentialsProvider(@Nullable final CredentialsProvider provider) {
        credentialsProvider = provider;
    }
    
    /**
     * A convenience method to set a (single) username and password used for BASIC authentication.
     * To disable BASIC authentication pass null for the credentials instance.
     * 
     * <p>
     * An {@link AuthScope} will be generated which specifies any host, port, scheme and realm.
     * </p>
     * 
     * <p>To specify multiple usernames and passwords for multiple host, port, scheme, and realm combinations, instead 
     * provide an instance of {@link CredentialsProvider} via {@link #setCredentialsProvider(CredentialsProvider)}.</p>
     * 
     * @param credentials the username and password credentials
     */
    public void setBasicCredentials(@Nullable final UsernamePasswordCredentials credentials) {
        setBasicCredentialsWithScope(credentials, null);
    }
    
    /**
     * A convenience method to set a (single) username and password used for BASIC authentication.
     * To disable BASIC authentication pass null for the credentials instance.
     * 
     * <p>
     * If the <code>authScope</code> is null, an {@link AuthScope} will be generated which specifies
     * any host, port, scheme and realm.
     * </p>
     * 
     * <p>To specify multiple usernames and passwords for multiple host, port, scheme, and realm combinations, instead 
     * provide an instance of {@link CredentialsProvider} via {@link #setCredentialsProvider(CredentialsProvider)}.</p>
     * 
     * @param credentials the username and password credentials
     * @param scope the HTTP client auth scope with which to scope the credentials, may be null
     */
    public void setBasicCredentialsWithScope(@Nullable final UsernamePasswordCredentials credentials,
            @Nullable final AuthScope scope) {

        if (credentials != null) {
            AuthScope authScope = scope;
            if (authScope == null) {
                authScope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT);
            }
            BasicCredentialsProvider provider = new BasicCredentialsProvider();
            provider.setCredentials(authScope, credentials);
            credentialsProvider = provider;
        } else {
            credentialsProvider = null;
        }

    }
    
    /**
     * Sets the optional trust engine used in evaluating server TLS credentials.
     * 
     * @return the trust engine instance to use, or null
     */
    @Nullable public TrustEngine<? super X509Credential> getTLSTrustEngine() {
        return tlsTrustEngine;
    }
    
    /**
     * Sets the optional trust engine used in evaluating server TLS credentials.
     * 
     * @param engine the trust engine instance to use
     */
    public void setTLSTrustEngine(@Nullable final TrustEngine<? super X509Credential> engine) {
        tlsTrustEngine = engine;
    }

    /**
     * Get the optional criteria set used in evaluating server TLS credentials.
     * 
     * @return the criteria set instance to use
     */
    @Nullable public CriteriaSet getTLSCriteriaSet() {
        return tlsCriteriaSet;
    }

    /**
     * Set the optional criteria set used in evaluating server TLS credentials.
     * 
     * @param criteriaSet the new criteria set instance to use
     */
    public void setTLSCriteriaSet(@Nullable final CriteriaSet criteriaSet) {
        tlsCriteriaSet = criteriaSet;
    }

    /**
     * Get the optional list of TLS protocols. 
     * 
     * @return the TLS protocols, or null
     */
    @Nullable public List<String> getTLSProtocols() {
        return tlsProtocols;
    }

    /**
     * Set the optional list of TLS protocols. 
     * 
     * @param protocols the TLS protocols or null
     */
    public void setTLSProtocols(@Nullable final List<String> protocols) {
        tlsProtocols = new ArrayList<>(StringSupport.normalizeStringCollection(protocols));
        if (tlsProtocols.isEmpty()) {
            tlsProtocols = null;
        }
    }

    /**
     * Get the optional list of TLS cipher suites.
     * 
     * @return the list of TLS cipher suites, or null
     */
    @Nullable public List<String> getTLSCipherSuites() {
        return tlsCipherSuites;
    }

    /**
     * Set the optional list of TLS cipher suites.
     * 
     * @param cipherSuites the TLS cipher suites, or null
     */
    public void setTLSCipherSuites(@Nullable final List<String> cipherSuites) {
        tlsCipherSuites = new ArrayList<>(StringSupport.normalizeStringCollection(cipherSuites));
        if (tlsCipherSuites.isEmpty()) {
            tlsCipherSuites = null;
        }
    }

    /**
     * Get the optional hostname verifier.
     * 
     * @return the hostname verifier, or null
     */
    @Nullable public X509HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    /**
     * Set the optional hostname verifier.
     * 
     * @param verifier the hostname verifier, or null
     */
    public void setHostnameVerifier(@Nullable final X509HostnameVerifier verifier) {
        hostnameVerifier = verifier;
    }

    /**
     * Get the optional client TLS credential.
     * 
     * @return the client TLS credential, or null
     */
    @Nullable public X509Credential getClientTLSCredential() {
        return clientTLSCredential;
    }

    /**
     * Set the optional client TLS credential.
     * 
     * @param credential the client TLS credential, or null
     */
    public void setClientTLSCredential(@Nullable final X509Credential credential) {
        clientTLSCredential = credential;
    }

}
