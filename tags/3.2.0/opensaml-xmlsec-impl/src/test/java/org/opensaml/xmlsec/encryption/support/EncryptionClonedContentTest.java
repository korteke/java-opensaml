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

package org.opensaml.xmlsec.encryption.support;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.keyinfo.impl.StaticKeyInfoCredentialResolver;
import org.opensaml.xmlsec.mock.SignableSimpleXMLObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 */
public class EncryptionClonedContentTest extends XMLObjectBaseTestCase {
    
    private String targetFile;
    
    private String algoURI = EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128;
    
    public EncryptionClonedContentTest() {
        super();
        
        targetFile = "/data/org/opensaml/xmlsec/encryption/support/SimpleEncryptionTest.xml";
    }

    @Test
    public void testEncryptDecrypt() throws Exception {
            SignableSimpleXMLObject origXMLObject = (SignableSimpleXMLObject) unmarshallElement(targetFile);
            SignableSimpleXMLObject clonedXMLObject = XMLObjectSupport.cloneXMLObject(origXMLObject);
            
            Credential encCred = AlgorithmSupport.generateSymmetricKeyAndCredential(algoURI);
            
            DataEncryptionParameters encParams = new DataEncryptionParameters();
            encParams.setAlgorithm(algoURI);
            encParams.setEncryptionCredential(encCred);
            
            Encrypter encrypter = new Encrypter();
            
            EncryptedData encryptedData = encrypter.encryptElement(clonedXMLObject, encParams);
            
            Assert.assertNotNull(encryptedData);
            Assert.assertEquals(encryptedData.getEncryptionMethod().getAlgorithm(), algoURI);
            
            StaticKeyInfoCredentialResolver dataKeyInfoResolver = new StaticKeyInfoCredentialResolver(encCred);
            
            Decrypter decrypter = new Decrypter(dataKeyInfoResolver, null, null);
            
            XMLObject decryptedXMLObject = decrypter.decryptData(encryptedData);
            
            Assert.assertNotNull(decryptedXMLObject);
            Assert.assertTrue(decryptedXMLObject instanceof SignableSimpleXMLObject);
            assertXMLEquals(origXMLObject.getDOM().getOwnerDocument(), decryptedXMLObject);
    }
}
