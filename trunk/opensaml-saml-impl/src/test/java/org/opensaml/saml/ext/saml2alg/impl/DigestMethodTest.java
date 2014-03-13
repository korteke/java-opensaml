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
import org.opensaml.saml.ext.saml2alg.DigestMethod;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Unit test for {@link DigestMethod}. */
public class DigestMethodTest extends XMLObjectProviderBaseTestCase {

    /** Constructor. */
    public DigestMethodTest() {
        singleElementFile = "/data/org/opensaml/saml/ext/saml2alg/impl/DigestMethod.xml";
        childElementsFile = "/data/org/opensaml/saml/ext/saml2alg/impl/DigestMethodChildElements.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        DigestMethod digestMethod = (DigestMethod) unmarshallElement(singleElementFile);
        Assert.assertNotNull(digestMethod);
        Assert.assertEquals(digestMethod.getAlgorithm(), SignatureConstants.ALGO_ID_DIGEST_SHA256);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        DigestMethod digestMethod = (DigestMethod) unmarshallElement(childElementsFile);
        Assert.assertNotNull(digestMethod);
        Assert.assertEquals(digestMethod.getAlgorithm(), SignatureConstants.ALGO_ID_DIGEST_SHA256);
        Assert.assertEquals(digestMethod.getUnknownXMLObjects().size(), 3);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        DigestMethod digestMethod = (DigestMethod) buildXMLObject(DigestMethod.DEFAULT_ELEMENT_NAME);
        digestMethod.setAlgorithm(SignatureConstants.ALGO_ID_DIGEST_SHA256);

        assertXMLEquals(expectedDOM, digestMethod);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        DigestMethod digestMethod = (DigestMethod) buildXMLObject(DigestMethod.DEFAULT_ELEMENT_NAME);
        digestMethod.setAlgorithm(SignatureConstants.ALGO_ID_DIGEST_SHA256);
        
        digestMethod.getUnknownXMLObjects().add(buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        digestMethod.getUnknownXMLObjects().add(buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        digestMethod.getUnknownXMLObjects().add(buildXMLObject(SimpleXMLObject.ELEMENT_NAME));

        assertXMLEquals(expectedChildElementsDOM, digestMethod);
    }
}