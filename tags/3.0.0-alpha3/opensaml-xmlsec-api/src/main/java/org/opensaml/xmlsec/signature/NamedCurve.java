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

package org.opensaml.xmlsec.signature;

import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.signature.support.SignatureConstants;


/**
 * XMLObject representing XML Digital Signature 1.1 NamedCurve element.
 */
public interface NamedCurve extends XMLObject {
    
    /** Element local name. */
    public static final String DEFAULT_ELEMENT_LOCAL_NAME = "NamedCurve";
    
    /** Default element name. */
    public static final QName DEFAULT_ELEMENT_NAME =
            new QName(SignatureConstants.XMLSIG11_NS, DEFAULT_ELEMENT_LOCAL_NAME, SignatureConstants.XMLSIG11_PREFIX);
    
    /** Local name of the XSI type. */
    public static final String TYPE_LOCAL_NAME = "NamedCurveType"; 
        
    /** QName of the XSI type. */
    public static final QName TYPE_NAME =
            new QName(SignatureConstants.XMLSIG11_NS, TYPE_LOCAL_NAME, SignatureConstants.XMLSIG11_PREFIX);
    
    /** URI attribute name. */
    public static final String URI_ATTRIB_NAME = "URI";
    
    /**
     * Get the URI attribute value.
     * 
     * @return the URI attribute value
     */
    @Nullable public String getURI();
    
    /**
     * Set the URI attribute value.
     * 
     * @param newURI the new URI attribute value
     */
    public void setURI(@Nullable final String newURI);
    
}