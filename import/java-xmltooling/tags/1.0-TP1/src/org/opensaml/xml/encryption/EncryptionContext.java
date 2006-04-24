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

/**
 * A data construct containing the information needed to encrypt an XML Node.  RSA (v1.5) is used 
 * to encrypt the symmetric key
 * 
 * Default algorithms are:
 * <ul>
 *   <li>Triple DES for key encryption</li>
 *   <li>AES 128 for data encryption</li>
 * </ul>
 */
public class EncryptionContext {

    /** Key used to encrypt/decrypt the symmetric key used to perform the encryption */
    private Key keyEncryptionKey;
    
    /** Algorithm used to encrypt the XML data */
    private String dataEncryptionAlgorithm;
    
    /**
     * Constructor
     */
    public EncryptionContext(){
        dataEncryptionAlgorithm = XMLCipher.AES_128;
    }
    
    /**
     * Gets the key used to encrypt/decrypt the symmetric key used to perform the encryption.
     * 
     * @return the key used to encrypt/decrypt the symmetric key used to perform the encryption
     */
    public Key getKeyEncryptionKey(){
        return keyEncryptionKey;
    }
    
    /**
     * Sets the key used to encrypt/decrypt the symmetric key used to perform the encryption.
     * 
     * @param newKey the key used to encrypt/decrypt the symmetric key used to perform the encryption
     */
    public void setKeyEncryptionKey(Key newKey){
        keyEncryptionKey = newKey;
    }

    /**
     * Gets the algorithm used to encrypt the XML data.
     *  
     * @return the algorithm used to encrypt the XML data
     */
    public String getDataEncryptionAlgorithm() {
        return dataEncryptionAlgorithm;
    }

    /**
     * Sets the algorithm used to encrypt the XML data.
     * 
     * @param newAlgorithm the algorithm used to encrypt the XML data
     */
    public void setDataEncryptionAlgorithm(String newAlgorithm) {
        dataEncryptionAlgorithm = newAlgorithm;
    }
}