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
import org.opensaml.saml.saml2.core.AuthnContextDecl;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.saml.saml2.core.impl.AuthnContextDeclImpl}.
 */
public class AuthnContextDeclTest extends XMLObjectProviderBaseTestCase {

    /** Expected Declaration value. */
    protected String expectedDeclartion;

    /** Constructor. */
    public AuthnContextDeclTest() {
        singleElementFile = "/data/org/opensaml/saml/saml2/core/impl/AuthnContextDecl.xml";
    }

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedDeclartion = "declaration";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        AuthnContextDecl authnContextDecl = (AuthnContextDecl) unmarshallElement(singleElementFile);

        String declaration = authnContextDecl.getTextContent();
        Assert.assertEquals(declaration, expectedDeclartion,
                "Declartion was " + declaration + ", expected " + expectedDeclartion);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        QName qname = new QName(SAMLConstants.SAML20_NS, AuthnContextDecl.DEFAULT_ELEMENT_LOCAL_NAME,
                SAMLConstants.SAML20_PREFIX);
        AuthnContextDecl authnContextDecl = (AuthnContextDecl) buildXMLObject(qname);

        authnContextDecl.setTextContent(expectedDeclartion);
        assertXMLEquals(expectedDOM, authnContextDecl);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        // do nothing
    }
}