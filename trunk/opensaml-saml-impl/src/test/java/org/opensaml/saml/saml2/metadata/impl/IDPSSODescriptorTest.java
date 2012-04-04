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
import org.testng.AssertJUnit;
import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.common.Extensions;
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

    /**
     * Constructor
     */
    public IDPSSODescriptorTest() {
        singleElementFile = "/data/org/opensaml/saml/saml2/metadata/impl/IDPSSODescriptor.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml/saml2/metadata/impl/IDPSSODescriptorOptionalAttributes.xml";
        childElementsFile = "/data/org/opensaml/saml/saml2/metadata/impl/IDPSSODescriptorChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedSupportedProtocol = new ArrayList<String>();
        expectedSupportedProtocol.add("urn:foo:bar");
        expectedSupportedProtocol.add("urn:fooz:baz");

        expectedCacheDuration = 90000;
        expectedValidUntil = new DateTime(2005, 12, 7, 10, 21, 0, 0, ISOChronology.getInstanceUTC());

        expectedErrorURL = "http://example.org";

        expectedWantAuthnReqSigned = new XSBooleanValue(Boolean.TRUE, false);
    }

    @Test
    public void testSingleElementUnmarshall() {
        IDPSSODescriptor descriptor = (IDPSSODescriptor) unmarshallElement(singleElementFile);

        AssertJUnit.assertEquals("Supported protocols not equal to expected value", expectedSupportedProtocol, descriptor
                .getSupportedProtocols());
    }

    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        IDPSSODescriptor descriptor = (IDPSSODescriptor) unmarshallElement(singleElementOptionalAttributesFile);

        AssertJUnit.assertEquals("Cache duration was not expected value", expectedCacheDuration, descriptor.getCacheDuration()
                .longValue());
        AssertJUnit.assertEquals("ValidUntil was not expected value", expectedValidUntil, descriptor.getValidUntil());
        AssertJUnit.assertEquals("WantAuthnRequestsSigned attribute was not expected value", expectedWantAuthnReqSigned, descriptor
                .getWantAuthnRequestsSignedXSBoolean());
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        IDPSSODescriptor descriptor = (IDPSSODescriptor) unmarshallElement(childElementsFile);

        AssertJUnit.assertNotNull("Extensions", descriptor.getExtensions());
        AssertJUnit.assertNotNull("Organization child", descriptor.getOrganization());
        AssertJUnit.assertEquals("ContactPerson count", 2, descriptor.getContactPersons().size());

        AssertJUnit.assertEquals("ArtifactResolutionService count", 1, descriptor.getArtifactResolutionServices().size());
        AssertJUnit.assertEquals("SingleLogoutService count", 2, descriptor.getSingleLogoutServices().size());
        AssertJUnit.assertEquals("ManageNameIDService count", 4, descriptor.getManageNameIDServices().size());
        AssertJUnit.assertEquals("NameIDFormat count", 1, descriptor.getNameIDFormats().size());

        AssertJUnit.assertEquals("SingleSignOnService count", 3, descriptor.getSingleSignOnServices().size());
        AssertJUnit.assertEquals("NameIDMappingService count", 2, descriptor.getNameIDMappingServices().size());
        AssertJUnit.assertEquals("AssertionIDRequestService count", 3, descriptor.getAssertionIDRequestServices().size());
        AssertJUnit.assertEquals("AttributeProfile count", 3, descriptor.getAttributeProfiles().size());
    }

    @Test
    public void testSingleElementMarshall() {
        QName qname = new QName(SAMLConstants.SAML20MD_NS, IDPSSODescriptor.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        IDPSSODescriptor descriptor = (IDPSSODescriptor) buildXMLObject(qname);

        for (String protocol : expectedSupportedProtocol) {
            descriptor.addSupportedProtocol(protocol);
        }
        descriptor.setWantAuthnRequestsSigned(expectedWantAuthnReqSigned);

        assertXMLEquals(expectedDOM, descriptor);
    }

    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        QName qname = new QName(SAMLConstants.SAML20MD_NS, IDPSSODescriptor.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
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
    @Test
    public void testChildElementsMarshall() {
        QName qname = new QName(SAMLConstants.SAML20MD_NS, IDPSSODescriptor.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        IDPSSODescriptor descriptor = (IDPSSODescriptor) buildXMLObject(qname);

        QName extensionsQName = new QName(SAMLConstants.SAML20MD_NS, Extensions.LOCAL_NAME,
                SAMLConstants.SAML20MD_PREFIX);
        descriptor.setExtensions((Extensions) buildXMLObject(extensionsQName));

        QName orgQName = new QName(SAMLConstants.SAML20MD_NS, Organization.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        descriptor.setOrganization((Organization) buildXMLObject(orgQName));

        QName contactQName = new QName(SAMLConstants.SAML20MD_NS, ContactPerson.DEFAULT_ELEMENT_LOCAL_NAME,
                SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < 2; i++) {
            descriptor.getContactPersons().add((ContactPerson) buildXMLObject(contactQName));
        }

        QName artResQName = new QName(SAMLConstants.SAML20MD_NS, ArtifactResolutionService.DEFAULT_ELEMENT_LOCAL_NAME,
                SAMLConstants.SAML20MD_PREFIX);
        descriptor.getArtifactResolutionServices().add((ArtifactResolutionService) buildXMLObject(artResQName));

        QName sloQName = new QName(SAMLConstants.SAML20MD_NS, SingleLogoutService.DEFAULT_ELEMENT_LOCAL_NAME,
                SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < 2; i++) {
            descriptor.getSingleLogoutServices().add((SingleLogoutService) buildXMLObject(sloQName));
        }

        QName mngNameIDQName = new QName(SAMLConstants.SAML20MD_NS, ManageNameIDService.DEFAULT_ELEMENT_LOCAL_NAME,
                SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < 4; i++) {
            descriptor.getManageNameIDServices().add((ManageNameIDService) buildXMLObject(mngNameIDQName));
        }

        QName nameIDFormatQName = new QName(SAMLConstants.SAML20MD_NS, NameIDFormat.DEFAULT_ELEMENT_LOCAL_NAME,
                SAMLConstants.SAML20MD_PREFIX);
        descriptor.getNameIDFormats().add((NameIDFormat) buildXMLObject(nameIDFormatQName));

        QName ssoQName = new QName(SAMLConstants.SAML20MD_NS, SingleSignOnService.DEFAULT_ELEMENT_LOCAL_NAME,
                SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < 3; i++) {
            descriptor.getSingleSignOnServices().add((SingleSignOnService) buildXMLObject(ssoQName));
        }

        QName nameIDMapQName = new QName(SAMLConstants.SAML20MD_NS, NameIDMappingService.DEFAULT_ELEMENT_LOCAL_NAME,
                SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < 2; i++) {
            descriptor.getNameIDMappingServices().add((NameIDMappingService) buildXMLObject(nameIDMapQName));
        }

        QName assertIDReqQName = new QName(SAMLConstants.SAML20MD_NS, AssertionIDRequestService.DEFAULT_ELEMENT_LOCAL_NAME,
                SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < 3; i++) {
            descriptor.getAssertionIDRequestServices()
                    .add((AssertionIDRequestService) buildXMLObject(assertIDReqQName));
        }

        QName attributeProlfileQName = new QName(SAMLConstants.SAML20MD_NS, AttributeProfile.DEFAULT_ELEMENT_LOCAL_NAME,
                SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < 3; i++) {
            descriptor.getAttributeProfiles().add((AttributeProfile) buildXMLObject(attributeProlfileQName));
        }
        assertXMLEquals(expectedChildElementsDOM, descriptor);
    }
    
    /**
     * Test the proper behavior of the XSBooleanValue attributes.
     */
    @Test
    public void testXSBooleanAttributes() {
        IDPSSODescriptor descriptor = (IDPSSODescriptor) buildXMLObject(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        
        descriptor.setWantAuthnRequestsSigned(Boolean.TRUE);
        AssertJUnit.assertEquals("Unexpected value for boolean attribute found", Boolean.TRUE, descriptor.getWantAuthnRequestsSigned());
        AssertJUnit.assertNotNull("XSBooleanValue was null", descriptor.getWantAuthnRequestsSignedXSBoolean());
        AssertJUnit.assertEquals("XSBooleanValue was unexpected value", new XSBooleanValue(Boolean.TRUE, false),
                descriptor.getWantAuthnRequestsSignedXSBoolean());
        AssertJUnit.assertEquals("XSBooleanValue string was unexpected value", "true",
                descriptor.getWantAuthnRequestsSignedXSBoolean().toString());
        
        descriptor.setWantAuthnRequestsSigned(Boolean.FALSE);
        AssertJUnit.assertEquals("Unexpected value for boolean attribute found", Boolean.FALSE, descriptor.getWantAuthnRequestsSigned());
        AssertJUnit.assertNotNull("XSBooleanValue was null", descriptor.getWantAuthnRequestsSignedXSBoolean());
        AssertJUnit.assertEquals("XSBooleanValue was unexpected value", new XSBooleanValue(Boolean.FALSE, false),
                descriptor.getWantAuthnRequestsSignedXSBoolean());
        AssertJUnit.assertEquals("XSBooleanValue string was unexpected value", "false",
                descriptor.getWantAuthnRequestsSignedXSBoolean().toString());
        
        descriptor.setWantAuthnRequestsSigned((Boolean) null);
        AssertJUnit.assertEquals("Unexpected default value for boolean attribute found", Boolean.FALSE, descriptor.getWantAuthnRequestsSigned());
        AssertJUnit.assertNull("XSBooleanValue was not null", descriptor.getWantAuthnRequestsSignedXSBoolean());
    }

}