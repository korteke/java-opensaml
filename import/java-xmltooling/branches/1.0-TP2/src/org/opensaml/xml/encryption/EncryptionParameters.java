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

import org.apache.xml.security.encryption.XMLCipher;
import org.opensaml.xml.signature.KeyInfo;

/**
 * Parameters for encrypting content. 
 */
public class EncryptionParameters {

    /** Key used to encrypt */
    private Key encryptionKey;

    /** Algorithm used to encrypt */
    private String algorithm;

    /** Information about the decryption key */
    private KeyInfo keyInfo;

    /**
     * Constructor
     */
    public EncryptionParameters() {
        algorithm = XMLCipher.AES_128;
    }

    /**
     * Gets the algorithm used to encrypt.
     * 
     * @return the algorithm used to encrypt
     */
    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * Sets the algorithm used to encrypt.
     * 
     * @param algorithm the algorithm used to encrypt
     */
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * Gets the key used to encrypt.
     * 
     * @return the key used to encrypt
     */
    public Key getEncryptionKey() {
        return encryptionKey;
    }

    /**
     * Sets the key used to encrypt.
     * 
     * @param encryptionKey the key used to encrypt
     */
    public void setEncryptionKey(Key encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    /**
     * Gets the information about the decryption key.
     * 
     * @return the information about the decryption key
     */
    public KeyInfo getKeyInfo() {
        return keyInfo;
    }

    /**
     * Sets the information about the decryption key.
     * 
     * @param keyInfo the information about the decryption key
     */
    public void setKeyInfo(KeyInfo keyInfo) {
        this.keyInfo = keyInfo;
    }
}