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

import org.opensaml.ws.soap.client.SOAPTransport;
import org.opensaml.xml.security.x509.X509Credential;

/**
 * A transport for moving SOAP messages over HTTP.
 * 
 * Note, the authentication credentials provided by this interface are credentials used to authenticate the entity to
 * the HTTP server. The {@link SOAPTransport} connection authentication credentials are used when performing mutual TLS
 * authentication with the server.
 */
public interface HTTPSOAPTransport extends SOAPTransport<X509Credential> {

    /** Authentication schemes supported by HTTP transports. */
    public static enum AuthenticationScheme {
        /** HTTP BASIC authentication. */
        Basic,
        
        /** HTTP Digest authentication. */
        Digest,
        
        /** HTTP NTML authentication. */
        NTLM
    };

    /**
     * Gets whether content should be sent using HTTP 1.1 and chunk encoded. If the implemenation does not support HTTP
     * 1.1 this method will always return false.
     * 
     * @return whether content should be sent using HTTP 1.1 and chunk encoded
     */
    public boolean isChunkEncoding();

    /**
     * Sets whether content should be sent using HTTP 1.1 and chunk encoded. If the implemenation does not support HTTP
     * 1.1 call to this method will be ignored.
     * 
     * @param isChunkEncoding whether content should be sent using HTTP 1.1 and chunk encoded
     */
    public void setChunkEncoding(boolean isChunkEncoding);

    /**
     * Gets the type of authentication to perform with the HTTP server.
     * 
     * @return type of authentication to perform with the HTTP server
     */
    public AuthenticationScheme getEntityAuthenticationScheme();

    /**
     * Sets the type of authentication to perform with the HTTP server.
     * 
     * @param authnScheme type of authentication to perform with the HTTP server
     */
    public void setEntityAuthenticationScheme(AuthenticationScheme authnScheme);

    /**
     * Gets the credentials to use to authenticate to the HTTP server.
     * 
     * @return credentials to use to authenticate to the HTTP server
     */
    public HTTPEntityCredential getEntityAuthenticationCredential();

    /**
     * Sets the credentials to use to authenticate to the HTTP server.
     * 
     * @param credentials credentials to use to authenticate to the HTTP server
     */
    public void setEntityAuthenticationCredentials(HTTPEntityCredential credentials);

    /**
     * Marker interface for HTTP connection credentials.
     */
    public interface HTTPEntityCredential {

    }

    /**
     * User name and password credentals.
     */
    public interface UsernamePasswordCredential extends HTTPEntityCredential {

        /**
         * Gets the user name.
         * 
         * @return user name
         */
        public String getUsername();

        /**
         * Gets the password.
         * 
         * @return password
         */
        public String getPassword();
    }

    /**
     * NTLM credentials.
     */
    public interface NTLMCredential extends UsernamePasswordCredential {

        /**
         * Gets the host name of the computer the request originates.
         * 
         * @return host name of the computer the request originates
         */
        public String getHost();

        /**
         * Gets the domain to authenticate to.
         * 
         * @return domain to authenticate to
         */
        public String getDomain();
    }
}