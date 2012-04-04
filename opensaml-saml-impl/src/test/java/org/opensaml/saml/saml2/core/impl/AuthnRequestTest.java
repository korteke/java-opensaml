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
package org.opensaml.saml.saml2.core.impl;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.AssertJUnit;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.NameIDPolicy;
import org.opensaml.saml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml.saml2.core.Scoping;
import org.opensaml.saml.saml2.core.Subject;

/**
 * Unit test for {@link AuthnRequest}.
 */
public class AuthnRequestTest extends RequestTestBase {
    
            
        /** Expected ForceAuthn attribute */    
        private XSBooleanValue expectedForceAuthn;
        
        /** Expected IsPassive attribute */    
        private XSBooleanValue expectedIsPassive;
        
        /** Expected ProtocolBinding attribute */    
        private String expectedProtocolBinding;
        
        /** Expected AssertionConsumerServiceIndex attribute */    
        private Integer expectedAssertionConsumerServiceIndex;
        
        /** Expected AssertionConsumerServiceURL attribute */    
        private String expectedAssertionConsumerServiceURL;
        
        /** Expected AttributeConsumingServiceIndex attribute */    
        private Integer expectedAttributeConsumingServiceIndex;
        
        /** Expected ProviderName attribute */    
        private String expectedProviderName;

