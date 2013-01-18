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

import org.opensaml.xmlsec.signature.Signature;

/**
 * Interface for a component which is responsible for cryptographically computing and storing the 
 * actual digital signature content held within a {@link Signature} instance.
 * 
 * <p>
 * Implementations must be thread-safe.
 * </p>
 * 
 * <p>
 * Instances of this classes are usually used via the {@link Signer} service class.
 * </p>
 */
@ThreadSafe
public interface SignerProvider {
    
    /**
     * Signs a single XMLObject.
     * 
     * @param signature the signature to computer the signature on
     * @throws SignatureException thrown if there is an error computing the signature
     */
    public void signObject(@Nonnull final Signature signature) throws SignatureException;

}
