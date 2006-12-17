/*
 * Copyright [2006] [University Corporation for Advanced Internet Development, Inc.]
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

import java.security.GeneralSecurityException;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathValidator;
import java.security.cert.CertStore;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.security.auth.x500.X500Principal;

import javolution.util.FastList;
import javolution.util.FastSet;

import org.apache.log4j.Logger;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.util.DatatypeHelper;

/**
 * Base class for trust engines that validate tokens using PKIX validation.
 * 
 * Name checking is enabled by default. If it is enabled during a validation then the trust engine will verify that the
 * credential's entity certificate's CN component of the subject DN, DNS subjectAltName, or URI subjectAltName match
 * either the peer name give by the {@link KeyInfoSource} or the key names resolved by the {@link X509KeyInfoResolver}.
 * This check provides reasonable assurance that even if there are no keys or certificates within the key info source
 * that the credential is really the credential that is intended and should thus be validated.
 * 
 * @param <TokenType> token to be validated
 * @param <KeyInfoResolverType> source of key information
 */
public abstract class BasePKIXTrustEngine<TokenType, KeyInfoResolverType extends X509KeyInfoResolver> extends
        BaseTrustEngine<TokenType, KeyInfoResolverType> implements PKIXTrustEngine<TokenType, KeyInfoResolverType> {
    
    /** Class logger. */
    private static Logger log = Logger.getLogger(BasePKIXTrustEngine.class);

    /** Whether to match the credential's entity cert name against the peer and key name. */
    private boolean checkName;
    
    /** Information necessary for performing PKIX validation. */
    private PKIXValidationInformation pkixInfo;
    
    /**
     * Gets whether to check the credential's entity certificate name against the peer's name and key names.
     * 
     * @return whether to check the credential's entity certificate name against the peer's name and key names
     */
    public boolean checkName() {
        return checkName;
    }

    /**
     * Sets whether to check the credential's entity certificate name against the peer's name and key names.
     * 
     * @param check whether to check the credential's entity certificate name against the peer's name and key names
     */
    public void setCheckName(boolean check) {
        checkName = check;
    }
    
    /** {@inheritDoc} */
    public PKIXValidationInformation getValidationInformation() {
        return pkixInfo;
    }

    /** {@inheritDoc} */
    public void setValidationInformation(PKIXValidationInformation validationInformation) {
        pkixInfo = validationInformation;
    }
     
    /**
     * Gets the default PKIX information set through {@link #setValidationInformation(PKIXValidationInformation)}.
     * 
     * Override this method in order to provide a set of validation information based on the peer.
     * 
     * @param peer ID of the peer to fetch the validation information for
     * 
     * @return the validation information, must not be null
     */
    protected Iterable<PKIXValidationInformation> getPKIXValidationInformation(String peer){
        ArrayList<PKIXValidationInformation> info = new ArrayList<PKIXValidationInformation>();
        info.add(getValidationInformation());
        return info;
    }

    /**
     * Checks that either the ID for the entity with the given role or the key names for the given role match the
     * subject or subject alternate names of the entity certificate.
     * 
     * @param entityCredential the credential for the entity to validate
     * @param keyInfoSource source of key information and peer name
     * @param keyResolver resolver used to resolve key names
     * 
     * @return true the name check succeeds, false if not
     */
    protected boolean checkEntityNames(X509EntityCredential entityCredential, KeyInfoSource keyInfoSource,
            X509KeyInfoResolver keyResolver) {
        X509Certificate entityCerficate = entityCredential.getEntityCertificate();

        // First, check to see if the entity's ID is in the certificate
        if (matchId(keyInfoSource.getName(), entityCerficate)) {
            return true;
        }
        
        // Next check to see if the entity's ID matches any of the key names
        List<String> keyNames;
        for (KeyInfo keyInfo : keyInfoSource) {
            keyNames = keyResolver.resolveKeyNames(keyInfo);
            if (keyNames != null) {
                for (String keyName : keyNames) {
                    if (matchKeyName(keyName, entityCerficate)) {
                        return true;
                    }
                }
            }
        }

        log.error("Entity credentials are not valid for the given role descriptor");
        return false;
    }

    /**
     * Checks to see if the given ID matches either the first CN component on the certificate's subject DN or any of the
     * certificate's DNS or URI subject alternate names. Matching is case-insensitive.
     * 
     * @param id the entity ID to match
     * @param certificate the certificate to match against
     * 
     * @return true if the entity ID matches the subject DN or subject alternate names of the certificate
     */
    protected boolean matchId(String id, X509Certificate certificate) {
        if (log.isDebugEnabled()) {
            log.debug("Attempting to match peer ID " + id + " with certificate subject information");
        }
        if (DatatypeHelper.isEmpty(id)) {
            log.error("Peer ID was empty or null");
            return false;
        }

        if (certificate == null) {
            log.error("X509 certificate null");
            return false;
        }

        String loweredEntityId = id.trim().toLowerCase();

        if (log.isDebugEnabled()) {
            log.debug("Attempting to match entity ID " + id
                    + " with the first CN component of the certificate's subject DN");
        }
        String firstCNComponent = X509Util.getCommonNames(certificate.getSubjectX500Principal()).get(0);
        if (!DatatypeHelper.isEmpty(firstCNComponent)) {
            if (loweredEntityId.equals(firstCNComponent.toLowerCase())) {
                if (log.isDebugEnabled()) {
                    log.debug("Entity ID matched the first CN component of the certificate's subject DN");
                }
                return true;
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("Attempting to match entity ID with certificate's DNS and URI subject alt names");
        }

        Integer[] altNameTypes = { X509Util.DNS_ALT_NAME, X509Util.URI_ALT_NAME };
        for (Object altName : X509Util.getAltNames(certificate, altNameTypes)) {
            if (altName.equals(id)) {
                if (log.isDebugEnabled()) {
                    log.debug("ID matched against subject alt name");
                }
                return true;
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("Unable to match ID against subject alt names");
        }
        return false;
    }

    /**
     * Checks if the given key name matches the certificate's Subject DN, the first CN component of the subject DN, or
     * any DNS or URI subject alt names.
     * 
     * @param keyName the key name to check
     * @param certificate the certificate to check the key name against
     * 
     * @return true if the key name matches, false if not
     */
    protected boolean matchKeyName(String keyName, X509Certificate certificate) {
        if (log.isDebugEnabled()) {
            log.debug("Attempting to match key name " + keyName + " against certificate information");
        }

        if (DatatypeHelper.isEmpty(keyName)) {
            log.error("Key name is null or empty");
            return false;
        }

        if (certificate == null) {
            log.error("Certificate is null");
            return false;
        }

        if (log.isDebugEnabled()) {
            log.debug("Attempting to match key name against certificate's subject DN");
        }
        X500Principal subjectPrincipal = certificate.getSubjectX500Principal();
        try {
            if (subjectPrincipal.equals(new X500Principal(keyName))) {
                if (log.isDebugEnabled()) {
                    log.debug("Key name matched certificate's subject DN");
                }
                return true;
            }
        } catch (IllegalArgumentException e) {
            // ingore this exception, this occurs if the key name
            // is not a valid DN, which is okay
        }

        return matchId(keyName, certificate);
    }

    /**
     * Attempts to validate the given entity credential using the PKIX information provided.
     * 
     * @param entityCredential the entity credential to validate
     * @param validationInfo the PKIX information to validate the credential against
     * 
     * @return true if the given credential is valid, false if not
     */
    @SuppressWarnings("unchecked")
    protected boolean pkixValidate(X509EntityCredential entityCredential, PKIXValidationInformation validationInfo) {
        Set<X509Certificate> trustChain = validationInfo.getTrustChain();

        if (trustChain == null || trustChain.size() < 1) {
            if (log.isDebugEnabled()) {
                log.debug("Unable to validate signature, no trust anchors found in the PKIX validation information");
            }
            return false;
        }

        if (log.isDebugEnabled()) {
            log.debug("Attempting PKIX path validation on entity credential");
        }
        try {
            if (log.isDebugEnabled()) {
                log.debug("Constructring trust anchors");
            }
            Set<TrustAnchor> trustAnchors = new FastSet<TrustAnchor>();
            for (X509Certificate cert : trustChain) {
                trustAnchors.add(new TrustAnchor(cert, null));
            }

            X509CertSelector selector = new X509CertSelector();
            selector.setCertificate(entityCredential.getEntityCertificate());

            if (log.isDebugEnabled()) {
                log.debug("Adding trust anchors to PKIX validator parameters");
            }
            PKIXBuilderParameters params = new PKIXBuilderParameters(trustAnchors, selector);

            if (log.isDebugEnabled()) {
                log.debug("Setting verification depth to " + validationInfo.getVerificationDepth());
            }
            params.setMaxPathLength(validationInfo.getVerificationDepth());

            if (log.isDebugEnabled()) {
                log.debug("Adding entity ceritifcate chain to certificate store");
            }
            List storeMaterial = new FastList(entityCredential.getEntityCertificateChain());

            Set<X509CRL> crls = validationInfo.getCRLs();
            if (crls.size() > 0) {
                if (log.isDebugEnabled()) {
                    log.debug(crls.size()
                            + " CRLs available, enabling CRL support and adding CRLs to certificate store");
                }
                storeMaterial.addAll(crls);
                params.setRevocationEnabled(true);
            } else {
                params.setRevocationEnabled(false);
            }

            if (log.isDebugEnabled()) {
                log.debug("Adding certificate store to PKIX validator parameters");
            }
            CertStore store = CertStore.getInstance("Collection", new CollectionCertStoreParameters(storeMaterial));
            List<CertStore> stores = new FastList<CertStore>();
            stores.add(store);
            params.setCertStores(stores);

            if (log.isDebugEnabled()) {
                log.debug("Building certificate validation path");
            }
            CertPathBuilder builder = CertPathBuilder.getInstance("PKIX");
            PKIXCertPathBuilderResult buildResult = (PKIXCertPathBuilderResult) builder.build(params);
            CertPath certificatePath = buildResult.getCertPath();

            if (log.isDebugEnabled()) {
                log.debug("Validating given entity credentials using built PKIX validator");
            }
            CertPathValidator validator = CertPathValidator.getInstance("PKIX");
            validator.validate(certificatePath, params);

            if (log.isDebugEnabled()) {
                log.debug("PKIX validation of credentials for " + entityCredential.getEntityID() + " successful");
            }
            return true;

        } catch (GeneralSecurityException e) {
            if (log.isDebugEnabled()) {
                log.debug("PKIX validation of credentials for entity " + entityCredential + " failed.", e);
            }

            return false;
        }
    }
}