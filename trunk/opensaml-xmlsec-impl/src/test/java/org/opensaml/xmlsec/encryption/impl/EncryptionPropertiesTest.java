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

package org.opensaml.xmlsec.encryption.impl;


import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.xmlsec.encryption.EncryptionProperties;
import org.opensaml.xmlsec.encryption.EncryptionProperty;

/**
 *
 */
public class EncryptionPropertiesTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedID;
    private int expectedNumEncProps;
    
    /**
     * Constructor
     *
     */
    public EncryptionPropertiesTest() {
        singleElementFile = "/data/org/opensaml/xmlsec/encryption/impl/EncryptionProperties.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/xmlsec/encryption/impl/EncryptionPropertiesOptionalAttributes.xml";
        childElementsFile = "/data/org/opensaml/xmlsec/encryption/impl/EncryptionPropertiesChildElements.xml";
    }

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedID = "someID";
        expectedNumEncProps = 3;
        
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        EncryptionProperties ep = (EncryptionProperties) unmarshallElement(singleElementFile);
        
        Assert.assertNotNull(ep, "EncryptionProperties");
        Assert.assertNull(ep.getID(), "Id attribute");
        Assert.assertEquals(ep.getEncryptionProperties().size(), 0, "# of EncryptionProperty children");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        EncryptionProperties ep = (EncryptionProperties) unmarshallElement(singleElementOptionalAttributesFile);
        
        Assert.assertNotNull(ep, "EncryptionProperties");
        Assert.assertEquals(ep.getID(), expectedID, "Id attribute");
        Assert.assertEquals(ep.getEncryptionProperties().size(), 0, "# of EncryptionProperty children");
        
        Assert.assertEquals(ep.resolveID(expectedID), ep, "ID lookup failed");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        EncryptionProperties ep = (EncryptionProperties) unmarshallElement(childElementsFile);
        
        Assert.assertNotNull(ep, "EncryptionProperties");
        Assert.assertNull(ep.getID(), "Id attribute");
        Assert.assertEquals(ep.getEncryptionProperties().size(), expectedNumEncProps, "# of EncryptionProperty children");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        EncryptionProperties ep = (EncryptionProperties) buildXMLObject(EncryptionProperties.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, ep);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        EncryptionProperties ep = (EncryptionProperties) buildXMLObject(EncryptionProperties.DEFAULT_ELEMENT_NAME);
        
        ep.setID(expectedID);
        
        assertXMLEquals(expectedOptionalAttributesDOM, ep);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        EncryptionProperties ep = (EncryptionProperties) buildXMLObject(EncryptionProperties.DEFAULT_ELEMENT_NAME);
        
        ep.getEncryptionProperties().add((EncryptionProperty) buildXMLObject(EncryptionProperty.DEFAULT_ELEMENT_NAME));
        ep.getEncryptionProperties().add((EncryptionProperty) buildXMLObject(EncryptionProperty.DEFAULT_ELEMENT_NAME));
        ep.getEncryptionProperties().add((EncryptionProperty) buildXMLObject(EncryptionProperty.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, ep);
    }

}
