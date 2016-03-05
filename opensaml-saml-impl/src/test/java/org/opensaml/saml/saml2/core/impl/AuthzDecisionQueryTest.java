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
import org.opensaml.saml.saml2.core.Action;
import org.opensaml.saml.saml2.core.AuthzDecisionQuery;
import org.opensaml.saml.saml2.core.Evidence;

/**
 *
 */
public class AuthzDecisionQueryTest extends SubjectQueryTestBase {
    
    /** Expected Resource attribute value */
    private String expectedResource;
    
    /** Expected number of Action child elements */
    private int expectedNumActions;
    

    /**
     * Constructor
     *
     */
    public AuthzDecisionQueryTest() {
        super();
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/AuthzDecisionQuery.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml2/core/impl/AuthzDecisionQueryOptionalAttributes.xml";
        childElementsFile = "/org/opensaml/saml/saml2/core/impl/AuthzDecisionQueryChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        expectedResource = "urn:string:resource";
        expectedNumActions = 2;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        QName qname = new QName(SAMLConstants.SAML20P_NS, AuthzDecisionQuery.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        AuthzDecisionQuery query = (AuthzDecisionQuery) buildXMLObject(qname);
        
        super.populateRequiredAttributes(query);
        query.setResource(expectedResource);
        
        assertXMLEquals(expectedDOM, query);
    }
    
    
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        QName qname = new QName(SAMLConstants.SAML20P_NS, AuthzDecisionQuery.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        AuthzDecisionQuery query = (AuthzDecisionQuery) buildXMLObject(qname);
        
        super.populateRequiredAttributes(query);
        super.populateOptionalAttributes(query);
        query.setResource(expectedResource);
        
        assertXMLEquals(expectedOptionalAttributesDOM, query);
    }
    
    

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        QName qname = new QName(SAMLConstants.SAML20P_NS, AuthzDecisionQuery.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        AuthzDecisionQuery query = (AuthzDecisionQuery) buildXMLObject(qname);
        
        super.populateChildElements(query);
        
        QName actionQName = new QName(SAMLConstants.SAML20_NS, Action.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        for (int i=0; i<expectedNumActions; i++){
            query.getActions().add((Action) buildXMLObject(actionQName));
        }
        
        QName evidenceQName = new QName(SAMLConstants.SAML20_NS, Evidence.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        query.setEvidence((Evidence) buildXMLObject(evidenceQName));
        
        assertXMLEquals(expectedChildElementsDOM, query);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        AuthzDecisionQuery query = (AuthzDecisionQuery) unmarshallElement(singleElementFile);
        
        Assert.assertNotNull(query, "AuthzDecisionQuery was null");
        Assert.assertEquals(query.getResource(), expectedResource, "Unmarshalled Resource attribute was not the expected value");
        super.helperTestSingleElementUnmarshall(query);

    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        AuthzDecisionQuery query = (AuthzDecisionQuery) unmarshallElement(singleElementOptionalAttributesFile);
        
        super.helperTestSingleElementOptionalAttributesUnmarshall(query);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        AuthzDecisionQuery query = (AuthzDecisionQuery) unmarshallElement(childElementsFile);
        
        Assert.assertEquals(query.getActions().size(), expectedNumActions, "Action count");
        Assert.assertNotNull(query.getEvidence(), "Evidence was null");
        super.helperTestChildElementsUnmarshall(query);
    }
}