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
package org.opensaml.saml.saml2.core.impl;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import javax.xml.namespace.QName;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeQuery;

/**
 *
 */
public class AttributeQueryTest extends SubjectQueryTestBase {
    
    /** Expected number of Attribute child elements */
    private int expectedNumAttributes;

    /**
     * Constructor
     *
     */
    public AttributeQueryTest() {
        super();
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/AttributeQuery.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml2/core/impl/AttributeQueryOptionalAttributes.xml";
        childElementsFile = "/org/opensaml/saml/saml2/core/impl/AttributeQueryChildElements.xml";
    }
    
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        expectedNumAttributes = 4;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        QName qname = new QName(SAMLConstants.SAML20P_NS, AttributeQuery.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        AttributeQuery query = (AttributeQuery) buildXMLObject(qname);
        
        super.populateRequiredAttributes(query);
        
        assertXMLEquals(expectedDOM, query);
    }
    
    

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        QName qname = new QName(SAMLConstants.SAML20P_NS, AttributeQuery.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        AttributeQuery query = (AttributeQuery) buildXMLObject(qname);
        
        super.populateRequiredAttributes(query);
        super.populateOptionalAttributes(query);
        
        assertXMLEquals(expectedOptionalAttributesDOM, query);
    }



    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        QName qname = new QName(SAMLConstants.SAML20P_NS, AttributeQuery.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        AttributeQuery query = (AttributeQuery) buildXMLObject(qname);
        
       populateChildElements(query);
       
       QName attributeQName = new QName(SAMLConstants.SAML20_NS, Attribute.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
       for (int i= 0; i<expectedNumAttributes; i++){
           query.getAttributes().add((Attribute) buildXMLObject(attributeQName));
       }
      
       assertXMLEquals(expectedChildElementsDOM, query);
    }



    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        AttributeQuery query = (AttributeQuery) unmarshallElement(singleElementFile);
        
        Assert.assertNotNull(query, "AttributeQuery was null");
        super.helperTestSingleElementUnmarshall(query);

    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        AttributeQuery query = (AttributeQuery) unmarshallElement(singleElementOptionalAttributesFile);
        
        Assert.assertNotNull(query, "AttributeQuery was null");
        super.helperTestSingleElementOptionalAttributesUnmarshall(query);
    }
    

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        AttributeQuery query = (AttributeQuery) unmarshallElement(childElementsFile);
        
        Assert.assertEquals(query.getAttributes().size(), expectedNumAttributes, "Attribute count");
        super.helperTestChildElementsUnmarshall(query);
    }
}