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
import org.opensaml.xacml.ctx.StatusCodeType;
import org.opensaml.xacml.ctx.StatusDetailType;
import org.opensaml.xacml.ctx.StatusMessageType;
import org.opensaml.xacml.ctx.StatusType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.xacml.ctx.StatusType}.
 */
public class StatusTest extends XMLObjectProviderBaseTestCase {

    private String expectedValue;
    private String expectedMessage;

    /**
     * Constructor
     */
    public StatusTest() {
        singleElementFile = "/org/opensaml/xacml/ctx/impl/Status.xml";
        childElementsFile = "/org/opensaml/xacml/ctx/impl/StatusChildElements.xml";
        
        expectedValue = "https://example.org/Status/Status/Code";
        expectedMessage = "StatusStatusMessageTextstatusCode";
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnmarshall() {
        StatusType status = (StatusType) unmarshallElement(singleElementFile);

        Assert.assertNull(status.getStatusCode());
        Assert.assertNull(status.getStatusMessage());
        Assert.assertNull(status.getStatusDetail());
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementMarshall() {
        StatusType status = new StatusTypeImplBuilder().buildObject();

        assertXMLEquals(expectedDOM, status);
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsUnmarshall() {
        StatusType status = (StatusType) unmarshallElement(childElementsFile);
        
        Assert.assertEquals(status.getStatusCode().getValue(), expectedValue);
        Assert.assertEquals(status.getStatusMessage().getValue(), expectedMessage);
        Assert.assertNotNull(status.getStatusDetail());
        
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsMarshall() {
        StatusType status = new StatusTypeImplBuilder().buildObject();

        status.setStatusCode((StatusCodeType) buildXMLObject(StatusCodeType.DEFAULT_ELEMENT_NAME));
        status.getStatusCode().setValue(expectedValue);
        
        status.setStatusMessage((StatusMessageType) buildXMLObject(StatusMessageType.DEFAULT_ELEMENT_NAME));
        status.getStatusMessage().setValue(expectedMessage);

        status.setStatusDetail((StatusDetailType) buildXMLObject(StatusDetailType.DEFAULT_ELEMENT_NAME));

        assertXMLEquals(expectedChildElementsDOM, status);
    }


}