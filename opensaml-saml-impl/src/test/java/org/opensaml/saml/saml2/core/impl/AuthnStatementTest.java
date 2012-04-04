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
import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.SubjectLocality;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.saml.saml2.core.impl.AuthnStatementImpl}.
 */
public class AuthnStatementTest extends XMLObjectProviderBaseTestCase {

    /** Expected AuthnInstant value */
    private DateTime expectedAuthnInstant;

    /** Expected SessionIndex value */
    private String expectedSessionIndex;

    /** Expected SessionNotOnOrAfter value */
    private DateTime expectedSessionNotOnOrAfter;

    /** Constructor */
    public AuthnStatementTest() {
        singleElementFile = "/data/org/opensaml/saml/saml2/core/impl/AuthnStatement.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml/saml2/core/impl/AuthnStatementOptionalAttributes.xml";
        childElementsFile = "/data/org/opensaml/saml/saml2/core/impl/AuthnStatementChildElements.xml";
    }

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedAuthnInstant = new DateTime(1984, 8, 26, 10, 01, 30, 43, ISOChronology.getInstanceUTC());
        expectedSessionIndex = "index";
        expectedSessionNotOnOrAfter = new DateTime(1984, 8, 26, 10, 11, 30, 43, ISOChronology.getInstanceUTC());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        AuthnStatement authnStatement = (AuthnStatement) unmarshallElement(singleElementFile);

        DateTime authnInstant = authnStatement.getAuthnInstant();
        AssertJUnit.assertEquals("AuthnInstant was " + authnInstant + ", expected " + expectedAuthnInstant, expectedAuthnInstant,
                authnInstant);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        AuthnStatement authnStatement = (AuthnStatement) unmarshallElement(singleElementOptionalAttributesFile);

        DateTime authnInstant = authnStatement.getAuthnInstant();
        AssertJUnit.assertEquals("AuthnInstant was " + authnInstant + ", expected " + expectedAuthnInstant, expectedAuthnInstant,
                authnInstant);

        String sessionIndex = authnStatement.getSessionIndex();
        AssertJUnit.assertEquals("SessionIndex was " + sessionIndex + ", expected " + expectedSessionIndex, expectedSessionIndex,
                sessionIndex);

        DateTime sessionNotOnOrAfter = authnStatement.getSessionNotOnOrAfter();
        AssertJUnit.assertEquals("SessionNotOnOrAfter was " + sessionNotOnOrAfter + ", expected " + expectedSessionNotOnOrAfter,
                expectedSessionNotOnOrAfter, sessionNotOnOrAfter);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        AuthnStatement authnStatement = (AuthnStatement) buildXMLObject(AuthnStatement.DEFAULT_ELEMENT_NAME);

        authnStatement.setAuthnInstant(expectedAuthnInstant);
        assertXMLEquals(expectedDOM, authnStatement);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        AuthnStatement authnStatement = (AuthnStatement) buildXMLObject(AuthnStatement.DEFAULT_ELEMENT_NAME);

        authnStatement.setAuthnInstant(expectedAuthnInstant);
        authnStatement.setSessionIndex(expectedSessionIndex);
        authnStatement.setSessionNotOnOrAfter(expectedSessionNotOnOrAfter);

        assertXMLEquals(expectedOptionalAttributesDOM, authnStatement);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        AuthnStatement authnStatement = (AuthnStatement) unmarshallElement(childElementsFile);
        AssertJUnit.assertNotNull("AuthnContext element not present", authnStatement.getAuthnContext());
        AssertJUnit.assertNotNull("SubjectLocality element not present", authnStatement.getSubjectLocality());
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        AuthnStatement authnStatement = (AuthnStatement) buildXMLObject(AuthnStatement.DEFAULT_ELEMENT_NAME);

        authnStatement.setSubjectLocality((SubjectLocality) buildXMLObject(SubjectLocality.DEFAULT_ELEMENT_NAME));
        
        authnStatement.setAuthnContext((AuthnContext) buildXMLObject(AuthnContext.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, authnStatement);
    }
}