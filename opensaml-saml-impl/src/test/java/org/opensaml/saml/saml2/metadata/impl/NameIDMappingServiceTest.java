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
import org.testng.Assert;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml2.metadata.NameIDMappingService;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.impl.NameIDMappingServiceImpl}.
 */
public class NameIDMappingServiceTest extends XMLObjectProviderBaseTestCase {
    
    protected String expectedBinding;
    protected String expectedLocation;
    protected String expectedResponseLocation;
    
    /**
     * Constructor
     */
    public NameIDMappingServiceTest() {
        singleElementFile = "/org/opensaml/saml/saml2/metadata/impl/NameIDMappingService.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml2/metadata/impl/NameIDMappingServiceOptionalAttributes.xml";
    }
    
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedBinding = "urn:binding:foo";
        expectedLocation = "example.org";
        expectedResponseLocation = "example.org/response";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        NameIDMappingService service = (NameIDMappingService) unmarshallElement(singleElementFile);
        
        Assert.assertEquals(service.getBinding(), expectedBinding, "Binding URI was not expected value");
        Assert.assertEquals(service.getLocation(), expectedLocation, "Location was not expected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        NameIDMappingService service = (NameIDMappingService) unmarshallElement(singleElementOptionalAttributesFile);
        
        Assert.assertEquals(service.getBinding(), expectedBinding, "Binding URI was not expected value");
        Assert.assertEquals(service.getLocation(), expectedLocation, "Location was not expected value");
        Assert.assertEquals(service.getResponseLocation(), expectedResponseLocation, "ResponseLocation was not expected value");;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        NameIDMappingService service = (new NameIDMappingServiceBuilder()).buildObject();
        
        service.setBinding(expectedBinding);
        service.setLocation(expectedLocation);

        assertXMLEquals(expectedDOM, service);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        NameIDMappingService service = (NameIDMappingService) buildXMLObject(NameIDMappingService.DEFAULT_ELEMENT_NAME);
        
        service.setBinding(expectedBinding);
        service.setLocation(expectedLocation);
        service.setResponseLocation(expectedResponseLocation);

        assertXMLEquals(expectedOptionalAttributesDOM, service);
    }
}