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

package org.opensaml.xmlsec.config;

import java.security.interfaces.DSAParams;
import java.util.ArrayList;

import javax.annotation.Nonnull;

import org.opensaml.xmlsec.DecryptionConfiguration;
import org.opensaml.xmlsec.EncryptionConfiguration;
import org.opensaml.xmlsec.SignatureSigningConfiguration;
import org.opensaml.xmlsec.SignatureValidationConfiguration;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptedKey;
import org.opensaml.xmlsec.encryption.support.ChainingEncryptedKeyResolver;
import org.opensaml.xmlsec.encryption.support.EncryptedKeyResolver;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.encryption.support.InlineEncryptedKeyResolver;
import org.opensaml.xmlsec.encryption.support.SimpleKeyInfoReferenceEncryptedKeyResolver;
import org.opensaml.xmlsec.encryption.support.SimpleRetrievalMethodEncryptedKeyResolver;
import org.opensaml.xmlsec.impl.BasicDecryptionConfiguration;
import org.opensaml.xmlsec.impl.BasicEncryptionConfiguration;
import org.opensaml.xmlsec.impl.BasicSignatureSigningConfiguration;
import org.opensaml.xmlsec.impl.BasicSignatureValidationConfiguration;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoGeneratorManager;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;
import org.opensaml.xmlsec.keyinfo.impl.BasicKeyInfoGeneratorFactory;
import org.opensaml.xmlsec.keyinfo.impl.BasicProviderKeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.impl.KeyInfoProvider;
import org.opensaml.xmlsec.keyinfo.impl.X509KeyInfoGeneratorFactory;
import org.opensaml.xmlsec.keyinfo.impl.provider.DEREncodedKeyValueProvider;
import org.opensaml.xmlsec.keyinfo.impl.provider.DSAKeyValueProvider;
import org.opensaml.xmlsec.keyinfo.impl.provider.InlineX509DataProvider;
import org.opensaml.xmlsec.keyinfo.impl.provider.RSAKeyValueProvider;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureConstants;


/**
 * A utility class which programatically builds an instance of {@link BasicSecurityConfiguration}
 * which has reasonable default values for the various configuration parameters.
 */
public class DefaultSecurityConfigurationBootstrap {
    
    /** Constructor. */
    protected DefaultSecurityConfigurationBootstrap() {}
    
    /**
     * Build and return a default configuration.
     * 
     * @return a new basic security configuration with reasonable default values
     */
    @Nonnull public static BasicSecurityConfiguration buildDefaultConfig() {
        //TODO remove when security config refactoring complete
        BasicSecurityConfiguration config = new BasicSecurityConfiguration();
        
        populateSignatureParams(config);
        populateEncryptionParams(config);
        
        config.setDefaultKeyInfoCredentialResolver(buildBasicKeyInfoCredentialResolver());
        config.setKeyInfoGeneratorManager(buildBasicKeyInfoGeneratorManager());
        
        populateKeyParams(config);
        
        return config;
    }
    
    /**
     * Build and return a default encryption configuration.
     * 
     * @return a new basic configuration with reasonable default values
     */
    @Nonnull public static EncryptionConfiguration buildDefaultEncryptionConfiguration() {
        BasicEncryptionConfiguration config = new BasicEncryptionConfiguration();
        
        //TODO blacklist anything by default?
        //TODO various data and key transport algo URI prefs, when API is stabilized
        //TODO auto-generated data enc URI prefs
        
        config.setDataKeyInfoGeneratorManager(buildDataEncryptionKeyInfoGeneratorManager());
        config.setKeyTransportKeyInfoGeneratorManager(buildKeyTransportEncryptionKeyInfoGeneratorManager());
        
        return config;
    }
    
    /**
     * Build and return a default decryption configuration.
     * 
     * @return a new basic configuration with reasonable default values
     */
    @Nonnull public static DecryptionConfiguration buildDefaultDecryptionConfiguration() {
        BasicDecryptionConfiguration config = new BasicDecryptionConfiguration();
        
        //TODO blacklist anything by default?
        
        config.setDataKeyInfoCredentialResolver(buildEncryptionDataKeyInfoCredentialResolver());
        config.setKEKKeyInfoCredentialResolver(buildEncryptionKEKKeyInfoCredentialResolver());
        config.setEncryptedKeyResolver(buildBasicEncryptedKeyResolver());
        
        return config;
    }

