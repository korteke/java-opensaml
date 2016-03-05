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

package org.opensaml.xacml.policy.impl;

import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.xacml.policy.ActionsType;
import org.opensaml.xacml.policy.AttributeValueType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.xacml.policy.AttributeValueType}.
 */
public class AttributeValueTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedDataType;
    private String expectedValue;
    /** Unknown Attributes */
    private QName[] unknownAttributeNames = {new QName("urn:foo:bar", "bar", "foo"), new QName("flibble")};

    /** Unknown Attribute Values */
    private String[] unknownAttributeValues = {"fred", "flobble"};

    /**
     * Constructor
     */
    public AttributeValueTest(){
        singleElementFile = "/org/opensaml/xacml/policy/impl/AttributeValue.xml";
        singleElementUnknownAttributesFile = "/org/opensaml/xacml/policy/impl/AttributeValueUnknownAttributes.xml";
        childElementsFile = "/org/opensaml/xacml/policy/impl/AttributeValueChildElements.xml";

        expectedDataType = "https://example.org/Data/Type";    
        expectedValue = "Some Text";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        AttributeValueType attributeValue = (AttributeValueType) unmarshallElement(singleElementFile);

        Assert.assertEquals(attributeValue.getDataType(), expectedDataType);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall(){
        AttributeValueType attributeValue   = (new AttributeValueTypeImplBuilder()).buildObject();
        
        attributeValue.setDataType(expectedDataType);
        assertXMLEquals(expectedDOM, attributeValue );
    }
    
    @Test
    public void testChildElementsMarshall() {
        AttributeValueType attributeValue = (AttributeValueType) unmarshallElement(childElementsFile);

        Assert.assertEquals(attributeValue.getDataType(), expectedDataType);
        Assert.assertEquals(attributeValue.getValue(), expectedValue);
        
        int count = 0;
        for (XMLObject o: attributeValue.getUnknownXMLObjects()) {
            Assert.assertTrue(o instanceof ActionsType);
            count ++;
        }
        Assert.assertEquals(count, 3);
        Assert.assertEquals(attributeValue.getUnknownXMLObjects(ActionsType.DEFAULT_ELEMENT_NAME).size(), 3);
    }
    
    @Test
    public void testChildElementsUnmarshall() {
        AttributeValueType attributeValue   = (new AttributeValueTypeImplBuilder()).buildObject();
        
        attributeValue.setDataType(expectedDataType);
        attributeValue.setValue(expectedValue);
        for (int i = 0; i < 3; i++) {
            attributeValue.getUnknownXMLObjects().add(buildXMLObject(ActionsType.DEFAULT_ELEMENT_NAME));
        }
        assertXMLEquals(expectedChildElementsDOM, attributeValue );        
    }
    
    @Test
    public void testSingleElementUnknownAttributesMarshall() {
        AttributeValueType attributeValue = (AttributeValueType) unmarshallElement(singleElementUnknownAttributesFile);

        Assert.assertEquals(attributeValue.getDataType(), expectedDataType);

        AttributeMap attributes = attributeValue.getUnknownAttributes();
        for (int i = 0; i < unknownAttributeNames.length; i++) {
            Assert.assertEquals(attributes.get(unknownAttributeNames[i]), unknownAttributeValues[i]);
        }
    }

    @Test
    public void testSingleElementUnknownAttributesUnmarshall() {
        AttributeValueType attributeValue   = (new AttributeValueTypeImplBuilder()).buildObject();
        
        attributeValue.setDataType(expectedDataType);
        
        
        for (int i = 0; i < unknownAttributeNames.length; i++) {
            attributeValue.getUnknownAttributes().put(unknownAttributeNames[i], unknownAttributeValues[i]);
        }

        assertXMLEquals(expectedUnknownAttributesDOM, attributeValue);
    }
}