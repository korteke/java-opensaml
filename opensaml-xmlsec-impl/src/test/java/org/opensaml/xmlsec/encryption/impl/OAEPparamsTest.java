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
import org.testng.AssertJUnit;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.xmlsec.encryption.OAEPparams;

/**
 *
 */
public class OAEPparamsTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedBase64Content;

    /**
     * Constructor
     *
     */
    public OAEPparamsTest() {
        singleElementFile = "/data/org/opensaml/xmlsec/encryption/impl/OAEPparams.xml";
        
    }

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedBase64Content = "someBase64==";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        OAEPparams oaep = (OAEPparams) unmarshallElement(singleElementFile);
        
        AssertJUnit.assertNotNull("OAEPparams", oaep);
        AssertJUnit.assertEquals("OAEPparams value", oaep.getValue(), expectedBase64Content);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        OAEPparams oaep = (OAEPparams) buildXMLObject(OAEPparams.DEFAULT_ELEMENT_NAME);
        oaep.setValue(expectedBase64Content);
        
        assertXMLEquals(expectedDOM, oaep);
    }

}
