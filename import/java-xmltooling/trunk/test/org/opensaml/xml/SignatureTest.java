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

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import javax.xml.namespace.QName;

import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.mock.SimpleXMLObject;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SigningContext;
import org.opensaml.xml.signature.impl.XMLSecSignatureBuilder;
import org.opensaml.xml.util.XMLConstants;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Element;

public class SignatureTest extends XMLObjectBaseTestCase {

    /** QName for XMLSecSignatureImpl element */
    private QName signatureQName;

    /** QName for SimpleXMLObject */
    private QName simpleXMLObjectQName;
    
    /** ID used for signing URI reference */
    private String ID;

    /**
     * Constructor
     * @throws ConfigurationException 
     * @throws XMLParserException 
     */
    public SignatureTest() throws XMLParserException, ConfigurationException{
        super();
        
        signatureQName = new QName(XMLConstants.XMLSIG_NS, Signature.LOCAL_NAME);
        simpleXMLObjectQName = new QName(SimpleXMLObject.NAMESAPACE, SimpleXMLObject.LOCAL_NAME);
        
        ID = "Foo";
    }
    
    /*
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Tests that a signature can be created and verified with an RSA key pair.
     * 
     * @throws MarshallingException thrown if the signature can not be created
     * @throws UnmarshallingException thrown if the signature can be verified
     * @throws XMLParserException thrown if a document can not be created to root the marshalled XMLObject in
     * @throws NoSuchAlgorithmException thrown if RSA keys are not supporter
     */
    public void testRSAPublicKeySignature() throws MarshallingException, UnmarshallingException, XMLParserException, NoSuchAlgorithmException {
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        SimpleXMLObject xmlObject = getXMLSignableObject(keyPair.getPrivate(), keyPair.getPublic());
        
        // Marshall & sign
        Marshaller marshaller = marshallerFactory.getMarshaller(simpleXMLObjectQName);
        Element domElement = marshaller.marshall(xmlObject, parserPool.newDocument());

        // Unmarshall and verify
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(simpleXMLObjectQName);
        unmarshaller.unmarshall(domElement);
    }
    
    /**
     * Tests that a signature can be created and verified with an DSA key pair.
     * 
     * @throws MarshallingException thrown if the signature can not be created
     * @throws UnmarshallingException thrown if the signature can be verified
     * @throws XMLParserException thrown if a document can not be created to root the marshalled XMLObject in
     * @throws NoSuchAlgorithmException thrown if DSA keys are not supporter
     */
    public void testDSAPublicKeySignature()  throws MarshallingException, UnmarshallingException, XMLParserException, NoSuchAlgorithmException {
        KeyPair keyPair = KeyPairGenerator.getInstance("DSA").generateKeyPair();
        SimpleXMLObject xmlObject = getXMLSignableObject(keyPair.getPrivate(), keyPair.getPublic());
        xmlObject.getSignature().getSigningContext().setSignatureAlgorithm(XMLSignature.ALGO_ID_SIGNATURE_DSA);
        
        // Marshall & sign
        Marshaller marshaller = marshallerFactory.getMarshaller(simpleXMLObjectQName);
        Element domElement = marshaller.marshall(xmlObject, parserPool.newDocument());

        // Unmarshall and verify
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(simpleXMLObjectQName);
        unmarshaller.unmarshall(domElement);
    }
    
    /**
     * Tests that a signature can be created with a RSA key pair and validated with a 
     * certificate containing the public key.
     * 
     * @throws GeneralSecurityException
     * @throws IOException 
     * @throws XMLParserException 
     * @throws MarshallingException 
     * @throws UnmarshallingException 
     *
     */
    public void testCertificateSignature() throws GeneralSecurityException, IOException, XMLParserException, MarshallingException, UnmarshallingException{
        KeyStore keystore = KeyStore.getInstance("JKS");
        char[] password = new String("insecure").toCharArray();
        keystore.load(SignatureTest.class.getResourceAsStream("/data/org/opensaml/xml/keys.jks"), password);
        
        PrivateKey signingKey = (PrivateKey) keystore.getKey("opensaml2testkeys", password);
        X509Certificate certificate = (X509Certificate) keystore.getCertificate("opensaml2testkeys");
        
        SimpleXMLObject xmlObject = getXMLObject();
        SigningContext signingContext = getSigningContext();
        signingContext.setSigningKey(signingKey);
        signingContext.getCertificates().add(certificate);
        
        XMLSecSignatureBuilder sigBuilder = (XMLSecSignatureBuilder) builderFactory.getBuilder(signatureQName);
        sigBuilder.setSigningContext(signingContext);
        Signature signature = (Signature) sigBuilder.buildObject();
        signature.setReferenceURI("#" + ID);
        xmlObject.setSignature(signature);
        
        // Marshall & sign
        Marshaller marshaller = marshallerFactory.getMarshaller(simpleXMLObjectQName);
        Element domElement = marshaller.marshall(xmlObject, parserPool.newDocument());
        System.out.println(XMLHelper.nodeToString(domElement));

        // Unmarshall and verify
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(simpleXMLObjectQName);
        unmarshaller.unmarshall(domElement);
    }
    
    /**
     * Creates an XMLObject that is ready to be signed.
     * 
     * @param signingKey key to use for signing
     * @param publicKey key to use for validating
     * 
     * @return an XMLObject that is read to be signed
     */
    private SimpleXMLObject getXMLSignableObject(PrivateKey signingKey, PublicKey publicKey){
        SimpleXMLObject xmlObject = getXMLObject();

        SigningContext signingContext = getSigningContext();
        signingContext.setSigningKey(signingKey);
        signingContext.setPublicKey(publicKey);
        
        XMLObjectBuilder sigBuilder = builderFactory.getBuilder(signatureQName);
        Signature signature = (Signature) sigBuilder.buildObject();
        signature.setReferenceURI("#" + ID);
        signature.setSigningContext(signingContext);
        xmlObject.setSignature(signature);
        
        return xmlObject;
    }
    
    /**
     * Creates a simple tree of XMLObjects which could be signed.
     * 
     * @return a simple tree of XMLObjects which could be signed
     */
    private SimpleXMLObject getXMLObject(){
        SimpleXMLObject xmlObject = new SimpleXMLObject();
        xmlObject.setId(ID);
        
        SimpleXMLObject child1 = new SimpleXMLObject();
        xmlObject.getSimpleXMLObjects().add(child1);
        
        SimpleXMLObject child2 = new SimpleXMLObject();
        xmlObject.getSimpleXMLObjects().add(child2);
        
        SimpleXMLObject child3 = new SimpleXMLObject();
        xmlObject.getSimpleXMLObjects().add(child3);
        
        return xmlObject;
    }

    /**
     * Creates a signing context with a base set of transforms.
     * 
     * @return a signing context with a base set of transforms
     */
    private SigningContext getSigningContext(){
        SigningContext context = new SigningContext();
        context.getTransforms().add(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
        context.getTransforms().add(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
        
        return context;
    }
}