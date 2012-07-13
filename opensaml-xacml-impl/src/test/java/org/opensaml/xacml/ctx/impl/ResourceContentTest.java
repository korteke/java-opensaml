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
import org.opensaml.xacml.ctx.ResourceContentType;
import org.opensaml.xacml.ctx.impl.ResourceContentTypeImplBuilder;
import org.opensaml.xacml.policy.ActionType;
import org.opensaml.xacml.policy.ApplyType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling {@link link org.opensaml.xacml.ctx.ResourceContextType}.
 */
public class ResourceContentTest extends XMLObjectProviderBaseTestCase {

    private String expectedFunctionId;

    private QName[] expectedAttributeName = {new QName("foorc"),
            new QName(XACMLConstants.XACML20_NS, "rcbar", XACMLConstants.XACML_PREFIX)};

    private String[] expectedAttributeValue = {"barrc", "rcfoo"};
    
    private String expectedContent;

    /**
     * Constructor
     */
    public ResourceContentTest() {
        singleElementFile = "/data/org/opensaml/xacml/ctx/impl/ResourceContent.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/xacml/ctx/impl/ResourceContentOptionalAttributes.xml";
        childElementsFile = "/data/org/opensaml/xacml/ctx/impl/ResourceContentChildElements.xml";

        expectedFunctionId = "http://example.org/Resource/Content/Apply/Function/Id";
        expectedContent = "ResourceContentText";
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnmarshall() {
        ResourceContentType resourceContent = (ResourceContentType) unmarshallElement(singleElementFile);

        Assert.assertTrue(resourceContent.getUnknownAttributes().isEmpty());
        Assert.assertTrue(resourceContent.getUnknownXMLObjects().isEmpty());
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementMarshall() {
        ResourceContentType resourceContent = (new ResourceContentTypeImplBuilder()).buildObject();

        Assert.assertTrue(resourceContent.getUnknownXMLObjects().isEmpty());
        Assert.assertNull(resourceContent.getValue());
        assertXMLEquals(expectedDOM, resourceContent);
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsUnmarshall() {
        ResourceContentType resourceContent = (ResourceContentType) unmarshallElement(childElementsFile);

        Assert.assertEquals(resourceContent.getUnknownXMLObjects().size(), 2);
        Assert.assertNotNull(resourceContent.getUnknownXMLObjects(ActionType.DEFAULT_ELEMENT_NAME));
        Assert.assertEquals(
                ((ApplyType) resourceContent.getUnknownXMLObjects(ApplyType.DEFAULT_ELEMENT_NAME).get(0)).getFunctionId(),
                expectedFunctionId);
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsMarshall() {
        ResourceContentType resourceContent = (new ResourceContentTypeImplBuilder()).buildObject();

        resourceContent.getUnknownXMLObjects().add(buildXMLObject(ActionType.DEFAULT_ELEMENT_NAME));
        ApplyType apply = (ApplyType) buildXMLObject(ApplyType.DEFAULT_ELEMENT_NAME);
        apply.setFunctionId(expectedFunctionId);
        resourceContent.getUnknownXMLObjects().add(apply);

        assertXMLEquals(expectedChildElementsDOM, resourceContent);
    }

    @Test public void testSingleElementOptionalAttributesUnmarshall() {
        ResourceContentType resourceContent = (ResourceContentType) unmarshallElement(singleElementOptionalAttributesFile);

        Assert.assertEquals(resourceContent.getValue(), expectedContent);
        Assert.assertEquals(resourceContent.getUnknownAttributes().size(), expectedAttributeValue.length);
        for (int i = 0; i < expectedAttributeValue.length; i++) {
            Assert.assertEquals(resourceContent.getUnknownAttributes().get(expectedAttributeName[i]), expectedAttributeValue[i]);
        }
    }


    @Test public void testSingleElementOptionalAttributesMarshall() {
        ResourceContentType resourceContent = (new ResourceContentTypeImplBuilder()).buildObject();
        
        resourceContent.setValue(expectedContent);
        for (int i = 0; i < expectedAttributeValue.length; i++) {
            resourceContent.getUnknownAttributes().put(expectedAttributeName[i], expectedAttributeValue[i]);
        }
        assertXMLEquals(expectedOptionalAttributesDOM, resourceContent);
    }

}