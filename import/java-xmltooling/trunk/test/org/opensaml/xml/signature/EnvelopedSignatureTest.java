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
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObjectBaseTestCase;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.mock.SimpleXMLObject;
import org.opensaml.xml.mock.SimpleXMLObjectBuilder;
import org.opensaml.xml.parse.ParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.signature.impl.KeyInfoBuilder;
import org.opensaml.xml.util.XMLHelper;
import org.opensaml.xml.validation.ValidationException;
import org.w3c.dom.Document;
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

    /** Builder of KeyInfo XML objects */
    private KeyInfoBuilder keyInfoBuilder;

    /** Parser pool used to parse example config files */
    private ParserPool parserPool;

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
        keyInfoBuilder = new KeyInfoBuilder();

        HashMap<String, Boolean> features = new HashMap<String, Boolean>();
        features.put("http://apache.org/xml/features/validation/schema/normalized-value", Boolean.FALSE);
        features.put("http://apache.org/xml/features/dom/defer-node-expansion", Boolean.FALSE);

        parserPool = new ParserPool(true, null, features);
    }

    /**
     * Tests creating an enveloped signature and then verifying it.
     * 
     * @throws MarshallingException thrown if the XMLObject tree can not be marshalled
     * @throws ValidationException thrown if the signature verification fails
     */
    public void testSigningAndVerification() throws MarshallingException, ValidationException {
        SimpleXMLObject sxo = getXMLObjectWithSignature();
        Signature signature = sxo.getSignature();

        KeyInfo keyInfo = keyInfoBuilder.buildObject();
        //TODO temp broken by KeyInfo changes
        //keyInfo.setPublicKey(verificationKey);
        signature.setKeyInfo(keyInfo);

        Marshaller marshaller = Configuration.getMarshallerFactory().getMarshaller(sxo);
        Element signedElement = marshaller.marshall(sxo);

        Signer.signObject(signature);

        if (log.isDebugEnabled()) {
            log.debug("Marshalled Signature: \n" + XMLHelper.nodeToString(signedElement));
        }

        SignatureValidator signatureValidator = new SignatureValidator(verificationKey);
        signatureValidator.validate(signature);

        try {
            signatureValidator = new SignatureValidator(badVerificationKey);
            signatureValidator.validate(sxo.getSignature());
            fail("Signature validated with an incorrect public key");
        } catch (ValidationException e) {
            // this is supposed to fail
        }
    }

    /**
     * Tests unmarshalling an enveloped signature.
     * 
     * @throws XMLParserException thrown if the XML can not be parsed
     * @throws UnmarshallingException thrown if the DOM can not be unmarshalled
     */
    public void testUnmarshallSignature() throws XMLParserException, UnmarshallingException {
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
        
        //TODO temp broken by KeyInfo changes
        //PublicKey pubKey = keyInfo.getPublicKey();
        PublicKey pubKey = null;
        assertNotNull("KeyInfo did not contain the verification key", pubKey);
    }

    /**
     * Creates a XMLObject that has a Signature child element.
     * 
     * @return a XMLObject that has a Signature child element
     */
    private SimpleXMLObject getXMLObjectWithSignature() {
        SimpleXMLObject sxo = sxoBuilder.buildObject();
        sxo.setId("FOO");

        Signature sig = sigBuilder.buildObject();
        sig.setSigningKey(signingKey);
        sig.setCanonicalizationAlgorithm(Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        sig.setSignatureAlgorithm(XMLSignature.ALGO_ID_SIGNATURE_RSA);
        
        DocumentInternalIDContentReference contentReference = new DocumentInternalIDContentReference("FOO");
        contentReference.getTransforms().add(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
        contentReference.getTransforms().add(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
        contentReference.setDigestAlgorithm(MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA256);
        sig.getContentReferences().add(contentReference);

        sxo.setSignature(sig);
        return sxo;
    }
}