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
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.Organization;
import org.opensaml.saml.saml2.metadata.OrganizationDisplayName;
import org.opensaml.saml.saml2.metadata.OrganizationName;
import org.opensaml.saml.saml2.metadata.OrganizationURL;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.saml.saml2.metadata.OrganizationName}.
 */
public class OrganizationTest extends XMLObjectProviderBaseTestCase {

    /** Unknown Attributes */
    protected QName[] unknownAttributeNames = {new QName("urn:foo:bar", "bar", "foo"), new QName("flibble")};

    /** Unknown Attribute Values */
    protected String[] unknownAttributeValues = {"fred", "flobble"};

    /**
     * Constructor
     */
    public OrganizationTest() {
        singleElementFile = "/org/opensaml/saml/saml2/metadata/impl/Organization.xml";
        childElementsFile = "/org/opensaml/saml/saml2/metadata/impl/OrganizationChildElements.xml";
        singleElementUnknownAttributesFile =
                "/org/opensaml/saml/saml2/metadata/impl/OrganizationUnknownAttributes.xml";
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnmarshall() {
        Organization org = (Organization) unmarshallElement(singleElementFile);
        Assert.assertEquals(org.getDisplayNames().size(), 0, "Display names");
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnknownAttributesUnmarshall() {
        Organization org = (Organization) unmarshallElement(singleElementUnknownAttributesFile);
        AttributeMap attributes = org.getUnknownAttributes();

        Assert.assertEquals(attributes.entrySet().size(), unknownAttributeNames.length);
        for (int i = 0; i < unknownAttributeNames.length; i++) {
            Assert.assertEquals(attributes.get(unknownAttributeNames[i]), unknownAttributeValues[i]);
        }
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsUnmarshall() {
        Organization org = (Organization) unmarshallElement(childElementsFile);

        Assert.assertNotNull(org.getExtensions(), "Extensions");
        Assert.assertEquals(org.getOrganizationNames().size(), 3, "OrganizationName count");
        Assert.assertEquals(org.getDisplayNames().size(), 2, "DisplayNames count");
        Assert.assertEquals(org.getURLs().size(), 1, "URL count");
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementMarshall() {
        Organization org = (Organization) buildXMLObject(Organization.DEFAULT_ELEMENT_NAME);

        assertXMLEquals(expectedDOM, org);
    }

    @Test public void testSingleElementUnknownAttributesMarshall() {
        Organization org = (new OrganizationBuilder()).buildObject();

        for (int i = 0; i < unknownAttributeNames.length; i++) {
            org.getUnknownAttributes().put(unknownAttributeNames[i], unknownAttributeValues[i]);
        }
        assertXMLEquals(expectedUnknownAttributesDOM, org);
    }

    /**
     * {@inheritDoc}
     */
    @Test public void testChildElementsMarshall() {
        Organization org = (Organization) buildXMLObject(Organization.DEFAULT_ELEMENT_NAME);

        QName extensionsQName =
                new QName(SAMLConstants.SAML20MD_NS, Extensions.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        org.setExtensions((Extensions) buildXMLObject(extensionsQName));

        for (int i = 0; i < 3; i++) {
            org.getOrganizationNames().add((OrganizationName) buildXMLObject(OrganizationName.DEFAULT_ELEMENT_NAME));
        }

        for (int i = 0; i < 2; i++) {
            org.getDisplayNames().add(
                    (OrganizationDisplayName) buildXMLObject(OrganizationDisplayName.DEFAULT_ELEMENT_NAME));
        }

        org.getURLs().add((OrganizationURL) buildXMLObject(OrganizationURL.DEFAULT_ELEMENT_NAME));

        assertXMLEquals(expectedChildElementsDOM, org);
    }
}