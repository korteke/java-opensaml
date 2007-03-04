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

import java.io.InputStream;

import org.opensaml.ws.MessageSource;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.trust.TrustEngine;

/**
 * A transport used to send and receive SOAP messages.
 * 
 * @param <CredentialType> type of credential used during connection authentication
 */
public interface SOAPTransport<CredentialType extends Credential> {

    /**
     * Gets whether this transport provides confidentiality.
     * 
     * @return whether this transport provides confidentiality
     */
    public boolean supportsConfidential();
    
    /**
     * Gets the time to wait for a response in seconds.
     * 
     * @return time to wait for a response in seconds
     */
    public long getRequestTimeout();
    
    /**
     * Sets the time to wait for a response in seconds.
     * 
     * @param timeout time to wait for a response in seconds
     */
    public void setRequestTimeout(long timeout);
    
    /**
     * Gets the credential used to authenticate to the peer when connecting.
     * 
     * @return credential used to authenticate to the peer
     */
    public CredentialType getConnectionAuthenticationCredential();
    
    /**
     * Sets the credential used to authenticate to the peer when connecting.
     * 
     * @param credential credential used to authenticate to the peer
     */
    public void setConnectionAuthenticationCredential(CredentialType credential);
 
    /**
     * Gets the trust engine to use to evaluate a peer's connection credentials.
     * 
     * @return trust engine to use to evaluate a peer's connection credentials
     */
    public TrustEngine<CredentialType, CredentialType> getPeerConnectionAuthenticatingTrustEngine();
    
    /**
     * Sets the trust engine to use to evaluate a peer's connection credentials.
     * 
     * @param trustEngine trust engine to use to evaluate a peer's connection credentials
     */
    public void setPeerConnectionAuthenticatingTrustEngine(TrustEngine<CredentialType, CredentialType> trustEngine);
    
    /**
     * Sends the SOAP message over this transport.
     * 
     * @param endpointURI peer endpoint to send the message to
     * @param message the message to send
     * 
     * @return response of the message sent
     */
    public MessageSource send(String endpointURI, InputStream message);
}