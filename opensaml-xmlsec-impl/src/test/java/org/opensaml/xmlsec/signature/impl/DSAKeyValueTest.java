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
import org.testng.annotations.BeforeMethod;
import org.testng.AssertJUnit;
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
        singleElementFile = "/data/org/opensaml/xmlsec/signature/impl/DSAKeyValue.xml";
        childElementsFile = "/data/org/opensaml/xmlsec/signature/impl/DSAKeyValueChildElements.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        DSAKeyValue keyValue = (DSAKeyValue) unmarshallElement(singleElementFile);
        
        AssertJUnit.assertNotNull("DSAKeyValue", keyValue);
        AssertJUnit.assertNull("P child element", keyValue.getP());
        AssertJUnit.assertNull("Q child element", keyValue.getQ());
        AssertJUnit.assertNull("G child element", keyValue.getG());
        AssertJUnit.assertNull("Y child element", keyValue.getY());
        AssertJUnit.assertNull("J child element", keyValue.getJ());
        AssertJUnit.assertNull("Seed element", keyValue.getSeed());
        AssertJUnit.assertNull("PgenCounter element", keyValue.getPgenCounter());
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        DSAKeyValue keyValue = (DSAKeyValue) unmarshallElement(childElementsFile);
        
        AssertJUnit.assertNotNull("DSAKeyValue", keyValue);
        AssertJUnit.assertNotNull("P child element", keyValue.getP());
        AssertJUnit.assertNotNull("Q child element", keyValue.getQ());
        AssertJUnit.assertNotNull("G child element", keyValue.getG());
        AssertJUnit.assertNotNull("Y child element", keyValue.getY());
        AssertJUnit.assertNotNull("J child element", keyValue.getJ());
        AssertJUnit.assertNotNull("Seed element", keyValue.getSeed());
        AssertJUnit.assertNotNull("PgenCounter element", keyValue.getPgenCounter());
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
