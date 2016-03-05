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
import org.opensaml.saml.ext.saml2mdui.DiscoHints;
import org.opensaml.saml.ext.saml2mdui.DomainHint;
import org.opensaml.saml.ext.saml2mdui.GeolocationHint;
import org.opensaml.saml.ext.saml2mdui.IPHint;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.OrganizationName}.
 */
public class DiscoHintsTest extends XMLObjectProviderBaseTestCase {
    
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
        singleElementFile = "/org/opensaml/saml/ext/saml2mdui/DiscoHints.xml";
        childElementsFile = "/org/opensaml/saml/ext/saml2mdui/DiscoHintsChildElements.xml";
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        DiscoHints hints = (DiscoHints) unmarshallElement(singleElementFile);
        //
        // Shut up warning
        //
        hints.toString();
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        DiscoHints hints = (DiscoHints) buildXMLObject(DiscoHints.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, hints);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall(){
        DiscoHints hints = (DiscoHints) unmarshallElement(childElementsFile);
        
        Assert.assertEquals(hints.getIPHints().size(), expectedIPHintCount, "<IPHint> count");
        Assert.assertEquals(hints.getDomainHints().size(), expectedDomainHintsCount, "<DomainHint> count");
        Assert.assertEquals(hints.getGeolocationHints().size(), expectedGeolocationHintsCount, "<GeolocationHint> count");
        Assert.assertEquals(hints.getXMLObjects(SimpleXMLObject.ELEMENT_NAME).size(), expectedSimpleElementCount, "<test:SimpleElement> count");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall(){
        DiscoHints hints = (DiscoHints) buildXMLObject(DiscoHints.DEFAULT_ELEMENT_NAME);
        
        hints.getDomainHints().add((DomainHint) buildXMLObject(DomainHint.DEFAULT_ELEMENT_NAME));
        
        hints.getIPHints().add((IPHint) buildXMLObject(IPHint.DEFAULT_ELEMENT_NAME));

        hints.getGeolocationHints().add((GeolocationHint) buildXMLObject(GeolocationHint.DEFAULT_ELEMENT_NAME));
        
        hints.getXMLObjects().add((SimpleXMLObject) buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        
        hints.getDomainHints().add((DomainHint) buildXMLObject(DomainHint.DEFAULT_ELEMENT_NAME));

        hints.getIPHints().add((IPHint) buildXMLObject(IPHint.DEFAULT_ELEMENT_NAME));
        
        hints.getDomainHints().add((DomainHint) buildXMLObject(DomainHint.DEFAULT_ELEMENT_NAME));

        assertXMLEquals(expectedChildElementsDOM, hints);   
    }

}