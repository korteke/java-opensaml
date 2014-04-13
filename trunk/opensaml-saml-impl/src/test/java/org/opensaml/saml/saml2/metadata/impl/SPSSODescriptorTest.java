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
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
    
    /** Expected Id */
    protected String expectedId;

    /**
     * Constructor
     */
    public SPSSODescriptorTest() {
        singleElementFile = "/data/org/opensaml/saml/saml2/metadata/impl/SPSSODescriptor.xml";
        singleElementOptionalAttributesFile =
                "/data/org/opensaml/saml/saml2/metadata/impl/SPSSODescriptorOptionalAttributes.xml";
        childElementsFile = "/data/org/opensaml/saml/saml2/metadata/impl/SPSSODescriptorChildElements.xml";
    }

    @BeforeMethod protected void setUp() throws Exception {
        expectedAuthnRequestSigned = new XSBooleanValue(Boolean.TRUE, false);
        expectedWantAssertionsSigned = new XSBooleanValue(Boolean.TRUE, false);

        expectedSupportedProtocol = new ArrayList<String>();
        expectedSupportedProtocol.add("urn:foo:bar");
        expectedSupportedProtocol.add("urn:fooz:baz");

        expectedCacheDuration = 90000;
        expectedValidUntil = new DateTime(2005, 12, 7, 10, 21, 0, 0, ISOChronology.getInstanceUTC());
        expectedId = "id";
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnmarshall() {
        SPSSODescriptor descriptor = (SPSSODescriptor) unmarshallElement(singleElementFile);

        Assert.assertEquals(descriptor.getSupportedProtocols(), expectedSupportedProtocol,
                "Supported protocols not equal to expected value");
        descriptor.removeAllSupportedProtocols();
        Assert.assertEquals(descriptor.getSupportedProtocols().size(), 0);
        Assert.assertTrue(descriptor.isValid());
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementOptionalAttributesUnmarshall() {
        SPSSODescriptor descriptor = (SPSSODescriptor) unmarshallElement(singleElementOptionalAttributesFile);

        Assert.assertEquals(descriptor.getSupportedProtocols(), expectedSupportedProtocol,
                "Supported protocols not equal to expected value");
        Assert.assertEquals(descriptor.isAuthnRequestsSignedXSBoolean(), expectedAuthnRequestSigned,
                "AuthnRequestsSigned attribute was not expected value");
        Assert.assertEquals(descriptor.getWantAssertionsSignedXSBoolean(), expectedWantAssertionsSigned,
                "WantAssertionsSigned attribute was not expected value");

        Assert.assertEquals(descriptor.getValidUntil(), expectedValidUntil,
                "ValudUntil attribute was not expected value");
        Assert.assertFalse(descriptor.isValid());
        
        descriptor.removeSupportedProtocol("urn:foo:bar");
        Assert.assertEquals(descriptor.getSupportedProtocols().size(), expectedSupportedProtocol.size() - 1);
        
        descriptor.removeSupportedProtocols(expectedSupportedProtocol);
        Assert.assertEquals(descriptor.getSupportedProtocols().size(), 0);
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsUnmarshall() {
        SPSSODescriptor descriptor = (SPSSODescriptor) unmarshallElement(childElementsFile);

        Assert.assertEquals(descriptor.getID(), expectedId);
        Assert.assertEquals(descriptor.getSignatureReferenceID(), expectedId);
        Assert.assertNotNull(descriptor.getSignature());
        
        Assert.assertNotNull(descriptor.getExtensions(), "Extensions");
        Assert.assertEquals(descriptor.getKeyDescriptors().size(), 0, "KeyDescriptor");
        Assert.assertNotNull(descriptor.getOrganization(), "Organization child");
        Assert.assertEquals(descriptor.getContactPersons().size(), 2, "ContactPerson count");

        Assert.assertEquals(descriptor.getArtifactResolutionServices().size(), 1, "ArtifactResolutionService count");
        Assert.assertEquals(descriptor.getEndpoints(ArtifactResolutionService.DEFAULT_ELEMENT_NAME).size(), 1, "ArtifactResolutionServices");
        
        Assert.assertEquals(descriptor.getSingleLogoutServices().size(), 2, "SingleLogoutService count");
        Assert.assertEquals(descriptor.getEndpoints(SingleLogoutService.DEFAULT_ELEMENT_NAME).size(), 2, "SingleLogoutService count");

        Assert.assertEquals(descriptor.getManageNameIDServices().size(), 4, "ManageNameIDService count");
        Assert.assertEquals(descriptor.getEndpoints(ManageNameIDService.DEFAULT_ELEMENT_NAME).size(), 4, "ManageNameIDService count");

        Assert.assertEquals(descriptor.getNameIDFormats().size(), 1, "NameIDFormat count");

        Assert.assertEquals(descriptor.getAssertionConsumerServices().size(), 2, "AssertionConsumerService count");
        Assert.assertEquals(descriptor.getEndpoints(AssertionConsumerService.DEFAULT_ELEMENT_NAME).size(), 2, "AssertionConsumerService count");
        Assert.assertEquals(descriptor.getAttributeConsumingServices().size(), 1, "AttributeConsumingService");

        Assert.assertEquals(descriptor.getEndpoints().size(), 9);
        
        Assert.assertNotNull(descriptor.getDefaultArtifactResolutionService());
        Assert.assertNotNull(descriptor.getDefaultAttributeConsumingService());
        Assert.assertNotNull(descriptor.getDefaultAssertionConsumerService());
    }

    @Test public void testSingleElementMarshall() {
        QName qname =
                new QName(SAMLConstants.SAML20MD_NS, SPSSODescriptor.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        SPSSODescriptor descriptor = (SPSSODescriptor) buildXMLObject(qname);

        for (String protocol : expectedSupportedProtocol) {
            descriptor.addSupportedProtocol(protocol);
        }

        assertXMLEquals(expectedDOM, descriptor);
    }

    @Test public void testSingleElementOptionalAttributesMarshall() {
        SPSSODescriptor descriptor = (new SPSSODescriptorBuilder()).buildObject();

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
    @Test public void testChildElementsMarshall() {
        QName qname =
                new QName(SAMLConstants.SAML20MD_NS, SPSSODescriptor.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        SPSSODescriptor descriptor = (SPSSODescriptor) buildXMLObject(qname);

        descriptor.setID(expectedId);
        descriptor.setSignature( buildSignatureSkeleton() );

        QName extensionsQName =
                new QName(SAMLConstants.SAML20MD_NS, Extensions.LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
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

        QName assertConsumeQName =
                new QName(SAMLConstants.SAML20MD_NS, AssertionConsumerService.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < 2; i++) {
            descriptor.getAssertionConsumerServices()
                    .add((AssertionConsumerService) buildXMLObject(assertConsumeQName));
        }

        QName attribConsumeQName =
                new QName(SAMLConstants.SAML20MD_NS, AttributeConsumingService.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        descriptor.getAttributeConsumingServices().add((AttributeConsumingService) buildXMLObject(attribConsumeQName));

        assertXMLEquals(expectedChildElementsDOM, descriptor);
    }

    /**
     * Test the proper behavior of the XSBooleanValue attributes.
     */
    @Test public void testXSBooleanAttributes() {
        SPSSODescriptor descriptor = (SPSSODescriptor) buildXMLObject(SPSSODescriptor.DEFAULT_ELEMENT_NAME);

        // AuthnRequestsSigned
        descriptor.setAuthnRequestsSigned(Boolean.TRUE);
        Assert.assertEquals(descriptor.isAuthnRequestsSigned(), Boolean.TRUE,
                "Unexpected value for boolean attribute found");
        Assert.assertNotNull(descriptor.isAuthnRequestsSignedXSBoolean(), "XSBooleanValue was null");
        Assert.assertEquals(descriptor.isAuthnRequestsSignedXSBoolean(), new XSBooleanValue(Boolean.TRUE, false),
                "XSBooleanValue was unexpected value");
        Assert.assertEquals(descriptor.isAuthnRequestsSignedXSBoolean().toString(), "true",
                "XSBooleanValue string was unexpected value");

        descriptor.setAuthnRequestsSigned(Boolean.FALSE);
        Assert.assertEquals(descriptor.isAuthnRequestsSigned(), Boolean.FALSE,
                "Unexpected value for boolean attribute found");
        Assert.assertNotNull(descriptor.isAuthnRequestsSignedXSBoolean(), "XSBooleanValue was null");
        Assert.assertEquals(descriptor.isAuthnRequestsSignedXSBoolean(), new XSBooleanValue(Boolean.FALSE, false),
                "XSBooleanValue was unexpected value");
        Assert.assertEquals(descriptor.isAuthnRequestsSignedXSBoolean().toString(), "false",
                "XSBooleanValue string was unexpected value");

        descriptor.setAuthnRequestsSigned((Boolean) null);
        Assert.assertEquals(descriptor.isAuthnRequestsSigned(), Boolean.FALSE,
                "Unexpected default value for boolean attribute found");
        Assert.assertNull(descriptor.isAuthnRequestsSignedXSBoolean(), "XSBooleanValue was not null");

        // WantAssertionsSigned
        descriptor.setWantAssertionsSigned(Boolean.TRUE);
        Assert.assertEquals(descriptor.getWantAssertionsSigned(), Boolean.TRUE,
                "Unexpected value for boolean attribute found");
        Assert.assertNotNull(descriptor.getWantAssertionsSignedXSBoolean(), "XSBooleanValue was null");
        Assert.assertEquals(descriptor.getWantAssertionsSignedXSBoolean(), new XSBooleanValue(Boolean.TRUE, false),
                "XSBooleanValue was unexpected value");
        Assert.assertEquals(descriptor.getWantAssertionsSignedXSBoolean().toString(), "true",
                "XSBooleanValue string was unexpected value");

        descriptor.setWantAssertionsSigned(Boolean.FALSE);
        Assert.assertEquals(descriptor.getWantAssertionsSigned(), Boolean.FALSE,
                "Unexpected value for boolean attribute found");
        Assert.assertNotNull(descriptor.getWantAssertionsSignedXSBoolean(), "XSBooleanValue was null");
        Assert.assertEquals(descriptor.getWantAssertionsSignedXSBoolean(), new XSBooleanValue(Boolean.FALSE, false),
                "XSBooleanValue was unexpected value");
        Assert.assertEquals(descriptor.getWantAssertionsSignedXSBoolean().toString(), "false",
                "XSBooleanValue string was unexpected value");

        descriptor.setWantAssertionsSigned((Boolean) null);
        Assert.assertEquals(descriptor.getWantAssertionsSigned(), Boolean.FALSE,
                "Unexpected default value for boolean attribute found");
        Assert.assertNull(descriptor.getWantAssertionsSignedXSBoolean(), "XSBooleanValue was not null");
    }

    /**
     * Build a Signature skeleton to use in marshalling unit tests.
     * 
     * @return minimally populated Signature element
     */
    private Signature buildSignatureSkeleton() {
        Signature signature = (Signature) buildXMLObject(Signature.DEFAULT_ELEMENT_NAME);
        signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);
        signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        return signature;
    }

}