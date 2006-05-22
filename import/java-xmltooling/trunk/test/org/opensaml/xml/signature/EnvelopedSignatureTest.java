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

package org.opensaml.xml.signature;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.xml.namespace.QName;

import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.signature.XMLSignature;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.ElementProxy;
import org.opensaml.xml.ElementProxyBuilder;
import org.opensaml.xml.XMLObjectBaseTestCase;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.mock.SimpleXMLObject;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Element;

/**
 * Test to verify {@link org.opensaml.xml.signature.Signature} and its marshallers and unmarshallers
 */
public class EnvelopedSignatureTest extends XMLObjectBaseTestCase {

    /** Key used for signing */
    private PrivateKey signingKey;
    
    /** Key used for verification */
    private PublicKey verificationKey;
    
    /** Builder of mock XML objects */
    private ElementProxyBuilder epBuilder;
    
    /** Builder of Signature XML objects */
    private SignatureBuilder sigBuilder;
    
    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
        
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair keyPair = keyGen.generateKeyPair();
        signingKey = keyPair.getPrivate();
        verificationKey = keyPair.getPublic();
        
        epBuilder = new ElementProxyBuilder();
        sigBuilder = new SignatureBuilder();
    }

    /**
     * Tests creating an enveloped signature and then verifying it
     * @throws MarshallingException 
     */
    public void testSigningAndVerification() throws MarshallingException{
        ElementProxy elementProxy = getXMLObjectWithSignature();
        Marshaller marshaller = Configuration.getMarshallerFactory().getMarshaller(elementProxy);
        Element signedElement = marshaller.marshall(elementProxy);
        
        Signer.signObject((Signature) elementProxy.getUnknownXMLObjects().get(0));
        
        XMLHelper.nodeToString(signedElement);
    }
    
    /**
     * Tests unmarshalling an enveloped signature
     */
    public void testUnmarshallSignature(){
        
    }
    
    /**
     * Creates a ElementProxy that has a Signature child element.
     * 
     * @return a ElementProxy that has a Signature child element
     */
    private ElementProxy getXMLObjectWithSignature(){
        ElementProxy elementProxy = epBuilder.buildObject("FOOSPACE", "FOO", );
        elementProxy.getUnknownAttributes().put(new QName("ID"), "foo");
        
        Signature sig = sigBuilder.buildObject();
        sig.setSigningKey(signingKey);
        sig.setCanonicalizationAlgorithm(Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        sig.setSignatureAlgorithm(XMLSignature.ALGO_ID_SIGNATURE_RSA);
        
        elementProxy.getUnknownXMLObjects().add(sig);
        return elementProxy;
    }
}