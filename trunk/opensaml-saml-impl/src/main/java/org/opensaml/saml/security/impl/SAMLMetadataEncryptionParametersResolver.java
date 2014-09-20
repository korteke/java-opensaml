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
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.collection.Pair;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.metadata.EncryptionMethod;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.EncryptionParameters;
import org.opensaml.xmlsec.KeyTransportAlgorithmPredicate;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.encryption.MGF;
import org.opensaml.xmlsec.encryption.OAEPparams;
import org.opensaml.xmlsec.encryption.support.RSAOAEPParameters;
import org.opensaml.xmlsec.impl.BasicEncryptionParametersResolver;
import org.opensaml.xmlsec.signature.DigestMethod;
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
    
    /** Flag indicating whether the resolver should attempt to complete a partially-resolved RSAOAEPParameters instance
     * by delegating to the superclass local config resolution process. */
    private boolean completePartialRSAOAEPParametersFromConfig;
    
    /**
     * Constructor.
     *
     * @param resolver the metadata credential resolver instance to use to resolve encryption credentials
     */
    public SAMLMetadataEncryptionParametersResolver(@Nonnull final MetadataCredentialResolver resolver) {
        credentialResolver = Constraint.isNotNull(resolver, "MetadataCredentialResoler may not be null");
    }
    
    /**
     * Determine whether to complete partially resolved RSA OAEP parameters by delegating to local configuration.
     * 
     * @return true if should complete partial parameters instances, false otherwise
     */
    public boolean isCompletePartialRSAOAEPParametersFromConfig() {
        return completePartialRSAOAEPParametersFromConfig;
    }

    /**
     * Set whether to complete partially resolved RSA OAEP parameters by delegating to local configuration.
     * 
     * @param flag true if should complete partial parameters instances, false otherwise
     */
    public void setCompletePartialRSAOAEPParametersFromConfig(boolean flag) {
        completePartialRSAOAEPParametersFromConfig = flag;
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
                
                Pair<String,EncryptionMethod> dataEncryptionAlgorithmAndMethod = resolveDataEncryptionAlgorithm(
                        criteria, whitelistBlacklistPredicate, metadataCredContext);
                
                Pair<String,EncryptionMethod> keyTransportAlgorithmAndMethod = resolveKeyTransportAlgorithm(
                        keyTransportCredential, criteria, whitelistBlacklistPredicate, 
                        dataEncryptionAlgorithmAndMethod.getFirst(), metadataCredContext);
                if (keyTransportAlgorithmAndMethod.getFirst() == null) {
                    log.debug("Unable to resolve key transport algorithm for credential with key type '{}', " 
                            + "considering other credentials", 
                            CredentialSupport.extractEncryptionKey(keyTransportCredential).getAlgorithm());
                    continue;
                }
                
                params.setKeyTransportEncryptionCredential(keyTransportCredential);
                params.setKeyTransportEncryptionAlgorithm(keyTransportAlgorithmAndMethod.getFirst());
                params.setDataEncryptionAlgorithm(dataEncryptionAlgorithmAndMethod.getFirst());
                
                resolveAndPopulateRSAOAEPParams(params, criteria, whitelistBlacklistPredicate, 
                        keyTransportAlgorithmAndMethod.getSecond());
                
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
     * Resolve and populate an instance of {@link RSAOAEPParameters}, if appropriate for the selected
     * key transport encryption algorithm.
     * 
     * <p>
     * This method itself resolves the parameters data from the metadata {@link EncryptionMethod}.  If
     * this results in a non-complete RSAOAEPParameters instance and if 
     * {@link #isCompletePartialRSAOAEPParametersFromConfig()} evaluates true, 
     * then the resolver will delegate to the local config resolution process via the superclass
     * for completion of any missing parameters 
     * (see {@link #resolveAndPopulateRSAOAEPParams(EncryptionParameters, CriteriaSet, Predicate)}).
     * </p>
     * 
     * @param params the current encryption parameters instance being resolved
     * @param criteria  the criteria instance being evaluated
     * @param whitelistBlacklistPredicate the whitelist/blacklist predicate with which to evaluate the 
     *          candidate data encryption and key transport algorithm URIs
     * @param encryptionMethod the method encryption method that was resolved along with the key transport 
     *          encryption algorithm URI, if any.  May be null.
     */
     protected void resolveAndPopulateRSAOAEPParams(@Nonnull final EncryptionParameters params, 
             @Nonnull final CriteriaSet criteria, 
             @Nonnull final Predicate<String> whitelistBlacklistPredicate, 
             @Nullable final EncryptionMethod encryptionMethod) {
         
         if (!AlgorithmSupport.isRSAOAEP(params.getKeyTransportEncryptionAlgorithm())) {
             return;
         }
         
         if (encryptionMethod == null) {
             super.resolveAndPopulateRSAOAEPParams(params, criteria, whitelistBlacklistPredicate);
             return;
         }
         
         if (params.getRSAOAEPParameters() == null) {
             params.setRSAOAEPParameters(new RSAOAEPParameters());
         }
         
         populateRSAOAEPParamsFromEncryptionMethod(params.getRSAOAEPParameters(), encryptionMethod, 
                 whitelistBlacklistPredicate);
        
         if (params.getRSAOAEPParameters().isComplete()) {
             return;
         } else if (params.getRSAOAEPParameters().isEmpty()) {
             super.resolveAndPopulateRSAOAEPParams(params, criteria, whitelistBlacklistPredicate);
         } else {
             if (isCompletePartialRSAOAEPParametersFromConfig()) {
                 super.resolveAndPopulateRSAOAEPParams(params, criteria, whitelistBlacklistPredicate);
             }
         }
    }

    /**
     * Extract {@link DigestMethod}, {@link MGF} and {@link OAEPparams} data present on the supplied
     * instance of {@link EncryptionMethod} and populate it on the supplied instance of of 
     * {@link RSAOAEPParameters}.
     * 
     * <p>
     * Whitelist/blacklist evaluation is applied to the digest method and MGF algorithm URIs.
     * </p>
     * 
     * @param params the existing RSAOAEPParameters instance being populated
     * @param encryptionMethod the method encryption method that was resolved along with the key transport 
     *          encryption algorithm URI, if any.  May be null.
     * @param whitelistBlacklistPredicate the whitelist/blacklist predicate with which to evaluate the 
     *          candidate data encryption and key transport algorithm URIs
     */
    protected void populateRSAOAEPParamsFromEncryptionMethod(@Nonnull final RSAOAEPParameters params, 
            @Nonnull final EncryptionMethod encryptionMethod, 
            @Nonnull final Predicate<String> whitelistBlacklistPredicate) {
        
        Predicate<String> algoSupportPredicate = getAlgorithmRuntimeSupportedPredicate();
        
        List<XMLObject> digestMethods = encryptionMethod.getUnknownXMLObjects(DigestMethod.DEFAULT_ELEMENT_NAME);
        if (digestMethods.size() > 0) {
            DigestMethod digestMethod = (DigestMethod) digestMethods.get(0);
            String digestAlgorithm = StringSupport.trimOrNull(digestMethod.getAlgorithm());
            if (digestAlgorithm != null && whitelistBlacklistPredicate.apply(digestAlgorithm)
                    && algoSupportPredicate.apply(digestAlgorithm)) {
                params.setDigestMethod(digestAlgorithm);
            }
        }
        
        List<XMLObject> mgfs = encryptionMethod.getUnknownXMLObjects(MGF.DEFAULT_ELEMENT_NAME);
        if (mgfs.size() > 0) {
            MGF mgf = (MGF) mgfs.get(0);
            String mgfAlgorithm = StringSupport.trimOrNull(mgf.getAlgorithm());
            if (mgfAlgorithm != null && whitelistBlacklistPredicate.apply(mgfAlgorithm)) {
                params.setMaskGenerationFunction(mgfAlgorithm);
            }
        }
        
        List<XMLObject> oaepParamsList = encryptionMethod.getUnknownXMLObjects(OAEPparams.DEFAULT_ELEMENT_NAME);
        if (oaepParamsList.size() > 0) {
            OAEPparams oaepParams = (OAEPparams) oaepParamsList.get(0);
            String value = StringSupport.trimOrNull(oaepParams.getValue());
            if (value != null) {
                params.setOAEPparams(value);
            }
        }
        
    }

    /**
     * Determine the key transport algorithm URI to use with the specified credential, also returning the associated
     * {@link EncryptionMethod} from metadata if relevant.
     * 
     * <p>
     * Any algorithms specified in metadata via the passed {@link SAMLMDCredentialContext} are considered first, 
     * followed by locally configured algorithms.
     * </p>
     * 
     * @param keyTransportCredential the key transport credential to evaluate
     * @param criteria  the criteria instance being evaluated
     * @param whitelistBlacklistPredicate the whitelist/blacklist predicate with which to evaluate the 
     *          candidate data encryption and key transport algorithm URIs
     * @param dataEncryptionAlgorithm the optional data encryption algorithm URI to consider
     * @param metadataCredContext the credential context extracted from metadata
     * @return the selected algorithm URI and the associated encryption method from metadata, if any. 
     */
    @Nonnull protected Pair<String, EncryptionMethod> resolveKeyTransportAlgorithm(
            @Nonnull final Credential keyTransportCredential, 
            @Nonnull final CriteriaSet criteria, @Nonnull final Predicate<String> whitelistBlacklistPredicate,
            @Nullable final String dataEncryptionAlgorithm,
            @Nullable final SAMLMDCredentialContext metadataCredContext) {
        
        if (metadataCredContext != null) {
            KeyTransportAlgorithmPredicate keyTransportPredicate = resolveKeyTransportAlgorithmPredicate(criteria);
            for (EncryptionMethod encryptionMethod : metadataCredContext.getEncryptionMethods()) {
                String algorithm = encryptionMethod.getAlgorithm();
                log.trace("Evaluating SAML metadata EncryptionMethod algorithm for key transport: {}", algorithm);
                if (isKeyTransportAlgorithm(algorithm) && whitelistBlacklistPredicate.apply(algorithm) 
                        && getAlgorithmRuntimeSupportedPredicate().apply(algorithm)
                        && credentialSupportsEncryptionMethod(keyTransportCredential, encryptionMethod)) {
                    
                    if (keyTransportPredicate != null) {
                        if (keyTransportPredicate.apply(new KeyTransportAlgorithmPredicate.SelectionInput(
                                algorithm, dataEncryptionAlgorithm, keyTransportCredential))) {
                            log.debug("Resolved key transport algorithm URI from SAML metadata EncryptionMethod: {}",
                                    algorithm);
                            return new Pair<>(algorithm, encryptionMethod);
                        }
                    } else {
                        log.debug("Resolved key transport algorithm URI from SAML metadata EncryptionMethod: {}",
                                algorithm);
                        return new Pair<>(algorithm, encryptionMethod);
                    }
                    
                }
            }
        }
        
        log.debug("Could not resolve key transport algorithm based on SAML metadata, " 
                + "falling back to locally configured algorithms");
        
        return new Pair<>(
                super.resolveKeyTransportAlgorithm(keyTransportCredential, criteria, whitelistBlacklistPredicate, 
                        dataEncryptionAlgorithm),
                null);
    }

    /**
     * Determine the data encryption algorithm URI to use, also returning the associated
     * {@link EncryptionMethod} from metadata if relevant.
     * 
     * <p>
     * Any algorithms specified in metadata via the passed {@link SAMLMDCredentialContext} are considered first, 
     * followed by locally configured algorithms.
     * </p>
     * 
     * @param criteria  the criteria instance being evaluated
     * @param whitelistBlacklistPredicate the whitelist/blacklist predicate with which to evaluate the 
     *          candidate data encryption and key transport algorithm URIs
     * @param metadataCredContext the credential context extracted from metadata
     * @return the selected algorithm URI and the associated encryption method from metadata, if any
     */
    @Nonnull protected Pair<String, EncryptionMethod> resolveDataEncryptionAlgorithm(
            @Nonnull final CriteriaSet criteria, 
            @Nonnull final Predicate<String> whitelistBlacklistPredicate,
            @Nullable final SAMLMDCredentialContext metadataCredContext) {
        
        if (metadataCredContext != null) {
            for (EncryptionMethod encryptionMethod : metadataCredContext.getEncryptionMethods()) {
                String algorithm = encryptionMethod.getAlgorithm();
                log.trace("Evaluating SAML metadata EncryptionMethod algorithm for data encryption: {}", algorithm);
                if (isDataEncryptionAlgorithm(algorithm) && whitelistBlacklistPredicate.apply(algorithm)
                        && getAlgorithmRuntimeSupportedPredicate().apply(algorithm)) {
                    log.debug("Resolved data encryption algorithm URI from SAML metadata EncryptionMethod: {}",
                            algorithm);
                    return new Pair<>(algorithm, encryptionMethod);
                }
            }
        }
        
        log.debug("Could not resolve data encryption algorithm based on SAML metadata, " 
                + "falling back to locally configured algorithms");
        
        return new Pair<>(
                super.resolveDataEncryptionAlgorithm(null, criteria, whitelistBlacklistPredicate),
                null);
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
