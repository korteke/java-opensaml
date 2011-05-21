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

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml2.samlext.saml2mdui.GeolocationHint}.
 */
public class GeolocationHintTest extends BaseSAMLObjectProviderTestCase {
    
    /** Expected name. */
    private String expectedHint;
    
    /**
     * Constructor.
     */
    public GeolocationHintTest() {
        singleElementFile = "/data/org/opensaml/samlext/saml2mdui/GeolocationHint.xml";
    }
    
    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
        expectedHint = "geo:lat,long";
    }

    /** {@inheritDoc} */
    public void testSingleElementUnmarshall() {
        GeolocationHint hint = (GeolocationHint) unmarshallElement(singleElementFile);
        
        assertEquals("Name was not expected value", expectedHint, hint.getHint());
    }

    /** {@inheritDoc} */
    public void testSingleElementMarshall() {
        QName qname = new QName(UIInfo.MDUI_NS, 
                                GeolocationHint.DEFAULT_ELEMENT_LOCAL_NAME, 
                                UIInfo.MDUI_PREFIX);
        
        GeolocationHint hint = (GeolocationHint) buildXMLObject(qname);
        
        hint.setHint(expectedHint);

        assertEquals(expectedDOM, hint);
    }
}