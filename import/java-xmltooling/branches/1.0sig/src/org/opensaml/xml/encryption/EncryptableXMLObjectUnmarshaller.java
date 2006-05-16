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

import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.w3c.dom.Element;

/**
 * A unmarshaller for {@link org.opensaml.xml.encryption.EncryptableXMLObject} objects that determines if a 
 * given element is encrypted and, if so, decrypts it and makes it available for further unmarshalling.
 */
public interface EncryptableXMLObjectUnmarshaller extends Unmarshaller {

    /**
     * Checks to see if an element is encrypted.
     * 
     * @param domElement the DOM Element to check
     * 
     * @return true if the element is encrypted, false if not
     * 
     * @throws UnmarshallingException thrown if there is an error while determining if the element is encrypted
     */
    public boolean isEncryptedElement(Element domElement) throws UnmarshallingException;
    
    /**
     * Decrypts an encrypted Element.
     * 
     * @param encryptedElement the elent to decrypt
     * 
     * @return the decrypted element
     * 
     * @throws UnmarshallingException thrown if there is an error while decrypting the element
     */
    public Element decryptElement(Element encryptedElement) throws UnmarshallingException;
}