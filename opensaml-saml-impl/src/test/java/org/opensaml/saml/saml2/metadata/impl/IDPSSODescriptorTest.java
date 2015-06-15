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

import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.core.impl.AttributeBuilder;
import org.opensaml.saml.saml2.metadata.ArtifactResolutionService;
import org.opensaml.saml.saml2.metadata.AssertionIDRequestService;
import org.opensaml.saml.saml2.metadata.AttributeProfile;
import org.opensaml.saml.saml2.metadata.ContactPerson;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.ManageNameIDService;
import org.opensaml.saml.saml2.metadata.NameIDFormat;
import org.opensaml.saml.saml2.metadata.NameIDMappingService;
import org.opensaml.saml.saml2.metadata.Organization;
import org.opensaml.saml.saml2.metadata.SingleLogoutService;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * 
 */
public class IDPSSODescriptorTest extends XMLObjectProviderBaseTestCase {

    /** List of expected supported protocols */
    protected ArrayList<String> expectedSupportedProtocol;

    /** Expected cacheDuration value in miliseconds */
    protected long expectedCacheDuration;

    /** Expected validUntil value */
    protected DateTime expectedValidUntil;

    /** Expected error url */
    protected String expectedErrorURL;

    /** expected value for WantAuthnRequestSigned attribute */
    protected XSBooleanValue expectedWantAuthnReqSigned;

    /** Unknown Attributes */
    protected QName[] unknownAttributeNames = {new QName("urn:foo:bar", "bar", "foo"), new QName("flibble")};

    /** Unknown Attribute Values */
    protected String[] unknownAttributeValues = {"fred", "flobble"};

    /**
     * Constructor
     */
    public IDPSSODescriptorTest() {
        singleElementFile = "/data/org/opensaml/saml/saml2/metadata/impl/IDPSSODescriptor.xml";
        singleElementOptionalAttributesFile =
                "/data/org/opensaml/saml/saml2/metadata/impl/IDPSSODescriptorOptionalAttributes.xml";
        childElementsFile = "/data/org/opensaml/saml/saml2/metadata/impl/IDPSSODescriptorChildElements.xml";
        singleElementUnknownAttributesFile =
                "/data/org/opensaml/saml/saml2/metadata/impl/IDPSSODescriptorUnknownAttributes.xml";
    }

    @BeforeMethod protected void setUp() throws Exception {
        expectedSupportedProtocol = new ArrayList<>();
        expectedSupportedProtocol.add("urn:foo:bar");
        expectedSupportedProtocol.add("urn:fooz:baz");

        expectedCacheDuration = 90000;
        expectedValidUntil = new DateTime(2005, 12, 7, 10, 21, 0, 0, ISOChronology.getInstanceUTC());

        expectedErrorURL = "http://example.org";

        expectedWantAuthnReqSigned = new XSBooleanValue(Boolean.TRUE, false);
    }

    @Test public void testSingleElementUnmarshall() {
        IDPSSODescriptor descriptor = (IDPSSODescriptor) unmarshallElement(singleElementFile);

        Assert.assertEquals(descriptor.getSupportedProtocols(), expectedSupportedProtocol,
                "Supported protocols not equal to expected value");
    }

    @Test public void testSingleElementOptionalAttributesUnmarshall() {
        IDPSSODescriptor descriptor = (IDPSSODescriptor) unmarshallElement(singleElementOptionalAttributesFile);

        Assert.assertEquals(descriptor.getCacheDuration().longValue(), expectedCacheDuration,
                "Cache duration was not expected value");
        Assert.assertEquals(descriptor.getValidUntil(), expectedValidUntil, "ValidUntil was not expected value");
        Assert.assertEquals(descriptor.getWantAuthnRequestsSignedXSBoolean(), expectedWantAuthnReqSigned,
                "WantAuthnRequestsSigned attribute was not expected value");
    }

