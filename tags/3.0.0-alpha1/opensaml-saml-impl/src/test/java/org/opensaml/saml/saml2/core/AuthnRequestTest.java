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

package org.opensaml.saml.saml2.core;

import org.testng.annotations.Test;
import org.testng.Assert;
import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.saml.common.BaseComplexSAMLObjectTestCase;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml2.core.Audience;
import org.opensaml.saml.saml2.core.AudienceRestriction;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml.saml2.core.Subject;

/**
 * Tests unmarshalling and marshalling for various request messages.
 */
public class AuthnRequestTest extends BaseComplexSAMLObjectTestCase {

    /**
     * Constructor
     */
    public AuthnRequestTest(){
        elementFile = "/data/org/opensaml/saml/saml2/core/AuthnRequest.xml";
    }
    

    /** {@inheritDoc} */
    @Test
    public void testUnmarshall() {
        AuthnRequest request = (AuthnRequest) unmarshallElement(elementFile);
        
        Assert.assertNotNull(request, "AuthnRequest was null");
        Assert.assertEquals(request.isForceAuthn().booleanValue(), true, "ForceAuthn");
        Assert.assertEquals(request.getAssertionConsumerServiceURL(), "http://www.example.com/", "AssertionConsumerServiceURL");
        Assert.assertEquals(request.getAttributeConsumingServiceIndex().intValue(), 0, "AttributeConsumingServiceIndex");
        Assert.assertEquals(request.getProviderName(), "SomeProvider", "ProviderName");
        Assert.assertEquals(request.getID(), "abe567de6", "ID");
        Assert.assertEquals(request.getVersion().toString(), SAMLVersion.VERSION_20.toString(), "Version");
        Assert.assertEquals(request.getIssueInstant(), new DateTime(2005, 1, 31, 12, 0, 0, 0, ISOChronology.getInstanceUTC()), "IssueInstant");
        Assert.assertEquals(request.getDestination(), "http://www.example.com/", "Destination");
        Assert.assertEquals(request.getConsent(), "urn:oasis:names:tc:SAML:2.0:consent:obtained", "Consent");
        Assert.assertEquals(request.getSubject().getNameID().getFormat(), "urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress", "Subject/NameID/@NameIdFormat");
        Assert.assertEquals(request.getSubject().getNameID().getValue(), "j.doe@company.com", "Subject/NameID contents");
        Audience audience = request.getConditions().getAudienceRestrictions().get(0).getAudiences().get(0);
        Assert.assertEquals(audience.getAudienceURI(), "urn:foo:sp.example.org", "Conditions/AudienceRestriction[1]/Audience[1] contents");
        AuthnContextClassRef classRef = (AuthnContextClassRef) request.getRequestedAuthnContext().getAuthnContextClassRefs().get(0);
        Assert.assertEquals(classRef.getAuthnContextClassRef(), "urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport", "RequestedAuthnContext/AuthnContextClassRef[1] contents");
    }

    /** {@inheritDoc} */
    @Test
    public void testMarshall() {
        NameID nameid = (NameID) buildXMLObject(NameID.DEFAULT_ELEMENT_NAME);
        nameid.setFormat("urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress");
        nameid.setValue("j.doe@company.com");
        
        Subject subject = (Subject) buildXMLObject(Subject.DEFAULT_ELEMENT_NAME);
        subject.setNameID(nameid);
        
        Audience audience = (Audience) buildXMLObject(Audience.DEFAULT_ELEMENT_NAME);
        audience.setAudienceURI("urn:foo:sp.example.org");
        
        AudienceRestriction ar = (AudienceRestriction) buildXMLObject(AudienceRestriction.DEFAULT_ELEMENT_NAME);
        ar.getAudiences().add(audience);
        
        Conditions conditions = (Conditions) buildXMLObject(Conditions.DEFAULT_ELEMENT_NAME);
        conditions.getAudienceRestrictions().add(ar);
        
        AuthnContextClassRef classRef = (AuthnContextClassRef) buildXMLObject(AuthnContextClassRef.DEFAULT_ELEMENT_NAME);
        classRef.setAuthnContextClassRef("urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport");
        
        RequestedAuthnContext rac = (RequestedAuthnContext) buildXMLObject(RequestedAuthnContext.DEFAULT_ELEMENT_NAME);
        rac.getAuthnContextClassRefs().add(classRef);
        
        AuthnRequest request = (AuthnRequest) buildXMLObject(AuthnRequest.DEFAULT_ELEMENT_NAME);
        request.setSubject(subject);
        request.setConditions(conditions);
        request.setRequestedAuthnContext(rac);
        
        request.setForceAuthn(XSBooleanValue.valueOf("true"));
        request.setAssertionConsumerServiceURL("http://www.example.com/");
        request.setAttributeConsumingServiceIndex(0);
        request.setProviderName("SomeProvider");
        request.setID("abe567de6");
        request.setVersion(SAMLVersion.VERSION_20);
        request.setIssueInstant(new DateTime(2005, 1, 31, 12, 0, 0, 0, ISOChronology.getInstanceUTC()));
        request.setDestination("http://www.example.com/");
        request.setConsent("urn:oasis:names:tc:SAML:2.0:consent:obtained");
        
        assertXMLEquals("Marshalled AuthnRequest", expectedDOM, request);
        
        
    }
}