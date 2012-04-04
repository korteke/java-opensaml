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
        
        Assert.assertEquals(service.getBinding(), expectedBinding, "Binding URI was not expected value");
        Assert.assertEquals(service.getLocation(), expectedLocation, "Location was not expected value");
        Assert.assertEquals(service.getIndex(), expectedIndex, "Index was not expected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        ArtifactResolutionService service = (ArtifactResolutionService) unmarshallElement(singleElementOptionalAttributesFile);
        
        Assert.assertEquals(service.getBinding(), expectedBinding, "Binding URI was not expected value");
        Assert.assertEquals(service.getLocation(), expectedLocation, "Location was not expected value");
        Assert.assertEquals(service.getIndex(), expectedIndex, "Index was not expected value");
        Assert.assertEquals(service.getResponseLocation(), expectedResponseLocation, "ResponseLocation was not expected value");
        Assert.assertEquals(service.isDefaultXSBoolean(), expectedIsDefault, "isDefault was not expected value");
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
        Assert.assertEquals(ars.isDefault(), Boolean.TRUE, "Unexpected value for boolean attribute found");
        Assert.assertNotNull(ars.isDefaultXSBoolean(), "XSBooleanValue was null");
        Assert.assertEquals(ars.isDefaultXSBoolean(), new XSBooleanValue(Boolean.TRUE, false),
                "XSBooleanValue was unexpected value");
        Assert.assertEquals(ars.isDefaultXSBoolean().toString(), "true", "XSBooleanValue string was unexpected value");
        
        ars.setIsDefault(Boolean.FALSE);
        Assert.assertEquals(ars.isDefault(), Boolean.FALSE, "Unexpected value for boolean attribute found");
        Assert.assertNotNull(ars.isDefaultXSBoolean(), "XSBooleanValue was null");
        Assert.assertEquals(ars.isDefaultXSBoolean(), new XSBooleanValue(Boolean.FALSE, false),
                "XSBooleanValue was unexpected value");
        Assert.assertEquals(ars.isDefaultXSBoolean().toString(), "false", "XSBooleanValue string was unexpected value");
        
        ars.setIsDefault((Boolean) null);
        Assert.assertEquals(ars.isDefault(), Boolean.FALSE, "Unexpected default value for boolean attribute found");
        Assert.assertNull(ars.isDefaultXSBoolean(), "XSBooleanValue was not null");
    }
}