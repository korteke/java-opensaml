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
package org.opensaml.saml.saml2.core.impl;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import javax.xml.namespace.QName;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDMappingResponse;

/**
 *
 */
public class NameIDMappingResponseTest extends StatusResponseTestBase {

    /**
     * Constructor
     *
     */
    public NameIDMappingResponseTest() {
        super();
        singleElementFile = "/data/org/opensaml/saml/saml2/core/impl/NameIDMappingResponse.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml/saml2/core/impl/NameIDMappingResponseOptionalAttributes.xml";
        childElementsFile = "/data/org/opensaml/saml/saml2/core/impl/NameIDMappingResponseChildElements.xml";
    }
    
    
    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
    }


    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        QName qname = new QName(SAMLConstants.SAML20P_NS, NameIDMappingResponse.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        NameIDMappingResponse resp = (NameIDMappingResponse) buildXMLObject(qname);
        
        super.populateRequiredAttributes(resp);
        
        assertXMLEquals(expectedDOM, resp);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        QName qname = new QName(SAMLConstants.SAML20P_NS, NameIDMappingResponse.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        NameIDMappingResponse resp = (NameIDMappingResponse) buildXMLObject(qname);
        
        super.populateRequiredAttributes(resp);
        super.populateOptionalAttributes(resp);
        
        assertXMLEquals(expectedOptionalAttributesDOM, resp);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        QName qname = new QName(SAMLConstants.SAML20P_NS, NameIDMappingResponse.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        NameIDMappingResponse req = (NameIDMappingResponse) buildXMLObject(qname);
        
        super.populateChildElements(req);
        
        QName nameIDQName = new QName(SAMLConstants.SAML20_NS, NameID.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        req.setNameID((NameID) buildXMLObject(nameIDQName));
        
        assertXMLEquals(expectedChildElementsDOM, req);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        NameIDMappingResponse resp = (NameIDMappingResponse) unmarshallElement(singleElementFile);
        
        Assert.assertNotNull(resp, "NameIDMappingResponse was null");
        super.helperTestSingleElementUnmarshall(resp);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        NameIDMappingResponse resp = (NameIDMappingResponse) unmarshallElement(singleElementOptionalAttributesFile);
        
        Assert.assertNotNull(resp, "NameIDMappingResponse was null");
        super.helperTestSingleElementOptionalAttributesUnmarshall(resp);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        NameIDMappingResponse resp = (NameIDMappingResponse) unmarshallElement(childElementsFile);
        
        Assert.assertNotNull(resp.getNameID(), "Identifier was null");
        super.helperTestChildElementsUnmarshall(resp);
    }

}
