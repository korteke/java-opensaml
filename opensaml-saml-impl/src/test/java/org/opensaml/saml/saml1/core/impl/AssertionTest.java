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
import javax.xml.namespace.QName;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml1.core.Advice;
import org.opensaml.saml.saml1.core.Assertion;
import org.opensaml.saml.saml1.core.AttributeStatement;
import org.opensaml.saml.saml1.core.AuthenticationStatement;
import org.opensaml.saml.saml1.core.AuthorizationDecisionStatement;
import org.opensaml.saml.saml1.core.Conditions;
import org.opensaml.saml.saml1.core.Statement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Test for {@link org.opensaml.saml.saml1.core.Assertion}
 */
public class AssertionTest extends XMLObjectProviderBaseTestCase {

    /** name used to generate objects */
    private final QName qname;

    private final int expectedMinorVersion;

    private final String expectedIssuer;

    private final DateTime expectedIssueInstant;

    private final String expectedID;
    
    /**
     * Constructor
     */
    public AssertionTest() {
        expectedID = "ident";
        expectedMinorVersion = 1;
        expectedIssuer = "issuer";
        //
        // IssueInstant="1970-01-02T01:01:02.100Z"
        //
        expectedIssueInstant = new DateTime(1970, 1, 2, 1, 1, 2, 100, ISOChronology.getInstanceUTC());

        singleElementFile = "/org/opensaml/saml/saml1/impl/singleAssertion.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml1/impl/singleAssertionAttributes.xml";
        childElementsFile = "/org/opensaml/saml/saml1/impl/AssertionWithChildren.xml";
        qname = Assertion.DEFAULT_ELEMENT_NAME;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {

        Assertion assertion = (Assertion) unmarshallElement(singleElementFile);

        Assert.assertNull(assertion.getIssuer(), "Issuer attribute");
        Assert.assertNull(assertion.getIssueInstant(), "IssueInstant attribute");
        Assert.assertNull(assertion.getID(), "ID attribute");

        Assert.assertNull(assertion.getConditions(), "Conditions element");
        Assert.assertNull(assertion.getAdvice(), "Advice element");
        Assert.assertNull(assertion.getSignature(), "Signature element");

        Assert.assertEquals(assertion.getStatements().size(), 0, "Statement element count");
        Assert.assertEquals(assertion.getAttributeStatements().size(), 0, "AttributeStatements element count");
        Assert.assertEquals(assertion.getSubjectStatements().size(), 0, "SubjectStatements element count");
        Assert.assertEquals(assertion.getAuthenticationStatements().size(), 0, "AuthenticationStatements element count");
        Assert.assertEquals(assertion.getAuthorizationDecisionStatements().size(), 0, "AuthorizationDecisionStatements element count");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        Assertion assertion = (Assertion) unmarshallElement(singleElementOptionalAttributesFile);

        Assert.assertEquals(assertion.getIssuer(), expectedIssuer, "Issuer attribute");
        Assert.assertEquals(assertion.getIssueInstant(), expectedIssueInstant, "IssueInstant attribute");
        Assert.assertEquals(assertion.getID(), expectedID, "ID attribute");
        Assert.assertEquals(assertion.getMinorVersion(), expectedMinorVersion, "Issuer expectedMinorVersion");

        Assert.assertNull(assertion.getConditions(), "Conditions element");
        Assert.assertNull(assertion.getAdvice(), "Advice element");
        Assert.assertNull(assertion.getSignature(), "Signature element");

        Assert.assertEquals(assertion.getStatements().size(), 0, "Statement element count");
        Assert.assertEquals(assertion.getAttributeStatements().size(), 0, "AttributeStatements element count");
        Assert.assertEquals(assertion.getSubjectStatements().size(), 0, "SubjectStatements element count");
        Assert.assertEquals(assertion.getAuthenticationStatements().size(), 0, "AuthenticationStatements element count");
        Assert.assertEquals(assertion.getAuthorizationDecisionStatements().size(), 0, "AuthorizationDecisionStatements element count");
    }

    /**
     * Test an XML file with children
     */
    @Test
    public void testChildElementsUnmarshall() {
        Assertion assertion = (Assertion) unmarshallElement(childElementsFile);

        Assert.assertNull(assertion.getIssuer(), "Issuer attribute");
        Assert.assertNull(assertion.getID(), "ID attribute");
        Assert.assertNull(assertion.getIssueInstant(), "IssueInstant attribute");

        Assert.assertNotNull(assertion.getConditions(), "Conditions element null");
        Assert.assertNotNull(assertion.getAdvice(), "Advice element null");
        Assert.assertNull(assertion.getSignature(), "Signature element");

        Assert.assertNotNull(assertion.getAuthenticationStatements(), "No Authentication Statements");
        Assert.assertEquals(assertion.getAuthenticationStatements().size(), 2, "AuthenticationStatements element count");

        Assert.assertNotNull(assertion.getAttributeStatements(), "No Attribute Statements");
        Assert.assertEquals(assertion.getAttributeStatements().size(), 3, "AttributeStatements element count");

        Assert.assertNotNull(assertion.getAuthorizationDecisionStatements(), "No AuthorizationDecisionStatements ");
        Assert.assertEquals(assertion.getAuthorizationDecisionStatements()
                .size(), 3, "AuthorizationDecisionStatements element count");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        assertXMLEquals(expectedDOM, buildXMLObject(qname));
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        Assertion assertion = (Assertion) buildXMLObject(qname);

        assertion.setIssueInstant(expectedIssueInstant);
        assertion.setID(expectedID);
        assertion.setIssuer(expectedIssuer);
        assertXMLEquals(expectedOptionalAttributesDOM, assertion);
    }

    /**
     * Test an XML file with Children
     * @throws MarshallingException 
     */
    @Test
    public void testChildElementsMarshall() {
        Assertion assertion = (Assertion) buildXMLObject(qname);
        
        assertion.setConditions((Conditions) buildXMLObject(Conditions.DEFAULT_ELEMENT_NAME));
        assertion.setAdvice((Advice) buildXMLObject(Advice.DEFAULT_ELEMENT_NAME));

        QName authenticationQname = AuthenticationStatement.DEFAULT_ELEMENT_NAME;
        QName authorizationQname = AuthorizationDecisionStatement.DEFAULT_ELEMENT_NAME;
        QName attributeQname = AttributeStatement.DEFAULT_ELEMENT_NAME;
        
        assertion.getStatements().add((Statement) buildXMLObject(authenticationQname));
        assertion.getStatements().add((Statement) buildXMLObject(authorizationQname));
        assertion.getStatements().add((Statement) buildXMLObject(attributeQname));
        assertion.getStatements().add((Statement) buildXMLObject(authenticationQname));
        assertion.getStatements().add((Statement) buildXMLObject(authorizationQname));
        assertion.getStatements().add((Statement) buildXMLObject(attributeQname));
        assertion.getStatements().add((Statement) buildXMLObject(authorizationQname));
        assertion.getStatements().add((Statement) buildXMLObject(attributeQname));

        assertXMLEquals(expectedChildElementsDOM, assertion);
    }
    
    @Test
    public void testSignatureUnmarshall() {
        Assertion assertion = (Assertion) unmarshallElement("/org/opensaml/saml/saml1/impl/AssertionWithSignature.xml");
        
        Assert.assertNotNull(assertion, "Assertion was null");
        Assert.assertNotNull(assertion.getSignature(), "Signature was null");
        Assert.assertNotNull(assertion.getSignature().getKeyInfo(), "KeyInfo was null");
    }
    
    @Test
    public void testDOMIDResolutionUnmarshall() {
        Assertion assertion = (Assertion) unmarshallElement("/org/opensaml/saml/saml1/impl/AssertionWithSignature.xml");
        
        Assert.assertNotNull(assertion, "Assertion was null");
        Assert.assertNotNull(assertion.getSignature(), "Signature was null");
        Document document = assertion.getSignature().getDOM().getOwnerDocument();
        Element idElem = assertion.getDOM();
        
        Assert.assertNotNull(document.getElementById(expectedID), "DOM ID resolution returned null");
        Assert.assertTrue(idElem.isSameNode(document.getElementById(expectedID)), "DOM elements were not equal");
    }

    @Test
    public void testDOMIDResolutionMarshall() throws MarshallingException {
        Assertion assertion = (Assertion) buildXMLObject(Assertion.DEFAULT_ELEMENT_NAME);
        assertion.setID(expectedID);
        assertion.getAttributeStatements().add((AttributeStatement) buildXMLObject(AttributeStatement.DEFAULT_ELEMENT_NAME));
        
        marshallerFactory.getMarshaller(assertion).marshall(assertion);
        
        Document document = assertion.getStatements().get(0).getDOM().getOwnerDocument();
        Element idElem = assertion.getDOM();
        
        Assert.assertNotNull(document.getElementById(expectedID), "DOM ID resolution returned null");
        Assert.assertTrue(idElem.isSameNode(document.getElementById(expectedID)), "DOM elements were not equal");
    }
    
}
