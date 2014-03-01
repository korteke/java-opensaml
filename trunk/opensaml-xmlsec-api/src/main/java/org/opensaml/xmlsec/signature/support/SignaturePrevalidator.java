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

import org.opensaml.xmlsec.signature.Signature;

/**
 * An interface for components which perform some pre-validation processing on an XML {@link Signature} instance,
 * for example to validate that the signature confirms to a particular profile of XML Signature.
 * 
 * <p>
 * Note that implementations of this interface are NOT used to perform the actual cryptographic validation of the 
 * signature against key material.  For that see {@link SignatureValidator}.
 * </p>
 */
public interface SignaturePrevalidator {

    /**
     * Validate the signature according to the requirements represented by the validator.
     * 
     * @param signature the signature to evaluate
     * @throws SignatureException if the signature does not meet the validator's requirements
     */
    public void validate(@Nonnull final Signature signature) throws SignatureException;

}