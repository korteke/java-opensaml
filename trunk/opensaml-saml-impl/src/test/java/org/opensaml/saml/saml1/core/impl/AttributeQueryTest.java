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
import java.util.List;

import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml1.core.AttributeDesignator;
import org.opensaml.saml.saml1.core.AttributeQuery;
import org.opensaml.saml.saml1.core.Subject;

/**
 * Test class for org.opensaml.saml.saml1.core.AttributeQuery
 */
public class AttributeQueryTest extends XMLObjectProviderBaseTestCase {

    /** name used to generate objects */
    private final QName qname;

   /** The expected value of the Resource Identifier */
    private final String expectedResource;

    /**
     * Constructor
     */
    public AttributeQueryTest() {
        singleElementFile = "/org/opensaml/saml/saml1/impl/singleAttributeQuery.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml1/impl/singleAttributeQueryAttributes.xml";
        childElementsFile = "/org/opensaml/saml/saml1/impl/AttributeQueryWithChildren.xml";
        expectedResource = "resource";
        qname = new QName(SAMLConstants.SAML10P_NS, AttributeQuery.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1P_PREFIX);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {

        AttributeQuery attributeQuery;
        attributeQuery = (AttributeQuery) unmarshallElement(singleElementFile);

        Assert.assertNull(attributeQuery.getResource(), "Resource attribute present");
        Assert.assertNull(attributeQuery.getSubject(), "Subject element present");
        Assert.assertEquals(attributeQuery.getAttributeDesignators().size(), 0, "Count of AttributeDesignator elements");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        AttributeQuery attributeQuery;
        attributeQuery = (AttributeQuery) unmarshallElement(singleElementOptionalAttributesFile);

        Assert.assertEquals(attributeQuery.getResource(), expectedResource, "Resource attribute");
        Assert.assertNull(attributeQuery.getSubject(), "Subject element present");
        Assert.assertEquals(attributeQuery.getAttributeDesignators().size(), 0, "Count of AttributeDesignator elements");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        AttributeQuery attributeQuery;
        attributeQuery = (AttributeQuery) unmarshallElement(childElementsFile);

        Assert.assertNotNull(attributeQuery.getSubject(), "Subject element present");
        Assert.assertEquals(attributeQuery.getAttributeDesignators().size(), 4, "Count of AttributeDesignator elements");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        assertXMLEquals(expectedDOM, buildXMLObject(qname));
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        AttributeQuery attributeQuery;
        attributeQuery = (AttributeQuery) buildXMLObject(qname);

        attributeQuery.setResource(expectedResource);
        assertXMLEquals(expectedOptionalAttributesDOM, attributeQuery);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        AttributeQuery attributeQuery = (AttributeQuery) buildXMLObject(qname);

        attributeQuery.setSubject((Subject) buildXMLObject(new QName(SAMLConstants.SAML1_NS, Subject.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX)));
        List <AttributeDesignator> list = attributeQuery.getAttributeDesignators();
        QName attqname = new QName(SAMLConstants.SAML1_NS, AttributeDesignator.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);  
        list.add((AttributeDesignator) buildXMLObject(attqname));
        list.add((AttributeDesignator) buildXMLObject(attqname));
        list.add((AttributeDesignator) buildXMLObject(attqname)); 
        list.add((AttributeDesignator) buildXMLObject(attqname)); 
        assertXMLEquals(expectedChildElementsDOM, attributeQuery);

    }

}
