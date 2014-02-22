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

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.component.AbstractIdentifiableInitializeableComponent;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.profile.ProfileException;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.SAMLObject;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * Abstract base class for simple implementations of {@link NameIdentifierGenerator}.
 * 
 * <p>
 * This class is suitable for implementing generators that produce simple kinds of identifiers. It supports various
 * options controlling the inclusion of qualifier attributes.
 * </p>
 * 
 * <p>
 * Subclasses must override one of {@link #doGenerate(ProfileRequestContext)} or
 * {@link #getIdentifier(ProfileRequestContext)}.
 * </p>
 * 
 * @param <NameIdType> type of object produced
 */
public abstract class AbstractNameIdentifierGenerator<NameIdType extends SAMLObject> extends
        AbstractIdentifiableInitializeableComponent implements FormatSpecificNameIdentifierGenerator<NameIdType> {

    /** A predicate indicating whether the component applies to a request. */
    @Nonnull private Predicate<ProfileRequestContext> activationCondition;

    /** Flag allowing qualifier(s) to be omitted when they would match defaults or are not set. */
    private boolean omitQualifiers;

    /** The identifier Format supported. */
    @NonnullAfterInit @NotEmpty private String format;

    /** Explicit NameQualifier, if any. */
    @Nullable private String idpNameQualifier;

    /** Explicit SPNameQualifier, if any. */
    @Nullable private String spNameQualifier;

    /** SPProvidedID, if any. */
    @Nullable private String spProvidedId;

    /** Constructor. */
    protected AbstractNameIdentifierGenerator() {
        super.setId(getClass().getName());

        activationCondition = Predicates.alwaysTrue();
    }

    /**
     * Set an activation condition that determines whether to run or not.
     * 
     * @param condition an activation condition
     */
    public void setActivationCondition(@Nonnull final Predicate<ProfileRequestContext> condition) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        activationCondition = Constraint.isNotNull(condition, "Predicate cannot be null");
    }

    /**
     * Get whether to omit NameQualifier/SPNameQualifier attributes if the qualifiers are not explicitly set or are set
     * to values matching the IdP and relying party names respectively.
     * 
     * @return whether to omit qualifiers
     */
    public boolean isOmitQualifiers() {
        return omitQualifiers;
    }

    /**
     * Set whether to omit NameQualifier/SPNameQualifier attributes if the qualifiers are not explicitly set or are set
     * to values matching the IdP and relying party names respectively.
     * 
     * @param flag flag to set
     */
    public void setOmitQualifiers(final boolean flag) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        omitQualifiers = flag;
    }

    /** {@inheritDoc} */
    @Override @NonnullAfterInit @NotEmpty public String getFormat() {
        return format;
    }

    /**
     * Set the Format attribute supported.
     * 
     * @param f format to set
     */
    public void setFormat(@Nonnull @NotEmpty final String f) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        format = Constraint.isNotNull(StringSupport.trimOrNull(f), "Format cannot be null or empty");
    }

    /**
     * Get the NameQualifier attribute.
     * 
     * @return the qualifier attribute
     */
    @Nullable public String getIdPNameQualifier() {
        return idpNameQualifier;
    }

    /**
     * Set the NameQualifier attribute.
     * 
     * <p>
     * If not set, and {@link #isOmitQualifiers()} is false, then the value used will be derived from the IdP identity.
     * </p>
     * 
     * @param qualifier qualifier to set
     */
    public void setIdPNameQualifier(@Nullable final String qualifier) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        idpNameQualifier = StringSupport.trimOrNull(qualifier);
    }

    /**
     * Get the SPNameQualifier attribute.
     * 
     * @return the qualifier attribute
     */
    @Nullable public String getSPNameQualifier() {
        return spNameQualifier;
    }

    /**
     * Set the SPNameQualifier attribute.
     * 
     * <p>
     * If not set, and {@link #isOmitQualifiers()} is false, then the value used will be derived from the relying party
     * identity.
     * </p>
     * 
     * @param qualifier qualifier to set
     */
    public void setSPNameQualifier(@Nullable final String qualifier) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        spNameQualifier = StringSupport.trimOrNull(qualifier);
    }

    /**
     * Get the SPProvidedID attribute.
     * 
     * @return the secondary ID attribute
     */
    @Nullable public String getSPProvidedID() {
        return spProvidedId;
    }

    /**
     * Set the SPProvidedID attribute.
     * 
     * @param id value to set
     */
    public void setSPProvidedId(@Nullable final String id) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        spProvidedId = id;
    }

    /** {@inheritDoc} */
    @Override protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();

        if (format == null) {
            throw new ComponentInitializationException("Format value cannot be null or empty");
        }
    }

    /** {@inheritDoc} */
    @Override public boolean apply(@Nullable final ProfileRequestContext input) {
        return activationCondition.apply(input);
    }

    /** {@inheritDoc} */
    @Override @Nullable public NameIdType generate(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull @NotEmpty final String theFormat) throws ProfileException {
        ComponentSupport.ifNotInitializedThrowUninitializedComponentException(this);

        if (!Objects.equal(format, theFormat)) {
            throw new ProfileException("The format to generate does not match the value configured");
        }
        return doGenerate(profileRequestContext);
    }

    /**
     * Override this method to fully control the generation process.
     * 
     * @param profileRequestContext current profile request context
     * @return the generated object
     * @throws ProfileException if an error occurs
     */
    @Nullable protected abstract NameIdType doGenerate(@Nonnull final ProfileRequestContext profileRequestContext)
            throws ProfileException;

    /**
     * Override this method to reuse this implementation of {@link #doGenerate(ProfileRequestContext)}, and return the
     * identifier to be included as the value of the eventual element.
     * 
     * @param profileRequestContext current profile request context
     * 
     * @return the generated identifier
     * @throws ProfileException if an error occurs
     */
    @Nullable protected String getIdentifier(@Nonnull final ProfileRequestContext profileRequestContext)
            throws ProfileException {
        return null;
    }

    /**
     * Get the effective NameQualifier to apply based on the properties set and the current request.
     * 
     * @param profileRequestContext current profile context
     * 
     * @return the effective NameQualifier to set, or null
     */
    @Nullable protected String getEffectiveIdPNameQualifier(@Nonnull final ProfileRequestContext profileRequestContext) {
        if (idpNameQualifier != null) {
            if (omitQualifiers) {
                if (!Objects.equal(idpNameQualifier, getDefaultIdPNameQualifier(profileRequestContext))) {
                    return idpNameQualifier;
                } else {
                    return null;
                }
            } else {
                return idpNameQualifier;
            }
        } else if (!omitQualifiers) {
            return getDefaultIdPNameQualifier(profileRequestContext);
        } else {
            return null;
        }
    }

    /**
     * Get the effective SPNameQualifier to apply based on the properties set and the current request.
     * 
     * @param profileRequestContext current profile context
     * 
     * @return the effective NameQualifier to set, or null
     */
    @Nullable protected String getEffectiveSPNameQualifier(@Nonnull final ProfileRequestContext profileRequestContext) {
        if (spNameQualifier != null) {
            if (omitQualifiers) {
                if (!Objects.equal(spNameQualifier, getDefaultSPNameQualifier(profileRequestContext))) {
                    return spNameQualifier;
                } else {
                    return null;
                }
            } else {
                return spNameQualifier;
            }
        } else if (!omitQualifiers) {
            return getDefaultSPNameQualifier(profileRequestContext);
        } else {
            return null;
        }
    }

    /**
     * Get the default NameQualifier implied by the current profile request context, or null.
     * 
     * @param profileRequestContext current profile request context
     * 
     * @return default qualifier
     */
    @Nullable protected String getDefaultIdPNameQualifier(@Nonnull final ProfileRequestContext profileRequestContext) {
        return null;
    }

    /**
     * Get the default SPNameQualifier implied by the current profile request context, or null.
     * 
     * @param profileRequestContext current profile request context
     * 
     * @return default qualifier
     */
    @Nullable protected String getDefaultSPNameQualifier(@Nonnull final ProfileRequestContext profileRequestContext) {
        return null;
    }

}