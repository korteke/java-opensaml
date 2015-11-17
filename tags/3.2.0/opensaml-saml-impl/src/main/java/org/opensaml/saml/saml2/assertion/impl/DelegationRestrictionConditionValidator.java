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

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.namespace.QName;

import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.ext.saml2delrestrict.DelegationRestrictionType;
import org.opensaml.saml.saml2.assertion.ConditionValidator;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Condition;


/**
 * {@link ConditionValidator} implementation for <code>DelegationRestrictionType</code> style conditions. 
 * 
 * <p>
 * Note, as delegation restriction conditions do not place any conditions on the use of an assertion this condition 
 * always evaluates as valid with the assumption that further processing of this information will be done later 
 * by the invoker of the validation process.
 * </p>
 * 
 * <p>
 * Supports the following {@link ValidationContext} static parameters:
 * <ul>
 * None.
 * </ul>
 * </p>
 * 
 * <p>
 * Supports the following {@link ValidationContext} dynamic parameters:
 * <ul>
 * None.
 * </ul>
 * </p>
 */
@ThreadSafe
public class DelegationRestrictionConditionValidator implements ConditionValidator {

    /** {@inheritDoc} */
    @Nonnull public QName getServicedCondition() {
        return DelegationRestrictionType.TYPE_NAME;
    }

    /** {@inheritDoc} */
    @Nonnull public ValidationResult validate(@Nonnull final Condition condition, @Nonnull final Assertion assertion, 
            @Nonnull final ValidationContext context) throws AssertionValidationException {
        
        if ((condition instanceof DelegationRestrictionType) 
                || Objects.equals(condition.getSchemaType(), DelegationRestrictionType.TYPE_NAME)) {
            // Delegation restriction information is a 'condition of use' type condition so we always return valid.
            return ValidationResult.VALID;
        } else {
            return ValidationResult.INDETERMINATE;
        }
    }
}