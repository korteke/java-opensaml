/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.xmlsec.signature.support;

import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PublicKey;

import javax.xml.bind.ValidationException;

import net.shibboleth.utilities.java.support.xml.SerializeSupport;
import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.keyinfo.KeyInfoSupport;
import org.opensaml.xmlsec.mock.SignableSimpleXMLObject;
import org.opensaml.xmlsec.mock.SignableSimpleXMLObjectBuilder;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.impl.SignatureBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Test to verify {@link org.opensaml.xmlsec.signature.Signature} and its marshallers and unmarshallers.
 */
public class EnvelopedSignatureTest extends XMLObjectBaseTestCase {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(EnvelopedSignatureTest.class);

    /** Credential used to sign and verify. */
    private Credential goodCredential;
    
    /** Invalid credential for verification. */
    private Credential badCredential;

    /** Builder of mock XML objects. */
    private SignableSimpleXMLObjectBuilder sxoBuilder;

    /** Builder of Signature XML objects. */
    private SignatureBuilder sigBuilder;
    
    /** Signature algorithm URI. */
    private String algoURI = SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1;

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        KeyPair keyPair = KeySupport.generateKeyPair("RSA", 1024, null);
        goodCredential = CredentialSupport.getSimpleCredential(keyPair.getPublic(), keyPair.getPrivate());

        keyPair = KeySupport.generateKeyPair("RSA", 1024, null);
        badCredential = CredentialSupport.getSimpleCredential(keyPair.getPublic(), null);

        sxoBuilder = new SignableSimpleXMLObjectBuilder();
        sigBuilder = new SignatureBuilder();
    }

    /**
     * Tests creating an enveloped signature and then verifying it.
     * 
     * @throws MarshallingException thrown if the XMLObject tree can not be marshalled
     * @throws ValidationException 
     * @throws SignatureException 
     */
    @Test
    public void testSigningAndVerification() throws MarshallingException, ValidationException, SignatureException{
        SignableSimpleXMLObject sxo = getXMLObjectWithSignature();
        Signature signature = sxo.getSignature();

        Marshaller marshaller = XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(sxo);
        Element signedElement = marshaller.marshall(sxo);
        
        Signer.signObject(signature);
        
        if (log.isDebugEnabled()) {
            log.debug("Marshalled Signature: \n" + SerializeSupport.nodeToString(signedElement));
        }
        
        SignatureValidator.validate(signature, goodCredential);

        try {
            SignatureValidator.validate(signature, badCredential);
            Assert.fail("Validated signature with improper public key");
        } catch (SignatureException e) {
            // expected
        }
    }

    /**
     * Tests unmarshalling an enveloped signature.
     * 
     * @throws XMLParserException thrown if the XML can not be parsed
     * @throws UnmarshallingException thrown if the DOM can not be unmarshalled
     * @throws GeneralSecurityException 
     */
    @Test
    public void testUnmarshall() throws XMLParserException, UnmarshallingException, GeneralSecurityException {
        String envelopedSignatureFile = "/data/org/opensaml/xmlsec/signature/support/envelopedSignature.xml";
        InputStream ins = EnvelopedSignatureTest.class.getResourceAsStream(envelopedSignatureFile);
        Document envelopedSignatureDoc = parserPool.parse(ins);
        Element rootElement = envelopedSignatureDoc.getDocumentElement();

        Unmarshaller unmarshaller = XMLObjectProviderRegistrySupport.getUnmarshallerFactory().getUnmarshaller(rootElement);
        SignableSimpleXMLObject sxo = (SignableSimpleXMLObject) unmarshaller.unmarshall(rootElement);

        Assert.assertEquals(sxo.getId(), "FOO", "Id attribute was not expected value");

        Signature signature = sxo.getSignature();
        Assert.assertNotNull(signature, "Signature was null");

        KeyInfo keyInfo = signature.getKeyInfo();
        Assert.assertNotNull(keyInfo, "Signature's KeyInfo was null");
        
        PublicKey pubKey = KeyInfoSupport.getPublicKeys(keyInfo).get(0);
        Assert.assertNotNull(pubKey, "KeyInfo did not contain the verification key");
    }

    /**
     * Creates a XMLObject that has a Signature child element.
     * 
     * @return a XMLObject that has a Signature child element
     */
    private SignableSimpleXMLObject getXMLObjectWithSignature() {
        SignableSimpleXMLObject sxo = sxoBuilder.buildObject();
        sxo.setId("FOO");

        Signature sig = sigBuilder.buildObject();
        sig.setSigningCredential(goodCredential);
        sig.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        sig.setSignatureAlgorithm(algoURI);
        
        DocumentInternalIDContentReference contentReference = new DocumentInternalIDContentReference("FOO");
        contentReference.getTransforms().add(SignatureConstants.TRANSFORM_ENVELOPED_SIGNATURE);
        contentReference.getTransforms().add(SignatureConstants.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
        contentReference.setDigestAlgorithm(SignatureConstants.ALGO_ID_DIGEST_SHA1);
        sig.getContentReferences().add(contentReference);

        sxo.setSignature(sig);
        return sxo;
    }
}