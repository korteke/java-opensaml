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

import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.w3c.dom.Element;

/**
 * A marshaller for {@link org.opensaml.xml.encryption.EncryptableXMLObject} objects that creates a symmetric key
 * uses it to encrypt the XML content and encrypts the key with a given asymmetric key.
 */
public interface EncryptableXMLObjectMarshaller extends Marshaller {

    /**
     * Encrypts the given element with information stored in the XMLObject.  It is very common to place the results of 
     * the encrypted element into an element with a different (though often related) name, for example, an element with a 
     * local name of Foo might be encrypted and the results placed in an element named EncryptedFoo.  If the {@link EncryptableXMLObject} 
     * given here returns a name from {@link EncryptableXMLObject#getEncryptedElementName()} the marshaller will create a new element with 
     * that name and place the results of the encryption of the given Element within it.  This resulting encrypted element will be owned 
     * by the Document owning the given Element.  If {@link EncryptableXMLObject#getEncryptedElementName()} return null the result of 
     * the encryption of the given Element will replace the current child nodes of given element.
     * 
     * @param element the element to be encrypted
     * @param xmlObject the encryptable XMLObject
     * 
     * @return the encrypted element
     * 
     * @throws MarshallingException thrown if the element can not be encrypted
     */
    public Element encryptElement(Element element, EncryptableXMLObject xmlObject) throws MarshallingException;
}
