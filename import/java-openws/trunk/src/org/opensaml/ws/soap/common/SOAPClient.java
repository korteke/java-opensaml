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

import javax.xml.validation.Schema;

import org.opensaml.ws.security.SecurityPolicy;

/**
 * A client for sending and receiving SOAP messages.
 * 
 * Clients are not re-usable or thread safe.
 */
public class SOAPClient {
    
    /** Transport used to send and receive messages. */
    private SOAPTransport soapTransport;
    
    /** Schema used to validate messages if validation is enabled. */
    private Schema validationSchema;
    
    /** Security policy to use to evaluate. */
    private SecurityPolicy securityPolicy;
    
    /**
     * Constructor.
     *
     * @param transport transport used to send and receive messages
     */
    public SOAPClient(SOAPTransport transport){
        soapTransport = transport;
    }
    
    /**
     * Gets the schema used to validate incoming messages.
     * 
     * @return schema used to validate incoming messages
     */
    public Schema getValidationSchema(){
        return validationSchema;
    }
    
    /**
     * Sets the schema used to validate incoming messages.
     * 
     * @param schema schema used to validate incoming messages
     */
    public void setValidationSchema(Schema schema){
        validationSchema = schema;
    }
    
    /**
     * Gets the security policy received messages will be evaluated against.
     * 
     * @return security policy received messages will be evaluated against
     */
    public SecurityPolicy getSecurityPolicy(){
        return securityPolicy;
    }
    
    /**
     * Sets the security policy received messages will be evaluated against.
     * 
     * @param policy security policy received messages will be evaluated against
     */
    public void setSecurityPolicy(SecurityPolicy policy){
        securityPolicy = policy;
    }
    
    /**
     * Sends a SOAP message to the given endpoint.
     * 
     * @param endpointURI endpoint to send the SOAP message to
     * @param soapMessage SOAP message to send
     */
    public void send(String endpointURI, SOAPObject soapMessage){
        // Add trust stuff
    }
    
    /**
     * Receives and incoming SOAP message.
     * 
     * @return the SOAP message received
     */
    public SOAPObject receive(){
        return null;
    }
}