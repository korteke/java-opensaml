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

package org.opensaml.xml.signature;

import java.io.InputStream;
import java.util.List;

import javax.crypto.SecretKey;

import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.xml.ElementSupport;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;
import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.apache.xml.security.signature.SignedInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.core.xml.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.validation.ValidationException;
import org.opensaml.security.SecurityHelper;
import org.opensaml.security.credential.Credential;
import org.opensaml.xml.mock.SignableSimpleXMLObject;
import org.opensaml.xml.mock.SignableSimpleXMLObjectBuilder;
import org.opensaml.xmlsec.keyinfo.KeyInfoHelper;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.KeyName;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.impl.KeyInfoBuilder;
import org.opensaml.xmlsec.signature.impl.SignatureBuilder;
import org.opensaml.xmlsec.signature.impl.SignatureImpl;
import org.opensaml.xmlsec.signature.support.DocumentInternalIDContentReference;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignatureValidator;
import org.opensaml.xmlsec.signature.support.Signer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Test to verify {@link org.opensaml.xmlsec.signature.Signature} and its marshallers and unmarshallers.
 */
public class HMACSignatureTest extends XMLObjectBaseTestCase {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(HMACSignatureTest.class);

    /** Credential used to sign and verify. */
    private Credential goodCredential;
    
    /** Invalid credential for verification. */
    private Credential badCredential;

    /** Builder of mock XML objects. */
    private SignableSimpleXMLObjectBuilder sxoBuilder;

    /** Builder of Signature XML objects. */
    private SignatureBuilder sigBuilder;
    
    /** Build of KeyInfo objects. */
    private KeyInfoBuilder keyInfoBuilder;
    
    /** Value of HMACOutputLength element child of SignatureMethod. */
    private Integer hmacOutputLength;
    
    /** Expected key name value in KeyInfo. */
    private String expectedKeyName;
    
    /** Signature algorithm URI. */
    private String algoURI = SignatureConstants.ALGO_ID_MAC_HMAC_SHA1;

    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
        
        hmacOutputLength = new Integer(160);
        expectedKeyName = "KeyFoo123";
        
        SecretKey key = SecurityHelper.generateKey("AES", 128, null);
        goodCredential = SecurityHelper.getSimpleCredential(key);
        
        key = SecurityHelper.generateKey("AES", 128, null);
        badCredential = SecurityHelper.getSimpleCredential(key);

