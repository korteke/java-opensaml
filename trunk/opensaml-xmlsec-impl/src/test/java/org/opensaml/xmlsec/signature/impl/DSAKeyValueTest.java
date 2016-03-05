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

package org.opensaml.xmlsec.signature.impl;


import org.testng.annotations.Test;
import org.testng.Assert;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.xmlsec.signature.DSAKeyValue;
import org.opensaml.xmlsec.signature.G;
import org.opensaml.xmlsec.signature.J;
import org.opensaml.xmlsec.signature.P;
import org.opensaml.xmlsec.signature.PgenCounter;
import org.opensaml.xmlsec.signature.Q;
import org.opensaml.xmlsec.signature.Seed;
import org.opensaml.xmlsec.signature.Y;

/**
 *
 */
public class DSAKeyValueTest extends XMLObjectProviderBaseTestCase {
    
    /**
     * Constructor
     *
     */
    public DSAKeyValueTest() {
        singleElementFile = "/org/opensaml/xmlsec/signature/impl/DSAKeyValue.xml";
        childElementsFile = "/org/opensaml/xmlsec/signature/impl/DSAKeyValueChildElements.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        DSAKeyValue keyValue = (DSAKeyValue) unmarshallElement(singleElementFile);
        
        Assert.assertNotNull(keyValue, "DSAKeyValue");
        Assert.assertNull(keyValue.getP(), "P child element");
        Assert.assertNull(keyValue.getQ(), "Q child element");
        Assert.assertNull(keyValue.getG(), "G child element");
        Assert.assertNull(keyValue.getY(), "Y child element");
        Assert.assertNull(keyValue.getJ(), "J child element");
        Assert.assertNull(keyValue.getSeed(), "Seed element");
        Assert.assertNull(keyValue.getPgenCounter(), "PgenCounter element");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        DSAKeyValue keyValue = (DSAKeyValue) unmarshallElement(childElementsFile);
        
        Assert.assertNotNull(keyValue, "DSAKeyValue");
        Assert.assertNotNull(keyValue.getP(), "P child element");
        Assert.assertNotNull(keyValue.getQ(), "Q child element");
        Assert.assertNotNull(keyValue.getG(), "G child element");
        Assert.assertNotNull(keyValue.getY(), "Y child element");
        Assert.assertNotNull(keyValue.getJ(), "J child element");
        Assert.assertNotNull(keyValue.getSeed(), "Seed element");
        Assert.assertNotNull(keyValue.getPgenCounter(), "PgenCounter element");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        DSAKeyValue keyValue = (DSAKeyValue) buildXMLObject(DSAKeyValue.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, keyValue);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        DSAKeyValue keyValue = (DSAKeyValue) buildXMLObject(DSAKeyValue.DEFAULT_ELEMENT_NAME);
        
        keyValue.setP((P) buildXMLObject(P.DEFAULT_ELEMENT_NAME));
        keyValue.setQ((Q) buildXMLObject(Q.DEFAULT_ELEMENT_NAME));
        keyValue.setG((G) buildXMLObject(G.DEFAULT_ELEMENT_NAME));
        keyValue.setY((Y) buildXMLObject(Y.DEFAULT_ELEMENT_NAME));
        keyValue.setJ((J) buildXMLObject(J.DEFAULT_ELEMENT_NAME));
        keyValue.setSeed((Seed) buildXMLObject(Seed.DEFAULT_ELEMENT_NAME));
        keyValue.setPgenCounter((PgenCounter) buildXMLObject(PgenCounter.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, keyValue);
    }

}
