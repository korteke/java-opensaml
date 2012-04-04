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
import org.testng.AssertJUnit;
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
        singleElementFile = "/data/org/opensaml/saml/saml2/core/impl/Assertion.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml/saml2/core/impl/AssertionOptionalAttributes.xml";
        childElementsFile = "/data/org/opensaml/saml/saml2/core/impl/AssertionChildElements.xml";
    }

    /** {@inheritDoc} */
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
        AssertJUnit.assertEquals("IssueInstant was " + notBefore + ", expected " + expectedIssueInstant, expectedIssueInstant,
                notBefore);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        Assertion assertion = (Assertion) unmarshallElement(singleElementOptionalAttributesFile);

        DateTime issueInstant = assertion.getIssueInstant();
        AssertJUnit.assertEquals("IssueInstant was " + issueInstant + ", expected " + expectedIssueInstant, expectedIssueInstant,
                issueInstant);

        String id = assertion.getID();
        AssertJUnit.assertEquals("ID was " + id + ", expected " + expectedID, expectedID, id);
        
        SAMLVersion version = assertion.getVersion();
        AssertJUnit.assertEquals("Version was " + version + ", expected " + expectedVersion, expectedVersion, version);
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

        AssertJUnit.assertNotNull("Issuer element not present", assertion.getIssuer());
        AssertJUnit.assertNotNull("Subject element not present", assertion.getSubject());
        AssertJUnit.assertNotNull("Conditions element not present", assertion.getConditions());
        AssertJUnit.assertNotNull("Advice element not present", assertion.getAdvice());
        AssertJUnit.assertEquals("Statement count not as expected", statementCount, assertion.getStatements().size());
        AssertJUnit.assertEquals("AuthnStatement count not as expected", authnStatementCount, assertion.getAuthnStatements().size());
        AssertJUnit.assertEquals("AuthzDecisionStatment count not as expected", authzDecisionStatementCount, assertion
                .getAuthzDecisionStatements().size());
        AssertJUnit.assertEquals("AttributeStatement count not as expected", attributeStatementCount, assertion
                .getAttributeStatements().size());
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