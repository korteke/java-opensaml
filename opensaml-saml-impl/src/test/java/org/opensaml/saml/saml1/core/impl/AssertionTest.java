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
import org.testng.AssertJUnit;
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
 * Test for {@link org.opensaml.saml.saml1.core.impl.Assertion}
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
        super();
        expectedID = "ident";
        expectedMinorVersion = 1;
        expectedIssuer = "issuer";
        //
        // IssueInstant="1970-01-02T01:01:02.100Z"
        //
        expectedIssueInstant = new DateTime(1970, 1, 2, 1, 1, 2, 100, ISOChronology.getInstanceUTC());

        singleElementFile = "/data/org/opensaml/saml/saml1/impl/singleAssertion.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml/saml1/impl/singleAssertionAttributes.xml";
        childElementsFile = "/data/org/opensaml/saml/saml1/impl/AssertionWithChildren.xml";
        qname = Assertion.DEFAULT_ELEMENT_NAME;
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementUnmarshall() {

        Assertion assertion = (Assertion) unmarshallElement(singleElementFile);

        AssertJUnit.assertNull("Issuer attribute", assertion.getIssuer());
        AssertJUnit.assertNull("IssueInstant attribute", assertion.getIssueInstant());
        AssertJUnit.assertNull("ID attribute", assertion.getID());

        AssertJUnit.assertNull("Conditions element", assertion.getConditions());
        AssertJUnit.assertNull("Advice element", assertion.getAdvice());
        AssertJUnit.assertNull("Signature element", assertion.getSignature());

        AssertJUnit.assertEquals("Statement element count", 0, assertion.getStatements().size());
        AssertJUnit.assertEquals("AttributeStatements element count", 0, assertion.getAttributeStatements().size());
        AssertJUnit.assertEquals("SubjectStatements element count", 0, assertion.getSubjectStatements().size());
        AssertJUnit.assertEquals("AuthenticationStatements element count", 0, assertion.getAuthenticationStatements().size());
        AssertJUnit.assertEquals("AuthorizationDecisionStatements element count", 0, assertion.getAuthorizationDecisionStatements().size());
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        Assertion assertion = (Assertion) unmarshallElement(singleElementOptionalAttributesFile);

        AssertJUnit.assertEquals("Issuer attribute", expectedIssuer, assertion.getIssuer());
        AssertJUnit.assertEquals("IssueInstant attribute", expectedIssueInstant, assertion.getIssueInstant());
        AssertJUnit.assertEquals("ID attribute", expectedID, assertion.getID());
        AssertJUnit.assertEquals("Issuer expectedMinorVersion", expectedMinorVersion, assertion.getMinorVersion());

        AssertJUnit.assertNull("Conditions element", assertion.getConditions());
        AssertJUnit.assertNull("Advice element", assertion.getAdvice());
        AssertJUnit.assertNull("Signature element", assertion.getSignature());

        AssertJUnit.assertEquals("Statement element count", 0, assertion.getStatements().size());
        AssertJUnit.assertEquals("AttributeStatements element count", 0, assertion.getAttributeStatements().size());
        AssertJUnit.assertEquals("SubjectStatements element count", 0, assertion.getSubjectStatements().size());
        AssertJUnit.assertEquals("AuthenticationStatements element count", 0, assertion.getAuthenticationStatements().size());
        AssertJUnit.assertEquals("AuthorizationDecisionStatements element count", 0, assertion.getAuthorizationDecisionStatements().size());
    }

    /**
     * Test an XML file with children
     */

    @Test
    public void testChildElementsUnmarshall() {
        Assertion assertion = (Assertion) unmarshallElement(childElementsFile);

        AssertJUnit.assertNull("Issuer attribute", assertion.getIssuer());
        AssertJUnit.assertNull("ID attribute", assertion.getID());
        AssertJUnit.assertNull("IssueInstant attribute", assertion.getIssueInstant());

        AssertJUnit.assertNotNull("Conditions element null", assertion.getConditions());
        AssertJUnit.assertNotNull("Advice element null", assertion.getAdvice());
        AssertJUnit.assertNull("Signature element", assertion.getSignature());

        AssertJUnit.assertNotNull("No Authentication Statements", assertion.getAuthenticationStatements());
        AssertJUnit.assertEquals("AuthenticationStatements element count", 2, assertion.getAuthenticationStatements().size());

        AssertJUnit.assertNotNull("No Attribute Statements", assertion.getAttributeStatements());
        AssertJUnit.assertEquals("AttributeStatements element count", 3, assertion.getAttributeStatements().size());

        AssertJUnit.assertNotNull("No AuthorizationDecisionStatements ", assertion.getAuthorizationDecisionStatements());
        AssertJUnit.assertEquals("AuthorizationDecisionStatements element count", 3, assertion.getAuthorizationDecisionStatements()
                .size());
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
        Assertion assertion = (Assertion) unmarshallElement("/data/org/opensaml/saml/saml1/impl/AssertionWithSignature.xml");
        
        AssertJUnit.assertNotNull("Assertion was null", assertion);
        AssertJUnit.assertNotNull("Signature was null", assertion.getSignature());
        AssertJUnit.assertNotNull("KeyInfo was null", assertion.getSignature().getKeyInfo());
    }
    
    @Test
    public void testDOMIDResolutionUnmarshall() {
        Assertion assertion = (Assertion) unmarshallElement("/data/org/opensaml/saml/saml1/impl/AssertionWithSignature.xml");
        
        AssertJUnit.assertNotNull("Assertion was null", assertion);
        AssertJUnit.assertNotNull("Signature was null", assertion.getSignature());
        Document document = assertion.getSignature().getDOM().getOwnerDocument();
        Element idElem = assertion.getDOM();
        
        AssertJUnit.assertNotNull("DOM ID resolution returned null", document.getElementById(expectedID));
        AssertJUnit.assertTrue("DOM elements were not equal", idElem.isSameNode(document.getElementById(expectedID)));
    }

    @Test
    public void testDOMIDResolutionMarshall() throws MarshallingException {
        Assertion assertion = (Assertion) buildXMLObject(Assertion.DEFAULT_ELEMENT_NAME);
        assertion.setID(expectedID);
        assertion.getAttributeStatements().add((AttributeStatement) buildXMLObject(AttributeStatement.DEFAULT_ELEMENT_NAME));
        
        marshallerFactory.getMarshaller(assertion).marshall(assertion);
        
        Document document = assertion.getStatements().get(0).getDOM().getOwnerDocument();
        Element idElem = assertion.getDOM();
        
        AssertJUnit.assertNotNull("DOM ID resolution returned null", document.getElementById(expectedID));
        AssertJUnit.assertTrue("DOM elements were not equal", idElem.isSameNode(document.getElementById(expectedID)));
    }
    
}
