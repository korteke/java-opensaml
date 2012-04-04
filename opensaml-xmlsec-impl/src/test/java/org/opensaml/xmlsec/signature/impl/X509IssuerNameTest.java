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
import org.testng.Assert;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.xmlsec.signature.X509IssuerName;

/**
 *
 */
public class X509IssuerNameTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedStringContent;

    /**
     * Constructor
     *
     */
    public X509IssuerNameTest() {
        singleElementFile = "/data/org/opensaml/xmlsec/signature/impl/X509IssuerName.xml";
        
    }

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedStringContent = "someX509IssuerName";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        X509IssuerName x509Element = (X509IssuerName) unmarshallElement(singleElementFile);
        
        Assert.assertNotNull(x509Element, "X509IssuerName");
        Assert.assertEquals(expectedStringContent, x509Element.getValue(), "X509IssuerName value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        X509IssuerName x509Element = (X509IssuerName) buildXMLObject(X509IssuerName.DEFAULT_ELEMENT_NAME);
        x509Element.setValue(expectedStringContent);
        
        assertXMLEquals(expectedDOM, x509Element);
    }

}
