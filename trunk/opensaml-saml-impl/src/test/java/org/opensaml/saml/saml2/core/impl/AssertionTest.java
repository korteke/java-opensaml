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

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.Advice;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.AuthzDecisionStatement;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.Subject;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.saml.saml2.core.impl.AssertionImpl}.
 */
public class AssertionTest extends XMLObjectProviderBaseTestCase {

    /** Expected Version value */
    private SAMLVersion expectedVersion;
    
    /** Expected IssueInstant value */
    private DateTime expectedIssueInstant;

    /** Expected ID value */
    private String expectedID;

    /** Count of Statement subelements */
    private int statementCount = 7;

    /** Count of AuthnStatement subelements */
    private int authnStatementCount = 2;

    /** Count of AuthzDecisionStatement submelements */
    private int authzDecisionStatementCount = 2;

    /** Count of AttributeStatement subelements */
    private int attributeStatementCount = 3;

    /** Constructor */
    public AssertionTest() {
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/Assertion.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml2/core/impl/AssertionOptionalAttributes.xml";
        childElementsFile = "/org/opensaml/saml/saml2/core/impl/AssertionChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedVersion = SAMLVersion.VERSION_20;
        expectedIssueInstant = new DateTime(1984, 8, 26, 10, 01, 30, 43, ISOChronology.getInstanceUTC());
        expectedID = "id";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        Assertion assertion = (Assertion) unmarshallElement(singleElementFile);

        DateTime notBefore = assertion.getIssueInstant();
        Assert.assertEquals(notBefore, expectedIssueInstant,
                "IssueInstant was " + notBefore + ", expected " + expectedIssueInstant);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        Assertion assertion = (Assertion) unmarshallElement(singleElementOptionalAttributesFile);

        DateTime issueInstant = assertion.getIssueInstant();
        Assert.assertEquals(issueInstant, expectedIssueInstant,
                "IssueInstant was " + issueInstant + ", expected " + expectedIssueInstant);

        String id = assertion.getID();
        Assert.assertEquals(id, expectedID, "ID was " + id + ", expected " + expectedID);
        
        SAMLVersion version = assertion.getVersion();
        Assert.assertEquals(version, expectedVersion, "Version was " + version + ", expected " + expectedVersion);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        QName qname = new QName(SAMLConstants.SAML20_NS, Assertion.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        Assertion assertion = (Assertion) buildXMLObject(qname);

        assertion.setIssueInstant(expectedIssueInstant);

        assertXMLEquals(expectedDOM, assertion);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        QName qname = new QName(SAMLConstants.SAML20_NS, Assertion.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        Assertion assertion = (Assertion) buildXMLObject(qname);

        assertion.setIssueInstant(expectedIssueInstant);
        assertion.setID(expectedID);
        assertion.setVersion(expectedVersion);

        assertXMLEquals(expectedOptionalAttributesDOM, assertion);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        Assertion assertion = (Assertion) unmarshallElement(childElementsFile);

        Assert.assertNotNull(assertion.getIssuer(), "Issuer element not present");
        Assert.assertNotNull(assertion.getSubject(), "Subject element not present");
        Assert.assertNotNull(assertion.getConditions(), "Conditions element not present");
        Assert.assertNotNull(assertion.getAdvice(), "Advice element not present");
        Assert.assertEquals(assertion.getStatements().size(), statementCount, "Statement count not as expected");
        Assert.assertEquals(assertion.getAuthnStatements().size(), authnStatementCount, "AuthnStatement count not as expected");
        Assert.assertEquals(assertion
                .getAuthzDecisionStatements().size(), authzDecisionStatementCount, "AuthzDecisionStatment count not as expected");
        Assert.assertEquals(assertion
                .getAttributeStatements().size(), attributeStatementCount, "AttributeStatement count not as expected");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        QName qname = new QName(SAMLConstants.SAML20_NS, Assertion.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        Assertion assertion = (Assertion) buildXMLObject(qname);

        QName issuerQName = new QName(SAMLConstants.SAML20_NS, Issuer.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        assertion.setIssuer((Issuer) buildXMLObject(issuerQName));
        
        QName subjectQName = new QName(SAMLConstants.SAML20_NS, Subject.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        assertion.setSubject((Subject) buildXMLObject(subjectQName));
        
        QName conditionsQName = new QName(SAMLConstants.SAML20_NS, Conditions.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        assertion.setConditions((Conditions) buildXMLObject(conditionsQName));
        
        QName adviceQName = new QName(SAMLConstants.SAML20_NS, Advice.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        assertion.setAdvice((Advice) buildXMLObject(adviceQName));

        QName authnStatementQName = new QName(SAMLConstants.SAML20_NS, AuthnStatement.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        for (int i = 0; i < authnStatementCount; i++) {
            assertion.getAuthnStatements().add((AuthnStatement) buildXMLObject(authnStatementQName));
        }
        
        QName authzDecisionStatementQName = new QName(SAMLConstants.SAML20_NS, AuthzDecisionStatement.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        for (int i = 0; i < authzDecisionStatementCount; i++) {
            assertion.getAuthzDecisionStatements().add((AuthzDecisionStatement) buildXMLObject(authzDecisionStatementQName));
        }
        
        QName attributeStatementQName = new QName(SAMLConstants.SAML20_NS, AttributeStatement.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        for (int i = 0; i < attributeStatementCount; i++) {
            assertion.getAttributeStatements().add((AttributeStatement) buildXMLObject(attributeStatementQName));
        }
        
        assertXMLEquals(expectedChildElementsDOM, assertion);
    }
}