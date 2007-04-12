/*
 * Copyright [2007] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.ws.soap.client;

import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.credential.CredentialCriteriaSet;
import org.opensaml.xml.security.credential.CredentialResolver;
import org.opensaml.xml.security.trust.TrustEngine;

/**
 * {@link SOAPTransport} base class handling a lot of boilerplate code.
 * 
 * @param <CredentialType> type of credential used during connection authentication
 */
public abstract class AbstractSOAPTransport<CredentialType extends Credential> 
    implements SOAPTransport<CredentialType> {

    /** Credential used to authentication connection to peer. */
    private CredentialType ctxAuthnCredential;
    
    /** Resolver used to lookup trusted credential information. */
    private CredentialResolver trustedCredentialResolver;
    
    /** Criteria describing trusted credentials. */
    private CredentialCriteriaSet trustedCredentialCriteria;

    /** Trust engine used to validate peer's connection authentication credential. */
    private TrustEngine<CredentialType, CredentialType> peerCtxAuthnTrustEngine;
    
    /** Connection timeout in milliseconds. */
    private long requestTimeout;

    /** Constructor. */
    protected AbstractSOAPTransport(){
        trustedCredentialCriteria = new CredentialCriteriaSet();
    }

    /** {@inheritDoc} */
    public CredentialType getConnectionAuthenticationCredential() {
        return ctxAuthnCredential;
    }

    /** {@inheritDoc} */
    public TrustEngine<CredentialType, CredentialType> getPeerConnectionAuthenticatingTrustEngine() {
        return peerCtxAuthnTrustEngine;
    }

    /** {@inheritDoc} */
    public long getRequestTimeout() {
        return requestTimeout;
    }

    /** {@inheritDoc} */
    public void setConnectionAuthenticationCredential(CredentialType credential) {
        ctxAuthnCredential = credential;
    }

    /** {@inheritDoc} */
    public void setPeerConnectionAuthenticatingTrustEngine(TrustEngine<CredentialType, CredentialType> trustEngine) {
        peerCtxAuthnTrustEngine = trustEngine;
    }

    /** {@inheritDoc} */
    public void setRequestTimeout(long timeout) {
        requestTimeout = timeout;
    }
    
    /** {@inheritDoc} */
    public CredentialCriteriaSet getTrustedCredentialCriteria(){
        return trustedCredentialCriteria;
    }
    
    /** {@inheritDoc} */
    public CredentialResolver getTrustedCredentialResolver(){
        return trustedCredentialResolver;
    }

    /** {@inheritDoc} */
    public void setTrustedCredentialResolver(CredentialResolver resolver){
        trustedCredentialResolver = resolver;
    }
}