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
import org.opensaml.saml.saml1.core.SubjectLocality;

/**
 * Test for {@link org.opensaml.saml.saml1.core.SubjectLocality}
 */
public class SubjectLocalityTest extends XMLObjectProviderBaseTestCase {

    /** name used to generate objects */
    private final QName qname;

    /** Value of IPAddress in test file */
    private final String expectedIPAddress;
    
    /** Value of DNSAddress in test file */
    private final String expectedDNSAddress;
    
    /**
     * Constructor
     */
    public SubjectLocalityTest() {
        expectedIPAddress = "207.75.164.30";
        expectedDNSAddress = "shibboleth.internet2.edu";
        singleElementFile = "/org/opensaml/saml/saml1/impl/singleSubjectLocality.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml1/impl/singleSubjectLocalityAttributes.xml";
        qname = new QName(SAMLConstants.SAML1_NS, SubjectLocality.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementUnmarshall() {
        SubjectLocality subjectLocality = (SubjectLocality) unmarshallElement(singleElementFile);
        
        Assert.assertNull(subjectLocality.getIPAddress(), "IPAddress present");
        Assert.assertNull(subjectLocality.getDNSAddress(), "DNSAddress present");
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        SubjectLocality subjectLocality = (SubjectLocality) unmarshallElement(singleElementOptionalAttributesFile);
        
        Assert.assertEquals(subjectLocality.getIPAddress(), expectedIPAddress, "IPAddress");
        Assert.assertEquals(subjectLocality.getDNSAddress(), expectedDNSAddress, "DNSAddress");
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementMarshall() {
        assertXMLEquals(expectedDOM, buildXMLObject(qname));
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        SubjectLocality subjectLocality = (SubjectLocality) buildXMLObject(qname);
        
        subjectLocality.setDNSAddress(expectedDNSAddress);
        subjectLocality.setIPAddress(expectedIPAddress);
        assertXMLEquals(expectedOptionalAttributesDOM, subjectLocality);

    }

}
