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

package org.opensaml.soap.common;

import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.soap.soap11.Fault;

/**
 * Specialized message decoding exception type for carrying a SOAP 1.1 Fault element.
 */
public class SOAP11FaultDecodingException extends MessageDecodingException {

    /** Serial version UID. */
    private static final long serialVersionUID = 7013840493662326895L;
    
    /** The SOAP 1.1. Fault element being carried. */
    private final Fault fault;
    
    /**
     * Constructor.
     *
     * @param soapFault the SOAP 1.1 fault being represented
     */
    public SOAP11FaultDecodingException(Fault soapFault) {
        super();
        fault = soapFault; 
    }
    
    /**
     * Constructor.
     *
     * @param soapFault the SOAP 1.1 fault being represented
     * @param message a textual exception message
     */
    public SOAP11FaultDecodingException(Fault soapFault, String message) {
        super(message);
        fault = soapFault; 
    }
    
    /**
     * Get the SOAP 1.1 Fault represented by the exception.
     * 
     * @return the SOAP 1.1 Fault element
     */
    public Fault getFault() {
        return fault;
    }

}
