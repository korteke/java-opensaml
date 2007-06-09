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

package org.opensaml.ws.security.provider;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import javax.security.auth.x500.X500Principal;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.opensaml.ws.security.HttpRequestX509CredentialAdapter;
import org.opensaml.ws.security.SecurityPolicyContext;
import org.opensaml.ws.security.SecurityPolicyException;
import org.opensaml.ws.security.SecurityPolicyRule;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.credential.EntityCriteria;
import org.opensaml.xml.security.trust.TrustEngine;
import org.opensaml.xml.security.x509.InternalX500DNHandler;
import org.opensaml.xml.security.x509.X500DNHandler;
import org.opensaml.xml.security.x509.X509Credential;
import org.opensaml.xml.security.x509.X509Util;
import org.opensaml.xml.util.DatatypeHelper;

/**
 * Factory for policy rules which check if the client cert used to authenticate the request is valid and trusted.
 * 
 */
public class ClientCertAuthRuleFactory extends BaseTrustEngineRuleFactory<X509Credential, HttpServletRequest> {
    
    /** Options for derving issuer names from an X.509 certificate. */
    private CertificateNameOptions certNameOptions;
    
    /**
     * Constructor.
     */
    public ClientCertAuthRuleFactory() {
        certNameOptions = new CertificateNameOptions();
    }
    
    /**
     * Get the current certificate name options.  The current instance will be cloned before
     * being returned.
     * 
     * @return cloned instance of the current name options
     */
    protected CertificateNameOptions getCertificateNameOptions() {
        return certNameOptions.clone();
    }

    /** {@inheritDoc} */
    public SecurityPolicyRule<HttpServletRequest> createRuleInstance() {
        return new ClientCertAuthRule(getTrustEngine(), getCertificateNameOptions());
    }
    
    /**
     * Get the option to evaluate the entity certificate subject common name (CN) as a derived issuer entity ID.
     * 
     * @return the option value
     */
    public boolean evaluateSubjectCommonName() {
        return certNameOptions.evaluateSubjectCommonName;
    }

    /**
     * Set the option to evaluate the entity certificate subject common name (CN) as a derived issuer entity ID.
     * 
     * @param newValue the new option value
     */
    public void setEvaluateSubjectCommonName(boolean newValue) {
        certNameOptions.evaluateSubjectCommonName = newValue;
    }
    
    /**
     * Get the option to evaluate the entity certificate subject DN as a derived issuer entity ID.
     * 
     * @return the option value
     */
    public boolean evaluateSubjectDN() {
        return certNameOptions.evaluateSubjectDN;
    }

    /**
     * Set the option to evaluate the entity certificate subject DN as a derived issuer entity ID.
     * 
     * @param newValue the new option value
     */
    public void setEvaluateSubjectDN(boolean newValue) {
        certNameOptions.evaluateSubjectDN = newValue;
    }
    
    /**
     * The set of types of subject alternative names to evaluate as derived issuer entity ID's.
     * 
     * Name types are represented using the constant OID tag name values defined 
     * in {@link X509Util}.  Note: A LinkedHashSet is used to provide predictable ordering
     * on iteration; consequently, the order of evaluation of the name types as derived
     * issuer names will be the same as insertion order into set.
     * 
     * 
     * @return the modifiable set of alt name identifiers
     */
    public LinkedHashSet<Integer> getSubjectAltNames() {
        return certNameOptions.subjectAltNames;
    }
    
    /**
     * Get the handler which process X.500 distinguished names.
     * 
     * Defaults to {@link InternalX500DNHandler}.
     * 
     * @return returns the X500DNHandler instance
     */
    public X500DNHandler getX500DNHandler() {
        return certNameOptions.x500DNHandler;
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
        certNameOptions.x500DNHandler = handler;
    }
    
    /**
     * Get the output format specifier for X.500 subject names.
     * 
     * Defaults to RFC2253 format. The meaning of this format specifier value
     * is dependent upon the implementation of {@link X500DNHandler} which is used.
     * 
     * @return returns the format specifier
     */
    public String getX500SubjectDNFormat() {
        return certNameOptions.x500SubjectDNFormat;
    }
    
