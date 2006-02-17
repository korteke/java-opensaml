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

import org.apache.xml.security.transforms.Transforms;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.mock.SimpleXMLObject;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureBuilder;
import org.opensaml.xml.signature.SigningContext;
import org.opensaml.xml.util.XMLConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SignatureTest extends XMLObjectBaseTestCase {
    
    private QName signatureQName;
    
    private QName simpleXMLObjectQName;
    
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
        
        signatureQName = new QName(XMLConstants.XMLSIG_NS, Signature.LOCAL_NAME);
        simpleXMLObjectQName = new QName(SimpleXMLObject.NAMESAPACE, SimpleXMLObject.LOCAL_NAME);
        
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
        
        SignatureBuilder sigBuilder = (SignatureBuilder) builderFactory.getBuilder(signatureQName);
        sigBuilder.setSigningContext(dsigCtx);
        Signature signature = (Signature) sigBuilder.buildObject();
        signature.setId(ID);
        rootXMLObject.setSignature(signature);
        
        Marshaller marshaller = marshallerFactory.getMarshaller(simpleXMLObjectQName);
        Element domElement = marshaller.marshall(rootXMLObject, document);
        
        //assertEquals(expectedDocument.getDocumentElement(), domElement);
        System.out.println(elementToString(domElement));
    }
    
    public void testSignatureVerification() throws UnmarshallingException{
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(simpleXMLObjectQName);
        
        SimpleXMLObject simpleXMLObject = (SimpleXMLObject) unmarshaller.unmarshall(expectedDocument.getDocumentElement());
        System.out.println(simpleXMLObject.getSignature().getXMLSignature());
    }
}
