/*
 * Copyright [2005] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.saml2.core.impl;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.SAMLObjectBaseTestCase;
import org.opensaml.common.SAMLVersion;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.Request;

/**
 *
 */
public abstract class RequestTest extends SAMLObjectBaseTestCase {
    
    /** Expected ID value */
    protected String expectedID;
    
    /** Expected SAML version */
    protected SAMLVersion expectedSAMLVersion;
    
    /** Expected IssueInstant attribute */
    protected DateTime expectedIssueInstant;

    /** Expected Destination attribute */
    protected String expectedDestination;
    
    /** Expected Consent attribute */
    protected String expectedConsent;
    
    /** Expected Issuer child element */
    protected Issuer expectedIssuer;
    
    //TODO Signature tests ?
    //TODO Extensions tests - need implementation
    
    /**
     * Constructor
     *
     */
    public RequestTest() {
        
    }
    

    /**
     * @see org.opensaml.common.SAMLObjectBaseTestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        expectedID = "abc123";
        expectedSAMLVersion = SAMLVersion.VERSION_20;
        expectedIssueInstant = new DateTime(2006, 2, 21, 16, 40, 0, 0, ISOChronology.getInstanceUTC());
        expectedDestination = "http://idp.example.org/endpoint";
        expectedConsent = "urn:string:consent";
        expectedIssuer = new IssuerImpl();
    }


    /**
     * @see org.opensaml.common.SAMLObjectBaseTestCase#testSingleElementUnmarshall()
     */
    public abstract void testSingleElementUnmarshall();

    /**
     * @see org.opensaml.common.SAMLObjectBaseTestCase#testSingleElementMarshall()
     */
    public abstract void testSingleElementMarshall();
    
    
    /**
     * Used by subclasses to populate the required attribute values
     * that this test expects.
     * 
     * @param samlObject
     */
    protected void populateRequiredAttributes(SAMLObject samlObject) {
        Request req = (Request) samlObject;
        
        req.setID(expectedID);
        req.setIssueInstant(expectedIssueInstant);
        // NOTE:  the SAML Version attribute is set automatically by the impl superclass
          
    }
    
    /**
     * Used by subclasses to populate the optional attribute values
     * that this test expects. 
     * 
     * @param samlObject
     */
    protected void populateOptionalAttributes(SAMLObject samlObject) {
        Request req = (Request) samlObject;
        
        req.setConsent(expectedConsent);
        req.setDestination(expectedDestination);
        
    }
    
    /**
     * Used by subclasses to populate the child elements that this test expects.
     * 
     * 
     * @param samlObject
     */
    protected void populateChildElements(SAMLObject samlObject) {
        Request req = (Request) samlObject;
        
        req.setIssuer(expectedIssuer);
        
    }
    
    protected void helperTestSingleElementUnmarshall(SAMLObject samlObject) {
        Request req = (Request) samlObject;
        
        assertEquals("Unmarshalled ID attribute was not the expected value", expectedID, req.getID());
        //TODO Should SAMLVersion  implement equals() ?
        assertEquals("Unmarshalled Version attribute was not the expected value", expectedSAMLVersion.toString(), req.getVersion().toString());
        assertEquals("Unmarshalled IssueInstant attribute was not the expected value", 0, expectedIssueInstant.compareTo(req.getIssueInstant()));
        
        assertNull("Consent was not null", req.getConsent());
        assertNull("Destination was not null", req.getDestination());
        
    }
    
    protected void helperTestSingleElementOptionalAttributesUnmarshall(SAMLObject samlObject) {
        Request req = (Request) samlObject;
        
        assertEquals("Unmarshalled ID attribute was not the expected value", expectedID, req.getID());
        //TODO Should SAMLVersion  implement equals() ?
        assertEquals("Unmarshalled Version attribute was not the expected value", expectedSAMLVersion.toString(), req.getVersion().toString());
        assertEquals("Unmarshalled IssueInstant attribute was not the expected value", 0, expectedIssueInstant.compareTo(req.getIssueInstant()));
        
        assertEquals("Unmarshalled Consent attribute was not the expected value", expectedConsent, req.getConsent());
        assertEquals("Unmarshalled Destination attribute was not the expected value", expectedDestination, req.getDestination());
        
    }

    protected void helperTestChildElementsUnmarshall(SAMLObject samlObject) {
        Request req = (Request) samlObject;
        
        assertNotNull("Issuer was null", req.getIssuer());
        
    }
}