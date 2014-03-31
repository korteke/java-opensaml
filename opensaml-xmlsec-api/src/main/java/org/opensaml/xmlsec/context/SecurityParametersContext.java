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

package org.opensaml.xmlsec.context;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.BaseContext;
import org.opensaml.xmlsec.DecryptionParameters;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.SignatureValidationParameters;
import org.opensaml.xmlsec.encryption.support.EncryptionParameters;

/**
 * A context implementation for storing parameter instances for XML signature signing and validation, and XML
 * encryption and decryption.
 */
public class SecurityParametersContext extends BaseContext {
    
    /** Signature signing parameters. */
    @Nullable private SignatureSigningParameters signatureSigningParameters;
    
    /** Signature validation parameters. */
    @Nullable private SignatureValidationParameters signatureValidationParameters;
    
    /** Encryption parameters. */
    @Nullable private EncryptionParameters encryptionParameters;
    
    /** Decryption parameters. */
    @Nullable private DecryptionParameters decryptionParameters;

    /**
     * Get the parameters to use for XML signature signing operations.
     * 
     * @return the parameters
     */
    @Nullable public SignatureSigningParameters getSignatureSigningParameters() {
        return signatureSigningParameters;
    }

    /**
     * Set the parameters to use for XML signature signing operations.
     * 
     * @param params the parameters
     * 
     * @return this context
     */
    @Nonnull public SecurityParametersContext setSignatureSigningParameters(
            @Nullable final SignatureSigningParameters params) {
        signatureSigningParameters = params;
        return this;
    }

    /**
     * Get the parameters to use for XML signature validation operations.
     * 
     * @return the parameters
     */
    @Nullable public SignatureValidationParameters getSignatureValidationParameters() {
        return signatureValidationParameters;
    }

    /**
     * Set the parameters to use for XML signature validation operations.
     * 
     * @param params The signatureValidationParameters to set
     * 
     * @return this context
     */
    @Nonnull public SecurityParametersContext setSignatureValidationParameters(
            @Nullable final SignatureValidationParameters params) {
        signatureValidationParameters = params;
        return this;
    }

    /**
     * Get the parameters to use for XML encryption operations.
     * 
     * @return the parameters
     */
    @Nullable public EncryptionParameters getEncryptionParameters() {
        return encryptionParameters;
    }

    /**
     * Set the parameters to use for XML encryption operations.
     * 
     * @param params the parameters
     * 
     * @return this context
     */
    @Nonnull public SecurityParametersContext setEncryptionParameters(@Nullable final EncryptionParameters params) {
        encryptionParameters = params;
        return this;
    }

    /**
     * Get the parameters to use for XML decryption operations.
     * 
     * @return the parameters
     */
    @Nullable public DecryptionParameters getDecryptionParameters() {
        return decryptionParameters;
    }

    /**
     * Set the parameters to use for XML decryption operations.
     * 
     * @param params the parameters
     * 
     * @return this context
     */
    @Nonnull public SecurityParametersContext setDecryptionParameters(@Nullable final DecryptionParameters params) {
        decryptionParameters = params;
        return this;
    }

}