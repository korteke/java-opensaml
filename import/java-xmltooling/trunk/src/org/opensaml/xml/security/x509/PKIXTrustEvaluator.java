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
import java.security.cert.CRL;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.security.auth.x500.X500Principal;

import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.util.DatatypeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Auxillary trust evaluator that validates X.509 credentials using PKIX validation.
 * 
 * <p>
 * The main entry point for calling implementations is: 
 * {@link #pkixValidate(PKIXValidationInformation, X509Credential)}.
 * Callers should construct an appropriate set of PKIX validation information from a source of trusted information.
 * </p>
 * 
 * <p>
 * Callers may perform optional trusted key name checking using {@link #checkName(X509Credential, Set)}. This name
 * check can be used to verify that at least one of the supported name type values contained within the untrusted
 * credential's entity certificate matches at least one value from the set of trusted key names supplied from trusted
 * credential information. If the supplied set of trusted key names is null or empty, the match is considered
 * successful.
 * </p>
 * 
 * <p>
 * Name checking may be performed in conjunction with PKIX validation by using the overloaded method
 * {@link #pkixValidate(PKIXValidationInformation, Set, X509Credential)}. If there is a match, the trust engine will
 * procceed with the more costly PKIX validation. If there is no match, the engine will assume the untrusted credential
 * is not a valid credential and will abort the validation.
 * </p>
 * 
 * <p>
 * Supported types of certificate-derived names for name checking purposes are:
 * <ol>
 * <li>Subject alternative names.</li>
 * <li>The first (i.e. most specific) common name (CN) from the subject distinguished name.</li>
 * <li>The complete subject distinguished name.</li>
 * </ol>
 * </p>
 * 
 * <p>
 * Name checking is enabled by default for all of the supported name types. The types of subject alternative names to
 * process are specified by using the appropriate constant values defined in {@link X509Util}. By default the following
 * types of subject alternative names are checked: DNS ({@link X509Util#DNS_ALT_NAME}) 
 * and URI ({@link X509Util#URI_ALT_NAME}).
 * </p>
 * 
 * <p>
 * The subject distinguished name from the untrusted certificate is compared to the trusted key names for complete DN
 * matching purposes by parsing each trusted key name into an {@link X500Principal} as returned by the configured
 * instance of {@link X500DNHandler}. The resulting distinguished name is then compared with the certificate subject
 * using {@link X500Principal#equals(Object)}. The default X500DNHandler used is {@link InternalX500DNHandler}.
 * </p>
 * 
 */
public class PKIXTrustEvaluator {
    
    /** Default verify depth. */
    public static final Integer DEFAULT_VERIFY_DEPTH = 1;

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(PKIXTrustEvaluator.class);

    /** Flag as to whether to perform name checking using untrusted credential's subject alt names. */
    private boolean checkSubjectAltNames;

    /** Flag as to whether to perform name checking using untrusted credential's subject DN's common name (CN). */
    private boolean checkSubjectDNCommonName;

    /** Flag as to whether to perform name checking using untrusted credential's subject DN. */
    private boolean checkSubjectDN;

    /** The set of types of subject alternative names to process. */
    private Set<Integer> subjectAltNameTypes;

    /** Responsible for parsing and serializing X.500 names to/from {@link X500Principal} instances. */
    private X500DNHandler x500DNHandler;

    /** Constructor. */
    public PKIXTrustEvaluator() {

        x500DNHandler = new InternalX500DNHandler();
        subjectAltNameTypes = new HashSet<Integer>();

        // Add some defaults
        setCheckSubjectAltNames(true);
        setCheckSubjectDNCommonName(true);
        setCheckSubjectDN(true);
        subjectAltNameTypes.add(X509Util.DNS_ALT_NAME);
        subjectAltNameTypes.add(X509Util.URI_ALT_NAME);
    }

    /**
     * Gets whether any of the supported name type checking is currently enabled.
     * 
     * @return true if any of the supported name type checking categories is currently enabled, false otherwise
     */
    public boolean isNameChecking() {
        return checkSubjectAltNames() || checkSubjectDNCommonName() || checkSubjectDN();
    }

    /**
     * The set of types of subject alternative names to process.
     * 
     * Name types are represented using the constant OID tag name values defined in {@link X509Util}.
     * 
     * 
     * @return the modifiable set of alt name identifiers
     */
    public Set<Integer> getSubjectAltNameTypes() {
        return subjectAltNameTypes;
    }

    /**
     * Gets whether to check the untrusted credential's entity certificate subject alt names against the trusted key
     * name values.
     * 
     * @return whether to check the untrusted credential's entity certificate subject alt names against the trusted key
     *         names
     */
    public boolean checkSubjectAltNames() {
        return checkSubjectAltNames;
    }

    /**
     * Sets whether to check the untrusted credential's entity certificate subject alt names against the trusted key
     * name values.
     * 
     * @param check whether to check the untrusted credential's entity certificate subject alt names against the trusted
     *            key names
     */
    public void setCheckSubjectAltNames(boolean check) {
        checkSubjectAltNames = check;
    }

    /**
     * Gets whether to check the untrusted credential's entity certificate subject DN's common name (CN) against the
     * trusted key name values.
     * 
     * @return whether to check the untrusted credential's entity certificate subject DN's CN against the trusted key
     *         names
     */
    public boolean checkSubjectDNCommonName() {
        return checkSubjectDNCommonName;
    }

    /**
     * Sets whether to check the untrusted credential's entity certificate subject DN's common name (CN) against the
     * trusted key name values.
     * 
     * @param check whether to check the untrusted credential's entity certificate subject DN's CN against the trusted
     *            key names
     */
    public void setCheckSubjectDNCommonName(boolean check) {
        checkSubjectDNCommonName = check;
    }

    /**
     * Gets whether to check the untrusted credential's entity certificate subject DN against the trusted key name
     * values.
     * 
     * @return whether to check the untrusted credential's entity certificate subject DN against the trusted key names
     */
    public boolean checkSubjectDN() {
        return checkSubjectDN;
    }

    /**
     * Sets whether to check the untrusted credential's entity certificate subject DN against the trusted key name
     * values.
     * 
     * @param check whether to check the untrusted credential's entity certificate subject DN against the trusted key
     *            names
     */
    public void setCheckSubjectDN(boolean check) {
        checkSubjectDN = check;
    }

    /**
     * Get the handler which process X.500 distinguished names.
     * 
     * Defaults to {@link InternalX500DNHandler}.
     * 
     * @return returns the X500DNHandler instance
     */
    public X500DNHandler getX500DNHandler() {
        return x500DNHandler;
    }

    /**
     * Set the handler which process X.500 distinguished names.
     * 
     * Defaults to {@link InternalX500DNHandler}.
     * 
     * @param handler the new X500DNHandler instance
     */
    public void setX500DNHandler(X500DNHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("X500DNHandler may not be null");
        }
        x500DNHandler = handler;
    }

    /**
     * Checks whether any of the supported name type values contained within the entity certificate of the specified
     * credential, and for which name checking is configured, matches any of the supplied trusted names.
     * 
     * @param untrustedCredential the credential for the entity to validate
     * @param trustedNames trusted names against which the credential will be evaluated
     * 
     * @return if true the name check succeeds, false if not
     */
    @SuppressWarnings("unchecked")
    public boolean checkName(X509Credential untrustedCredential, Set<String> trustedNames) {
        if (!isNameChecking() || trustedNames == null || trustedNames.isEmpty()) {
            return true;
        }

        if (log.isDebugEnabled()) {
            log.debug("Checking trusted names against untrusted credential: {}",
                    X509Util.getIdentifiersToken(untrustedCredential, x500DNHandler));
            log.debug("Trusted names being evaluated are: {}",
                    trustedNames.toString());
        }        
        return processNameChecks(untrustedCredential, trustedNames);
    }

    /**
     * Process any name checks that are enabled.
     * 
     * @param untrustedCredential the credential for the entity to validate
     * @param trustedNames trusted names against which the credential will be evaluated
     * @return if true the name check succeeds, false if not
     */
    protected boolean processNameChecks(X509Credential untrustedCredential, Set<String> trustedNames) {
        X509Certificate entityCertificate = untrustedCredential.getEntityCertificate();

        if (checkSubjectAltNames()) {
            if (processSubjectAltNames(entityCertificate, trustedNames)) {
                if (log.isDebugEnabled()) {
                    log.debug("Untrusted credential {} passed name check based on subject alt names.",
                            X509Util.getIdentifiersToken(untrustedCredential, x500DNHandler));
                }                
                return true;
            }
        }

        if (checkSubjectDNCommonName()) {
            if (processSubjectDNCommonName(entityCertificate, trustedNames)) {
                if (log.isDebugEnabled()) {
                    log.debug("Untrusted credential {} passed name check based on subject common name.",
                            X509Util.getIdentifiersToken(untrustedCredential, x500DNHandler));
                }                
                return true;
            }
        }

        if (checkSubjectDN()) {
            if (processSubjectDN(entityCertificate, trustedNames)) {
                if (log.isDebugEnabled()) {
                    log.debug("Untrusted credential {} passed name check based on subject DN.",
                            X509Util.getIdentifiersToken(untrustedCredential, x500DNHandler));
                }                
            }
            return true;
        }

        log.error("Untrusted credential failed name check: " 
                + X509Util.getIdentifiersToken(untrustedCredential, x500DNHandler));
        return false;
    }

    /**
     * Process name checking for a certificate subject DN's common name.
     * 
     * @param untrustedCertificate the certificate to process
     * @param trustedNames the set of trusted names
     * 
     * @return true if the subject DN common name matches the set of trusted names, false otherwise
     * 
     */
    protected boolean processSubjectDNCommonName(X509Certificate untrustedCertificate, Set<String> trustedNames) {
        X500Principal subjectPrincipal = untrustedCertificate.getSubjectX500Principal();
        List<String> commonNames = X509Util.getCommonNames(subjectPrincipal);
        if (commonNames == null || commonNames.isEmpty()) {
            return false;
        }
        // TODO We only check the first one returned by X509Util. Maybe we should check all,
        // if there are multiple CN AVA's from the same (first) RDN.
        String commonName = commonNames.get(0);
        log.debug("Extracted common name from certificate: {}", commonName);

        if (DatatypeHelper.isEmpty(commonName)) {
            return false;
        }
        if (trustedNames.contains(commonName)) {
            log.debug("Matched subject DN common name to trusted names: {}", commonName);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Process name checking for the certificate subject DN.
     * 
     * @param untrustedCertificate the certificate to process
     * @param trustedNames the set of trusted names
     * 
     * @return true if the subject DN matches the set of trusted names, false otherwise
     */
    protected boolean processSubjectDN(X509Certificate untrustedCertificate, Set<String> trustedNames) {
        X500Principal subjectPrincipal = untrustedCertificate.getSubjectX500Principal();

        if (log.isDebugEnabled()) {
            log.debug("Extracted X500Principal from certificate: {}", x500DNHandler.getName(subjectPrincipal));
        }        
        for (String trustedName : trustedNames) {
            X500Principal keyNamePrincipal = null;
            try {
                keyNamePrincipal = x500DNHandler.parse(trustedName);
                if (subjectPrincipal.equals(keyNamePrincipal)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Matched subject DN to trusted names: {}", x500DNHandler.getName(subjectPrincipal));
                    }                    
                    return true;
                }
            } catch (IllegalArgumentException e) {
                // Do nothing, probably wasn't a distinguished name.
                // TODO maybe try and match only the "suspected" DN values above
                // - maybe match with regex for '='or something
                continue;
            }
        }
        return false;
    }

    /**
     * Process name checking for the subject alt names within the certificate.
     * 
     * @param untrustedCertificate the certificate to process
     * @param trustedNames the set of trusted names
     * 
     * @return true if one of the subject alt names matches the set of trusted names, false otherwise
     */
    protected boolean processSubjectAltNames(X509Certificate untrustedCertificate, Set<String> trustedNames) {
        Integer[] nameTypes = new Integer[subjectAltNameTypes.size()];
        subjectAltNameTypes.toArray(nameTypes);
        List altNames = X509Util.getAltNames(untrustedCertificate, nameTypes);

        log.debug("Extracted subject alt names from certificate: {}", altNames);

        for (Object altName : altNames) {
            if (trustedNames.contains(altName)) {
                log.debug("Matched subject alt name to trusted names: {}", altName.toString());
                return true;
            }
        }
        return false;
    }

    /**
     * Attempts to validate the given entity credential using the PKIX information provided.
     * 
     * @param untrustedCredential the entity credential to validate
     * @param trustedNames trusted names against which the credential will be evaluated if name checking is enabled
     * @param validationInfo the PKIX information to validate the credential against
     * 
     * @return true if the given credential is valid, false if not
     * 
     * @throws SecurityException thrown if there is a problem attempting the validation
     */
    @SuppressWarnings("unchecked")
    public boolean pkixValidate(PKIXValidationInformation validationInfo, Set<String> trustedNames,
            X509Credential untrustedCredential) throws SecurityException {

        if (!checkName(untrustedCredential, trustedNames)) {
            log.error("Name checking failed, aborting PKIX validation for untrusted credential: "
                    + X509Util.getIdentifiersToken(untrustedCredential, x500DNHandler));
            return false;
        }
        return pkixValidate(validationInfo, untrustedCredential);
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
    public boolean pkixValidate(PKIXValidationInformation validationInfo, X509Credential untrustedCredential)
            throws SecurityException {
        
        if (log.isDebugEnabled()) {
            log.debug("Attempting PKIX path validation on untrusted credential: {}",
                    X509Util.getIdentifiersToken(untrustedCredential, x500DNHandler));
        }        
        
        try {
            PKIXBuilderParameters params = getPKIXBuilderParameters(validationInfo, untrustedCredential);

            log.trace("Building certificate validation path");

            CertPathBuilder builder = CertPathBuilder.getInstance("PKIX");
            PKIXCertPathBuilderResult buildResult = (PKIXCertPathBuilderResult) builder.build(params);
            if (log.isDebugEnabled()) {
                logCertPathDebug(buildResult, untrustedCredential.getEntityCertificate());
                log.debug("PKIX validation succeeded for untrusted credential: {}",
                        X509Util.getIdentifiersToken(untrustedCredential, x500DNHandler));
            }            
            return true;

        } catch (CertPathBuilderException e) {
            if (log.isTraceEnabled()) {
                log.trace("PKIX path construction failed for untrusted credential: " 
                        + X509Util.getIdentifiersToken(untrustedCredential, x500DNHandler), e);
            } else {
                log.error("PKIX path construction failed for untrusted credential: " 
                        + X509Util.getIdentifiersToken(untrustedCredential, x500DNHandler) + ": " + e.getMessage());
            }
            return false;
        } catch (GeneralSecurityException e) {
            log.error("PKIX validation failure", e);
            throw new SecurityException("PKIX validation failure", e);
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
        if (trustAnchors == null || trustAnchors.isEmpty()) {
            throw new GeneralSecurityException(
                    "Unable to validate X509 certificate, no trust anchors found in the PKIX validation information");
        }

        X509CertSelector selector = new X509CertSelector();
        selector.setCertificate(untrustedCredential.getEntityCertificate());

        log.trace("Adding trust anchors to PKIX validator parameters");
        PKIXBuilderParameters params = new PKIXBuilderParameters(trustAnchors, selector);

        Integer effectiveVerifyDepth = getEffectiveVerificationDepth(validationInfo);
        log.trace("Setting max verification depth to: {} ", effectiveVerifyDepth);
        params.setMaxPathLength(effectiveVerifyDepth);

        CertStore certStore = buildCertStore(validationInfo, untrustedCredential);
        params.addCertStore(certStore);

        if (storeContainsCRLs(certStore)) {
            log.trace("At least one CRL was present in cert store, enabling revocation checking");
            params.setRevocationEnabled(true);
        } else {
            log.trace("No CRLs present in cert store, disabling revocation checking");
            params.setRevocationEnabled(false);
        }

        return params;
    }

    /**
     * Determine whether there are any CRL's in the {@link CertStore} that is to be used.
     * 
     * @param certStore the cert store that will be used for validation
     * @return true if the store contains at least 1 CRL instance, false otherwise
     */
    protected boolean storeContainsCRLs(CertStore certStore) {
        Collection<? extends CRL> crls = null;
        try {
            //Save some cycles and memory: Collection cert store allows null as specifier to return all.
            //crls = certStore.getCRLs( new X509CRLSelector() );
            crls = certStore.getCRLs(null);
        } catch (CertStoreException e) {
            log.error("Error examining cert store for CRL's, treating as if no CRL's present", e);
            return false;
        }
        if (crls != null && !crls.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Get the effective maximum path depth to use when constructing PKIX cert path builder parameters.
     * 
     * @param validationInfo PKIX validation information
     * @return the effective max verification depth to use
     */
    protected Integer getEffectiveVerificationDepth(PKIXValidationInformation validationInfo) {
        Integer effectiveVerifyDepth = validationInfo.getVerificationDepth();
        if (effectiveVerifyDepth == null) {
            effectiveVerifyDepth = DEFAULT_VERIFY_DEPTH;
        }
        return effectiveVerifyDepth;
    }

    /**
     * Creates the collection of trust anchors to use during validation.
     * 
     * @param validationInfo PKIX validation information
     * 
     * @return trust anchors to use during validation
     */
    protected Set<TrustAnchor> getTrustAnchors(PKIXValidationInformation validationInfo) {
        Collection<X509Certificate> validationCertificates = validationInfo.getCertificates();

        log.trace("Constructing trust anchors for PKIX validation");
        Set<TrustAnchor> trustAnchors = new HashSet<TrustAnchor>();
        for (X509Certificate cert : validationCertificates) {
            trustAnchors.add(buildTrustAnchor(cert));
        }

        if (log.isTraceEnabled()) {
            for (TrustAnchor anchor : trustAnchors) {
                log.trace("TrustAnchor: {}", anchor.toString());
            }
        }

        return trustAnchors;
    }

    /**
     * Build a trust anchor from the given X509 certificate.
     * 
     * This could for example be extended by subclasses to add custom name constraints, if desired.
     * 
     * @param cert the certificate which serves as the trust anchor
     * @return the newly constructed TrustAnchor
     */
    protected TrustAnchor buildTrustAnchor(X509Certificate cert) {
        return new TrustAnchor(cert, null);
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

        log.trace("Creating cert store to use during path validation");

        log.trace("Adding entity certificate chain to cert store");
        List<Object> storeMaterial = new ArrayList<Object>(untrustedCredential.getEntityCertificateChain());
        if (log.isTraceEnabled()) {
            for (X509Certificate cert : untrustedCredential.getEntityCertificateChain()) {
                log.trace(String.format("Added X509Certificate from entity cert chain to cert store "
                        + "with subject name '%s' issued by '%s' with serial number '%s'",
                        x500DNHandler.getName(cert.getSubjectX500Principal()),
                        x500DNHandler.getName(cert.getIssuerX500Principal()),
                        cert.getSerialNumber().toString()));
            }
        }
        
        Date now = new Date();
        for (X509CRL crl : validationInfo.getCRLs()) {
            if (crl.getRevokedCertificates() != null && !crl.getRevokedCertificates().isEmpty()) {
                storeMaterial.add(crl);
                if (log.isTraceEnabled()) {
                    log.trace("Added X509CRL to cert store from issuer {} dated {}",
                            x500DNHandler.getName(crl.getIssuerX500Principal()), crl.getThisUpdate());
                }
                if (crl.getNextUpdate().before(now)) {
                    log.warn("Using X509CRL from issuer {} with a nextUpdate in the past: {}",
                            x500DNHandler.getName(crl.getIssuerX500Principal()), crl.getNextUpdate());
                }
            } else {
                if (log.isTraceEnabled()) {
                    log.trace("Empty X509CRL not added to cert store, from issuer {} dated {}",
                            x500DNHandler.getName(crl.getIssuerX500Principal()), crl.getThisUpdate());
                }
            }
        }

        return CertStore.getInstance("Collection", new CollectionCertStoreParameters(storeMaterial));
    }

    /**
     * Log information from the constructed cert path at level debug.
     * 
     * @param buildResult the PKIX cert path builder result containing the cert path and trust anchor
     * @param targetCert the cert untrusted certificate that was being evaluated
     */
    private void logCertPathDebug(PKIXCertPathBuilderResult buildResult, X509Certificate targetCert) {
        log.debug("Built valid PKIX cert path");
        log.debug("Target certificate: {}", x500DNHandler.getName(targetCert.getSubjectX500Principal()));
        for (Certificate cert : buildResult.getCertPath().getCertificates()) {
            log.debug("CertPath certificate: {}", x500DNHandler.getName(((X509Certificate) cert)
                    .getSubjectX500Principal()));
        }
        TrustAnchor ta = buildResult.getTrustAnchor();
        if (ta.getTrustedCert() != null) {
            log.debug("TrustAnchor: {}", x500DNHandler.getName(ta.getTrustedCert().getSubjectX500Principal()));
        } else if (ta.getCA() != null) {
            log.debug("TrustAnchor: {}", x500DNHandler.getName(ta.getCA()));
        } else {
            log.debug("TrustAnchor: {}", ta.getCAName());
        }
    }

}