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


import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.xmlsec.signature.NamedCurve;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class NamedCurveTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedURI;
    
    /**
     * Constructor.
     *
     */
    public NamedCurveTest() {
        singleElementFile = "/data/org/opensaml/xmlsec/signature/impl/NamedCurve.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedURI = "urn:oid:1.1.1.1";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        NamedCurve nc = (NamedCurve) unmarshallElement(singleElementFile);
        
        Assert.assertNotNull(nc, "NamedCurve");
        Assert.assertEquals(expectedURI, nc.getURI(), "URI attribute");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        NamedCurve nc = (NamedCurve) buildXMLObject(NamedCurve.DEFAULT_ELEMENT_NAME);
        
        nc.setURI(expectedURI);
        
        assertXMLEquals(expectedDOM, nc);
    }
    
}