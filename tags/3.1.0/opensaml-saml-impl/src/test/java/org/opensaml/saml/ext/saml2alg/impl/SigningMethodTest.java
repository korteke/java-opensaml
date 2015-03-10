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

package org.opensaml.saml.ext.saml2alg.impl;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.opensaml.saml.ext.saml2alg.SigningMethod;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Unit test for {@link SigningMethod}. */
public class SigningMethodTest extends XMLObjectProviderBaseTestCase {

    /** Constructor. */
    public SigningMethodTest() {
        singleElementFile = "/data/org/opensaml/saml/ext/saml2alg/impl/SigningMethod.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml/ext/saml2alg/impl/SigningMethodOptionalAttributes.xml";
        childElementsFile = "/data/org/opensaml/saml/ext/saml2alg/impl/SigningMethodChildElements.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        SigningMethod signingMethod = (SigningMethod) unmarshallElement(singleElementFile);
        Assert.assertNotNull(signingMethod);
        Assert.assertEquals(signingMethod.getAlgorithm(), SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
    }
    
    

    /** {@inheritDoc} */
    @Test public void testSingleElementOptionalAttributesUnmarshall() {
        SigningMethod signingMethod = (SigningMethod) unmarshallElement(singleElementOptionalAttributesFile);
        Assert.assertNotNull(signingMethod);
        Assert.assertEquals(signingMethod.getAlgorithm(), SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        Assert.assertEquals(signingMethod.getMinKeySize(), new Integer(2048));
        Assert.assertEquals(signingMethod.getMaxKeySize(), new Integer(4096));
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        SigningMethod signingMethod = (SigningMethod) unmarshallElement(childElementsFile);
        Assert.assertNotNull(signingMethod);
        Assert.assertEquals(signingMethod.getAlgorithm(), SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        Assert.assertEquals(signingMethod.getUnknownXMLObjects().size(), 3);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        SigningMethod signingMethod = (SigningMethod) buildXMLObject(SigningMethod.DEFAULT_ELEMENT_NAME);
        signingMethod.setAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);

        assertXMLEquals(expectedDOM, signingMethod);
    }
    

    /** {@inheritDoc} */
    @Test public void testSingleElementOptionalAttributesMarshall() {
        SigningMethod signingMethod = (SigningMethod) buildXMLObject(SigningMethod.DEFAULT_ELEMENT_NAME);
        signingMethod.setAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        signingMethod.setMinKeySize(2048);
        signingMethod.setMaxKeySize(4096);

        assertXMLEquals(expectedOptionalAttributesDOM, signingMethod);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        SigningMethod signingMethod = (SigningMethod) buildXMLObject(SigningMethod.DEFAULT_ELEMENT_NAME);
        signingMethod.setAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        
        signingMethod.getUnknownXMLObjects().add(buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        signingMethod.getUnknownXMLObjects().add(buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        signingMethod.getUnknownXMLObjects().add(buildXMLObject(SimpleXMLObject.ELEMENT_NAME));

        assertXMLEquals(expectedChildElementsDOM, signingMethod);
    }
}