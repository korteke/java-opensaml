/*
 * Copyright [2006] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.xml.encryption;

import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;

import org.apache.xml.security.algorithms.JCEMapper;

/**
 * Class of utility methods for doing encryption/decryption tests.
 */
public class EncryptionTestHelper {

    /**
     * Constructor.
     *
     */
    protected EncryptionTestHelper() {
    }
    
    /**
     * Randomly generates a Java JCE Key object from the specified XML Encryption algorithm URI.
     * Note: This is for symmetric keys only, not key pairs or public/private keys.
     * 
     * @param algoURI  The XML Encryption algorithm URI
     * @return a randomly-generated Key
     */
    public static Key generateKey(String algoURI) {
        String jceAlgorithmName = JCEMapper.getJCEKeyAlgorithmFromURI(algoURI);
        int keyLength = JCEMapper.getKeyLengthFromURI(algoURI);
        Key key = null;
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(jceAlgorithmName);
            keyGenerator.init(keyLength);
            key = keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return key;
    }

}
