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
import org.testng.Assert;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.xmlsec.encryption.CipherData;
import org.opensaml.xmlsec.encryption.CipherReference;
import org.opensaml.xmlsec.encryption.CipherValue;

/**
 *
 */
public class CipherDataTest extends XMLObjectProviderBaseTestCase {
    
    /**
     * Constructor
     *
     */
    public CipherDataTest() {
        singleElementFile = "/data/org/opensaml/xmlsec/encryption/impl/CipherData.xml";
        childElementsFile = "/data/org/opensaml/xmlsec/encryption/impl/CipherDataChildElements.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        CipherData cipherData = (CipherData) unmarshallElement(singleElementFile);
        
        Assert.assertNotNull(cipherData, "CipherData");
        Assert.assertNull(cipherData.getCipherValue(), "CipherValue child element");
        Assert.assertNull(cipherData.getCipherReference(), "CipherReference child element");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        CipherData cipherData = (CipherData) unmarshallElement(childElementsFile);
        
        Assert.assertNotNull(cipherData, "CipherData");
        Assert.assertNotNull(cipherData.getCipherValue(), "CipherValue child element");
        Assert.assertNotNull(cipherData.getCipherReference(), "CipherReference child element");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        CipherData cipherData = (CipherData) buildXMLObject(CipherData.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, cipherData);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        CipherData cipherData = (CipherData) buildXMLObject(CipherData.DEFAULT_ELEMENT_NAME);
        
        cipherData.setCipherValue((CipherValue) buildXMLObject(CipherValue.DEFAULT_ELEMENT_NAME));
        cipherData.setCipherReference((CipherReference) buildXMLObject(CipherReference.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, cipherData);
    }

}
