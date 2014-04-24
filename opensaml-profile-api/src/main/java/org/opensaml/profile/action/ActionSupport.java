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
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.profile.context.EventContext;
import org.opensaml.profile.context.ProfileRequestContext;

/** Helper class for {@link org.opensaml.profile.action.ProfileAction} operations. */
public final class ActionSupport {

    /** Constructor. */
    private ActionSupport() {
    }

    /**
     * Signals a successful outcome by an action.
     * 
     * @param profileRequestContext the context to carry the event
     */
    public static void buildProceedEvent(@Nonnull final ProfileRequestContext profileRequestContext) {
        profileRequestContext.removeSubcontext(EventContext.class);
    }

    /**
     * Builds an event with a given ID but no related attributes.
     * 
     * @param profileRequestContext the context to carry the event
     * @param eventId the ID of the event
     * 
     */
    public static void buildEvent(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull @NotEmpty final String eventId) {
        
        Constraint.isNotNull(profileRequestContext, "Profile request context cannot be null");
        final String trimmedEventId =
                Constraint.isNotNull(StringSupport.trimOrNull(eventId), "ID of event cannot be null or empty");
        
        profileRequestContext.getSubcontext(EventContext.class, true).setEvent(trimmedEventId);
    }
}