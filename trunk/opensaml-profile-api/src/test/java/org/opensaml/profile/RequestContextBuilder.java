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

package org.opensaml.profile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.BasicMessageMetadataContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.profile.action.ActionTestingSupport;
import org.opensaml.profile.context.ProfileRequestContext;

import com.google.common.base.Objects;

/**
 * Builder used to construct {@link ProfileRequestContext} used in {@link org.opensaml.profile.action.ProfileAction}
 * executions.
 */
public class RequestContextBuilder {

    /** Value used to represent a string value that has not be set. */
    private final String NO_VAL = "novalue";

    /** The ID of the inbound message. */
    private String inboundMessageId = NO_VAL;

    /** The issue instant of the inbound message in milliseconds. */
    private long inboundMessageIssueInstant;

    /** The issuer of the inbound message. */
    private String inboundMessageIssuer = NO_VAL;

    /** The inbound message. */
    private Object inboundMessage;

    /** The ID of the outbound message. */
    private String outboundMessageId = NO_VAL;

    /** The issue instant of the outbound message in milliseconds. */
    private long outboundMessageIssueInstant;

    /** The issuer of the outbound message. */
    private String outboundMessageIssuer = NO_VAL;

    /** The outbound message. */
    private Object outboundMessage;

    /** Constructor. */
    public RequestContextBuilder() {

    }

    /**
     * Constructor.
     * 
     * @param prototype prototype whose properties are copied onto this builder
     */
    public RequestContextBuilder(RequestContextBuilder prototype) {
        inboundMessageId = prototype.inboundMessageId;
        inboundMessageIssueInstant = prototype.inboundMessageIssueInstant;
        inboundMessageIssuer = prototype.inboundMessageIssuer;
        inboundMessage = prototype.inboundMessage;
        outboundMessageId = prototype.outboundMessageId;
        outboundMessageIssueInstant = prototype.outboundMessageIssueInstant;
        outboundMessageIssuer = prototype.outboundMessageIssuer;
        outboundMessage = prototype.outboundMessage;
    }

    /**
     * Sets the ID of the inbound message.
     * 
     * @param id ID of the inbound message
     * 
     * @return this builder
     */
    @Nonnull public RequestContextBuilder setInboundMessageId(@Nullable final String id) {
        inboundMessageId = id;
        return this;
    }

    /**
     * Sets the issue instant of the inbound message in milliseconds.
     * 
     * @param instant issue instant of the inbound message in milliseconds
     * 
     * @return this builder
     */
    @Nonnull public RequestContextBuilder setInboundMessageIssueInstant(final long instant) {
        inboundMessageIssueInstant = instant;
        return this;
    }

    /**
     * Sets the issuer of the inbound message.
     * 
     * @param issuer issuer of the inbound message
     * 
     * @return this builder
     */
    @Nonnull public RequestContextBuilder setInboundMessageIssuer(@Nullable final String issuer) {
        inboundMessageIssuer = issuer;
        return this;
    }

    /**
     * Sets the inbound message.
     * 
     * @param message the inbound message
     * 
     * @return this builder
     */
    @Nonnull public RequestContextBuilder setInboundMessage(@Nullable final Object message) {
        inboundMessage = message;
        return this;
    }

    /**
     * Sets the ID of the outbound message.
     * 
     * @param id ID of the outbound message
     * 
     * @return this builder
     */
    @Nonnull public RequestContextBuilder setOutboundMessageId(@Nullable final String id) {
        outboundMessageId = id;
        return this;
    }

    /**
     * Sets the issue instant of the outbound message in milliseconds.
     * 
     * @param instant issue instant of the outbound message in milliseconds
     * 
     * @return this builder
     */
    @Nonnull public RequestContextBuilder setOutboundMessageIssueInstant(final long instant) {
        outboundMessageIssueInstant = instant;
        return this;
    }

    /**
     * Sets the issuer of the outbound message.
     * 
     * @param issuer issuer of the outbound message
     * 
     * @return this builder
     */
    public RequestContextBuilder setOutboundMessageIssuer(@Nullable final String issuer) {
        outboundMessageIssuer = issuer;
        return this;
    }

    /**
     * Sets the outbound message.
     * 
     * @param message the outbound message
     * 
     * @return this builder
     */
    @Nonnull public RequestContextBuilder setOutboundMessage(@Nullable final Object message) {
        outboundMessage = message;
        return this;
    }

    /**
     * Builds a {@link ProfileRequestContext}.
     * 
     * The default implementation builds a {@link ProfileRequestContext} that contains a:
     * <ul>
     * <li>inbound message context created by {@link #buildInboundMessageContext()}</li>
     * <li>outbound message context created by {@link #buildOutboundMessageContext()}</li>
     * </ul>
     * 
     * @return the constructed {@link ProfileRequestContext}

     */
    @Nonnull public ProfileRequestContext buildProfileRequestContext() {
        final ProfileRequestContext profileContext = new ProfileRequestContext();
        profileContext.setInboundMessageContext(buildInboundMessageContext());
        profileContext.setOutboundMessageContext(buildOutboundMessageContext());
        
        return profileContext;
    }

