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
import java.util.HashMap;
import java.util.Map;

import org.opensaml.ws.soap.common.SOAPMessageContext;
import org.opensaml.ws.transport.Transport;
import org.opensaml.ws.transport.TransportException;
import org.opensaml.xml.security.credential.CredentialResolver;

/**
 * A client for sending and receiving SOAP messages.
 * 
 * When a client sends a message it will create a {@link Transport} instance, based on the endpoint's scheme, marshall
 * and bind the message to the transport, receive, decode, and umarshall the response, evaluate the message security
 * policy, and finally return the response. After this process is complete the response message and transport will be
 * added to the message context.
 */
public class SOAPClient {
    
    /** Registered transport factories. */
    private HashMap<String, ClientTransportFactory> transportFactories;
    
    /** Resolver used to look up local credentials used to connect to the peer. */
    private CredentialResolver credentialResolver;

    /**
     * Constructor.
     * 
     * @param credentials resolver used to look up local credentials used to connect to the peer
     */
    public SOAPClient(CredentialResolver credentials){
        transportFactories = new HashMap<String, ClientTransportFactory>();
        credentialResolver = credentials;
    }
    
    /**
     * Gets the transports registered with this client.
     * 
     * @return mutable list of transports registered with this client
     */
    public Map<String, ClientTransportFactory> getRegisteredTransports(){
        return transportFactories;
    }
    
    /**
     * Sends a SOAP message to the given endpoint.
     * 
     * @param endpointURI endpoint to send the SOAP message to
     * @param messageContext context of the message to send
     * 
     * @throws TransportException thrown if there is a problem creating or using the {@link Transport}
     */
    public void send(URI endpointURI, SOAPMessageContext messageContext) throws TransportException{
        String transportScheme = endpointURI.getScheme();
        ClientTransportFactory transFactory = transportFactories.get(transportScheme);
        
        if(transFactory == null){
            throw new TransportException("No transport registered for URI scheme: " + transportScheme);
        }
        
        ClientTransport transport = transFactory.createTransport();
        
        // lookup and add credential
        // connect
        // marshall and bind message
        // unmarshall response
        // evaluate security policy
        // update message context
        // return
    }
}