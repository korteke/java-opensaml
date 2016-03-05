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
import org.opensaml.saml.saml1.core.AssertionIDReference;

/**
 * Test case for {@link org.opensaml.saml.saml1.core.impl.AssertionIDReferenceImpl}
 */
public class AssertionIDReferenceTest extends XMLObjectProviderBaseTestCase {

    private final String expectedNCName;

    /** name used to generate objects */
    private final QName qname;


    /**
     * Constructor
     */
    public AssertionIDReferenceTest() {
        super();
        singleElementFile = "/org/opensaml/saml/saml1/impl/singleAssertionIDReference.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml1/impl/singleAssertionIDReferenceContents.xml";
        expectedNCName = "NibbleAHappyWarthog";
        qname = new QName(SAMLConstants.SAML1_NS, AssertionIDReference.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementUnmarshall() {
        AssertionIDReference assertionIDReference;

        assertionIDReference = (AssertionIDReference) unmarshallElement(singleElementFile);

        Assert.assertNull(assertionIDReference
                .getReference(), "NCName was " + assertionIDReference.getReference() + " expected null");
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        AssertionIDReference assertionIDReference;

        assertionIDReference = (AssertionIDReference) unmarshallElement(singleElementOptionalAttributesFile);

        Assert.assertEquals(assertionIDReference.getReference(), expectedNCName, "NCName ");
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementMarshall() {
        assertXMLEquals(expectedDOM, buildXMLObject(qname));
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        AssertionIDReference assertionIDReference = (AssertionIDReference) buildXMLObject(qname);

        assertionIDReference.setReference(expectedNCName);
        assertXMLEquals(expectedOptionalAttributesDOM, assertionIDReference);
    }
}
