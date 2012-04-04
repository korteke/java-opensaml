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
import org.testng.annotations.BeforeMethod;
import org.testng.AssertJUnit;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.xmlsec.signature.X509IssuerName;
import org.opensaml.xmlsec.signature.X509IssuerSerial;
import org.opensaml.xmlsec.signature.X509SerialNumber;

/**
 *
 */
public class X509IssuerSerialTest extends XMLObjectProviderBaseTestCase {
    
    /**
     * Constructor
     *
     */
    public X509IssuerSerialTest() {
        singleElementFile = "/data/org/opensaml/xmlsec/signature/impl/X509IssuerSerial.xml";
        childElementsFile = "/data/org/opensaml/xmlsec/signature/impl/X509IssuerSerialChildElements.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        X509IssuerSerial x509Element = (X509IssuerSerial) unmarshallElement(singleElementFile);
        
        AssertJUnit.assertNotNull("X509IssuerSerial", x509Element);
        AssertJUnit.assertNull("X509IssuerName child element", x509Element.getX509IssuerName());
        AssertJUnit.assertNull("X509SerialNumber child element", x509Element.getX509SerialNumber());
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        X509IssuerSerial x509Element = (X509IssuerSerial) unmarshallElement(childElementsFile);
        
        AssertJUnit.assertNotNull("X509IssuerSerial", x509Element);
        AssertJUnit.assertNotNull("X509IssuerName child element", x509Element.getX509IssuerName());
        AssertJUnit.assertNotNull("X509SerialNumber child element", x509Element.getX509SerialNumber());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        X509IssuerSerial x509Element = (X509IssuerSerial) buildXMLObject(X509IssuerSerial.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, x509Element);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        X509IssuerSerial x509Element = (X509IssuerSerial) buildXMLObject(X509IssuerSerial.DEFAULT_ELEMENT_NAME);
        
        x509Element.setX509IssuerName((X509IssuerName) buildXMLObject(X509IssuerName.DEFAULT_ELEMENT_NAME));
        x509Element.setX509SerialNumber((X509SerialNumber) buildXMLObject(X509SerialNumber.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, x509Element);
    }

}
