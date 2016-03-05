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
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml1.core.AuthenticationStatement;
import org.opensaml.saml.saml1.core.AuthorityBinding;
import org.opensaml.saml.saml1.core.Subject;
import org.opensaml.saml.saml1.core.SubjectLocality;

/**
 * 
 */
public class AuthenticationStatementTest extends XMLObjectProviderBaseTestCase {

    /** name used to generate objects */
    private final QName qname;

    /** Expected value of AuthenticationMethod */
    private String expectedAuthenticationMethod;

    /** Expected value of AuthenticationInstant */
    private DateTime expectedAuthenticationInstant;

    /**
     * Constructor
     */
    public AuthenticationStatementTest() {
        super();
        expectedAuthenticationMethod = "trustme";
        //
        // AuthenticationInstant="1970-01-02T01:01:02.123Z"
        //
        expectedAuthenticationInstant = new DateTime(1970, 1, 2, 1, 1, 2, 123, ISOChronology.getInstanceUTC());

        singleElementFile = "/org/opensaml/saml/saml1/impl/singleAuthenticationStatement.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml1/impl/singleAuthenticationStatementAttributes.xml";
        childElementsFile = "/org/opensaml/saml/saml1/impl/AuthenticationStatementWithChildren.xml";
        
        qname = new QName(SAMLConstants.SAML1_NS, AuthenticationStatement.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementUnmarshall() {
        AuthenticationStatement authenticationStatement = (AuthenticationStatement) unmarshallElement(singleElementFile);

        Assert.assertNull(authenticationStatement.getAuthenticationMethod(), "AuthenticationMethod attribute present");
        Assert.assertNull(authenticationStatement.getAuthenticationInstant(), "AuthenticationInstant attribute present");

        Assert.assertNull(authenticationStatement.getSubject(), "<Subject> element present");
        Assert.assertNull(authenticationStatement.getSubjectLocality(), "<SubjectLocailty> element present");
        Assert.assertEquals(authenticationStatement.getAuthorityBindings().size(), 0, "Non zero count of <AuthorityBinding> elements");
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        AuthenticationStatement authenticationStatement = (AuthenticationStatement) unmarshallElement(singleElementOptionalAttributesFile);

        Assert.assertEquals(authenticationStatement
                .getAuthenticationMethod(), expectedAuthenticationMethod, "AuthenticationMethod");
        Assert.assertEquals(authenticationStatement.getAuthenticationInstant(), expectedAuthenticationInstant, "AuthenticationInstant");
    }

    /**
     * Test an XML file with children
     */

    @Test
    public void testChildElementsUnmarshall() {
        AuthenticationStatement authenticationStatement = (AuthenticationStatement) unmarshallElement(childElementsFile);

        Assert.assertNotNull(authenticationStatement.getSubject(), "<Subject> element not present");

        Assert.assertNotNull(authenticationStatement.getSubjectLocality(), "<SubjectLocality> element not present");
        Assert.assertNotNull(authenticationStatement.getAuthorityBindings(), "<AuthorityBinding> elements not present");
        Assert.assertEquals(authenticationStatement.getAuthorityBindings().size(), 2, "count of <AuthorityBinding> elements");

        AuthorityBinding authorityBinding = authenticationStatement.getAuthorityBindings().get(0);
        authenticationStatement.getAuthorityBindings().remove(authorityBinding);
        Assert.assertEquals(authenticationStatement.getAuthorityBindings().size(), 1, "count of <AuthorityBinding> elements");
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementMarshall() {
        assertXMLEquals(expectedDOM, buildXMLObject(qname));
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        AuthenticationStatement authenticationStatement = (AuthenticationStatement) buildXMLObject(qname);

        authenticationStatement.setAuthenticationInstant(expectedAuthenticationInstant);
        authenticationStatement.setAuthenticationMethod(expectedAuthenticationMethod);
        assertXMLEquals(expectedOptionalAttributesDOM, authenticationStatement);
    }

    /** {@inheritDoc} */

    @Test
    public void testChildElementsMarshall() {
        AuthenticationStatement authenticationStatement = (AuthenticationStatement) buildXMLObject(qname);

        authenticationStatement.setSubject((Subject) buildXMLObject(new QName(SAMLConstants.SAML1_NS, Subject.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX)));

        authenticationStatement.setSubjectLocality((SubjectLocality) buildXMLObject(new QName(SAMLConstants.SAML1_NS, SubjectLocality.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX)));
        QName authQname = new QName(SAMLConstants.SAML1_NS, AuthorityBinding.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
        authenticationStatement.getAuthorityBindings().add((AuthorityBinding) buildXMLObject(authQname));
        authenticationStatement.getAuthorityBindings().add((AuthorityBinding) buildXMLObject(authQname));

        assertXMLEquals(expectedChildElementsDOM, authenticationStatement);
    }
}
