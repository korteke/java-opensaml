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

package org.opensaml.saml.saml2.metadata.impl;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.AssertionIDRequestService;
import org.opensaml.saml.saml2.metadata.AuthnAuthorityDescriptor;
import org.opensaml.saml.saml2.metadata.AuthnQueryService;
import org.opensaml.saml.saml2.metadata.ContactPerson;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.NameIDFormat;
import org.opensaml.saml.saml2.metadata.Organization;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.impl.AuthnAuthorityDescriptorImpl}.
 */
public class AuthnAuthorityDescriptorTest extends XMLObjectProviderBaseTestCase {

    /** Expected supported protocol enumeration */
    protected List<String> expectedSupportedProtocols;

    /** Expected cacheDuration value in miliseconds */
    protected long expectedCacheDuration;

    /** Expected validUntil value */
    protected DateTime expectedValidUntil;

    /** Expected errorURL value */
    protected String expectedErrorURL;

    /** Expected number of <code> KeyDescriptor </code> sub elements */
    protected int expectedKeyDescriptors;

    /** Expected number of <code> ContactPerson </code> sub elements */
    protected int expectedContactPersons;

    /** Expected number of <code> AuthnQueryService </code> sub elements */
    protected int expectedAuthnQueryServices;

    /** Expected number of <code> AssertionIdRequestService </code> sub elements */
    protected int expectedAssertionIdRequestServices;

    /** Expected number of <code> NameIdFormat </code> sub elements */
    protected int expectedNameIdFormats;

    /**
     * Constructor
     */
    public AuthnAuthorityDescriptorTest() {
        singleElementFile = "/org/opensaml/saml/saml2/metadata/impl/AuthnAuthorityDescriptor.xml";
        singleElementOptionalAttributesFile =
                "/org/opensaml/saml/saml2/metadata/impl/AuthnAuthorityDescriptorOptionalAttributes.xml";
        childElementsFile = "/org/opensaml/saml/saml2/metadata/impl/AuthnAuthorityDescriptorChildElements.xml";
    }

