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

package org.opensaml.saml.saml2.metadata.impl;

import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.common.Extensions;
import org.opensaml.saml.saml2.metadata.Organization;
import org.opensaml.saml.saml2.metadata.OrganizationDisplayName;
import org.opensaml.saml.saml2.metadata.OrganizationName;
import org.opensaml.saml.saml2.metadata.OrganizationURL;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.saml.saml2.metadata.OrganizationName}.
 */
public class OrganizationTest extends XMLObjectProviderBaseTestCase {

    /**
     * Constructor
     */
    public OrganizationTest() {
        singleElementFile = "/data/org/opensaml/saml/saml2/metadata/impl/Organization.xml";
        childElementsFile = "/data/org/opensaml/saml/saml2/metadata/impl/OrganizationChildElements.xml";
    }

    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /** {@inheritDoc} */
    public void testSingleElementUnmarshall() {
        Organization org = (Organization) unmarshallElement(singleElementFile);
        assertEquals("Display names", 0, org.getDisplayNames().size());
    }

    /** {@inheritDoc} */
    public void testChildElementsUnmarshall() {
        Organization org = (Organization) unmarshallElement(childElementsFile);

        assertNotNull("Extensions", org.getExtensions());
        assertEquals("OrganizationName count", 3, org.getOrganizationNames().size());
        assertEquals("DisplayNames count", 2, org.getDisplayNames().size());
        assertEquals("URL count", 1, org.getURLs().size());
    }

    /** {@inheritDoc} */
    public void testSingleElementMarshall() {
        Organization org = (Organization) buildXMLObject(Organization.DEFAULT_ELEMENT_NAME);

        assertXMLEquals(expectedDOM, org);
    }

    /**
     * 
     */
    public void testChildElementsMarshall() {
        Organization org = (Organization) buildXMLObject(Organization.DEFAULT_ELEMENT_NAME);

        QName extensionsQName = new QName(SAMLConstants.SAML20MD_NS, Extensions.LOCAL_NAME,
                SAMLConstants.SAML20MD_PREFIX);
        org.setExtensions((Extensions) buildXMLObject(extensionsQName));

        for (int i = 0; i < 3; i++) {
            org.getOrganizationNames().add((OrganizationName) buildXMLObject(OrganizationName.DEFAULT_ELEMENT_NAME));
        }

        for (int i = 0; i < 2; i++) {
            org.getDisplayNames().add((OrganizationDisplayName) buildXMLObject(OrganizationDisplayName.DEFAULT_ELEMENT_NAME));
        }

        org.getURLs().add((OrganizationURL) buildXMLObject(OrganizationURL.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, org);
    }
}