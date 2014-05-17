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

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Nonnull;

import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.net.IPRange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.net.InetAddresses;

/**
 * This action validates that a request comes from an authorized client, based on the client's address.
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#ACCESS_DENIED}
 */
public final class CheckClientAddress extends AbstractProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(CheckClientAddress.class);

    /** List of CIDR blocks allowed to access this servlet. */
    @Nonnull @NonnullElements private Collection<IPRange> allowedRanges;
    
    /** Constructor. */
    public CheckClientAddress() {
        allowedRanges = Collections.emptyList();
    }

    /**
     * Set the CIDR address ranges to allow.
     * 
     * @param ranges ranges to allow
     */
    public void setAllowedRanges(@Nonnull @NonnullElements Collection<IPRange> ranges) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        Constraint.isNotNull(ranges, "IPRange collection cannot be null");
        
        allowedRanges = Lists.newArrayList(Collections2.filter(ranges, Predicates.notNull()));
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        if (getHttpServletRequest() == null) {
            log.warn("{} HttpServletRequest was null, disallowing access", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.ACCESS_DENIED);
            return false;
        } else if (getHttpServletRequest().getRemoteAddr() == null) {
            log.warn("{} Remote address was null, disallowing access", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.ACCESS_DENIED);
            return false;
        }
        
        return super.doPreExecute(profileRequestContext);
    }
    
    /** {@inheritDoc} */
    @Override
    public void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {

        final String addr = getHttpServletRequest().getRemoteAddr();
        log.debug("{} Evaluating client address '{}'", getLogPrefix(), addr);
        
        try {
            byte[] resolvedAddress = InetAddresses.forString(addr).getAddress();
            for (final IPRange range : allowedRanges) {
                if (range.contains(resolvedAddress)) {
                    return;
                }
            }
        } catch (final IllegalArgumentException e) {
            log.warn(getLogPrefix() + " Error translating client address", e);
        }
        
        log.warn("{} Denied request from client address '{}'", getLogPrefix(), addr);
    }

}