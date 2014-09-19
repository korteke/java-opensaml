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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.opensaml.xmlsec.encryption.support.ChainingEncryptedKeyResolver;
import org.opensaml.xmlsec.encryption.support.EncryptedKeyResolver;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.encryption.support.InlineEncryptedKeyResolver;
import org.opensaml.xmlsec.encryption.support.RSAOAEPParameters;
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
import org.opensaml.xmlsec.signature.support.SignatureConstants;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


/**
 * A utility class which programmatically builds basic instances of various components 
 * related to security configuration which have reasonable default values for their 
 * various configuration parameters.
 */
public class DefaultSecurityConfigurationBootstrap {
    
    /** Constructor. */
    protected DefaultSecurityConfigurationBootstrap() {}
    
    /**
     * Build and return a default encryption configuration.
     * 
     * @return a new basic configuration with reasonable default values
     */
    @Nonnull public static BasicEncryptionConfiguration buildDefaultEncryptionConfiguration() {
        BasicEncryptionConfiguration config = new BasicEncryptionConfiguration();
        
        config.setBlacklistedAlgorithms(Sets.newHashSet(
                EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15
                ));
        
        config.setDataEncryptionAlgorithms(Lists.newArrayList(
                // The order of these is significant.
                EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128,
                EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192,
                EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256,
                EncryptionConstants.ALGO_ID_BLOCKCIPHER_TRIPLEDES
                ));
        
        config.setKeyTransportEncryptionAlgorithms(Lists.newArrayList(
                // The order of the RSA algos is significant.
                EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP,
                
                // The order of these is not significant.
                // These aren't really "preferences" per se. They just need to be registered 
                // so that they can be used if a credential with a key of that type and size is seen.
                EncryptionConstants.ALGO_ID_KEYWRAP_AES128,
                EncryptionConstants.ALGO_ID_KEYWRAP_AES192,
                EncryptionConstants.ALGO_ID_KEYWRAP_AES256,
                EncryptionConstants.ALGO_ID_KEYWRAP_TRIPLEDES
                ));
        
        config.setRSAOAEPParameters(new RSAOAEPParameters(
                EncryptionConstants.ALGO_ID_DIGEST_SHA256, 
                EncryptionConstants.ALGO_ID_MGF1_SHA256, 
                null
                ));
        
        config.setDataKeyInfoGeneratorManager(buildDataEncryptionKeyInfoGeneratorManager());
        config.setKeyTransportKeyInfoGeneratorManager(buildKeyTransportEncryptionKeyInfoGeneratorManager());
        
        return config;
    }
    
    /**
     * Build and return a default decryption configuration.
     * 
     * @return a new basic configuration with reasonable default values
     */
    @Nonnull public static BasicDecryptionConfiguration buildDefaultDecryptionConfiguration() {
        BasicDecryptionConfiguration config = new BasicDecryptionConfiguration();
        
        config.setBlacklistedAlgorithms(Sets.newHashSet(
                EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15
                ));
        
        config.setEncryptedKeyResolver(buildBasicEncryptedKeyResolver());
        
        return config;
    }

    /**
     * Build and return a default signature signing configuration.
     * 
     * @return a new basic configuration with reasonable default values
     */
    @Nonnull public static BasicSignatureSigningConfiguration buildDefaultSignatureSigningConfiguration() {
        BasicSignatureSigningConfiguration config = new BasicSignatureSigningConfiguration();
        
        config.setBlacklistedAlgorithms(Sets.newHashSet(
                SignatureConstants.ALGO_ID_DIGEST_NOT_RECOMMENDED_MD5,
                SignatureConstants.ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5,
                SignatureConstants.ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5
                ));
        
        config.setSignatureAlgorithms(Lists.newArrayList(
                // The order within each key group is significant.
                // The order of the key groups themselves is not significant.
                
                // RSA
                SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256,
                SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA384,
                SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA512,
                SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1,
                
                // ECDSA
                SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA256,
                SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA384,
                SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA512,
                SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA1,
                
                // DSA
                SignatureConstants.ALGO_ID_SIGNATURE_DSA_SHA1,
                
                // HMAC (all symmetric keys)
                SignatureConstants.ALGO_ID_MAC_HMAC_SHA256,
                SignatureConstants.ALGO_ID_MAC_HMAC_SHA384,
                SignatureConstants.ALGO_ID_MAC_HMAC_SHA512,
                SignatureConstants.ALGO_ID_MAC_HMAC_SHA1
                ));
        
        config.setSignatureReferenceDigestMethods(Lists.newArrayList(
                // The order of these is significant.
                SignatureConstants.ALGO_ID_DIGEST_SHA256,
                SignatureConstants.ALGO_ID_DIGEST_SHA384,
                SignatureConstants.ALGO_ID_DIGEST_SHA512,
                SignatureConstants.ALGO_ID_DIGEST_SHA1
                ));
        
        config.setSignatureCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        
        config.setKeyInfoGeneratorManager(buildSignatureKeyInfoGeneratorManager());
        
        return config;
    }
    
