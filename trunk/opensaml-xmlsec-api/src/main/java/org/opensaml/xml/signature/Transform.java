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

package org.opensaml.xml.signature;


import java.util.List;

import javax.xml.namespace.QName;

import org.opensaml.xml.XMLObject;
import org.opensaml.xml.util.XMLConstants;
import org.opensaml.xml.validation.ValidatingXMLObject;

/**
 * XMLObject representing XML Digital Signature, version 20020212, Transform element.
 */
public interface Transform extends ValidatingXMLObject {
    
    /** Element local name */
    public final static String DEFAULT_ELEMENT_LOCAL_NAME = "Transform";
    
    /** Default element name */
    public final static QName DEFAULT_ELEMENT_NAME = new QName(XMLConstants.XMLSIG_NS, DEFAULT_ELEMENT_LOCAL_NAME, XMLConstants.XMLSIG_PREFIX);
    
    /** Local name of the XSI type */
    public final static String TYPE_LOCAL_NAME = "TransformType"; 
        
    /** QName of the XSI type */
    public final static QName TYPE_NAME = new QName(XMLConstants.XMLSIG_NS, TYPE_LOCAL_NAME, XMLConstants.XMLSIG_PREFIX);
    
    /** Algorithm attribute name */
    public final static String ALGORITHM_ATTRIB_NAME = "Algorithm";
    
    /**
     * Get the Algorithm URI attribute value
     *  
     * @return the Algorithm URI attribute value
     */
    public String getAlgorithm();
    
    /**
     * 
     * Set the Algorithm URI attribute value
     *  
     * @param newAlgorithm the new Algorithm URI attribute value
     */
    public void setAlgorithm(String newAlgorithm);
    
    /** 
     * Get the list of XMLObject children that match a particular QName
     *
     * @param typeOrName the QName of the statements to return
     * 
     * @return the list of XMLObject children that match the given QName
     */
    public List<XMLObject> getXMLObjects(QName typeOrName);
    
    /** 
     * Get the list of XPath child elements
     * 
     * @return list of XPath child elements
     */
    public List<XPath> getXPaths();
    
    /**
     * Gets the ordered list of all XMLObject child elements of this element.
     * 
     * @return list of XMLObject children
     */
    public List<XMLObject> getAllChildren();
    
}
