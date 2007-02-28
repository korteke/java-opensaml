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

package org.opensaml.xml.security.x509;

import java.security.GeneralSecurityException;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javolution.util.FastSet;

import org.apache.log4j.Logger;

/**
 * Base class for trust engines that validate tokens using PKIX validation.
 * 
 * Name checking is enabled by default. If it is enabled during a validation then the trust engine will verify that the
 * untrusted credential's entity certificate's CN component of the subject DN, DNS subjectAltName, or URI subjectAltName
 * match either the trusted credential's entity ID or key names. If key names are present and there is a match, or if
 * there are no key names, the trust engine will procceed with the more costly PKIX validation, if there is no match the
 * engine will assume the untrusted credential is not a valid credential for validation against the trusted credential
 * information and will abort the validation.
 * 
 * @param <TokenType> token to be validated
 * @param <TrustedCredentialType> trusted credential information the given token will be checked against
 */
public abstract class BasePKIXTrustEngine<TokenType, TrustedCredentialType extends X509Credential> implements
        PKIXTrustEngine<TokenType, TrustedCredentialType> {

    /** Class logger. */
    private static Logger log = Logger.getLogger(BasePKIXTrustEngine.class);

    /** Whether to match the credential's entity cert name against the peer and key name. */
    private boolean nameChecking = true;

    /**
     * Gets whether to check the credential's entity certificate name against the peer's name and key names.
     * 
     * @return whether to check the credential's entity certificate name against the peer's name and key names
     */
    public boolean isNameChecking() {
        return nameChecking;
    }

    /**
     * Sets whether to check the credential's entity certificate name against the peer's name and key names.
     * 
     * @param isNameChecking whether to check the credential's entity certificate name against the peer's name and key
     *            names
     */
    public void setNameChecking(boolean isNameChecking) {
        nameChecking = isNameChecking;
    }

    /**
     * Checks that either the ID for the entity with the given role or the key names for the given role match the
     * subject or subject alternate names of the entity certificate.
     * 
     * @param untrustedCredential the credential for the entity to validate
     * @param trustedCredential credential against which the entities names will be matched
     * 
     * @return true the name check succeeds, false if not
     */
    @SuppressWarnings("unchecked")
    protected boolean checkName(X509Credential untrustedCredential, X509Credential trustedCredential) {
        if (!nameChecking) {
            return true;
        }

        if (log.isDebugEnabled()) {
            log.debug("Checking untrusted " + untrustedCredential.getEntityId()
                    + " credential against trusted entity ID and key names");
        }
        Integer[] altNameTypes = { X509Util.DNS_ALT_NAME, X509Util.URI_ALT_NAME };
        Collection<String> possibleKeyNames = new HashSet<String>(X509Util.getSubjectNames(untrustedCredential
                .getEntityCertificate(), altNameTypes));

        HashSet<String> trustedNames = new HashSet<String>(trustedCredential.getKeyNames());
        trustedNames.add(trustedCredential.getEntityId());

        if (Collections.disjoint(possibleKeyNames, trustedNames)) {
            log
                    .error("Untrusted credential for entity " + untrustedCredential.getEntityId()
                            + " failed name checking.");
            return false;
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Untrusted credential for entity " + untrustedCredential.getEntityId()
                        + " passed name checking.");
            }
            return true;
        }
    }

    /**
     * Attempts to validate the given entity credential using the PKIX information provided.
     * 
     * @param untrustedCredential the entity credential to validate
     * @param validationInfo the PKIX information to validate the credential against
     * 
     * @return true if the given credential is valid, false if not
     * 
     * @throws SecurityException thrown if there is a problem attempting the validation
     */
    @SuppressWarnings("unchecked")
    protected boolean pkixValidate(PKIXValidationInformation validationInfo, X509Credential untrustedCredential)
            throws SecurityException {
        if (log.isDebugEnabled()) {
            log.debug("Attempting PKIX path validation on untrusted credential " + untrustedCredential.getEntityId());
        }

        try {
            PKIXBuilderParameters params = getPKIXBuilderParameters(validationInfo, untrustedCredential);

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
                log.debug("PKIX validation of credentials for " + untrustedCredential.getEntityId() + " successful");
            }
            return true;

        } catch (CertPathValidatorException e) {
            log.error("PKIX validation of credentials for entity " + untrustedCredential + " failed.", e);
            return false;
        } catch (GeneralSecurityException e) {
            log.error("Unable to create PKIX validator", e);
            throw new SecurityException("Unable to create PKIX validator", e);
        }
    }

    /**
     * Creates the set of PKIX builder parameters to use when building the cert path builder.
     * 
     * @param validationInfo PKIX validation information
     * @param untrustedCredential credential to be validated
     * 
     * @return PKIX builder params
     * 
     * @throws GeneralSecurityException thrown if the parameters can not be created
     */
    protected PKIXBuilderParameters getPKIXBuilderParameters(PKIXValidationInformation validationInfo,
            X509Credential untrustedCredential) throws GeneralSecurityException {
        Set<TrustAnchor> trustAnchors = getTrustAnchors(validationInfo);
        if (trustAnchors.size() < 1) {
            throw new GeneralSecurityException(
                    "Unable to validate signature, no trust anchors found in the PKIX validation information");
        }

        X509CertSelector selector = new X509CertSelector();
        selector.setCertificate(untrustedCredential.getEntityCertificate());

        if (log.isDebugEnabled()) {
            log.debug("Adding trust anchors to PKIX validator parameters");
        }
        PKIXBuilderParameters params = new PKIXBuilderParameters(trustAnchors, selector);

        if (log.isDebugEnabled()) {
            log.debug("Setting verification depth to " + validationInfo.getVerificationDepth());
        }
        params.setMaxPathLength(validationInfo.getVerificationDepth());

        params.addCertStore(buildCertStore(validationInfo, untrustedCredential));

        if (validationInfo.getCRLs() == null || validationInfo.getCRLs().size() > 0) {
            if (log.isDebugEnabled()) {
                log.debug("No CRLs available in PKIX validation information, disable revocation checking");
            }
            params.setRevocationEnabled(false);
        }

        return params;
    }

    /**
     * Creates the collection of trust anchors to use during validation.
     * 
     * @param validationInfo PKIX validation information
     * 
     * @return trust anchors to use during validation
     */
    protected Set<TrustAnchor> getTrustAnchors(PKIXValidationInformation validationInfo) {
        Collection<X509Certificate> trustChain = validationInfo.getTrustChain();

        if (log.isDebugEnabled()) {
            log.debug("Constructring trust anchors");
        }
        Set<TrustAnchor> trustAnchors = new FastSet<TrustAnchor>();
        for (X509Certificate cert : trustChain) {
            trustAnchors.add(new TrustAnchor(cert, null));
        }

        return trustAnchors;
    }

    /**
     * Creates the certificate store that will be used during validation.
     * 
     * @param validationInfo PKIX validation information
     * @param untrustedCredential credential to be validated
     * 
     * @return certificate store used during validation
     * 
     * @throws GeneralSecurityException thrown if the certificate store can not be created from the cert and CRL
     *             material
     */
    protected CertStore buildCertStore(PKIXValidationInformation validationInfo, X509Credential untrustedCredential)
            throws GeneralSecurityException {
        if (log.isDebugEnabled()) {
            log.debug("Creating cert store to use during path validation");
            log.debug("Adding entity ceritifcate chain to certificate store");
        }
        List<Object> storeMaterial = new ArrayList<Object>(untrustedCredential.getEntityCertificateChain());
        storeMaterial.addAll(validationInfo.getTrustChain());
        return CertStore.getInstance("Collection", new CollectionCertStoreParameters(storeMaterial));
    }
}