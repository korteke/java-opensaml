/*
 * Copyright [2007] [University Corporation for Advanced Internet Development, Inc.]
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

package org.opensaml.xml.security;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.ssl.PKCS8Key;
import org.apache.xml.security.Init;
import org.apache.xml.security.algorithms.JCEMapper;
import org.opensaml.core.config.ConfigurationService;
import org.opensaml.util.FileSupport;
import org.opensaml.util.StringSupport;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.encryption.EncryptionParameters;
import org.opensaml.xml.encryption.KeyEncryptionParameters;
import org.opensaml.xml.security.credential.BasicCredential;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xml.security.keyinfo.KeyInfoGenerator;
import org.opensaml.xml.security.keyinfo.KeyInfoGeneratorFactory;
import org.opensaml.xml.security.keyinfo.NamedKeyInfoGeneratorManager;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureConstants;
import org.opensaml.util.Base64;
import org.opensaml.util.collections.LazySet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper methods for security-related requirements.
 */
public final class XMLSecurityHelper {

    /** Additional algorithm URI's which imply RSA keys. */
    private static Set<String> rsaAlgorithmURIs;

    /** Additional algorithm URI's which imply DSA keys. */
    private static Set<String> dsaAlgorithmURIs;

    /** Additional algorithm URI's which imply ECDSA keys. */
    private static Set<String> ecdsaAlgorithmURIs;

    /** Constructor. */
    private XMLSecurityHelper() {
    }

    /**
     * Get the Java security JCA/JCE algorithm identifier associated with an algorithm URI.
     * 
     * @param algorithmURI the algorithm URI to evaluate
     * @return the Java algorithm identifier, or null if the mapping is unavailable or indeterminable from the URI
     */
    public static String getAlgorithmIDFromURI(String algorithmURI) {
        return StringSupport.trimOrNull(JCEMapper.translateURItoJCEID(algorithmURI));
    }

    /**
     * Check whether the signature method algorithm URI indicates HMAC.
     * 
     * @param signatureAlgorithm the signature method algorithm URI
     * @return true if URI indicates HMAC, false otherwise
     */
    public static boolean isHMAC(String signatureAlgorithm) {
        String algoClass = StringSupport.trimOrNull(JCEMapper.getAlgorithmClassFromURI(signatureAlgorithm));
        return ApacheXMLSecurityConstants.ALGO_CLASS_MAC.equals(algoClass);
    }

    /**
     * Get the Java security JCA/JCE key algorithm specifier associated with an algorithm URI.
     * 
     * @param algorithmURI the algorithm URI to evaluate
     * @return the Java key algorithm specifier, or null if the mapping is unavailable or indeterminable from the URI
     */
    public static String getKeyAlgorithmFromURI(String algorithmURI) {
        // The default Apache config file currently only includes the key algorithm for
        // the block ciphers and key wrap URI's. Note: could use a custom config file which contains others.
        String apacheValue = StringSupport.trimOrNull(JCEMapper.getJCEKeyAlgorithmFromURI(algorithmURI));
        if (apacheValue != null) {
            return apacheValue;
        }

        // HMAC uses any symmetric key, so there is no implied specific key algorithm
        if (isHMAC(algorithmURI)) {
            return null;
        }

        // As a last ditch fallback, check some known common and supported ones.
        if (rsaAlgorithmURIs.contains(algorithmURI)) {
            return "RSA";
        }
        if (dsaAlgorithmURIs.contains(algorithmURI)) {
            return "DSA";
        }
        if (ecdsaAlgorithmURIs.contains(algorithmURI)) {
            return "ECDSA";
        }

        return null;
    }
    
    /**
     * Get the length of the key indicated by the algorithm URI, if applicable and available.
     * 
     * @param algorithmURI the algorithm URI to evaluate
     * @return the length of the key indicated by the algorithm URI, or null if the length is either unavailable or
     *         indeterminable from the URI
     */
    public static Integer getKeyLengthFromURI(String algorithmURI) {
        Logger log = getLogger();
        String algoClass = StringSupport.trimOrNull(JCEMapper.getAlgorithmClassFromURI(algorithmURI));

        if (ApacheXMLSecurityConstants.ALGO_CLASS_BLOCK_ENCRYPTION.equals(algoClass)
                || ApacheXMLSecurityConstants.ALGO_CLASS_SYMMETRIC_KEY_WRAP.equals(algoClass)) {

            try {
                int keyLength = JCEMapper.getKeyLengthFromURI(algorithmURI);
                return new Integer(keyLength);
            } catch (NumberFormatException e) {
                log.warn("XML Security config contained invalid key length value for algorithm URI: " + algorithmURI);
            }
        }

        log.info("Mapping from algorithm URI {} to key length not available", algorithmURI);
        return null;
    }

