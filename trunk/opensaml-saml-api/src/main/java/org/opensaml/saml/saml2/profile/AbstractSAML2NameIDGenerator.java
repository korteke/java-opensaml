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

import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.messaging.context.navigate.MessageLookup;
import org.opensaml.profile.ProfileException;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.InboundMessageContextLookup;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.profile.AbstractNameIdentifierGenerator;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.NameID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Strings;

/**
 * Abstract base class for simple implementations of {@link SAML2NameIDGenerator}.
 *
 * <p>This class is suitable for implementing generators that produce simple kinds of identifiers.
 * It supports various options controlling the inclusion of qualifier attributes.</p>
 * 
 * <p>Subclasses must override one of {@link #doGenerate(ProfileRequestContext)} or
 * {@link #getIdentifier(ProfileRequestContext)}.</p>
 */
public abstract class AbstractSAML2NameIDGenerator extends AbstractNameIdentifierGenerator<NameID>
        implements SAML2NameIDGenerator {
    
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AbstractSAML2NameIDGenerator.class);
    
    /** Builder for objects. */
    @Nonnull private final SAMLObjectBuilder<NameID> nameBuilder;

    /** Strategy used to locate an {@link AuthnRequest} to check. */
    @Nonnull private Function<ProfileRequestContext,AuthnRequest> requestLookupStrategy;
    
    /** Constructor. */
    protected AbstractSAML2NameIDGenerator() {
        nameBuilder = (SAMLObjectBuilder<NameID>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<NameID>getBuilderOrThrow(
                        NameID.DEFAULT_ELEMENT_NAME);
        requestLookupStrategy =
                Functions.compose(new MessageLookup<>(AuthnRequest.class), new InboundMessageContextLookup());
    }
    
    /**
     * Set the strategy used to locate the {@link AuthnRequest} to check for a
     * {@org.opensaml.saml.saml2.core.NameIDPolicy}.
     * 
     * @param strategy lookup strategy
     */
    public synchronized void setRequestLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,AuthnRequest> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
    
        requestLookupStrategy = Constraint.isNotNull(strategy, "AuthnRequest lookup strategy cannot be null");
    }
    
    /** {@inheritDoc} */
    @Override
    @Nullable protected NameID doGenerate(@Nonnull final ProfileRequestContext profileRequestContext)
            throws ProfileException {
        
        final String identifier = getIdentifier(profileRequestContext);
        if (identifier == null) {
            log.debug("No identifier to use");
            return null;
        }
        
        log.debug("Generating NameID {} with Format {}", identifier, getFormat());
        
        final NameID nameIdentifier = nameBuilder.buildObject();
        nameIdentifier.setValue(identifier);
        nameIdentifier.setFormat(getFormat());
        nameIdentifier.setNameQualifier(getEffectiveIdPNameQualifier(profileRequestContext));
        nameIdentifier.setSPNameQualifier(getEffectiveSPNameQualifier(profileRequestContext));
        nameIdentifier.setSPProvidedID(getSPProvidedID());
        
        return nameIdentifier;
    }

    /** {@inheritDoc} */
    @Override
    @Nullable protected String getEffectiveSPNameQualifier(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        // Override the default behavior if the SP specifies a qualifier in its request.
        final AuthnRequest request = requestLookupStrategy.apply(profileRequestContext);
        if (request != null && request.getNameIDPolicy() != null) {
            final String qual = request.getNameIDPolicy().getSPNameQualifier();
            if (!Strings.isNullOrEmpty(qual)) {
                return qual;
            }
        }
        
        return super.getEffectiveSPNameQualifier(profileRequestContext);
    }

}