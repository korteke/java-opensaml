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
import org.opensaml.xmlsec.encryption.RecipientKeyInfo;
import org.opensaml.xmlsec.signature.KeyName;
import org.opensaml.xmlsec.signature.KeyValue;
import org.opensaml.xmlsec.signature.MgmtData;
import org.opensaml.xmlsec.signature.PGPData;
import org.opensaml.xmlsec.signature.RetrievalMethod;
import org.opensaml.xmlsec.signature.SPKIData;
import org.opensaml.xmlsec.signature.X509Data;

/**
 *
 */
public class RecipientKeyInfoTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedID;
    
    /**
     * Constructor
     *
     */
    public RecipientKeyInfoTest() {
        singleElementFile = "/data/org/opensaml/xmlsec/encryption/impl/RecipientKeyInfo.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/xmlsec/encryption/impl/RecipientKeyInfoOptionalAttributes.xml";
        childElementsFile = "/data/org/opensaml/xmlsec/encryption/impl/RecipientKeyInfoChildElements.xml";
    }

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedID = "abc123";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        RecipientKeyInfo keyInfo = (RecipientKeyInfo) unmarshallElement(singleElementFile);
        
        AssertJUnit.assertNotNull("RecipientKeyInfo", keyInfo);
        AssertJUnit.assertNull("Id attribute", keyInfo.getID());
        AssertJUnit.assertEquals("Total # of XMLObject child elements", 0, keyInfo.getXMLObjects().size());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        RecipientKeyInfo keyInfo = (RecipientKeyInfo) unmarshallElement(singleElementOptionalAttributesFile);
        
        AssertJUnit.assertNotNull("RecipientKeyInfo", keyInfo);
        AssertJUnit.assertEquals("Id attribute", expectedID, keyInfo.getID());
        AssertJUnit.assertEquals("Total # of XMLObject child elements", 0, keyInfo.getXMLObjects().size());
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        RecipientKeyInfo keyInfo = (RecipientKeyInfo) unmarshallElement(childElementsFile);
        
        AssertJUnit.assertNotNull("RecipientKeyInfo", keyInfo);
        AssertJUnit.assertEquals("Total # of XMLObject child elements", 11, keyInfo.getXMLObjects().size());
        AssertJUnit.assertEquals("# of KeyName child elements", 2, keyInfo.getKeyNames().size());
        AssertJUnit.assertEquals("# of KeyValue child elements", 2, keyInfo.getKeyValues().size());
        AssertJUnit.assertEquals("# of RetrievalMethod child elements", 1, keyInfo.getRetrievalMethods().size());
        AssertJUnit.assertEquals("# of X509Data child elements", 2, keyInfo.getX509Datas().size());
        AssertJUnit.assertEquals("# of PGPData child elements", 1, keyInfo.getPGPDatas().size());
        AssertJUnit.assertEquals("# of SPKIData child elements", 1, keyInfo.getSPKIDatas().size());
        AssertJUnit.assertEquals("# of MgmtData child elements", 1, keyInfo.getMgmtDatas().size());
        AssertJUnit.assertEquals("# of SimpleElement child elements", 1, keyInfo.getXMLObjects(SimpleXMLObject.ELEMENT_NAME).size());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        RecipientKeyInfo keyInfo = (RecipientKeyInfo) buildXMLObject(RecipientKeyInfo.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, keyInfo);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        RecipientKeyInfo keyInfo = (RecipientKeyInfo) buildXMLObject(RecipientKeyInfo.DEFAULT_ELEMENT_NAME);
        
        keyInfo.setID(expectedID);
        
        assertXMLEquals(expectedOptionalAttributesDOM, keyInfo);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        RecipientKeyInfo keyInfo = (RecipientKeyInfo) buildXMLObject(RecipientKeyInfo.DEFAULT_ELEMENT_NAME);
        
        keyInfo.getXMLObjects().add(buildXMLObject(KeyName.DEFAULT_ELEMENT_NAME));
        keyInfo.getXMLObjects().add(buildXMLObject(KeyValue.DEFAULT_ELEMENT_NAME));
        keyInfo.getXMLObjects().add(buildXMLObject(X509Data.DEFAULT_ELEMENT_NAME));
        
        keyInfo.getXMLObjects().add(buildXMLObject(KeyName.DEFAULT_ELEMENT_NAME));
        keyInfo.getXMLObjects().add(buildXMLObject(KeyValue.DEFAULT_ELEMENT_NAME));
        keyInfo.getXMLObjects().add(buildXMLObject(X509Data.DEFAULT_ELEMENT_NAME));
        
        keyInfo.getXMLObjects().add(buildXMLObject(RetrievalMethod.DEFAULT_ELEMENT_NAME));
        keyInfo.getXMLObjects().add(buildXMLObject(PGPData.DEFAULT_ELEMENT_NAME));
        keyInfo.getXMLObjects().add(buildXMLObject(SPKIData.DEFAULT_ELEMENT_NAME));
        keyInfo.getXMLObjects().add(buildXMLObject(MgmtData.DEFAULT_ELEMENT_NAME));
        keyInfo.getXMLObjects().add(buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, keyInfo);
    }

}