    /**
     * Build and return a default signature signing configuration.
     * 
     * @return a new basic configuration with reasonable default values
     */
    @Nonnull public static SignatureSigningConfiguration buildDefaultSignatureSigningConfiguration() {
        BasicSignatureSigningConfiguration config = new BasicSignatureSigningConfiguration();
        
        //TODO blacklist anything by default?
        //TODO various signing method algo URI prefs, when API is stabilized
        //TODO various digest method algo URI prefs, when API is stabilized
        //TODO various C14N algo URI prefs, when API is stabilized
        //TODO what to do about DSAParams?
        //TODO default HMAC output length?
        
        config.setKeyInfoGeneratorManager(buildSignatureKeyInfoGeneratorManager());
        
        return config;
    }
    
    /**
     * Build and return a default signature validation configuration.
     * 
     * @return a new basic configuration with reasonable default values
     */
    @Nonnull public static SignatureValidationConfiguration buildDefaultSignatureValidationConfiguration() {
        BasicSignatureValidationConfiguration config = new BasicSignatureValidationConfiguration();
        
        //TODO blacklist anything by default?
        
        return config;
    }
    
    /**
     * Populate signature-related parameters.
     * 
     * @param config the security configuration to populate
     */
    protected static void populateSignatureParams(@Nonnull final BasicSecurityConfiguration config) {
        // Asymmetric key algorithms
        config.registerSignatureAlgorithmURI("RSA", SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);
        config.registerSignatureAlgorithmURI("DSA", SignatureConstants.ALGO_ID_SIGNATURE_DSA);
        config.registerSignatureAlgorithmURI("EC", SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA1);
        
        // HMAC algorithms
        config.registerSignatureAlgorithmURI("AES", SignatureConstants.ALGO_ID_MAC_HMAC_SHA1);
        config.registerSignatureAlgorithmURI("DESede", SignatureConstants.ALGO_ID_MAC_HMAC_SHA1);
        
        // Other signature-related params
        config.setSignatureCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        config.setSignatureHMACOutputLength(null);
        config.setSignatureReferenceDigestMethod(SignatureConstants.ALGO_ID_DIGEST_SHA1);
    }
    