    /**
     * Builds a inbound {@link MessageContext}.
     * 
     * The default implementation builds a {@link MessageContext} that contains:
     * <ul>
     * <li>the message provided by {@link #setInboundMessage(Object)}</li>
     * <li>a {@link BasicMessageMetadataContext} created by {@link #buildInboudMessageMetadataContext(MessageContext)}</li>
     * </ul>
     * 
     * @return the constructed {@link MessageContext}
     */
    @Nonnull protected MessageContext buildInboundMessageContext() {
        final MessageContext context = new MessageContext();
        context.setMessage(inboundMessage);
        buildInboudMessageMetadataContext(context);
        return context;
    }

    /**
     * Builds a {@link BasicMessageMetadataContext} and adds it to the given inbound {@link MessageContext}.
     * 
     * The default implementation builds a {@link BasicMessageMetadataContext} that contains:
     * <ul>
     * <li>a message ID provided by {@link #setInboundMessageId(String)} or {@link ActionTestingSupport#INBOUND_MSG_ID}
     * if none is given</li>
     * <li>a message issue instant provided by {@link #setInboundMessageIssueInstant(long)} or 0 (1970-01-01T00:00:00Z)
     * if none is given</li>
     * <li>a message issuer provided by {@link #setInboundMessageIssuer(String)} or
     * {@link ActionTestingSupport#INBOUND_MSG_ISSUER} if none is given
     * </ul>
     * 
     * @param inboundMsgCtx the inbound message context to which the constructed {@link BasicMessageMetadataContext} is
     *            added
     * 
     * @return the constructed {@link BasicMessageMetadataContext}
     */
    @Nullable protected BasicMessageMetadataContext buildInboudMessageMetadataContext(
            @Nonnull final MessageContext inboundMsgCtx) {
        if (Objects.equal(NO_VAL, inboundMessageId) && Objects.equal(NO_VAL, inboundMessageIssuer)) {
            return null;
        }

        final BasicMessageMetadataContext metadataCtx = new BasicMessageMetadataContext();
        inboundMsgCtx.addSubcontext(metadataCtx);

        if (Objects.equal(NO_VAL, inboundMessageId)) {
            metadataCtx.setMessageId(ActionTestingSupport.OUTBOUND_MSG_ID);
        } else {
            metadataCtx.setMessageId(inboundMessageId);
        }

        metadataCtx.setMessageIssueInstant(inboundMessageIssueInstant);

        if (Objects.equal(NO_VAL, inboundMessageIssuer)) {
            metadataCtx.setMessageIssuer(ActionTestingSupport.OUTBOUND_MSG_ISSUER);
        } else {
            metadataCtx.setMessageIssuer(inboundMessageIssuer);
        }

        return metadataCtx;
    }

    /**
     * Builds a outbound {@link MessageContext}.
     * 
     * The default implementation builds a {@link MessageContext} that contains:
     * <ul>
     * <li>the message provided by {@link #setOutboundMessage(Object)}</li>
     * <li>a {@link BasicMessageMetadataContext} created by {@link #buildOutboudMessageMetadataContext()}</li>
     * </ul>
     * 
     * @return the constructed {@link MessageContext}
     */
    @Nonnull protected MessageContext buildOutboundMessageContext() {
        final MessageContext context = new MessageContext();
        context.setMessage(outboundMessage);
        buildOutboundMessageMetadataContext(context);
        return context;

    }

    /**
     * Builds a {@link BasicMessageMetadataContext} and adds it to the given outbound {@link MessageContext}.
     * 
     * The default implementation builds a {@link BasicMessageMetadataContext} that contains:
     * <ul>
     * <li>a message ID provided by {@link #setOutboundMessageId(String)} or {@link ActionTestingSupport#INBOUND_MSG_ID}
     * if none is given</li>
     * <li>a message issue instant provided by {@link #setOutboundMessageIssueInstant(long)} or 0 (1970-01-01T00:00:00Z)
     * if none is given</li>
     * <li>a message issuer provided by {@link #setOutboundMessageIssuer(String)} or
     * {@link ActionTestingSupport#INBOUND_MSG_ISSUER} if none is given
     * </ul>
     * 
     * @param outboundMsgCtx the outbound message context to which the constructed {@link BasicMessageMetadataContext}
     *            is added
     * 
     * @return the constructed {@link BasicMessageMetadataContext}
     */
    @Nonnull protected BasicMessageMetadataContext buildOutboundMessageMetadataContext(
            @Nonnull final MessageContext outboundMsgCtx) {
        if (Objects.equal(NO_VAL, outboundMessageId) && Objects.equal(NO_VAL, outboundMessageIssuer)) {
            return null;
        }

        final BasicMessageMetadataContext metadataCtx = new BasicMessageMetadataContext();
        outboundMsgCtx.addSubcontext(metadataCtx);

        if (Objects.equal(NO_VAL, outboundMessageId)) {
            metadataCtx.setMessageId(ActionTestingSupport.OUTBOUND_MSG_ID);
        } else {
            metadataCtx.setMessageId(outboundMessageId);
        }

        metadataCtx.setMessageIssueInstant(outboundMessageIssueInstant);

        if (Objects.equal(NO_VAL, outboundMessageIssuer)) {
            metadataCtx.setMessageIssuer(ActionTestingSupport.OUTBOUND_MSG_ISSUER);
        } else {
            metadataCtx.setMessageIssuer(outboundMessageIssuer);
        }

        return metadataCtx;
    }

}