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
import org.opensaml.samlext.saml2mdui.PrivacyStatementURL;
import org.opensaml.samlext.saml2mdui.UIInfo;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml2.metadata.OrganizationName}.
 */
public class PrivacyStatementURLTest extends BaseSAMLObjectProviderTestCase {
    
    /** Expected name. */
    protected String expectValue="https://example.org/Privacy";
    /** Expected language. */
    protected String expectLang="PrivacyLang";
    
    /**
     * Constructor.
     */
    public PrivacyStatementURLTest() {
        singleElementFile = "/data/org/opensaml/samlext/saml2mdui/PrivacyStatementURL.xml";
    }
    

    /** {@inheritDoc} */
    public void testSingleElementUnmarshall() {
        PrivacyStatementURL url = (PrivacyStatementURL) unmarshallElement(singleElementFile);
        
        assertEquals("URI was not expected value", expectValue, url.getValue());
        assertEquals("xml:lang was not expected value", expectLang, url.getXMLLang());
    }

    /** {@inheritDoc} */
    public void testSingleElementMarshall() {
        QName qname = new QName(UIInfo.MDUI_NS, 
                                PrivacyStatementURL.DEFAULT_ELEMENT_LOCAL_NAME, 
                                UIInfo.MDUI_PREFIX);
        
        PrivacyStatementURL url = (PrivacyStatementURL) buildXMLObject(qname);
        
        url.setValue(expectValue);
        url.setXMLLang(expectLang);

        assertEquals(expectedDOM, url);
    }
}