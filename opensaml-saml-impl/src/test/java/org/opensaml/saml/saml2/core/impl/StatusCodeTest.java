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
import org.opensaml.saml.saml2.core.StatusCode;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.core.impl.StatusCodeImpl}.
 */
public class StatusCodeTest extends XMLObjectProviderBaseTestCase {
    
    /** Expected Value attribute value*/
    private String expectedValue;

    /**
     * Constructor
     *
     */
    public StatusCodeTest() {
       singleElementFile = "/data/org/opensaml/saml/saml2/core/impl/StatusCode.xml";
       childElementsFile = "/data/org/opensaml/saml/saml2/core/impl/StatusCodeChildElements.xml";
    }
    
    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedValue = "urn:string";
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        StatusCode statusCode = (StatusCode) buildXMLObject(StatusCode.DEFAULT_ELEMENT_NAME);
        
        statusCode.setValue(expectedValue);
        
        assertXMLEquals(expectedDOM, statusCode);

    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        StatusCode statusCode = (StatusCode) buildXMLObject(StatusCode.DEFAULT_ELEMENT_NAME);
        
        QName statusCodeQName = new QName(SAMLConstants.SAML20P_NS, StatusCode.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        statusCode.setStatusCode((StatusCode) buildXMLObject(statusCodeQName));
        
        assertXMLEquals(expectedChildElementsDOM, statusCode);
    }



    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        StatusCode statusCode = (StatusCode) unmarshallElement(singleElementFile);
        
        Assert.assertEquals(statusCode.getValue(), expectedValue, "Unmarshalled status code URI value was not the expected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        StatusCode statusCode = (StatusCode) unmarshallElement(childElementsFile);
        
        Assert.assertNotNull(statusCode.getStatusCode());
    }
}