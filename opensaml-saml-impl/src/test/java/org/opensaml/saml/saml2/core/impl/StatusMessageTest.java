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
import org.testng.Assert;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml2.core.StatusMessage;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.core.impl.StatusMessageImpl}.
 */
public class StatusMessageTest extends XMLObjectProviderBaseTestCase {
    
   /** The expected message*/ 
    protected String expectedMessage;
    
    /**
     * Constructor
     *
     */
    public StatusMessageTest() {
        singleElementFile = "/data/org/opensaml/saml/saml2/core/impl/StatusMessage.xml";
    }

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedMessage = "Status Message";
    }
    

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        StatusMessage message = (StatusMessage) buildXMLObject(StatusMessage.DEFAULT_ELEMENT_NAME);
        
        message.setMessage(expectedMessage);
        
        assertXMLEquals(expectedDOM, message);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        StatusMessage message = (StatusMessage) unmarshallElement(singleElementFile);
        
        Assert.assertEquals(message.getMessage(), expectedMessage, "Unmarshalled status message was not the expected value");   
    }
}