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

import javax.crypto.Cipher;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.security.SecurityProviderTestSupport;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.algorithm.AlgorithmDescriptor;
import org.opensaml.xmlsec.algorithm.AlgorithmRegistry;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.algorithm.KeyLengthSpecifiedAlgorithm;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.keyinfo.impl.StaticKeyInfoCredentialResolver;
import org.opensaml.xmlsec.mock.SignableSimpleXMLObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 */
public class AESGCMTest extends XMLObjectBaseTestCase {
    
    private Logger log = LoggerFactory.getLogger(AESGCMTest.class);
    
    private String targetFile;
    
    private SecurityProviderTestSupport providerSupport;
    
    public AESGCMTest() {
        super();
        
        providerSupport = new SecurityProviderTestSupport();
        
        targetFile = "/data/org/opensaml/xmlsec/encryption/support/SimpleEncryptionTest.xml";
    }
    
    @DataProvider
    public Object[][] testDataAESGCM() {
        AlgorithmRegistry registry = AlgorithmSupport.getGlobalAlgorithmRegistry();
        AlgorithmDescriptor aesGCM128 = registry.get(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM);
        AlgorithmDescriptor aesGCM192 = registry.get(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192_GCM);
        AlgorithmDescriptor aesGCM256 = registry.get(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM);
        
        return new Object[][] {
                new Object[] {aesGCM128, 7, true},
                new Object[] {aesGCM192, 7, true},
                new Object[] {aesGCM256, 7, true},
                
                new Object[] {aesGCM128, 8, false},
                new Object[] {aesGCM192, 8, false},
                new Object[] {aesGCM256, 8, false},
                
                new Object[] {aesGCM128, 8, true},
                new Object[] {aesGCM192, 8, true},
                new Object[] {aesGCM256, 8, true},
        };
    }

    @Test(dataProvider="testDataAESGCM")
    public void testEncryptDecrypt(AlgorithmDescriptor descriptor, int minJavaVersion, boolean loadBC) throws Exception {
        if (!providerSupport.haveJavaGreaterOrEqual(minJavaVersion)) {
            log.debug("Not Java {}+, skipping test", minJavaVersion);
            return;
        }
        
        int maxKeyLength = Cipher.getMaxAllowedKeyLength(descriptor.getJCAAlgorithmID());
        log.debug("Installed policy indicates max allowed key length for '{}' is: {}", descriptor.getJCAAlgorithmID(), maxKeyLength);
        if (descriptor instanceof KeyLengthSpecifiedAlgorithm 
                && ((KeyLengthSpecifiedAlgorithm)descriptor).getKeyLength() > maxKeyLength) {
            log.debug("Key length {} will exceed max key length {}", 
                    ((KeyLengthSpecifiedAlgorithm)descriptor).getKeyLength(), maxKeyLength);
        } else {
            log.debug("Key length {} is ok for max key length {}", 
                    ((KeyLengthSpecifiedAlgorithm)descriptor).getKeyLength(), maxKeyLength);
        }
        
        try {
            if (loadBC) {
                providerSupport.loadBC();
            }
        
            SignableSimpleXMLObject sxo = (SignableSimpleXMLObject) unmarshallElement(targetFile);
            
            Credential encCred = AlgorithmSupport.generateSymmetricKeyAndCredential(descriptor.getURI());
            
            DataEncryptionParameters encParams = new DataEncryptionParameters();
            encParams.setAlgorithm(descriptor.getURI());
            encParams.setEncryptionCredential(encCred);
            
            Encrypter encrypter = new Encrypter();
            
            EncryptedData encryptedData = encrypter.encryptElement(sxo, encParams);
            
            Assert.assertNotNull(encryptedData);
            Assert.assertEquals(encryptedData.getEncryptionMethod().getAlgorithm(), descriptor.getURI());
            
            StaticKeyInfoCredentialResolver dataKeyInfoResolver = new StaticKeyInfoCredentialResolver(encCred);
            
            Decrypter decrypter = new Decrypter(dataKeyInfoResolver, null, null);
            
            XMLObject decryptedXMLObject = decrypter.decryptData(encryptedData);
            
            Assert.assertNotNull(decryptedXMLObject);
            Assert.assertTrue(decryptedXMLObject instanceof SignableSimpleXMLObject);
            assertXMLEquals(sxo.getDOM().getOwnerDocument(), decryptedXMLObject);
            
        } finally {
            providerSupport.unloadBC();
        }
        
        
    }
}
