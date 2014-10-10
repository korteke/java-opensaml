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

package org.opensaml.saml.common.profile.logic;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.saml2.metadata.AffiliateMember;
import org.opensaml.saml.saml2.metadata.AffiliationDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Evaluates name identifier data and enforces a default policy over its content.
 * 
 * <p>If an SPNameQualifier value is non-null, the value must match the
 * request issuer, or must be an identifier for a SAML {@link AffiliationDescriptor} that
 * contains the issuer.</p>
 */
public class AffiliationNameIDPolicyPredicate extends DefaultNameIDPolicyPredicate {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AffiliationNameIDPolicyPredicate.class);
    
    /** Metadata resolver to use. */
    @NonnullAfterInit private MetadataResolver metadataResolver;

    /**
     * Set the metadata resolver to use.
     * 
     * @param resolver  resolver to use
     */
    public void setMetadataResolver(@Nonnull final MetadataResolver resolver) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        metadataResolver = Constraint.isNotNull(resolver, "MetadataResolver cannot be null");
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (metadataResolver == null) {
            throw new ComponentInitializationException("MetadataResolver cannot be null");
        }
    }

    /** {@inheritDoc} */
    @Override
    protected boolean doApply(@Nullable final String requesterId, @Nullable final String responderId,
            @Nullable final String format, @Nullable final String nameQualifier,
            @Nullable final String spNameQualifier) {
        ComponentSupport.ifNotInitializedThrowUninitializedComponentException(this);
        
        if (super.doApply(requesterId, responderId, format, nameQualifier, spNameQualifier)) {
            return true;
        } else if (spNameQualifier == null) {
            return true;
        }
        
        try {
            final EntityDescriptor affiliation =
                    metadataResolver.resolveSingle(new CriteriaSet(new EntityIdCriterion(spNameQualifier)));
            if (affiliation != null) {
                final AffiliationDescriptor descriptor = affiliation.getAffiliationDescriptor();
                if (descriptor != null) {
                    for (final AffiliateMember member : descriptor.getMembers()) {
                        if (Objects.equals(member.getID(), requesterId)) {
                            log.debug("Entity {} is authorized as a member of Affiliation {}", requesterId,
                                    spNameQualifier);
                            return true;
                        }
                    }
                    log.warn("Entity {} was not a member of Affiliation {}", requesterId, spNameQualifier);
                } else {
                    log.warn("Affiliation entity {} found, but did not contain an AffiliationDescriptor",
                            spNameQualifier);
                }
            } else {
                log.warn("No metadata found for affiliation {}", spNameQualifier);
            }
        } catch (final ResolverException e) {
            log.error("Error resolving metadata for affiliation {}", spNameQualifier, e);
        }
        
        return false;
    }

}