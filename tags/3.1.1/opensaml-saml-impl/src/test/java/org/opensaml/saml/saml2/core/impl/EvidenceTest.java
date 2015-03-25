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
import org.testng.Assert;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AssertionIDRef;
import org.opensaml.saml.saml2.core.AssertionURIRef;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.core.Evidence;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.saml.saml2.core.impl.EvidenceImpl}.
 */
public class EvidenceTest extends XMLObjectProviderBaseTestCase {

    /** Count of AssertionIDRef subelements. */
    private int assertionIDRefCount = 3;

    /** Count of AssertionURIRef subelements. */
    private int assertionURIRefCount = 4;

    /** Count of Assertion subelements. */
    private int assertionCount = 2;
    
    /** Count of EncryptedAssertion subelements. */
    private int encryptedAssertionCount = 2;


    /** Constructor. */
    public EvidenceTest() {
        singleElementFile = "/data/org/opensaml/saml/saml2/core/impl/Evidence.xml";
        childElementsFile = "/data/org/opensaml/saml/saml2/core/impl/EvidenceChildElements.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        Evidence evidence = (Evidence) unmarshallElement(singleElementFile);

        Assert.assertNotNull(evidence);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        Evidence evidence = (Evidence) unmarshallElement(childElementsFile);

        Assert.assertEquals(evidence.getAssertionIDReferences()
                .size(), assertionIDRefCount, "AssertionIDRef count not as expected");
        Assert.assertEquals(evidence
                .getAssertionURIReferences().size(), assertionURIRefCount, "AssertionURIRef count not as expected");
        Assert.assertEquals(evidence.getAssertions().size(), assertionCount, "Assertion count not as expected");
        Assert.assertEquals(evidence.getEncryptedAssertions().size(), 
                encryptedAssertionCount, "EncryptedAssertion count not as expected");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        Evidence evidence = (Evidence) buildXMLObject(Evidence.DEFAULT_ELEMENT_NAME);

        assertXMLEquals(expectedDOM, evidence);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        Evidence evidence = (Evidence) buildXMLObject(Evidence.DEFAULT_ELEMENT_NAME);
        
        evidence.getAssertionIDReferences()
            .add((AssertionIDRef) buildXMLObject(AssertionIDRef.DEFAULT_ELEMENT_NAME));
        evidence.getAssertionIDReferences()
            .add((AssertionIDRef) buildXMLObject(AssertionIDRef.DEFAULT_ELEMENT_NAME));
        evidence.getAssertionURIReferences()
            .add((AssertionURIRef) buildXMLObject(AssertionURIRef.DEFAULT_ELEMENT_NAME));
        evidence.getAssertionIDReferences()
            .add((AssertionIDRef) buildXMLObject(AssertionIDRef.DEFAULT_ELEMENT_NAME));
        evidence.getAssertionURIReferences()
            .add((AssertionURIRef) buildXMLObject(AssertionURIRef.DEFAULT_ELEMENT_NAME));
        evidence.getAssertionURIReferences()
            .add((AssertionURIRef) buildXMLObject(AssertionURIRef.DEFAULT_ELEMENT_NAME));
        evidence.getAssertionURIReferences()
            .add((AssertionURIRef) buildXMLObject(AssertionURIRef.DEFAULT_ELEMENT_NAME));
        evidence.getAssertions()
            .add((Assertion) buildXMLObject(Assertion.DEFAULT_ELEMENT_NAME));
        evidence.getEncryptedAssertions()
            .add((EncryptedAssertion) buildXMLObject(EncryptedAssertion.DEFAULT_ELEMENT_NAME));
        evidence.getEncryptedAssertions()
            .add((EncryptedAssertion) buildXMLObject(EncryptedAssertion.DEFAULT_ELEMENT_NAME));
        evidence.getAssertions()
            .add((Assertion) buildXMLObject(Assertion.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, evidence);
    }
}