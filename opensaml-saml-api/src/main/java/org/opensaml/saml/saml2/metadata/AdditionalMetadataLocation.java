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

package org.opensaml.saml.saml2.metadata;

import javax.xml.namespace.QName;

import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

/**
 * SAML 2.0 Metadata AdditionalMetadataLocation.
 */
public interface AdditionalMetadataLocation extends SAMLObject {

    /** Element name, no namespace. */
    public static final String DEFAULT_ELEMENT_LOCAL_NAME = "AdditionalMetadataLocation";

    /** Default element name. */
    public static final QName DEFAULT_ELEMENT_NAME = new QName(SAMLConstants.SAML20MD_NS, DEFAULT_ELEMENT_LOCAL_NAME,
            SAMLConstants.SAML20MD_PREFIX);

    /** Local name of the XSI type. */
    public static final String TYPE_LOCAL_NAME = "AdditionalMetadataLocationType";

    /** QName of the XSI type. */
    public static final QName TYPE_NAME = new QName(SAMLConstants.SAML20MD_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20MD_PREFIX);

    /** "affiliationOwnerID" attribute's local name. */
    public static final String NAMESPACE_ATTRIB_NAME = "namespace";

    /**
     * Gets the location URI.
     * 
     * @return the location URI
     */
    public String getLocationURI();

    /**
     * Sets the location URI.
     * 
     * @param locationURI the location URI
     */
    public void setLocationURI(String locationURI);

    /**
     * Gets the namespace URI.
     * 
     * @return the namespace URI
     */
    public String getNamespaceURI();

    /**
     * Sets the namespace URI.
     * 
     * @param namespaceURI the namespace URI
     */
    public void setNamespaceURI(String namespaceURI);
}
