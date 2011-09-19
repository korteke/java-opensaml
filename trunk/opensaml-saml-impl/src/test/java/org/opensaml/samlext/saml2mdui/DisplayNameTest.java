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
package org.opensaml.samlext.saml2mdui;

import javax.xml.namespace.QName;

import org.opensaml.common.BaseSAMLObjectProviderTestCase;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml2.metadata.OrganizationName}.
 */
public class DisplayNameTest extends BaseSAMLObjectProviderTestCase {
    
    /** Expected name. */
    protected String expectValue="Prifysgol Caerdydd";
    /** Expected language. */
    protected String expectLang = "cy";
    
    /**
     * Constructor.
     */
    public DisplayNameTest() {
        singleElementFile = "/data/org/opensaml/samlext/saml2mdui/DisplayName.xml";
    }
    
    /** {@inheritDoc} */
    public void testSingleElementUnmarshall() {
        DisplayName name = (DisplayName) unmarshallElement(singleElementFile);
        
        assertEquals("Name was not expected value", expectValue, name.getValue());
        assertEquals("xml:lang was not expected value", expectLang, name.getXMLLang());
    }

    /** {@inheritDoc} */
    public void testSingleElementMarshall() {
        QName qname = new QName(UIInfo.MDUI_NS, 
                                DisplayName.DEFAULT_ELEMENT_LOCAL_NAME, 
                                UIInfo.MDUI_PREFIX);
        
        DisplayName name = (DisplayName) buildXMLObject(qname);
        
        name.setValue(expectValue);
        name.setXMLLang(expectLang);

        assertEquals(expectedDOM, name);
    }
}