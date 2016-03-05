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
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml1.core.Advice;
import org.opensaml.saml.saml1.core.Assertion;
import org.opensaml.saml.saml1.core.AssertionIDReference;

/**
 * Test for {@link org.opensaml.saml.saml1.core.Advice}
 */
public class AdviceTest extends XMLObjectProviderBaseTestCase {

    /** name used to generate objects */
    private final QName qname;

    /**
     * Constructor
     */
    public AdviceTest() {
        singleElementFile = "/org/opensaml/saml/saml1/impl/singleAdvice.xml";
        childElementsFile = "/org/opensaml/saml/saml1/impl/AdviceWithChildren.xml";
        qname = new QName(SAMLConstants.SAML1_NS, Advice.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        Advice advice = (Advice) unmarshallElement(singleElementFile);

        Assert.assertEquals(advice.getAssertionIDReferences().size(), 0, "Number of child AssertIDReference elements");
        Assert.assertEquals(advice.getAssertions().size(), 0, "Number of child Assertion elements");
    }

    /**
     * Test an XML file with children
     */
    @Test
    public void testChildElementsUnmarshall() {
        Advice advice = (Advice) unmarshallElement(childElementsFile);

        Assert.assertEquals(advice.getAssertionIDReferences().size(), 2, "Number of child AssertIDReference elements");
        Assert.assertEquals(advice.getAssertions().size(), 1, "Number of child Assertion elements");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        assertXMLEquals(expectedDOM, buildXMLObject(qname));
    }

    /** {@inheritDoc} */

    @Test
    public void testChildElementsMarshall() {
        Advice advice = (Advice) buildXMLObject(qname);
        
        QName assertionIDRefQname = new QName(SAMLConstants.SAML1_NS, AssertionIDReference.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
        QName assertionQname = new QName(SAMLConstants.SAML1_NS, Assertion.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
        
        advice.getAssertionIDReferences().add((AssertionIDReference) buildXMLObject(assertionIDRefQname));
        advice.getAssertions().add((Assertion) buildXMLObject(assertionQname) );
        advice.getAssertionIDReferences().add((AssertionIDReference) buildXMLObject(assertionIDRefQname));

        assertXMLEquals(expectedChildElementsDOM, advice);
    }
}
