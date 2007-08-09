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

package org.opensaml.ws.transport;

import org.opensaml.xml.security.credential.Credential;

/**
 * Base interface for inbound and outboud transports.
 */
public interface Transport {

    /**
     * Gets the local credential used to authenticate to the peer.
     * 
     * @return local credential used to authenticate to the peer
     */
    public Credential getLocalCredential();

    /**
     * Gets the credential offered by the peer to authenticate itself.
     * 
     * @return credential offered by the peer to authenticate itself
     */
    public Credential getPeerCredential();

    /**
     * Gets the endpoint the transport is connected to.
     * 
     * @return endpoint the transport is connected to
     */
    public String getPeerEndpoint();

    /**
     * Gets whether the peer is authenticated.
     * 
     * @return whether the peer is authenticated
     */
    public boolean isAuthenticated();

    /**
     * Gets whether the transport represents a confidential connection (e.g. an SSL connection).
     * 
     * @return whether the transport represents a confidential connection
     */
    public boolean isConfidential();
}