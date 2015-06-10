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

/**
 * An interface for WS-Security tokens.
 * 
 * @param <TokenType> the type of token represented
 */
public interface Token<TokenType> {
    
    /** Enumeration of the possible states of token validation. */
    public enum ValidationStatus {
        /** Indicates the token has been successfully validated. */
        VALID,
        /** Indicates the token has been unsuccessfully validated. */
        INVALID,
        /** Indicates that some attempt has been made to validate the token
         * but that its validation status could not be determined. */
        INDETERMINATE,
        /** Indicates that token validation has not yet been attempted. */
        VALIDATION_NOT_ATTEMPTED,
    }
    
    /**
     * Get the underlying data structure of this token.
     * 
     * @return the underlying token object
     */
    public TokenType getWrappedToken();
    
    /**
     * Get the token validation status.
     * 
     * @return the token validation status
     */
    public ValidationStatus getValidationStatus();
    
}