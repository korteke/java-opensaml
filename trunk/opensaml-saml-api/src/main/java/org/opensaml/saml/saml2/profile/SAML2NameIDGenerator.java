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

package org.opensaml.saml.saml2.profile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;

import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.SAMLException;
import org.opensaml.saml.common.profile.NameIdentifierGenerator;
import org.opensaml.saml.saml2.core.NameID;

/** Specialization of {@link NameIdentifierGenerator} for SAML 2.0. */
public interface SAML2NameIDGenerator extends NameIdentifierGenerator<NameID> {

    /** {@inheritDoc} */
    @Override
    @Nullable NameID generate(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull @NotEmpty final String format) throws SAMLException;
    
}