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

package org.opensaml.xmlsec.encryption.impl;


import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.AssertJUnit;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.opensaml.xmlsec.encryption.AgreementMethod;
import org.opensaml.xmlsec.encryption.KANonce;
import org.opensaml.xmlsec.encryption.OriginatorKeyInfo;
import org.opensaml.xmlsec.encryption.RecipientKeyInfo;

/**
 *
 */
public class AgreementMethodTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedAlgorithm;
    private int expectedNumUnknownChildren;
    
    /**
     * Constructor
     *
     */
    public AgreementMethodTest() {
        singleElementFile = "/data/org/opensaml/xmlsec/encryption/impl/AgreementMethod.xml";
        childElementsFile = "/data/org/opensaml/xmlsec/encryption/impl/AgreementMethodChildElements.xml";
    }

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedAlgorithm = "urn:string:foo";
        expectedNumUnknownChildren = 2;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        AgreementMethod am = (AgreementMethod) unmarshallElement(singleElementFile);
        
        AssertJUnit.assertNotNull("AgreementMethod", am);
        AssertJUnit.assertEquals("Algorithm attribute", expectedAlgorithm, am.getAlgorithm());
        AssertJUnit.assertNull("KA-Nonce child element", am.getKANonce());
        AssertJUnit.assertEquals("Unknown children", 0, am.getUnknownXMLObjects().size());
        AssertJUnit.assertNull("OriginatorKeyInfo child element", am.getOriginatorKeyInfo());
        AssertJUnit.assertNull("RecipientKeyInfo child element", am.getRecipientKeyInfo());
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        AgreementMethod am = (AgreementMethod) unmarshallElement(childElementsFile);
        
        AssertJUnit.assertNotNull("AgreementMethod", am);
        AssertJUnit.assertEquals("Algorithm attribute", expectedAlgorithm, am.getAlgorithm());
        AssertJUnit.assertNotNull("KA-Nonce child element", am.getKANonce());
        AssertJUnit.assertEquals("Unknown children", expectedNumUnknownChildren, am.getUnknownXMLObjects().size());
        AssertJUnit.assertNotNull("OriginatorKeyInfo child element", am.getOriginatorKeyInfo());
        AssertJUnit.assertNotNull("RecipientKeyInfo child element", am.getRecipientKeyInfo());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        AgreementMethod am = (AgreementMethod) buildXMLObject(AgreementMethod.DEFAULT_ELEMENT_NAME);
        
        am.setAlgorithm(expectedAlgorithm);
        
        assertXMLEquals(expectedDOM, am);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        AgreementMethod am = (AgreementMethod) buildXMLObject(AgreementMethod.DEFAULT_ELEMENT_NAME);
        
        am.setAlgorithm(expectedAlgorithm);
        am.setKANonce((KANonce) buildXMLObject(KANonce.DEFAULT_ELEMENT_NAME));
        am.getUnknownXMLObjects().add((SimpleXMLObject) buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        am.getUnknownXMLObjects().add((SimpleXMLObject) buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        am.setOriginatorKeyInfo((OriginatorKeyInfo) buildXMLObject(OriginatorKeyInfo.DEFAULT_ELEMENT_NAME));
        am.setRecipientKeyInfo((RecipientKeyInfo) buildXMLObject(RecipientKeyInfo.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, am);
    }

}
