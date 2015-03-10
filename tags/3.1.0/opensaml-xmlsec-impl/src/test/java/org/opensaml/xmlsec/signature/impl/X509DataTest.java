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
import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.opensaml.xmlsec.signature.X509CRL;
import org.opensaml.xmlsec.signature.X509Certificate;
import org.opensaml.xmlsec.signature.X509Data;
import org.opensaml.xmlsec.signature.X509IssuerSerial;
import org.opensaml.xmlsec.signature.X509SKI;
import org.opensaml.xmlsec.signature.X509SubjectName;

/**
 *
 */
public class X509DataTest extends XMLObjectProviderBaseTestCase {
    
    /**
     * Constructor
     *
     */
    public X509DataTest() {
        singleElementFile = "/data/org/opensaml/xmlsec/signature/impl/X509Data.xml";
        childElementsFile = "/data/org/opensaml/xmlsec/signature/impl/X509DataChildElements.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        X509Data x509Data = (X509Data) unmarshallElement(singleElementFile);
        
        Assert.assertNotNull(x509Data, "X509Data");
        Assert.assertEquals(x509Data.getXMLObjects().size(), 0, "Total # of XMLObject child elements");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        X509Data x509Data = (X509Data) unmarshallElement(childElementsFile);
        
        Assert.assertNotNull(x509Data, "X509Data");
        Assert.assertEquals(x509Data.getXMLObjects().size(), 11, "Total # of XMLObject child elements");
        Assert.assertEquals(x509Data.getX509IssuerSerials().size(), 1, "# of X509IssuerSerial child elements");
        Assert.assertEquals(x509Data.getX509SKIs().size(), 1, "# of X509SKI child elements");
        Assert.assertEquals(x509Data.getX509SubjectNames().size(), 2, "# of X509SubjectName child elements");
        Assert.assertEquals(x509Data.getX509Certificates().size(), 3, "# of X509Certificate child elements");
        Assert.assertEquals(x509Data.getX509CRLs().size(), 2, "# of X509CRL child elements");
        Assert.assertEquals(x509Data.getXMLObjects(SimpleXMLObject.ELEMENT_NAME).size(), 2, "# of SimpleElement child elements");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        X509Data x509Data = (X509Data) buildXMLObject(X509Data.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, x509Data);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        X509Data x509Data = (X509Data) buildXMLObject(X509Data.DEFAULT_ELEMENT_NAME);
        
        x509Data.getXMLObjects().add(buildXMLObject(X509IssuerSerial.DEFAULT_ELEMENT_NAME));
        x509Data.getXMLObjects().add(buildXMLObject(X509SKI.DEFAULT_ELEMENT_NAME));
        x509Data.getXMLObjects().add(buildXMLObject(X509SubjectName.DEFAULT_ELEMENT_NAME));
        x509Data.getXMLObjects().add(buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        x509Data.getXMLObjects().add(buildXMLObject(X509Certificate.DEFAULT_ELEMENT_NAME));
        x509Data.getXMLObjects().add(buildXMLObject(X509Certificate.DEFAULT_ELEMENT_NAME));
        x509Data.getXMLObjects().add(buildXMLObject(X509CRL.DEFAULT_ELEMENT_NAME));
        x509Data.getXMLObjects().add(buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        x509Data.getXMLObjects().add(buildXMLObject(X509SubjectName.DEFAULT_ELEMENT_NAME));
        x509Data.getXMLObjects().add(buildXMLObject(X509CRL.DEFAULT_ELEMENT_NAME));
        x509Data.getXMLObjects().add(buildXMLObject(X509Certificate.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, x509Data);
    }

}
