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

package org.opensaml.security.x509.tls.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.security.x509.X509Credential;

/**
 * Class which holds and makes available an instance of {@link X509Credential} via ThreadLocal storage, 
 * typically used for client TLS authentication via {@link ThreadLocalX509CredentialKeyManager}.
 */
public final class ThreadLocalX509CredentialContext {

    /** ThreadLocal storage for credential. */
    private static ThreadLocal<X509Credential> currentCredential = new ThreadLocal<>();

    /** Constructor. */
    private ThreadLocalX509CredentialContext() {
    };

    /**
     * Load the thread-local storage with the current credential.
     * 
     * @param credential the current {@link X509Credential}
     */
    public static void loadCurrent(@Nonnull final X509Credential credential) {
        Constraint.isNotNull(credential, "X509Credential may not be null");

        currentCredential.set(credential);
    }

    /**
     * Clear the current thread-local credential.
     */
    public static void clearCurrent() {
        currentCredential.remove();
    }
    
    /**
     * Get whether the current thread-local is populated with a non-null value.
     * 
     * @return true if thread-local has a value, false otherwise
     */
    public static boolean haveCurrent() {
        return currentCredential.get() != null;
    }

    /**
     * Return the current thread-local {@link X509Credential}.
     * 
     * @return the current response
     */
    @Nullable public static X509Credential getCredential() {
        return currentCredential.get();
    }
    
}
