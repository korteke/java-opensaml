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
import org.testng.annotations.BeforeMethod;
import org.testng.AssertJUnit;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.ext.saml2mdui.Logo;
import org.opensaml.saml.ext.saml2mdui.UIInfo;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.OrganizationName}.
 */
public class LogoTest extends XMLObjectProviderBaseTestCase {
    
    /** Expected URL. */
    private final String expectedURL;
    
    /** expected language. */
    private final String expectedLang;
    
    /** expected height. */
    private final Integer expectedHeight;
    
    /** expected width. */
    private final Integer expectedWidth;
    
    /**
     * Constructor.
     */
    public LogoTest() {
        singleElementFile = "/data/org/opensaml/saml/ext/saml2mdui/Logo.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml/ext/saml2mdui/LogoWithLang.xml";
        expectedURL = "http://exaple.org/Logo";
        expectedHeight = new Integer(10);
        expectedWidth = new Integer(23);
        expectedLang = "logoLang";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        Logo logo = (Logo) unmarshallElement(singleElementFile);
        
        AssertJUnit.assertEquals("URL was not expected value", expectedURL, logo.getURL());
        AssertJUnit.assertEquals("height was not expected value", expectedHeight, logo.getHeight());
        AssertJUnit.assertEquals("width was not expected value", expectedWidth, logo.getWidth());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        Logo logo = (Logo) unmarshallElement(singleElementOptionalAttributesFile);
        
        AssertJUnit.assertEquals("URL was not expected value", expectedURL, logo.getURL());
        AssertJUnit.assertEquals("height was not expected value", expectedHeight, logo.getHeight());
        AssertJUnit.assertEquals("width was not expected value", expectedWidth, logo.getWidth());
        AssertJUnit.assertEquals("xml:lang was not the expected value", expectedLang, logo.getXMLLang());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        QName qname = new QName(UIInfo.MDUI_NS, 
                                Logo.DEFAULT_ELEMENT_LOCAL_NAME, 
                                UIInfo.MDUI_PREFIX);
        
        Logo logo = (Logo) buildXMLObject(qname);
        
        logo.setURL(expectedURL);
        logo.setWidth(expectedWidth);
        logo.setHeight(expectedHeight);

        assertXMLEquals(expectedDOM, logo);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        QName qname = new QName(UIInfo.MDUI_NS, 
                                Logo.DEFAULT_ELEMENT_LOCAL_NAME, 
                                UIInfo.MDUI_PREFIX);
        
        Logo logo = (Logo) buildXMLObject(qname);
        
        logo.setURL(expectedURL);
        logo.setWidth(expectedWidth);
        logo.setHeight(expectedHeight);
        logo.setXMLLang(expectedLang);

        assertXMLEquals(expectedOptionalAttributesDOM, logo);
    }
}