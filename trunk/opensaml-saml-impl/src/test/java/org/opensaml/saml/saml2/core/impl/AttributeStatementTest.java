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

package org.opensaml.saml.saml2.core.impl;

import org.testng.annotations.Test;
import org.testng.Assert;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.EncryptedAttribute;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.saml.saml2.core.impl.AttributeStatementImpl}.
 */
public class AttributeStatementTest extends XMLObjectProviderBaseTestCase {

    /** Count of Attribute subelements. */
    private int expectedAttributeCount = 3;
    
    /** Count of EncryptedAttribute subelements. */
    private int expectedEncryptedAttributeCount = 3;


    /** Constructor. */
    public AttributeStatementTest() {
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/AttributeStatement.xml";
        childElementsFile = "/org/opensaml/saml/saml2/core/impl/AttributeStatementChildElements.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        AttributeStatement attributeStatement = (AttributeStatement) unmarshallElement(singleElementFile);

        Assert.assertNotNull(attributeStatement);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        AttributeStatement attributeStatement = (AttributeStatement) unmarshallElement(childElementsFile);
        Assert.assertEquals(attributeStatement.getAttributes().size(), expectedAttributeCount, "Attribute Count");
        Assert.assertEquals(attributeStatement.getEncryptedAttributes().size(), 
                expectedEncryptedAttributeCount, "EncryptedAttribute Count");
    }


    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        AttributeStatement attributeStatement = 
            (AttributeStatement) buildXMLObject(AttributeStatement.DEFAULT_ELEMENT_NAME);

        assertXMLEquals(expectedDOM, attributeStatement);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        AttributeStatement attributeStatement = 
            (AttributeStatement) buildXMLObject(AttributeStatement.DEFAULT_ELEMENT_NAME);

        attributeStatement.getAttributes()
            .add((Attribute) buildXMLObject(Attribute.DEFAULT_ELEMENT_NAME));
        attributeStatement.getEncryptedAttributes()
            .add((EncryptedAttribute) buildXMLObject(EncryptedAttribute.DEFAULT_ELEMENT_NAME));
        attributeStatement.getAttributes()
            .add((Attribute) buildXMLObject(Attribute.DEFAULT_ELEMENT_NAME));
        attributeStatement.getEncryptedAttributes()
            .add((EncryptedAttribute) buildXMLObject(EncryptedAttribute.DEFAULT_ELEMENT_NAME));
        attributeStatement.getEncryptedAttributes()
            .add((EncryptedAttribute) buildXMLObject(EncryptedAttribute.DEFAULT_ELEMENT_NAME));
        attributeStatement.getAttributes()
            .add((Attribute) buildXMLObject(Attribute.DEFAULT_ELEMENT_NAME));

        assertXMLEquals(expectedChildElementsDOM, attributeStatement);
    }
}