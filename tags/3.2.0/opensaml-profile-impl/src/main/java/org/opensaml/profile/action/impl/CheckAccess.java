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
import javax.annotation.Nullable;

import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.security.AccessControlService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This action validates that a request comes from an authorized client, based on an injected service
 * and policy parameters.
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#ACCESS_DENIED}
 */
public class CheckAccess extends AbstractProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(CheckAccess.class);

    /** Access control service. */
    @NonnullAfterInit private AccessControlService service;
    
    /** Policy name. */
    @NonnullAfterInit private String policyName;
    
    /** Operation. */
    @Nullable private String operation;

    /** Resource. */
    @Nullable private String resource;

    /**
     * Set the service to use.
     * 
     * @param acs service to use
     */
    public void setAccessControlService(@Nonnull final AccessControlService acs) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        service = Constraint.isNotNull(acs, "AccessControlService cannot be null");
    }

    /**
     * Set policy name.
     * 
     * @param name  policy name
     */
    public void setPolicyName(@Nonnull final String name) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        policyName = Constraint.isNotNull(StringSupport.trimOrNull(name), "Policy name cannot be null or empty");
    }

    /**
     * Set operation.
     * 
     * @param op operation
     */
    public void setOperation(@Nonnull final String op) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        operation = StringSupport.trimOrNull(op);
    }

    /**
     * Set resource.
     * 
     * @param res resource
     */
    public void setResource(@Nonnull final String res) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        resource = StringSupport.trimOrNull(res);
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (service == null || policyName == null) {
            throw new ComponentInitializationException("AccessControlService and policy name cannot be null");
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        if (!super.doPreExecute(profileRequestContext)) {
            return false;
        } else if (getHttpServletRequest() == null) {
            log.warn("{} HttpServletRequest was null, disallowing access", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.ACCESS_DENIED);
            return false;
        }
        
        return true;
    }
    
    /** {@inheritDoc} */
    @Override
    public void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {

        if (!service.getInstance(policyName).checkAccess(getHttpServletRequest(), operation, resource)) {
            ActionSupport.buildEvent(profileRequestContext, EventIds.ACCESS_DENIED);
        }
    }

}