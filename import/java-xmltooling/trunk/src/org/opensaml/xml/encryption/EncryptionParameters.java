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

import org.opensaml.xml.signature.KeyInfo;

/**
 * Parameters for encrypting content. 
 */
public class EncryptionParameters {

    /** Key used to encrypt. */
    private Key encryptionKey;

    /** Algorithm used to encrypt. */
    private String algorithm;

    /** Information about the decryption key. */
    private KeyInfo keyInfo;

    /**
     * Constructor.
     */
    public EncryptionParameters() {
        algorithm = EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128;
    }

    /**
     * Gets the algorithm used to encrypt.
     * 
     * @return the algorithm used to encrypt
     */
    public String getAlgorithm() {
        return this.algorithm;
    }

    /**
     * Sets the algorithm used to encrypt.
     * 
     * @param newAlgorithm the algorithm used to encrypt
     */
    public void setAlgorithm(String newAlgorithm) {
        this.algorithm = newAlgorithm;
    }

    /**
     * Gets the key used to encrypt.
     * 
     * @return the key used to encrypt
     */
    public Key getEncryptionKey() {
        return this.encryptionKey;
    }

    /**
     * Sets the key used to encrypt.
     * 
     * @param newEncryptionKey the key used to encrypt
     */
    public void setEncryptionKey(Key newEncryptionKey) {
        this.encryptionKey = newEncryptionKey;
    }

    /**
     * Gets the information about the decryption key.
     * 
     * @return the information about the decryption key
     */
    public KeyInfo getKeyInfo() {
        return this.keyInfo;
    }

    /**
     * Sets the information about the decryption key.
     * 
     * @param newKeyInfo the information about the decryption key
     */
    public void setKeyInfo(KeyInfo newKeyInfo) {
        this.keyInfo = newKeyInfo;
    }
}