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

/** Constants to use for {@link ProfileAction} {@link org.opensaml.profile.context.EventContext}s. */
public final class EventIds {

    /**
     * ID of event returned if an I/O-related error occurs.
     */
    public static final String IO_ERROR = "InputOutputError";
    
    /**
     * ID of event returned if the {@link org.opensaml.profile.context.ProfileRequestContext} associated with the
     * current request is missing or corrupt in some way.
     */
    public static final String INVALID_PROFILE_CTX = "InvalidProfileContext";

    /**
     * ID of the event returned if a {@link org.opensaml.messaging.context.MessageContext} is missing or corrupt in some
     * way.
     */
    public static final String INVALID_MSG_CTX = "InvalidMessageContext";

    /**
     * ID of the event returned if a {@link org.opensaml.messaging.context.BasicMessageMetadataContext} is missing or
     * corrupt in some way.
     */
    public static final String INVALID_MSG_MD = "InvalidMessageMetadata";

    /**
     * ID of the event returned if a {@link org.opensaml.messaging.decoder.MessageDecoder} is unable to decode a
     * message.
     */
    public static final String UNABLE_TO_DECODE = "UnableToDecode";
    
    /**
     * ID of the event returned if a {@link org.opensaml.messaging.encoder.MessageEncoder} is unable to encode a
     * message.
     */
    public static final String UNABLE_TO_ENCODE = "UnableToEncode";
    
    /**
     * ID of an Event indicating that an action completed successfully and processing should move on to the next step.
     */
    public static final String PROCEED_EVENT_ID = "proceed";

    /** Constructor. */
    private EventIds() {
    }
}