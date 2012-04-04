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
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.AttributeConsumingService;
import org.opensaml.saml.saml2.metadata.ContactPerson;
import org.opensaml.saml.saml2.metadata.ManageNameIDService;
import org.opensaml.saml.saml2.metadata.NameIDFormat;
import org.opensaml.saml.saml2.metadata.Organization;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SingleLogoutService;

/**
 * 
 */
public class SPSSODescriptorTest extends XMLObjectProviderBaseTestCase {

    /** expected value for AuthnRequestSigned attribute */
    protected XSBooleanValue expectedAuthnRequestSigned;

    /** expected value for WantAssertionsSigned attribute */
    protected XSBooleanValue expectedWantAssertionsSigned;

    /** List of expected supported protocols */
    protected ArrayList<String> expectedSupportedProtocol;

    /** Expected cacheDuration value in miliseconds */
    protected long expectedCacheDuration;

    /** Expected validUntil value */
    protected DateTime expectedValidUntil;

    /**
     * Constructor
     */
    public SPSSODescriptorTest() {
        singleElementFile = "/data/org/opensaml/saml/saml2/metadata/impl/SPSSODescriptor.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml/saml2/metadata/impl/SPSSODescriptorOptionalAttributes.xml";
        childElementsFile = "/data/org/opensaml/saml/saml2/metadata/impl/SPSSODescriptorChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedAuthnRequestSigned = new XSBooleanValue(Boolean.TRUE, false);
        expectedWantAssertionsSigned = new XSBooleanValue(Boolean.TRUE, false);

        expectedSupportedProtocol = new ArrayList<String>();
        expectedSupportedProtocol.add("urn:foo:bar");
        expectedSupportedProtocol.add("urn:fooz:baz");

        expectedCacheDuration = 90000;
        expectedValidUntil = new DateTime(2005, 12, 7, 10, 21, 0, 0, ISOChronology.getInstanceUTC());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        SPSSODescriptor descriptor = (SPSSODescriptor) unmarshallElement(singleElementFile);

        AssertJUnit.assertEquals("Supported protocols not equal to expected value", expectedSupportedProtocol, descriptor
                .getSupportedProtocols());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        SPSSODescriptor descriptor = (SPSSODescriptor) unmarshallElement(singleElementOptionalAttributesFile);

        AssertJUnit.assertEquals("Supported protocols not equal to expected value", expectedSupportedProtocol, descriptor
                .getSupportedProtocols());
        AssertJUnit.assertEquals("AuthnRequestsSigned attribute was not expected value", expectedAuthnRequestSigned, descriptor
                .isAuthnRequestsSignedXSBoolean());
        AssertJUnit.assertEquals("WantAssertionsSigned attribute was not expected value", expectedWantAssertionsSigned, descriptor
                .getWantAssertionsSignedXSBoolean());
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        SPSSODescriptor descriptor = (SPSSODescriptor) unmarshallElement(childElementsFile);

        AssertJUnit.assertNotNull("Extensions", descriptor.getExtensions());
        AssertJUnit.assertEquals("KeyDescriptor", 0, descriptor.getKeyDescriptors().size());
        AssertJUnit.assertNotNull("Organization child", descriptor.getOrganization());
        AssertJUnit.assertEquals("ContactPerson count", 2, descriptor.getContactPersons().size());

        AssertJUnit.assertEquals("ArtifactResolutionService count", 1, descriptor.getArtifactResolutionServices().size());
        AssertJUnit.assertEquals("SingleLogoutService count", 2, descriptor.getSingleLogoutServices().size());
        AssertJUnit.assertEquals("ManageNameIDService count", 4, descriptor.getManageNameIDServices().size());
        AssertJUnit.assertEquals("NameIDFormat count", 1, descriptor.getNameIDFormats().size());

        AssertJUnit.assertEquals("AssertionConsumerService count", 2, descriptor.getAssertionConsumerServices().size());
        AssertJUnit.assertEquals("AttributeConsumingService", 1, descriptor.getAttributeConsumingServices().size());
    }

    @Test
    public void testSingleElementMarshall() {
        QName qname = new QName(SAMLConstants.SAML20MD_NS, SPSSODescriptor.DEFAULT_ELEMENT_LOCAL_NAME,
                SAMLConstants.SAML20MD_PREFIX);
        SPSSODescriptor descriptor = (SPSSODescriptor) buildXMLObject(qname);

        for (String protocol : expectedSupportedProtocol) {
            descriptor.addSupportedProtocol(protocol);
        }

        assertXMLEquals(expectedDOM, descriptor);
    }

    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        QName qname = new QName(SAMLConstants.SAML20MD_NS, SPSSODescriptor.DEFAULT_ELEMENT_LOCAL_NAME,
                SAMLConstants.SAML20MD_PREFIX);
        SPSSODescriptor descriptor = (SPSSODescriptor) buildXMLObject(qname);

        descriptor.setAuthnRequestsSigned(expectedAuthnRequestSigned);
        descriptor.setWantAssertionsSigned(expectedWantAssertionsSigned);

        for (String protocol : expectedSupportedProtocol) {
            descriptor.addSupportedProtocol(protocol);
        }

        descriptor.setCacheDuration(expectedCacheDuration);
        descriptor.setValidUntil(expectedValidUntil);

