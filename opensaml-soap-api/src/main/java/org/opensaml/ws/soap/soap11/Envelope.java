/*
 * Copyright [2006] [University Corporation for Advanced Internet Development, Inc.]
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

package org.opensaml.ws.soap.soap11;

import javax.xml.namespace.QName;

import org.opensaml.ws.soap.common.SOAPObject;
import org.opensaml.ws.soap.util.SOAPConstants;
import org.opensaml.xml.AttributeExtensibleXMLObject;
import org.opensaml.xml.ElementExtensibleXMLObject;

/**
 * SOAP 1.1 Envelope.
 */
public interface Envelope extends SOAPObject, ElementExtensibleXMLObject, AttributeExtensibleXMLObject {

    /** Element local name. */
    public static final String DEFAULT_ELEMENT_LOCAL_NAME = "Envelope";
    
    /** Default element name. */
    public static final QName DEFAULT_ELEMENT_NAME = 
        new QName(SOAPConstants.SOAP11_NS, DEFAULT_ELEMENT_LOCAL_NAME, SOAPConstants.SOAP11_PREFIX);
    
    /** Local name of the XSI type. */
    public static final String TYPE_LOCAL_NAME = "Envelope"; 
        
    /** QName of the XSI type. */
    public static final QName TYPE_NAME = 
        new QName(SOAPConstants.SOAP11_NS, TYPE_LOCAL_NAME, SOAPConstants.SOAP11_PREFIX);
    
    /**
     * Gets the header of this envelope.
     * 
     * @return the header of this envelope
     */
    public Header getHeader();
    
    /**
     * Sets the header of this envelope.
     * 
     * @param newHeader the header of this envelope
     */
    public void setHeader(Header newHeader);
    
    /**
     * Gets the body of this envelope.
     * 
     * @return the body of this envelope
     */
    public Body getBody();
    
    /**
     * Sets the body of this envelope.
     * 
     * @param newBody the body of this envelope
     */
    public void setBody(Body newBody);
}
