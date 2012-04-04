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
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml2.metadata.ArtifactResolutionService;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.impl.ArtifactResolutionServiceImpl}.
 */
public class ArtifactResolutionServiceTest extends XMLObjectProviderBaseTestCase {
    
    protected String expectedBinding;
    protected String expectedLocation;
    protected String expectedResponseLocation;
    protected Integer expectedIndex;
    protected XSBooleanValue expectedIsDefault;
    
    /**
     * Constructor
     */
    public ArtifactResolutionServiceTest() {
        singleElementFile = "/data/org/opensaml/saml/saml2/metadata/impl/ArtifactResolutionService.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml/saml2/metadata/impl/ArtifactResolutionServiceOptionalAttributes.xml";
    }
    
    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedBinding = "urn:binding:foo";
        expectedLocation = "example.org";
        expectedResponseLocation = "example.org/response";
        expectedIndex = new Integer(3);
        expectedIsDefault = new XSBooleanValue(Boolean.TRUE, false);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        ArtifactResolutionService service = (ArtifactResolutionService) unmarshallElement(singleElementFile);
        
        AssertJUnit.assertEquals("Binding URI was not expected value", expectedBinding, service.getBinding());
        AssertJUnit.assertEquals("Location was not expected value", expectedLocation, service.getLocation());
        AssertJUnit.assertEquals("Index was not expected value", expectedIndex, service.getIndex());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        ArtifactResolutionService service = (ArtifactResolutionService) unmarshallElement(singleElementOptionalAttributesFile);
        
        AssertJUnit.assertEquals("Binding URI was not expected value", expectedBinding, service.getBinding());
        AssertJUnit.assertEquals("Location was not expected value", expectedLocation, service.getLocation());
        AssertJUnit.assertEquals("Index was not expected value", expectedIndex, service.getIndex());
        AssertJUnit.assertEquals("ResponseLocation was not expected value", expectedResponseLocation, service.getResponseLocation());
        AssertJUnit.assertEquals("isDefault was not expected value", expectedIsDefault, service.isDefaultXSBoolean());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        ArtifactResolutionService service = (ArtifactResolutionService) buildXMLObject(ArtifactResolutionService.DEFAULT_ELEMENT_NAME);
        
        service.setBinding(expectedBinding);
        service.setLocation(expectedLocation);
        service.setIndex(expectedIndex);

        assertXMLEquals(expectedDOM, service);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        ArtifactResolutionService service = (ArtifactResolutionService) buildXMLObject(ArtifactResolutionService.DEFAULT_ELEMENT_NAME);
        
        service.setBinding(expectedBinding);
        service.setLocation(expectedLocation);
        service.setIndex(expectedIndex);
        service.setResponseLocation(expectedResponseLocation);
        service.setIsDefault(expectedIsDefault);

        assertXMLEquals(expectedOptionalAttributesDOM, service);
    }
    
    /**
     * Test the proper behavior of the XSBooleanValue attributes.
     */
    @Test
    public void testXSBooleanAttributes() {
        ArtifactResolutionService ars = 
            (ArtifactResolutionService) buildXMLObject(ArtifactResolutionService.DEFAULT_ELEMENT_NAME);
        
        // isDefault attribute
        ars.setIsDefault(Boolean.TRUE);
        AssertJUnit.assertEquals("Unexpected value for boolean attribute found", Boolean.TRUE, ars.isDefault());
        AssertJUnit.assertNotNull("XSBooleanValue was null", ars.isDefaultXSBoolean());
        AssertJUnit.assertEquals("XSBooleanValue was unexpected value", new XSBooleanValue(Boolean.TRUE, false),
                ars.isDefaultXSBoolean());
        AssertJUnit.assertEquals("XSBooleanValue string was unexpected value", "true", ars.isDefaultXSBoolean().toString());
        
        ars.setIsDefault(Boolean.FALSE);
        AssertJUnit.assertEquals("Unexpected value for boolean attribute found", Boolean.FALSE, ars.isDefault());
        AssertJUnit.assertNotNull("XSBooleanValue was null", ars.isDefaultXSBoolean());
        AssertJUnit.assertEquals("XSBooleanValue was unexpected value", new XSBooleanValue(Boolean.FALSE, false),
                ars.isDefaultXSBoolean());
        AssertJUnit.assertEquals("XSBooleanValue string was unexpected value", "false", ars.isDefaultXSBoolean().toString());
        
        ars.setIsDefault((Boolean) null);
        AssertJUnit.assertEquals("Unexpected default value for boolean attribute found", Boolean.FALSE, ars.isDefault());
        AssertJUnit.assertNull("XSBooleanValue was not null", ars.isDefaultXSBoolean());
    }
}