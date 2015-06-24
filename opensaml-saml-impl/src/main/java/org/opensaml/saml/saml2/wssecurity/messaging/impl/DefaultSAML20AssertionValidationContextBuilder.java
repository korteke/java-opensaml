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

package org.opensaml.saml.saml2.wssecurity.messaging.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.collection.LazySet;
import net.shibboleth.utilities.java.support.collection.Pair;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.messaging.context.SAMLSelfEntityContext;
import org.opensaml.saml.saml2.assertion.SAML2AssertionValidationParameters;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.security.messaging.ServletRequestX509CredentialAdapter;
import org.opensaml.security.x509.X509Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;

/**
 *  Function which implements default behavior for building an instance of {@link ValidationContext}
 *  from an instance of {@link SAML20AssertionTokenValidationInput}.
 */
public class DefaultSAML20AssertionValidationContextBuilder 
        implements Function<SAML20AssertionTokenValidationInput, ValidationContext> {
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(DefaultSAML20AssertionValidationContextBuilder.class);
    
    /** A function for resolving the signature validation CriteriaSet for a particular function. */
    private Function<Pair<MessageContext, Assertion>, CriteriaSet> signatureCriteriaSetFunction;
    
    /** Flag indicating whether an Assertion signature is required. */
    private boolean signatureRequired;
    
    /**
     * Constructor.
     */
    public DefaultSAML20AssertionValidationContextBuilder() {
        super();
        signatureRequired = true;
    }

    /**
     * Get the flag indicating whether an Assertion signature is required.
     * 
     * <p>
     * Defaults to: {@code true}.
     * </p>
     * 
     * @return true if required, false if not
     */
    public boolean isSignatureRequired() {
        return signatureRequired;
    }

    /**
     * Set the flag indicating whether an Assertion signature is required.
     * 
     * <p>
     * Defaults to: {@code true}.
     * </p>
     * 
     * @param flag true if required, false if not
     */
    public void setSignatureRequired(boolean flag) {
        signatureRequired = flag;
    }

    /**
     * Get the function for resolving the signature validation CriteriaSet for a particular function.
     * 
     * <p>
     * Defaults to: {@code null}.
     * </p>
     * 
     * @return a criteria set instance, or null
     */
    @Nullable public Function<Pair<MessageContext, Assertion>, CriteriaSet> getSignatureCriteriaSetFunction() {
        return signatureCriteriaSetFunction;
    }

    /**
     * Set the function for resolving the signature validation CriteriaSet for a particular function.
     * 
     * <p>
     * Defaults to: {@code null}.
     * </p>
     * 
     * @param function the resolving function, may be null
     */
    public void setSignatureCriteriaSetFunction(
            @Nullable final Function<Pair<MessageContext, Assertion>, CriteriaSet> function) {
        signatureCriteriaSetFunction = function;
    }

    /** {@inheritDoc} */
    @Nullable public ValidationContext apply(@Nullable final SAML20AssertionTokenValidationInput input) {
        if (input == null) {
            return null;
        }
        
        return new ValidationContext(buildStaticParameters(input));
    }
    
    /**
     * Build the static parameters map for input to the {@link ValidationContext}.
     * 
     * @param input the assertion validation input
     * 
     * @return the static parameters map
     */
    @Nonnull protected Map<String,Object> buildStaticParameters(
            @Nonnull final SAML20AssertionTokenValidationInput input) {
        
        HashMap<String, Object> staticParams = new HashMap<>();
        
        //For signature validation
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, new Boolean(isSignatureRequired()));
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_VALIDATION_CRITERIA_SET, 
                getSignatureCriteriaSet(input));
        
        // For HoK subject confirmation
        X509Certificate attesterCertificate = getAttesterCertificate(input);
        if (attesterCertificate != null) {
            staticParams.put(SAML2AssertionValidationParameters.SC_HOK_PRESENTER_CERT, attesterCertificate);
        }
        PublicKey attesterPublicKey = getAttesterPublicKey(input);
        if (attesterPublicKey != null) {
            staticParams.put(SAML2AssertionValidationParameters.SC_HOK_PRESENTER_KEY, attesterPublicKey);
        }
        
        // For SubjectConfirmationData
        staticParams.put(SAML2AssertionValidationParameters.SC_VALID_RECIPIENTS, getValidRecipients(input));
        staticParams.put(SAML2AssertionValidationParameters.SC_VALID_ADDRESSES, getValidAddresses(input));
        
        // For Audience Condition
        staticParams.put(SAML2AssertionValidationParameters.COND_VALID_AUDIENCES, getValidAudiences(input));
        
        log.debug("Built static parameters map: {}", staticParams);
        
        return staticParams;
    }
    
    /**
     * Get the signature validation criteria set.
     * 
     * <p>
     * This implementation first evaluates the result of applying the function 
     * {@link #getSignatureCriteriaSetFunction()}, if configured. If that evaluation did not
     * produce an {@link EntityIdCriterion}, one is added based on the issuer of the {@link Assertion}.
     * If that evaluation did not produce an instance of {@link UsageCriterion}, one is added with
     * the value of {@link UsageType#SIGNING}.
     * </p>
     * 
     * @param input the assertion validation input
     * 
     * @return the criteria set based on the message context data
     */
    @Nonnull protected CriteriaSet getSignatureCriteriaSet(@Nonnull final SAML20AssertionTokenValidationInput input) {
        CriteriaSet criteriaSet = new CriteriaSet();
        
        if (getSignatureCriteriaSetFunction() != null) {
            CriteriaSet dynamicCriteria = getSignatureCriteriaSetFunction().apply(
                    new Pair<MessageContext, Assertion>());
            if (dynamicCriteria != null) {
                criteriaSet.addAll(dynamicCriteria);
            }
        }
        
        if (!criteriaSet.contains(EntityIdCriterion.class)) {
            String issuer = null;
            if (input.getAssertion().getIssuer() != null) {
                issuer = StringSupport.trimOrNull(input.getAssertion().getIssuer().getValue());
            }
            if (issuer != null) {
                log.debug("Adding internally-generated EntityIdCriterion with value of: {}", issuer);
                criteriaSet.add(new EntityIdCriterion(issuer));
            }
        }
        
        if (!criteriaSet.contains(UsageCriterion.class)) {
            log.debug("Adding internally-generated UsageCriterion with value of: {}", UsageType.SIGNING);
            criteriaSet.add(new UsageCriterion(UsageType.SIGNING));
        }
        
        log.debug("Resolved Signature validation CriteriaSet: {}", criteriaSet);
        
        return criteriaSet;
    }

    /**
     * Get the attesting entity's {@link X509Certificate}.
     * 
     * <p>
     * This implementation returns the client TLS certificate present in the 
     * {@link javax.servlet.http.HttpServletRequest}, or null if one is not present.
     * </p>
     * 
     * @param input the assertion validation input
     * 
     * @return the entity certificate, or null
     */
    @Nullable protected X509Certificate getAttesterCertificate(
            @Nonnull final SAML20AssertionTokenValidationInput input) {
        try {
            X509Credential credential = new ServletRequestX509CredentialAdapter(input.getHttpServletRequest());
            return ((X509Credential)credential).getEntityCertificate();
        } catch (SecurityException e) {
            log.warn("Peer TLS X.509 certificate was not present. " 
                    + "Holder-of-key proof-of-possession via client TLS cert will not be possible");
            return null;
        }
    }

    /**
     * Get the attesting entity's {@link PublicKey}.
     * 
     * <p>
     * This implementation returns null. Subclasses should override to implement specific logic.
     * </p>
     * 
     * @param input the assertion validation input
     * 
     * @return the entity public key, or null
     */
    @Nullable protected PublicKey getAttesterPublicKey(@Nonnull final SAML20AssertionTokenValidationInput input) {
        //TODO this could come theoretically from a WS-Security signature, which has previously been validated.
        return null;
    }
    
    /**
     * Get the valid recipient endpoints for attestation.
     * 
     * <p>
     * This implementation returns a set containing the single value 
     * from {@link javax.servlet.http.HttpServletRequest#getRequestURL()}.
     * </p>
     * 
     * @param input the assertion validation input
     * 
     * @return set of recipient endpoint URI's
     */
    @Nonnull protected Set<String> getValidRecipients(@Nonnull final SAML20AssertionTokenValidationInput input) {
        LazySet<String> validRecipients = new LazySet<>();
        String endpoint = input.getHttpServletRequest().getRequestURL().toString();
        validRecipients.add(endpoint);
        log.debug("Resolved valid subject confirmation recipients set: {}", validRecipients);
        return validRecipients;
    }

    /**
     * Get the set of addresses which are valid for subject confirmation.
     * 
     * <p>
     * This implementation simply returns the set based on 
     * {@link #getAttesterIPAddress(SAML20AssertionTokenValidationInput)}, if that produces a value.
     * Otherwise an empty set is returned.
     * </p>
     * 
     * @param input the assertion validation input
     * 
     * @return the set of valid addresses
     */
    @Nonnull protected Set<InetAddress> getValidAddresses(@Nonnull final SAML20AssertionTokenValidationInput input) {
        try {
            LazySet<InetAddress> validAddresses = new LazySet<>();
            InetAddress[] addresses = null;
            String attesterIPAddress = getAttesterIPAddress(input);
            log.debug("Saw attester IP address: {}", attesterIPAddress);
            if (attesterIPAddress != null) {
                addresses = InetAddress.getAllByName(attesterIPAddress);
                validAddresses.addAll(Arrays.asList(addresses));
                log.debug("Resolved valid subject confirmation InetAddress set: {}", validAddresses);
                return validAddresses;
            } else {
                log.warn("Could not determine attester IP address. Validation of Assertion may or may not succeed");
                return Collections.emptySet();
            }
        } catch (UnknownHostException e) {
            log.warn("Processing of attester IP address failed. Validation of Assertion may or may not succeed", e);
            return Collections.emptySet();
        }
    }
    
    /**
     * Get the attester's IP address.
     * 
     * <p>
     * This implementation returns the value of {@link javax.servlet.http.HttpServletRequest#getRemoteAddr()}.
     * </p>
     * 
     * @param input the assertion validation input
     * 
     * @return the IP address of the attester
     */
    @Nonnull protected String getAttesterIPAddress(@Nonnull final SAML20AssertionTokenValidationInput input) {
        return input.getHttpServletRequest().getRemoteAddr();
    }
    
    /**
     * Get the valid audiences for attestation.
     * 
     * <p>
     * This implementation returns a set containing the single entityID held by the message context's 
     * {@link SAMLSelfEntityContext#getEntityId()}, if present.  Otherwise an empty set is returned.
     * </p>
     * 
     * @param input the assertion validation input
     * 
     * @return set of audience URI's
     */
    @Nonnull protected Set<String> getValidAudiences(@Nonnull final SAML20AssertionTokenValidationInput input) {
        LazySet<String> validAudiences = new LazySet<>();
        
        SAMLSelfEntityContext selfContext = input.getMessageContext().getSubcontext(SAMLSelfEntityContext.class);
        if (selfContext != null && selfContext.getEntityId() != null) {
            validAudiences.add(selfContext.getEntityId());
        }
        
        log.debug("Resolved valid audiences set: {}", validAudiences);
        return validAudiences;
    }

}
