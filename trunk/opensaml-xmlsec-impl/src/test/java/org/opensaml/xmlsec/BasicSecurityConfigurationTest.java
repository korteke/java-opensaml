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

package org.opensaml.xmlsec;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.AssertJUnit;
import java.security.KeyPair;

import org.apache.xml.security.Init;
import org.opensaml.security.SecurityHelper;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.XMLSecurityHelper;
import org.opensaml.xmlsec.config.BasicSecurityConfiguration;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.signature.support.SignatureConstants;

/**
 * Testing some aspects of the basic security config impl.
 */
public class BasicSecurityConfigurationTest {
    
    private BasicSecurityConfiguration config;
    
    private Credential rsaCred;
    private Credential aes128Cred;

    @BeforeMethod
    protected void setUp() throws Exception {
        if (!Init.isInitialized()) {
            Init.init();
        }
        
        KeyPair kp = SecurityHelper.generateKeyPair("RSA", 1024, null);
        rsaCred = SecurityHelper.getSimpleCredential(kp.getPublic(), kp.getPrivate());
        aes128Cred = XMLSecurityHelper.generateKeyAndCredential(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128);
        
        config = new BasicSecurityConfiguration();
    }
    
    @Test
    public void testGetSigAlgURI() {
        AssertJUnit.assertNull(config.getSignatureAlgorithmURI("RSA"));
        AssertJUnit.assertNull(config.getSignatureAlgorithmURI(rsaCred));
        
        config.registerSignatureAlgorithmURI("RSA", SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);
        
        AssertJUnit.assertEquals(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1, config.getSignatureAlgorithmURI("RSA"));
        AssertJUnit.assertEquals(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1, config.getSignatureAlgorithmURI(rsaCred));
    }

    @Test
    public void testGetDataEncURI() {
        AssertJUnit.assertNull(config.getDataEncryptionAlgorithmURI("AES", 128));
        AssertJUnit.assertNull(config.getDataEncryptionAlgorithmURI("AES", 256));
        AssertJUnit.assertNull(config.getDataEncryptionAlgorithmURI("AES", null));
        
        config.registerDataEncryptionAlgorithmURI("AES", 128, EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128);
        config.registerDataEncryptionAlgorithmURI("AES", 256, EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256);
        config.registerDataEncryptionAlgorithmURI("AES", null, EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256);
        
        AssertJUnit.assertEquals(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128, 
                config.getDataEncryptionAlgorithmURI("AES", 128));
        AssertJUnit.assertEquals(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256, 
                config.getDataEncryptionAlgorithmURI("AES", 256));
        AssertJUnit.assertEquals(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256, 
                config.getDataEncryptionAlgorithmURI("AES", null));
        
        AssertJUnit.assertEquals(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128, 
                config.getDataEncryptionAlgorithmURI(aes128Cred));
    }
    
    @Test
    public void testGetKeyTransportEncURI() {
        AssertJUnit.assertNull(config.getKeyTransportEncryptionAlgorithmURI("RSA", null, "AES"));
        AssertJUnit.assertNull(config.getKeyTransportEncryptionAlgorithmURI("RSA", null, "DESede"));
        AssertJUnit.assertNull(config.getKeyTransportEncryptionAlgorithmURI("RSA", null, null));
        AssertJUnit.assertNull(config.getKeyTransportEncryptionAlgorithmURI("AES", 256, "AES"));
        AssertJUnit.assertNull(config.getKeyTransportEncryptionAlgorithmURI("AES", 256, "DESede"));
        
        config.registerKeyTransportEncryptionAlgorithmURI("RSA", null, "AES", 
                EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        config.registerKeyTransportEncryptionAlgorithmURI("RSA", null, "DESede", 
                EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
        config.registerKeyTransportEncryptionAlgorithmURI("RSA", null, null, 
                EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
        config.registerKeyTransportEncryptionAlgorithmURI("AES", 128, null, 
                EncryptionConstants.ALGO_ID_KEYWRAP_AES128);
        config.registerKeyTransportEncryptionAlgorithmURI("AES", 256, null, 
                EncryptionConstants.ALGO_ID_KEYWRAP_AES256);
        config.registerKeyTransportEncryptionAlgorithmURI("AES", null, "AES", 
                EncryptionConstants.ALGO_ID_KEYWRAP_AES128);
        config.registerKeyTransportEncryptionAlgorithmURI("AES", null, "DESede", 
                EncryptionConstants.ALGO_ID_KEYWRAP_AES256);
        
        AssertJUnit.assertEquals(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP, 
                config.getKeyTransportEncryptionAlgorithmURI("RSA", null, "AES"));
        AssertJUnit.assertEquals(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15, 
                config.getKeyTransportEncryptionAlgorithmURI("RSA", null, "DESede"));
        AssertJUnit.assertEquals(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15, 
                config.getKeyTransportEncryptionAlgorithmURI("RSA", null, "FOO"));
        AssertJUnit.assertEquals(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15, 
                config.getKeyTransportEncryptionAlgorithmURI("RSA", null, null));
        
        AssertJUnit.assertEquals(EncryptionConstants.ALGO_ID_KEYWRAP_AES128,
                config.getKeyTransportEncryptionAlgorithmURI("AES", 128, null));
        AssertJUnit.assertEquals(EncryptionConstants.ALGO_ID_KEYWRAP_AES256,
                config.getKeyTransportEncryptionAlgorithmURI("AES", 256, null));
        AssertJUnit.assertEquals(EncryptionConstants.ALGO_ID_KEYWRAP_AES128,
                config.getKeyTransportEncryptionAlgorithmURI("AES", null, "AES"));
        AssertJUnit.assertEquals(EncryptionConstants.ALGO_ID_KEYWRAP_AES256,
                config.getKeyTransportEncryptionAlgorithmURI("AES", null, "DESede"));
        
        AssertJUnit.assertEquals(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP, 
                config.getKeyTransportEncryptionAlgorithmURI(rsaCred, "AES"));
        AssertJUnit.assertEquals(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15, 
                config.getKeyTransportEncryptionAlgorithmURI(rsaCred, "DESede"));
        AssertJUnit.assertEquals(EncryptionConstants.ALGO_ID_KEYWRAP_AES128,
                config.getKeyTransportEncryptionAlgorithmURI(aes128Cred, null));
        
    }
}