    /**
     * Constructor
     *
     */
    public AuthnRequestTest() {
        super();
        
        singleElementFile = "/data/org/opensaml/saml/saml2/core/impl/AuthnRequest.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml/saml2/core/impl/AuthnRequestOptionalAttributes.xml";
        childElementsFile = "/data/org/opensaml/saml/saml2/core/impl/AuthnRequestChildElements.xml";
    }
    

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        
        expectedForceAuthn = new XSBooleanValue(Boolean.TRUE, false);
        expectedIsPassive = new XSBooleanValue(Boolean.TRUE, false);
        expectedProtocolBinding = "urn:string:protocol-binding";
        expectedAssertionConsumerServiceIndex = new Integer(3);
        expectedAssertionConsumerServiceURL = "http://sp.example.org/acs";
        expectedAttributeConsumingServiceIndex = new Integer(2);
        expectedProviderName = "Example Org";
    }



    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        QName qname = new QName(SAMLConstants.SAML20P_NS, AuthnRequest.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        AuthnRequest req = (AuthnRequest) buildXMLObject(qname);
        
        super.populateRequiredAttributes(req);
        
        assertXMLEquals(expectedDOM, req);

    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        QName qname = new QName(SAMLConstants.SAML20P_NS, AuthnRequest.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        AuthnRequest req = (AuthnRequest) buildXMLObject(qname);
        
        super.populateRequiredAttributes(req);
        super.populateOptionalAttributes(req);
        
        req.setForceAuthn(expectedForceAuthn);
        req.setIsPassive(expectedIsPassive);
        req.setProtocolBinding(expectedProtocolBinding);
        req.setAssertionConsumerServiceIndex(expectedAssertionConsumerServiceIndex);
        req.setAssertionConsumerServiceURL(expectedAssertionConsumerServiceURL);
        req.setAttributeConsumingServiceIndex(expectedAttributeConsumingServiceIndex);
        req.setProviderName(expectedProviderName);
        
        assertXMLEquals(expectedOptionalAttributesDOM, req);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        QName qname = new QName(SAMLConstants.SAML20P_NS, AuthnRequest.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        AuthnRequest req = (AuthnRequest) buildXMLObject(qname);
        
        super.populateChildElements(req);
        
        QName subjectQName = new QName(SAMLConstants.SAML20_NS, Subject.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        req.setSubject((Subject) buildXMLObject(subjectQName));
        
        QName nameIDPolicyQName = new QName(SAMLConstants.SAML20P_NS, NameIDPolicy.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        req.setNameIDPolicy((NameIDPolicy) buildXMLObject(nameIDPolicyQName));
        
        QName conditionsQName = new QName(SAMLConstants.SAML20_NS, Conditions.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        req.setConditions((Conditions) buildXMLObject(conditionsQName));
        
        QName requestedAuthnContextQName = new QName(SAMLConstants.SAML20P_NS, RequestedAuthnContext.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        req.setRequestedAuthnContext((RequestedAuthnContext) buildXMLObject(requestedAuthnContextQName));
        
        QName scopingQName = new QName(SAMLConstants.SAML20P_NS, Scoping.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        req.setScoping((Scoping) buildXMLObject(scopingQName));
        
        assertXMLEquals(expectedChildElementsDOM, req);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        AuthnRequest req = (AuthnRequest) unmarshallElement(singleElementFile);
        
        AssertJUnit.assertNotNull("AuthnRequest was null", req);
        AssertJUnit.assertEquals("ForceAuthn (empty) was not default value", Boolean.FALSE, req.isForceAuthn());
        AssertJUnit.assertEquals("IsPassive (empty) was not default value", Boolean.FALSE, req.isPassive());
        AssertJUnit.assertNull("ProtocolBinding was not null", req.getProtocolBinding());
        AssertJUnit.assertNull("AssertionConsumerServiceIndex was not null", req.getAssertionConsumerServiceIndex());
        AssertJUnit.assertNull("AssertionConsumerServiceURL was not null", req.getAssertionConsumerServiceURL());
        AssertJUnit.assertNull("AttributeConsumingServiceIndex was not null", req.getAttributeConsumingServiceIndex());
        AssertJUnit.assertNull("ProviderName was not null", req.getProviderName());
        
        super.helperTestSingleElementUnmarshall(req);

    }
 
    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        AuthnRequest req = (AuthnRequest) unmarshallElement(singleElementOptionalAttributesFile);
        
        AssertJUnit.assertEquals("Unmarshalled ForceAuthn was not the expected value", expectedForceAuthn, req.isForceAuthnXSBoolean());
        AssertJUnit.assertEquals("Unmarshalled IsPassive was not the expected value", expectedIsPassive, req.isPassiveXSBoolean());
        AssertJUnit.assertEquals("Unmarshalled ProtocolBinding was not the expected value", expectedProtocolBinding, req.getProtocolBinding());
        AssertJUnit.assertEquals("Unmarshalled AssertionConsumerServiceIndex was not the expected value", expectedAssertionConsumerServiceIndex, req.getAssertionConsumerServiceIndex());
        AssertJUnit.assertEquals("Unmarshalled AssertionConsumerServiceURL was not the expected value", expectedAssertionConsumerServiceURL, req.getAssertionConsumerServiceURL());
        AssertJUnit.assertEquals("Unmarshalled AttributeConsumingServiceIndex was not the expected value", expectedAttributeConsumingServiceIndex, req.getAttributeConsumingServiceIndex());
        AssertJUnit.assertEquals("Unmarshalled ProviderName was not the expected value", expectedProviderName, req.getProviderName());
        
        super.helperTestSingleElementOptionalAttributesUnmarshall(req);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        AuthnRequest req = (AuthnRequest) unmarshallElement(childElementsFile);
        
        AssertJUnit.assertNotNull("Subject was null", req.getSubject());
        AssertJUnit.assertNotNull("NameIDPolicy was null", req.getNameIDPolicy());
        AssertJUnit.assertNotNull("Conditions was null", req.getConditions());
        AssertJUnit.assertNotNull("RequestedAuthnContext was null", req.getRequestedAuthnContext());
        AssertJUnit.assertNotNull("Scoping was null", req.getScoping());
        
        super.helperTestChildElementsUnmarshall(req);
    }
    
    /**
     * Test the proper behavior of the XSBooleanValue attributes.
     */
    @Test
    public void testXSBooleanAttributes() {
        AuthnRequest req = (AuthnRequest) buildXMLObject(AuthnRequest.DEFAULT_ELEMENT_NAME);
        
        // ForceAuthn attribute
        req.setForceAuthn(Boolean.TRUE);
        AssertJUnit.assertEquals("Unexpected value for boolean attribute found", Boolean.TRUE, req.isForceAuthn());
        AssertJUnit.assertNotNull("XSBooleanValue was null", req.isForceAuthnXSBoolean());
        AssertJUnit.assertEquals("XSBooleanValue was unexpected value", new XSBooleanValue(Boolean.TRUE, false),
                req.isForceAuthnXSBoolean());
        AssertJUnit.assertEquals("XSBooleanValue string was unexpected value", "true", req.isForceAuthnXSBoolean().toString());
        
        req.setForceAuthn(Boolean.FALSE);
        AssertJUnit.assertEquals("Unexpected value for boolean attribute found", Boolean.FALSE, req.isForceAuthn());
        AssertJUnit.assertNotNull("XSBooleanValue was null", req.isForceAuthnXSBoolean());
        AssertJUnit.assertEquals("XSBooleanValue was unexpected value", new XSBooleanValue(Boolean.FALSE, false),
                req.isForceAuthnXSBoolean());
        AssertJUnit.assertEquals("XSBooleanValue string was unexpected value", "false", req.isForceAuthnXSBoolean().toString());
        
        req.setForceAuthn((Boolean) null);
        AssertJUnit.assertEquals("Unexpected default value for boolean attribute found", Boolean.FALSE, req.isForceAuthn());
        AssertJUnit.assertNull("XSBooleanValue was not null", req.isForceAuthnXSBoolean());
        
        
        // IsPassive attribute
        req.setIsPassive(Boolean.TRUE);
        AssertJUnit.assertEquals("Unexpected value for boolean attribute found", Boolean.TRUE, req.isPassive());
        AssertJUnit.assertNotNull("XSBooleanValue was null", req.isPassiveXSBoolean());
        AssertJUnit.assertEquals("XSBooleanValue was unexpected value", new XSBooleanValue(Boolean.TRUE, false),
                req.isPassiveXSBoolean());
        AssertJUnit.assertEquals("XSBooleanValue string was unexpected value", "true", req.isPassiveXSBoolean().toString());
        
        req.setIsPassive(Boolean.FALSE);
        AssertJUnit.assertEquals("Unexpected value for boolean attribute found", Boolean.FALSE, req.isPassive());
        AssertJUnit.assertNotNull("XSBooleanValue was null", req.isPassiveXSBoolean());
        AssertJUnit.assertEquals("XSBooleanValue was unexpected value", new XSBooleanValue(Boolean.FALSE, false),
                req.isPassiveXSBoolean());
        AssertJUnit.assertEquals("XSBooleanValue string was unexpected value", "false", req.isPassiveXSBoolean().toString());
        
        req.setIsPassive((Boolean) null);
        AssertJUnit.assertEquals("Unexpected default value for boolean attribute found", Boolean.FALSE, req.isPassive());
        AssertJUnit.assertNull("XSBooleanValue was not null", req.isPassiveXSBoolean());
    }
    
}