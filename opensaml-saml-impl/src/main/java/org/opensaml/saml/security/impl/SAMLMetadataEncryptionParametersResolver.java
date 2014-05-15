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

package org.opensaml.saml.security.impl;

import java.security.Key;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.saml.saml2.metadata.EncryptionMethod;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.EncryptionParameters;
import org.opensaml.xmlsec.impl.BasicEncryptionParametersResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;

/**
 * A specialization of {@link BasicEncryptionParametersResolver} which resolves
 * credentials and algorithm preferences against SAML metadata via a {@link MetadataCredentialResolver}.
 * 
 * <p>
 * In addition to the {@link net.shibboleth.utilities.java.support.resolver.Criterion} inputs documented in 
 * {@link BasicEncryptionParametersResolver}, the inputs and associated modes of operation documented for 
 * {@link MetadataCredentialResolver} are also supported and required.
 * </p>
 * 
 * <p>The {@link CriteriaSet} instance passed to the configured metadata credential resolver will be a copy 
 * of the input criteria set, with the addition of a {@link UsageCriterion} containing the value
 * {@link UsageType#ENCRYPTION}, which will replace any existing usage criterion instance.
 * </p>
 * 
 */
public class SAMLMetadataEncryptionParametersResolver extends BasicEncryptionParametersResolver {
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(SAMLMetadataEncryptionParametersResolver.class);
    
    /** Metadata credential resolver. */
    private MetadataCredentialResolver credentialResolver;
    
    /**
     * Constructor.
     *
     * @param resolver the metadata credential resolver instance to use to resolve encryption credentials
     */
    public SAMLMetadataEncryptionParametersResolver(@Nonnull final MetadataCredentialResolver resolver) {
        credentialResolver = Constraint.isNotNull(resolver, "MetadataCredentialResoler may not be null");
    }
    
    /**
     * Get the metadata credential resolver instance to use to resolve encryption credentials.
     * 
     * @return the configured metadata credential resolver instance
     */
    @Nonnull protected MetadataCredentialResolver getMetadataCredentialResolver() {
        return credentialResolver;
    }

    /** {@inheritDoc} */
    protected void resolveAndPopulateCredentialsAndAlgorithms(@Nonnull final EncryptionParameters params,
            @Nonnull final CriteriaSet criteria, @Nonnull final Predicate<String> whitelistBlacklistPredicate) {
        
        // Create a new CriteriaSet for input to the metadata credential resolver, explicitly 
        // setting/forcing an encryption usage criterion.
        CriteriaSet mdCredResolverCriteria = new CriteriaSet();
        mdCredResolverCriteria.addAll(criteria);
        mdCredResolverCriteria.add(new UsageCriterion(UsageType.ENCRYPTION), true);
        
        // Note: Here we assume that we will only ever resolve a key transport credential from metadata.
        // Even if it's a symmetric key credential (via a key agreement protocol, or resolved from a KeyName, etc),
        // it ought to be used for symmetric key wrap, not direct data encryption.
        try {
            for (Credential keyTransportCredential : getMetadataCredentialResolver().resolve(mdCredResolverCriteria)) {
                
                if (log.isTraceEnabled()) {
                    Key key = CredentialSupport.extractEncryptionKey(keyTransportCredential);
                    log.trace("Evaluating key transport encryption credential from SAML metadata of type: {}", 
                            key != null ? key.getAlgorithm() : "n/a");
                }
                
                SAMLMDCredentialContext metadataCredContext = 
                        keyTransportCredential.getCredentialContextSet().get(SAMLMDCredentialContext.class);
                
                String dataEncryptionAlgorithm = resolveDataEncryptionAlgorithm(criteria, whitelistBlacklistPredicate, 
                        metadataCredContext);
                
                String keyTransportAlgorithm = resolveKeyTransportAlgorithm(keyTransportCredential,
                        criteria, whitelistBlacklistPredicate, dataEncryptionAlgorithm, metadataCredContext);
                if (keyTransportAlgorithm == null) {
                    log.debug("Unable to resolve key transport algorithm for credential with key type '{}', " 
                            + "considering other credentials", 
                            CredentialSupport.extractEncryptionKey(keyTransportCredential).getAlgorithm());
                    continue;
                }
                
                params.setKeyTransportEncryptionCredential(keyTransportCredential);
                params.setKeyTransportEncryptionAlgorithmURI(keyTransportAlgorithm);
                params.setDataEncryptionAlgorithmURI(dataEncryptionAlgorithm);
                
                processDataEncryptionCredentialAutoGeneration(params);
                
                return;
            }
        } catch (ResolverException e) {
            log.warn("Problem resolving credentials from metadata, falling back to local configuration", e);
        }
        
        log.debug("Could not resolve encryption parameters based on SAML metadata, " 
                + "falling back to locally configured credentials and algorithms");
        
        super.resolveAndPopulateCredentialsAndAlgorithms(params, criteria, whitelistBlacklistPredicate);
    }

