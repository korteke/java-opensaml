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

import javax.xml.namespace.QName;

import org.opensaml.xml.XMLObject;

/**
 * An XMLObject whose DOM representation can be encrypted.
 */
public interface EncryptableXMLObject extends XMLObject {
    
    /**
     * Gets the name of the element that will hold the encrypted element's data and key info.
     * 
     * @return the name of the element that will hold the encrypted element's data and key info
     */
    public QName getEncryptedElementName();

    /**
     * Gets the encryption context containing information necessary to perform encryption/decryption.
     * 
     * @return the encryption context
     */
    public EncryptionContext getEncryptionContext();
    
    /**
     * Sets the encryption context containing information necessary to perform encryption/decryption.
     * 
     * @param newContext the encryption context
     */
    public void setEncryptionContext(EncryptionContext newContext);
}