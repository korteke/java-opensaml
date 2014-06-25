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
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.xacml.ctx.StatusCodeType}.
 */
public class StatusCodeTest extends XMLObjectProviderBaseTestCase {

    private String expectedValue;
    private String expectedMinorValue;

    /**
     * Constructor
     */
    public StatusCodeTest() {
        singleElementFile = "/data/org/opensaml/xacml/ctx/impl/StatusCode.xml";
        childElementsFile = "/data/org/opensaml/xacml/ctx/impl/StatusCodeChildElements.xml"; 

        expectedValue = "https://example.org/Status/Code";
        expectedMinorValue = "https://example.org/Status/Code/Minor";
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnmarshall() {
        StatusCodeType statusCode = (StatusCodeType) unmarshallElement(singleElementFile);

        Assert.assertEquals(statusCode.getValue(), expectedValue);
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementMarshall() {
        StatusCodeType statusCode = (new StatusCodeTypeImplBuilder()).buildObject();

        statusCode.setValue(expectedValue);
        assertXMLEquals(expectedDOM, statusCode);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        StatusCodeType statusCode = (StatusCodeType) unmarshallElement(childElementsFile);

        Assert.assertEquals(statusCode.getValue(), expectedValue);
        Assert.assertEquals(statusCode.getStatusCode().getValue(), expectedMinorValue);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        StatusCodeType statusCode = (new StatusCodeTypeImplBuilder()).buildObject();

        statusCode.setValue(expectedValue);
        statusCode.setStatusCode(new StatusCodeTypeImplBuilder().buildObject());
        statusCode.getStatusCode().setValue(expectedMinorValue);
        assertXMLEquals(expectedChildElementsDOM, statusCode);
    }
}