    /**
     * Set the output format specifier for X.500 subject names.
     * 
     * Defaults to RFC2253 format. The meaning of this format specifier value
     * is dependent upon the implementation of {@link X500DNHandler} which is used.
     * 
     * @param format the new X500DNHandler instance
     */
    public void setX500SubjectDNFormat(String format) {
        certNameOptions.x500SubjectDNFormat = format;
    }

    /**
     * Policy rule that checks if the client cert used to authenticate the request is valid and trusted.
     * 
     * <p>If the issuer has been previously set in the security policy context by another rule, then that issuer
     * is used to build a criteria set via {@link #buildCriteriaSet(String, HttpServletRequest, XMLObject, 
     * SecurityPolicyContext)}, and then evaluated via {@link #evaluate(X509Credential, CriteriaSet)}.
     * If this trust evaluation is successful, the context issuer authentication state will be set to
     * <code>true</code>, otherwise it will be set to <code>false</code>.  In either case, rule processing
     * is then terminated.</p>
     * 
     * <p>If no context issuer was previously set, then rule evaluation will proceed as described in
     * {@link #evaluateCertificateNameDerivedIssuers(X509Credential, HttpServletRequest, XMLObject, 
     * SecurityPolicyContext)}, based on the currently configured certificate name evaluation options.
     * If this method returns a non-null issuer entity ID, it will be set as
     * the issuer in the context, the context's issuer authentication 
     * state will be set to <code>true</code> and rule processing is terminated.
     * If the method returns null, the context issuer and issuer authentication state 
     * will remain unmodified and rule processing continues.</p>
     * 
     * <p>Finally rule evaluation will proceed as described in
     * {@link #evaluateDerivedIssuers(X509Credential, HttpServletRequest, XMLObject, SecurityPolicyContext)}.
     * This is primarily an extension point by which subclasses may implement specific custom logic.
     * If this method returns a non-null issuer entity ID, it will be set as
     * the issuer in the context, the context's issuer authentication 
     * state will be set to <code>true</code> and rule processing is terminated.
     * If the method returns null, the context issuer and issuer authentication state 
     * will remain unmodified.</p>
     */
    protected class ClientCertAuthRule extends BaseTrustEngineRule<X509Credential, HttpServletRequest> {
        
        /** Logger. */
        private Logger log = Logger.getLogger(ClientCertAuthRule.class);
        
        /** Options for derving issuer names from an X.509 certificate. */
        private CertificateNameOptions certNameOptions;
        
        /**
         * Constructor.
         * 
         * @param engine Trust engine used to verify the request X509Credential
         * @param nameOptions options for deriving issuer names from an X.509 certificate
         * 
         */
        public ClientCertAuthRule(TrustEngine<X509Credential> engine, CertificateNameOptions nameOptions) {
            super(engine);
            certNameOptions = nameOptions;
        }

        /** {@inheritDoc} */
        public void evaluate(HttpServletRequest request, XMLObject message, SecurityPolicyContext context)
                throws SecurityPolicyException {
            
            X509Credential requestCredential = null;
            try {
                requestCredential = new HttpRequestX509CredentialAdapter(request);
            } catch (IllegalArgumentException e) {
                log.info("HTTP request did not contain a certificate, skipping client certificate authentication");
                return;
            }
            
            String contextIssuer = context.getIssuer();
            
            if (contextIssuer != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Attempting client certificate authentication using context issuer: " + contextIssuer);
                }
                CriteriaSet criteriaSet = buildCriteriaSet(contextIssuer, request, message, context);
                if (evaluate(requestCredential, criteriaSet)) {
                    log.info("Authentication via client certificate succeeded for context issuer entity ID '" 
                            + contextIssuer + "'");
                    context.setIssuerAuthenticated(true);
                } else {
                    log.info("Authentication via client certificate failed for context issuer entity ID '" 
                            + contextIssuer + "'");
                    context.setIssuerAuthenticated(false);
                }
                return;
            } 
            
