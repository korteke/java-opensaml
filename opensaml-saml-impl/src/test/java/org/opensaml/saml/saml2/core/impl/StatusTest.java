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

import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.StatusMessage;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.core.impl.StatusImpl}.
 */
public class StatusTest extends XMLObjectProviderBaseTestCase {

    /**
     * Constructor
     *
     */
    public StatusTest() {
        singleElementFile = "/data/org/opensaml/saml/saml2/core/impl/Status.xml";
        childElementsFile = "/data/org/opensaml/saml/saml2/core/impl/StatusChildElements.xml";
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        Status status = (Status) buildXMLObject(Status.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, status);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        Status status = (Status) buildXMLObject(Status.DEFAULT_ELEMENT_NAME);
        
        QName statusCodeQName = new QName(SAMLConstants.SAML20P_NS, StatusCode.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        status.setStatusCode((StatusCode) buildXMLObject(statusCodeQName));
        
        QName statusMessageQName = new QName(SAMLConstants.SAML20P_NS, StatusMessage.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        status.setStatusMessage((StatusMessage) buildXMLObject(statusMessageQName));
        
        assertXMLEquals(expectedChildElementsDOM, status);
    }


    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        Status status = (Status) unmarshallElement(singleElementFile);
        
        AssertJUnit.assertNotNull("Status", status);
        AssertJUnit.assertNull("StatusCode child", status.getStatusCode());
        AssertJUnit.assertNull("StatusMessage", status.getStatusMessage());
    }


    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        Status status = (Status) unmarshallElement(childElementsFile);
        
        AssertJUnit.assertNotNull("StatusCode of Status was null", status.getStatusCode());
        AssertJUnit.assertNotNull("StatusMessage of Status was null", status.getStatusMessage());
    }
}