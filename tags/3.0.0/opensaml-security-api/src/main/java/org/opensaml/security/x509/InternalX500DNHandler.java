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

package org.opensaml.security.x509;

import javax.annotation.Nonnull;
import javax.security.auth.x500.X500Principal;

import net.shibboleth.utilities.java.support.logic.Constraint;

/**
 * Basic implementation of {@link X500DNHandler} which uses the internal built-in mechanisms
 * provided by {@link X500Principal} directly.
 */
public class InternalX500DNHandler implements X500DNHandler {

    /** {@inheritDoc} */
    @Nonnull public byte[] getEncoded(@Nonnull final X500Principal principal) {
        Constraint.isNotNull(principal, "X500Principal cannot be null");
        return principal.getEncoded();
    }

    /** {@inheritDoc} */
    @Nonnull public String getName(@Nonnull final X500Principal principal) {
        Constraint.isNotNull(principal, "X500Principal cannot be null");
        return principal.getName();
    }

    /** {@inheritDoc} */
    @Nonnull public String getName(@Nonnull final X500Principal principal, @Nonnull final String format) {
        Constraint.isNotNull(principal, "X500Principal cannot be null");
        return principal.getName(format);
    }

    /** {@inheritDoc} */
    @Nonnull public X500Principal parse(@Nonnull final String name) {
        Constraint.isNotNull(name, "X.500 name string cannot be null");
        return new X500Principal(name);
    }

    /** {@inheritDoc} */
    @Nonnull public X500Principal parse(@Nonnull final byte[] name) {
        Constraint.isNotNull(name, "X.500 DER-encoded name cannot be null");
        return new X500Principal(name);
    }

    /** {@inheritDoc} */
    @Nonnull public X500DNHandler clone() {
        // We don't have any state, just return a new instance to maintain the clone() contract.
        return new InternalX500DNHandler();
    }

}