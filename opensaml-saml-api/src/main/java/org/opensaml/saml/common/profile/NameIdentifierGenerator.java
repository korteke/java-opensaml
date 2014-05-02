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

package org.opensaml.saml.common.profile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;

import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.SAMLException;
import org.opensaml.saml.common.SAMLObject;

/**
 * Interface for a component that produces SAML {@link org.opensaml.saml.saml1.core.NameIdentifier}
 * and/or {@link org.opensaml.saml.saml2.NameID} objects for inclusion in assertion subjects.
 * 
 * <p>Such a component typically consumes attribute information produced about the subject and
 * transforms it (possibly via identity function) into use as a subject identifier. This operation
 * is essentially the inverse of a subject canonicalization flow that operates on SAML identifiers
 * and turns them back into principal names.</p>
 * 
 * <p>A component may be self-contained and need not depend on any other subject information,
 * depending on the nature of the identifier.</p>
 * 
 * @param <NameIdType>  type of object produced
 */
public interface NameIdentifierGenerator<NameIdType extends SAMLObject> {

    /**
     * Generate an identifier object.
     * 
     * @param profileRequestContext the current profile request context
     * @param format the identifier format to generate
     * 
     * @return  the identifier object, or null
     * @throws SAMLException if an error occurs generating an identifier
     */
    @Nullable NameIdType generate(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull @NotEmpty final String format) throws SAMLException;
    
}