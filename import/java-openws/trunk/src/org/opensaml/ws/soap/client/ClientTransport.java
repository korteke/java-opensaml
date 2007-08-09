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

import java.net.URI;

import org.opensaml.ws.transport.InTransport;
import org.opensaml.ws.transport.OutTransport;
import org.opensaml.ws.transport.TransportException;

/**
 * Transport used by the {@link SOAPClient} to connect to a peer and send data.
 */
public interface ClientTransport {
    
    /**
     * Opens a connection to the peer. Any connection parameter changed after the connection is established will be
     * ignored.
     * 
     * @param endpoint peer endpoint to connect to
     * 
     * @throws TransportException thrown if there is a problem connecting to the peer
     */
    public void connect(URI endpoint) throws TransportException;

    /**
     * Disconnects from the peer, closing the connection but not destroying any currently held resources (such as
     * buffered data).
     * 
     * @throws TransportException thrown if there is a problem connecting to the peer
     */
    public void disconnect() throws TransportException;

    /**
     * Gets the transport used to send outbound data.
     * 
     * @return transport used to send outbound data
     */
    public OutTransport getOutboudTransport();

    /**
     * Gets the transport used to receive incomming data.
     * 
     * @return transport used to receive incomming data
     */
    public InTransport getInTransport();
}