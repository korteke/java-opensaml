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
import org.opensaml.xacml.ctx.ActionType;
import org.opensaml.xacml.ctx.EnvironmentType;
import org.opensaml.xacml.ctx.RequestType;
import org.opensaml.xacml.ctx.ResourceType;
import org.opensaml.xacml.ctx.SubjectType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling {@link link org.opensaml.xacml.ctx.RequestType}.
 */
public class RequestTest extends XMLObjectProviderBaseTestCase {

    private int expectedNumSubjects;

    private int expectedNumResources;

    private String expectedSubjectCategory;

    /**
     * Constructor
     */
    public RequestTest() {
        singleElementFile = "/data/org/opensaml/xacml/ctx/impl/Request.xml";
        childElementsFile = "/data/org/opensaml/xacml/ctx/impl/RequestChildElements.xml";

        expectedNumSubjects = 3;
        expectedNumResources = 2;
        expectedSubjectCategory = "https://example.org/Request/Subject/Subject/Category";
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnmarshall() {
        RequestType request = (RequestType) unmarshallElement(singleElementFile);

        Assert.assertTrue(request.getSubjects().isEmpty());
        Assert.assertTrue(request.getResources().isEmpty());
        Assert.assertNull(request.getAction());
        Assert.assertNull(request.getEnvironment());
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementMarshall() {
        RequestType request = new RequestTypeImplBuilder().buildObject();

        assertXMLEquals(expectedDOM, request);
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsUnmarshall() {
        RequestType request = (RequestType) unmarshallElement(childElementsFile);

        Assert.assertEquals(request.getSubjects().size(), expectedNumSubjects);
        for (SubjectType subject : request.getSubjects()) {
            Assert.assertEquals(subject.getSubjectCategory(), expectedSubjectCategory);
        }
        Assert.assertEquals(request.getResources().size(), expectedNumResources);
        Assert.assertNotNull(request.getAction());
        Assert.assertNotNull(request.getEnvironment());
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsMarshall() {
        RequestType request = new RequestTypeImplBuilder().buildObject();

        for (int i = 0; i < expectedNumResources; i++) {
            request.getResources().add((ResourceType) buildXMLObject(ResourceType.DEFAULT_ELEMENT_NAME));
        }
        for (int i = 0; i < expectedNumSubjects; i++) {
            SubjectType subject = buildXMLObject(SubjectType.DEFAULT_ELEMENT_NAME);
            subject.setSubjectCategory(expectedSubjectCategory);
            request.getSubjects().add(subject); 
        }
        request.setAction((ActionType) buildXMLObject(ActionType.DEFAULT_ELEMENT_NAME));
        request.setEnvironment((EnvironmentType) buildXMLObject(EnvironmentType.DEFAULT_ELEMENT_NAME));

        assertXMLEquals(expectedChildElementsDOM, request);
    }

}