    /**
     * Generates a random Java JCE symmetric Key object from the specified XML Encryption algorithm URI.
     * 
     * @param algoURI The XML Encryption algorithm URI
     * @return a randomly-generated symmetric Key
     * @throws NoSuchAlgorithmException thrown if the specified algorithm is invalid
     * @throws KeyException thrown if the length of the key to generate could not be determined
     */
    public static SecretKey generateSymmetricKey(String algoURI) throws NoSuchAlgorithmException, KeyException {
        Logger log = getLogger();
        String jceAlgorithmName = getKeyAlgorithmFromURI(algoURI);
        if (StringSupport.isNullOrEmpty(jceAlgorithmName)) {
            log.error("Mapping from algorithm URI '" + algoURI
                    + "' to key algorithm not available, key generation failed");
            throw new NoSuchAlgorithmException("Algorithm URI'" + algoURI + "' is invalid for key generation");
        }
        Integer keyLength = getKeyLengthFromURI(algoURI);
        if (keyLength == null) {
            log.error("Key length could not be determined from algorithm URI, can't generate key");
            throw new KeyException("Key length not determinable from algorithm URI, could not generate new key");
        }
        KeyGenerator keyGenerator = KeyGenerator.getInstance(jceAlgorithmName);
        keyGenerator.init(keyLength);
        return keyGenerator.generateKey();
    }
    
    /**
     * Randomly generates a Java JCE symmetric Key object from the specified XML Encryption algorithm URI.
     * 
     * @param algoURI  The XML Encryption algorithm URI
     * @return a randomly-generated symmteric key
     * @throws NoSuchProviderException  provider not found
     * @throws NoSuchAlgorithmException algorithm not found
     */
    public static SecretKey generateKeyFromURI(String algoURI) 
            throws NoSuchAlgorithmException, NoSuchProviderException {
        String jceAlgorithmName = JCEMapper.getJCEKeyAlgorithmFromURI(algoURI);
        int keyLength = JCEMapper.getKeyLengthFromURI(algoURI);
        return SecurityHelper.generateKey(jceAlgorithmName, keyLength, null);
    }
    
    /**
     * Randomly generates a Java JCE KeyPair object from the specified XML Encryption algorithm URI.
     * 
     * @param algoURI  The XML Encryption algorithm URI
     * @param keyLength  the length of key to generate
     * @return a randomly-generated KeyPair
     * @throws NoSuchProviderException  provider not found
     * @throws NoSuchAlgorithmException  algorithm not found
     */
    public static KeyPair generateKeyPairFromURI(String algoURI, int keyLength) 
            throws NoSuchAlgorithmException, NoSuchProviderException {
        String jceAlgorithmName = JCEMapper.getJCEKeyAlgorithmFromURI(algoURI);
        return SecurityHelper.generateKeyPair(jceAlgorithmName, keyLength, null);
    }
    
    /**
     * Generate a random symmetric key and return in a BasicCredential.
     * 
     * @param algorithmURI The XML Encryption algorithm URI
     * @return a basic credential containing a randomly generated symmetric key
     * @throws NoSuchAlgorithmException algorithm not found
     * @throws NoSuchProviderException provider not found
     */
    public static Credential generateKeyAndCredential(String algorithmURI) 
            throws NoSuchAlgorithmException, NoSuchProviderException {
        SecretKey key = generateKeyFromURI(algorithmURI);
        BasicCredential credential = new BasicCredential();
        credential.setSecretKey(key);
        return credential;
    }
    
