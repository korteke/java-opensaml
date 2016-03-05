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
package org.opensaml.saml.ext.saml2mdui.impl;

import org.testng.annotations.Test;
import org.testng.Assert;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.ext.saml2mdui.DisplayName;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.OrganizationName}.
 */
public class DisplayNameTest extends XMLObjectProviderBaseTestCase {
    
    /** Expected name. */
    protected String expectValue="Prifysgol Caerdydd";
    /** Expected language. */
    protected String expectLang = "cy";
    
    /**
     * Constructor.
     */
    public DisplayNameTest() {
        singleElementFile = "/org/opensaml/saml/ext/saml2mdui/DisplayName.xml";
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        DisplayName name = (DisplayName) unmarshallElement(singleElementFile);
        
        Assert.assertEquals(name.getValue(), expectValue, "Name was not expected value");
        Assert.assertEquals(name.getXMLLang(), expectLang, "xml:lang was not expected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        DisplayName name = (DisplayName) buildXMLObject(DisplayName.DEFAULT_ELEMENT_NAME);
        
        name.setValue(expectValue);
        name.setXMLLang(expectLang);

        assertXMLEquals(expectedDOM, name);
    }
}