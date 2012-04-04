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
import org.testng.annotations.BeforeMethod;
import org.testng.AssertJUnit;
import java.util.List;

import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml1.core.Action;
import org.opensaml.saml.saml1.core.AuthorizationDecisionQuery;
import org.opensaml.saml.saml1.core.Evidence;
import org.opensaml.saml.saml1.core.Subject;
import org.w3c.dom.Document;

/**
 * Test class for org.opensaml.saml.saml1.core.AttributeQuery
 */
public class AuthorizationDecisionQueryTest extends XMLObjectProviderBaseTestCase {

    /** name used to generate objects */
    private final QName qname;

    /** A file with a AuthenticationQuery with kids */

    private final String fullElementsFile;

    /** The expected result of a marshalled multiple element */

    private Document expectedFullDOM;

    private final String expectedResource;

    /**
     * Constructor
     */
    public AuthorizationDecisionQueryTest() {
        singleElementFile = "/data/org/opensaml/saml/saml1/impl/singleAuthorizationDecisionQuery.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml/saml1/impl/singleAuthorizationDecisionQueryAttributes.xml";
        fullElementsFile = "/data/org/opensaml/saml/saml1/impl/AuthorizationDecisionQueryWithChildren.xml";

        expectedResource = "resource";
        
        qname =new QName(SAMLConstants.SAML10P_NS, AuthorizationDecisionQuery.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1P_PREFIX);
    }

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedFullDOM = parserPool.parse(this.getClass().getResourceAsStream(fullElementsFile));
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {

        AuthorizationDecisionQuery authorizationDecisionQuery;
        authorizationDecisionQuery = (AuthorizationDecisionQuery) unmarshallElement(singleElementFile);

        AssertJUnit.assertNull("Resource attribute present", authorizationDecisionQuery.getResource());
        AssertJUnit.assertNull("Subject element present", authorizationDecisionQuery.getSubject());
        AssertJUnit.assertEquals("Count of AttributeDesignator elements", 0, authorizationDecisionQuery.getActions().size());
        AssertJUnit.assertNull("Evidence element present", authorizationDecisionQuery.getEvidence());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        AuthorizationDecisionQuery authorizationDecisionQuery;
        authorizationDecisionQuery = (AuthorizationDecisionQuery) unmarshallElement(singleElementOptionalAttributesFile);

        AssertJUnit.assertEquals("Resource attribute", expectedResource, authorizationDecisionQuery.getResource());
        AssertJUnit.assertNull("Subject element present", authorizationDecisionQuery.getSubject());
        AssertJUnit.assertEquals("Count of AttributeDesignator elements", 0, authorizationDecisionQuery.getActions().size());
        AssertJUnit.assertNull("Evidence element present", authorizationDecisionQuery.getEvidence());
    }

    /**
     * Test an Response file with children
     */
    @Test
    public void testFullElementsUnmarshall() {
        AuthorizationDecisionQuery authorizationDecisionQuery;
        authorizationDecisionQuery = (AuthorizationDecisionQuery) unmarshallElement(fullElementsFile);

        AssertJUnit.assertNotNull("Subject element present", authorizationDecisionQuery.getSubject());
        AssertJUnit.assertEquals("Count of Action elements", 3, authorizationDecisionQuery.getActions().size());
        AssertJUnit.assertNotNull("Evidence element present", authorizationDecisionQuery.getEvidence());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        assertXMLEquals(expectedDOM, buildXMLObject(qname));
    } 

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        AuthorizationDecisionQuery authorizationDecisionQuery;
        authorizationDecisionQuery = (AuthorizationDecisionQuery) buildXMLObject(qname);

        authorizationDecisionQuery.setResource(expectedResource);
        assertXMLEquals(expectedOptionalAttributesDOM, authorizationDecisionQuery);
    }

    /**
     * Test Marshalling up a file with children
     * 
     */
    @Test
    public void testFullElementsMarshall() {
        AuthorizationDecisionQuery authorizationDecisionQuery;
        authorizationDecisionQuery = (AuthorizationDecisionQuery) buildXMLObject(qname);
        authorizationDecisionQuery.setSubject((Subject) buildXMLObject(new QName(SAMLConstants.SAML1_NS, Subject.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX)));

        QName actionQname = new QName(SAMLConstants.SAML1_NS, Action.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
        List <Action> list = authorizationDecisionQuery.getActions();
        list.add((Action) buildXMLObject(actionQname));
        list.add((Action) buildXMLObject(actionQname));
        list.add((Action) buildXMLObject(actionQname));
        
        authorizationDecisionQuery.setEvidence((Evidence) buildXMLObject(new QName(SAMLConstants.SAML1_NS, Evidence.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX)));
        assertXMLEquals(expectedFullDOM, authorizationDecisionQuery);

    }

}
