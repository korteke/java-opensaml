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

package org.opensaml.xml;

import java.rmi.UnmarshalException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.security.transforms.Transforms;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.mock.SimpleXMLObject;
import org.opensaml.xml.mock.SimpleXMLObjectMarshaller;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SigningContext;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

public class SignatureTest extends XMLObjectBaseTestCase {
    
    /** Signing key */
    private PrivateKey signingKey;

    /** Public key sed to validate signature */
    private PublicKey publicKey;
    
    private String ID;
    
    private Document document;
    
    private Document expectedDocument;
    
    public SignatureTest() {
        
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = keyGen.generateKeyPair();
        signingKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
        
        ID = "Foo";
        
        document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        expectedDocument = parse("/data/org/opensaml/xml/mock/SignedSimpleXMLObject.xml");
    }

    public void testSigning() throws MarshallingException {
        SimpleXMLObject rootXMLObject = new SimpleXMLObject();
        rootXMLObject.setId(ID);
        
        SimpleXMLObject child1 = new SimpleXMLObject();
        rootXMLObject.getSimpleXMLObjects().add(child1);
        
        SimpleXMLObject child2 = new SimpleXMLObject();
        rootXMLObject.getSimpleXMLObjects().add(child2);
        
        SimpleXMLObject child3 = new SimpleXMLObject();
        rootXMLObject.getSimpleXMLObjects().add(child3);
        
        SigningContext dsigCtx = new SigningContext();
        dsigCtx.getTransforms().add(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
        dsigCtx.getTransforms().add(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
        dsigCtx.setSigningKey(signingKey);
        dsigCtx.setPublicKey(publicKey);
        
        Signature signature = new Signature(dsigCtx);
        signature.setId("#" + ID);
        rootXMLObject.setSignature(signature);
        
        SimpleXMLObjectMarshaller marshaller = new SimpleXMLObjectMarshaller(marshallerFactory);
        Element domElement = marshaller.marshall(rootXMLObject, document);
        
        //assertEquals(expectedDocument.getDocumentElement(), domElement);
        System.out.println(elementToString(domElement));
    }
    
    public void testSignatureVerification() throws UnmarshalException{

    }
}
