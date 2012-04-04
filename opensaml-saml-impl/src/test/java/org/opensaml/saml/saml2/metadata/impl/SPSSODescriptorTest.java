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

        Assert.assertEquals(descriptor
                .getSupportedProtocols(), expectedSupportedProtocol, "Supported protocols not equal to expected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        SPSSODescriptor descriptor = (SPSSODescriptor) unmarshallElement(singleElementOptionalAttributesFile);

        Assert.assertEquals(descriptor
                .getSupportedProtocols(), expectedSupportedProtocol, "Supported protocols not equal to expected value");
        Assert.assertEquals(descriptor
                .isAuthnRequestsSignedXSBoolean(), expectedAuthnRequestSigned, "AuthnRequestsSigned attribute was not expected value");
        Assert.assertEquals(descriptor
                .getWantAssertionsSignedXSBoolean(), expectedWantAssertionsSigned, "WantAssertionsSigned attribute was not expected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        SPSSODescriptor descriptor = (SPSSODescriptor) unmarshallElement(childElementsFile);

        Assert.assertNotNull(descriptor.getExtensions(), "Extensions");
        Assert.assertEquals(descriptor.getKeyDescriptors().size(), 0, "KeyDescriptor");
        Assert.assertNotNull(descriptor.getOrganization(), "Organization child");
        Assert.assertEquals(descriptor.getContactPersons().size(), 2, "ContactPerson count");

        Assert.assertEquals(descriptor.getArtifactResolutionServices().size(), 1, "ArtifactResolutionService count");
        Assert.assertEquals(descriptor.getSingleLogoutServices().size(), 2, "SingleLogoutService count");
        Assert.assertEquals(descriptor.getManageNameIDServices().size(), 4, "ManageNameIDService count");
        Assert.assertEquals(descriptor.getNameIDFormats().size(), 1, "NameIDFormat count");

        Assert.assertEquals(descriptor.getAssertionConsumerServices().size(), 2, "AssertionConsumerService count");
        Assert.assertEquals(descriptor.getAttributeConsumingServices().size(), 1, "AttributeConsumingService");
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
        Assert.assertEquals(descriptor.isAuthnRequestsSigned(), Boolean.TRUE, "Unexpected value for boolean attribute found");
        Assert.assertNotNull(descriptor.isAuthnRequestsSignedXSBoolean(), "XSBooleanValue was null");
        Assert.assertEquals(descriptor.isAuthnRequestsSignedXSBoolean(), new XSBooleanValue(Boolean.TRUE, false),
                "XSBooleanValue was unexpected value");
        Assert.assertEquals(descriptor.isAuthnRequestsSignedXSBoolean().toString(), "true",
                "XSBooleanValue string was unexpected value");
        
        descriptor.setAuthnRequestsSigned(Boolean.FALSE);
        Assert.assertEquals(descriptor.isAuthnRequestsSigned(), Boolean.FALSE, "Unexpected value for boolean attribute found");
        Assert.assertNotNull(descriptor.isAuthnRequestsSignedXSBoolean(), "XSBooleanValue was null");
        Assert.assertEquals(descriptor.isAuthnRequestsSignedXSBoolean(), new XSBooleanValue(Boolean.FALSE, false),
                "XSBooleanValue was unexpected value");
        Assert.assertEquals(descriptor.isAuthnRequestsSignedXSBoolean().toString(), "false",
                "XSBooleanValue string was unexpected value");
        
        descriptor.setAuthnRequestsSigned((Boolean) null);
        Assert.assertEquals(descriptor.isAuthnRequestsSigned(), Boolean.FALSE, "Unexpected default value for boolean attribute found");
        Assert.assertNull(descriptor.isAuthnRequestsSignedXSBoolean(), "XSBooleanValue was not null");
        
        
        
        // WantAssertionsSigned
        descriptor.setWantAssertionsSigned(Boolean.TRUE);
        Assert.assertEquals(descriptor.getWantAssertionsSigned(), Boolean.TRUE, "Unexpected value for boolean attribute found");
        Assert.assertNotNull(descriptor.getWantAssertionsSignedXSBoolean(), "XSBooleanValue was null");
        Assert.assertEquals(descriptor.getWantAssertionsSignedXSBoolean(), new XSBooleanValue(Boolean.TRUE, false),
                "XSBooleanValue was unexpected value");
        Assert.assertEquals(descriptor.getWantAssertionsSignedXSBoolean().toString(), "true",
                "XSBooleanValue string was unexpected value");
        
        descriptor.setWantAssertionsSigned(Boolean.FALSE);
        Assert.assertEquals(descriptor.getWantAssertionsSigned(), Boolean.FALSE, "Unexpected value for boolean attribute found");
        Assert.assertNotNull(descriptor.getWantAssertionsSignedXSBoolean(), "XSBooleanValue was null");
        Assert.assertEquals(descriptor.getWantAssertionsSignedXSBoolean(), new XSBooleanValue(Boolean.FALSE, false),
                "XSBooleanValue was unexpected value");
        Assert.assertEquals(descriptor.getWantAssertionsSignedXSBoolean().toString(), "false",
                "XSBooleanValue string was unexpected value");
        
        descriptor.setWantAssertionsSigned((Boolean) null);
        Assert.assertEquals(descriptor.getWantAssertionsSigned(), Boolean.FALSE, "Unexpected default value for boolean attribute found");
        Assert.assertNull(descriptor.getWantAssertionsSignedXSBoolean(), "XSBooleanValue was not null");
    }

}