            String derivedIssuer = evaluateCertificateNameDerivedIssuers(requestCredential, request, message, context);
            if (derivedIssuer != null) {
                log.info("Authentication via client certificate succeeded for certificate-derived issuer entity ID '" 
                        + derivedIssuer + "'");
                context.setIssuer(derivedIssuer);
                context.setIssuerAuthenticated(true);
                return;
            }
            
            derivedIssuer = evaluateDerivedIssuers(requestCredential, request, message, context);
            if (derivedIssuer != null) {
                log.info("Authentication via client certificate succeeded for derived issuer entity ID '" 
                        + derivedIssuer + "'");
                context.setIssuer(derivedIssuer);
                context.setIssuerAuthenticated(true);
                return;
            }
        }

        /** {@inheritDoc} */
        protected CriteriaSet buildCriteriaSet(String entityID, HttpServletRequest request, 
                XMLObject message, SecurityPolicyContext context){
            
            CriteriaSet criteriaSet = new CriteriaSet();
            if (! DatatypeHelper.isEmpty(entityID)) {
                criteriaSet.add(new EntityCriteria(entityID, null));
            }
            return criteriaSet;
        }
        
        /**
         * Evaluate any candidate issuer entity ID's which may be derived from the 
         * credential or other request or message information. 
         * 
         * <p>This serves primarily as an extension point for subclasses to implement 
         * application-specific logic.</p>
         * 
         * <p>If multiple derived candidate entity ID's would satisfy the trust engine criteria,
         * the choice of which one to return as the canonical issuer value is implementation-specific.</p>
         * 
         * @param requestCredential the X509Credential derived from the request
         * @param request the protocol request
         * @param message the incoming message
         * @param context the security policy context to use for evaluation and storage of related state info
         * @return an issuer entity ID which was successfully evaluated by the trust engine
         * @throws SecurityPolicyException thrown if there is error during processing
         */
        protected String evaluateDerivedIssuers(X509Credential requestCredential, HttpServletRequest request, 
                XMLObject message, SecurityPolicyContext context) throws SecurityPolicyException {
            
            return null;
        }
        
        /**
         * Evaluate candidate issuer entity ID's which may be derived from the 
         * request credential's entity certificate and which are currently configured.
         * 
         * <p>Configured certificate name types are derived as candidate issuers and processed 
         * in the following order:
         * <ol>
         *   <li>The first common name (CN) value appearing in the certificate subject DN.</li>
         *   <li>Subject alternative names of the types configured via 
         *       {@link ClientCertAuthRuleFactory#getSubjectAltNames()}.</li>
         *   <li>The certificate subject DN string as serialized by the X500DNHandler configured 
         *       via {@link ClientCertAuthRuleFactory#getX500DNHandler()} and using the output format 
         *       indicated by {@link ClientCertAuthRuleFactory#getX500SubjectDNFormat()}.</li>
         * </ol>
         * </p>
         * 
         * <p>The first one of the above which is successfully evaluated by the trust engine
         * using criteria built from {@link BaseTrustEngineRule#buildCriteriaSet(String, javax.servlet.ServletRequest, 
         * XMLObject, SecurityPolicyContext)} will be returned.</p>
         * 
         * @param requestCredential the X509Credential derived from the request
         * @param request the protocol request
         * @param message the incoming message
         * @param context the security policy context to use for evaluation and storage of related state info
         * @return an issuer entity ID which was successfully evaluated by the trust engine
         * @throws SecurityPolicyException thrown if there is error during processing
         */
        protected String evaluateCertificateNameDerivedIssuers(X509Credential requestCredential, 
                HttpServletRequest request, XMLObject message, SecurityPolicyContext context)
                throws SecurityPolicyException {
            
            String candidateIssuer = null;
            
            if (certNameOptions.evaluateSubjectCommonName) {
                candidateIssuer = evaluateSubjectCommonName(requestCredential, request, message, context);
                if (candidateIssuer != null) {
                    return candidateIssuer;
                }
            }
            
            if (! certNameOptions.subjectAltNames.isEmpty()) {
                candidateIssuer = evaluateSubjectAltNames(requestCredential, request, message, context);
                if (candidateIssuer != null) {
                    return candidateIssuer;
                }
            }
            
            if (certNameOptions.evaluateSubjectDN) {
                candidateIssuer = evaluateSubjectDN(requestCredential, request, message, context);
                if (candidateIssuer != null) {
                    return candidateIssuer;
                }
            }
            
            return null;
        }
        
