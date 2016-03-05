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

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.metadata.AssertionIDRequestService;
import org.opensaml.saml.saml2.metadata.AttributeAuthorityDescriptor;
import org.opensaml.saml.saml2.metadata.AttributeService;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.impl.AttributeAuthorityDescriptorImpl}.
 */
public class AttributeAuthorityDescriptorTest extends XMLObjectProviderBaseTestCase {

    /**
     * Constructor
     */
    public AttributeAuthorityDescriptorTest() {
        singleElementFile = "/org/opensaml/saml/saml2/metadata/impl/AttributeAuthorityDescriptor.xml";
        childElementsFile = "/org/opensaml/saml/saml2/metadata/impl/AttributeAuthorityDescriptorChildElements.xml";
    }


    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        AttributeAuthorityDescriptor descriptor = (AttributeAuthorityDescriptor) unmarshallElement(singleElementFile);

        Assert.assertEquals(descriptor.getAttributeServices().size(), 0);
        Assert.assertEquals(descriptor.getAssertionIDRequestServices().size(), 0);
        Assert.assertEquals(descriptor.getNameIDFormats().size(), 0);
        Assert.assertEquals(descriptor.getAttributeProfiles().size(), 0);
        Assert.assertEquals(descriptor.getAttributes().size(), 0);
    }


    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        AttributeAuthorityDescriptor descriptor = (AttributeAuthorityDescriptor) unmarshallElement(childElementsFile);

        Assert.assertEquals(descriptor.getAttributeServices().size(), 1);
        Assert.assertEquals(descriptor.getAssertionIDRequestServices().size(), 2);
        Assert.assertEquals(descriptor.getNameIDFormats().size(), 1);
        Assert.assertEquals(descriptor.getAttributeProfiles().size(), 2);
        Assert.assertEquals(descriptor.getAttributes().size(), 3);

        Assert.assertEquals(descriptor.getEndpoints().size(), 3);
        
        QName qname = new QName(SAMLConstants.SAML20MD_NS, AttributeService.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        Assert.assertEquals(descriptor.getEndpoints(qname).size(), 1);
        qname = new QName(SAMLConstants.SAML20MD_NS, AssertionIDRequestService.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        Assert.assertEquals(descriptor.getEndpoints(qname).size(), 2);
        Assert.assertNull(descriptor.getEndpoints(AttributeAuthorityDescriptor.DEFAULT_ELEMENT_NAME));
}

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        AttributeAuthorityDescriptor descriptor = (new AttributeAuthorityDescriptorBuilder()).buildObject();

        assertXMLEquals(expectedDOM, descriptor);
    }

    @Test
    public void testChildElementsMarshall() {
        AttributeAuthorityDescriptor descriptor = (new AttributeAuthorityDescriptorBuilder()).buildObject();
        int i;
        
        for (i = 0 ; i < 1; i ++) {
            descriptor.getAttributeServices().add((new AttributeServiceBuilder().buildObject()));
        }
        for (i = 0 ; i < 2; i ++) {
            descriptor.getAssertionIDRequestServices().add((new AssertionIDRequestServiceBuilder().buildObject()));
        }
        for (i = 0 ; i < 1; i ++) {
            descriptor.getNameIDFormats().add((new NameIDFormatBuilder().buildObject()));
        }
        for (i = 0 ; i < 2; i ++) {
            descriptor.getAttributeProfiles().add((new AttributeProfileBuilder().buildObject()));
        }
        for (i = 0 ; i < 3; i ++) {
            descriptor.getAttributes().add((Attribute)buildXMLObject(Attribute.DEFAULT_ELEMENT_NAME));
        }

        assertXMLEquals(expectedChildElementsDOM, descriptor);
    }
    
   
}