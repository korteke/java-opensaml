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
import org.opensaml.xmlsec.signature.KeyValue;
import org.opensaml.xmlsec.signature.RSAKeyValue;

/**
 *
 */
public class KeyValueTest extends XMLObjectProviderBaseTestCase {
    
    /**
     * Constructor
     *
     */
    public KeyValueTest() {
        singleElementFile = "/data/org/opensaml/xmlsec/signature/impl/KeyValue.xml";
        childElementsFile = "/data/org/opensaml/xmlsec/signature/impl/KeyValueChildElements.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        KeyValue keyValue = (KeyValue) unmarshallElement(singleElementFile);
        
        Assert.assertNotNull(keyValue, "KeyValue");
        Assert.assertNull(keyValue.getRSAKeyValue(), "RSAKeyValue child element");
        Assert.assertNull(keyValue.getDSAKeyValue(), "DSAKeyValue child element");
        Assert.assertNull(keyValue.getUnknownXMLObject(), "Wildcard child element");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        KeyValue keyValue = (KeyValue) unmarshallElement(childElementsFile);
        
        Assert.assertNotNull(keyValue, "KeyValue");
        Assert.assertNotNull(keyValue.getRSAKeyValue(), "RSAKeyValue child element");
        Assert.assertNull(keyValue.getDSAKeyValue(), "DSAKeyValue child element");
        Assert.assertNull(keyValue.getUnknownXMLObject(), "Wildcard child element");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        KeyValue keyValue = (KeyValue) buildXMLObject(KeyValue.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, keyValue);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        KeyValue keyValue = (KeyValue) buildXMLObject(KeyValue.DEFAULT_ELEMENT_NAME);
        
        keyValue.setRSAKeyValue((RSAKeyValue) buildXMLObject(RSAKeyValue.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, keyValue);
    }

}
