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

package org.opensaml.saml.ext.samlec.impl;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.ext.samlec.GeneratedKey;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


/**
 * Tests {@link GeneratedKeyImpl}
 */
public class GeneratedKeyTest extends XMLObjectProviderBaseTestCase {

    private String expectedValue;

    private String expectedSOAP11Actor;
    
    private Boolean expectedSOAP11MustUnderstand;
    
    /** Constructor */
    public GeneratedKeyTest() {
        singleElementFile = "/org/opensaml/saml/ext/samlec/impl/GeneratedKey.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedValue = "AGeneratedKey";
        expectedSOAP11Actor = "https://soap11actor.example.org";
        expectedSOAP11MustUnderstand = true;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        GeneratedKeyBuilder builder = (GeneratedKeyBuilder) builderFactory.getBuilder(GeneratedKey.DEFAULT_ELEMENT_NAME);

        GeneratedKey key = builder.buildObject();
        key.setSOAP11Actor(expectedSOAP11Actor);
        key.setSOAP11MustUnderstand(expectedSOAP11MustUnderstand);
        key.setValue(expectedValue);

        assertXMLEquals(expectedDOM, key);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        GeneratedKey key = (GeneratedKey) unmarshallElement(singleElementFile);

        Assert.assertNotNull(key);
        Assert.assertEquals(expectedValue, key.getValue());
        Assert.assertEquals(expectedSOAP11MustUnderstand, key.isSOAP11MustUnderstand(),
                "SOAP mustUnderstand had unxpected value");
        Assert.assertEquals(expectedSOAP11Actor, key.getSOAP11Actor(),
                "SOAP actor had unxpected value");
    }
}