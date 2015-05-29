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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.SubjectConfirmation;

/**
 * Validates a Sender Vouches subject confirmation.
 * 
 * This validator does not expect any parameters in the {@link ValidationContext#getStaticParameters()} or
 * {@link ValidationContext#getDynamicParameters()}.
 * 
 * This validator does not populate any parameters in the {@link ValidationContext#getDynamicParameters()}.
 */
@ThreadSafe
public class SenderVouchersSubjectConfirmationValidator extends AbstractSubjectConfirmationValidator {

    /** {@inheritDoc} */
    @Nonnull public String getServicedMethod() {
        return SubjectConfirmation.METHOD_SENDER_VOUCHES;
    }

    /** {@inheritDoc} */
    @Nonnull protected ValidationResult doValidate(@Nonnull final SubjectConfirmation confirmation, 
            @Nonnull final Assertion assertion, @Nonnull final ValidationContext context) 
                    throws AssertionValidationException {
        return ValidationResult.VALID;
    }
}