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

package org.opensaml.saml.saml1.core.impl;

import org.testng.annotations.Test;
import org.testng.Assert;
import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml1.core.Attribute;
import org.opensaml.saml.saml1.core.AttributeStatement;
import org.opensaml.saml.saml1.core.Subject;

/**
 * Test for {@link org.opensaml.saml.saml1.core.AttributeStatement}
 */
public class AttributeStatementTest extends XMLObjectProviderBaseTestCase {

    /** name used to generate objects */
    private final QName qname;

    /**
     * Constructor
     */
    public AttributeStatementTest() {
        super();
        singleElementFile = "/data/org/opensaml/saml/saml1/impl/singleAttributeStatement.xml";
        childElementsFile = "/data/org/opensaml/saml/saml1/impl/AttributeStatementWithChildren.xml";
        qname = new QName(SAMLConstants.SAML1_NS, AttributeStatement.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementUnmarshall() {
        AttributeStatement attributeStatement = (AttributeStatement) unmarshallElement(singleElementFile);

        Assert.assertNull(attributeStatement.getSubject(), "<Subject> element present");
        Assert.assertEquals(attributeStatement.getAttributes().size(), 0, "Non zero count of <Attribute> elements");
    }

    /** {@inheritDoc} */

    @Test
    public void testChildElementsUnmarshall() {
        AttributeStatement attributeStatement = (AttributeStatement) unmarshallElement(childElementsFile);

        Assert.assertNotNull(attributeStatement.getSubject(), "<Subject> element not present");
        Assert.assertNotNull(attributeStatement.getAttributes(), "<AuthorityBinding> elements not present");
        Assert.assertEquals(attributeStatement.getAttributes().size(), 5, "count of <AuthorityBinding> elements");

        Attribute attribute = attributeStatement.getAttributes().get(0);
        attributeStatement.getAttributes().remove(attribute);
        Assert.assertEquals(attributeStatement
                .getAttributes().size(), 4, "count of <AttributeStatement> elements after single remove");

        ArrayList<Attribute> list = new ArrayList<>(2);

        list.add(attributeStatement.getAttributes().get(0));
        list.add(attributeStatement.getAttributes().get(2));

        attributeStatement.getAttributes().removeAll(list);

        Assert.assertEquals(attributeStatement
                .getAttributes().size(), 2, "count of <AttributeStatement> elements after double remove");
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementMarshall() {
        assertXMLEquals(expectedDOM, buildXMLObject(qname));
    }

    /** {@inheritDoc} */

    @Test
    public void testChildElementsMarshall() {

        AttributeStatement attributeStatement = (AttributeStatement) buildXMLObject(qname);

        attributeStatement.setSubject((Subject) buildXMLObject(new QName(SAMLConstants.SAML1_NS, Subject.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX)));

        QName oqname = new QName(SAMLConstants.SAML1_NS, Attribute.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
        for (int i = 0; i < 5; i++) {
            attributeStatement.getAttributes().add((Attribute) buildXMLObject(oqname));
        }

        assertXMLEquals(expectedChildElementsDOM, attributeStatement);
    }
}
