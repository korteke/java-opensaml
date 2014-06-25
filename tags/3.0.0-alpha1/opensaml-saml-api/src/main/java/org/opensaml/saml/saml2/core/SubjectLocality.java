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

package org.opensaml.saml.saml2.core;

import javax.xml.namespace.QName;

import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

/**
 * SAML 2.0 Core SubjectLocality.
 */
public interface SubjectLocality extends SAMLObject {

    /** Element local name. */
    public static final String DEFAULT_ELEMENT_LOCAL_NAME = "SubjectLocality";

    /** Default element name. */
    public static final QName DEFAULT_ELEMENT_NAME = new QName(SAMLConstants.SAML20_NS, DEFAULT_ELEMENT_LOCAL_NAME,
            SAMLConstants.SAML20_PREFIX);

    /** Local name of the XSI type. */
    public static final String TYPE_LOCAL_NAME = "SubjectLocalityType";

    /** QName of the XSI type. */
    public static final QName TYPE_NAME = new QName(SAMLConstants.SAML20_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20_PREFIX);

    /** Address attribute name. */
    public static final String ADDRESS_ATTRIB_NAME = "Address";

    /** DNSName attribute name. */
    public static final String DNS_NAME_ATTRIB_NAME = "DNSName";

    /**
     * Gets the IP address of the system from which the subject was authenticated.
     * 
     * @return the IP address of the system from which the subject was authenticated
     */
    public String getAddress();

    /**
     * Sets the IP address of the system from which the subject was authenticated.
     * 
     * @param newAddress the IP address of the system from which the subject was authenticated
     */
    public void setAddress(String newAddress);

    /**
     * Gets the DNSName of the system from which the subject was authenticated.
     * 
     * @return the DNSName of the system from which the subject was authenticated
     */
    public String getDNSName();

    /**
     * Sets the DNSName of the system from which the subject was authenticated.
     * 
     * @param newDNSName the DNSName of the system from which the subject was authenticated
     */
    public void setDNSName(String newDNSName);
}