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

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml1.core.AuthenticationQuery;
import org.opensaml.saml.saml1.core.Subject;

/**
 * Test class for org.opensaml.saml.saml1.core.AuthenticationQuery
 */
public class AuthenticationQueryTest extends XMLObjectProviderBaseTestCase {

    /** name used to generate objects */
    private final QName qname;

    private final String expectedAuthenticationMethod;

    /**
     * Constructor
     */
    public AuthenticationQueryTest() {
        singleElementFile = "/data/org/opensaml/saml/saml1/impl/singleAuthenticationQuery.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml/saml1/impl/singleAuthenticationQueryAttributes.xml";
        childElementsFile = "/data/org/opensaml/saml/saml1/impl/AuthenticationQueryWithChildren.xml";
        expectedAuthenticationMethod = "Trust Me";
        qname = new QName(SAMLConstants.SAML10P_NS, AuthenticationQuery.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1P_PREFIX);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {

        AuthenticationQuery authenticationQuery;
        
        authenticationQuery = (AuthenticationQuery) unmarshallElement(singleElementFile);

        AssertJUnit.assertNull("AuthenticationQuery attribute present", authenticationQuery.getAuthenticationMethod());;
        AssertJUnit.assertNull("Subject element present", authenticationQuery.getSubject());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        AuthenticationQuery authenticationQuery;
        
        authenticationQuery = (AuthenticationQuery) unmarshallElement(singleElementOptionalAttributesFile);

        AssertJUnit.assertEquals("AuthenticationQuery attribute", expectedAuthenticationMethod, authenticationQuery.getAuthenticationMethod());;
        AssertJUnit.assertNull("Subject element present", authenticationQuery.getSubject());
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        AuthenticationQuery authenticationQuery;
        
        authenticationQuery = (AuthenticationQuery) unmarshallElement(childElementsFile);

        AssertJUnit.assertNotNull("No Subject element found", authenticationQuery.getSubject());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        assertXMLEquals(expectedDOM, buildXMLObject(qname));
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        AuthenticationQuery authenticationQuery = (AuthenticationQuery) buildXMLObject(qname);

        authenticationQuery.setAuthenticationMethod(expectedAuthenticationMethod);
        assertXMLEquals(expectedOptionalAttributesDOM, authenticationQuery);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        AuthenticationQuery authenticationQuery = (AuthenticationQuery) buildXMLObject(qname);

        authenticationQuery.setSubject((Subject) buildXMLObject(new QName(SAMLConstants.SAML1_NS, Subject.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX)));
        assertXMLEquals(expectedChildElementsDOM, authenticationQuery);

    }

}
