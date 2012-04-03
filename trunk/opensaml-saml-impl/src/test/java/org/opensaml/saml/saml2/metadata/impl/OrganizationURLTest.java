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
package org.opensaml.saml.saml2.metadata.impl;

import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.OrganizationURL;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.OrganizationURL}.
 */
public class OrganizationURLTest extends XMLObjectProviderBaseTestCase {
    
    /** Expected URL. */
    private String expectValue = "http://example.org";
    private String expectLang = "Language" ;
    
    /**
     * Constructor
     */
    public OrganizationURLTest() {
        singleElementFile = "/data/org/opensaml/saml/saml2/metadata/impl/OrganizationURL.xml";
    }
    
    /** {@inheritDoc} */

    /** {@inheritDoc} */
    public void testSingleElementUnmarshall() {
        OrganizationURL url = (OrganizationURL) unmarshallElement(singleElementFile);
        
        assertEquals("URL was not expected value", expectValue, url.getValue());
        assertEquals("langg was not expected value", expectLang, url.getXMLLang());
    }

    /** {@inheritDoc} */
    public void testSingleElementMarshall() {
        QName qname = new QName(SAMLConstants.SAML20MD_NS, OrganizationURL.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        OrganizationURL url = (OrganizationURL) buildXMLObject(qname);
        
        url.setValue(expectValue);
        url.setXMLLang(expectLang);

        assertXMLEquals(expectedDOM, url);
    }

}