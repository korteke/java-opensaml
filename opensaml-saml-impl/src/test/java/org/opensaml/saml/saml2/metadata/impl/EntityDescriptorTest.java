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
import org.opensaml.saml.saml2.metadata.AdditionalMetadataLocation;
import org.opensaml.saml.saml2.metadata.AffiliationDescriptor;
import org.opensaml.saml.saml2.metadata.AttributeAuthorityDescriptor;
import org.opensaml.saml.saml2.metadata.AuthnAuthorityDescriptor;
import org.opensaml.saml.saml2.metadata.ContactPerson;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.Organization;
import org.opensaml.saml.saml2.metadata.PDPDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureConstants;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.saml.saml2.metadata.impl.EntityDescriptorImpl}.
 */
public class EntityDescriptorTest extends XMLObjectProviderBaseTestCase {

    /** Expected entityID value */
    protected String expectedEntityID;

    /** Expected ID value */
    protected String expectedID;

    /** Expected cacheDuration value in miliseconds */
    protected long expectedCacheDuration;

    /** Expected validUntil value */
    protected DateTime expectedValidUntil;

    /**
     * Constructor
     */
    public EntityDescriptorTest() {
        singleElementFile = "/data/org/opensaml/saml/saml2/metadata/impl/EntityDescriptor.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml/saml2/metadata/impl/EntityDescriptorOptionalAttributes.xml";
        childElementsFile = "/data/org/opensaml/saml/saml2/metadata/impl/EntityDescriptorChildElements.xml";
    }

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedID = "id";
        expectedEntityID = "99ff33";
        expectedCacheDuration = 90000;
        expectedValidUntil = new DateTime(2005, 12, 7, 10, 21, 0, 0, ISOChronology.getInstanceUTC());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        EntityDescriptor descriptor = (EntityDescriptor) unmarshallElement(singleElementFile);

        String entityID = descriptor.getEntityID();
        Assert.assertEquals(entityID,
                expectedEntityID, "entityID attribute has a value of " + entityID + ", expected a value of " + expectedEntityID);

        Long duration = descriptor.getCacheDuration();
        Assert.assertNull(duration, "cacheDuration attribute has a value of " + duration + ", expected no value");

