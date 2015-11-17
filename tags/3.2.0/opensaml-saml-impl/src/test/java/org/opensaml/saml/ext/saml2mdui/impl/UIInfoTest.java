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

import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.ext.saml2mdui.Description;
import org.opensaml.saml.ext.saml2mdui.DisplayName;
import org.opensaml.saml.ext.saml2mdui.InformationURL;
import org.opensaml.saml.ext.saml2mdui.Keywords;
import org.opensaml.saml.ext.saml2mdui.Logo;
import org.opensaml.saml.ext.saml2mdui.PrivacyStatementURL;
import org.opensaml.saml.ext.saml2mdui.UIInfo;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.OrganizationName}.
 */
public class UIInfoTest extends XMLObjectProviderBaseTestCase {
    
    /** Expected count of &lt;DisplayName&gt;. */
    private final int expectedDisplayNamesCount = 3;
    
    /** Expected count of &lt;Description&gt;. */
    private final int expectedDescriptionsCount = 1;
    
    /** Expected count of &lt;Keywords&gt;. */
    private final int expectedKeywordsCount = 2;
    
    /** Expected count of &lt;Logo&gt;. */
    private final int expectedLogosCount = 1;
    
    /** Expected count of &lt;InformationURL&gt;. */
    private final int expectedInformationURLsCount = 1;
    
    /** Expected count of &lt;PrivacyStatementURL&gt;. */
    private final int expectedPrivacyStatementURLsCount =1;
    
    /** Expected count of &lt;test:SimpleElementgt;. */
    private final int expectedSimpleElementCount =1;
    
    /**
     * Constructor.
     */
    public UIInfoTest() {
        singleElementFile = "/data/org/opensaml/saml/ext/saml2mdui/UIInfo.xml";
        childElementsFile = "/data/org/opensaml/saml/ext/saml2mdui/UIInfoChildElements.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        UIInfo uiinfo = (UIInfo) unmarshallElement(singleElementFile);
        
        Assert.assertNotNull(uiinfo, "UIInfo");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        UIInfo uiinfo = (UIInfo) buildXMLObject(UIInfo.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, uiinfo);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall(){
        UIInfo uiinfo = (UIInfo) unmarshallElement(childElementsFile);
        
        Assert.assertEquals(uiinfo.getDisplayNames().size(), expectedDisplayNamesCount, "<DisplayName> count");
        Assert.assertEquals(uiinfo.getDescriptions().size(), expectedDescriptionsCount, "<Descriptions> count");
        Assert.assertEquals(uiinfo.getLogos().size(), expectedLogosCount, "<Logos> count");
        Assert.assertEquals(uiinfo.getKeywords().size(), expectedKeywordsCount, "<Keywords> count");
        Assert.assertEquals(uiinfo.getInformationURLs().size(), expectedInformationURLsCount, "<InformationURLs> count");
        Assert.assertEquals(uiinfo.getPrivacyStatementURLs().size(), expectedPrivacyStatementURLsCount, 
                                                     "<PrivacyStatementURLs> count");
        Assert.assertEquals(uiinfo.getXMLObjects(SimpleXMLObject.ELEMENT_NAME).size(), expectedSimpleElementCount, "<test:SimpleElement> count");
        
       
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall(){
        UIInfo uiinfo = (UIInfo) buildXMLObject(UIInfo.DEFAULT_ELEMENT_NAME);
        
        uiinfo.getDisplayNames().add((DisplayName) buildXMLObject(DisplayName.DEFAULT_ELEMENT_NAME));

        uiinfo.getDescriptions().add((Description) buildXMLObject(Description.DEFAULT_ELEMENT_NAME));

        uiinfo.getKeywords().add((Keywords) buildXMLObject(Keywords.DEFAULT_ELEMENT_NAME));
        
        uiinfo.getKeywords().add((Keywords) buildXMLObject(Keywords.DEFAULT_ELEMENT_NAME));
        
        uiinfo.getInformationURLs().add((InformationURL) buildXMLObject(InformationURL.DEFAULT_ELEMENT_NAME));
        
        uiinfo.getDisplayNames().add((DisplayName) buildXMLObject(DisplayName.DEFAULT_ELEMENT_NAME));
        
        uiinfo.getLogos().add((Logo) buildXMLObject(Logo.DEFAULT_ELEMENT_NAME));

        uiinfo.getPrivacyStatementURLs().add((PrivacyStatementURL) buildXMLObject(PrivacyStatementURL.DEFAULT_ELEMENT_NAME));
        
        uiinfo.getXMLObjects().add((SimpleXMLObject) buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        
        uiinfo.getDisplayNames().add((DisplayName) buildXMLObject(DisplayName.DEFAULT_ELEMENT_NAME));

        assertXMLEquals(expectedChildElementsDOM, uiinfo);   
    }

}