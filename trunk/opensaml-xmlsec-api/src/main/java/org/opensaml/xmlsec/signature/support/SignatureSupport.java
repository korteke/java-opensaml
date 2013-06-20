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
import javax.annotation.Nullable;

import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.SecurityConfiguration;
import org.opensaml.xmlsec.SecurityConfigurationSupport;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.crypto.AlgorithmSupport;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.keyinfo.KeyInfoSupport;
import org.opensaml.xmlsec.signature.KeyInfo;
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

    //TODO decide whether to keep this overloaded method variant, or just the SignatureSigningParameters one.

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
     * All values are determined by the specified {@link SecurityConfiguration}. If a security configuration is not
     * supplied, the global security configuration
     * (from {@link SecurityConfigurationSupport#getGlobalXMLSecurityConfiguration()})
     * will be used.
     * </p>
     * 
     * <p>
     * The signature algorithm URI and optional HMAC output length are derived from the signing credential.
     * </p>
     * 
     * <p>
     * The KeyInfo to be generated is based on the {@link NamedKeyInfoGeneratorManager} defined in the security
     * configuration, and is determined by the type of the signing credential and an optional KeyInfo generator manager
     * name. If the latter is omitted, the default manager ({@link NamedKeyInfoGeneratorManager#getDefaultManager()})
     * of the security configuration's named generator manager will be used.
     * </p>
     * 
     * @param signature the Signature to be updated
     * @param signingCredential the credential with which the Signature will be computed
     * @param config the SecurityConfiguration to use (may be null)
     * @param keyInfoGenName the named KeyInfoGeneratorManager configuration to use (may be null)
     * @throws SecurityException thrown if there is an error generating the KeyInfo from the signing credential
     */
    public static void prepareSignatureParams(@Nonnull final Signature signature,
            @Nonnull final Credential signingCredential, @Nullable final SecurityConfiguration config,
            @Nullable final String keyInfoGenName) throws SecurityException {
        Logger log = getLogger();
    
        SecurityConfiguration secConfig;
        if (config != null) {
            secConfig = config;
        } else {
            secConfig = SecurityConfigurationSupport.getGlobalXMLSecurityConfiguration();
        }
    
        // The algorithm URI is derived from the credential.
        String signAlgo = signature.getSignatureAlgorithm();
        if (signAlgo == null) {
            signAlgo = secConfig.getSignatureAlgorithmURI(signingCredential);
            signature.setSignatureAlgorithm(signAlgo);
        }
    
        // If we're doing HMAC, set the output length.
        if (AlgorithmSupport.isHMAC(signAlgo)) {
            if (signature.getHMACOutputLength() == null) {
                signature.setHMACOutputLength(secConfig.getSignatureHMACOutputLength());
            }
        }
    
        if (signature.getCanonicalizationAlgorithm() == null) {
            signature.setCanonicalizationAlgorithm(secConfig.getSignatureCanonicalizationAlgorithm());
        }
        
        // Note: Only override what's in the reference if we were passed a non-null configuration,
        // and it's not the global one.
        String digestAlgo = null;
        if (config != null && config != SecurityConfigurationSupport.getGlobalXMLSecurityConfiguration()) {
            digestAlgo = config.getSignatureReferenceDigestMethod();
        }
        for (ContentReference cr : signature.getContentReferences()) {
            if (cr instanceof ConfigurableContentReference) {
                ConfigurableContentReference configurableReference = (ConfigurableContentReference) cr;
                if (digestAlgo != null) {
                    configurableReference.setDigestAlgorithm(digestAlgo);
                } else {
                    if (configurableReference.getDigestAlgorithm() == null) {
                        configurableReference.setDigestAlgorithm(SecurityConfigurationSupport.
                                getGlobalXMLSecurityConfiguration().getSignatureReferenceDigestMethod());
                    }
                }
            }
        }
    
        if (signature.getKeyInfo() == null) {
            KeyInfoGenerator kiGenerator = 
                    KeyInfoSupport.getKeyInfoGenerator(signingCredential, secConfig, keyInfoGenName);
            if (kiGenerator != null) {
                try {
                    KeyInfo keyInfo = kiGenerator.generate(signingCredential);
                    signature.setKeyInfo(keyInfo);
                } catch (SecurityException e) {
                    log.error("Error generating KeyInfo from credential", e);
                    throw e;
                }
            } else {
                log.info("No factory for named KeyInfoGenerator {} was found for credential type {}", keyInfoGenName,
                        signingCredential.getCredentialType().getName());
                log.info("No KeyInfo will be generated for Signature");
            }
        }
    }
    
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
     * All values are determined by the specified {@link SignatureSigningParameters}. If a particular parameter value
     * is not supplied, the corresponding value from the global security configuration
     * (from {@link SecurityConfigurationSupport#getGlobalXMLSecurityConfiguration()})
     * will be used.
     * </p>
     * 
     * @param signature the Signature to be updated
     * @param parameters the signing parameters to use
     * 
     * @throws SecurityException thrown if no credential is supplied, or there is an error generating 
     *          the KeyInfo from the signing credential
     */
    public static void prepareSignatureParams(@Nonnull final Signature signature,
            @Nonnull final SignatureSigningParameters parameters) throws SecurityException {
        Logger log = getLogger();
        
        Credential signingCredential = signature.getSigningCredential();
        if (signingCredential == null) {
            signingCredential = parameters.getSigningCredential();
            signature.setSigningCredential(signingCredential);
        }
        
        if (signature.getSigningCredential() == null) {
            throw new SecurityException("Signing credential was null");
        }
        
        SecurityConfiguration globalSecurityConfig = SecurityConfigurationSupport.getGlobalXMLSecurityConfiguration();
    
        String signAlgo = signature.getSignatureAlgorithm();
        if (signAlgo == null) {
            signAlgo = parameters.getSignatureAlgorithmURI();
            if (signAlgo == null) {
                // In this case the algorithm URI is derived from the credential.
                signAlgo = globalSecurityConfig.getSignatureAlgorithmURI(signingCredential);
            }
            signature.setSignatureAlgorithm(signAlgo);
        }
    
        // If we're doing HMAC, set the output length.
        if (AlgorithmSupport.isHMAC(signAlgo)) {
            if (signature.getHMACOutputLength() == null) {
                Integer hmacOutputLength = parameters.getSignatureHMACOutputLength();
                if (hmacOutputLength == null) {
                    hmacOutputLength = globalSecurityConfig.getSignatureHMACOutputLength();
                }
                signature.setHMACOutputLength(hmacOutputLength);
            }
        }
    
        if (signature.getCanonicalizationAlgorithm() == null) {
            String c14Algo = parameters.getSignatureCanonicalizationAlgorithm();
            if (c14Algo == null) {
                c14Algo = globalSecurityConfig.getSignatureCanonicalizationAlgorithm();
            }
            signature.setCanonicalizationAlgorithm(c14Algo);
        }
        
        // Note: Only override what's in the reference if we were passed a non-null value in the parameters.
        String digestAlgo = parameters.getSignatureReferenceDigestMethod();
        for (ContentReference cr : signature.getContentReferences()) {
            if (cr instanceof ConfigurableContentReference) {
                ConfigurableContentReference configurableReference = (ConfigurableContentReference) cr;
                if (digestAlgo != null) {
                    configurableReference.setDigestAlgorithm(digestAlgo);
                } else {
                    if (configurableReference.getDigestAlgorithm() == null) {
                        configurableReference.setDigestAlgorithm(
                                globalSecurityConfig.getSignatureReferenceDigestMethod());
                    }
                }
            }
        }
    
        if (signature.getKeyInfo() == null) {
            KeyInfoGenerator kiGenerator = parameters.getKeyInfoGenerator();
            if (kiGenerator == null) {
                kiGenerator = KeyInfoSupport.getKeyInfoGenerator(signingCredential, globalSecurityConfig, null);
            }
            if (kiGenerator != null) {
                try {
                    KeyInfo keyInfo = kiGenerator.generate(signingCredential);
                    signature.setKeyInfo(keyInfo);
                } catch (SecurityException e) {
                    log.error("Error generating KeyInfo from credential", e);
                    throw e;
                }
            } else {
                log.info("No KeyInfoGenerator was supplied in parameters or resolveable for credential type {}", 
                        signingCredential.getCredentialType().getName());
                log.info("No KeyInfo will be generated for Signature");
            }
        }
    }

}
