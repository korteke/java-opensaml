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

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusResponseType;

/**
 *
 */
public abstract class StatusResponseTestBase extends XMLObjectProviderBaseTestCase {
    
    /** Expected ID attribute */
    protected String expectedID;
    
    /** Expected InResponseTo attribute */
    protected String expectedInResponseTo;
    
    /** Expected Version attribute */
    protected SAMLVersion expectedSAMLVersion;
    
    /** Expected IssueInstant attribute */
    protected DateTime expectedIssueInstant;
    
    /** Expected Destination attribute */
    protected String expectedDestination;
    
    /** Expected Consent attribute */
    protected String expectedConsent;
    
    /** Expected Issuer child element */
    protected Issuer expectedIssuer;
    
    /** Expected Status child element */
    protected Status expectedStatus;

    /**
     * Constructor
     *
     */
    public StatusResponseTestBase() {
        
    }

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedID = "def456";
        expectedInResponseTo = "abc123";
        expectedSAMLVersion = SAMLVersion.VERSION_20;
        expectedIssueInstant = new DateTime(2006, 2, 21, 16, 40, 0, 0, ISOChronology.getInstanceUTC());
        expectedDestination = "http://sp.example.org/endpoint";
        expectedConsent = "urn:string:consent";
        
        QName issuerQName = new QName(SAMLConstants.SAML20_NS, Issuer.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        expectedIssuer = (Issuer) buildXMLObject(issuerQName);
        
        QName statusQName = new QName(SAMLConstants.SAML20P_NS, Status.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        expectedStatus = (Status) buildXMLObject(statusQName);
    }

    /** {@inheritDoc} */
    @Test
    public abstract void testSingleElementUnmarshall();

    /** {@inheritDoc} */
    @Test
    public abstract void testSingleElementMarshall();
    
    
    /**
     * Used by subclasses to populate the required attribute values
     * that this test expects.
     * 
     * @param samlObject
     */
    protected void populateRequiredAttributes(SAMLObject samlObject) {
        StatusResponseType sr = (StatusResponseType) samlObject;
        
        sr.setID(expectedID);
        sr.setIssueInstant(expectedIssueInstant);
        // NOTE:  the SAML Version attribute is set automatically by the impl superclas
        
    }
    
    /**
     * Used by subclasses to populate the optional attribute values
     * that this test expects. 
     * 
     * @param samlObject
     */
    protected void populateOptionalAttributes(SAMLObject samlObject) {
        StatusResponseType sr = (StatusResponseType) samlObject;
        
        sr.setInResponseTo(expectedInResponseTo);
        sr.setConsent(expectedConsent);
        sr.setDestination(expectedDestination);
        
    }
    
    /**
     * Used by subclasses to populate the child elements that this test expects.
     * 
     * 
     * @param samlObject
     */
    protected void populateChildElements(SAMLObject samlObject) {
        StatusResponseType sr = (StatusResponseType) samlObject;
        
        sr.setIssuer(expectedIssuer);
        sr.setStatus(expectedStatus);
        
    }
    
    protected void helperTestSingleElementUnmarshall(SAMLObject samlObject) {
        StatusResponseType sr = (StatusResponseType) samlObject;
        
        AssertJUnit.assertEquals("Unmarshalled ID attribute was not the expected value", expectedID, sr.getID());
        AssertJUnit.assertEquals("Unmarshalled Version attribute was not the expected value", expectedSAMLVersion.toString(), sr.getVersion().toString());
        AssertJUnit.assertEquals("Unmarshalled IssueInstant attribute was not the expected value", 0, expectedIssueInstant.compareTo(sr.getIssueInstant()));
        
        AssertJUnit.assertNull("InResponseTo was not null", sr.getInResponseTo());
        AssertJUnit.assertNull("Consent was not null", sr.getConsent());
        AssertJUnit.assertNull("Destination was not null", sr.getDestination());
        
    }
    
    protected void helperTestSingleElementOptionalAttributesUnmarshall(SAMLObject samlObject) {
        StatusResponseType sr = (StatusResponseType) samlObject;
        
        AssertJUnit.assertEquals("Unmarshalled ID attribute was not the expected value", expectedID, sr.getID());
        AssertJUnit.assertEquals("Unmarshalled Version attribute was not the expected value", expectedSAMLVersion.toString(), sr.getVersion().toString());
        AssertJUnit.assertEquals("Unmarshalled IssueInstant attribute was not the expected value", 0, expectedIssueInstant.compareTo(sr.getIssueInstant()));
        
        AssertJUnit.assertEquals("Unmarshalled InResponseTo attribute was not the expected value", expectedInResponseTo, sr.getInResponseTo());
        AssertJUnit.assertEquals("Unmarshalled Consent attribute was not the expected value", expectedConsent, sr.getConsent());
        AssertJUnit.assertEquals("Unmarshalled Destination attribute was not the expected value", expectedDestination, sr.getDestination());
        
    }

    protected void helperTestChildElementsUnmarshall(SAMLObject samlObject) {
        StatusResponseType sr = (StatusResponseType) samlObject;
        
        AssertJUnit.assertNotNull("Issuer was null", sr.getIssuer());
        AssertJUnit.assertNotNull("Status was null", sr.getIssuer());
    }

}
