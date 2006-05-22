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

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import org.opensaml.xml.XMLObjectBaseTestCase;
import org.opensaml.xml.encryption.EncryptionContext;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.mock.SimpleXMLObject;
import org.w3c.dom.Element;

/**
 * Tests the encryption support in this library.
 */
public class EncryptionTest extends XMLObjectBaseTestCase {
    
    /**
     * Constructor
     */
    public EncryptionTest(){

    }
    
    /**
     * Tests encryption and then decrypting an XML fragment.
     * 
     * @throws GeneralSecurityException
     * @throws MarshallingException
     * @throws UnmarshallingException 
     */
    public void testEncryption() throws GeneralSecurityException, MarshallingException, UnmarshallingException {
//        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
//        
//        SimpleXMLObject xmlObject = new SimpleXMLObject();
//        
//        SimpleXMLObject child1 = new SimpleXMLObject();
//        xmlObject.getSimpleXMLObjects().add(child1);
//        
//        SimpleXMLObject child2 = new SimpleXMLObject();
//        xmlObject.getSimpleXMLObjects().add(child2);
//        
//        EncryptionContext encryptionContext = new EncryptionContext();
//        encryptionContext.setKeyEncryptionKey(keyPair.getPublic());
//        xmlObject.setEncryptionContext(encryptionContext);
//        
//        // Marshall & encrypt
//        Marshaller marshaller = marshallerFactory.getMarshaller(xmlObject);
//        Element domElement = marshaller.marshall(xmlObject);
//        
//        // Unmarshall and decrypt
//        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(domElement);
//        unmarshaller.unmarshall(domElement);
    }
}