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
import org.opensaml.saml.saml2.core.AuthnQuery;
import org.opensaml.saml.saml2.core.RequestedAuthnContext;

/**
 *
 */
public class AuthnQueryTest extends SubjectQueryTestBase {
    
    /** Expected SessionIndex attribute value */
    private String expectedSessionIndex;

    /**
     * Constructor
     *
     */
    public AuthnQueryTest() {
        super();
        singleElementFile = "/data/org/opensaml/saml/saml2/core/impl/AuthnQuery.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml/saml2/core/impl/AuthnQueryOptionalAttributes.xml";
        childElementsFile = "/data/org/opensaml/saml/saml2/core/impl/AuthnQueryChildElements.xml";
    }
    

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        expectedSessionIndex = "session12345";
    }



    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        QName qname = new QName(SAMLConstants.SAML20P_NS, AuthnQuery.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        AuthnQuery query = (AuthnQuery) buildXMLObject(qname);
        
        super.populateRequiredAttributes(query);
        
        assertXMLEquals(expectedDOM, query);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        QName qname = new QName(SAMLConstants.SAML20P_NS, AuthnQuery.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        AuthnQuery query = (AuthnQuery) buildXMLObject(qname);
        
        super.populateRequiredAttributes(query);
        super.populateOptionalAttributes(query);
        query.setSessionIndex(expectedSessionIndex);
        
        assertXMLEquals(expectedOptionalAttributesDOM, query);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        QName qname = new QName(SAMLConstants.SAML20P_NS, AuthnQuery.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        AuthnQuery query = (AuthnQuery) buildXMLObject(qname);
        
        super.populateChildElements(query);
        
        QName requestedAuthnContextQName = new QName(SAMLConstants.SAML20P_NS, RequestedAuthnContext.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        query.setRequestedAuthnContext((RequestedAuthnContext) buildXMLObject(requestedAuthnContextQName));
        
        assertXMLEquals(expectedChildElementsDOM, query);
    }


    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        AuthnQuery query = (AuthnQuery) unmarshallElement(singleElementFile);
        
        Assert.assertNotNull(query, "AuthnQuery");
        Assert.assertNull(query.getSessionIndex(), "SessionIndex");
        super.helperTestSingleElementUnmarshall(query);

    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        AuthnQuery query = (AuthnQuery) unmarshallElement(singleElementOptionalAttributesFile);
        
        super.helperTestSingleElementOptionalAttributesUnmarshall(query);
        Assert.assertEquals(query.getSessionIndex(), expectedSessionIndex, "Unmarshalled SessionIndex was not the expected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        AuthnQuery query = (AuthnQuery) unmarshallElement(childElementsFile);
        
        super.helperTestChildElementsUnmarshall(query);
        Assert.assertNotNull(query.getRequestedAuthnContext(), "RequestedAuthnContext");
    }
}