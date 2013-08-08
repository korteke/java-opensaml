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

package org.opensaml.xacml.ctx.impl;

import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.xacml.XACMLConstants;
import org.opensaml.xacml.ctx.AttributeValueType;
import org.opensaml.xacml.policy.ActionType;
import org.opensaml.xacml.policy.ApplyType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.xacml.ctx.AttributeValueType}.
 */
public class AttributeValueTest extends XMLObjectProviderBaseTestCase {

    private String expectedFunctionId;
    
    private String expectedTextContent;

    private QName[] expectedAttributeName = {new QName("foo"),
            new QName(XACMLConstants.XACML20_NS, "bar", XACMLConstants.XACML_PREFIX)};

    private String[] expectedAttributeValue = {"bar", "foo"};

    /**
     * Constructor
     */
    public AttributeValueTest() {
        singleElementFile = "/data/org/opensaml/xacml/ctx/impl/AttributeValue.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/xacml/ctx/impl/AttributeValueOptionalAttributes.xml";
        childElementsFile = "/data/org/opensaml/xacml/ctx/impl/AttributeValueChildElements.xml";

        expectedFunctionId = "http://example.org";
        expectedTextContent = "AttributeValueText";
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnmarshall() {
        AttributeValueType attributeValue = (AttributeValueType) unmarshallElement(singleElementFile);

        Assert.assertEquals(attributeValue.getValue(), expectedTextContent);
        Assert.assertTrue(attributeValue.getUnknownAttributes().isEmpty());
        Assert.assertTrue(attributeValue.getUnknownXMLObjects().isEmpty());
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementMarshall() {
        AttributeValueType attributeValue = (new AttributeValueTypeImplBuilder()).buildObject();

        attributeValue.setValue(expectedTextContent);
        
        assertXMLEquals(expectedDOM, attributeValue);
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsUnmarshall() {
        AttributeValueType attributeValue = (AttributeValueType) unmarshallElement(childElementsFile);

        Assert.assertEquals(attributeValue.getUnknownXMLObjects().size(), 2);
        Assert.assertNotNull(attributeValue.getUnknownXMLObjects(ActionType.DEFAULT_ELEMENT_NAME));
        Assert.assertEquals(
                ((ApplyType) attributeValue.getUnknownXMLObjects(ApplyType.DEFAULT_ELEMENT_NAME).get(0)).getFunctionId(),
                expectedFunctionId);
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsMarshall() {
        AttributeValueType attributeValue = (new AttributeValueTypeImplBuilder()).buildObject();

        attributeValue.getUnknownXMLObjects().add(buildXMLObject(ActionType.DEFAULT_ELEMENT_NAME));
        ApplyType apply = (ApplyType) buildXMLObject(ApplyType.DEFAULT_ELEMENT_NAME);
        apply.setFunctionId(expectedFunctionId);
        attributeValue.getUnknownXMLObjects().add(apply);

        assertXMLEquals(expectedChildElementsDOM, attributeValue);
    }

    @Test public void testSingleElementOptionalAttributesUnmarshall() {
        AttributeValueType attributeValue = (AttributeValueType) unmarshallElement(singleElementOptionalAttributesFile);

        Assert.assertEquals(attributeValue.getUnknownAttributes().size(), expectedAttributeValue.length);
        for (int i = 0; i < expectedAttributeValue.length; i++) {
            Assert.assertEquals(attributeValue.getUnknownAttributes().get(expectedAttributeName[i]), expectedAttributeValue[i]);
        }
    }


    @Test public void testSingleElementOptionalAttributesMarshall() {
        AttributeValueType attributeValue = (new AttributeValueTypeImplBuilder()).buildObject();
        
        for (int i = 0; i < expectedAttributeValue.length; i++) {
            attributeValue.getUnknownAttributes().put(expectedAttributeName[i], expectedAttributeValue[i]);
        }
        assertXMLEquals(expectedOptionalAttributesDOM, attributeValue);
    }

}