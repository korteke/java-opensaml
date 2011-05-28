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
import org.opensaml.samlext.saml2mdui.DiscoHints;
import org.opensaml.samlext.saml2mdui.DomainHint;
import org.opensaml.samlext.saml2mdui.GeolocationHint;
import org.opensaml.samlext.saml2mdui.IPHint;
import org.opensaml.samlext.saml2mdui.UIInfo;
import org.opensaml.xml.mock.SimpleXMLObject;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml2.metadata.OrganizationName}.
 */
public class DiscoHintsTest extends BaseSAMLObjectProviderTestCase {
    
    /** Expected count of &lt;IPHint/&gt;. */
    private final int expectedIPHintCount = 2;
    
    /** Expected count of &lt;DomainHint/&gt;. */
    private final int expectedDomainHintsCount = 3;
    
    /** Expected count of &lt;GeolocationHint/&gt;. */
    private final int expectedGeolocationHintsCount = 1;
    
    /** Expected count of &lt;test:SimpleElementgt;. */
    private final int expectedSimpleElementCount =1;
    
    /**
     * Constructor.
     */
    public DiscoHintsTest() {
        singleElementFile = "/data/org/opensaml/samlext/saml2mdui/DiscoHints.xml";
        childElementsFile = "/data/org/opensaml/samlext/saml2mdui/DiscoHintsChildElements.xml";
    }
    
    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();      
    }

    /** {@inheritDoc} */
    public void testSingleElementUnmarshall() {
        DiscoHints hints = (DiscoHints) unmarshallElement(singleElementFile);
        //
        // Shut up warning
        //
        hints.toString();
    }

    /** {@inheritDoc} */
    public void testSingleElementMarshall() {
        QName qname = new QName(UIInfo.MDUI_NS, 
                                DiscoHints.DEFAULT_ELEMENT_LOCAL_NAME, 
                                UIInfo.MDUI_PREFIX);
        
        DiscoHints hints = (DiscoHints) buildXMLObject(qname);
        
        assertEquals(expectedDOM, hints);
    }

    /** {@inheritDoc} */
    public void testChildElementsUnmarshall(){
        DiscoHints hints = (DiscoHints) unmarshallElement(childElementsFile);
        
        assertEquals("<IPHint> count", expectedIPHintCount, hints.getIPHints().size());
        assertEquals("<DomainHint> count", expectedDomainHintsCount, hints.getDomainHints().size());
        assertEquals("<GeolocationHint> count", expectedGeolocationHintsCount, hints.getGeolocationHints().size());
        assertEquals("<test:SimpleElement> count", expectedSimpleElementCount, hints.getXMLObjects(SimpleXMLObject.ELEMENT_NAME).size());
    }

    /** {@inheritDoc} */
    public void testChildElementsMarshall(){
        DiscoHints hints = (DiscoHints) buildXMLObject(DiscoHints.DEFAULT_ELEMENT_NAME);
        
        hints.getDomainHints().add((DomainHint) buildXMLObject(DomainHint.DEFAULT_ELEMENT_NAME));
        
        hints.getIPHints().add((IPHint) buildXMLObject(IPHint.DEFAULT_ELEMENT_NAME));

        hints.getGeolocationHints().add((GeolocationHint) buildXMLObject(GeolocationHint.DEFAULT_ELEMENT_NAME));
        
        hints.getXMLObjects().add((SimpleXMLObject) buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        
        hints.getDomainHints().add((DomainHint) buildXMLObject(DomainHint.DEFAULT_ELEMENT_NAME));

        hints.getIPHints().add((IPHint) buildXMLObject(IPHint.DEFAULT_ELEMENT_NAME));
        
        hints.getDomainHints().add((DomainHint) buildXMLObject(DomainHint.DEFAULT_ELEMENT_NAME));

        assertEquals(expectedChildElementsDOM, hints);   
    }

}