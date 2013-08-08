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
import org.opensaml.xacml.ctx.ResponseType;
import org.opensaml.xacml.ctx.ResultType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.xacml.ctx.ResponseType}.
 */
public class ResponseTest extends XMLObjectProviderBaseTestCase {

    private int expectedNumResults ;

    /**
     * Constructor
     */
    public ResponseTest() {
        singleElementFile = "/data/org/opensaml/xacml/ctx/impl/Response.xml";
        childElementsFile = "/data/org/opensaml/xacml/ctx/impl/ResponseChildElements.xml";
        
        expectedNumResults = 6;
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnmarshall() {
        ResponseType response = (ResponseType) unmarshallElement(singleElementFile);

        Assert.assertTrue(response.getResults().isEmpty());
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementMarshall() {
        ResponseType response = new ResponseTypeImplBuilder().buildObject();

        assertXMLEquals(expectedDOM, response);
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsUnmarshall() {
        ResponseType response = (ResponseType) unmarshallElement(childElementsFile);

        Assert.assertEquals(response.getResults().size(), expectedNumResults);
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsMarshall() {
        ResponseType response = new ResponseTypeImplBuilder().buildObject();

        for (int i = 0; i < expectedNumResults; i++) {
            response.getResults().add((ResultType) buildXMLObject(ResultType.DEFAULT_ELEMENT_NAME));
        }
        
        assertXMLEquals(expectedChildElementsDOM, response);
    }


}