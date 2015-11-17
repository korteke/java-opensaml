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

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.EncryptionMethod;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.security.credential.UsageType;
import org.opensaml.xmlsec.signature.KeyInfo;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.impl.KeyDescriptorImpl}.
 */
public class KeyDescriptorTest extends XMLObjectProviderBaseTestCase {

    /** Expected Name attribute value. */
    protected UsageType expectedUse;
    
    /** Expected number of EncrptionMethod children. */
    protected int expectedNumEncMethods;

    /**
     * Constructor.
     */
    public KeyDescriptorTest() {
        singleElementFile = "/data/org/opensaml/saml/saml2/metadata/impl/KeyDescriptor.xml";
        singleElementOptionalAttributesFile = 
                "/data/org/opensaml/saml/saml2/metadata/impl/KeyDescriptorOptionalAttributes.xml";
        childElementsFile = "/data/org/opensaml/saml/saml2/metadata/impl/KeyDescriptorChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedUse = UsageType.ENCRYPTION;
        expectedNumEncMethods = 2;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        KeyDescriptor keyDescriptor = (KeyDescriptor) unmarshallElement(singleElementFile);
        
        Assert.assertNotNull(keyDescriptor, "KeyDescriptor");
        Assert.assertEquals(keyDescriptor.getUse(), UsageType.UNSPECIFIED, "Unexpected use attribute value");

    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        KeyDescriptor keyDescriptor = (KeyDescriptor) unmarshallElement(singleElementOptionalAttributesFile);
        
        Assert.assertNotNull(keyDescriptor, "KeyDescriptor");
        Assert.assertEquals(keyDescriptor.getUse(), expectedUse, "Use attribute");
    }
    
    @Test
    public void testSingleElementBadAttributesUnmarshall() {
        try {
            unmarshallElement("/data/org/opensaml/saml/saml2/metadata/impl/KeyDescriptorBadAttributes1.xml");
            Assert.fail();
        } catch (AssertionError e) {
        }
        try {
            unmarshallElement("/data/org/opensaml/saml/saml2/metadata/impl/KeyDescriptorBadAttributes2.xml");
            Assert.fail();
        } catch (AssertionError e) {
        }
       
    }
    

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        KeyDescriptor keyDescriptor = (KeyDescriptor) unmarshallElement(childElementsFile);

        Assert.assertNotNull(keyDescriptor, "KeyDescriptor");
        Assert.assertNotNull(keyDescriptor.getKeyInfo(), "KeyInfo Child element");
        Assert.assertEquals(keyDescriptor.getEncryptionMethods().size(), expectedNumEncMethods,
                "# of EncryptionMethod child elements");
   }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        KeyDescriptor keyDescriptor = (new KeyDescriptorBuilder()).buildObject();
        keyDescriptor.setUse(null);

        assertXMLEquals(expectedDOM, keyDescriptor);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        QName qname = new QName(SAMLConstants.SAML20MD_NS, KeyDescriptor.DEFAULT_ELEMENT_LOCAL_NAME, 
                SAMLConstants.SAML20MD_PREFIX);
        KeyDescriptor keyDescriptor = (KeyDescriptor) buildXMLObject(qname);

        keyDescriptor.setUse(UsageType.ENCRYPTION);

        assertXMLEquals(expectedOptionalAttributesDOM, keyDescriptor);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        QName qname = new QName(SAMLConstants.SAML20MD_NS, KeyDescriptor.DEFAULT_ELEMENT_LOCAL_NAME, 
                SAMLConstants.SAML20MD_PREFIX);
        KeyDescriptor keyDescriptor = (KeyDescriptor) buildXMLObject(qname);
        
        keyDescriptor.setKeyInfo((KeyInfo) buildXMLObject(KeyInfo.DEFAULT_ELEMENT_NAME));
        keyDescriptor.getEncryptionMethods()
            .add((EncryptionMethod) buildXMLObject(EncryptionMethod.DEFAULT_ELEMENT_NAME));
        keyDescriptor.getEncryptionMethods()
            .add((EncryptionMethod) buildXMLObject(EncryptionMethod.DEFAULT_ELEMENT_NAME));

        assertXMLEquals(expectedChildElementsDOM, keyDescriptor);
    }
}