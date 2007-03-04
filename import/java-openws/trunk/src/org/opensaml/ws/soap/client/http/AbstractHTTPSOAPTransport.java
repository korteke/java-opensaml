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

package org.opensaml.ws.soap.client.http;

import org.opensaml.ws.soap.client.AbstractSOAPTransport;
import org.opensaml.xml.security.x509.X509Credential;

/**
 * {@link HTTPSOAPTransport} base class handling a lot of boilerplate code.
 */
public abstract class AbstractHTTPSOAPTransport extends AbstractSOAPTransport<X509Credential> 
    implements HTTPSOAPTransport {

    /** Entity authentication credential. */
    private HTTPEntityCredential entityCredential;
    
    /** Scheme used to authenticate entity. */
    private AuthenticationScheme authenticationScheme;
    
    /** Whether to chunk encode data. */
    private boolean chunkEncoding;
    
    /** {@inheritDoc} */
    public HTTPEntityCredential getEntityAuthenticationCredential() {
        return entityCredential;
    }

    /** {@inheritDoc} */
    public AuthenticationScheme getEntityAuthenticationScheme() {
        return authenticationScheme;
    }

    /** {@inheritDoc} */
    public boolean isChunkEncoding() {
        return chunkEncoding;
    }

    /** {@inheritDoc} */
    public void setChunkEncoding(boolean isChunkEncoding) {
        chunkEncoding = isChunkEncoding;
    }

    /** {@inheritDoc} */
    public void setEntityAuthenticationCredentials(HTTPEntityCredential credentials) {
        entityCredential = credentials;
    }

    /** {@inheritDoc} */
    public void setEntityAuthenticationScheme(AuthenticationScheme authnScheme) {
        authenticationScheme = authnScheme;
    }
}