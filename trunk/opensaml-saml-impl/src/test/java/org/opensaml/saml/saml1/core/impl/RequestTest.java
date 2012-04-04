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
import org.testng.Assert;
import org.testng.AssertJUnit;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml1.core.AssertionArtifact;
import org.opensaml.saml.saml1.core.AssertionIDReference;
import org.opensaml.saml.saml1.core.AttributeQuery;
import org.opensaml.saml.saml1.core.Request;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Test in and around the {@link org.opensaml.saml.saml1.core.Request} interface
 */
public class RequestTest extends XMLObjectProviderBaseTestCase {

    /** name used to generate objects */
    private final QName qname;

    private final String expectedID;
    
    private final DateTime expectedIssueInstant;

    private final int expectedMinorVersion;
    
    public RequestTest() {
        expectedID = "ident";
        singleElementFile = "/data/org/opensaml/saml/saml1/impl/singleRequest.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml/saml1/impl/singleRequestAttributes.xml";
        expectedIssueInstant = new DateTime(1970, 1, 1, 0, 0, 0, 100, ISOChronology.getInstanceUTC());
        expectedMinorVersion = 1;
        qname = Request.DEFAULT_ELEMENT_NAME;
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        Request request = (Request) unmarshallElement(singleElementFile);

        String id = request.getID();
        AssertJUnit.assertNull("ID attribute has value " + id + "expected no value", id);
        
        DateTime date = request.getIssueInstant();
        AssertJUnit.assertNull("IssueInstant attribute has a value of " + date + ", expected no value", date);

        AssertJUnit.assertNull("Query has value", request.getQuery());
        AssertJUnit.assertEquals("AssertionArtifact present", 0, request.getAssertionArtifacts().size());
        AssertJUnit.assertEquals("AssertionIDReferences present", 0, request.getAssertionIDReferences().size());
        AssertJUnit.assertNull("IssueInstance has value", request.getIssueInstant());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        Request request = (Request) unmarshallElement(singleElementOptionalAttributesFile);
        
        AssertJUnit.assertEquals("ID", expectedID, request.getID());
        AssertJUnit.assertEquals("MinorVersion", expectedMinorVersion, request.getVersion().getMinorVersion());
        AssertJUnit.assertEquals("IssueInstant", expectedIssueInstant, request.getIssueInstant());
        
    }
    
    /**
     * Test a few Requests with children 
     */
    @Test
    public void testSingleElementChildrenUnmarshall() {
        Request request; 
        
        request = (Request) unmarshallElement("/data/org/opensaml/saml/saml1/impl/RequestWithAssertionArtifact.xml");
        
        AssertJUnit.assertNull("Query is not null", request.getQuery());
        AssertJUnit.assertEquals("AssertionId count", 0, request.getAssertionIDReferences().size());
        AssertJUnit.assertEquals("AssertionArtifact count", 2, request.getAssertionArtifacts().size());
        
        request = (Request) unmarshallElement("/data/org/opensaml/saml/saml1/impl/RequestWithQuery.xml");
        
        AssertJUnit.assertNotNull("Query is null", request.getQuery());
        AssertJUnit.assertEquals("AssertionId count", 0, request.getAssertionIDReferences().size());
        AssertJUnit.assertEquals("AssertionArtifact count", 0, request.getAssertionArtifacts().size());
        
        request = (Request) unmarshallElement("/data/org/opensaml/saml/saml1/impl/RequestWithAssertionIDReference.xml");
        AssertJUnit.assertNull("Query is not null", request.getQuery());
        AssertJUnit.assertNotNull("AssertionId", request.getAssertionIDReferences());
        AssertJUnit.assertEquals("AssertionId count", 3, request.getAssertionIDReferences().size());
        AssertJUnit.assertEquals("AssertionArtifact count", 0, request.getAssertionArtifacts().size());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        assertXMLEquals(expectedDOM, buildXMLObject(qname));
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        
        Request request = (Request) buildXMLObject(qname);

        request.setID(expectedID);
        request.setIssueInstant(expectedIssueInstant);
        assertXMLEquals(expectedOptionalAttributesDOM, request);
    }

