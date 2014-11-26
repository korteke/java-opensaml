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

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.security.SecurityException;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.SignableXMLObject;
import org.opensaml.xmlsec.signature.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper methods for working with XML Signature.
 */
public final class SignatureSupport {
    
    //TODO refactor these methods to get method length and cyclomatic complexity down.
    
    /** Constructor. */
    private SignatureSupport() {
        
    }
    
    /**
     * Get an SLF4J Logger.
     * 
     * @return a Logger instance
     */
    @Nonnull private static Logger getLogger() {
        return LoggerFactory.getLogger(SignatureSupport.class);
    }
    
// Checkstyle: CyclomaticComplexity OFF
    /**
     * Prepare a {@link Signature} with necessary additional information prior to signing.
     * 
     * <p>
     * <strong>NOTE:</strong>Since this operation modifies the specified Signature object, it should be called
     * <strong>prior</strong> to marshalling the Signature object.
     * </p>
     * 
     * <p>
     * The following Signature values will be added:
     * <ul>
     * <li>signing credential</li>
     * <li>signature algorithm URI</li>
     * <li>canonicalization algorithm URI</li>
     * <li>reference digest method</li>
     * <li>HMAC output length (if applicable and a value is configured)</li>
     * <li>a {@link KeyInfo} element representing the signing credential</li>
     * </ul>
     * </p>
     * 
     * <p>
     * Existing (non-null) values of these parameters on the specified signature will <strong>NOT</strong> be
     * overwritten, however.
     * </p>
     * 
     * <p>
     * All values are determined by the specified {@link SignatureSigningParameters}. If no value for 
     * a required parameter is specified or included on the passed signature, a {@link SecurityException}
     * will be thrown.
     * </p>
     * 
     * @param signature the Signature to be updated
     * @param parameters the signing parameters to use
     * 
     * @throws SecurityException thrown if a required parameter is not supplied in the parameters instance
     *          or available on the Signature instance
     */
    public static void prepareSignatureParams(@Nonnull final Signature signature,
            @Nonnull final SignatureSigningParameters parameters) throws SecurityException {
        Constraint.isNotNull(signature, "Signature cannot be null");
        Constraint.isNotNull(parameters, "Signature signing parameters cannot be null");

        Logger log = getLogger();
        
        // Signing credential
        if (signature.getSigningCredential() == null) {
            signature.setSigningCredential(parameters.getSigningCredential());
        }
        if (signature.getSigningCredential() == null) {
            throw new SecurityException("No signing credential was available on the signing parameters or Signature");
        }
    
        // Signing algorithm
        if (signature.getSignatureAlgorithm() == null) {
            signature.setSignatureAlgorithm(parameters.getSignatureAlgorithm());
        }
        if (signature.getSignatureAlgorithm() == null) {
            throw new SecurityException("No signature algorithm was available on the signing parameters or Signature");
        }
    
        // HMAC output length, if applicable
        if (signature.getHMACOutputLength() == null &&  AlgorithmSupport.isHMAC(signature.getSignatureAlgorithm())) {
            signature.setHMACOutputLength(parameters.getSignatureHMACOutputLength());
        }
    
        // C14N
        if (signature.getCanonicalizationAlgorithm() == null) {
            signature.setCanonicalizationAlgorithm(parameters.getSignatureCanonicalizationAlgorithm());
        }
        if (signature.getCanonicalizationAlgorithm() == null) {
            throw new SecurityException("No C14N algorithm was available on the signing parameters or Signature");
        }
    
        // Reference(s) digest method
        String paramsDigestAlgo = parameters.getSignatureReferenceDigestMethod();
        for (ContentReference cr : signature.getContentReferences()) {
            if (cr instanceof ConfigurableContentReference) {
                ConfigurableContentReference configurableReference = (ConfigurableContentReference) cr;
                if (paramsDigestAlgo != null) {
                    configurableReference.setDigestAlgorithm(paramsDigestAlgo);
                }
                if (configurableReference.getDigestAlgorithm() == null) {
                    throw new SecurityException("No reference digest algorithm was available " 
                            + "on the signing parameters or Signature ContentReference");
                }
            }
        }
    
        // KeyInfo
        if (signature.getKeyInfo() == null) {
            KeyInfoGenerator kiGenerator = parameters.getKeyInfoGenerator();
            if (kiGenerator != null) {
                try {
                    KeyInfo keyInfo = kiGenerator.generate(signature.getSigningCredential());
                    signature.setKeyInfo(keyInfo);
                } catch (SecurityException e) {
                    log.error("Error generating KeyInfo from credential", e);
                    throw e;
                }
            } else {
                log.info("No KeyInfoGenerator was supplied in parameters or resolveable " 
                        + "for credential type {}, No KeyInfo will be generated for Signature", 
                        signature.getSigningCredential().getCredentialType().getName());
            }
        }
    }
// Checkstyle: CyclomaticComplexity ON
    
    /**
     * Signs a {@link SignableXMLObject}.
     * 
     * @param signable the signable XMLObject to sign
     * @param parameters the signing parameters to use
     * 
     * @throws SecurityException if there is a problem preparing the signature
     * @throws MarshallingException if there is a problem marshalling the XMLObject
     * @throws SignatureException if there is a problem with the signature operation
     */
    public static void signObject(@Nonnull final SignableXMLObject signable,
            @Nonnull final SignatureSigningParameters parameters) throws SecurityException, MarshallingException,
            SignatureException {
        Constraint.isNotNull(signable, "Signable XMLObject cannot be null");
        Constraint.isNotNull(parameters, "Signature signing parameters cannot be null");

        XMLObjectBuilder<Signature> signatureBuilder =
                (XMLObjectBuilder<Signature>) XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(
                        Signature.DEFAULT_ELEMENT_NAME);
        Signature signature = signatureBuilder.buildObject(Signature.DEFAULT_ELEMENT_NAME);

        signable.setSignature(signature);

        SignatureSupport.prepareSignatureParams(signature, parameters);

        Marshaller marshaller = XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(signable);
        marshaller.marshall(signable);

        Signer.signObject(signature);
    }

}
