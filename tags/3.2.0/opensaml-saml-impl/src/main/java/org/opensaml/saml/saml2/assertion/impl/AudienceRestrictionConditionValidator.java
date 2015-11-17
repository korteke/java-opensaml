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

package org.opensaml.saml.saml2.assertion.impl;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.saml2.assertion.ConditionValidator;
import org.opensaml.saml.saml2.assertion.SAML2AssertionValidationParameters;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Audience;
import org.opensaml.saml.saml2.core.AudienceRestriction;
import org.opensaml.saml.saml2.core.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ConditionValidator} implementation for {@link AudienceRestriction} conditions.
 * 
 * <p>
 * Supports the following {@link ValidationContext} static parameters:
 * <ul>
 * <li>
 * {@link SAML2AssertionValidationParameters#COND_VALID_AUDIENCES}:
 * Required.
 * </li>
 * </ul>
 * </p>
 * 
 * <p>
 * Supports the following {@link ValidationContext} dynamic parameters:
 * <ul>
 * None.
 * </ul>
 * </p>
 * 
 */
@ThreadSafe
public class AudienceRestrictionConditionValidator implements ConditionValidator {

    /** Logger. */
    private Logger log = LoggerFactory.getLogger(AudienceRestrictionConditionValidator.class);

    /** {@inheritDoc} */
    @Nonnull public QName getServicedCondition() {
        return AudienceRestriction.DEFAULT_ELEMENT_NAME;
    }

    /** {@inheritDoc} */
    @Nonnull public ValidationResult validate(@Nonnull final Condition condition, @Nonnull final Assertion assertion, 
            @Nonnull final ValidationContext context) throws AssertionValidationException {
        
        if (!(condition instanceof AudienceRestriction)) {
            log.warn("Condition '{}' of type '{}' in assertion '{}' was not an '{}' condition.  Unable to process.",
                    new Object[] { condition.getElementQName(), condition.getSchemaType(), assertion.getID(),
                            getServicedCondition(), });
            return ValidationResult.INDETERMINATE;
        }
        
        Set<String> validAudiences;
        try {
            validAudiences = (Set<String>) context.getStaticParameters().get(
                    SAML2AssertionValidationParameters.COND_VALID_AUDIENCES);
        } catch (ClassCastException e) {
            log.warn("The value of the static validation parameter '{}' was not java.util.Set<String>",
                    SAML2AssertionValidationParameters.COND_VALID_AUDIENCES);
            context.setValidationFailureMessage("Unable to determine list of valid audiences");
            return ValidationResult.INDETERMINATE;
        }
        if (validAudiences == null || validAudiences.isEmpty()) {
            log.warn("Set of valid audiences was not available from the validation context, " 
                    + "unable to evaluate AudienceRestriction Condition");
            context.setValidationFailureMessage("Unable to determine list of valid audiences");
            return ValidationResult.INDETERMINATE;
        }
        log.debug("Evaluating the Assertion's AudienceRestriction/Audience values " 
                + "against the list of valid audiences: {}",
                validAudiences.toString());

        AudienceRestriction audienceRestriction = (AudienceRestriction) condition;
        List<Audience> audiences = audienceRestriction.getAudiences();
        if (audiences == null || audiences.isEmpty()) {
            context.setValidationFailureMessage(String.format(
                    "'%s' condition in assertion '%s' is malformed as it does not contain any audiences",
                    getServicedCondition(), assertion.getID()));
            return ValidationResult.INVALID;
        }

        for (Audience audience : audiences) {
            String audienceURI = StringSupport.trimOrNull(audience.getAudienceURI());
            if (validAudiences.contains(audienceURI)) {
                log.debug("Matched valid audience: {}", audienceURI);
                return ValidationResult.VALID;
            }
        }

        String msg = String.format(
                "None of the audiences within Assertion '%s' matched the list of valid audiances", assertion.getID());
        log.debug(msg);
        context.setValidationFailureMessage(msg);
        return ValidationResult.INVALID;
    }
}