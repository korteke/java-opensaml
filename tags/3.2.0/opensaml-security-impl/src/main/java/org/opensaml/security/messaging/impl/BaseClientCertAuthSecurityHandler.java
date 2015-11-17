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

package org.opensaml.security.messaging.impl;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.codec.Base64Support;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.security.messaging.ServletRequestX509CredentialAdapter;
import org.opensaml.security.messaging.ClientTLSSecurityParametersContext;
import org.opensaml.security.trust.TrustEngine;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.security.x509.X509Support;
import org.opensaml.security.x509.tls.CertificateNameOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * Policy rule that checks if the client cert used to authenticate the request is valid and trusted.
 * 
 * <p>
 * This rule is only evaluated if the supplied {@link HttpServletRequest} contains a peer {@link X509Credential} 
 * as returned via {@link ServletRequestX509CredentialAdapter}.
 * </p>
 * 
 * <p>
 * The entity ID used to perform trust evaluation of the X509 credential is first retrieved via
 * {@link #getCertificatePresenterEntityID(MessageContext)}. If this value is non-null, then trust evaluation 
 * proceeds on that basis. If trust evaluation using this entity ID is successful, the message context's 
 * authentication state will be set to <code>true</code> via {@link #setAuthenticatedState(MessageContext, boolean)}
 * and processing is terminated. If unsuccessful, a {@link MessageHandlerException} is thrown.
 * </p>
 * 
 * <p>
 * If no value was available from {@link #getCertificatePresenterEntityID(MessageContext)}, then rule evaluation
 * will be attempted as described in {@link #evaluateCertificateNameDerivedPresenters(X509Credential, MessageContext)},
 * based on the currently configured certificate name evaluation options. If this method returns a non-null certificate
 * presenter entity ID, it will be set on the message context by calling
 * {@link #setAuthenticatedCertificatePresenterEntityID(MessageContext, String)}. The message context's 
 * authentication state will be set to <code>true</code> via {@link #setAuthenticatedState(MessageContext, boolean)}.
 * Rule processing is then terminated. If the method returns null, the client certificate presenter entity ID 
 * and message context authentication state will remain unmodified and rule processing continues.
 * </p>
 * 
 * <p>
 * Finally rule evaluation will proceed as described in
 * {@link #evaluateDerivedPresenters(X509Credential, MessageContext)}. This is primarily an extension point by which
 * subclasses may implement specific custom logic. If this method returns a non-null client certificate presenter entity
 * ID, it will be set via {@link #setAuthenticatedCertificatePresenterEntityID(MessageContext, String)}, the message
 * context's authentication state will be set to <code>true</code> via 
 * {@link #setAuthenticatedState(MessageContext, boolean)} and rule processing is terminated.
 * If the method returns null, the client certificate presenter entity ID and transport authentication state will remain
 * unmodified.
 * </p>
 */
public abstract class BaseClientCertAuthSecurityHandler extends BaseTrustEngineSecurityHandler<X509Credential> {

    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(BaseClientCertAuthSecurityHandler.class);

    /** Options for deriving client cert presenter entity ID's from an X.509 certificate. */
    @Nullable private CertificateNameOptions certNameOptions;
    
    /** The HttpServletRequest being processed. */
    @NonnullAfterInit private HttpServletRequest httpServletRequest;
    
    /**
     * Get the HTTP servlet request being processed.
     * 
     * @return Returns the request.
     */
    @NonnullAfterInit public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    /**
     * Set the HTTP servlet request being processed.
     * 
     * @param request The to set.
     */
    public void setHttpServletRequest(@Nonnull final HttpServletRequest request) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        httpServletRequest = Constraint.isNotNull(request, "HttpServletRequest cannot be null");
    }

    /**
     * Get the certificate name options in use.
     * 
     * @return Returns the certNameOptions.
     */
    @Nullable protected CertificateNameOptions getCertificateNameOptions() {
        return certNameOptions;
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (httpServletRequest == null) {
            throw new ComponentInitializationException("HttpServletRequest cannot be null");
        }
    }
    
    /** {@inheritDoc} */
    @Override
    @Nullable protected TrustEngine<? super X509Credential> resolveTrustEngine(
            @Nonnull final MessageContext messageContext) {
        final ClientTLSSecurityParametersContext secContext = 
                messageContext.getSubcontext(ClientTLSSecurityParametersContext.class);
        if (secContext == null || secContext.getValidationParameters() == null)  {
            return null;
        } else {
            return secContext.getValidationParameters().getX509TrustEngine();
        }
    }

    /** {@inheritDoc} */
    @Override
    protected boolean doPreInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        
        if (!super.doPreInvoke(messageContext)) {
            return false;
        }
        
        final ClientTLSSecurityParametersContext secContext = 
                messageContext.getSubcontext(ClientTLSSecurityParametersContext.class);
        if (secContext != null && !secContext.isEvaluateClientCertificate()) {
            log.debug("{} ClientTLSSecurityParametersContext signals to not perform client TLS cert evaluation",
                    getLogPrefix());
            return false;
        }
        if (secContext == null || secContext.getValidationParameters() == null 
                || secContext.getValidationParameters().getCertificateNameOptions() == null)  {
            throw new MessageHandlerException("CertificateNameOptions was not available from the MessageContext");
        } else {
            certNameOptions = secContext.getValidationParameters().getCertificateNameOptions();
        }
        
        return true;
    }

    /** {@inheritDoc} */
    @Override
    protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {

        final X509Credential requestCredential;
        try {
            requestCredential = new ServletRequestX509CredentialAdapter(getHttpServletRequest());
        } catch (final SecurityException e) {
            log.debug("{} HttpServletRequest did not contain a peer credential, "
                    + "skipping client certificate authentication", getLogPrefix());
            return;
        }
        
        if (log.isDebugEnabled()) {
            try {
                log.debug("{} Attempting to authenticate inbound connection that presented the certificate:",
                        getLogPrefix());
                log.debug(Base64Support.encode(requestCredential.getEntityCertificate().getEncoded(),
                        Base64Support.UNCHUNKED));
            } catch (final CertificateEncodingException e) {
                // do nothing
            }
        }
        
        doEvaluate(requestCredential, messageContext);
    }
    
    /**
     * Evaluate the request credential.
     * 
     * @param requestCredential the X509Credential derived from the request
     * @param messageContext the message context being evaluated
     * @throws MessageHandlerException thrown if a certificate presenter entity ID available from the message context
     *             and the client certificate token can not be establishd as trusted on that basis, or if there is error
     *             during evaluation processing
     */
    protected void doEvaluate(@Nonnull final X509Credential requestCredential,
            @Nonnull final MessageContext messageContext) throws MessageHandlerException {

        final String presenterEntityID = getCertificatePresenterEntityID(messageContext);

        if (presenterEntityID != null) {
            log.debug("{} Attempting client certificate authentication using context presenter entity ID: {}",
                    getLogPrefix(), presenterEntityID);
            if (evaluate(requestCredential, presenterEntityID, messageContext)) {
                log.debug("{} Authentication via client certificate succeeded for context presenter entity ID: {}",
                        getLogPrefix(), presenterEntityID);
                setAuthenticatedState(messageContext, true);
            } else {
                log.error("{} Authentication via client certificate failed for context presenter entity ID: {}",
                        getLogPrefix(), presenterEntityID);
                throw new MessageHandlerException(
                        "Client certificate authentication failed for context presenter entity ID");
            }
            return;
        }

        String derivedPresenter = evaluateCertificateNameDerivedPresenters(requestCredential, messageContext);
        if (derivedPresenter != null) {
            log.debug("{} Authentication via client certificate succeeded for "
                    + "certificate-derived presenter entity ID: {}", getLogPrefix(), derivedPresenter);
            setAuthenticatedCertificatePresenterEntityID(messageContext, derivedPresenter);
            setAuthenticatedState(messageContext, true);
            return;
        }

        derivedPresenter = evaluateDerivedPresenters(requestCredential, messageContext);
        if (derivedPresenter != null) {
            log.debug("{} Authentication via client certificate succeeded for derived presenter entity ID: {}",
                    getLogPrefix(), derivedPresenter);
            setAuthenticatedCertificatePresenterEntityID(messageContext, derivedPresenter);
            setAuthenticatedState(messageContext, true);
            return;
        }
    }

    /**
     * Get the entity ID of the presenter of the client TLS certificate, as will be used for trust evaluation purposes.
     * 
     * <p>
     * This tends to be performed in a protcol-specific manner, so it is therefore abstract and must be 
     * implemented in a concrete subclass.
     * </p>
     * 
     * @param messageContext the current message context
     * @return the entity ID of the client TLS certificate presenter
     */
    @Nullable protected abstract String getCertificatePresenterEntityID(@Nonnull final MessageContext messageContext);

    /**
     * Store the successfully authenticated derived entity ID of the certificate presenter in the message context.
     * 
     * <p>
     * This tends to be performed in a protocol-specific manner, so it is therefore abstract and must be 
     * implemented in a concrete subclass.
     * </p>
     * 
     * @param messageContext the current message context
     * @param entityID the successfully authenticated derived entity ID of the client TLS certificate presenter
     */
    protected abstract void setAuthenticatedCertificatePresenterEntityID(@Nonnull final MessageContext messageContext, 
            @Nullable final String entityID);
    
    
    /**
     * Store the indicated message authentication state in the message context.
     * 
     * <p>
     * This tends to be performed in a protocol-specific manner, so it is therefore abstract and must be 
     * implemented in a concrete subclass.
     * </p>
     * 
     * @param messageContext the current message context
     * @param authenticated flag indicating what authentication state to store
     */
    protected abstract void setAuthenticatedState(@Nonnull final MessageContext messageContext,
            final boolean authenticated);

    /** {@inheritDoc} */
    @Override
    @Nullable protected CriteriaSet buildCriteriaSet(@Nullable final String entityID,
            @Nonnull final MessageContext messageContext) throws MessageHandlerException {

        final CriteriaSet criteriaSet = new CriteriaSet();
        if (!Strings.isNullOrEmpty(entityID)) {
            criteriaSet.add(new EntityIdCriterion(entityID));
        }

        criteriaSet.add(new UsageCriterion(UsageType.SIGNING));

        return criteriaSet;
    }

    /**
     * Evaluate any candidate presenter entity ID's which may be derived from the credential or other message context
     * information.
     * 
     * <p>
     * This serves primarily as an extension point for subclasses to implement application-specific logic.
     * </p>
     * 
     * <p>
     * If multiple derived candidate entity ID's would satisfy the trust engine criteria, the choice of which one to
     * return as the canonical presenter entity ID value is implementation-specific.
     * </p>
     * 
     * @param requestCredential the X509Credential derived from the request
     * @param messageContext the message context being evaluated
     * @return a presenter entity ID which was successfully evaluated by the trust engine
     * @throws MessageHandlerException thrown if there is error during processing
     */
    @Nullable protected String evaluateDerivedPresenters(@Nonnull final X509Credential requestCredential, 
            @Nonnull final MessageContext messageContext) throws MessageHandlerException {

        return null;
    }

    /**
     * Evaluate candidate presenter entity ID's which may be derived from the request credential's entity certificate
     * according to the options supplied via {@link CertificateNameOptions}.
     * 
     * <p>
     * Configured certificate name types are derived as candidate presenter entity ID's and processed in the following
     * order:
     * <ol>
     * <li>The certificate subject DN string as serialized by the X500DNHandler obtained via
     * {@link CertificateNameOptions#getX500DNHandler()} and using the output format indicated by
     * {@link CertificateNameOptions#getX500SubjectDNFormat()}.</li>
     * <li>Subject alternative names of the types configured via {@link CertificateNameOptions#getSubjectAltNames()}.
     * Note that this is a LinkedHashSet, so the order of evaluation is the order of insertion.</li>
     * <li>The first common name (CN) value appearing in the certificate subject DN.</li>
     * </ol>
     * </p>
     * 
     * <p>
     * The first one of the above which is successfully evaluated by the trust engine using criteria built from
     * {@link BaseTrustEngineSecurityHandler#buildCriteriaSet(String, MessageContext)} will be returned.
     * </p>
     * 
     * @param requestCredential the X509Credential derived from the request
     * @param messageContext the message context being evaluated
     * @return a certificate presenter entity ID which was successfully evaluated by the trust engine
     * @throws MessageHandlerException thrown if there is error during processing
     */
    @Nullable protected String evaluateCertificateNameDerivedPresenters(
            @Nullable final X509Credential requestCredential, @Nonnull final MessageContext messageContext)
                    throws MessageHandlerException {

        String candidatePresenter = null;

        if (getCertificateNameOptions().evaluateSubjectDN()) {
            candidatePresenter = evaluateSubjectDN(requestCredential, messageContext);
            if (candidatePresenter != null) {
                return candidatePresenter;
            }
        }

        if (!getCertificateNameOptions().getSubjectAltNames().isEmpty()) {
            candidatePresenter = evaluateSubjectAltNames(requestCredential, messageContext);
            if (candidatePresenter != null) {
                return candidatePresenter;
            }
        }

        if (getCertificateNameOptions().evaluateSubjectCommonName()) {
            candidatePresenter = evaluateSubjectCommonName(requestCredential, messageContext);
            if (candidatePresenter != null) {
                return candidatePresenter;
            }
        }

        return null;
    }

    /**
     * Evaluate the presenter entity ID as derived from the cert subject common name (CN).
     * 
     * Only the first CN value from the subject DN is evaluated.
     * 
     * @param requestCredential the X509Credential derived from the request
     * @param messageContext the message context being evaluated
     * @return a presenter entity ID which was successfully evaluated by the trust engine
     * @throws MessageHandlerException thrown if there is error during processing
     */
    @Nullable protected String evaluateSubjectCommonName(@Nonnull final X509Credential requestCredential, 
            @Nonnull final MessageContext messageContext) throws MessageHandlerException {

        log.debug("{} Evaluating client cert by deriving presenter as cert CN", getLogPrefix());
        final X509Certificate certificate = requestCredential.getEntityCertificate();
        final String candidatePresenter = getCommonName(certificate);
        if (candidatePresenter != null) {
            if (evaluate(requestCredential, candidatePresenter, messageContext)) {
                log.debug("{} Authentication succeeded for presenter entity ID derived from CN: {}", getLogPrefix(),
                        candidatePresenter);
                return candidatePresenter;
            } else {
                log.debug("{} Authentication failed for presenter entity ID derived from CN: {}", getLogPrefix(),
                        candidatePresenter);
            }
        }
        return null;
    }

    /**
     * Evaluate the presenter entity ID as derived from the cert subject DN.
     * 
     * @param requestCredential the X509Credential derived from the request
     * @param messageContext the message context being evaluated
     * @return a presenter entity ID which was successfully evaluated by the trust engine
     * @throws MessageHandlerException thrown if there is error during processing
     */
    @Nullable protected String evaluateSubjectDN(@Nonnull final X509Credential requestCredential,
            @Nonnull final MessageContext messageContext) throws MessageHandlerException {

        log.debug("{} Evaluating client cert by deriving presenter as cert subject DN", getLogPrefix());
        final X509Certificate certificate = requestCredential.getEntityCertificate();
        final String candidatePresenter = getSubjectName(certificate);
        if (candidatePresenter != null) {
            if (evaluate(requestCredential, candidatePresenter, messageContext)) {
                log.debug("{} Authentication succeeded for presenter entity ID derived from subject DN: {}",
                        getLogPrefix(), candidatePresenter);
                return candidatePresenter;
            } else {
                log.debug("{} Authentication failed for presenter entity ID derived from subject DN: {}",
                        getLogPrefix(), candidatePresenter); 
            }
        }
        return null;
    }

    /**
     * Evaluate the presenter entity ID as derived from the cert subject alternative names specified by types enumerated
     * in {@link CertificateNameOptions#getSubjectAltNames()}.
     * 
     * @param requestCredential the X509Credential derived from the request
     * @param messageContext the message context being evaluated
     * @return a presenter entity ID which was successfully evaluated by the trust engine
     * @throws MessageHandlerException thrown if there is error during processing
     */
    @Nullable protected String evaluateSubjectAltNames(@Nonnull final X509Credential requestCredential, 
            @Nonnull final MessageContext messageContext) throws MessageHandlerException {

        log.debug("{} Evaluating client cert by deriving presenter from subject alt names", getLogPrefix());
        final X509Certificate certificate = requestCredential.getEntityCertificate();
        for (final Integer altNameType : getCertificateNameOptions().getSubjectAltNames()) {
            log.debug("{} Evaluating alt names of type: {}", getLogPrefix(), altNameType.toString());
            final List<String> altNames = getAltNames(certificate, altNameType);
            for (final String altName : altNames) {
                if (evaluate(requestCredential, altName, messageContext)) {
                    log.debug("{} Authentication succeeded for presenter entity ID derived from subject alt name: {}",
                            getLogPrefix(), altName);
                    return altName;
                } else {
                    log.debug("{} Authentication failed for presenter entity ID derived from subject alt name: {}",
                            getLogPrefix(), altName);
                }
            }
        }
        return null;
    }

    /**
     * Get the first common name (CN) value from the subject DN of the specified certificate.
     * 
     * @param cert the certificate being processed
     * @return the first CN value, or null if there are none
     */
    @Nullable protected String getCommonName(@Nonnull final X509Certificate cert) {
        final List<String> names = X509Support.getCommonNames(cert.getSubjectX500Principal());
        if (names != null && !names.isEmpty()) {
            String name = names.get(0);
            log.debug("{} Extracted common name from certificate: {}", getLogPrefix(), name);
            return name;
        }
        return null;
    }

    /**
     * Get subject name from a certificate, using the currently configured X500DNHandler and subject DN output format.
     * 
     * @param cert the certificate being processed
     * @return the subject name
     */
    @Nullable protected String getSubjectName(@Nonnull final X509Certificate cert) {
        if (cert == null) {
            return null;
        }
        String name = null;
        if (!Strings.isNullOrEmpty(getCertificateNameOptions().getX500SubjectDNFormat())) {
            name =
                    getCertificateNameOptions().getX500DNHandler().getName(cert.getSubjectX500Principal(),
                            getCertificateNameOptions().getX500SubjectDNFormat());
        } else {
            name = getCertificateNameOptions().getX500DNHandler().getName(cert.getSubjectX500Principal());
        }
        log.debug("{} Extracted subject name from certificate: {}", getLogPrefix(), name);
        return name;
    }

    /**
     * Get the list of subject alt name values from the certificate which are of the specified alt name type.
     * 
     * @param cert the certificate from which to extract alt names
     * @param altNameType the type of alt name to extract
     * 
     * @return the list of certificate subject alt names
     */
    @Nonnull @NonnullElements protected List<String> getAltNames(@Nonnull final X509Certificate cert,
            @Nonnull final Integer altNameType) {
        log.debug("{} Extracting alt names from certificate of type: {}", getLogPrefix(), altNameType.toString());
        Integer[] nameTypes = new Integer[] {altNameType};
        List altNames = X509Support.getAltNames(cert, nameTypes);
        List<String> names = new ArrayList<>();
        for (Object altNameValue : altNames) {
            if (!(altNameValue instanceof String)) {
                log.debug("{} Skipping non-String certificate alt name value", getLogPrefix());
            } else {
                names.add((String) altNameValue);
            }
        }
        log.debug("{} Extracted alt names from certificate: {}", getLogPrefix(), names.toString());
        return names;
    }

}