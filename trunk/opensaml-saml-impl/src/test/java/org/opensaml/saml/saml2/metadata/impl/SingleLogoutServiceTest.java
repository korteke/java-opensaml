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
package org.opensaml.saml.saml2.metadata.impl;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.AssertJUnit;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml2.metadata.SingleLogoutService;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.impl.SingleLogoutServiceImpl}.
 */
public class SingleLogoutServiceTest extends XMLObjectProviderBaseTestCase {
    
    protected String expectedBinding;
    protected String expectedLocation;
    protected String expectedResponseLocation;
    
    /**
     * Constructor
     */
    public SingleLogoutServiceTest() {
        singleElementFile = "/data/org/opensaml/saml/saml2/metadata/impl/SingleLogoutService.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml/saml2/metadata/impl/SingleLogoutServiceOptionalAttributes.xml";
    }
    
    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedBinding = "urn:binding:foo";
        expectedLocation = "example.org";
        expectedResponseLocation = "example.org/response";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        SingleLogoutService service = (SingleLogoutService) unmarshallElement(singleElementFile);
        
        AssertJUnit.assertEquals("Binding URI was not expected value", expectedBinding, service.getBinding());
        AssertJUnit.assertEquals("Location was not expected value", expectedLocation, service.getLocation());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        SingleLogoutService service = (SingleLogoutService) unmarshallElement(singleElementOptionalAttributesFile);
        
        AssertJUnit.assertEquals("Binding URI was not expected value", expectedBinding, service.getBinding());
        AssertJUnit.assertEquals("Location was not expected value", expectedLocation, service.getLocation());
        AssertJUnit.assertEquals("ResponseLocation was not expected value", expectedResponseLocation, service.getResponseLocation());;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        SingleLogoutService service = (SingleLogoutService) buildXMLObject(SingleLogoutService.DEFAULT_ELEMENT_NAME);
        
        service.setBinding(expectedBinding);
        service.setLocation(expectedLocation);

        assertXMLEquals(expectedDOM, service);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        SingleLogoutService service = (SingleLogoutService) buildXMLObject(SingleLogoutService.DEFAULT_ELEMENT_NAME);
        
        service.setBinding(expectedBinding);
        service.setLocation(expectedLocation);
        service.setResponseLocation(expectedResponseLocation);

        assertXMLEquals(expectedOptionalAttributesDOM, service);
    }
}