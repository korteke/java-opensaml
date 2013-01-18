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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.signature.Signature;

/**
 * Interface for a provider component that cryptographically validates an 
 * XML Signature {@link Signature} using a candidate validation {@link Credential}.
 * 
 * <p>
 * Implementations must be thread-safe.
 * </p>
 * 
 * <p>
 * Instances of this classes are usually used via the {@link SignatureValidator} service class.
 * </p>
 * 
 */
@ThreadSafe
public interface SignatureValidationProvider {

    /**
     * Validate the given XML Signature using the given candidate validation Credential.
     * 
     * @param signature the XMLSignature to validate
     * @param validationCredential the candidate validation Credential
     * @throws SignatureException if the signature does not validate using the candiate Credential,
     *                              or if there is otherwise an error during the validation operation
     */
    public void validate(@Nonnull final Signature signature, @Nonnull final Credential validationCredential) 
            throws SignatureException;

}