        DateTime validUntil = descriptor.getValidUntil();
        Assert.assertNull(validUntil, "validUntil attribute has a value of " + validUntil + ", expected no value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        EntityDescriptor descriptor = (EntityDescriptor) unmarshallElement(singleElementOptionalAttributesFile);

        String entityID = descriptor.getEntityID();
        Assert.assertEquals(entityID,
                expectedEntityID, "entityID attribute has a value of " + entityID + ", expected a value of " + expectedEntityID);

        String id = descriptor.getID();
        Assert.assertEquals(id, expectedID, "ID attribute has a value of " + id + ", expected a value of " + expectedID);

        long duration = descriptor.getCacheDuration().longValue();
        Assert.assertEquals(duration, expectedCacheDuration, "cacheDuration attribute has a value of " + duration + ", expected a value of "
                        + expectedCacheDuration);

        DateTime validUntil = descriptor.getValidUntil();
        Assert.assertEquals(expectedValidUntil
                .compareTo(validUntil), 0, "validUntil attribute value did not match expected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        EntityDescriptor descriptor = (EntityDescriptor) unmarshallElement(childElementsFile);

        Assert.assertNotNull(descriptor.getExtensions(), "Extensions child");
        Assert.assertNotNull(descriptor.getSignature(), "Signature child");
        Assert.assertEquals(descriptor.getRoleDescriptors(IDPSSODescriptor.DEFAULT_ELEMENT_NAME).size(), 2, "IDPSSODescriptor count");
        Assert.assertEquals(descriptor.getRoleDescriptors(SPSSODescriptor.DEFAULT_ELEMENT_NAME).size(), 3, "SPSSODescriptor count");
        Assert.assertEquals(descriptor.getRoleDescriptors(AuthnAuthorityDescriptor.DEFAULT_ELEMENT_NAME).size(), 2, "AuthnAuthorityDescriptor count");
        Assert.assertEquals(descriptor.getRoleDescriptors(AttributeAuthorityDescriptor.DEFAULT_ELEMENT_NAME).size(), 1, "AttributeAuthorityDescriptor count");
        Assert.assertEquals(descriptor.getRoleDescriptors(PDPDescriptor.DEFAULT_ELEMENT_NAME).size(), 2, "PDPDescriptor count");
        Assert.assertNotNull(descriptor.getAffiliationDescriptor(), "AffiliationDescriptor ");
        Assert.assertNotNull(descriptor.getOrganization(), "Organization ");
        Assert.assertEquals(descriptor.getContactPersons().size(), 1, "ContactPerson count");
        Assert.assertEquals(descriptor.getAdditionalMetadataLocations().size(), 3, "AdditionalMetadataLocation count");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        QName qname = new QName(SAMLConstants.SAML20MD_NS, EntityDescriptor.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        EntityDescriptor descriptor = (EntityDescriptor) buildXMLObject(qname);

        descriptor.setEntityID(expectedEntityID);

        assertXMLEquals(expectedDOM, descriptor);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        QName qname = new QName(SAMLConstants.SAML20MD_NS, EntityDescriptor.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        EntityDescriptor descriptor = (EntityDescriptor) buildXMLObject(qname);

        descriptor.setEntityID(expectedEntityID);
        descriptor.setID(expectedID);
        descriptor.setValidUntil(expectedValidUntil);
        descriptor.setCacheDuration(expectedCacheDuration);

        assertXMLEquals(expectedOptionalAttributesDOM, descriptor);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        QName qname = new QName(SAMLConstants.SAML20MD_NS, EntityDescriptor.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        EntityDescriptor descriptor = (EntityDescriptor) buildXMLObject(qname);
        descriptor.setID(expectedID);
        descriptor.setEntityID(expectedEntityID);

        QName extensionsQName = new QName(SAMLConstants.SAML20MD_NS, Extensions.LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        descriptor.setExtensions((Extensions) buildXMLObject(extensionsQName));
        
        descriptor.setSignature( buildSignatureSkeleton() );
        
        QName idpSSOQName = new QName(SAMLConstants.SAML20MD_NS, IDPSSODescriptor.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        QName spSSOQName = new QName(SAMLConstants.SAML20MD_NS, SPSSODescriptor.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        QName authnAuthQName = new QName(SAMLConstants.SAML20MD_NS, AuthnAuthorityDescriptor.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        QName pdpQName = new QName(SAMLConstants.SAML20MD_NS, PDPDescriptor.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        QName affilQName = new QName(SAMLConstants.SAML20MD_NS, AffiliationDescriptor.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        descriptor.getRoleDescriptors(IDPSSODescriptor.DEFAULT_ELEMENT_NAME).add((IDPSSODescriptor) buildXMLObject(idpSSOQName));
        descriptor.getRoleDescriptors(SPSSODescriptor.DEFAULT_ELEMENT_NAME).add((SPSSODescriptor) buildXMLObject(spSSOQName));
        descriptor.getRoleDescriptors(SPSSODescriptor.DEFAULT_ELEMENT_NAME).add((SPSSODescriptor) buildXMLObject(spSSOQName));
        descriptor.getRoleDescriptors(AuthnAuthorityDescriptor.DEFAULT_ELEMENT_NAME).add((AuthnAuthorityDescriptor) buildXMLObject(authnAuthQName));
        descriptor.getRoleDescriptors(PDPDescriptor.DEFAULT_ELEMENT_NAME).add((PDPDescriptor) buildXMLObject(pdpQName));
        descriptor.getRoleDescriptors(IDPSSODescriptor.DEFAULT_ELEMENT_NAME).add((IDPSSODescriptor) buildXMLObject(idpSSOQName));
        descriptor.getRoleDescriptors(AttributeAuthorityDescriptor.DEFAULT_ELEMENT_NAME).add((AttributeAuthorityDescriptor) buildXMLObject(AttributeAuthorityDescriptor.DEFAULT_ELEMENT_NAME));
        descriptor.getRoleDescriptors(SPSSODescriptor.DEFAULT_ELEMENT_NAME).add((SPSSODescriptor) buildXMLObject(spSSOQName));
        descriptor.getRoleDescriptors(AuthnAuthorityDescriptor.DEFAULT_ELEMENT_NAME).add((AuthnAuthorityDescriptor) buildXMLObject(authnAuthQName));
        descriptor.getRoleDescriptors(PDPDescriptor.DEFAULT_ELEMENT_NAME).add((PDPDescriptor) buildXMLObject(pdpQName));
        descriptor.setAffiliationDescriptor((AffiliationDescriptor) buildXMLObject(affilQName));
        
        QName orgQName = new QName(SAMLConstants.SAML20MD_NS, Organization.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        descriptor.setOrganization((Organization) buildXMLObject(orgQName));
        
        QName contactQName = new QName(SAMLConstants.SAML20MD_NS, ContactPerson.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        descriptor.getContactPersons().add((ContactPerson) buildXMLObject(contactQName));
        
        QName addMDQName = new QName(SAMLConstants.SAML20MD_NS, AdditionalMetadataLocation.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < 3; i++) {
            descriptor.getAdditionalMetadataLocations().add((AdditionalMetadataLocation) buildXMLObject(addMDQName));
        }

        assertXMLEquals(expectedChildElementsDOM, descriptor);
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