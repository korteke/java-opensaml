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
import org.testng.AssertJUnit;
import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.saml.common.BaseComplexSAMLObjectTestCase;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.AttributeValue;
import org.opensaml.saml.saml2.core.Audience;
import org.opensaml.saml.saml2.core.AudienceRestriction;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;

/**
 * Tests unmarshalling and marshalling for various response messages.
 */
public class ResponseSuccessAuthnAttribTest extends BaseComplexSAMLObjectTestCase {
    
    /**
     * Constructor
     */
    public ResponseSuccessAuthnAttribTest(){
        elementFile = "/data/org/opensaml/saml/saml2/core/ResponseSuccessAuthnAttrib.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testUnmarshall() {
        Response response = (Response) unmarshallElement(elementFile);
        
        AssertJUnit.assertNotNull("Response was null", response);
        AssertJUnit.assertEquals("Response ID", "_c7055387-af61-4fce-8b98-e2927324b306", response.getID());
        AssertJUnit.assertEquals("InResponseTo", "_abcdef123456", response.getInResponseTo());
        AssertJUnit.assertEquals("Version", SAMLVersion.VERSION_20.toString(), response.getVersion().toString());
        AssertJUnit.assertEquals("IssueInstant", new DateTime(2006, 1, 26, 13, 35, 5, 0, ISOChronology.getInstanceUTC()), response.getIssueInstant());
        AssertJUnit.assertEquals("Issuer/@Format", "urn:oasis:names:tc:SAML:2.0:nameid-format:entity", response.getIssuer().getFormat());
        AssertJUnit.assertEquals("Status/Statuscode/@Value", "urn:oasis:names:tc:SAML:2.0:status:Success", response.getStatus().getStatusCode().getValue());
        
        Assertion assertion = (Assertion) response.getAssertions().get(0);
        AssertJUnit.assertNotNull("Assertion[0] was null", assertion);
        AssertJUnit.assertEquals("Assertion ID", "_a75adf55-01d7-40cc-929f-dbd8372ebdfc", assertion.getID());
        AssertJUnit.assertEquals("Assertion/@IssueInstant", new DateTime(2006, 1, 26, 13, 35, 5, 0, ISOChronology.getInstanceUTC()), assertion.getIssueInstant());
        AssertJUnit.assertEquals("Assertion/@Version", SAMLVersion.VERSION_20.toString(), assertion.getVersion().toString());
        AssertJUnit.assertEquals("Assertion/Issuer/@Format", "urn:oasis:names:tc:SAML:2.0:nameid-format:entity", assertion.getIssuer().getFormat());
        AssertJUnit.assertEquals("Assertion/Subject/NameID/@Format", "urn:oasis:names:tc:SAML:2.0:nameid-format:transient", assertion.getSubject().getNameID().getFormat());
        AssertJUnit.assertEquals("Assertion/Subject/NameID contents", "_820d2843-2342-8236-ad28-8ac94fb3e6a1", assertion.getSubject().getNameID().getValue());
        SubjectConfirmation sc = assertion.getSubject().getSubjectConfirmations().get(0);
        AssertJUnit.assertEquals("Assertion/Subject/SubjectConfirmation/@Method", "urn:oasis:names:tc:SAML:2.0:cm:bearer", sc.getMethod());
        AssertJUnit.assertEquals("Assertion/Condition/@NotBefore", new DateTime(2006, 1, 26, 13, 35, 5, 0, ISOChronology.getInstanceUTC()), assertion.getConditions().getNotBefore());
        AssertJUnit.assertEquals("Assertion/Condition/@NotOnOrAfter", new DateTime(2006, 1, 26, 13, 45, 5, 0, ISOChronology.getInstanceUTC()), assertion.getConditions().getNotOnOrAfter());
        Audience audience = (Audience) assertion.getConditions().getAudienceRestrictions().get(0).getAudiences().get(0);
        AssertJUnit.assertEquals("Assertion/Conditions/AudienceRestriction/Audience contents", "https://sp.example.org", audience.getAudienceURI());
        
        AuthnStatement authnStatement = (AuthnStatement) assertion.getAuthnStatements().get(0);
        AssertJUnit.assertEquals("Assertion/AuthnStatement/@AuthnInstant", new DateTime(2006, 1, 26, 13, 35, 5, 0, ISOChronology.getInstanceUTC()), authnStatement.getAuthnInstant());
        AssertJUnit.assertEquals("Assertion/AuthnStatement/AuthnContext/AuthnContextClassRef contents", "urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport", authnStatement.getAuthnContext().getAuthnContextClassRef().getAuthnContextClassRef());
        
        AttributeStatement  attribStatement = (AttributeStatement) assertion.getAttributeStatements().get(0);
        Attribute attrib = null;
        XSString value = null;
        
        attrib = attribStatement.getAttributes().get(0);
        AssertJUnit.assertEquals("Attribute/@FriendlyName", "fooAttrib", attrib.getFriendlyName());
        AssertJUnit.assertEquals("Attribute/@Name", "urn:foo:attrib", attrib.getName());
        AssertJUnit.assertEquals("Attribute/@NameFormat", "urn:oasis:names:tc:SAML:2.0:attrname-format:uri", attrib.getNameFormat());
        AssertJUnit.assertEquals("Number of fooAttrib AttributeValues", 2, attrib.getAttributeValues().size());
        value = (XSString) attrib.getAttributeValues().get(0);
        AssertJUnit.assertEquals("Attribute content", "SomeValue", value.getValue());
        value = (XSString) attrib.getAttributeValues().get(1);
        AssertJUnit.assertEquals("Attribute content", "SomeOtherValue", value.getValue());
        
        attrib = attribStatement.getAttributes().get(1);
        AssertJUnit.assertEquals("Attribute/@FriendlyName", "eduPersonPrincipalName", attrib.getFriendlyName());
        AssertJUnit.assertEquals("Attribute/@Name", "urn:oid:1.3.6.1.4.1.5923.1.1.1.6", attrib.getName());
        AssertJUnit.assertEquals("Attribute/@NameFormat", "urn:oasis:names:tc:SAML:2.0:attrname-format:uri", attrib.getNameFormat());
        AssertJUnit.assertEquals("Number of ldapAttrib AttributeValues", 1, attrib.getAttributeValues().size());
        value = (XSString) attrib.getAttributeValues().get(0);
        AssertJUnit.assertEquals("Attribute content", "j.doe@idp.example.org", value.getValue());
    }

