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
import org.testng.Assert;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.opensaml.xmlsec.encryption.OriginatorKeyInfo;
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
public class OriginatorKeyInfoTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedID;
    
    /**
     * Constructor
     *
     */
    public OriginatorKeyInfoTest() {
        singleElementFile = "/org/opensaml/xmlsec/encryption/impl/OriginatorKeyInfo.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/xmlsec/encryption/impl/OriginatorKeyInfoOptionalAttributes.xml";
        childElementsFile = "/org/opensaml/xmlsec/encryption/impl/OriginatorKeyInfoChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedID = "abc123";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        OriginatorKeyInfo keyInfo = (OriginatorKeyInfo) unmarshallElement(singleElementFile);
        
        Assert.assertNotNull(keyInfo, "OriginatorKeyInfo");
        Assert.assertNull(keyInfo.getID(), "Id attribute");
        Assert.assertEquals(keyInfo.getXMLObjects().size(), 0, "Total # of XMLObject child elements");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        OriginatorKeyInfo keyInfo = (OriginatorKeyInfo) unmarshallElement(singleElementOptionalAttributesFile);
        
        Assert.assertNotNull(keyInfo, "OriginatorKeyInfo");
        Assert.assertEquals(keyInfo.getID(), expectedID, "Id attribute");
        Assert.assertEquals(keyInfo.getXMLObjects().size(), 0, "Total # of XMLObject child elements");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        OriginatorKeyInfo keyInfo = (OriginatorKeyInfo) unmarshallElement(childElementsFile);
        
        Assert.assertNotNull(keyInfo, "OriginatorKeyInfo");
        Assert.assertEquals(keyInfo.getXMLObjects().size(), 11, "Total # of XMLObject child elements");
        Assert.assertEquals(keyInfo.getKeyNames().size(), 2, "# of KeyName child elements");
        Assert.assertEquals(keyInfo.getKeyValues().size(), 2, "# of KeyValue child elements");
        Assert.assertEquals(keyInfo.getRetrievalMethods().size(), 1, "# of RetrievalMethod child elements");
        Assert.assertEquals(keyInfo.getX509Datas().size(), 2, "# of X509Data child elements");
        Assert.assertEquals(keyInfo.getPGPDatas().size(), 1, "# of PGPData child elements");
        Assert.assertEquals(keyInfo.getSPKIDatas().size(), 1, "# of SPKIData child elements");
        Assert.assertEquals(keyInfo.getMgmtDatas().size(), 1, "# of MgmtData child elements");
        Assert.assertEquals(keyInfo.getXMLObjects(SimpleXMLObject.ELEMENT_NAME).size(), 1, "# of SimpleElement child elements");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        OriginatorKeyInfo keyInfo = (OriginatorKeyInfo) buildXMLObject(OriginatorKeyInfo.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, keyInfo);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        OriginatorKeyInfo keyInfo = (OriginatorKeyInfo) buildXMLObject(OriginatorKeyInfo.DEFAULT_ELEMENT_NAME);
        
        keyInfo.setID(expectedID);
        
        assertXMLEquals(expectedOptionalAttributesDOM, keyInfo);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        OriginatorKeyInfo keyInfo = (OriginatorKeyInfo) buildXMLObject(OriginatorKeyInfo.DEFAULT_ELEMENT_NAME);
        
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