        /**
         * Evaluate the issuer entity ID as derived from the cert subject common name (CN).
         * 
         * Only the first CN value from the subject DN is evaluated.
         * 
         * @param requestCredential the X509Credential derived from the request
         * @param request the protocol request
         * @param message the incoming message
         * @param context the security policy context to use for evaluation and storage of related state info
         * @return an issuer entity ID which was successfully evaluated by the trust engine
         * @throws SecurityPolicyException thrown if there is error during processing
         */
        protected String evaluateSubjectCommonName(X509Credential requestCredential, 
                HttpServletRequest request, XMLObject message, SecurityPolicyContext context)
                throws SecurityPolicyException {
            
            log.debug("Evaluating client cert by deriving issuer as cert CN");
            X509Certificate certificate = requestCredential.getEntityCertificate();
            String candidateIssuer = getCommonName(certificate);
            if (candidateIssuer != null) {
                CriteriaSet criteriaSet = buildCriteriaSet(candidateIssuer, request, message, context);
                if (evaluate(requestCredential, criteriaSet)) {
                    log.info("Authentication succeeded for issuer derived from CN'" 
                            + candidateIssuer + "'");
                    return candidateIssuer;
                }
            }
            return null;
        }
        
        /**
         * Evaluate the issuer entity ID as derived from the cert subject DN.
         * 
         * @param requestCredential the X509Credential derived from the request
         * @param request the protocol request
         * @param message the incoming message
         * @param context the security policy context to use for evaluation and storage of related state info
         * @return an issuer entity ID which was successfully evaluated by the trust engine
         * @throws SecurityPolicyException thrown if there is error during processing
         */
        protected String evaluateSubjectDN(X509Credential requestCredential, 
                HttpServletRequest request, XMLObject message, SecurityPolicyContext context)
                throws SecurityPolicyException {
            
            log.debug("Evaluating client cert by deriving issuer as cert subject DN");
            X509Certificate certificate = requestCredential.getEntityCertificate();
            String candidateIssuer = getSubjectName(certificate);
            if (candidateIssuer != null) {
                CriteriaSet criteriaSet = buildCriteriaSet(candidateIssuer, request, message, context);
                if (evaluate(requestCredential, criteriaSet)) {
                    log.info("Authentication succeeded for issuer derived from subject DN'"
                            + candidateIssuer + "'");
                    return candidateIssuer;
                }
            }
            return null;
        }
        
        /**
         * Evaluate the issuer entity ID as derived from the cert subject alternative names
         * specified by types enumerated in {@link ClientCertAuthRuleFactory#getSubjectAltNames()}.
         * 
         * @param requestCredential the X509Credential derived from the request
         * @param request the protocol request
         * @param message the incoming message
         * @param context the security policy context to use for evaluation and storage of related state info
         * @return an issuer entity ID which was successfully evaluated by the trust engine
         * @throws SecurityPolicyException thrown if there is error during processing
         */
        protected String evaluateSubjectAltNames(X509Credential requestCredential, 
                HttpServletRequest request, XMLObject message, SecurityPolicyContext context)
                throws SecurityPolicyException {
            
            log.debug("Evaluating client cert by deriving issuer from subject alt names");
            X509Certificate certificate = requestCredential.getEntityCertificate();
            for (Integer altNameType : certNameOptions.subjectAltNames) {
                if (log.isDebugEnabled()) {
                    log.debug("Evaluating alt names of type: " + altNameType.toString());
                }
                List<String> altNames = getAltNames(certificate, altNameType);
                for (String altName : altNames) {
                    CriteriaSet criteriaSet = buildCriteriaSet(altName, request, message, context);
                    if (evaluate(requestCredential, criteriaSet)) {
                        log.info("Authentication succeeded for issuer derived from subject alt name'" 
                                + altName + "'");
                        return altName;
                    }
                }
            }
            return null;
        }
        