    /**
     * Build and return a default signature validation configuration.
     * 
     * @return a new basic configuration with reasonable default values
     */
    @Nonnull public static BasicSignatureValidationConfiguration buildDefaultSignatureValidationConfiguration() {
        BasicSignatureValidationConfiguration config = new BasicSignatureValidationConfiguration();
        
        config.setBlacklistedAlgorithms(Sets.newHashSet(
                SignatureConstants.ALGO_ID_DIGEST_NOT_RECOMMENDED_MD5,
                SignatureConstants.ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5,
                SignatureConstants.ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5
                ));
        
        return config;
    }
    
    /**
     * Build a basic instance of {@link EncryptedKeyResolver}.
     * 
     * @return an EncryptedKey resolver instance
     */
    protected static EncryptedKeyResolver buildBasicEncryptedKeyResolver() {
        List<EncryptedKeyResolver> resolverChain = new ArrayList<>();
        resolverChain.add(new InlineEncryptedKeyResolver()); 
        resolverChain.add(new SimpleRetrievalMethodEncryptedKeyResolver());
        resolverChain.add(new SimpleKeyInfoReferenceEncryptedKeyResolver());
        
        return new ChainingEncryptedKeyResolver(resolverChain);
    }

    /**
     * Build a basic instance of {@link KeyInfoCredentialResolver}.
     * 
     * @return a KeyInfo credential resolver instance
     */
    public static KeyInfoCredentialResolver buildBasicInlineKeyInfoCredentialResolver() {
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
     * Build a basic {@link NamedKeyInfoGeneratorManager} for use when generating an
     * {@link org.opensaml.xmlsec.encryption.EncryptedData}.
     * 
     * @return a named KeyInfo generator manager instance
     */
    protected static NamedKeyInfoGeneratorManager buildDataEncryptionKeyInfoGeneratorManager() {
        return buildBasicKeyInfoGeneratorManager();
    }
    
    /**
     * Build a basic {@link NamedKeyInfoGeneratorManager} for use when generating an
     * {@link org.opensaml.xmlsec.encryption.EncryptedKey}.
     * 
     * @return a named KeyInfo generator manager instance
     */
    protected static NamedKeyInfoGeneratorManager buildKeyTransportEncryptionKeyInfoGeneratorManager() {
        return buildBasicKeyInfoGeneratorManager();
    }
    
    /**
     * Build a basic {@link NamedKeyInfoGeneratorManager} for use when generating an
     * {@link org.opensaml.xmlsec.signature.Signature}.
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
    public static NamedKeyInfoGeneratorManager buildBasicKeyInfoGeneratorManager() {
        NamedKeyInfoGeneratorManager namedManager = new NamedKeyInfoGeneratorManager();
        
        namedManager.setUseDefaultManager(true);
        KeyInfoGeneratorManager defaultManager = namedManager.getDefaultManager();
        
        // Generator for basic Credentials
        BasicKeyInfoGeneratorFactory basicFactory = new BasicKeyInfoGeneratorFactory();
        basicFactory.setEmitPublicKeyValue(true);
        basicFactory.setEmitPublicDEREncodedKeyValue(true);
        basicFactory.setEmitKeyNames(true);
        
        // Generator for X509Credentials
        X509KeyInfoGeneratorFactory x509Factory = new X509KeyInfoGeneratorFactory();
        x509Factory.setEmitEntityCertificate(true);
        
        defaultManager.registerFactory(basicFactory);
        defaultManager.registerFactory(x509Factory);
        
        return namedManager;
    }

}