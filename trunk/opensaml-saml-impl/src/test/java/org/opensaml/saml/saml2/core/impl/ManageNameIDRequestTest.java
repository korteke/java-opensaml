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
import org.opensaml.saml.saml2.core.ManageNameIDRequest;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NewID;

/**
 *
 */
public class ManageNameIDRequestTest extends RequestTestBase {

    /**
     * Constructor
     *
     */
    public ManageNameIDRequestTest() {
        super();
        singleElementFile = "/data/org/opensaml/saml/saml2/core/impl/ManageNameIDRequest.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml/saml2/core/impl/ManageNameIDRequestOptionalAttributes.xml";
        childElementsFile = "/data/org/opensaml/saml/saml2/core/impl/ManageNameIDRequestChildElements.xml";
    }
    
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        QName qname = new QName(SAMLConstants.SAML20P_NS, ManageNameIDRequest.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        ManageNameIDRequest req = (ManageNameIDRequest) buildXMLObject(qname);
        
        super.populateRequiredAttributes(req);
        
        assertXMLEquals(expectedDOM, req);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        QName qname = new QName(SAMLConstants.SAML20P_NS, ManageNameIDRequest.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        ManageNameIDRequest req = (ManageNameIDRequest) buildXMLObject(qname);
        
        super.populateRequiredAttributes(req);
        super.populateOptionalAttributes(req);
        
        assertXMLEquals(expectedOptionalAttributesDOM, req);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        QName qname = new QName(SAMLConstants.SAML20P_NS, ManageNameIDRequest.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        ManageNameIDRequest req = (ManageNameIDRequest) buildXMLObject(qname);
        
        super.populateChildElements(req);
        
        QName nameIDQName = new QName(SAMLConstants.SAML20_NS, NameID.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        req.setNameID((NameID) buildXMLObject(nameIDQName));
        
        QName newIDQName = new QName(SAMLConstants.SAML20P_NS, NewID.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        req.setNewID((NewID) buildXMLObject(newIDQName));
        
        assertXMLEquals(expectedChildElementsDOM, req);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        ManageNameIDRequest req = (ManageNameIDRequest) unmarshallElement(singleElementFile);
        
        Assert.assertNotNull(req, "ManageNameIDRequest was null");
        super.helperTestSingleElementUnmarshall(req);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        ManageNameIDRequest req = (ManageNameIDRequest) unmarshallElement(singleElementOptionalAttributesFile);
        
        Assert.assertNotNull(req, "ManageNameIDRequest was null");
        super.helperTestSingleElementOptionalAttributesUnmarshall(req);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        ManageNameIDRequest req = (ManageNameIDRequest) unmarshallElement(childElementsFile);
        
        Assert.assertNotNull(req.getNameID(), "NameID was null");
        Assert.assertNotNull(req.getNewID(), "NewID was null");
        super.helperTestChildElementsUnmarshall(req);
    }

}