        sxoBuilder = new SignableSimpleXMLObjectBuilder();
        sigBuilder = new SignatureBuilder();
        keyInfoBuilder = new KeyInfoBuilder();
    }

    /**
     * Tests creating an enveloped signature and then verifying it.
     * 
     * @throws MarshallingException thrown if the XMLObject tree can not be marshalled
     * @throws ValidationException thrown if signature validator fails to validate the signature
     * @throws SignatureException 
     */
    public void testSigningAndVerificationNoOutputLength() throws MarshallingException, ValidationException, SignatureException {
        SignableSimpleXMLObject sxo = getXMLObjectWithSignature(false);
        Signature signature = sxo.getSignature();

        Marshaller marshaller = XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(sxo);
        Element signedElement = marshaller.marshall(sxo);
        
        Signer.signObject(signature);
        
        if (log.isDebugEnabled()) {
            log.debug("Marshalled Signature: \n" + SerializeSupport.nodeToString(signedElement));
        }
        
        SignatureValidator sigValidator = new SignatureValidator(goodCredential);
        sigValidator.validate(signature);

        try {
            sigValidator = new SignatureValidator(badCredential);
            sigValidator.validate(signature);
            fail("Validated signature with invalid secret key");
        } catch (ValidationException e) {
            // expected
        }
    }

    /**
     * Tests creating an enveloped signature and then verifying it.
     * 
     * @throws MarshallingException thrown if the XMLObject tree can not be marshalled
     * @throws ValidationException thrown if signature validator fails to validate the signature
     * @throws SignatureException 
     */
    public void testSigningAndVerificationWithOutputLength() throws MarshallingException, ValidationException, SignatureException {
        SignableSimpleXMLObject sxo = getXMLObjectWithSignature(true);
        Signature signature = sxo.getSignature();

        Marshaller marshaller = XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(sxo);
        Element signedElement = marshaller.marshall(sxo);
        
        Signer.signObject(signature);
        
        if (log.isDebugEnabled()) {
            log.debug("Marshalled Signature: \n" + SerializeSupport.nodeToString(signedElement));
        }
        
        SignatureValidator sigValidator = new SignatureValidator(goodCredential);
        sigValidator.validate(signature);

        try {
            sigValidator = new SignatureValidator(badCredential);
            sigValidator.validate(signature);
            fail("Validated signature with invalid secret key");
        } catch (ValidationException e) {
            // expected
        }
    }

    /**
     * Tests unmarshalling with SignatureMethod/HMACOutputLength not present.
     * 
     * @throws XMLParserException thrown if the XML can not be parsed
     * @throws UnmarshallingException thrown if the DOM can not be unmarshalled
     */
    public void testUnmarshallNoOutputLength() throws XMLParserException, UnmarshallingException {
        String envelopedSignatureFile = "/data/org/opensaml/xml/signature/HMACSignatureNoOutputLength.xml";
        InputStream ins = HMACSignatureTest.class.getResourceAsStream(envelopedSignatureFile);
        Document envelopedSignatureDoc = parserPool.parse(ins);
        Element rootElement = envelopedSignatureDoc.getDocumentElement();

        Unmarshaller unmarshaller = XMLObjectProviderRegistrySupport.getUnmarshallerFactory().getUnmarshaller(rootElement);
        SignableSimpleXMLObject sxo = (SignableSimpleXMLObject) unmarshaller.unmarshall(rootElement);

        assertEquals("Id attribute was not expected value", "FOO", sxo.getId());

        Signature signature = sxo.getSignature();
        assertNotNull("Signature was null", signature);

        KeyInfo keyInfo = signature.getKeyInfo();
        assertNotNull("Signature's KeyInfo was null", keyInfo);
        
        KeyName keyName = keyInfo.getKeyNames().get(0);
        assertNotNull("KeyName was null", keyName);
        String keyNameValue = StringSupport.trimOrNull(keyName.getValue());
        assertNotNull("KeyName value was empty", keyNameValue);
        
        assertNull("HMACOutputLength value was not null", signature.getHMACOutputLength());
    }
    
    /**
     * Tests unmarshalling with SignatureMethod/HMACOutputLength present.
     * 
     * @throws XMLParserException thrown if the XML can not be parsed
     * @throws UnmarshallingException thrown if the DOM can not be unmarshalled
     */
    public void testUnmarshallWithOutputLength() throws XMLParserException, UnmarshallingException {
        String envelopedSignatureFile = "/data/org/opensaml/xml/signature/HMACSignatureWithOutputLength.xml";
        InputStream ins = HMACSignatureTest.class.getResourceAsStream(envelopedSignatureFile);
        Document envelopedSignatureDoc = parserPool.parse(ins);
        Element rootElement = envelopedSignatureDoc.getDocumentElement();

        Unmarshaller unmarshaller = XMLObjectProviderRegistrySupport.getUnmarshallerFactory().getUnmarshaller(rootElement);
        SignableSimpleXMLObject sxo = (SignableSimpleXMLObject) unmarshaller.unmarshall(rootElement);

        assertEquals("Id attribute was not expected value", "FOO", sxo.getId());

        Signature signature = sxo.getSignature();
        assertNotNull("Signature was null", signature);

        KeyInfo keyInfo = signature.getKeyInfo();
        assertNotNull("Signature's KeyInfo was null", keyInfo);
        
        KeyName keyName = keyInfo.getKeyNames().get(0);
        assertNotNull("KeyName was null", keyName);
        String keyNameValue = StringSupport.trimOrNull(keyName.getValue());
        assertNotNull("KeyName value was empty", keyNameValue);
        
        assertNotNull("HMACOutputLength value was null", signature.getHMACOutputLength());
        assertEquals("HMACOutputLength value was incorrect value", hmacOutputLength, signature.getHMACOutputLength());
    }
    
    /**
     * Tests marshalling with SignatureMethod/HMACOutputLength not present.
     * 
     * @throws MarshallingException thrown in signed object can't be marshalled
     */
    public void testMarshallNoOutputLength() throws MarshallingException {
        SignableSimpleXMLObject sxo = getXMLObjectWithSignature(false);
        Signature signature = sxo.getSignature();

        Marshaller marshaller = XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(sxo);
        marshaller.marshall(sxo);
        
        assertNotNull("Signature DOM was null", signature.getDOM());
        XMLSignature apacheSignature = ((SignatureImpl) signature).getXMLSignature();
        assertNotNull("Apache XMLSignature was null", apacheSignature);
        SignedInfo apacheSignedInfo = apacheSignature.getSignedInfo(); 
        assertNotNull("Apache SignedInfo was null", apacheSignedInfo);
        Element sigMethodElement = apacheSignedInfo.getSignatureMethodElement();
        List<Element> children = 
            ElementSupport.getChildElementsByTagNameNS(sigMethodElement, SignatureConstants.XMLSIG_NS, "HMACOutputLength");
        assertTrue("Signature method should not have HMACOutputLength child", children.isEmpty());
    }

    /**
     * Tests marshalling with SignatureMethod/HMACOutputLength present.
     * 
     * @throws MarshallingException thrown in signed object can't be marshalled
     */
    public void testMarshallWithOutputLength() throws MarshallingException {
        SignableSimpleXMLObject sxo = getXMLObjectWithSignature(true);
        Signature signature = sxo.getSignature();

        Marshaller marshaller = XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(sxo);
        marshaller.marshall(sxo);
        
        assertNotNull("Signature DOM was null", signature.getDOM());
        XMLSignature apacheSignature = ((SignatureImpl) signature).getXMLSignature();
        assertNotNull("Apache XMLSignature was null", apacheSignature);
        SignedInfo apacheSignedInfo = apacheSignature.getSignedInfo(); 
        assertNotNull("Apache SignedInfo was null", apacheSignedInfo);
        Element sigMethodElement = apacheSignedInfo.getSignatureMethodElement();
        List<Element> children = 
            ElementSupport.getChildElementsByTagNameNS(sigMethodElement, SignatureConstants.XMLSIG_NS, "HMACOutputLength");
        assertFalse("Signature method should have HMACOutputLength child", children.isEmpty());
        Element outputLengthElement = children.get(0);
        String value = StringSupport.trimOrNull(outputLengthElement.getTextContent());
        assertNotNull("Output length value was empty", value);
        assertEquals("Output length was not the expected value", hmacOutputLength, new Integer(value));
    }

    /**
     * Creates a XMLObject that has a Signature child element.
     * 
     * @param useHMACOutputLength if true, set value for HMACOutputLength
     * 
     * @return a XMLObject that has a Signature child element
     */
    private SignableSimpleXMLObject getXMLObjectWithSignature(boolean useHMACOutputLength) {
        SignableSimpleXMLObject sxo = sxoBuilder.buildObject();
        sxo.setId("FOO");

        Signature sig = sigBuilder.buildObject();
        sig.setSigningCredential(goodCredential);
        sig.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        sig.setSignatureAlgorithm(algoURI);
        if (useHMACOutputLength) {
            sig.setHMACOutputLength(hmacOutputLength);
        }
        
        DocumentInternalIDContentReference contentReference = new DocumentInternalIDContentReference("FOO");
        contentReference.getTransforms().add(SignatureConstants.TRANSFORM_ENVELOPED_SIGNATURE);
        contentReference.getTransforms().add(SignatureConstants.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
        contentReference.setDigestAlgorithm(SignatureConstants.ALGO_ID_DIGEST_SHA1);
        sig.getContentReferences().add(contentReference);
        
        KeyInfo keyInfo = keyInfoBuilder.buildObject();
        KeyInfoHelper.addKeyName(keyInfo, expectedKeyName);
        sig.setKeyInfo(keyInfo);

        sxo.setSignature(sig);
        return sxo;
    }
}