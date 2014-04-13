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

package org.opensaml.saml.saml2.core.impl;

import org.testng.annotations.Test;
import org.testng.Assert;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml2.core.EncryptedID;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptedKey;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.saml.saml2.core.impl.EncryptedIDImpl}.
 */
public class EncryptedIDTest extends XMLObjectProviderBaseTestCase {

    /** Count of EncryptedKey subelements. */
    private int encryptedKeyCount = 3;

    /** Constructor. */
    public EncryptedIDTest() {
        singleElementFile = "/data/org/opensaml/saml/saml2/core/impl/EncryptedID.xml";
        childElementsFile = "/data/org/opensaml/saml/saml2/core/impl/EncryptedIDChildElements.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        EncryptedID encElement = (EncryptedID) unmarshallElement(singleElementFile);

        Assert.assertNotNull(encElement);
        Assert.assertNull(encElement.getEncryptedData(), "EncryptedData child element");
        Assert.assertEquals(encElement.getEncryptedKeys().size(), 0, "# of EncryptedKey children");
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        EncryptedID encElement = (EncryptedID) unmarshallElement(childElementsFile);
        
        Assert.assertNotNull(encElement, "EncryptedID was null");
        Assert.assertNotNull(encElement.getEncryptedData(), "EncryptedData child element");
        Assert.assertEquals(encElement.getEncryptedKeys().size(), encryptedKeyCount, "# of EncryptedKey children");

    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        EncryptedID encElement = (EncryptedID) buildXMLObject(EncryptedID.DEFAULT_ELEMENT_NAME);

        assertXMLEquals(expectedDOM, encElement);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        EncryptedID encElement = (EncryptedID) buildXMLObject(EncryptedID.DEFAULT_ELEMENT_NAME);
        
        encElement.setEncryptedData((EncryptedData) buildXMLObject(EncryptedData.DEFAULT_ELEMENT_NAME));
        for (int i=0; i < encryptedKeyCount; i++) {
            encElement.getEncryptedKeys().add((EncryptedKey) buildXMLObject(EncryptedKey.DEFAULT_ELEMENT_NAME));
        }
        
        assertXMLEquals(expectedChildElementsDOM, encElement);
    }
}