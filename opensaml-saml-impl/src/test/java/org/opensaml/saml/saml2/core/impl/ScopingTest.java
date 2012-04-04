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

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.IDPList;
import org.opensaml.saml.saml2.core.RequesterID;
import org.opensaml.saml.saml2.core.Scoping;

/**
 *Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.core.impl.ScopingImpl}.
 */
public class ScopingTest extends XMLObjectProviderBaseTestCase {
    
    /** Expected ProxyCount*/
    private int expectedProxyCount;

    /** Expected number of child RequesterID's */
    private int expectedNumRequestIDs;
    
    /**
     * Constructor
     *
     */
    public ScopingTest() {
        singleElementFile = "/data/org/opensaml/saml/saml2/core/impl/Scoping.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml/saml2/core/impl/ScopingOptionalAttributes.xml";
        childElementsFile = "/data/org/opensaml/saml/saml2/core/impl/ScopingChildElements.xml";
    }
    
    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedProxyCount = 5;
        expectedNumRequestIDs = 3;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        Scoping scoping = (Scoping) buildXMLObject(Scoping.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, scoping);

    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        Scoping scoping = (Scoping) buildXMLObject(Scoping.DEFAULT_ELEMENT_NAME);
        
        scoping.setProxyCount(new Integer(expectedProxyCount));
        
        assertXMLEquals(expectedOptionalAttributesDOM, scoping);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        Scoping scoping = (Scoping) buildXMLObject(Scoping.DEFAULT_ELEMENT_NAME);
        
        QName idpListQName = new QName(SAMLConstants.SAML20P_NS, IDPList.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        scoping.setIDPList((IDPList) buildXMLObject(idpListQName));
        
        QName requesterIDQName = new QName(SAMLConstants.SAML20P_NS, RequesterID.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        for (int i = 0; i<expectedNumRequestIDs; i++){
            scoping.getRequesterIDs().add((RequesterID) buildXMLObject(requesterIDQName));
        }
        
        assertXMLEquals(expectedChildElementsDOM, scoping);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        Scoping scoping = (Scoping) unmarshallElement(singleElementFile);
        
        Assert.assertNull(scoping.getProxyCount(), "ProxyCount");
        Assert.assertNull(scoping.getIDPList(), "IDPList");
        Assert.assertEquals(scoping.getRequesterIDs().size(), 0 , "RequesterID count");

    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        Scoping scoping = (Scoping) unmarshallElement(singleElementOptionalAttributesFile);
        
        Assert.assertNotNull(scoping.getProxyCount(), "ProxyCount");
        Assert.assertNull(scoping.getIDPList(), "IDPList");
        Assert.assertEquals(scoping.getRequesterIDs().size(), 0, "RequesterID count");
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        Scoping scoping = (Scoping) unmarshallElement(childElementsFile);
        
        Assert.assertNull(scoping.getProxyCount(), "ProxyCount");
        Assert.assertNotNull(scoping.getIDPList(), "IDPList");
        Assert.assertEquals(scoping.getRequesterIDs().size(), expectedNumRequestIDs, "RequesterID count");
    }
}