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

package org.opensaml.saml.saml2.assertion;

/**
 * Parameter keys used to store and retrieve static and dynamic parameters within a 
 * {@link org.opensaml.saml.common.assertion.ValidationContext}.
 */
public final class SAML2AssertionValidationParameters {

    /** The standard prefix for all SAML 2 parameters defined in this set. */
    public static final String STD_PREFIX = "saml2";
    
    /** The standard infix for parameters concerned with subject confirmation. */
    public static final String SC_INFIX = ".SubjectConfirmation";
    
    /** The standard infix for parameters concerned with conditions evaluation. */
    public static final String COND_INFIX = ".Conditions";
    
    /** The standard infix for parameters concerned with statement evaluation. */
    public static final String STMT_INFIX = ".Statement";
    
    /**
     * Carries a clock skew value in milliseconds as a {@link Long}.
     */
    public static final String CLOCK_SKEW = STD_PREFIX + ".ClockSkew";

    /**
     * Carries the {@link org.opensaml.saml.saml2.core.SubjectConfirmation} that confirmed the subject.
     */
    public static final String CONFIRMED_SUBJECT_CONFIRMATION = STD_PREFIX + ".ConfirmedSubjectConfirmation";

    /**
     * Carries a {@link Boolean} flag which indicates whether the Assertion is required to be signed.
     */
    public static final String SIGNATURE_REQUIRED = STD_PREFIX + ".SignatureRequired";

    /**
     * Carries a {@link net.shibboleth.utilities.java.support.resolver.CriteriaSet} which will be used as the 
     * input to a signature trust engine.  
     */
    public static final String SIGNATURE_VALIDATION_CRITERIA_SET = STD_PREFIX + ".SignatureValidationCriteriaSet";

    /**
     * Carries a {@link Set<String>} whose values are the acceptable subject confirmation data recipient endpoints.
     */
    public static final String SC_VALID_RECIPIENTS = STD_PREFIX + SC_INFIX + ".ValidRecipients";

    /**
     * Carries a {@link Set<InetAddress>} whose values are the acceptable subject confirmation data addresses.
     */
    public static final String SC_VALID_ADDRESSES = STD_PREFIX + SC_INFIX + ".ValidAddresses";
    
    /**
     * Carries the {@link java.security.PublicKey} used by the presenter.
     */
    public static final String SC_HOK_PRESENTER_KEY = STD_PREFIX + SC_INFIX + ".HoK.PresenterKey";

    /**
     * Carries the {@link java.security.cert.X509Certificate} used by the presenter.
     */
    public static final String SC_HOK_PRESENTER_CERT = STD_PREFIX + SC_INFIX + ".HoK.PresenterCertificate";

    /**
     * Carries the {@link org.opensaml.xmlsec.signature.KeyInfo} that successfully confirmed the subject 
     * via holder-of-key subject confirmation.
     */
    public static final String SC_HOK_CONFIRMED_KEYINFO = STD_PREFIX + SC_INFIX + ".HoK.ConfirmedKeyInfo";

    /**
     * Carries a {@link Set<String>} whose values are the acceptable AudienceRestriction Audience values 
     * for evaluating the Assertion.
     */
    public static final String COND_VALID_AUDIENCES = STD_PREFIX + COND_INFIX + ".ValidAudiences";

    /**
     * Carries a {@link Long} representing the per-invocation value for the Assertion replay cache expiration,
     * in milliseconds.
     */
    public static final String COND_ONE_TIME_USE_EXPIRES = STD_PREFIX + COND_INFIX + ".OneTimeUseExpires";
    
    
    
    /** Constructor. */
    private SAML2AssertionValidationParameters() {}

}