    /** {@inheritDoc} */
    @Test
    public void testMarshall(){
        Response response = (Response) buildXMLObject(Response.DEFAULT_ELEMENT_NAME);
        response.setID("_c7055387-af61-4fce-8b98-e2927324b306");
        response.setInResponseTo("_abcdef123456");
        response.setIssueInstant(new DateTime(2006, 1, 26, 13, 35, 5, 0, ISOChronology.getInstanceUTC()));
        
        Issuer rIssuer = (Issuer) buildXMLObject(Issuer.DEFAULT_ELEMENT_NAME);
        rIssuer.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:entity");
        rIssuer.setValue("https://idp.example.org");
        
        Status status = (Status) buildXMLObject(Status.DEFAULT_ELEMENT_NAME);
        StatusCode statusCode = (StatusCode) buildXMLObject(StatusCode.DEFAULT_ELEMENT_NAME);
        statusCode.setValue("urn:oasis:names:tc:SAML:2.0:status:Success");
        
        Assertion assertion = (Assertion) buildXMLObject(Assertion.DEFAULT_ELEMENT_NAME);
        assertion.setID("_a75adf55-01d7-40cc-929f-dbd8372ebdfc");
        assertion.setIssueInstant(new DateTime(2006, 1, 26, 13, 35, 5, 0, ISOChronology.getInstanceUTC()));
        
        Issuer aIssuer = (Issuer) buildXMLObject(Issuer.DEFAULT_ELEMENT_NAME);
        aIssuer.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:entity");
        aIssuer.setValue("https://idp.example.org");
        
        Subject subject = (Subject) buildXMLObject(Subject.DEFAULT_ELEMENT_NAME);
        NameID nameID = (NameID) buildXMLObject(NameID.DEFAULT_ELEMENT_NAME);
        nameID.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:transient");
        nameID.setValue("_820d2843-2342-8236-ad28-8ac94fb3e6a1");
        
        SubjectConfirmation subjectConfirmation = (SubjectConfirmation) buildXMLObject(SubjectConfirmation.DEFAULT_ELEMENT_NAME);
        subjectConfirmation.setMethod("urn:oasis:names:tc:SAML:2.0:cm:bearer");
        
        Conditions conditions = (Conditions) buildXMLObject(Conditions.DEFAULT_ELEMENT_NAME);
        conditions.setNotBefore(new DateTime(2006, 1, 26, 13, 35, 5, 0, ISOChronology.getInstanceUTC()));
        conditions.setNotOnOrAfter(new DateTime(2006, 1, 26, 13, 45, 5, 0, ISOChronology.getInstanceUTC()));
        
        AudienceRestriction audienceRestriction = (AudienceRestriction) buildXMLObject(AudienceRestriction.DEFAULT_ELEMENT_NAME);
        Audience audience = (Audience) buildXMLObject(Audience.DEFAULT_ELEMENT_NAME);
        audience.setAudienceURI("https://sp.example.org");
        
        AuthnStatement authnStatement = (AuthnStatement) buildXMLObject(AuthnStatement.DEFAULT_ELEMENT_NAME);
        authnStatement.setAuthnInstant(new DateTime(2006, 1, 26, 13, 35, 5, 0, ISOChronology.getInstanceUTC()));
        
        AuthnContext authnContext = (AuthnContext) buildXMLObject(AuthnContext.DEFAULT_ELEMENT_NAME);
        AuthnContextClassRef classRef = (AuthnContextClassRef) buildXMLObject(AuthnContextClassRef.DEFAULT_ELEMENT_NAME);
        classRef.setAuthnContextClassRef("urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport");
        
        AttributeStatement attribStatement = (AttributeStatement) buildXMLObject(AttributeStatement.DEFAULT_ELEMENT_NAME);
        XMLObjectBuilder stringBuilder = builderFactory.getBuilder(XSString.TYPE_NAME);
        
        Attribute fooAttrib = (Attribute) buildXMLObject(Attribute.DEFAULT_ELEMENT_NAME);
        fooAttrib.setFriendlyName("fooAttrib");
        fooAttrib.setName("urn:foo:attrib");
        fooAttrib.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");
        XSString fooAttribValue = null;
        fooAttribValue = (XSString) stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
        fooAttribValue.setValue("SomeValue");
        fooAttrib.getAttributeValues().add(fooAttribValue);
        fooAttribValue = (XSString) stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
        fooAttribValue.setValue("SomeOtherValue");
        fooAttrib.getAttributeValues().add(fooAttribValue);
        
        Attribute ldapAttrib = (Attribute) buildXMLObject(Attribute.DEFAULT_ELEMENT_NAME);
        ldapAttrib.setFriendlyName("eduPersonPrincipalName");
        ldapAttrib.setName("urn:oid:1.3.6.1.4.1.5923.1.1.1.6");
        ldapAttrib.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");
        XSString ldapAttribValue = (XSString) stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
        ldapAttribValue.setValue("j.doe@idp.example.org");
        ldapAttrib.getAttributeValues().add(ldapAttribValue);
        
        response.setIssuer(rIssuer);
        status.setStatusCode(statusCode);
        response.setStatus(status);
        response.getAssertions().add(assertion);
        
        assertion.setIssuer(aIssuer);
        subject.setNameID(nameID);
        subject.getSubjectConfirmations().add(subjectConfirmation);
        assertion.setSubject(subject);
        
        audienceRestriction.getAudiences().add(audience);
        conditions.getAudienceRestrictions().add(audienceRestriction);
        assertion.setConditions(conditions);
        
        authnContext.setAuthnContextClassRef(classRef);
        authnStatement.setAuthnContext(authnContext);
        assertion.getAuthnStatements().add(authnStatement);
        
        attribStatement.getAttributes().add(fooAttrib);
        attribStatement.getAttributes().add(ldapAttrib);
        assertion.getAttributeStatements().add(attribStatement);

        assertXMLEquals("Marshalled Response was not the expected value", expectedDOM, response);
    }
    
}