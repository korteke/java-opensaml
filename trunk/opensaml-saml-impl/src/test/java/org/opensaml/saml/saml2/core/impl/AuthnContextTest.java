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

import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthenticatingAuthority;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnContextDecl;
import org.opensaml.saml.saml2.core.AuthnContextDeclRef;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.saml.saml2.core.impl.AuthnContextImpl}.
 */
public class AuthnContextTest extends XMLObjectProviderBaseTestCase {

    /** Count of AuthenticatingAuthority subelements */
    protected int expectedAuthenticatingAuthorityCount = 2;

    /** Constructor */
    public AuthnContextTest() {
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/AuthnContext.xml";
        childElementsFile = "/org/opensaml/saml/saml2/core/impl/AuthnContextChildElements.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        AuthnContext authnContext = (AuthnContext) unmarshallElement(singleElementFile);

        Assert.assertNotNull(authnContext);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        QName qname = new QName(SAMLConstants.SAML20_NS, AuthnContext.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        AuthnContext authnContext = (AuthnContext) buildXMLObject(qname);

        assertXMLEquals(expectedDOM, authnContext);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        AuthnContext authnContext = (AuthnContext) unmarshallElement(childElementsFile);

        Assert.assertNotNull(authnContext.getAuthnContextClassRef(), "AuthnContextClassRef element not present");
        Assert.assertNotNull(authnContext.getAuthContextDecl(), "AuthnContextDecl element not present");
        Assert.assertNotNull(authnContext.getAuthnContextDeclRef(), "AuthnContextDeclRef element not present");
        Assert.assertEquals(authnContext
                .getAuthenticatingAuthorities().size(), expectedAuthenticatingAuthorityCount, "AuthenticatingAuthorityCount Count");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        QName qname = new QName(SAMLConstants.SAML20_NS, AuthnContext.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        AuthnContext authnContext = (AuthnContext) buildXMLObject(qname);

        QName authnContextClassRefQName = new QName(SAMLConstants.SAML20_NS, AuthnContextClassRef.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        authnContext.setAuthnContextClassRef((AuthnContextClassRef) buildXMLObject(authnContextClassRefQName));
        
        QName authnContextDeclQName = new QName(SAMLConstants.SAML20_NS, AuthnContextDecl.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        authnContext.setAuthnContextDecl((AuthnContextDecl) buildXMLObject(authnContextDeclQName));
        
        QName authnContextDeclRefQName = new QName(SAMLConstants.SAML20_NS, AuthnContextDeclRef.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        authnContext.setAuthnContextDeclRef((AuthnContextDeclRef) buildXMLObject(authnContextDeclRefQName));
        
        QName authenticatingAuthorityQName = new QName(SAMLConstants.SAML20_NS, AuthenticatingAuthority.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        for (int i = 0; i < expectedAuthenticatingAuthorityCount; i++) {
            authnContext.getAuthenticatingAuthorities().add((AuthenticatingAuthority) buildXMLObject(authenticatingAuthorityQName));
        }

        assertXMLEquals(expectedChildElementsDOM, authnContext);
    }
}