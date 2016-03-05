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
import org.opensaml.saml.saml2.core.AuthnContextClassRef;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.saml.saml2.core.impl.AuthnContextClassRefImpl}.
 */
public class AuthnContextClassRefTest extends XMLObjectProviderBaseTestCase {

    /** Expected Class Reference value */
    protected String expectedClassRef;

    /** Constructor */
    public AuthnContextClassRefTest() {
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/AuthnContextClassRef.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedClassRef = "class reference";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        AuthnContextClassRef authnContextClassRef = (AuthnContextClassRef) unmarshallElement(singleElementFile);

        String classRef = authnContextClassRef.getAuthnContextClassRef();
        Assert.assertEquals(classRef, expectedClassRef, "Class Reference was " + classRef + ", expected " + expectedClassRef);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        QName qname = new QName(SAMLConstants.SAML20_NS, AuthnContextClassRef.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        AuthnContextClassRef authnContextClassRef = (AuthnContextClassRef) buildXMLObject(qname);

        authnContextClassRef.setAuthnContextClassRef(expectedClassRef);
        assertXMLEquals(expectedDOM, authnContextClassRef);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        // do nothing
    }
}