    /**
     * Determine the key transport algorithm URI to use with the specified credential.
     * Any algorithms specified in metadata via 
     * the passed {@link SAMLMDCredentialContext} are considered first, 
     * followed by locally configured algorithms.
     * 
     * @param keyTransportCredential the key transport credential to evaluate
     * @param criteria  the criteria instance being evaluated
     * @param whitelistBlacklistPredicate the whitelist/blacklist predicate with which to evaluate the 
     *          candidate data encryption and key transport algorithm URIs
     * @param dataEncryptionAlgorithm the optional data encryption algorithm URI to consider
     * @param metadataCredContext the credential context extracted from metadata
     * @return the selected algorithm URI
     */
    @Nullable protected String resolveKeyTransportAlgorithm(@Nonnull final Credential keyTransportCredential, 
            @Nonnull final CriteriaSet criteria, @Nonnull final Predicate<String> whitelistBlacklistPredicate,
            @Nullable final String dataEncryptionAlgorithm,
            @Nullable final SAMLMDCredentialContext metadataCredContext) {
        
        if (metadataCredContext != null) {
            for (EncryptionMethod encryptionMethod : metadataCredContext.getEncryptionMethods()) {
                String algorithm = encryptionMethod.getAlgorithm();
                log.trace("Evaluating SAML metadata EncryptionMethod algorithm for key transport: {}", algorithm);
                if (isKeyTransportAlgorithm(algorithm) && whitelistBlacklistPredicate.apply(algorithm) 
                        && credentialSupportsEncryptionMethod(keyTransportCredential, encryptionMethod)) {
                    log.debug("Resolved key transport algorithm URI from SAML metadata EncryptionMethod: {}",
                            algorithm);
                    return algorithm;
                }
            }
        }
        
        log.debug("Could not resolve key transport algorithm based on SAML metadata, " 
                + "falling back to locally configured algorithms");
        
        return super.resolveKeyTransportAlgorithm(keyTransportCredential, criteria, whitelistBlacklistPredicate,
                dataEncryptionAlgorithm);
    }

    /**
     * Determine the data encryption algorithm URI to use. Any algorithms specified in metadata via 
     * the passed {@link SAMLMDCredentialContext} are considered first, 
     * followed by locally configured algorithms.
     * 
     * @param criteria  the criteria instance being evaluated
     * @param whitelistBlacklistPredicate the whitelist/blacklist predicate with which to evaluate the 
     *          candidate data encryption and key transport algorithm URIs
     * @param metadataCredContext the credential context extracted from metadata
     * @return the selected algorithm URI
     */
    @Nullable protected String resolveDataEncryptionAlgorithm(@Nonnull final CriteriaSet criteria, 
            @Nonnull final Predicate<String> whitelistBlacklistPredicate,
            @Nullable final SAMLMDCredentialContext metadataCredContext) {
        
        if (metadataCredContext != null) {
            for (EncryptionMethod encryptionMethod : metadataCredContext.getEncryptionMethods()) {
                String algorithm = encryptionMethod.getAlgorithm();
                log.trace("Evaluating SAML metadata EncryptionMethod algorithm for data encryption: {}", algorithm);
                if (isDataEncryptionAlgorithm(algorithm) && whitelistBlacklistPredicate.apply(algorithm)) {
                    log.debug("Resolved data encryption algorithm URI from SAML metadata EncryptionMethod: {}",
                            algorithm);
                    return algorithm;
                }
            }
        }
        
        log.debug("Could not resolve data encryption algorithm based on SAML metadata, " 
                + "falling back to locally configured algorithms");
        
        return super.resolveDataEncryptionAlgorithm(null, criteria, whitelistBlacklistPredicate);
    }

    /**
     * Evaluate whether the specified credential is supported for use with the specified {@link EncryptionMethod}.
     * 
     * @param credential the credential to evaluate
     * @param encryptionMethod the encryption method to evaluate
     * @return true if credential may be used with the supplied encryption method, false otherwise
     */
    protected boolean credentialSupportsEncryptionMethod(@Nonnull final Credential credential, 
            @Nonnull @NotEmpty final EncryptionMethod encryptionMethod) {
        if (!credentialSupportsAlgorithm(credential, encryptionMethod.getAlgorithm())) {
            return false;
        }
        
        if (encryptionMethod.getKeySize() != null && encryptionMethod.getKeySize().getValue() != null) {
            Key encryptionKey = CredentialSupport.extractEncryptionKey(credential);
            if (encryptionKey == null) {
                log.warn("Could not extract encryption key from credential. Failing evaluation");
                return false;
            }
            
            Integer keyLength = KeySupport.getKeyLength(encryptionKey);
            if (keyLength == null) {
                log.warn("Could not determine key length of candidate encryption credential. Failing evaluation");
                return false;
            }
        
            if (! keyLength.equals(encryptionMethod.getKeySize().getValue())) {
                return false;
            }
        }
        
        //TODO anything else?  OAEPParams?
        
        return true;
    }

}
