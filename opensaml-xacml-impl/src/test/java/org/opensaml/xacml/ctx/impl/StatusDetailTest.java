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

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.xacml.ctx.StatusDetailType;
import org.opensaml.xacml.policy.ActionType;
import org.opensaml.xacml.policy.ApplyType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.xacml.ctx.StatusDetailType}.
 */
public class StatusDetailTest extends XMLObjectProviderBaseTestCase {

    private String expectedFunctionId;

    /**
     * Constructor
     */
    public StatusDetailTest() {
        singleElementFile = "/data/org/opensaml/xacml/ctx/impl/StatusDetail.xml";
        childElementsFile = "/data/org/opensaml/xacml/ctx/impl/StatusDetailChildElements.xml";
        
        expectedFunctionId = "http://example.org";

    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnmarshall() {
        StatusDetailType statusDetail = (StatusDetailType) unmarshallElement(singleElementFile);

        Assert.assertTrue(statusDetail.getUnknownXMLObjects().isEmpty());
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementMarshall() {
        StatusDetailType statusDetail = (new StatusDetailTypeImplBuilder()).buildObject();

        assertXMLEquals(expectedDOM, statusDetail);
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsUnmarshall() {
        StatusDetailType statusDetail = (StatusDetailType) unmarshallElement(childElementsFile);

        Assert.assertEquals(statusDetail.getUnknownXMLObjects().size(), 2);
        Assert.assertNotNull(statusDetail.getUnknownXMLObjects(ActionType.DEFAULT_ELEMENT_NAME));
        Assert.assertEquals(
                ((ApplyType) statusDetail.getUnknownXMLObjects(ApplyType.DEFAULT_ELEMENT_NAME).get(0)).getFunctionId(),
                expectedFunctionId);
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsMarshall() {
        StatusDetailType statusDetail = (new StatusDetailTypeImplBuilder()).buildObject();

        statusDetail.getUnknownXMLObjects().add(buildXMLObject(ActionType.DEFAULT_ELEMENT_NAME));
        ApplyType apply = (ApplyType) buildXMLObject(ApplyType.DEFAULT_ELEMENT_NAME);
        apply.setFunctionId(expectedFunctionId);
        statusDetail.getUnknownXMLObjects().add(apply);

        assertXMLEquals(expectedChildElementsDOM, statusDetail);
    }


}