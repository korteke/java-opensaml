/*
 * Copyright [2005] [University Corporation for Advanced Internet Development, Inc.]
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

package org.opensaml.saml2.core;

import javax.xml.namespace.QName;

import org.opensaml.common.SAMLObject;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.xml.schema.XSBooleanValue;

/**
 * SAML 2.0 Core NameIDPolicy.
 */
public interface NameIDPolicy extends SAMLObject {
    
    /** Element local name */
    public final static String DEFAULT_ELEMENT_LOCAL_NAME = "NameIDPolicy";
    
    /** Default element name */
    public final static QName DEFAULT_ELEMENT_NAME = new QName(SAMLConstants.SAML20P_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
    
    /** Local name of the XSI type */
    public final static String TYPE_LOCAL_NAME = "NameIDPolicyType"; 
        
    /** QName of the XSI type */
    public final static QName TYPE_NAME = new QName(SAMLConstants.SAML20P_NS, TYPE_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
    
    /** Format attribute name */
    public final static String FORMAT_ATTRIB_NAME = "Format";

    /** SPNameQualifier attribute name */
    public final static String SP_NAME_QUALIFIER_ATTRIB_NAME = "SPNameQualifier";
    
    /** AllowCreate attribute name */
    public final static String ALLOW_CREATE_ATTRIB_NAME = "AllowCreate";
    
    /**
     * Gets the format of the NameIDPolicy.
     * 
     * @return the format of the NameIDPolicy
     */
    public String getFormat();

    /**
     * Sets the format of the NameIDPolicy
     * 
     * @param newFormat the format of the NameIDPolicy
     */
    public void setFormat(String newFormat);

    /**
     * Gets the SPNameQualifier value.
     * 
     * @return the SPNameQualifier value
     */
    public String getSPNameQualifier();

    /**
     * Sets the SPNameQualifier value.
     * 
     * @param newSPNameQualifier the SPNameQualifier value
     */
    public void setSPNameQualifier(String newSPNameQualifier);

    /**
     * Gets the AllowCreate value.
     * 
     * @return the AllowCreate value
     */
    public XSBooleanValue getAllowCreate();

    /**
     * Sets the AllowCreate value.
     * 
     * @param newAllowCreate the AllowCreate value
     */
    public void setAllowCreate(XSBooleanValue newAllowCreate);

}