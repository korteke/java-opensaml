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

import org.apache.log4j.Logger;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.signature.XMLSignature;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObjectBaseTestCase;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.mock.SimpleXMLObject;
import org.opensaml.xml.mock.SimpleXMLObjectBuilder;
import org.opensaml.xml.util.XMLHelper;
import org.opensaml.xml.validation.ValidationException;
import org.w3c.dom.Element;

/**
 * Test to verify {@link org.opensaml.xml.signature.Signature} and its marshallers and unmarshallers
 */
public class EnvelopedSignatureTest extends XMLObjectBaseTestCase {
    
    /** Logger */
    private static Logger log = Logger.getLogger(EnvelopedSignatureTest.class);

    /** Key used for signing */
    private PrivateKey signingKey;
    
    /** Key used for verification */
    private PublicKey verificationKey;
    
    /** Verification key that should fail to verify signature */
    private PublicKey badVerificationKey;
    
    /** Builder of mock XML objects */
    private SimpleXMLObjectBuilder sxoBuilder;
    
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
        
        keyGen.initialize(1024);
        keyPair = keyGen.generateKeyPair();
        badVerificationKey = keyPair.getPublic();
        
        sxoBuilder = new SimpleXMLObjectBuilder();
        sigBuilder = new SignatureBuilder();
    }

    /**
     * Tests creating an enveloped signature and then verifying it
     * @throws MarshallingException 
     * @throws ValidationException 
     */
    public void testSigningAndVerification() throws MarshallingException, ValidationException{
        SimpleXMLObject sxo = getXMLObjectWithSignature();
        Marshaller marshaller = Configuration.getMarshallerFactory().getMarshaller(sxo);
        Element signedElement = marshaller.marshall(sxo);
        
        Signer.signObject(sxo.getSignature());
        
        if(log.isDebugEnabled()){
            log.debug("Marshalled Signature: \n" + XMLHelper.nodeToString(signedElement));
        }
        
        SignatureValidator signatureValidator = new SignatureValidator(verificationKey);
        signatureValidator.validate(sxo.getSignature());
        
        try{
            signatureValidator = new SignatureValidator(badVerificationKey);
            signatureValidator.validate(sxo.getSignature());
            fail("Signature validated with an incorrect public key");
        }catch(ValidationException e){
            // this is supposed to fail
        }
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
    private SimpleXMLObject getXMLObjectWithSignature(){
        SimpleXMLObject sxo = sxoBuilder.buildObject();
        sxo.setId("FOO");
        
        Signature sig = sigBuilder.buildObject();
        sig.setSigningKey(signingKey);
        sig.setCanonicalizationAlgorithm(Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        sig.setSignatureAlgorithm(XMLSignature.ALGO_ID_SIGNATURE_RSA);
        sig.getContentReferences().add(new EnvelopedSignatureContentReference("FOO"));
        
        sxo.setSignature(sig);
        return sxo;
    }
}