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
import org.testng.annotations.BeforeMethod;
import org.testng.AssertJUnit;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.SubjectLocality;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.saml.saml2.core.impl.SubjectLocalityImpl}.
 */
public class SubjectLocalityTest extends XMLObjectProviderBaseTestCase {

    /** Expected Address value */
    private String expectedAddress;

    /** Expected DNSName value */
    private String expectedDNSName;

    /** Constructor */
    public SubjectLocalityTest() {
        singleElementFile = "/data/org/opensaml/saml/saml2/core/impl/SubjectLocality.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml/saml2/core/impl/SubjectLocalityOptionalAttributes.xml";
    }

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedAddress = "ip address";
        expectedDNSName = "dns name";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        SubjectLocality subjectLocality = (SubjectLocality) unmarshallElement(singleElementFile);
        String address = subjectLocality.getAddress();

        AssertJUnit.assertEquals("Address was " + address + ", expected " + expectedAddress, expectedAddress, address);

    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        SubjectLocality subjectLocality = (SubjectLocality) unmarshallElement(singleElementOptionalAttributesFile);

        String address = subjectLocality.getAddress();
        AssertJUnit.assertEquals("Address was " + address + ", expected " + expectedAddress, expectedAddress, address);

        String dnsName = subjectLocality.getDNSName();
        AssertJUnit.assertEquals("DNSName was " + dnsName + ", expected " + expectedDNSName, expectedDNSName, dnsName);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        QName qname = new QName(SAMLConstants.SAML20_NS, SubjectLocality.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        SubjectLocality subjectLocality = (SubjectLocality) buildXMLObject(qname);

        subjectLocality.setAddress(expectedAddress);
        assertXMLEquals(expectedDOM, subjectLocality);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        QName qname = new QName(SAMLConstants.SAML20_NS, SubjectLocality.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        SubjectLocality subjectLocality = (SubjectLocality) buildXMLObject(qname);

        subjectLocality.setAddress(expectedAddress);
        subjectLocality.setDNSName(expectedDNSName);
        assertXMLEquals(expectedOptionalAttributesDOM, subjectLocality);
    }
}