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

package org.opensaml.xmlsec.signature.impl;


import org.testng.annotations.Test;
import org.testng.Assert;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.xmlsec.signature.Exponent;
import org.opensaml.xmlsec.signature.Modulus;
import org.opensaml.xmlsec.signature.RSAKeyValue;

/**
 *
 */
public class RSAKeyValueTest extends XMLObjectProviderBaseTestCase {
    
    /**
     * Constructor
     *
     */
    public RSAKeyValueTest() {
        singleElementFile = "/data/org/opensaml/xmlsec/signature/impl/RSAKeyValue.xml";
        childElementsFile = "/data/org/opensaml/xmlsec/signature/impl/RSAKeyValueChildElements.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        RSAKeyValue keyValue = (RSAKeyValue) unmarshallElement(singleElementFile);
        
        Assert.assertNotNull(keyValue, "RSAKeyValue");
        Assert.assertNull(keyValue.getModulus(), "Modulus child element");
        Assert.assertNull(keyValue.getExponent(), "Exponent child element");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        RSAKeyValue keyValue = (RSAKeyValue) unmarshallElement(childElementsFile);
        
        Assert.assertNotNull(keyValue, "RSAKeyValue");
        Assert.assertNotNull(keyValue.getModulus(), "Modulus child element");
        Assert.assertNotNull(keyValue.getExponent(), "Exponent child element");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        RSAKeyValue keyValue = (RSAKeyValue) buildXMLObject(RSAKeyValue.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, keyValue);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        RSAKeyValue keyValue = (RSAKeyValue) buildXMLObject(RSAKeyValue.DEFAULT_ELEMENT_NAME);
        
        keyValue.setModulus((Modulus) buildXMLObject(Modulus.DEFAULT_ELEMENT_NAME));
        keyValue.setExponent((Exponent) buildXMLObject(Exponent.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, keyValue);
    }

}
