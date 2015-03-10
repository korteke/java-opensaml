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
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.Action;
import org.opensaml.saml.saml2.core.AuthzDecisionStatement;
import org.opensaml.saml.saml2.core.DecisionTypeEnumeration;
import org.opensaml.saml.saml2.core.Evidence;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.core.impl.AuthzDecisionStatementImpl}.
 */
public class AuthzDecisionStatementTest extends XMLObjectProviderBaseTestCase {

    /** Expected Resource value */
    protected String expectedResource;

    /** Expected Decision value */
    protected DecisionTypeEnumeration expectedDecision;

    /** Count of Action subelements */
    protected int expectedActionCount = 3;

    /** Constructor */
    public AuthzDecisionStatementTest() {
        singleElementFile = "/data/org/opensaml/saml/saml2/core/impl/AuthzDecisionStatement.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml/saml2/core/impl/AuthzDecisionStatementOptionalAttributes.xml";
        childElementsFile = "/data/org/opensaml/saml/saml2/core/impl/AuthzDecisionStatementChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedResource = "resource name";
        expectedDecision = DecisionTypeEnumeration.DENY;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        AuthzDecisionStatement authzDecisionStatement = (AuthzDecisionStatement) unmarshallElement(singleElementFile);

        String resource = authzDecisionStatement.getResource();
        Assert.assertEquals(resource, expectedResource, "Resource not as expected");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        AuthzDecisionStatement authzDecisionStatement = (AuthzDecisionStatement) unmarshallElement(singleElementOptionalAttributesFile);

        String resource = authzDecisionStatement.getResource();
        Assert.assertEquals(resource, expectedResource, "Resource not as expected");

        DecisionTypeEnumeration decision = authzDecisionStatement.getDecision();
        Assert.assertEquals(decision.toString(), expectedDecision.toString(), "Decision not as expected");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        QName qname = new QName(SAMLConstants.SAML20_NS, AuthzDecisionStatement.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        AuthzDecisionStatement authzDecisionStatement = (AuthzDecisionStatement) buildXMLObject(qname);

        authzDecisionStatement.setResource(expectedResource);
        assertXMLEquals(expectedDOM, authzDecisionStatement);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        QName qname = new QName(SAMLConstants.SAML20_NS, AuthzDecisionStatement.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        AuthzDecisionStatement authzDecisionStatement = (AuthzDecisionStatement) buildXMLObject(qname);

        authzDecisionStatement.setResource(expectedResource);
        authzDecisionStatement.setDecision(expectedDecision);

        assertXMLEquals(expectedOptionalAttributesDOM, authzDecisionStatement);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        AuthzDecisionStatement authzDecisionStatement = (AuthzDecisionStatement) unmarshallElement(childElementsFile);
        Assert.assertEquals(authzDecisionStatement.getActions().size(), expectedActionCount, "Action Count");
        Assert.assertNotNull(authzDecisionStatement.getEvidence(), "Evidence element not present");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        QName qname = new QName(SAMLConstants.SAML20_NS, AuthzDecisionStatement.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        AuthzDecisionStatement authzDecisionStatement = (AuthzDecisionStatement) buildXMLObject(qname);

        QName actionQName = new QName(SAMLConstants.SAML20_NS, Action.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        for (int i = 0; i < expectedActionCount; i++) {
            authzDecisionStatement.getActions().add((Action) buildXMLObject(actionQName));
        }
        
        QName evidenceQName = new QName(SAMLConstants.SAML20_NS, Evidence.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        authzDecisionStatement.setEvidence((Evidence) buildXMLObject(evidenceQName));
        
        assertXMLEquals(expectedChildElementsDOM, authzDecisionStatement);
    }
}