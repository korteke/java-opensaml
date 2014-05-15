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

package org.opensaml.saml.saml2.profile.impl;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.messaging.context.navigate.MessageLookup;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.OutboundMessageContextLookup;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.EncryptedAttribute;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.profile.context.EncryptionContext;
import org.opensaml.xmlsec.EncryptionParameters;
import org.opensaml.xmlsec.encryption.support.EncryptionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;

/**
 * Action that encrypts all attributes in a {@link Response} message obtained from a lookup strategy,
 * by default the outbound message context.
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#UNABLE_TO_ENCRYPT}
 * 
 * @post All SAML attributes in any given statement in the response have been replaced with encrypted versions,
 * or no changes are made to that statement. It's possible for some statements to be modified but others not
 * if an error occurs.
 */
public class EncryptAttributes extends AbstractEncryptAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(EncryptAttributes.class);
    
    /** Strategy used to locate the {@link Response} to operate on. */
    @Nonnull private Function<ProfileRequestContext,Response> responseLookupStrategy;
    
    /** The message to operate on. */
    @Nullable private Response response;
    
    /** Constructor. */
    public EncryptAttributes() {
        responseLookupStrategy =
                Functions.compose(new MessageLookup<>(Response.class), new OutboundMessageContextLookup());
    }

    /**
     * Set the strategy used to locate the {@link Response} to operate on.
     * 
     * @param strategy strategy used to locate the {@link Response} to operate on
     */
    public void setResponseLookupStrategy(@Nonnull final Function<ProfileRequestContext,Response> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        responseLookupStrategy = Constraint.isNotNull(strategy, "Response lookup strategy cannot be null");
    }

    /** {@inheritDoc} */
    @Override
    @Nullable protected EncryptionParameters getApplicableParameters(@Nullable final EncryptionContext ctx) {
        if (ctx != null) {
            return ctx.getAttributeEncryptionParameters();
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        response = responseLookupStrategy.apply(profileRequestContext);
        if (response == null || response.getAssertions().isEmpty()) {
            log.debug("{} Response was not present or contained no assertions, nothing to do", getLogPrefix());
            return false;
        }
        
        return super.doPreExecute(profileRequestContext);
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        for (final Assertion assertion : response.getAssertions()) {
            for (final AttributeStatement statement : assertion.getAttributeStatements()) {
                
                final List<EncryptedAttribute> accumulator =
                        Lists.newArrayListWithExpectedSize(statement.getAttributes().size());
                
                for (final Attribute attribute : statement.getAttributes()) {
                    try {
                        accumulator.add(getEncrypter().encrypt(attribute));
                    } catch (final EncryptionException e) {
                        log.warn(getLogPrefix() + " Error encrypting attribute", e);
                        ActionSupport.buildEvent(profileRequestContext, EventIds.UNABLE_TO_ENCRYPT);
                        return;
                    }
                }
                
                statement.getEncryptedAttributes().addAll(accumulator);
                statement.getAttributes().clear();
            }
        }
    }
    
}