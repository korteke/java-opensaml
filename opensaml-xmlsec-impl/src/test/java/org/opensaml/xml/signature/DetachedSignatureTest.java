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

import java.security.KeyPair;

import net.shibboleth.utilities.java.support.xml.BasicParserPool;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.core.xml.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.validation.ValidationException;
import org.opensaml.xml.mock.SignableSimpleXMLObject;
import org.opensaml.xml.mock.SignableSimpleXMLObjectBuilder;
import org.opensaml.xml.security.SecurityHelper;
import org.opensaml.xml.security.credential.BasicCredential;
import org.opensaml.xml.signature.impl.SignatureBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class DetachedSignatureTest extends XMLObjectBaseTestCase {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(EnvelopedSignatureTest.class);

    /** Key resolver containing proper verification key. */
    private BasicCredential goodCredential;

    /** Key resolver containing invalid verification key. */
    private BasicCredential badCredential;

    /** Builder of mock XML objects. */
    private SignableSimpleXMLObjectBuilder sxoBuilder;

    /** Builder of Signature XML objects. */
    private SignatureBuilder sigBuilder;

    /** Parser pool used to parse example config files. */
    private BasicParserPool parserPool;

    /** Signature algorithm URI. */
    private String algoURI = SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1;

    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();

        KeyPair keyPair = SecurityHelper.generateKeyPair("RSA", 1024, null);
        goodCredential = SecurityHelper.getSimpleCredential(keyPair.getPublic(), keyPair.getPrivate());

        keyPair = SecurityHelper.generateKeyPair("RSA", 1024, null);
        badCredential = SecurityHelper.getSimpleCredential(keyPair.getPublic(), null);

        sxoBuilder = new SignableSimpleXMLObjectBuilder();
        sigBuilder = new SignatureBuilder();

        parserPool = new BasicParserPool();
        parserPool.setNamespaceAware(true);
    }

    /**
     * Tests creating a detached signature within the same document as the element signed and then verifying it.
     * 
     * @throws MarshallingException thrown if the XMLObject tree can not be marshalled
     * @throws ValidationException thrown if there is a problem attempting to validate the signature
     * @throws UnmarshallingException thrown if the signature can not be unmarshalled
     * @throws SignatureException 
     */
    public void testInternalSignatureAndVerification() throws MarshallingException, UnmarshallingException,
            ValidationException, SignatureException {
        SignableSimpleXMLObject sxo = getXMLObjectWithSignature();
        Signature signature = sxo.getSignature();

        Marshaller marshaller = XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(sxo);
        Element signedElement = marshaller.marshall(sxo);

        Signer.signObject(signature);
        if (log.isDebugEnabled()) {
            log.debug("Marshalled deatched Signature: \n" + SerializeSupport.nodeToString(signedElement));
        }

        Unmarshaller unmarshaller = XMLObjectProviderRegistrySupport.getUnmarshallerFactory().getUnmarshaller(signedElement);
        sxo = (SignableSimpleXMLObject) unmarshaller.unmarshall(signedElement);
        signature = (Signature) sxo.getOrderedChildren().get(1);

        SignatureValidator sigValidator = new SignatureValidator(goodCredential);
        sigValidator.validate(signature);

        try {
            sigValidator = new SignatureValidator(badCredential);
            sigValidator.validate(signature);
            fail("Validated signature with improper public key");
        } catch (ValidationException e) {
            // expected
        }
    }

    /**
     * Tests creating a detached signature within a different document as the element signed and then verifying it. The
     * external references used are the InCommon and InQueue metadata files.
     * 
     * @throws MarshallingException thrown if the XMLObject tree can not be marshalled
     * @throws ValidationException thrown if the signature verification fails
     * @throws SignatureException 
     */
    public void testExternalSignatureAndVerification() throws MarshallingException, ValidationException, SignatureException {
        Signature signature = sigBuilder.buildObject();
        signature.setSigningCredential(goodCredential);
        signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA);

        String incommonMetadata = "http://wayf.incommonfederation.org/InCommon/InCommon-metadata.xml";
        URIContentReference contentReference = new URIContentReference(incommonMetadata);
        contentReference.getTransforms().add(SignatureConstants.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
        contentReference.setDigestAlgorithm(SignatureConstants.ALGO_ID_DIGEST_SHA1);
        signature.getContentReferences().add(contentReference);

        Marshaller marshaller = XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(signature);
        Element signatureElement = marshaller.marshall(signature);

        Signer.signObject(signature);
        if (log.isDebugEnabled()) {
            log.debug("Marshalled deatched Signature: \n" + SerializeSupport.nodeToString(signatureElement));
        }

        SignatureValidator sigValidator = new SignatureValidator(goodCredential);
        sigValidator.validate(signature);
    }

    /**
     * Unmarshalls the XML DSIG spec RSA example signature and verifies it with the key contained in the KeyInfo.
     * 
     * @throws IOException thrown if the signature can not be fetched from the W3C site
     * @throws MalformedURLException thrown if the signature can not be fetched from the W3C site
     * @throws XMLParserException thrown if the signature is not valid XML
     * @throws UnmarshallingException thrown if the signature DOM can not be unmarshalled
     * @throws ValidationException thrown if the Signature does not validate against the key
     * @throws GeneralSecurityException
     * @throws SecurityException
     */
// TODO this test now fails because the detached signature document is a signature over a document that has now changed
//    public void testUnmarshallExternalSignatureAndVerification() throws IOException, MalformedURLException,
//            XMLParserException, UnmarshallingException, ValidationException, GeneralSecurityException, SecurityException {
//        String signatureLocation = "http://www.w3.org/TR/xmldsig-core/signature-example-rsa.xml";
//        InputStream ins = new URL(signatureLocation).openStream();
//        Element signatureElement = parserPool.parse(ins).getDocumentElement();
//
//        Unmarshaller unmarshaller = Configuration.getUnmarshallerFactory().getUnmarshaller(signatureElement);
//        Signature signature = (Signature) unmarshaller.unmarshall(signatureElement);
//
//        KeyInfoCredentialResolver resolver = XMLSecurityTestingHelper.buildBasicInlineKeyInfoResolver();
//
//        KeyInfoCriterion criteria = new KeyInfoCriterion(signature.getKeyInfo());
//        CriteriaSet criteriaSet = new CriteriaSet(criteria);
//        Credential credential = resolver.resolveSingle(criteriaSet);
//        SignatureValidator sigValidator = new SignatureValidator(credential);
//        sigValidator.validate(signature);
//    }

    /**
     * Creates a XMLObject that has another XMLObject and a Signature as children. The Signature is is a detached
     * signature of its sibling.
     * 
     * @return the XMLObject
     */
    private SignableSimpleXMLObject getXMLObjectWithSignature() {
        SignableSimpleXMLObject rootSXO = sxoBuilder.buildObject();

        SignableSimpleXMLObject childSXO = sxoBuilder.buildObject();
        childSXO.setId("FOO");
        rootSXO.getSimpleXMLObjects().add(childSXO);

        Signature sig = sigBuilder.buildObject();
        sig.setSigningCredential(goodCredential);
        sig.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        sig.setSignatureAlgorithm(algoURI);

        DocumentInternalIDContentReference contentReference = new DocumentInternalIDContentReference("FOO");
        contentReference.getTransforms().add(SignatureConstants.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
        contentReference.setDigestAlgorithm(SignatureConstants.ALGO_ID_DIGEST_SHA1);
        sig.getContentReferences().add(contentReference);

        rootSXO.setSignature(sig);
        return rootSXO;
    }
}