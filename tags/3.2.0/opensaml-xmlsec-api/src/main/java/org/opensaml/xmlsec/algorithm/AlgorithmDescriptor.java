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

package org.opensaml.xmlsec.algorithm;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;

/**
 * An interface for components which describe an algorithm identified by an algorithm URI.
 */
public interface AlgorithmDescriptor {
    
    /** Algorithm descriptor types. */
    public enum AlgorithmType {
        /** Block Encryption. */
        BlockEncryption,
        /** Message Authentication Code (MAC). */
        Mac,
        /** Message Digest. */
        MessageDigest,
        /** Key Agreement. */
        KeyAgreement,
        /** Key Transport. */
        KeyTransport,
        /** Signature. */
        Signature,
        /** Symmetric Key Wrap. */
        SymmetricKeyWrap,
    }
    
    /**
     * Get the algorithm's identifying URI.
     * 
     * @return the algorithm URI
     */
    @Nonnull @NotEmpty public String getURI();
    
    /**
     * Get the algorithm URI's fundamental type.
     * 
     * @return a type specified with {@link AlgorithmType}
     */
    @Nonnull public AlgorithmType getType();
    
    /**
     * Get the algorithm's JCA algorithm ID.
     * 
     * @return the JCA algorithm ID
     */
    @Nonnull @NotEmpty public String getJCAAlgorithmID();

}
