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
import org.opensaml.saml.saml2.core.Advice;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AssertionIDRef;
import org.opensaml.saml.saml2.core.AssertionURIRef;
import org.opensaml.saml.saml2.core.EncryptedAssertion;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.saml.saml2.core.impl.AdviceImpl}.
 */
public class AdviceTest extends XMLObjectProviderBaseTestCase {

    /** Count of AssertionIDRef subelements */
    protected int assertionIDRefCount = 3;

    /** Count of AssertionURIRef subelements */
    protected int assertionURIRefCount = 2;

    /** Count of Assertion subelements */
    protected int assertionCount = 3;

    /** Count of Assertion subelements */
    protected int encryptedAssertionCount = 2;

    /** Constructor */
    public AdviceTest() {
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/Advice.xml";
        childElementsFile = "/org/opensaml/saml/saml2/core/impl/AdviceChildElements.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        Advice advice = (Advice) unmarshallElement(singleElementFile);

        Assert.assertNotNull(advice);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        Advice advice = (Advice) unmarshallElement(childElementsFile);

        Assert.assertEquals(advice.getAssertionIDReferences()
                .size(), assertionIDRefCount, "AssertionIDRef count not as expected");
        Assert.assertEquals(advice.getAssertionURIReferences()
                .size(), assertionURIRefCount, "AssertionURIRef count not as expected");
        Assert.assertEquals(advice.getAssertions().size(), assertionCount, "Assertion count not as expected");
        Assert.assertEquals(advice.getEncryptedAssertions().size(), encryptedAssertionCount, "EncryptedAssertion count not as expected");
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        Advice advice = (Advice) buildXMLObject(Advice.DEFAULT_ELEMENT_NAME);

        assertXMLEquals(expectedDOM, advice);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        Advice advice = (Advice) buildXMLObject(Advice.DEFAULT_ELEMENT_NAME);
        
        advice.getChildren().add(buildXMLObject(AssertionIDRef.DEFAULT_ELEMENT_NAME));
        advice.getChildren().add(buildXMLObject(AssertionURIRef.DEFAULT_ELEMENT_NAME));
        advice.getChildren().add(buildXMLObject(AssertionIDRef.DEFAULT_ELEMENT_NAME));
        advice.getChildren().add(buildXMLObject(AssertionURIRef.DEFAULT_ELEMENT_NAME));
        advice.getChildren().add(buildXMLObject(AssertionIDRef.DEFAULT_ELEMENT_NAME));
        advice.getChildren().add(buildXMLObject(Assertion.DEFAULT_ELEMENT_NAME));
        advice.getChildren().add(buildXMLObject(EncryptedAssertion.DEFAULT_ELEMENT_NAME));
        advice.getChildren().add(buildXMLObject(Assertion.DEFAULT_ELEMENT_NAME));
        advice.getChildren().add(buildXMLObject(Assertion.DEFAULT_ELEMENT_NAME));
        advice.getChildren().add(buildXMLObject(EncryptedAssertion.DEFAULT_ELEMENT_NAME));

        assertXMLEquals(expectedChildElementsDOM, advice);
    }
}