    @Test public void testSingleElementUnknownAttributesMarshall() {
        IDPSSODescriptor descriptor = (new IDPSSODescriptorBuilder()).buildObject();

        for (int i = 0; i < unknownAttributeNames.length; i++) {
            descriptor.getUnknownAttributes().put(unknownAttributeNames[i], unknownAttributeValues[i]);
        }
        assertXMLEquals(expectedUnknownAttributesDOM, descriptor);
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnknownAttributesUnmarshall() {
        IDPSSODescriptor descriptor = (IDPSSODescriptor) unmarshallElement(singleElementUnknownAttributesFile);
        AttributeMap attributes = descriptor.getUnknownAttributes();

        Assert.assertEquals(attributes.entrySet().size(), unknownAttributeNames.length);
        for (int i = 0; i < unknownAttributeNames.length; i++) {
            Assert.assertEquals(attributes.get(unknownAttributeNames[i]), unknownAttributeValues[i]);
        }
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsUnmarshall() {
        IDPSSODescriptor descriptor = (IDPSSODescriptor) unmarshallElement(childElementsFile);

        Assert.assertNotNull(descriptor.getExtensions(), "Extensions");
        Assert.assertNotNull(descriptor.getOrganization(), "Organization child");
        Assert.assertEquals(descriptor.getContactPersons().size(), 2, "ContactPerson count");

        Assert.assertEquals(descriptor.getArtifactResolutionServices().size(), 1, "ArtifactResolutionService count");
        Assert.assertEquals(descriptor.getSingleLogoutServices().size(), 2, "SingleLogoutService count");
        Assert.assertEquals(descriptor.getManageNameIDServices().size(), 4, "ManageNameIDService count");
        Assert.assertEquals(descriptor.getNameIDFormats().size(), 1, "NameIDFormat count");
        
        Assert.assertEquals(descriptor.getEndpoints().size(), 15, "All Endpoints");
        

        Assert.assertEquals(descriptor.getSingleSignOnServices().size(), 3, "SingleSignOnService count");
        Assert.assertEquals(descriptor.getEndpoints(SingleSignOnService.DEFAULT_ELEMENT_NAME).size(), 3, "SingleSignOnService count");

        Assert.assertEquals(descriptor.getNameIDMappingServices().size(), 2, "NameIDMappingService count");
        Assert.assertEquals(descriptor.getEndpoints(NameIDMappingService.DEFAULT_ELEMENT_NAME).size(), 2, "NameIDMappingService count");

        Assert.assertEquals(descriptor.getAssertionIDRequestServices().size(), 3, "AssertionIDRequestService count");
        Assert.assertEquals(descriptor.getEndpoints(AssertionIDRequestService.DEFAULT_ELEMENT_NAME).size(), 3, "AssertionIDRequestService count");

        Assert.assertEquals(descriptor.getAttributeProfiles().size(), 3, "AttributeProfile count");
        
        Assert.assertEquals(descriptor.getAttributes().size(), 1);
    }

    @Test public void testSingleElementMarshall() {
        QName qname =
                new QName(SAMLConstants.SAML20MD_NS, IDPSSODescriptor.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        IDPSSODescriptor descriptor = (IDPSSODescriptor) buildXMLObject(qname);

        for (String protocol : expectedSupportedProtocol) {
            descriptor.addSupportedProtocol(protocol);
        }
        descriptor.setWantAuthnRequestsSigned(expectedWantAuthnReqSigned);

        assertXMLEquals(expectedDOM, descriptor);
    }

    @Test public void testSingleElementOptionalAttributesMarshall() {
        QName qname =
                new QName(SAMLConstants.SAML20MD_NS, IDPSSODescriptor.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        IDPSSODescriptor descriptor = (IDPSSODescriptor) buildXMLObject(qname);

        for (String protocol : expectedSupportedProtocol) {
            descriptor.addSupportedProtocol(protocol);
        }

        descriptor.setCacheDuration(expectedCacheDuration);
        descriptor.setValidUntil(expectedValidUntil);
        descriptor.setErrorURL(expectedErrorURL);
        descriptor.setWantAuthnRequestsSigned(expectedWantAuthnReqSigned);

        assertXMLEquals(expectedOptionalAttributesDOM, descriptor);
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsMarshall() {
        QName qname =
                new QName(SAMLConstants.SAML20MD_NS, IDPSSODescriptor.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        IDPSSODescriptor descriptor = (IDPSSODescriptor) buildXMLObject(qname);

        QName extensionsQName =
                new QName(SAMLConstants.SAML20MD_NS, Extensions.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        descriptor.setExtensions((Extensions) buildXMLObject(extensionsQName));

        QName orgQName =
                new QName(SAMLConstants.SAML20MD_NS, Organization.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        descriptor.setOrganization((Organization) buildXMLObject(orgQName));

        QName contactQName =
                new QName(SAMLConstants.SAML20MD_NS, ContactPerson.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < 2; i++) {
            descriptor.getContactPersons().add((ContactPerson) buildXMLObject(contactQName));
        }

        QName artResQName =
                new QName(SAMLConstants.SAML20MD_NS, ArtifactResolutionService.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        descriptor.getArtifactResolutionServices().add((ArtifactResolutionService) buildXMLObject(artResQName));

        QName sloQName =
                new QName(SAMLConstants.SAML20MD_NS, SingleLogoutService.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < 2; i++) {
            descriptor.getSingleLogoutServices().add((SingleLogoutService) buildXMLObject(sloQName));
        }

        QName mngNameIDQName =
                new QName(SAMLConstants.SAML20MD_NS, ManageNameIDService.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < 4; i++) {
            descriptor.getManageNameIDServices().add((ManageNameIDService) buildXMLObject(mngNameIDQName));
        }

        QName nameIDFormatQName =
                new QName(SAMLConstants.SAML20MD_NS, NameIDFormat.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        descriptor.getNameIDFormats().add((NameIDFormat) buildXMLObject(nameIDFormatQName));

        QName ssoQName =
                new QName(SAMLConstants.SAML20MD_NS, SingleSignOnService.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < 3; i++) {
            descriptor.getSingleSignOnServices().add((SingleSignOnService) buildXMLObject(ssoQName));
        }

        QName nameIDMapQName =
                new QName(SAMLConstants.SAML20MD_NS, NameIDMappingService.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < 2; i++) {
            descriptor.getNameIDMappingServices().add((NameIDMappingService) buildXMLObject(nameIDMapQName));
        }

        QName assertIDReqQName =
                new QName(SAMLConstants.SAML20MD_NS, AssertionIDRequestService.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < 3; i++) {
            descriptor.getAssertionIDRequestServices()
                    .add((AssertionIDRequestService) buildXMLObject(assertIDReqQName));
        }

        QName attributeProlfileQName =
                new QName(SAMLConstants.SAML20MD_NS, AttributeProfile.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < 3; i++) {
            descriptor.getAttributeProfiles().add((AttributeProfile) buildXMLObject(attributeProlfileQName));
        }
        descriptor.getAttributes().add((new AttributeBuilder()).buildObject());
        assertXMLEquals(expectedChildElementsDOM, descriptor);
    }

    /**
     * Test the proper behavior of the XSBooleanValue attributes.
     */
    @Test public void testXSBooleanAttributes() {
        IDPSSODescriptor descriptor = (IDPSSODescriptor) buildXMLObject(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);

        descriptor.setWantAuthnRequestsSigned(Boolean.TRUE);
        Assert.assertEquals(descriptor.getWantAuthnRequestsSigned(), Boolean.TRUE,
                "Unexpected value for boolean attribute found");
        Assert.assertNotNull(descriptor.getWantAuthnRequestsSignedXSBoolean(), "XSBooleanValue was null");
        Assert.assertEquals(descriptor.getWantAuthnRequestsSignedXSBoolean(), new XSBooleanValue(Boolean.TRUE, false),
                "XSBooleanValue was unexpected value");
        Assert.assertEquals(descriptor.getWantAuthnRequestsSignedXSBoolean().toString(), "true",
                "XSBooleanValue string was unexpected value");

        descriptor.setWantAuthnRequestsSigned(Boolean.FALSE);
        Assert.assertEquals(descriptor.getWantAuthnRequestsSigned(), Boolean.FALSE,
                "Unexpected value for boolean attribute found");
        Assert.assertNotNull(descriptor.getWantAuthnRequestsSignedXSBoolean(), "XSBooleanValue was null");
        Assert.assertEquals(descriptor.getWantAuthnRequestsSignedXSBoolean(), new XSBooleanValue(Boolean.FALSE, false),
                "XSBooleanValue was unexpected value");
        Assert.assertEquals(descriptor.getWantAuthnRequestsSignedXSBoolean().toString(), "false",
                "XSBooleanValue string was unexpected value");

        descriptor.setWantAuthnRequestsSigned((Boolean) null);
        Assert.assertEquals(descriptor.getWantAuthnRequestsSigned(), Boolean.FALSE,
                "Unexpected default value for boolean attribute found");
        Assert.assertNull(descriptor.getWantAuthnRequestsSignedXSBoolean(), "XSBooleanValue was not null");
    }

}