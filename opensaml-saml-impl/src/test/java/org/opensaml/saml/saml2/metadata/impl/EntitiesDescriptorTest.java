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
import javax.xml.namespace.QName;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.common.Extensions;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureConstants;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.impl.EntitiesDescriptorImpl}.
 */
public class EntitiesDescriptorTest extends XMLObjectProviderBaseTestCase {

    /** Expected Name attribute value */
    protected String expectedName;

    /** Expected ID attribute value */
    protected String expectedID;

    /** Expected cacheDuration value in miliseconds */
    protected long expectedCacheDuration;

    /** Expected validUntil value */
    protected DateTime expectedValidUntil;

    /** Expected number of child EntitiesDescriptors */
    protected int expectedEntitiesDescriptorsCount;

    /** Expected number of child EntityDescriptors */
    protected int expectedEntityDescriptorsCount;

    /**
     * Constructor
     */
    public EntitiesDescriptorTest() {
        singleElementFile = "/data/org/opensaml/saml/saml2/metadata/impl/EntitiesDescriptor.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml/saml2/metadata/impl/EntitiesDescriptorOptionalAttributes.xml";
        childElementsFile = "/data/org/opensaml/saml/saml2/metadata/impl/EntitiesDescriptorChildElements.xml";
    }

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedID = "id";
        expectedName = "eDescName";
        expectedCacheDuration = 90000;
        expectedValidUntil = new DateTime(2005, 12, 7, 10, 21, 0, 0, ISOChronology.getInstanceUTC());
        expectedEntitiesDescriptorsCount = 3;
        expectedEntityDescriptorsCount = 2;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        EntitiesDescriptor entitiesDescriptorObj = (EntitiesDescriptor) unmarshallElement(singleElementFile);

        String name = entitiesDescriptorObj.getName();
        Assert.assertNull(name, "Name attribute has a value of " + name + ", expected no value");

        Long duration = entitiesDescriptorObj.getCacheDuration();
        Assert.assertNull(duration, "cacheDuration attribute has a value of " + duration + ", expected no value");

        DateTime validUntil = entitiesDescriptorObj.getValidUntil();
        Assert.assertNull(validUntil, "validUntil attribute has a value of " + validUntil + ", expected no value");
        Assert.assertTrue(entitiesDescriptorObj.isValid());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        EntitiesDescriptor entitiesDescriptorObj = (EntitiesDescriptor) unmarshallElement(singleElementOptionalAttributesFile);

        String name = entitiesDescriptorObj.getName();
        Assert.assertEquals(name, expectedName,
                "Name attribute has a value of " + name + ", expected a value of " + expectedName);

        String id = entitiesDescriptorObj.getID();
        Assert.assertEquals(id, expectedID, "ID attriubte has a value of " + id + ", expected a value of " + expectedID);

        long duration = entitiesDescriptorObj.getCacheDuration().longValue();
        Assert.assertEquals(duration, expectedCacheDuration, "cacheDuration attribute has a value of " + duration + ", expected a value of "
                        + expectedCacheDuration);

        DateTime validUntil = entitiesDescriptorObj.getValidUntil();
        Assert.assertEquals(expectedValidUntil
                .compareTo(validUntil), 0, "validUntil attribute value did not match expected value");
        Assert.assertFalse(entitiesDescriptorObj.isValid());
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        EntitiesDescriptor entitiesDescriptor = (EntitiesDescriptor) unmarshallElement(childElementsFile);

        Assert.assertNotNull(entitiesDescriptor.getSignature(), "Signature");
        Assert.assertNotNull(entitiesDescriptor.getExtensions(), "Extensions");
        Assert.assertEquals(entitiesDescriptor
                .getEntitiesDescriptors().size(), expectedEntitiesDescriptorsCount, "Entities Descriptor child elements");
        Assert.assertEquals(entitiesDescriptor
                .getEntityDescriptors().size(), expectedEntityDescriptorsCount, "Entity Descriptor child elements");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        QName qname = new QName(SAMLConstants.SAML20MD_NS, EntitiesDescriptor.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        EntitiesDescriptor entitiesDescriptor = (EntitiesDescriptor) buildXMLObject(qname);

        assertXMLEquals(expectedDOM, entitiesDescriptor);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        QName qname = new QName(SAMLConstants.SAML20MD_NS, EntitiesDescriptor.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        EntitiesDescriptor entitiesDescriptor = (EntitiesDescriptor) buildXMLObject(qname);

        entitiesDescriptor.setName(expectedName);
        entitiesDescriptor.setID(expectedID);
        entitiesDescriptor.setCacheDuration(new Long(expectedCacheDuration));
        entitiesDescriptor.setValidUntil(expectedValidUntil);

        assertXMLEquals(expectedOptionalAttributesDOM, entitiesDescriptor);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        QName qname = new QName(SAMLConstants.SAML20MD_NS, EntitiesDescriptor.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        EntitiesDescriptor entitiesDescriptor = (EntitiesDescriptor) buildXMLObject(qname);
        entitiesDescriptor.setID(expectedID);
        
        entitiesDescriptor.setSignature( buildSignatureSkeleton() );

        QName extensionsQName = new QName(SAMLConstants.SAML20MD_NS, Extensions.LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        entitiesDescriptor.setExtensions((Extensions) buildXMLObject(extensionsQName));
        
        QName entitiesDescriptorQName = new QName(SAMLConstants.SAML20MD_NS, EntitiesDescriptor.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        QName entityDescriptorQName = new QName(SAMLConstants.SAML20MD_NS, EntityDescriptor.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        entitiesDescriptor.getEntitiesDescriptors().add((EntitiesDescriptor) buildXMLObject(entitiesDescriptorQName));
        entitiesDescriptor.getEntityDescriptors().add((EntityDescriptor) buildXMLObject(entityDescriptorQName));
        entitiesDescriptor.getEntitiesDescriptors().add((EntitiesDescriptor) buildXMLObject(entitiesDescriptorQName));
        entitiesDescriptor.getEntityDescriptors().add((EntityDescriptor) buildXMLObject(entityDescriptorQName));
        entitiesDescriptor.getEntitiesDescriptors().add((EntitiesDescriptor) buildXMLObject(entitiesDescriptorQName));
        assertXMLEquals(expectedChildElementsDOM, entitiesDescriptor);
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