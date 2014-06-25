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

package org.opensaml.saml.ext.saml2cb.impl;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.ext.saml2cb.ChannelBindings;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling {@link ChannelBindings}.
 */
public class ChannelBindingsTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedContent;
    
    private String expectedSOAP11Actor;
    
    private Boolean expectedSOAP11MustUnderstand;
    
    public ChannelBindingsTest() {
        super();
        singleElementFile = "/data/org/opensaml/saml/ext/saml2cb/impl/ChannelBindings.xml";
    }
 
    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedContent = "YourChannelIsBound";
        expectedSOAP11Actor = "https://soap11actor.example.org";
        expectedSOAP11MustUnderstand = true;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        ChannelBindings cb = (ChannelBindings) unmarshallElement(singleElementFile);
        
        Assert.assertNotNull(cb);
        
        Assert.assertEquals(expectedSOAP11MustUnderstand, cb.isSOAP11MustUnderstand(),
                "SOAP mustUnderstand had unxpected value");
        Assert.assertEquals(expectedSOAP11Actor, cb.getSOAP11Actor(), "SOAP actor had unxpected value");
        Assert.assertEquals(expectedContent, cb.getValue(), "Element content had unexpected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        ChannelBindings cb = (ChannelBindings) buildXMLObject(ChannelBindings.DEFAULT_ELEMENT_NAME);
        
        cb.setSOAP11Actor(expectedSOAP11Actor);
        cb.setSOAP11MustUnderstand(expectedSOAP11MustUnderstand);
        cb.setValue(expectedContent);
        
        assertXMLEquals(expectedDOM, cb);
    }

}