        /**
         * Get the first common name (CN) value from the subject DN of the specified 
         * certificate.
         * 
         * @param cert the certificate being processed
         * @return the first CN value, or null if there are none
         */
        protected String getCommonName(X509Certificate cert) {
            List<String> names = X509Util.getCommonNames(cert.getIssuerX500Principal());
            if (names != null && ! names.isEmpty()) {
                String name = names.get(0);
                if (log.isDebugEnabled()) {
                    log.debug("Extracted common name from certificate: " + name);
                }
                return name;
            }
            return null;
        }
        
        /**
         * Get subject name from a certificate, using the currently configured X500DNHandler
         * and subject DN output format.
         * 
         * @param cert the certificate being processed
         * @return the subject name
         */
        protected String getSubjectName(X509Certificate cert) {
            if (cert == null) {
                return null;
            }
            String name = null;
            if (! DatatypeHelper.isEmpty(certNameOptions.x500SubjectDNFormat)) {
                name = certNameOptions.x500DNHandler.getName(cert.getSubjectX500Principal(), 
                        certNameOptions.x500SubjectDNFormat);
            } else {
                name = certNameOptions.x500DNHandler.getName(cert.getSubjectX500Principal());
            }
            if (log.isDebugEnabled()) {
                log.debug("Extracted subject name from certificate: " + name);
            }
            return name;
        }
        
        /**
         *  Get the list of subject alt name values from the certificate which are 
         *  of the specified alt name type.
         *  
         *  @param cert the certificate from which to extract alt names
         *  @param altNameType the type of alt name to extract
         *  
         *  @return the list of certificate subject alt names
         */
        protected List<String> getAltNames(X509Certificate cert, Integer altNameType) {
            if (log.isDebugEnabled()) {
                log.debug("Extracting alt names from certificate of type: " + altNameType.toString());
            }
            Integer[] nameTypes = new Integer[] { altNameType };
            List altNames = X509Util.getAltNames(cert, nameTypes);
            List<String> names = new ArrayList<String>();
            for (Object altNameValue : altNames) {
                if (!(altNameValue instanceof String)) {
                    log.debug("Skipping non-String certificate alt name value");
                } else {
                    names.add((String) altNameValue);
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("Extracted alt names from certificate: " + names.toString());
            }
            return names;
        }

    }
    
    /**
     * Options for deriving issuer names from an X.509 certificate.
     */
    protected class CertificateNameOptions implements Cloneable {
        
        /** Evaluate the certificate subject DN as a derived issuer entity ID. */
        private boolean evaluateSubjectDN;
        
        /** Evaluate the certificate subject DN's common name (CN) as a derived issuer entity ID. */
        private boolean evaluateSubjectCommonName;
        
        /** The set of types of subject alternative names evaluate as derived issuer entity ID names. */
        private LinkedHashSet<Integer> subjectAltNames;
        
        /** Responsible for serializing X.500 name strings from 
         * certificate-derived {@link X500Principal} instances. */
        private X500DNHandler x500DNHandler;
        
        /** The format specifier for serializaing X.500 subject names. */
        private String x500SubjectDNFormat;
        
        /** Constructor. */
        protected CertificateNameOptions() {
            subjectAltNames = new LinkedHashSet<Integer>();
            x500DNHandler = new InternalX500DNHandler();
            x500SubjectDNFormat = X500DNHandler.FORMAT_RFC2253;
        }
        
        /** {@inheritDoc} */
        protected CertificateNameOptions clone() {
            CertificateNameOptions clonedOptions;
            try {
                clonedOptions = (CertificateNameOptions) super.clone();
            } catch (CloneNotSupportedException e) {
                // we know we're cloneable, so this will never happen
                return null;
            }
            
            clonedOptions.subjectAltNames = new LinkedHashSet<Integer>();
            clonedOptions.subjectAltNames.addAll(this.subjectAltNames);
            
            clonedOptions.x500DNHandler = this.x500DNHandler.clone();
            
            return clonedOptions;
        }
        
    }
    
}