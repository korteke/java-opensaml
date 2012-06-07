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

package org.opensaml.saml.saml2.metadata.impl;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml2.metadata.AttributeProfile;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.impl.AttributeProfileImpl}.
 */
public class AttributeProfileTest extends XMLObjectProviderBaseTestCase {
    
    /** Expected ProfileURI */
    private String expectedProfileURI;
    
    /**
     * Constructor
     */
    public AttributeProfileTest(){
        singleElementFile = "/data/org/opensaml/saml/saml2/metadata/impl/AttributeProfile.xml";
    }
    
    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedProfileURI = "http://example.org";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall(){
        AttributeProfile profile = (AttributeProfile) unmarshallElement(singleElementFile);
        
        Assert.assertEquals(profile.getProfileURI(), expectedProfileURI, "Profile URI has a value of " + profile.getProfileURI() + ", expected a value of " + expectedProfileURI);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall(){
        AttributeProfile profile = (new AttributeProfileBuilder()).buildObject();
        
        profile.setProfileURI(expectedProfileURI);
        
        assertXMLEquals(expectedDOM, profile);
    }
}