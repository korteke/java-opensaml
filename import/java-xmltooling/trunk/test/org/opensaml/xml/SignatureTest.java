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

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.xml.security.Init;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.mock.SimpleXMLObject;
import org.opensaml.xml.mock.SimpleXMLObjectMarshaller;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureMarshaller;
import org.opensaml.xml.util.XMLConstants;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

public class SignatureTest extends TestCase {

    private MarshallerFactory<QName, Marshaller<XMLObject>> marshallerFactory;
    
    /** Signing key */
    private PrivateKey signingKey;

    /** Public key sed to validate signature */
    private PublicKey publicKey;
    
    private String ID;
    
    private Document document;
    
    protected void setUp() throws Exception {
        super.setUp();
        
        Init.init();
        
        marshallerFactory = new MarshallerFactory<QName, Marshaller<XMLObject>>();
        marshallerFactory.registerMarshaller(new QName(SimpleXMLObject.NAMESAPACE, SimpleXMLObject.LOCAL_NAME), new SimpleXMLObjectMarshaller(marshallerFactory));
        marshallerFactory.registerMarshaller(new QName(XMLConstants.XMLSIG_NS, Signature.LOCAL_NAME), new SignatureMarshaller());
        
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = keyGen.generateKeyPair();
        signingKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
        
        ID = "Foo";
        
        document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
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
        
        SigningContext dsigCtx = new SigningContext(SimpleXMLObject.ID_ATTRIB_NAME, ID);
        dsigCtx.setSigningKey(signingKey);
        dsigCtx.setPublicKey(publicKey);
        rootXMLObject.setSigningContext(dsigCtx);
        
        rootXMLObject.setSignature(new Signature());
        
        SimpleXMLObjectMarshaller marshaller = new SimpleXMLObjectMarshaller(marshallerFactory);
        Element domElement = marshaller.marshall(rootXMLObject, document);
        System.out.println(elementToString(domElement));
    }
    
    public String elementToString(Element domElement) {
        DOMImplementation domImpl = domElement.getOwnerDocument().getImplementation();
        DOMImplementationLS domImplLS = (DOMImplementationLS) domImpl.getFeature("LS", "3.0");
        LSSerializer serializer = domImplLS.createLSSerializer();
        return serializer.writeToString(domElement);
    }
}
