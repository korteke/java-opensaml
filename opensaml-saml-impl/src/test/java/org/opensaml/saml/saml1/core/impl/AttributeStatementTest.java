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
import org.testng.AssertJUnit;
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

        AssertJUnit.assertNull("<Subject> element present", attributeStatement.getSubject());
        AssertJUnit.assertEquals("Non zero count of <Attribute> elements", 0, attributeStatement.getAttributes().size());
    }

    /** {@inheritDoc} */

    @Test
    public void testChildElementsUnmarshall() {
        AttributeStatement attributeStatement = (AttributeStatement) unmarshallElement(childElementsFile);

        AssertJUnit.assertNotNull("<Subject> element not present", attributeStatement.getSubject());
        AssertJUnit.assertNotNull("<AuthorityBinding> elements not present", attributeStatement.getAttributes());
        AssertJUnit.assertEquals("count of <AuthorityBinding> elements", 5, attributeStatement.getAttributes().size());

        Attribute attribute = attributeStatement.getAttributes().get(0);
        attributeStatement.getAttributes().remove(attribute);
        AssertJUnit.assertEquals("count of <AttributeStatement> elements after single remove", 4, attributeStatement
                .getAttributes().size());

        ArrayList<Attribute> list = new ArrayList<Attribute>(2);

        list.add(attributeStatement.getAttributes().get(0));
        list.add(attributeStatement.getAttributes().get(2));

        attributeStatement.getAttributes().removeAll(list);

        AssertJUnit.assertEquals("count of <AttributeStatement> elements after double remove", 2, attributeStatement
                .getAttributes().size());
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
