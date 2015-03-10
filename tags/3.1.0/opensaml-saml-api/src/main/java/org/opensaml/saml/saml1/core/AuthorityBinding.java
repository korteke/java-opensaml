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

/**
 * 
 */

package org.opensaml.saml.saml1.core;

import javax.xml.namespace.QName;

import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

/**
 * Interface to define how a <code>AuthorityBinding</code> element behaves.
 */
public interface AuthorityBinding extends SAMLObject {

    /** Element name, no namespace. */
    public static final String DEFAULT_ELEMENT_LOCAL_NAME = "AuthorityBinding";
    
    /** Default element name. */
    public static final QName DEFAULT_ELEMENT_NAME =
            new QName(SAMLConstants.SAML1_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
    
    /** Local name of the XSI type. */
    public static final String TYPE_LOCAL_NAME = "AuthorityBindingType"; 
        
    /** QName of the XSI type. */
    public static final QName TYPE_NAME =
            new QName(SAMLConstants.SAML1_NS, TYPE_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
    
    /** Name for the AuthorityKind attribute.  */
    public static final String AUTHORITYKIND_ATTRIB_NAME = "AuthorityKind";
    
    /** Name for the Location attribute.  */
    public static final String LOCATION_ATTRIB_NAME = "Location";

    /** Name for the Binding attribute.  */
    public static final String BINDING_ATTRIB_NAME = "Binding";

    /**
     * Get the type of authority described.
     * 
     * @return the type of authority
     */
    public QName getAuthorityKind();

    /**
     * Set the type of authority described.
     * 
     * @param authorityKind the type of authority
     */
    public void setAuthorityKind(QName authorityKind);
    
    /**
     * Get the authority location.
     * 
     * @return the location
     */
    public String getLocation();

    /**
     * Set the authority location.
     * 
     * @param location the location
     */
    public void setLocation(String location);
    
    /**
     * Get the authority binding.
     * 
     * @return the binding
     */
    public String getBinding();

    /**
     * Set the authority binding.
     * 
     * @param binding the binding
     */
    public void setBinding(String binding);

}