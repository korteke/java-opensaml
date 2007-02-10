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

import java.io.InputStream;
import java.security.KeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;

import javolution.util.FastList;

import org.apache.log4j.Logger;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObjectBaseTestCase;
import org.opensaml.xml.encryption.EncryptionConstants;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.mock.SimpleXMLObject;
import org.opensaml.xml.mock.SimpleXMLObjectBuilder;
import org.opensaml.xml.parse.ParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.security.DirectKeyInfoResolver;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.signature.impl.KeyInfoBuilder;
import org.opensaml.xml.signature.impl.SignatureBuilder;
import org.opensaml.xml.util.XMLHelper;
import org.opensaml.xml.validation.ValidationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Test to verify {@link org.opensaml.xml.signature.Signature} and its marshallers and unmarshallers.
 */
public class EnvelopedSignatureTest extends XMLObjectBaseTestCase {

    /** Class logger. */
    private static Logger log = Logger.getLogger(EnvelopedSignatureTest.class);

    /** Key used for signing. */
    private PrivateKey signingKey;

    /** Trust engine used to verify signatures. */
    private BasicX509SignatureTrustEngine trustEngine;
    
    /** Key resolver containing proper verification key. */
    private DirectKeyInfoResolver verificationKeyResolver;
    
    /** Key resolver containing invalid verification key. */
    private DirectKeyInfoResolver badKeyResolver;

    /** Builder of mock XML objects. */
    private SimpleXMLObjectBuilder sxoBuilder;

    /** Builder of Signature XML objects. */
    private SignatureBuilder sigBuilder;

    /** Builder of KeyInfo XML objects. */
    private KeyInfoBuilder keyInfoBuilder;

    /** Parser pool used to parse example config files. */
    private ParserPool parserPool;

    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();

        trustEngine = new BasicX509SignatureTrustEngine();
        
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair keyPair = keyGen.generateKeyPair();
        signingKey = keyPair.getPrivate();
        
        FastList<PublicKey> verificationKey = new FastList<PublicKey>();
        verificationKey.add(keyPair.getPublic());
        verificationKeyResolver = new DirectKeyInfoResolver(null, verificationKey, null, null);

        keyGen.initialize(1024);
        keyPair = keyGen.generateKeyPair();
        FastList<PublicKey> badKey = new FastList<PublicKey>();
        badKey.add(keyPair.getPublic());
        badKeyResolver = new DirectKeyInfoResolver(null, badKey, null, null);

        sxoBuilder = new SimpleXMLObjectBuilder();
        sigBuilder = new SignatureBuilder();
        keyInfoBuilder = new KeyInfoBuilder();

        parserPool = new ParserPool();
        parserPool.setNamespaceAware(true);
    }

    /**
     * Tests creating an enveloped signature and then verifying it.
     * 
     * @throws MarshallingException thrown if the XMLObject tree can not be marshalled
     * @throws SecurityException 
     * @throws KeyException 
     * @throws IllegalArgumentException 
     * @throws ValidationException thrown if the signature verification fails
     */
    public void testSigningAndVerification() throws MarshallingException, SecurityException, IllegalArgumentException, KeyException {
        SimpleXMLObject sxo = getXMLObjectWithSignature();
        Signature signature = sxo.getSignature();

        Marshaller marshaller = Configuration.getMarshallerFactory().getMarshaller(sxo);
        Element signedElement = marshaller.marshall(sxo);
        
        Signer.signObject(signature);
        
        if (log.isDebugEnabled()) {
            log.debug("Marshalled Signature: \n" + XMLHelper.nodeToString(signedElement));
        }
        
        if(!trustEngine.validate(signature, null, verificationKeyResolver)){
            fail("Failed to validate signature with proper public key");
        }
    }

    /**
     * Tests unmarshalling an enveloped signature.
     * 
     * @throws XMLParserException thrown if the XML can not be parsed
     * @throws UnmarshallingException thrown if the DOM can not be unmarshalled
     * @throws KeyException 
     */
    public void testUnmarshallSignature() throws XMLParserException, UnmarshallingException, KeyException {
        String envelopedSignatureFile = "/data/org/opensaml/xml/signature/envelopedSignature.xml";
        InputStream ins = EnvelopedSignatureTest.class.getResourceAsStream(envelopedSignatureFile);
        Document envelopedSignatureDoc = parserPool.parse(ins);
        Element rootElement = envelopedSignatureDoc.getDocumentElement();

        Unmarshaller unmarshaller = Configuration.getUnmarshallerFactory().getUnmarshaller(rootElement);
        SimpleXMLObject sxo = (SimpleXMLObject) unmarshaller.unmarshall(rootElement);

        assertEquals("Id attribute was not expected value", "FOO", sxo.getId());

        Signature signature = sxo.getSignature();
        assertNotNull("Signature was null", signature);

        KeyInfo keyInfo = signature.getKeyInfo();
        assertNotNull("Signature's KeyInfo was null", keyInfo);
        
        PublicKey pubKey = KeyInfoHelper.getPublicKeys(keyInfo).get(0);
        assertNotNull("KeyInfo did not contain the verification key", pubKey);
    }

    /**
     * Creates a XMLObject that has a Signature child element.
     * 
     * @return a XMLObject that has a Signature child element
     * @throws KeyException 
     * @throws IllegalArgumentException 
     */
    private SimpleXMLObject getXMLObjectWithSignature() throws IllegalArgumentException, KeyException {
        SimpleXMLObject sxo = sxoBuilder.buildObject();
        sxo.setId("FOO");

        Signature sig = sigBuilder.buildObject();
        sig.setSigningKey(signingKey);
        sig.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        sig.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA);
        
        DocumentInternalIDContentReference contentReference = new DocumentInternalIDContentReference("FOO");
        contentReference.getTransforms().add(SignatureConstants.TRANSFORM_ENVELOPED_SIGNATURE);
        contentReference.getTransforms().add(SignatureConstants.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
        contentReference.setDigestAlgorithm(EncryptionConstants.ALGO_ID_DIGEST_SHA256);
        sig.getContentReferences().add(contentReference);
        
        KeyInfo keyInfo = keyInfoBuilder.buildObject();
        KeyInfoHelper.addPublicKey(keyInfo, verificationKeyResolver.resolveKey(null));
        sig.setKeyInfo(keyInfo);

        sxo.setSignature(sig);
        return sxo;
    }
}