    /**
     * Populate encryption-related parameters.
     * 
     * @param config the security configuration to populate
     */
    protected static void populateEncryptionParams(@Nonnull final BasicSecurityConfiguration config) {        
        // Data encryption URI's
        config.registerDataEncryptionAlgorithmURI("AES", 128, EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128);
        config.registerDataEncryptionAlgorithmURI("AES", 192, EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192);
        config.registerDataEncryptionAlgorithmURI("AES", 256, EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256);
        config.registerDataEncryptionAlgorithmURI("DESede", 168, EncryptionConstants.ALGO_ID_BLOCKCIPHER_TRIPLEDES);
        config.registerDataEncryptionAlgorithmURI("DESede", 192, EncryptionConstants.ALGO_ID_BLOCKCIPHER_TRIPLEDES);
        
        // Key encryption URI's
        
        // Asymmetric key transport algorithms
        config.registerKeyTransportEncryptionAlgorithmURI(
                "RSA", null, "AES", EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        config.registerKeyTransportEncryptionAlgorithmURI(
                "RSA", null, "DESede", EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        
        // Symmetric key wrap algorithms
        config.registerKeyTransportEncryptionAlgorithmURI("AES", 128, null, EncryptionConstants.ALGO_ID_KEYWRAP_AES128);
        config.registerKeyTransportEncryptionAlgorithmURI("AES", 192, null, EncryptionConstants.ALGO_ID_KEYWRAP_AES192);
        config.registerKeyTransportEncryptionAlgorithmURI("AES", 256, null, EncryptionConstants.ALGO_ID_KEYWRAP_AES256);
        config.registerKeyTransportEncryptionAlgorithmURI(
                "DESede", 168, null, EncryptionConstants.ALGO_ID_KEYWRAP_TRIPLEDES);
        config.registerKeyTransportEncryptionAlgorithmURI(
                "DESede", 192, null, EncryptionConstants.ALGO_ID_KEYWRAP_TRIPLEDES);
        
        // Other encryption-related params
        config.setAutoGeneratedDataEncryptionKeyAlgorithmURI(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128);
    }
    
    /**
     * Build a basic instance of {@link EncryptedKeyResolver}.
     * 
     * @return an EncryptedKey resolver instance
     */
    protected static EncryptedKeyResolver buildBasicEncryptedKeyResolver() {
        ChainingEncryptedKeyResolver resolver = new ChainingEncryptedKeyResolver();
        resolver.getResolverChain().add(new InlineEncryptedKeyResolver());
        resolver.getResolverChain().add(new SimpleRetrievalMethodEncryptedKeyResolver());
        resolver.getResolverChain().add(new SimpleKeyInfoReferenceEncryptedKeyResolver());
        return resolver;
    }

    /**
     * Build a basic instance of {@link KeyInfoCredentialResolver} for handling {@link KeyInfo}
     * instances located within an {@link EncryptedData}.
     * 
     * @return a KeyInfo credential resolver instance
     */
    protected static KeyInfoCredentialResolver buildEncryptionDataKeyInfoCredentialResolver() {
        return buildBasicKeyInfoCredentialResolver();
    }
    
    /**
     * Build a basic instance of {@link KeyInfoCredentialResolver} for handling {@link KeyInfo}
     * instances located within an {@link EncryptedKey}.
     * 
     * @return a KeyInfo credential resolver instance
     */
    protected static KeyInfoCredentialResolver buildEncryptionKEKKeyInfoCredentialResolver() {
        return buildBasicKeyInfoCredentialResolver();
    }
    
    /**
     * Build a basic instance of {@link KeyInfoCredentialResolver}.
     * 
     * @return a KeyInfo credential resolver instance
     */
    protected static KeyInfoCredentialResolver buildBasicKeyInfoCredentialResolver() {
        // Basic resolver for inline info
        ArrayList<KeyInfoProvider> providers = new ArrayList<KeyInfoProvider>();
        providers.add( new RSAKeyValueProvider() );
        providers.add( new DSAKeyValueProvider() );
        providers.add( new DEREncodedKeyValueProvider() );
        providers.add( new InlineX509DataProvider() );
        
        KeyInfoCredentialResolver resolver = new BasicProviderKeyInfoCredentialResolver(providers);
        return resolver;
    }

    /**
     * Build a basic {@link NamedKeyInfoGeneratorManager} for use when generating an {@link EncryptedData}.
     * 
     * @return a named KeyInfo generator manager instance
     */
    protected static NamedKeyInfoGeneratorManager buildDataEncryptionKeyInfoGeneratorManager() {
        return buildBasicKeyInfoGeneratorManager();
    }
    
    /**
     * Build a basic {@link NamedKeyInfoGeneratorManager} for use when generating an {@link EncryptedData}.
     * 
     * @return a named KeyInfo generator manager instance
     */
    protected static NamedKeyInfoGeneratorManager buildKeyTransportEncryptionKeyInfoGeneratorManager() {
        return buildBasicKeyInfoGeneratorManager();
    }
    
    /**
     * Build a basic {@link NamedKeyInfoGeneratorManager} for use when generating an {@link Signature}.
     * 
     * @return a named KeyInfo generator manager instance
     */
    protected static NamedKeyInfoGeneratorManager buildSignatureKeyInfoGeneratorManager() {
        return buildBasicKeyInfoGeneratorManager();
    }
    
    /**
     * Build a basic {@link NamedKeyInfoGeneratorManager}.
     * 
     * @return a named KeyInfo generator manager instance
     */
    protected static NamedKeyInfoGeneratorManager buildBasicKeyInfoGeneratorManager() {
        NamedKeyInfoGeneratorManager namedManager = new NamedKeyInfoGeneratorManager();
        
        namedManager.setUseDefaultManager(true);
        KeyInfoGeneratorManager defaultManager = namedManager.getDefaultManager();
        
        // Generator for basic Credentials
        BasicKeyInfoGeneratorFactory basicFactory = new BasicKeyInfoGeneratorFactory();
        basicFactory.setEmitPublicKeyValue(true);
        
        // Generator for X509Credentials
        X509KeyInfoGeneratorFactory x509Factory = new X509KeyInfoGeneratorFactory();
        x509Factory.setEmitEntityCertificate(true);
        
        defaultManager.registerFactory(basicFactory);
        defaultManager.registerFactory(x509Factory);
        
        return namedManager;
    }

    /**
     * Populate misc key-related parameters.
     * 
     * @param config the security configuration to populate
     */
    //TODO remove when refactoring done.
    protected static void populateKeyParams(@Nonnull final BasicSecurityConfiguration config) {
        // Maybe populate some DSA parameters here, if there are commonly accepcted default values
    }
    
    /**
     * Populate misc key-related parameters.
     * 
     * @param config the security configuration to populate
     */
    protected static DSAParams buildDSAParams() {
        // TODO Is there a standard instance here?  
        // Thought there was a standard "key family" that is used, at least in Java...
        return null;
    }

}