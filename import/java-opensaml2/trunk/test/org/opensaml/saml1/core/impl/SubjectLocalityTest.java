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

/**
 * 
 */
package org.opensaml.saml1.core.impl;

import javax.xml.namespace.QName;

import org.opensaml.common.SAMLObjectBaseTestCase;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml1.core.SubjectLocality;

/**
 * Test for {@link org.opensaml.saml1.core.impl.SubjectLocalityImpl}
 */
public class SubjectLocalityTest extends SAMLObjectBaseTestCase {

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
        super();
        expectedIPAddress = "207.75.164.30";
        expectedDNSAddress = "shibboleth.internet2.edu";
        singleElementFile = "/data/org/opensaml/saml1/singleSubjectLocality.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml1/singleSubjectLocalityAttributes.xml";
        qname = new QName(SAMLConstants.SAML1_NS, SubjectLocality.LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
    }

    /*
     * @see org.opensaml.common.SAMLObjectBaseTestCase#testSingleElementUnmarshall()
     */
    @Override
    public void testSingleElementUnmarshall() {
        SubjectLocality subjectLocality = (SubjectLocality) unmarshallElement(singleElementFile);
        
        assertNull("IPAddress present", subjectLocality.getIPAddress());
        assertNull("DNSAddress present", subjectLocality.getDNSAddress());
    }

    /*
     * @see org.opensaml.common.SAMLObjectBaseTestCase#testSingleElementOptionalAttributesUnmarshall()
     */
    @Override
    public void testSingleElementOptionalAttributesUnmarshall() {
        SubjectLocality subjectLocality = (SubjectLocality) unmarshallElement(singleElementOptionalAttributesFile);
        
        assertEquals("IPAddress", expectedIPAddress, subjectLocality.getIPAddress());
        assertEquals("DNSAddress", expectedDNSAddress, subjectLocality.getDNSAddress());
    }

    /*
     * @see org.opensaml.common.SAMLObjectBaseTestCase#testSingleElementMarshall()
     */
    @Override
    public void testSingleElementMarshall() {
        assertEquals(expectedDOM, buildXMLObject(qname));
    }

    /*
     * @see org.opensaml.common.SAMLObjectBaseTestCase#testSingleElementOptionalAttributesMarshall()
     */
    @Override
    public void testSingleElementOptionalAttributesMarshall() {
        SubjectLocality subjectLocality = (SubjectLocality) buildXMLObject(qname);
        
        subjectLocality.setDNSAddress(expectedDNSAddress);
        subjectLocality.setIPAddress(expectedIPAddress);
        assertEquals(expectedOptionalAttributesDOM, subjectLocality);

    }

}
