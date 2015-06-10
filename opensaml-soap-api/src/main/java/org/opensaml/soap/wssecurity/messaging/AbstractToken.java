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

package org.opensaml.soap.wssecurity.messaging;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.logic.Constraint;

/**
 * Base abstract implementation of {@link Token}.
 * 
 * @param <TokenType> the type of token  represented
 */
public abstract class AbstractToken<TokenType> implements Token<TokenType> {
    
    /** The wrapped token object instance. */
    private TokenType wrappedToken;
    
    /** Token validation status. */
    private ValidationStatus validationStatus;
    
    /**
     * Constructor.
     *
     * @param token the wrapped token instance
     */
    protected AbstractToken(@Nonnull final TokenType token) {
        wrappedToken = Constraint.isNotNull(token, "Wrapped token may not be null");
        validationStatus = ValidationStatus.VALIDATION_NOT_ATTEMPTED;
    }
    
    /** {@inheritDoc} */
    @Nonnull public TokenType getWrappedToken() {
        return wrappedToken;
    }

    /** {@inheritDoc} */
    @Nonnull public ValidationStatus getValidationStatus() {
        return validationStatus;
    }
    
    /**
     * Get the token validation status.
     * 
     * @param status the new token validation status
     */
    public void setValidationStatus(@Nonnull final ValidationStatus status) {
        validationStatus = Constraint.isNotNull(status, "Validation status may not be null");
    }

}
