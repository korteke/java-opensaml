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

package org.opensaml.profile.action;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;

/** Constants to use for {@link ProfileAction} {@link org.opensaml.profile.context.EventContext}s. */
public final class EventIds {

    /** ID of event returned if an authorization failure occurs. */
    @Nonnull @NotEmpty public static final String ACCESS_DENIED = "AccessDenied";
    
    /**
     * ID of event returned if the {@link org.opensaml.profile.context.ProfileRequestContext} associated with the
     * current request is missing or corrupt in some way.
     */
    @Nonnull @NotEmpty public static final String INVALID_PROFILE_CTX = "InvalidProfileContext";

    /**
     * ID of the event returned if a {@link org.opensaml.messaging.context.MessageContext} is missing or corrupt in some
     * way.
     */
    @Nonnull @NotEmpty public static final String INVALID_MSG_CTX = "InvalidMessageContext";

    /** ID of the event returned if a message is invalid in some general way. */
    @Nonnull @NotEmpty public static final String INVALID_MESSAGE = "InvalidMessage";

    /** ID of the event returned if a message version is incorrect or unsupported. */
    @Nonnull @NotEmpty public static final String INVALID_MESSAGE_VERSION = "InvalidMessageVersion";

    /** ID of event returned if an error occurs with security configuration. */
    @Nonnull @NotEmpty public static final String INVALID_SEC_CFG = "InvalidSecurityConfiguration";

    /** ID of event returned if an I/O-related error occurs. */
    @Nonnull @NotEmpty public static final String IO_ERROR = "InputOutputError";
        
    /** ID of the event returned if a message can't be authenticated. */
    @Nonnull @NotEmpty public static final String MESSAGE_AUTHN_ERROR = "MessageAuthenticationError";
    
    /** ID of the event returned if a message is stale. */
    @Nonnull @NotEmpty public static final String MESSAGE_EXPIRED = "MessageExpired";

    /** ID of the event returned if the preparation of an outbound message fails in some way. */
    @Nonnull @NotEmpty public static final String MESSAGE_PROC_ERROR = "MessageProcessingError";

    /** ID of the event returned if a message is replayed. */
    @Nonnull @NotEmpty public static final String MESSAGE_REPLAY = "MessageReplay";
    
    /** ID of the event returned if a runtime exception is caught. */
    @Nonnull @NotEmpty public static final String RUNTIME_EXCEPTION = "RuntimeException";
    
    /**
     * ID of the event returned if a {@link org.opensaml.messaging.decoder.MessageDecoder} is unable to decode a
     * message.
     */
    @Nonnull @NotEmpty public static final String UNABLE_TO_DECODE = "UnableToDecode";
    
    /**
     * ID of the event returned if a {@link org.opensaml.messaging.encoder.MessageEncoder} is unable to encode a
     * message.
     */
    @Nonnull @NotEmpty public static final String UNABLE_TO_ENCODE = "UnableToEncode";
    
    /** ID of the event returned if creation of a signature fails. */
    @Nonnull @NotEmpty public static final String UNABLE_TO_SIGN = "UnableToSign";

    /** ID of the event returned if encryption of an object/data fails. */
    @Nonnull @NotEmpty public static final String UNABLE_TO_ENCRYPT = "UnableToEncrypt";
    
    /**
     * ID of an Event indicating that an action completed successfully and processing should move on to the next step.
     */
    @Nonnull @NotEmpty public static final String PROCEED_EVENT_ID = "proceed";

    /** Constructor. */
    private EventIds() {
        
    }
}