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


import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.X509Digest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class X509DigestTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedAlgorithm;
    
    private String expectedStringContent;
    
    /**
     * Constructor.
     *
     */
    public X509DigestTest() {
        singleElementFile = "/org/opensaml/xmlsec/signature/impl/X509Digest.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedAlgorithm = SignatureConstants.ALGO_ID_DIGEST_SHA1;
        expectedStringContent = "someX509Digest";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        X509Digest digest = (X509Digest) unmarshallElement(singleElementFile);
        
        Assert.assertNotNull(digest, "X509Digest");
        Assert.assertEquals(expectedAlgorithm, digest.getAlgorithm(), "Algorithm attribute");
        Assert.assertEquals(digest.getValue(), expectedStringContent, "X509Digest value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        X509Digest digest = (X509Digest) buildXMLObject(X509Digest.DEFAULT_ELEMENT_NAME);
        
        digest.setValue(expectedStringContent);
        digest.setAlgorithm(expectedAlgorithm);
        
        assertXMLEquals(expectedDOM, digest);
    }
    
}