        assertXMLEquals(expectedOptionalAttributesDOM, descriptor);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        QName qname = new QName(SAMLConstants.SAML20MD_NS, SPSSODescriptor.DEFAULT_ELEMENT_LOCAL_NAME,
                SAMLConstants.SAML20MD_PREFIX);
        SPSSODescriptor descriptor = (SPSSODescriptor) buildXMLObject(qname);

        QName extensionsQName = new QName(SAMLConstants.SAML20MD_NS, Extensions.LOCAL_NAME,
                SAMLConstants.SAML20MD_PREFIX);
        descriptor.setExtensions((Extensions) buildXMLObject(extensionsQName));

        QName orgQName = new QName(SAMLConstants.SAML20MD_NS, Organization.DEFAULT_ELEMENT_LOCAL_NAME,
                SAMLConstants.SAML20MD_PREFIX);
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

        QName assertConsumeQName = new QName(SAMLConstants.SAML20MD_NS,
                AssertionConsumerService.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < 2; i++) {
            descriptor.getAssertionConsumerServices()
                    .add((AssertionConsumerService) buildXMLObject(assertConsumeQName));
        }

        QName attribConsumeQName = new QName(SAMLConstants.SAML20MD_NS,
                AttributeConsumingService.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        descriptor.getAttributeConsumingServices().add((AttributeConsumingService) buildXMLObject(attribConsumeQName));

        assertXMLEquals(expectedChildElementsDOM, descriptor);
    }
    
    /**
     * Test the proper behavior of the XSBooleanValue attributes.
     */
    @Test
    public void testXSBooleanAttributes() {
        SPSSODescriptor descriptor = (SPSSODescriptor) buildXMLObject(SPSSODescriptor.DEFAULT_ELEMENT_NAME);
        
        // AuthnRequestsSigned
        descriptor.setAuthnRequestsSigned(Boolean.TRUE);
        AssertJUnit.assertEquals("Unexpected value for boolean attribute found", Boolean.TRUE, descriptor.isAuthnRequestsSigned());
        AssertJUnit.assertNotNull("XSBooleanValue was null", descriptor.isAuthnRequestsSignedXSBoolean());
        AssertJUnit.assertEquals("XSBooleanValue was unexpected value", new XSBooleanValue(Boolean.TRUE, false),
                descriptor.isAuthnRequestsSignedXSBoolean());
        AssertJUnit.assertEquals("XSBooleanValue string was unexpected value", "true",
                descriptor.isAuthnRequestsSignedXSBoolean().toString());
        
        descriptor.setAuthnRequestsSigned(Boolean.FALSE);
        AssertJUnit.assertEquals("Unexpected value for boolean attribute found", Boolean.FALSE, descriptor.isAuthnRequestsSigned());
        AssertJUnit.assertNotNull("XSBooleanValue was null", descriptor.isAuthnRequestsSignedXSBoolean());
        AssertJUnit.assertEquals("XSBooleanValue was unexpected value", new XSBooleanValue(Boolean.FALSE, false),
                descriptor.isAuthnRequestsSignedXSBoolean());
        AssertJUnit.assertEquals("XSBooleanValue string was unexpected value", "false",
                descriptor.isAuthnRequestsSignedXSBoolean().toString());
        
        descriptor.setAuthnRequestsSigned((Boolean) null);
        AssertJUnit.assertEquals("Unexpected default value for boolean attribute found", Boolean.FALSE, descriptor.isAuthnRequestsSigned());
        AssertJUnit.assertNull("XSBooleanValue was not null", descriptor.isAuthnRequestsSignedXSBoolean());
        
        
        
        // WantAssertionsSigned
        descriptor.setWantAssertionsSigned(Boolean.TRUE);
        AssertJUnit.assertEquals("Unexpected value for boolean attribute found", Boolean.TRUE, descriptor.getWantAssertionsSigned());
        AssertJUnit.assertNotNull("XSBooleanValue was null", descriptor.getWantAssertionsSignedXSBoolean());
        AssertJUnit.assertEquals("XSBooleanValue was unexpected value", new XSBooleanValue(Boolean.TRUE, false),
                descriptor.getWantAssertionsSignedXSBoolean());
        AssertJUnit.assertEquals("XSBooleanValue string was unexpected value", "true",
                descriptor.getWantAssertionsSignedXSBoolean().toString());
        
        descriptor.setWantAssertionsSigned(Boolean.FALSE);
        AssertJUnit.assertEquals("Unexpected value for boolean attribute found", Boolean.FALSE, descriptor.getWantAssertionsSigned());
        AssertJUnit.assertNotNull("XSBooleanValue was null", descriptor.getWantAssertionsSignedXSBoolean());
        AssertJUnit.assertEquals("XSBooleanValue was unexpected value", new XSBooleanValue(Boolean.FALSE, false),
                descriptor.getWantAssertionsSignedXSBoolean());
        AssertJUnit.assertEquals("XSBooleanValue string was unexpected value", "false",
                descriptor.getWantAssertionsSignedXSBoolean().toString());
        
        descriptor.setWantAssertionsSigned((Boolean) null);
        AssertJUnit.assertEquals("Unexpected default value for boolean attribute found", Boolean.FALSE, descriptor.getWantAssertionsSigned());
        AssertJUnit.assertNull("XSBooleanValue was not null", descriptor.getWantAssertionsSignedXSBoolean());
    }

}