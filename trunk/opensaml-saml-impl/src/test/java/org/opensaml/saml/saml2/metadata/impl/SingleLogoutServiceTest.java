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

import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.opensaml.core.xml.Namespace;
import org.opensaml.core.xml.NamespaceManager;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.schema.impl.XSAnyBuilder;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.core.xml.util.IDIndex;
import org.opensaml.saml.common.AbstractSAMLObject;
import org.opensaml.saml.saml2.metadata.SingleLogoutService;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.impl.SingleLogoutServiceImpl}.
 */
public class SingleLogoutServiceTest extends XMLObjectProviderBaseTestCase {
    
    protected String expectedBinding;
    protected String expectedLocation;
    protected String expectedResponseLocation;
    
    /** Unknown Attributes */
    protected QName[] unknownAttributeNames = { new QName("urn:foo:bar", "bar", "foo"), new  QName("flibble") };
    /** Unknown Attribute Values */
    protected String[] unknownAttributeValues = {"fred", "flobble"};

    /**
     * Constructor
     */
    public SingleLogoutServiceTest() {
        singleElementFile = "/data/org/opensaml/saml/saml2/metadata/impl/SingleLogoutService.xml";
        childElementsFile = "/data/org/opensaml/saml/saml2/metadata/impl/SingleLogoutServiceChildElements.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml/saml2/metadata/impl/SingleLogoutServiceOptionalAttributes.xml";
        singleElementUnknownAttributesFile = "/data/org/opensaml/saml/saml2/metadata/impl/SingleLogoutServiceUnknownAttributes.xml";
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
        
        Assert.assertEquals(service.getBinding(), expectedBinding, "Binding URI was not expected value");
        Assert.assertEquals(service.getLocation(), expectedLocation, "Location was not expected value");
    }
    
    /** {@inheritDoc} */
    @Test public void testSingleElementUnknownAttributesUnmarshall() {
        SingleLogoutService service = (SingleLogoutService) unmarshallElement(singleElementUnknownAttributesFile);
        AttributeMap attributes = service.getUnknownAttributes();

        Assert.assertEquals(attributes.entrySet().size(), unknownAttributeNames.length);
        for (int i = 0; i < unknownAttributeNames.length; i++) {
            Assert.assertEquals(attributes.get(unknownAttributeNames[i]), unknownAttributeValues[i]);
        }
    }


    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        SingleLogoutService service = (SingleLogoutService) unmarshallElement(singleElementOptionalAttributesFile);
        
        Assert.assertEquals(service.getBinding(), expectedBinding, "Binding URI was not expected value");
        Assert.assertEquals(service.getLocation(), expectedLocation, "Location was not expected value");
        Assert.assertEquals(service.getResponseLocation(), expectedResponseLocation, "ResponseLocation was not expected value");;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        SingleLogoutService service = (SingleLogoutService) buildXMLObject(SingleLogoutService.DEFAULT_ELEMENT_NAME);
        
        service.setBinding(expectedBinding);
        service.setLocation(expectedLocation);

        assertXMLEquals(expectedDOM, service);
    }
    
    @Test
    public void testSingleElementUnknownAttributesMarshall() {
        SingleLogoutService service = (new SingleLogoutServiceBuilder()).buildObject();

        for (int i = 0; i < unknownAttributeNames.length; i++) {
            service.getUnknownAttributes().put(unknownAttributeNames[i], unknownAttributeValues[i]);
        }
        assertXMLEquals(expectedUnknownAttributesDOM, service);
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
    
    /** {@inheritDoc} */
    @Test public void testChildElementsUnmarshall() {
        SingleLogoutService service = (SingleLogoutService) unmarshallElement(childElementsFile);
        Assert.assertEquals(service.getUnknownXMLObjects().size(), 1);
    }
    
    /** {@inheritDoc} */
    @Test public void testChildElementsMarshall() {
        SingleLogoutService service = (new SingleLogoutServiceBuilder()).buildObject();
        service.setBinding(expectedBinding);
        service.setLocation(expectedLocation);


        XMLObject obj = new XSAnyBuilder().buildObject(new QName("http://example.org/", "bar", "foo"));
        
        service.getUnknownXMLObjects().add(obj);

        assertXMLEquals(expectedChildElementsDOM, service);    
    }
}