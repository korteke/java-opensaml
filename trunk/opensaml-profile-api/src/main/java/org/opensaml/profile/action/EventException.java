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
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

/**
 * Runtime exception which may be used to communicate a specific event ID.
 */
public class EventException extends RuntimeException {
    
    /** Serial version UID. */
    private static final long serialVersionUID = -6394047591957378161L;
    
    /** The event ID. */
    private final String eventID;

    /**
     * Constructor.
     *
     * @param event the event ID
     */
    public EventException(@Nonnull final String event) {
        super();
        eventID = Constraint.isNotNull(StringSupport.trimOrNull(event), "Event ID may not be null");
    }
    
    /**
     * Constructor.
     *
     * @param event the event ID
     * @param message the exception details message
     */
    public EventException(@Nonnull final String event, @Nullable final String message) {
        super(message);
        eventID = Constraint.isNotNull(StringSupport.trimOrNull(event), "Event ID may not be null");
    }
    
    /**
     * Constructor.
     *
     * @param event the event ID
     * @param cause the exception cause
     */
    public EventException(@Nonnull final String event, @Nullable final Throwable cause) {
        super(cause);
        eventID = Constraint.isNotNull(StringSupport.trimOrNull(event), "Event ID may not be null");
    }

    /**
     * Constructor.
     *
     * @param event the event ID
     * @param message the exception details message
     * @param cause the exception cause
     */
    public EventException(@Nonnull final String event, @Nullable final String message, 
            @Nullable final Throwable cause) {
        super(message, cause);
        eventID = Constraint.isNotNull(StringSupport.trimOrNull(event), "Event ID may not be null");
    }

    /**
     * Get the event ID represented by this exception.
     * 
     * @return the event ID
     */
    @Nonnull public String getEventID() {
        return eventID;
    }
    
}