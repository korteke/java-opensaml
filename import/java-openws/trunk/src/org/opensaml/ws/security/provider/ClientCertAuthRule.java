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
import java.util.List;

import javax.servlet.ServletRequest;

import org.apache.log4j.Logger;
import org.opensaml.ws.security.SecurityPolicyContext;
import org.opensaml.ws.security.SecurityPolicyException;
import org.opensaml.ws.security.ServletRequestX509CredentialAdapter;
import org.opensaml.ws.security.provider.ClientCertAuthRuleFactory.CertificateNameOptions;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.credential.EntityIDCriteria;
import org.opensaml.xml.security.credential.UsageCriteria;
import org.opensaml.xml.security.credential.UsageType;
import org.opensaml.xml.security.trust.TrustEngine;
import org.opensaml.xml.security.x509.X509Credential;
import org.opensaml.xml.security.x509.X509Util;
import org.opensaml.xml.util.DatatypeHelper;



/**
 * Policy rule that checks if the client cert used to authenticate the request is valid and trusted.
 * 
 * <p>If the issuer has been previously set in the security policy context by another rule, then that issuer
 * is used to build a criteria set via {@link #buildCriteriaSet(String, ServletRequest, XMLObject, 
 * SecurityPolicyContext)}, and then evaluated via {@link #evaluate(Object, CriteriaSet)}.
 * If this trust evaluation is successful, the context issuer authentication state will be set to
 * <code>true</code>, otherwise it will be set to <code>false</code>.  In either case, rule processing
 * is then terminated.</p>
 * 
 * <p>If no context issuer was previously set, then rule evaluation will proceed as described in
 * {@link #evaluateCertificateNameDerivedIssuers(X509Credential, ServletRequest, XMLObject, 
 * SecurityPolicyContext)}, based on the currently configured certificate name evaluation options.
 * If this method returns a non-null issuer entity ID, it will be set as
 * the issuer in the context, the context's issuer authentication 
 * state will be set to <code>true</code> and rule processing is terminated.
 * If the method returns null, the context issuer and issuer authentication state 
 * will remain unmodified and rule processing continues.</p>
 * 
 * <p>Finally rule evaluation will proceed as described in
 * {@link #evaluateDerivedIssuers(X509Credential, ServletRequest, XMLObject, SecurityPolicyContext)}.
 * This is primarily an extension point by which subclasses may implement specific custom logic.
 * If this method returns a non-null issuer entity ID, it will be set as
 * the issuer in the context, the context's issuer authentication 
 * state will be set to <code>true</code> and rule processing is terminated.
 * If the method returns null, the context issuer and issuer authentication state 
 * will remain unmodified.</p>
 */
public class ClientCertAuthRule extends BaseTrustEngineRule<X509Credential, ServletRequest> {
    
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
    public void evaluate(ServletRequest request, XMLObject message, SecurityPolicyContext context)
        throws SecurityPolicyException {
        
        X509Credential requestCredential = null;
        try {
            requestCredential = new ServletRequestX509CredentialAdapter(request);
        } catch (IllegalArgumentException e) {
            log.info("Request did not contain a certificate, skipping client certificate authentication");
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
    protected CriteriaSet buildCriteriaSet(String entityID, ServletRequest request, XMLObject message, 
            SecurityPolicyContext context) {
        
        CriteriaSet criteriaSet = new CriteriaSet();
        if (! DatatypeHelper.isEmpty(entityID)) {
            criteriaSet.add(new EntityIDCriteria(entityID) );
        }
        
        criteriaSet.add( new UsageCriteria(UsageType.SIGNING) );
        
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
    protected String evaluateDerivedIssuers(X509Credential requestCredential, ServletRequest request, 
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
     *   <li>The certificate subject DN string as serialized by the X500DNHandler configured 
     *       via {@link ClientCertAuthRuleFactory#getX500DNHandler()} and using the output format 
     *       indicated by {@link ClientCertAuthRuleFactory#getX500SubjectDNFormat()}.</li>
     *   <li>Subject alternative names of the types configured via 
     *       {@link ClientCertAuthRuleFactory#getSubjectAltNames()}. Note that this
     *       is a LinkedHashSet, so the order of evaluation is the order or insertion.</li>
     *   <li>The first common name (CN) value appearing in the certificate subject DN.</li>
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
    protected String evaluateCertificateNameDerivedIssuers(X509Credential requestCredential, ServletRequest request,
            XMLObject message, SecurityPolicyContext context) throws SecurityPolicyException {
        
        String candidateIssuer = null;
        
        if (certNameOptions.evaluateSubjectDN()) {
            candidateIssuer = evaluateSubjectDN(requestCredential, request, message, context);
            if (candidateIssuer != null) {
                return candidateIssuer;
            }
        }
        
        if (! certNameOptions.getSubjectAltNames().isEmpty()) {
            candidateIssuer = evaluateSubjectAltNames(requestCredential, request, message, context);
            if (candidateIssuer != null) {
                return candidateIssuer;
            }
        }
        
        if (certNameOptions.evaluateSubjectCommonName()) {
            candidateIssuer = evaluateSubjectCommonName(requestCredential, request, message, context);
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
    protected String evaluateSubjectCommonName(X509Credential requestCredential, ServletRequest request,
            XMLObject message, SecurityPolicyContext context) throws SecurityPolicyException {
        
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
    protected String evaluateSubjectDN(X509Credential requestCredential, ServletRequest request, XMLObject message,
            SecurityPolicyContext context) throws SecurityPolicyException {
        
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
    protected String evaluateSubjectAltNames(X509Credential requestCredential, ServletRequest request,
            XMLObject message, SecurityPolicyContext context) throws SecurityPolicyException {
        
        log.debug("Evaluating client cert by deriving issuer from subject alt names");
        X509Certificate certificate = requestCredential.getEntityCertificate();
        for (Integer altNameType : certNameOptions.getSubjectAltNames()) {
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
        List<String> names = X509Util.getCommonNames(cert.getSubjectX500Principal());
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
        if (! DatatypeHelper.isEmpty(certNameOptions.getX500SubjectDNFormat())) {
            name = certNameOptions.getX500DNHandler().getName(cert.getSubjectX500Principal(), 
                    certNameOptions.getX500SubjectDNFormat());
        } else {
            name = certNameOptions.getX500DNHandler().getName(cert.getSubjectX500Principal());
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