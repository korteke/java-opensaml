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

package org.opensaml.saml.saml2.encryption;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import net.shibboleth.utilities.java.support.xml.SerializeSupport;
import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.joda.time.DateTime;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.saml.saml2.encryption.Encrypter;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.encryption.support.DecryptionException;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.encryption.support.EncryptionException;
import org.opensaml.xmlsec.encryption.support.DataEncryptionParameters;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.impl.StaticKeyInfoCredentialResolver;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignatureSupport;
import org.opensaml.xmlsec.signature.support.SignatureValidator;
import org.opensaml.xmlsec.signature.support.Signer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *  Tests that decryption of an Assertion does not invalidate the signature of a containing object (Response).
 */
public class DecryptionPlusSigningTest extends XMLObjectBaseTestCase {
    
    private KeyInfoCredentialResolver keyResolver;
    
    private String encURI;
    private DataEncryptionParameters encParams;
    
    private Encrypter encrypter;
    
    private Credential signingCred;
    
    /**
     * Constructor.
     *
     */
    public DecryptionPlusSigningTest() {
        encURI = EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128;
        
    }
    
    @BeforeMethod
    protected void setUp() throws Exception {
        Credential encCred = AlgorithmSupport.generateSymmetricKeyAndCredential(encURI);
        encCred.getSecretKey();
        keyResolver = new StaticKeyInfoCredentialResolver(encCred);
        encParams = new DataEncryptionParameters();
        encParams.setAlgorithm(encURI);
        encParams.setEncryptionCredential(encCred);
        
        encrypter = new Encrypter(encParams);
        
        KeyPair kp = KeySupport.generateKeyPair("RSA", 1024, null);
        signingCred = CredentialSupport.getSimpleCredential(kp.getPublic(), kp.getPrivate());
        
    }
    
    /**
     * Test decryption of an EncryptedAssertion and validation of the signature on the enclosing Response.
     *  
     * @throws XMLParserException  thrown if there is an error parsing the control XML file
     * @throws EncryptionException  thrown if there is an error encrypting the control XML
     * @throws NoSuchProviderException 
     * @throws NoSuchAlgorithmException 
     * @throws SecurityException 
     * @throws MarshallingException 
     * @throws SignatureException 
     * @throws UnmarshallingException 
     */
    @Test
    public void testEncryptedAssertionInResponse() throws XMLParserException, EncryptionException, 
            NoSuchAlgorithmException, NoSuchProviderException, SecurityException, MarshallingException, 
            SignatureException, UnmarshallingException {
        
        //Build encrypted Assertion
        String filename = "/org/opensaml/saml/saml2/encryption/Assertion.xml";
        Document targetDOM = getDOM(filename);
        
        Assertion assertion = (Assertion) unmarshallElement(filename);
        EncryptedAssertion encryptedAssertion = encrypter.encrypt(assertion);
        
        // Build Response container
        Response response = (Response) buildXMLObject(Response.DEFAULT_ELEMENT_NAME);
        response.setID("def456");
        response.setIssueInstant(new DateTime());
        
        Issuer issuer = (Issuer) buildXMLObject(Issuer.DEFAULT_ELEMENT_NAME);
        issuer.setValue("urn:string:issuer");
        response.setIssuer(issuer);
        
        response.getEncryptedAssertions().add(encryptedAssertion);
        
        // Sign Response
        Signature responseSignature = (Signature) buildXMLObject(Signature.DEFAULT_ELEMENT_NAME);
        response.setSignature(responseSignature);
        SignatureSigningParameters signingParams = new SignatureSigningParameters();
        signingParams.setSigningCredential(signingCred);
        signingParams.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        signingParams.setSignatureReferenceDigestMethod(SignatureConstants.ALGO_ID_DIGEST_SHA256);
        signingParams.setSignatureCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_WITH_COMMENTS);
        
        SignatureSupport.prepareSignatureParams(responseSignature, signingParams);
        
        marshallerFactory.getMarshaller(response).marshall(response);
        
        Signer.signObject(responseSignature);
        
        // Marshall Response and re-parse, for good measure
        Element marshalledResponse = marshallerFactory.getMarshaller(response).marshall(response);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SerializeSupport.writeNode(marshalledResponse, baos);
        
        //System.out.println(XMLHelper.prettyPrintXML(marshalledResponse));
        
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        Document parsedDoc = parserPool.parse(bais);
        Element parsedResponse = parsedDoc.getDocumentElement();
        
        Response newResponse = 
            (Response) unmarshallerFactory.getUnmarshaller(parsedResponse).unmarshall(parsedResponse);
        
        // Validate Response signature first time
        try {
            SignatureValidator.validate(newResponse.getSignature(), signingCred);
        } catch (SignatureException e1) {
            Assert.fail("First Response signature validation failed");
        }
        
        // Decrypt Assertion
        EncryptedAssertion newEncryptedAssertion = newResponse.getEncryptedAssertions().get(0);
        
        Decrypter decrypter = new Decrypter(keyResolver, null, null);
        decrypter.setRootInNewDocument(true);
        
        Assertion decryptedAssertion = null;
        try {
            decryptedAssertion = decrypter.decrypt(newEncryptedAssertion);
        } catch (DecryptionException e) {
            Assert.fail("Error on decryption of EncryptedAssertion: " + e);
        }
        
        Assert.assertNotNull(decryptedAssertion, "Decrypted Assertion was null");
        
        assertXMLEquals(targetDOM, decryptedAssertion);
        
        // Validate Response signature second time
        try {
            SignatureValidator.validate(newResponse.getSignature(), signingCred);
        } catch (SignatureException e1) {
            Assert.fail("Second Response signature validation failed");
        }
        
    }
    
    /**
     * Parse the XML file and return the DOM Document.
     * 
     * @param filename file containing control XML
     * @return parsed Document
     * @throws XMLParserException if parser encounters an error
     */
    private Document getDOM(String filename) throws XMLParserException {
        Document targetDOM = parserPool.parse(DecryptionPlusSigningTest.class.getResourceAsStream(filename));
        return targetDOM;
    }
    
}
