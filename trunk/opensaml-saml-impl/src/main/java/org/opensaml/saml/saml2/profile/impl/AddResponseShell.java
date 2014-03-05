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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.security.IdentifierGenerationStrategy;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.messaging.context.BasicMessageMetadataContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.profile.ProfileException;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;

/**
 * Action that creates an empty {@link Response}, and sets it as the
 * message returned by {@link ProfileRequestContext#getOutboundMessageContext()}.
 * 
 * <p>The {@link Status} is set to {@link StatusCode#SUCCESS_URI} as a default assumption,
 * and this can be overridden by subsequent actions.</p>
 * 
 * <p>If the {@link RelyingPartyContext} contains a responder identity (via
 * {@link net.shibboleth.idp.relyingparty.RelyingPartyConfiguration#getResponderId()},
 * it is set as the Issuer of the message.
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_MSG_CTX}
 * @event {@link IdPEventIds#INVALID_RELYING_PARTY_CTX}
 * @event {@link IdPEventIds#INVALID_PROFILE_CONFIG}
 * 
 * @post ProfileRequestContext.getOutboundMessageContext().getMessage() != null
 * @post ProfileRequestContext.getOutboundMessageContext().getSubcontext(
 *  BasicMessageMetadataContext.class, false) != null
 */
public class AddResponseShell extends AbstractProfileAction {

    /** Class logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(AddResponseShell.class);

    /** Strategy used to locate the {@link IdentifierGenerationStrategy} to use. */
    @NonnullAfterInit private Function<ProfileRequestContext,IdentifierGenerationStrategy> idGeneratorLookupStrategy;

    /** Strategy used to obtain the response issuer value. */
    @Nullable private Function<ProfileRequestContext,String> issuerLookupStrategy;
    
    /** The generator to use. */
    @Nullable private IdentifierGenerationStrategy idGenerator;

    /** EntityID to populate into Issuer element. */
    @Nullable private String issuerId;
    
    /**
     * Set the strategy used to locate the {@link IdentifierGenerationStrategy} to use.
     * 
     * @param strategy lookup strategy
     */
    public synchronized void setIdentifierGeneratorLookupStrategy(
            @Nonnull final Function<ProfileRequestContext, IdentifierGenerationStrategy> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        idGeneratorLookupStrategy =
                Constraint.isNotNull(strategy, "IdentifierGenerationStrategy lookup strategy cannot be null");
    }

    /**
     * Set the strategy used to locate the issuer value to use.
     * 
     * @param strategy lookup strategy
     */
    public synchronized void setIssuerLookupStrategy(
            @Nonnull final Function<ProfileRequestContext, String> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        issuerLookupStrategy = Constraint.isNotNull(strategy, "Issuer lookup strategy cannot be null");
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (idGeneratorLookupStrategy == null) {
            throw new ComponentInitializationException("IdentifierGenerationStrategy lookup strategy cannot be null");
        }
    }

    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) throws ProfileException {

        final MessageContext<Response> outboundMessageCtx = profileRequestContext.getOutboundMessageContext();
        if (outboundMessageCtx == null) {
            log.debug("{} No Outbound message context", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        } else if (outboundMessageCtx.getMessage() != null) {
            log.debug("{} Outbound message context already contains a Response", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        }

        idGenerator = idGeneratorLookupStrategy.apply(profileRequestContext);
        if (idGenerator == null) {
            log.debug("{} No identifier generation strategy", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_PROFILE_CTX);
            return false;
        }
        
        issuerId = issuerLookupStrategy.apply(profileRequestContext);

        return super.doPreExecute(profileRequestContext);
    }

    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) throws ProfileException {

        final XMLObjectBuilderFactory bf = XMLObjectProviderRegistrySupport.getBuilderFactory();
        final SAMLObjectBuilder<StatusCode> statusCodeBuilder =
                (SAMLObjectBuilder<StatusCode>) bf.<StatusCode>getBuilderOrThrow(StatusCode.TYPE_NAME);
        final SAMLObjectBuilder<Status> statusBuilder =
                (SAMLObjectBuilder<Status>) bf.<Status>getBuilderOrThrow(Status.TYPE_NAME);
        final SAMLObjectBuilder<Response> responseBuilder =
                (SAMLObjectBuilder<Response>) bf.<Response>getBuilderOrThrow(Response.DEFAULT_ELEMENT_NAME);

        final StatusCode statusCode = statusCodeBuilder.buildObject();
        statusCode.setValue(StatusCode.SUCCESS_URI);

        final Status status = statusBuilder.buildObject();
        status.setStatusCode(statusCode);

        final Response response = responseBuilder.buildObject();

        response.setID(idGenerator.generateIdentifier());
        response.setIssueInstant(new DateTime(ISOChronology.getInstanceUTC()));
        response.setStatus(status);
        response.setVersion(SAMLVersion.VERSION_20);

        if (issuerId != null) {
            log.debug("{} Setting Issuer to {}", getLogPrefix(), issuerId);
            final SAMLObjectBuilder<Issuer> issuerBuilder =
                    (SAMLObjectBuilder<Issuer>) bf.<Issuer>getBuilderOrThrow(Issuer.DEFAULT_ELEMENT_NAME);
            final Issuer issuer = issuerBuilder.buildObject();
            issuer.setValue(issuerId);
            response.setIssuer(issuer);
        } else {
            log.debug("{} No issuer value available, leaving Issuer unset", getLogPrefix());
        }

        profileRequestContext.getOutboundMessageContext().setMessage(response);

        // TODO: Should this be here? Are other contexts also needed?
        final BasicMessageMetadataContext messageMetadata = new BasicMessageMetadataContext();
        messageMetadata.setMessageId(response.getID());
        messageMetadata.setMessageIssueInstant(response.getIssueInstant().getMillis());
        messageMetadata.setMessageIssuer(issuerId);

        profileRequestContext.getOutboundMessageContext().addSubcontext(messageMetadata);
    }

}