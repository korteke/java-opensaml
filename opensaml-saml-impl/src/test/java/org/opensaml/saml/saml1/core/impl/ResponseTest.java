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

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml1.core.Assertion;
import org.opensaml.saml.saml1.core.Response;
import org.opensaml.saml.saml1.core.Status;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Test class for org.opensaml.saml.saml1.core.Response
 */
public class ResponseTest extends XMLObjectProviderBaseTestCase {

    /** name used to generate objects */
    private final QName qname;

    /** Representation of IssueInstant in test file. */
    private final String expectedID;

    /** Representation of IssueInstant in test file. */
    private final DateTime expectedIssueInstant;

    /** Representation of InResponseTo in test file. */
    private final String expectedInResponseTo;

    /** Representation of MinorVersion in test file. */
    private final int expectedMinorVersion;

    /** Representation of Recipient in test file. */
    private final String expectedRecipient;

    /**
     * Constructor
     */
    public ResponseTest() {
        expectedID = "ident";
        singleElementFile = "/data/org/opensaml/saml/saml1/impl/singleResponse.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml/saml1/impl/singleResponseAttributes.xml";
        childElementsFile = "/data/org/opensaml/saml/saml1/impl/ResponseWithChildren.xml";
        //
        // IssueInstant="1970-01-01T00:00:00.100Z"
        //
        expectedIssueInstant = new DateTime(1970, 1, 1, 0, 0, 0, 100, ISOChronology.getInstanceUTC());

        expectedInResponseTo="inresponseto";
        expectedMinorVersion=1;
        expectedRecipient="recipient";
        
        qname = Response.DEFAULT_ELEMENT_NAME;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {

        Response response = (Response) unmarshallElement(singleElementFile);

        String id = response.getID();
        AssertJUnit.assertNull("ID attribute has value " + id + "expected no value", id);
       
        AssertJUnit.assertNull("IssueInstant attribute has a value of " + response.getIssueInstant() 
                + ", expected no value", response.getIssueInstant());

        AssertJUnit.assertEquals("Assertion elements count", 0, response.getAssertions().size());

        Status status;
        status = response.getStatus();
        AssertJUnit.assertNull("Status element has a value of " + status + ", expected no value", status);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        Response response;
        response = (Response) unmarshallElement(singleElementOptionalAttributesFile);

        AssertJUnit.assertEquals("ID", expectedID, response.getID());
        AssertJUnit.assertEquals("IssueInstant attribute ", expectedIssueInstant, response.getIssueInstant());

        String string = response.getInResponseTo();
        AssertJUnit.assertEquals("InResponseTo attribute ", expectedInResponseTo, string);

        string = response.getRecipient();
        AssertJUnit.assertEquals("Recipient attribute ", expectedRecipient, string);

        int i = response.getVersion().getMinorVersion();
        AssertJUnit.assertEquals("MinorVersion attribute ", expectedMinorVersion, i);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        Response response = (Response) unmarshallElement(childElementsFile);

        AssertJUnit.assertEquals("No Assertion elements count", 1, response.getAssertions().size());

        Status status;
        status = response.getStatus();
        AssertJUnit.assertNotNull("No Status element found", status);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        assertXMLEquals(expectedDOM, buildXMLObject(qname));
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        Response response = (Response) buildXMLObject(qname);

        response.setID(expectedID);
        response.setInResponseTo(expectedInResponseTo);
        response.setIssueInstant(expectedIssueInstant);
        response.setRecipient(expectedRecipient);

        assertXMLEquals(expectedOptionalAttributesDOM, response);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        Response response = (Response) buildXMLObject(qname);

        response.getAssertions().add((Assertion) buildXMLObject(Assertion.DEFAULT_ELEMENT_NAME));
        response.setStatus((Status)buildXMLObject(Status.DEFAULT_ELEMENT_NAME));

        assertXMLEquals(expectedChildElementsDOM, response);

    }
    
    @Test
    public void testSignatureUnmarshall() {
        Response response = (Response) unmarshallElement("/data/org/opensaml/saml/saml1/impl/ResponseWithSignature.xml");
        
        AssertJUnit.assertNotNull("Response was null", response);
        AssertJUnit.assertNotNull("Signature was null", response.getSignature());
        AssertJUnit.assertNotNull("KeyInfo was null", response.getSignature().getKeyInfo());
    }
    
    @Test
    public void testDOMIDResolutionUnmarshall() {
        Response response = (Response) unmarshallElement("/data/org/opensaml/saml/saml1/impl/ResponseWithSignature.xml");
        
        AssertJUnit.assertNotNull("Response was null", response);
        AssertJUnit.assertNotNull("Signature was null", response.getSignature());
        Document document = response.getSignature().getDOM().getOwnerDocument();
        Element idElem = response.getDOM();
        
        AssertJUnit.assertNotNull("DOM ID resolution returned null", document.getElementById(expectedID));
        AssertJUnit.assertTrue("DOM elements were not equal", idElem.isSameNode(document.getElementById(expectedID)));
    }

    @Test
    public void testDOMIDResolutionMarshall() throws MarshallingException {
        Response response = (Response) buildXMLObject(Response.DEFAULT_ELEMENT_NAME);
        response.setID(expectedID);
        response.setStatus((Status) buildXMLObject(Status.DEFAULT_ELEMENT_NAME));
        
        marshallerFactory.getMarshaller(response).marshall(response);
        
        Document document = response.getStatus().getDOM().getOwnerDocument();
        Element idElem = response.getDOM();
        
        AssertJUnit.assertNotNull("DOM ID resolution returned null", document.getElementById(expectedID));
        AssertJUnit.assertTrue("DOM elements were not equal", idElem.isSameNode(document.getElementById(expectedID)));
    }

}
