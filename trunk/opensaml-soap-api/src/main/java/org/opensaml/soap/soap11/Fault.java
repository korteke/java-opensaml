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

package org.opensaml.soap.soap11;

import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.soap.common.SOAPObject;
import org.opensaml.soap.util.SOAPConstants;

/**
 * SOAP 1.1 Fault.
 */
public interface Fault extends SOAPObject {
    
    /** Element local name. */
    public static final String DEFAULT_ELEMENT_LOCAL_NAME = "Fault";
    
    /** Default element name. */
    public static final QName DEFAULT_ELEMENT_NAME = 
        new QName(SOAPConstants.SOAP11_NS, DEFAULT_ELEMENT_LOCAL_NAME, SOAPConstants.SOAP11_PREFIX);
    
    /** Local name of the XSI type. */
    public static final String TYPE_LOCAL_NAME = "Fault"; 
        
    /** QName of the XSI type. */
    public static final QName TYPE_NAME = 
        new QName(SOAPConstants.SOAP11_NS, TYPE_LOCAL_NAME, SOAPConstants.SOAP11_PREFIX);

    /**
     * Gets the fault code for this fault.
     * 
     * @return the fault code for this fault
     */
    @Nullable public FaultCode getCode();
    
    /**
     * Sets the fault code for this fault.
     * 
     * @param newFaultCode the fault code for this fault
     */
    public void setCode(@Nullable final FaultCode newFaultCode);
    
    /**
     * Gets the fault string for this fault.
     * 
     * @return the fault string for this fault
     */
    @Nullable public FaultString getMessage();
    
    /**
     * Sets the fault string for this fault.
     * 
     * @param newMessage the fault string for this fault
     */
    public void setMessage(@Nullable final FaultString newMessage);
    
    /**
     * Gets the URI of the fault actor for this fault.
     * 
     * @return the URI of the fault actor for this fault
     */
    @Nullable public FaultActor getActor();
    
    /**
     * Sets the URI of the fault actor for this fault.
     * 
     * @param newActor the URI of the fault actor for this fault
     */
    public void setActor(@Nullable final FaultActor newActor);
    
    /**
     * Gets details of this fault.
     * 
     * @return details of this fault
     */
    @Nullable public Detail getDetail();
    
    /**
     * Sets details of this fault.
     * 
     * @param newDetail details of this fault
     */
    public void setDetail(@Nullable final Detail newDetail);
}
