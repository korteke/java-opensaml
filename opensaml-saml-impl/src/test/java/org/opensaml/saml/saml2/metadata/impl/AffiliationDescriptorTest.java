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

import javax.xml.namespace.QName;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.AffiliateMember;
import org.opensaml.saml.saml2.metadata.AffiliationDescriptor;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.impl.AffiliationDescriptorImpl}.
 */
public class AffiliationDescriptorTest extends XMLObjectProviderBaseTestCase {

    /** Expected affiliationOwnerID value */
    protected String expectedOwnerID;

    /** Expceted ID value */
    protected String expectedID;

    /** Expected cacheDuration value in miliseconds */
    protected long expectedCacheDuration;

    /** Expected validUntil value */
    protected DateTime expectedValidUntil;

    /** Unknown Attributes */
    protected QName[] unknownAttributeNames = { new QName("urn:foo:bar", "bar", "foo"), new  QName("flibble") };
    /** Unknown Attribute Values */
    protected String[] unknownAttributeValues = {"fred", "flobble"};

    /**
     * Constructor
     */
    public AffiliationDescriptorTest() {
        singleElementFile = "/org/opensaml/saml/saml2/metadata/impl/AffiliationDescriptor.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml2/metadata/impl/AffiliationDescriptorOptionalAttributes.xml";
        childElementsFile = "/org/opensaml/saml/saml2/metadata/impl/AffiliationDescriptorChildElements.xml";
        singleElementUnknownAttributesFile = "/org/opensaml/saml/saml2/metadata/impl/AffiliationDescriptorUnknownAttributes.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedOwnerID = "urn:example.org";
        expectedID = "id";
        expectedCacheDuration = 90000;
        expectedValidUntil = new DateTime(2005, 12, 7, 10, 21, 0, 0, ISOChronology.getInstanceUTC());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        AffiliationDescriptor descriptor = (AffiliationDescriptor) unmarshallElement(singleElementFile);

        String ownerId = descriptor.getOwnerID();
        Assert.assertEquals(ownerId,
                expectedOwnerID, "entityID attribute has a value of " + ownerId + ", expected a value of " + expectedOwnerID);

        Long duration = descriptor.getCacheDuration();
        Assert.assertNull(duration, "cacheDuration attribute has a value of " + duration + ", expected no value");

        DateTime validUntil = descriptor.getValidUntil();
        Assert.assertNull(validUntil, "validUntil attribute has a value of " + validUntil + ", expected no value");
        
        Assert.assertTrue(descriptor.isValid());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        AffiliationDescriptor descriptor = (AffiliationDescriptor) unmarshallElement(singleElementOptionalAttributesFile);

        String ownerId = descriptor.getOwnerID();
        Assert.assertEquals(ownerId,
                expectedOwnerID, "entityID attribute has a value of " + ownerId + ", expected a value of " + expectedOwnerID);

        String id = descriptor.getID();
        Assert.assertEquals(id, expectedID, "ID attribute has a value of " + id + ", expected a value of " + expectedID);

        long duration = descriptor.getCacheDuration().longValue();
        Assert.assertEquals(duration, expectedCacheDuration, "cacheDuration attribute has a value of " + duration + ", expected a value of "
                        + expectedCacheDuration);

        DateTime validUntil = descriptor.getValidUntil();
        Assert.assertEquals(expectedValidUntil
                .compareTo(validUntil), 0, "validUntil attribute value did not match expected value");
        Assert.assertFalse(descriptor.isValid());
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnknownAttributesUnmarshall() {
        AffiliationDescriptor descriptor = (AffiliationDescriptor) unmarshallElement(singleElementUnknownAttributesFile);
        AttributeMap attributes = descriptor.getUnknownAttributes();

        Assert.assertEquals(attributes.entrySet().size(), unknownAttributeNames.length);
        for (int i = 0; i < unknownAttributeNames.length; i++) {
            Assert.assertEquals(attributes.get(unknownAttributeNames[i]), unknownAttributeValues[i]);
        }
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        AffiliationDescriptor descriptor = (AffiliationDescriptor) unmarshallElement(childElementsFile);

        Assert.assertNotNull(descriptor.getExtensions(), "Extensions");
        Assert.assertNotNull(descriptor.getSignature(), "Signature");
        Assert.assertEquals(descriptor.getKeyDescriptors().size(), 1, "KeyDescriptor count");
        Assert.assertEquals(descriptor.getMembers().size(), 3, "Affiliate Member count ");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        QName qname = new QName(SAMLConstants.SAML20MD_NS, AffiliationDescriptor.DEFAULT_ELEMENT_LOCAL_NAME,
                SAMLConstants.SAML20MD_PREFIX);
        AffiliationDescriptor descriptor = (AffiliationDescriptor) buildXMLObject(qname);

        descriptor.setOwnerID(expectedOwnerID);

        assertXMLEquals(expectedDOM, descriptor);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        AffiliationDescriptor descriptor = (new AffiliationDescriptorBuilder()).buildObject();

        descriptor.setOwnerID(expectedOwnerID);
        descriptor.setID(expectedID);
        descriptor.setValidUntil(expectedValidUntil);
        descriptor.setCacheDuration(expectedCacheDuration);

        assertXMLEquals(expectedOptionalAttributesDOM, descriptor);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnknownAttributesMarshall() {
        AffiliationDescriptor descriptor = (new AffiliationDescriptorBuilder()).buildObject();

        for (int i = 0; i < unknownAttributeNames.length; i++) {
            descriptor.getUnknownAttributes().put(unknownAttributeNames[i], unknownAttributeValues[i]);
        }
        assertXMLEquals(expectedUnknownAttributesDOM, descriptor);
    }

    @Test
    public void testChildElementsMarshall() {
        QName qname = new QName(SAMLConstants.SAML20MD_NS, AffiliationDescriptor.DEFAULT_ELEMENT_LOCAL_NAME,
                SAMLConstants.SAML20MD_PREFIX);
        AffiliationDescriptor descriptor = (AffiliationDescriptor) buildXMLObject(qname);

        StringBuilder bigString = new StringBuilder();
        for (int i=0; i < 1026; i++) {
            bigString.append('s');
        }
        try {
            descriptor.setOwnerID(bigString.toString());
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
        
        descriptor.setOwnerID(expectedOwnerID);
        descriptor.setID(expectedID);
        
        descriptor.setSignature( buildSignatureSkeleton() );

        descriptor.setExtensions((Extensions) buildXMLObject(Extensions.DEFAULT_ELEMENT_NAME));

        QName affilMemberQName = new QName(SAMLConstants.SAML20MD_NS, AffiliateMember.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        descriptor.getMembers().add((AffiliateMember) buildXMLObject(affilMemberQName));
        descriptor.getMembers().add((AffiliateMember) buildXMLObject(affilMemberQName));
        descriptor.getMembers().add((AffiliateMember) buildXMLObject(affilMemberQName));
        descriptor.getKeyDescriptors().add((new KeyDescriptorBuilder().buildObject()));

        assertXMLEquals(expectedChildElementsDOM, descriptor);
    }
    
    /**
     * Build a Signature skeleton to use in marshalling unit tests.
     * 
     * @return minimally populated Signature element
     */
    private Signature buildSignatureSkeleton() {
        Signature signature = (Signature) buildXMLObject(Signature.DEFAULT_ELEMENT_NAME);
        signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        return signature;
    }
    
}