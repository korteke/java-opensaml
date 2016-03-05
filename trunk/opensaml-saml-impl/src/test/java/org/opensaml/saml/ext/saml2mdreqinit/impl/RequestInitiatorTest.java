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

/**
 * 
 */

package org.opensaml.saml.ext.saml2mdreqinit.impl;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.ext.saml2mdreqinit.RequestInitiator;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.saml.ext.saml2mdreqinit.RequestInitiator}.
 */
public class RequestInitiatorTest extends XMLObjectProviderBaseTestCase {

    protected String expectedBinding;

    protected String expectedLocation;

    protected String expectedResponseLocation;

    /**
     * Constructor
     */
    public RequestInitiatorTest() {
        singleElementFile = "/org/opensaml/saml/ext/saml2mdreqinit/impl/RequestInitiator.xml";
    }

    @BeforeMethod protected void setUp() throws Exception {
        expectedBinding = RequestInitiator.REQUIRED_BINDING_VALUE;
        expectedLocation = "https://example.org/reqinit";
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnmarshall() {
        RequestInitiator reqinit = (RequestInitiator) unmarshallElement(singleElementFile);

        Assert.assertEquals(reqinit.getBinding(), expectedBinding, "Binding URI was not expected value");
        Assert.assertEquals(reqinit.getLocation(), expectedLocation, "Location was not expected value");
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementMarshall() {
        RequestInitiator reqinit = (new RequestInitiatorBuilder()).buildObject();

        reqinit.setBinding(expectedBinding);
        reqinit.setLocation(expectedLocation);

        assertXMLEquals(expectedDOM, reqinit);
    }

}