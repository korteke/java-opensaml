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

package org.opensaml.profile.action.impl;

import javax.annotation.Nonnull;

import org.opensaml.profile.ProfileException;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.context.ErrorEventContext;
import org.opensaml.profile.context.PreviousEventContext;
import org.opensaml.profile.context.ProfileRequestContext;

/**
 * A profile action that copies the contents of the {@link PreviousEventContext} to
 * an {@link ErrorEventContext} for use by error handling actions.
 * 
 * @event {@link org.opensaml.profile.action.EventIds#PROCEED_EVENT_ID}
 * 
 * @post <pre>ProfileRequestContext.getSubcontext(ErrorEventContext.class) != null</pre>
 */
public class SavePreviousEventAsError extends AbstractProfileAction {

    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) throws ProfileException {
        
        final ErrorEventContext error = profileRequestContext.getSubcontext(ErrorEventContext.class, true);
        
        final PreviousEventContext previous = profileRequestContext.getSubcontext(PreviousEventContext.class);
        if (previous != null) {
            error.setEvent(previous.getEvent());
        }
    }
    
}