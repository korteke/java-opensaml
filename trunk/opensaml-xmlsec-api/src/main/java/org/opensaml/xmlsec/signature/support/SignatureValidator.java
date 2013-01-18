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

package org.opensaml.xmlsec.signature.support;

import java.util.Iterator;
import java.util.ServiceLoader;

import javax.annotation.Nonnull;

import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.signature.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A service class that cryptographically validates an XML Signature {@link Signature} 
 * using a candidate validation {@link Credential}.
 */
public class SignatureValidator {
    
    /** The cached signature validation provider instance to use. */
    private static SignatureValidationProvider validatorInstance;

    /** Constructor. */
    protected SignatureValidator() { }

    /**
     * Validate the given XML Signature using the given candidate validation Credential.
     * 
     * @param signature the XMLSignature to validate
     * @param validationCredential the candidate validation Credential
     * @throws SignatureException if the signature does not validate using the candiate Credential,
     *                              or if there is otherwise an error during the validation operation
     */
    public static void validate(@Nonnull final Signature signature, @Nonnull final Credential validationCredential) 
            throws SignatureException {
        SignatureValidationProvider validator = getSignatureValidationProvider();
        getLogger().debug("Using a validation provider of implementation: {}", validator.getClass().getName());
        validator.validate(signature, validationCredential);
    }
    
    /**
     * Obtain the {@link SignatureValidationProvider} instance to be used.
     * 
     * @return the SignatureValidationProvider
     * @throws SignatureException if a SignatureValidationProvider could not be loaded
     */
    @Nonnull private static SignatureValidationProvider getSignatureValidationProvider() throws SignatureException {
        if (validatorInstance == null) {
            ServiceLoader<SignatureValidationProvider> loader = ServiceLoader.load(SignatureValidationProvider.class);
            Iterator<SignatureValidationProvider> iterator = loader.iterator();
            if (iterator.hasNext()) {
                validatorInstance = iterator.next();
            } else {
                throw new SignatureException("Could not load a signature validation provider implementation via service API");
            }
        }
        return validatorInstance;
    }
    
    /**
     * Get an SLF4J Logger.
     * 
     * @return a Logger instance
     */
    @Nonnull private static Logger getLogger() {
        return LoggerFactory.getLogger(SignatureValidationProvider.class);
    }

}