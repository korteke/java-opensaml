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
import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.ctx.SubjectType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.xacml.ctx.SubjectType}.
 */
public class SubjectTest extends XMLObjectProviderBaseTestCase {

    private int expectedNumAttributes;

    private String expectedAttributeId;

    private String expectedDataType;

    private String expectedSubjectCategory;

    /**
     * Constructor
     */
    public SubjectTest() {
        singleElementFile = "/data/org/opensaml/xacml/ctx/impl/Subject.xml";
        childElementsFile = "/data/org/opensaml/xacml/ctx/impl/SubjectChildElements.xml";

        expectedSubjectCategory="https://example.org/Subject/Subject/Category";
        expectedAttributeId = "https://example.org/Subject/Attribute/Attribute/Id";
        expectedDataType = "https://example.org/Subject/Attribute/Data/Type";
        expectedNumAttributes = 2;
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnmarshall() {
        SubjectType subject = (SubjectType) unmarshallElement(singleElementFile);

        Assert.assertEquals(subject.getSubjectCategory(), SubjectType.SUBJECT_CATEGORY_ATTTRIB_DEFAULT);
        Assert.assertTrue(subject.getAttributes().isEmpty());
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementMarshall() {
        SubjectType subject = new SubjectTypeImplBuilder().buildObject();

        Assert.assertEquals(subject.getSubjectCategory(), SubjectType.SUBJECT_CATEGORY_ATTTRIB_DEFAULT);
        subject.setSubjectCategory(null);
        assertXMLEquals(expectedDOM, subject);
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsUnmarshall() {
        SubjectType subject = (SubjectType) unmarshallElement(childElementsFile);

        Assert.assertEquals(subject.getSubjectCategory(), expectedSubjectCategory);
        Assert.assertEquals(subject.getAttributes().size(), expectedNumAttributes);
        for (AttributeType attribute: subject.getAttributes()) {
            Assert.assertEquals(attribute.getAttributeId(), expectedAttributeId);
            Assert.assertEquals(attribute.getDataType(), expectedDataType);
        }
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsMarshall() {
        SubjectType subject = new SubjectTypeImplBuilder().buildObject();

        subject.setSubjectCategory(expectedSubjectCategory);
        for (int i = 0; i < expectedNumAttributes; i++) {
            AttributeType attribute = buildXMLObject(AttributeType.DEFAULT_ELEMENT_NAME);
            attribute.setAttributeID(expectedAttributeId);
            attribute.setDataType(expectedDataType);
            subject.getAttributes().add(attribute);
        }

        assertXMLEquals(expectedChildElementsDOM, subject);
    }

}