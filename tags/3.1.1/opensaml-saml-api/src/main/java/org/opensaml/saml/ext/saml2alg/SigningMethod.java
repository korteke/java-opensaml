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

package org.opensaml.saml.ext.saml2alg;

import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.ElementExtensibleXMLObject;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

/** SAML v2.0 Metadata Profile for Algorithm Support Version 1.0 SigningMethod SAMLObject. */
public interface SigningMethod extends SAMLObject, ElementExtensibleXMLObject {

    /** Element local name. */
    public static final String DEFAULT_ELEMENT_LOCAL_NAME = "SigningMethod";

    /** Default element name. */
    public static final QName DEFAULT_ELEMENT_NAME =
        new QName(SAMLConstants.SAML20ALG_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20ALG_PREFIX);
    
    /** Local name of the XSI type. */
    public static final String TYPE_LOCAL_NAME = "SigningMethodType";

    /** QName of the XSI type. */
    public static final QName TYPE_NAME =
        new QName(SAMLConstants.SAML20ALG_NS, TYPE_LOCAL_NAME, SAMLConstants.SAML20ALG_PREFIX);
    
    /** Algorithm attribute name. */
    public static final String ALGORITHM_ATTRIB_NAME = "Algorithm";
    
    /** MinKeySize attribute name. */
    public static final String MIN_KEY_SIZE_ATTRIB_NAME = "MinKeySize";
    
    /** MaxKeySize attribute name. */
    public static final String MAX_KEY_SIZE_ATTRIB_NAME = "MaxKeySize";
    
    /**
     * Get the value of the Algorithm URI attribute.
     * 
     * @return the algorithm URI
     */
    @Nullable public String getAlgorithm();
    
    /**
     * Get the value of the Algorithm URI attribute.
     * 
     * @param value the algorithm URI
     */
    public void setAlgorithm(@Nullable final String value);
    
    /**
     * Get the value of the MinKeySize attribute.
     * 
     * @return the MinKeySize value
     */
    @Nullable public Integer getMinKeySize();
    
    /**
     * Set the value of the MinKeySize attribute.
     * 
     * @param value the MinKeySize value
     */
    public void setMinKeySize(@Nullable Integer value);
    
    /**
     * Get the value of the MaxKeySize attribute.
     * 
     * @return the MaxKeySize value
     */
    @Nullable public Integer getMaxKeySize();
    
    /**
     * Set the value of the MaxKeySize attribute.
     * 
     * @param value the MaxKeySize value
     */
    public void setMaxKeySize(@Nullable Integer value);
    
}