    /**
     * Test a few Requests with children 
     */
    @Test
    public void testSingleElementChildrenMarshall() {
        QName oqname;
        Request request; 
        Document dom;
                
        
        try {
            dom = parserPool.parse(this.getClass().getResourceAsStream("/data/org/opensaml/saml/saml1/impl/RequestWithAssertionArtifact.xml")); request = (Request) buildXMLObject(qname); 
            oqname = AssertionArtifact.DEFAULT_ELEMENT_NAME;
            request.getAssertionArtifacts().add((AssertionArtifact) buildXMLObject(oqname));
            request.getAssertionArtifacts().add((AssertionArtifact) buildXMLObject(oqname));
            assertXMLEquals(dom, request);
          
            dom = parserPool.parse(this.getClass().getResourceAsStream("/data/org/opensaml/saml/saml1/impl/RequestWithAssertionIDReference.xml"));
            request = (Request) buildXMLObject(qname); 
            oqname = AssertionIDReference.DEFAULT_ELEMENT_NAME;
            request.getAssertionIDReferences().add((AssertionIDReference) buildXMLObject(oqname));
            request.getAssertionIDReferences().add((AssertionIDReference) buildXMLObject(oqname));
            request.getAssertionIDReferences().add((AssertionIDReference) buildXMLObject(oqname));
            assertXMLEquals(dom, request);

            dom = parserPool.parse(this.getClass().getResourceAsStream("/data/org/opensaml/saml/saml1/impl/RequestWithQuery.xml"));
            request = (Request) buildXMLObject(qname); 
            oqname = AttributeQuery.DEFAULT_ELEMENT_NAME;
            request.setQuery((AttributeQuery) buildXMLObject(oqname));
            assertXMLEquals(dom, request);

        } catch (XMLParserException e) {
            Assert.fail(e.toString());
        }
    }
    
    @Test
    public void testSignatureUnmarshall() {
        Request request = (Request) unmarshallElement("/data/org/opensaml/saml/saml1/impl/RequestWithSignature.xml");
        
        AssertJUnit.assertNotNull("Request was null", request);
        AssertJUnit.assertNotNull("Signature was null", request.getSignature());
        AssertJUnit.assertNotNull("KeyInfo was null", request.getSignature().getKeyInfo());
    }
    
    @Test
    public void testDOMIDResolutionUnmarshall() {
        Request request = (Request) unmarshallElement("/data/org/opensaml/saml/saml1/impl/RequestWithSignature.xml");
        
        AssertJUnit.assertNotNull("Request was null", request);
        AssertJUnit.assertNotNull("Signature was null", request.getSignature());
        Document document = request.getSignature().getDOM().getOwnerDocument();
        Element idElem = request.getDOM();
        
        AssertJUnit.assertNotNull("DOM ID resolution returned null", document.getElementById(expectedID));
        AssertJUnit.assertTrue("DOM elements were not equal", idElem.isSameNode(document.getElementById(expectedID)));
    }

    @Test
    public void testDOMIDResolutionMarshall() throws MarshallingException {
        Request request = (Request) buildXMLObject(Request.DEFAULT_ELEMENT_NAME);
        request.setID(expectedID);
        request.setQuery((AttributeQuery) buildXMLObject(AttributeQuery.DEFAULT_ELEMENT_NAME));
        
        marshallerFactory.getMarshaller(request).marshall(request);
        
        Document document = request.getQuery().getDOM().getOwnerDocument();
        Element idElem = request.getDOM();
        
        AssertJUnit.assertNotNull("DOM ID resolution returned null", document.getElementById(expectedID));
        AssertJUnit.assertTrue("DOM elements were not equal", idElem.isSameNode(document.getElementById(expectedID)));
    }

}
