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

package org.opensaml.saml.saml2.metadata.impl;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml2.metadata.AttributeConsumingService;
import org.opensaml.saml.saml2.metadata.RequestedAttribute;
import org.opensaml.saml.saml2.metadata.ServiceDescription;
import org.opensaml.saml.saml2.metadata.ServiceName;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.impl.AssertionConsumerServiceImpl}.
 */
public class AttributeConsumingServiceTest extends XMLObjectProviderBaseTestCase {
    
    protected int expectedIndex;
    protected XSBooleanValue expectedIsDefault;
    protected int expectedServiceNameCount;
    protected int expectedServiceDecsriptionCount;
    protected int expectedRequestedAttributeCount;
    
    /**
     * Constructor
     */
    public AttributeConsumingServiceTest() {
        singleElementFile = "/data/org/opensaml/saml/saml2/metadata/impl/AttributeConsumingService.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml/saml2/metadata/impl/AttributeConsumingServiceOptionalAttributes.xml";
        childElementsFile = "/data/org/opensaml/saml/saml2/metadata/impl/AttributeConsumingServiceChildElements.xml";
    }
    
    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedIndex = 1;
        expectedIsDefault = new XSBooleanValue(Boolean.TRUE, false);
        expectedServiceNameCount = 2;
        expectedServiceDecsriptionCount = 3;
        expectedRequestedAttributeCount = 1;
        
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        AttributeConsumingService service = (AttributeConsumingService) unmarshallElement(singleElementFile);
        
        Assert.assertEquals(service.getIndex(), expectedIndex, "Index was not expected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        AttributeConsumingService service = (AttributeConsumingService) unmarshallElement(singleElementOptionalAttributesFile);
        
        Assert.assertEquals(service.getIndex(), expectedIndex, "Index was not expected value");
        Assert.assertEquals(service.isDefaultXSBoolean(), expectedIsDefault, "isDefault was not expected value");
    }
    
    /** {@inheritDoc} */

    @Test
    public void testChildElementsUnmarshall(){
        AttributeConsumingService service = (AttributeConsumingService) unmarshallElement(childElementsFile);
        
        Assert.assertEquals(service.getNames().size(), expectedServiceNameCount, "<ServiceName> count");
        Assert.assertEquals(service.getDescriptions().size(), expectedServiceDecsriptionCount, "<ServiceDescription> count");
        Assert.assertEquals(service.getRequestAttributes().size(), expectedRequestedAttributeCount, "<ReqestAttribute> count");
       
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        AttributeConsumingService service = (new AttributeConsumingServiceBuilder()).buildObject();
        
        service.setIndex(expectedIndex);

        assertXMLEquals(expectedDOM, service);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        AttributeConsumingService service = (AttributeConsumingService) buildXMLObject(AttributeConsumingService.DEFAULT_ELEMENT_NAME);
        
        service.setIndex(expectedIndex);
        service.setIsDefault(expectedIsDefault);

        assertXMLEquals(expectedOptionalAttributesDOM, service);
    }
    

    @Test
    public void testChildElementsMarshall()
    {
        AttributeConsumingService service = (AttributeConsumingService) buildXMLObject(AttributeConsumingService.DEFAULT_ELEMENT_NAME);
        
        service.setIndex(expectedIndex);
        
        for (int i = 0; i < expectedServiceNameCount; i++) {
            service.getNames().add((ServiceName) buildXMLObject(ServiceName.DEFAULT_ELEMENT_NAME));
        }

        for (int i = 0; i < expectedServiceDecsriptionCount; i++) {
            service.getDescriptions().add((ServiceDescription) buildXMLObject(ServiceDescription.DEFAULT_ELEMENT_NAME));
        }

        service.getRequestAttributes().add((RequestedAttribute) buildXMLObject(RequestedAttribute.DEFAULT_ELEMENT_NAME));

        assertXMLEquals(expectedChildElementsDOM, service);
    
    }
    
    /**
     * Test the proper behavior of the XSBooleanValue attributes.
     */
    @Test
    public void testXSBooleanAttributes() {
        AttributeConsumingService acs = 
            (AttributeConsumingService) buildXMLObject(AttributeConsumingService.DEFAULT_ELEMENT_NAME);
        
        // isDefault attribute
        acs.setIsDefault(Boolean.TRUE);
        Assert.assertEquals(acs.isDefault(), Boolean.TRUE, "Unexpected value for boolean attribute found");
        Assert.assertNotNull(acs.isDefaultXSBoolean(), "XSBooleanValue was null");
        Assert.assertEquals(acs.isDefaultXSBoolean(), new XSBooleanValue(Boolean.TRUE, false),
                "XSBooleanValue was unexpected value");
        Assert.assertEquals(acs.isDefaultXSBoolean().toString(), "true", "XSBooleanValue string was unexpected value");
        
        acs.setIsDefault(Boolean.FALSE);
        Assert.assertEquals(acs.isDefault(), Boolean.FALSE, "Unexpected value for boolean attribute found");
        Assert.assertNotNull(acs.isDefaultXSBoolean(), "XSBooleanValue was null");
        Assert.assertEquals(acs.isDefaultXSBoolean(), new XSBooleanValue(Boolean.FALSE, false),
                "XSBooleanValue was unexpected value");
        Assert.assertEquals(acs.isDefaultXSBoolean().toString(), "false", "XSBooleanValue string was unexpected value");
        
        acs.setIsDefault((Boolean) null);
        Assert.assertEquals(acs.isDefault(), Boolean.FALSE, "Unexpected default value for boolean attribute found");
        Assert.assertNull(acs.isDefaultXSBoolean(), "XSBooleanValue was not null");
    }
}
