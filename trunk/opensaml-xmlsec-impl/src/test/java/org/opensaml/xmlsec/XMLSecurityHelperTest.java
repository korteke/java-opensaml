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
import org.testng.AssertJUnit;
import java.io.InputStream;
import java.security.PrivateKey;

import org.opensaml.security.SecurityHelper;
import org.opensaml.xmlsec.XMLSecurityHelper;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.signature.support.SignatureConstants;

/**
 * Unit test for {@link SecurityHelper}.
 */
public class XMLSecurityHelperTest {

    /** Location of non-encrypted, PEM formatted, RSA private key. */
    private String rsaPrivKeyPEMNoEncrypt = "/data/rsa-privkey-nopass.pem";

    /** Location of non-encrypted, DER formatted, RSA private key. */
    private String rsaPrivKeyDERNoEncrypt = "/data/rsa-privkey-nopass.der";
    
    /** Location of non-encrypted, PEM formatted, dSA private key. */
    private String dsaPrivKeyPEMNoEncrypt = "/data/dsa-privkey-nopass.pem";

    /** Location of non-encrypted, DER formatted, dSA private key. */
    private String dsaPrivKeyDERNoEncrypt = "/data/dsa-privkey-nopass.der";

    /** Password for private key. */
    private char[] privKeyPassword = { 'c', 'h', 'a', 'n', 'g', 'e', 'i', 't' };

    /** Location of encrypted, PEM formatted, RSA private key. */
    private String rsaPrivKeyPEMEncrypt = "/data/rsa-privkey-changeit-pass.pem";

    /** Location of encrypted, PEM formatted, DSA private key. */
    private String dsaPrivKeyPEMEncrypt = "/data/dsa-privkey-changeit-pass.pem";
    
    /** Test mapping algorithm URI's to JCA key algorithm specifiers. */
    @Test
    public void testKeyAlgorithmURIMappings() {
        // Encryption related.
        AssertJUnit.assertEquals("RSA", XMLSecurityHelper.getKeyAlgorithmFromURI(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15));
        AssertJUnit.assertEquals("RSA", XMLSecurityHelper.getKeyAlgorithmFromURI(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP));
        
        AssertJUnit.assertEquals("AES", XMLSecurityHelper.getKeyAlgorithmFromURI(EncryptionConstants.ALGO_ID_KEYWRAP_AES128));
        AssertJUnit.assertEquals("AES", XMLSecurityHelper.getKeyAlgorithmFromURI(EncryptionConstants.ALGO_ID_KEYWRAP_AES192));
        AssertJUnit.assertEquals("AES", XMLSecurityHelper.getKeyAlgorithmFromURI(EncryptionConstants.ALGO_ID_KEYWRAP_AES256));
        AssertJUnit.assertEquals("DESede", XMLSecurityHelper.getKeyAlgorithmFromURI(EncryptionConstants.ALGO_ID_KEYWRAP_TRIPLEDES));
        
        AssertJUnit.assertEquals("AES", XMLSecurityHelper.getKeyAlgorithmFromURI(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128));
        AssertJUnit.assertEquals("AES", XMLSecurityHelper.getKeyAlgorithmFromURI(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192));
        AssertJUnit.assertEquals("AES", XMLSecurityHelper.getKeyAlgorithmFromURI(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256));
        AssertJUnit.assertEquals("DESede", XMLSecurityHelper.getKeyAlgorithmFromURI(EncryptionConstants.ALGO_ID_BLOCKCIPHER_TRIPLEDES));
        
        //Signature related.
        AssertJUnit.assertEquals("RSA", XMLSecurityHelper.getKeyAlgorithmFromURI(SignatureConstants.ALGO_ID_SIGNATURE_RSA));
        AssertJUnit.assertEquals("RSA", XMLSecurityHelper.getKeyAlgorithmFromURI(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1));
        AssertJUnit.assertEquals("RSA", XMLSecurityHelper.getKeyAlgorithmFromURI(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256));
        AssertJUnit.assertEquals("RSA", XMLSecurityHelper.getKeyAlgorithmFromURI(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA384));
        AssertJUnit.assertEquals("RSA", XMLSecurityHelper.getKeyAlgorithmFromURI(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA512));
        AssertJUnit.assertEquals("RSA", XMLSecurityHelper.getKeyAlgorithmFromURI(SignatureConstants.ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5));
        AssertJUnit.assertEquals("RSA", XMLSecurityHelper.getKeyAlgorithmFromURI(SignatureConstants.ALGO_ID_SIGNATURE_RSA_RIPEMD160));
        AssertJUnit.assertEquals("DSA", XMLSecurityHelper.getKeyAlgorithmFromURI(SignatureConstants.ALGO_ID_SIGNATURE_DSA));
        AssertJUnit.assertEquals("ECDSA", XMLSecurityHelper.getKeyAlgorithmFromURI(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA1));
        
        // Mac related.  No specific key algorithm is indicated, any symmetric key will do. Should always return null;
        AssertJUnit.assertNull(XMLSecurityHelper.getKeyAlgorithmFromURI(SignatureConstants.ALGO_ID_MAC_HMAC_SHA1));
        AssertJUnit.assertNull(XMLSecurityHelper.getKeyAlgorithmFromURI(SignatureConstants.ALGO_ID_MAC_HMAC_SHA256));
        AssertJUnit.assertNull(XMLSecurityHelper.getKeyAlgorithmFromURI(SignatureConstants.ALGO_ID_MAC_HMAC_SHA384));
        AssertJUnit.assertNull(XMLSecurityHelper.getKeyAlgorithmFromURI(SignatureConstants.ALGO_ID_MAC_HMAC_SHA512));
        AssertJUnit.assertNull(XMLSecurityHelper.getKeyAlgorithmFromURI(SignatureConstants.ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5));
        AssertJUnit.assertNull(XMLSecurityHelper.getKeyAlgorithmFromURI(SignatureConstants.ALGO_ID_MAC_HMAC_RIPEMD160));
    }    
    
    


    /** Generic key testing. */
    @Test
    protected PrivateKey testPrivKey(String keyFile, char[] password, String algo) throws Exception {
        InputStream keyInS = XMLSecurityHelperTest.class.getResourceAsStream(keyFile);

        byte[] keyBytes = new byte[keyInS.available()];
        keyInS.read(keyBytes);

        PrivateKey key = SecurityHelper.decodePrivateKey(keyBytes, password);
        AssertJUnit.assertNotNull(key);
        AssertJUnit.assertEquals(algo, key.getAlgorithm());
        
        return key;
    }
}