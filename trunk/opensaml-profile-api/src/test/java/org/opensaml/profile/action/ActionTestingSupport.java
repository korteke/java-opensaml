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

import org.opensaml.profile.context.EventContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.testng.Assert;

/**
 * Helper methods for creating/testing objects within profile action tests.
 */
public class ActionTestingSupport {

    /** ID of the inbound message. */
    public final static String INBOUND_MSG_ID = "inbound";

    /** Issuer of the inbound message. */
    public final static String INBOUND_MSG_ISSUER = "http://sp.example.org";

    /** ID of the outbound message. */
    public final static String OUTBOUND_MSG_ID = "outbound";

    /** Issuer of the outbound message. */
    public final static String OUTBOUND_MSG_ISSUER = "http://idp.example.org";

    /**
     * Checks that the request context contains an EventContext, and that the event content is as given.
     * 
     * @param profileRequestContext the context to check
     * @param event event to check
     */
    public static void assertEvent(@Nonnull final ProfileRequestContext profileRequestContext, @Nonnull final Object event) {
        EventContext ctx = profileRequestContext.getSubcontext(EventContext.class);
        Assert.assertNotNull(ctx);
        Assert.assertEquals(ctx.getEvent(), event);
    }

    /**
     * Checks that the given request context does not contain an EventContext (thus signaling a "proceed" event).
     * 
     * @param profileRequestContext the context to check
     */
    public static void assertProceedEvent(@Nonnull final ProfileRequestContext profileRequestContext) {
        Assert.assertNull(profileRequestContext.getSubcontext(EventContext.class));
    }
    
}