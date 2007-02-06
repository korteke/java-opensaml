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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
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
import org.opensaml.xml.security.InlineX509KeyInfoResolver;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.WrapperKeyInfoSource;
import org.opensaml.xml.signature.impl.SignatureBuilder;
import org.opensaml.xml.util.XMLHelper;
import org.opensaml.xml.validation.ValidationException;
import org.w3c.dom.Element;

public class DetachedSignatureTest extends XMLObjectBaseTestCase {

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

        HashMap<String, Boolean> features = new HashMap<String, Boolean>();
        features.put("http://apache.org/xml/features/validation/schema/normalized-value", Boolean.FALSE);
        features.put("http://apache.org/xml/features/dom/defer-node-expansion", Boolean.FALSE);
        parserPool = new ParserPool(true, null, features);
    }

    /**
     * Tests creating a detached signature within the same document as the element signed and then verifying it.
     * 
     * @throws MarshallingException thrown if the XMLObject tree can not be marshalled
     * @throws SecurityException thrown if there is a problem attempting to validate the signature
     * @throws UnmarshallingException 
     */
    public void testInternalSignatureAndVerification() throws MarshallingException, SecurityException, UnmarshallingException {
        SimpleXMLObject sxo = getXMLObjectWithSignature();
        Signature signature = sxo.getSignature();

        Marshaller marshaller = Configuration.getMarshallerFactory().getMarshaller(sxo);
        Element signedElement = marshaller.marshall(sxo);

        Signer.signObject(signature);
        if (log.isDebugEnabled()) {
            log.debug("Marshalled deatched Signature: \n" + XMLHelper.nodeToString(signedElement));
        }

        Unmarshaller unmarshaller = Configuration.getUnmarshallerFactory().getUnmarshaller(signedElement);
        sxo = (SimpleXMLObject) unmarshaller.unmarshall(signedElement);
        signature = (Signature) sxo.getOrderedChildren().get(1);
        if(!trustEngine.validate(signature, null, verificationKeyResolver)){
            fail("Failed to validate signature with proper public key");
        }
        
        if(trustEngine.validate(signature, null, badKeyResolver)){
            fail("Validate signature with improper public key");
        }
    }

    /**
     * Tests creating a detached signature within a different document as the element signed and then verifying it. The
     * external references used are the InCommon and InQueue metadata files.
     * 
     * @throws MarshallingException thrown if the XMLObject tree can not be marshalled
     * @throws ValidationException thrown if the signature verification fails
     * @throws SecurityException 
     */
    public void testExternalSignatureAndVerification() throws MarshallingException, ValidationException, SecurityException {
        Signature signature = sigBuilder.buildObject();
        signature.setSigningKey(signingKey);
        signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA);

        String incommonMetadata = "http://wayf.incommonfederation.org/InCommon/InCommon-metadata.xml";
        URIContentReference contentReference = new URIContentReference(incommonMetadata);
        contentReference.getTransforms().add(SignatureConstants.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
        contentReference.setDigestAlgorithm(EncryptionConstants.ALGO_ID_DIGEST_SHA256);
        signature.getContentReferences().add(contentReference);

        Marshaller marshaller = Configuration.getMarshallerFactory().getMarshaller(signature);
        Element signatureElement = marshaller.marshall(signature);

        Signer.signObject(signature);
        if (log.isDebugEnabled()) {
            log.debug("Marshalled deatched Signature: \n" + XMLHelper.nodeToString(signatureElement));
        }

        if(!trustEngine.validate(signature, null, verificationKeyResolver)){
            fail("Failed to validate signature with proper public key");
        }
    }

    /**
     * Unmarshalls the XML DSIG spec RSA example signature and verifies it with the key contained in the KeyInfo.
     * 
     * @throws IOException thrown if the signature can not be fetched from the W3C site
     * @throws MalformedURLException thrown if the signature can not be fetched from the W3C site
     * @throws XMLParserException thrown if the signature is not valid XML
     * @throws UnmarshallingException thrown if the signature DOM can not be unmarshalled
     * @throws ValidationException thrown if the Signature does not validate against the key
     * @throws KeyException 
     * @throws SecurityException 
     * 
     */
    public void testUnmarshallExternalSignatureAndVerification() throws MalformedURLException, IOException,
            XMLParserException, UnmarshallingException, SecurityException {
        String signatureLocation = "http://www.w3.org/TR/xmldsig-core/signature-example-rsa.xml";
        InputStream ins = new URL(signatureLocation).openStream();
        Element signatureElement = parserPool.parse(ins).getDocumentElement();

        Unmarshaller unmarshaller = Configuration.getUnmarshallerFactory().getUnmarshaller(signatureElement);
        Signature signature = (Signature) unmarshaller.unmarshall(signatureElement);
        
        WrapperKeyInfoSource keyInfoSource = new WrapperKeyInfoSource(null, signature.getKeyInfo());
        if(!trustEngine.validate(signature, keyInfoSource, new InlineX509KeyInfoResolver())){
            fail("Failed to validate signature with proper public key");
        }
    }

    /**
     * Creates a XMLObject that has another XMLObject and a Signature as children. The Signature is is a detached
     * signature of its sibling.
     * 
     * @return the XMLObject
     */
    private SimpleXMLObject getXMLObjectWithSignature() {
        SimpleXMLObject rootSXO = sxoBuilder.buildObject();

        SimpleXMLObject childSXO = sxoBuilder.buildObject();
        childSXO.setId("FOO");
        rootSXO.getSimpleXMLObjects().add(childSXO);

        Signature sig = sigBuilder.buildObject();
        sig.setSigningKey(signingKey);
        sig.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        sig.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA);

        DocumentInternalIDContentReference contentReference = new DocumentInternalIDContentReference("FOO");
        contentReference.getTransforms().add(SignatureConstants.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
        contentReference.setDigestAlgorithm(EncryptionConstants.ALGO_ID_DIGEST_SHA256);
        sig.getContentReferences().add(contentReference);

        rootSXO.setSignature(sig);
        return rootSXO;
    }
}