    @BeforeMethod protected void setUp() throws Exception {
        expectedSupportedProtocols = new ArrayList<>();
        expectedSupportedProtocols.add(SAMLConstants.SAML20P_NS);
        expectedCacheDuration = 90000;
        expectedValidUntil = new DateTime(2005, 12, 7, 10, 21, 0, 0, ISOChronology.getInstanceUTC());
        expectedErrorURL = "http://example.org";
        //
        // Element counts
        //
        expectedKeyDescriptors = 0;
        expectedContactPersons = 2;
        expectedAuthnQueryServices = 3;
        expectedAssertionIdRequestServices = 2;
        expectedNameIdFormats = 1;
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnmarshall() {
        AuthnAuthorityDescriptor authnAuthorityObj = (AuthnAuthorityDescriptor) unmarshallElement(singleElementFile);

        List<String> protoEnum = authnAuthorityObj.getSupportedProtocols();
        Assert.assertEquals(protoEnum, expectedSupportedProtocols,
                "Supported protocol enumeration was not equal to expected enumeration");

        Long duration = authnAuthorityObj.getCacheDuration();
        Assert.assertNull(duration, "cacheDuration attribute has a value of " + duration + ", expected no value");

        DateTime validUntil = authnAuthorityObj.getValidUntil();
        Assert.assertNull(validUntil, "validUntil attribute has a value of " + validUntil + ", expected no value");

        String errorURL = authnAuthorityObj.getErrorURL();
        Assert.assertNull(errorURL, "errorURL attribute has a value of " + errorURL + ", expected no value");
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementOptionalAttributesUnmarshall() {
        AuthnAuthorityDescriptor authnAuthorityObj =
                (AuthnAuthorityDescriptor) unmarshallElement(singleElementOptionalAttributesFile);

        List<String> protoEnum = authnAuthorityObj.getSupportedProtocols();
        Assert.assertEquals(protoEnum, expectedSupportedProtocols,
                "Supported protocol enumeration was not equal to expected enumeration");

        long duration = authnAuthorityObj.getCacheDuration().longValue();
        Assert.assertEquals(duration, expectedCacheDuration, "cacheDuration attribute has a value of " + duration
                + ", expected a value of " + expectedCacheDuration);

        DateTime validUntil = authnAuthorityObj.getValidUntil();
        Assert.assertEquals(expectedValidUntil.compareTo(validUntil), 0,
                "validUntil attribute value did not match expected value");

        String errorURL = authnAuthorityObj.getErrorURL();
        Assert.assertEquals(errorURL, expectedErrorURL, "errorURL attribute has a value of " + errorURL
                + ", expected a value of " + expectedErrorURL);
    }

    /** {@inheritDoc} */

    @Test public void testChildElementsUnmarshall() {
        AuthnAuthorityDescriptor authnAuthorityObj = (AuthnAuthorityDescriptor) unmarshallElement(childElementsFile);

        Assert.assertNotNull(authnAuthorityObj.getExtensions(), "<Extensions>");
        Assert.assertEquals(authnAuthorityObj.getKeyDescriptors().size(), 0, "KeyDescriptor");

        Assert.assertEquals(authnAuthorityObj.getKeyDescriptors().size(), expectedKeyDescriptors,
                "KeyDescriptors count");
        Assert.assertNotNull(authnAuthorityObj.getOrganization(), "Organization");
        Assert.assertEquals(authnAuthorityObj.getContactPersons().size(), expectedContactPersons,
                "ContactPersons count");
        Assert.assertEquals(authnAuthorityObj.getAuthnQueryServices().size(), expectedAuthnQueryServices,
                "AuthnQueryServices count");
        Assert.assertEquals(authnAuthorityObj.getAssertionIDRequestServices().size(),
                expectedAssertionIdRequestServices, "AssertionIDRequestServices count");
        Assert.assertEquals(authnAuthorityObj.getNameIDFormats().size(), expectedNameIdFormats, "NameIdFormats count");

        Assert.assertEquals(authnAuthorityObj.getEndpoints().size(), expectedAuthnQueryServices
                + expectedAssertionIdRequestServices, "Endpoints count");
        Assert.assertEquals(authnAuthorityObj.getEndpoints(AuthnQueryService.DEFAULT_ELEMENT_NAME).size(),
                expectedAuthnQueryServices, "Endpoints(AuthnQueryService) count");
        Assert.assertEquals(authnAuthorityObj.getEndpoints(AssertionIDRequestService.DEFAULT_ELEMENT_NAME).size(),
                expectedAssertionIdRequestServices, "Endpoints(AssertionIdRequestService) count");
        Assert.assertNull(authnAuthorityObj.getEndpoints(EntityDescriptor.DEFAULT_ELEMENT_NAME));
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementMarshall() {
        AuthnAuthorityDescriptor descriptor = (new AuthnAuthorityDescriptorBuilder()).buildObject();

        descriptor.addSupportedProtocol(SAMLConstants.SAML20P_NS);

        assertXMLEquals(expectedDOM, descriptor);
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementOptionalAttributesMarshall() {
        AuthnAuthorityDescriptor descriptor = (new AuthnAuthorityDescriptorBuilder()).buildObject();

        descriptor.addSupportedProtocol(SAMLConstants.SAML20P_NS);
        descriptor.setValidUntil(expectedValidUntil);
        descriptor.setCacheDuration(expectedCacheDuration);
        descriptor.setErrorURL(expectedErrorURL);

        assertXMLEquals(expectedOptionalAttributesDOM, descriptor);
    }

    /** {@inheritDoc} */

    @Test public void testChildElementsMarshall() {
        QName qname =
                new QName(SAMLConstants.SAML20MD_NS, AuthnAuthorityDescriptor.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        AuthnAuthorityDescriptor descriptor = (AuthnAuthorityDescriptor) buildXMLObject(qname);

        descriptor.addSupportedProtocol(SAMLConstants.SAML20P_NS);

        QName extensionsQName =
                new QName(SAMLConstants.SAML20MD_NS, Extensions.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        descriptor.setExtensions((Extensions) buildXMLObject(extensionsQName));

        QName orgQName =
                new QName(SAMLConstants.SAML20MD_NS, Organization.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        descriptor.setOrganization((Organization) buildXMLObject(orgQName));

        QName contactPersonQName =
                new QName(SAMLConstants.SAML20MD_NS, ContactPerson.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < expectedContactPersons; i++) {
            descriptor.getContactPersons().add((ContactPerson) buildXMLObject(contactPersonQName));
        }

        QName authnQueryServiceQName =
                new QName(SAMLConstants.SAML20MD_NS, AuthnQueryService.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < expectedAuthnQueryServices; i++) {
            descriptor.getAuthnQueryServices().add((AuthnQueryService) buildXMLObject(authnQueryServiceQName));
        }

        QName assertionIDRequestServiceQName =
                new QName(SAMLConstants.SAML20MD_NS, AssertionIDRequestService.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < expectedAssertionIdRequestServices; i++) {
            descriptor.getAssertionIDRequestServices().add(
                    (AssertionIDRequestService) buildXMLObject(assertionIDRequestServiceQName));
        }

        QName nameIDFormatQName =
                new QName(SAMLConstants.SAML20MD_NS, NameIDFormat.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < expectedNameIdFormats; i++) {
            descriptor.getNameIDFormats().add((NameIDFormat) buildXMLObject(nameIDFormatQName));
        }

        assertXMLEquals(expectedChildElementsDOM, descriptor);
    }
}