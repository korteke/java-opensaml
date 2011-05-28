/*
 * Copyright [2005] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
package org.opensaml.samlext.saml2mdui;

import javax.xml.namespace.QName;

import org.opensaml.common.BaseSAMLObjectProviderTestCase;
import org.opensaml.samlext.saml2mdui.Description;
import org.opensaml.samlext.saml2mdui.DisplayName;
import org.opensaml.samlext.saml2mdui.InformationURL;
import org.opensaml.samlext.saml2mdui.Keywords;
import org.opensaml.samlext.saml2mdui.Logo;
import org.opensaml.samlext.saml2mdui.PrivacyStatementURL;
import org.opensaml.samlext.saml2mdui.UIInfo;
import org.opensaml.xml.mock.SimpleXMLObject;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml2.metadata.OrganizationName}.
 */
public class UIInfoTest extends BaseSAMLObjectProviderTestCase {
    
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
        singleElementFile = "/data/org/opensaml/samlext/saml2mdui/UIInfo.xml";
        childElementsFile = "/data/org/opensaml/samlext/saml2mdui/UIInfoChildElements.xml";
    }
    
    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();      
    }

    /** {@inheritDoc} */
    public void testSingleElementUnmarshall() {
        UIInfo uiinfo = (UIInfo) unmarshallElement(singleElementFile);
        //
        // No contents sanity to check
        //
    }

    /** {@inheritDoc} */
    public void testSingleElementMarshall() {
        QName qname = new QName(UIInfo.MDUI_NS, 
                                UIInfo.DEFAULT_ELEMENT_LOCAL_NAME, 
                                UIInfo.MDUI_PREFIX);
        
        UIInfo uiinfo = (UIInfo) buildXMLObject(qname);
        
        assertEquals(expectedDOM, uiinfo);
    }

    /** {@inheritDoc} */
    public void testChildElementsUnmarshall(){
        UIInfo uiinfo = (UIInfo) unmarshallElement(childElementsFile);
        
        assertEquals("<DisplayName> count", expectedDisplayNamesCount, uiinfo.getDisplayNames().size());
        assertEquals("<Descriptions> count", expectedDescriptionsCount, uiinfo.getDescriptions().size());
        assertEquals("<Logos> count", expectedLogosCount, uiinfo.getLogos().size());
        assertEquals("<Keywords> count", expectedKeywordsCount, uiinfo.getKeywords().size());
        assertEquals("<InformationURLs> count", expectedInformationURLsCount, uiinfo.getInformationURLs().size());
        assertEquals("<PrivacyStatementURLs> count", expectedPrivacyStatementURLsCount, 
                                                     uiinfo.getPrivacyStatementURLs().size());
        assertEquals("<test:SimpleElement> count", expectedSimpleElementCount, uiinfo.getXMLObjects(SimpleXMLObject.ELEMENT_NAME).size());
        
       
    }

    /** {@inheritDoc} */
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

        assertEquals(expectedChildElementsDOM, uiinfo);   
    }

}