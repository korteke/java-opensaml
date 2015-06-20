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

package org.opensaml.soap.messaging.context;

import javax.annotation.Nullable;

import org.opensaml.messaging.context.BaseContext;
import org.opensaml.soap.soap11.Envelope;
import org.opensaml.soap.soap11.Fault;

/**
 * Subcontext that carries information about the SOAP 1.1 message transport.
 */
public class SOAP11Context extends BaseContext {
    
    /** The SAML protocol in use. */
    private Envelope envelope;
    
    /** SOAP 1.1 Fault related to the current message processing context. */
    private Fault fault;
    
    /** The HTTP response status code to return. */
    private Integer httpResponseStatus;

    /**
     * Gets the current SOAP 1.1 Envelope.
     * 
     * @return current SOAP 1.1 Envelope, may be null
     */
    @Nullable public Envelope getEnvelope() {
        return envelope;
    }

    /**
     * Sets the current SOAP 1.1 Envelope.
     * 
     * @param newEnvelope the current SOAP 1.1 Envelope
     */
    public void setEnvelope(@Nullable final Envelope newEnvelope) {
        envelope = newEnvelope;
    }
    
    /**
     * Get the current SOAP 1.1 Fault related to the current message processing context.
     * 
     * @return the current SOAP 1.1 Fault, may be null
     */
    @Nullable public Fault getFault() {
        return fault;
    }

    /**
     * Set the current SOAP 1.1 Fault related to the current message processing context.
     * 
     * @param newFault the new Fault
     */
    public void setFault(@Nullable final Fault newFault) {
        fault = newFault;
    }

    /**
     * Get the optional HTTP response status code to return.
     * 
     * @return HTTP response status code, may be null
     */
    @Nullable public Integer getHTTPResponseStatus() {
        return httpResponseStatus;
    }

    /**
     * Set the optional HTTP response status code to return.
     * 
     * @param status the HTTP response status code, may be null
     */
    public void setHTTPResponseStatus(@Nullable final Integer status) {
        httpResponseStatus = status;
    }
    
}