    /**
     * Generate a random asymmetric key pair and return in a BasicCredential.
     * 
     * @param algorithmURI The XML Encryption algorithm URI
     * @param keyLength key length
     * @param includePrivate if true, the private key will be included as well
     * @return a basic credential containing a randomly generated asymmetric key pair
     * @throws NoSuchAlgorithmException algorithm not found
     * @throws NoSuchProviderException provider not found
     */
    public static Credential generateKeyPairAndCredential(String algorithmURI, int keyLength, boolean includePrivate) 
            throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPair keyPair = generateKeyPairFromURI(algorithmURI, keyLength);
        BasicCredential credential = new BasicCredential();
        credential.setPublicKey(keyPair.getPublic());
        if (includePrivate) {
            credential.setPrivateKey(keyPair.getPrivate());
        }
        return credential;
    }
    
    /**
     * Get the global XML security configuration instance.
     * 
     * @return the global XML security configuration
     */
    public static SecurityConfiguration getGlobalXMLSecurityConfiguration() {
        return ConfigurationService.get(SecurityConfiguration.class);
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
     * supplied, the global security configuration ({@link Configuration#getGlobalSecurityConfiguration()}) will be
     * used.
     * </p>
     * 
     * <p>
     * The signature algorithm URI and optional HMAC output length are derived from the signing credential.
     * </p>
     * 
     * <p>
     * The KeyInfo to be generated is based on the {@link NamedKeyInfoGeneratorManager} defined in the security
     * configuration, and is determined by the type of the signing credential and an optional KeyInfo generator manager
     * name. If the latter is ommited, the default manager ({@link NamedKeyInfoGeneratorManager#getDefaultManager()})
     * of the security configuration's named generator manager will be used.
     * </p>
     * 
     * @param signature the Signature to be updated
     * @param signingCredential the credential with which the Signature will be computed
     * @param config the SecurityConfiguration to use (may be null)
     * @param keyInfoGenName the named KeyInfoGeneratorManager configuration to use (may be null)
     * @throws SecurityException thrown if there is an error generating the KeyInfo from the signing credential
     */
    public static void prepareSignatureParams(Signature signature, Credential signingCredential,
            SecurityConfiguration config, String keyInfoGenName) throws SecurityException {
        Logger log = getLogger();

        SecurityConfiguration secConfig;
        if (config != null) {
            secConfig = config;
        } else {
            secConfig = getGlobalXMLSecurityConfiguration();
        }

        // The algorithm URI is derived from the credential
        String signAlgo = signature.getSignatureAlgorithm();
        if (signAlgo == null) {
            signAlgo = secConfig.getSignatureAlgorithmURI(signingCredential);
            signature.setSignatureAlgorithm(signAlgo);
        }

        // If we're doing HMAC, set the output length
        if (XMLSecurityHelper.isHMAC(signAlgo)) {
            if (signature.getHMACOutputLength() == null) {
                signature.setHMACOutputLength(secConfig.getSignatureHMACOutputLength());
            }
        }

        if (signature.getCanonicalizationAlgorithm() == null) {
            signature.setCanonicalizationAlgorithm(secConfig.getSignatureCanonicalizationAlgorithm());
        }

        if (signature.getKeyInfo() == null) {
            KeyInfoGenerator kiGenerator = getKeyInfoGenerator(signingCredential, secConfig, keyInfoGenName);
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
     * Build an instance of {@link EncryptionParameters} suitable for passing to an
     * {@link org.opensaml.xml.encryption.Encrypter}.
     * 
     * <p>
     * The following parameter values will be added:
     * <ul>
     * <li>the encryption credential (optional)</li>
     * <li>encryption algorithm URI</li>
     * <li>an appropriate {@link KeyInfoGenerator} instance which will be used to generate a {@link KeyInfo} element
     * from the encryption credential</li>
     * </ul>
     * </p>
     * 
     * <p>
     * All values are determined by the specified {@link SecurityConfiguration}. If a security configuration is not
     * supplied, the global security configuration ({@link Configuration#getGlobalSecurityConfiguration()}) will be
     * used.
     * </p>
     * 
     * <p>
     * The encryption algorithm URI is derived from the optional supplied encryption credential. If omitted, the value
     * of {@link SecurityConfiguration#getAutoGeneratedDataEncryptionKeyAlgorithmURI()} will be used.
     * </p>
     * 
     * <p>
     * The KeyInfoGenerator to be used is based on the {@link NamedKeyInfoGeneratorManager} defined in the security
     * configuration, and is determined by the type of the signing credential and an optional KeyInfo generator manager
     * name. If the latter is ommited, the default manager ({@link NamedKeyInfoGeneratorManager#getDefaultManager()})
     * of the security configuration's named generator manager will be used.
     * </p>
     * 
     * @param encryptionCredential the credential with which the data will be encrypted (may be null)
     * @param config the SecurityConfiguration to use (may be null)
     * @param keyInfoGenName the named KeyInfoGeneratorManager configuration to use (may be null)
     * @return a new instance of EncryptionParameters
     */
    public static EncryptionParameters buildDataEncryptionParams(Credential encryptionCredential,
            SecurityConfiguration config, String keyInfoGenName) {
        Logger log = getLogger();

        SecurityConfiguration secConfig;
        if (config != null) {
            secConfig = config;
        } else {
            secConfig = getGlobalXMLSecurityConfiguration();
        }

        EncryptionParameters encParams = new EncryptionParameters();
        encParams.setEncryptionCredential(encryptionCredential);

        if (encryptionCredential == null) {
            encParams.setAlgorithm(secConfig.getAutoGeneratedDataEncryptionKeyAlgorithmURI());
        } else {
            encParams.setAlgorithm(secConfig.getDataEncryptionAlgorithmURI(encryptionCredential));

            KeyInfoGenerator kiGenerator = getKeyInfoGenerator(encryptionCredential, secConfig, keyInfoGenName);
            if (kiGenerator != null) {
                encParams.setKeyInfoGenerator(kiGenerator);
            } else {
                log.info("No factory for named KeyInfoGenerator {} was found for credential type{}", keyInfoGenName,
                        encryptionCredential.getCredentialType().getName());
                log.info("No KeyInfo will be generated for EncryptedData");
            }
        }

        return encParams;
    }

    /**
     * Build an instance of {@link KeyEncryptionParameters} suitable for passing to an
     * {@link org.opensaml.xml.encryption.Encrypter}.
     * 
     * <p>
     * The following parameter values will be added:
     * <ul>
     * <li>the key encryption credential</li>
     * <li>key transport encryption algorithm URI</li>
     * <li>an appropriate {@link KeyInfoGenerator} instance which will be used to generate a {@link KeyInfo} element
     * from the key encryption credential</li>
     * <li>intended recipient of the resultant encrypted key (optional)</li>
     * </ul>
     * </p>
     * 
     * <p>
     * All values are determined by the specified {@link SecurityConfiguration}. If a security configuration is not
     * supplied, the global security configuration ({@link Configuration#getGlobalSecurityConfiguration()}) will be
     * used.
     * </p>
     * 
     * <p>
     * The encryption algorithm URI is derived from the optional supplied encryption credential. If omitted, the value
     * of {@link SecurityConfiguration#getAutoGeneratedDataEncryptionKeyAlgorithmURI()} will be used.
     * </p>
     * 
     * <p>
     * The KeyInfoGenerator to be used is based on the {@link NamedKeyInfoGeneratorManager} defined in the security
     * configuration, and is determined by the type of the signing credential and an optional KeyInfo generator manager
     * name. If the latter is ommited, the default manager ({@link NamedKeyInfoGeneratorManager#getDefaultManager()})
     * of the security configuration's named generator manager will be used.
     * </p>
     * 
     * @param encryptionCredential the credential with which the key will be encrypted
     * @param wrappedKeyAlgorithm the JCA key algorithm name of the key to be encrypted (may be null)
     * @param config the SecurityConfiguration to use (may be null)
     * @param keyInfoGenName the named KeyInfoGeneratorManager configuration to use (may be null)
     * @param recipient the intended recipient of the resultant encrypted key, typically the owner of the key encryption
     *            key (may be null)
     * @return a new instance of KeyEncryptionParameters
     * @throws SecurityException if encryption credential is not supplied
     * 
     */
    public static KeyEncryptionParameters buildKeyEncryptionParams(Credential encryptionCredential,
            String wrappedKeyAlgorithm, SecurityConfiguration config, String keyInfoGenName, String recipient)
            throws SecurityException {
        Logger log = getLogger();

        SecurityConfiguration secConfig;
        if (config != null) {
            secConfig = config;
        } else {
            secConfig = getGlobalXMLSecurityConfiguration();
        }

        KeyEncryptionParameters kekParams = new KeyEncryptionParameters();
        kekParams.setEncryptionCredential(encryptionCredential);

        if (encryptionCredential == null) {
            throw new SecurityException("Key encryption credential may not be null");
        }

        kekParams.setAlgorithm(secConfig.getKeyTransportEncryptionAlgorithmURI(encryptionCredential,
                wrappedKeyAlgorithm));

        KeyInfoGenerator kiGenerator = getKeyInfoGenerator(encryptionCredential, secConfig, keyInfoGenName);
        if (kiGenerator != null) {
            kekParams.setKeyInfoGenerator(kiGenerator);
        } else {
            log.info("No factory for named KeyInfoGenerator {} was found for credential type {}", keyInfoGenName,
                    encryptionCredential.getCredentialType().getName());
            log.info("No KeyInfo will be generated for EncryptedKey");
        }

        kekParams.setRecipient(recipient);

        return kekParams;
    }

    /**
     * Obtains a {@link KeyInfoGenerator} for the specified {@link Credential}.
     * 
     * <p>
     * The KeyInfoGenerator returned is based on the {@link NamedKeyInfoGeneratorManager} defined by the specified
     * security configuration via {@link SecurityConfiguration#getKeyInfoGeneratorManager()}, and is determined by the
     * type of the signing credential and an optional KeyInfo generator manager name. If the latter is ommited, the
     * default manager ({@link NamedKeyInfoGeneratorManager#getDefaultManager()}) of the security configuration's
     * named generator manager will be used.
     * </p>
     * 
     * <p>
     * The generator is determined by the specified {@link SecurityConfiguration}. If a security configuration is not
     * supplied, the global security configuration ({@link Configuration#getGlobalSecurityConfiguration()}) will be
     * used.
     * </p>
     * 
     * @param credential the credential for which a generator is desired
     * @param config the SecurityConfiguration to use (may be null)
     * @param keyInfoGenName the named KeyInfoGeneratorManager configuration to use (may be null)
     * @return a KeyInfoGenerator appropriate for the specified credential
     */
    public static KeyInfoGenerator getKeyInfoGenerator(Credential credential, SecurityConfiguration config,
            String keyInfoGenName) {

        SecurityConfiguration secConfig;
        if (config != null) {
            secConfig = config;
        } else {
            secConfig = getGlobalXMLSecurityConfiguration();
        }

        NamedKeyInfoGeneratorManager kiMgr = secConfig.getKeyInfoGeneratorManager();
        if (kiMgr != null) {
            KeyInfoGeneratorFactory kiFactory = null;
            if (StringSupport.isNullOrEmpty(keyInfoGenName)) {
                kiFactory = kiMgr.getDefaultManager().getFactory(credential);
            } else {
                kiFactory = kiMgr.getFactory(keyInfoGenName, credential);
            }
            if (kiFactory != null) {
                return kiFactory.newInstance();
            }
        }
        return null;
    }
    
    /**
     * Get an SLF4J Logger.
     * 
     * @return a Logger instance
     */
    private static Logger getLogger() {
        return LoggerFactory.getLogger(XMLSecurityHelper.class);
    }

    static {
        // We use some Apache XML Security utility functions, so need to make sure library
        // is initialized.
        if (!Init.isInitialized()) {
            Init.init();
        }

        // Additonal algorithm URI to JCA key algorithm mappins, beyond what is currently
        // supplied in the Apache XML Security mapper config.
        dsaAlgorithmURIs = new LazySet<String>();
        dsaAlgorithmURIs.add(SignatureConstants.ALGO_ID_SIGNATURE_DSA);

        ecdsaAlgorithmURIs = new LazySet<String>();
        ecdsaAlgorithmURIs.add(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA1);

        rsaAlgorithmURIs = new HashSet<String>(10);
        rsaAlgorithmURIs.add(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);
        rsaAlgorithmURIs.add(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        rsaAlgorithmURIs.add(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA384);
        rsaAlgorithmURIs.add(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA512);
        rsaAlgorithmURIs.add(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA512);
        rsaAlgorithmURIs.add(SignatureConstants.ALGO_ID_SIGNATURE_RSA_RIPEMD160);
        rsaAlgorithmURIs.add(SignatureConstants.ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5);
    }
}