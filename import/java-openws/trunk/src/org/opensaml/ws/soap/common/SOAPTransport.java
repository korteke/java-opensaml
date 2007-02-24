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

package org.opensaml.ws.soap.common;

import java.io.InputStream;

import org.opensaml.ws.MessageSource;

/**
 * A transport used to send and receive SOAP messages.
 */
public interface SOAPTransport {

    /**
     * Gets whether this transport provides confidentiality.
     * 
     * @return whether this transport provides confidentiality
     */
    public boolean isConfidential();
    
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
     * Gets whether the communication peer was authenticated.
     * 
     * @return whether the communication peer was authenticated
     */
    public boolean isPeerAuthenticated();
 
    /**
     * Sends the SOAP message over this transport.
     * 
     * @param message the message to send
     * 
     * @return response of the message sent
     */
    public MessageSource send(InputStream message);
}