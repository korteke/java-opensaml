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

package org.opensaml.saml.common.messaging.context;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.annotation.constraint.Live;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;

import org.opensaml.messaging.context.BaseContext;
import org.opensaml.saml.ext.saml2cb.ChannelBindings;

/**
 * Context, usually attached to a {@link org.opensaml.messaging.context.MessageContext}
 * that carries a collection of {@link ChannelBindings} objects supplied with a message.
 */
public class ChannelBindingsContext extends BaseContext {

    /** The set of ChannelBindings. */
    @Nonnull @NonnullElements private Collection<ChannelBindings> channelBindings;
    
    /** Constructor. */
    public ChannelBindingsContext() {
        channelBindings = new ArrayList<>();
    }

    /**
     * Get the channel bindings.
     * 
     * @return the channel bindings
     */
    @Nonnull @NonnullElements @Live public Collection<ChannelBindings> getChannelBindings() {
        return channelBindings;
    }
    
}