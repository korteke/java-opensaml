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

package org.opensaml.saml.common.messaging.context.navigate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.messaging.context.AbstractAuthenticatableSAMLEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;

import com.google.common.base.Function;

/**
 * Function that returns the authenticated state of a configured {@link AbstractAuthenticatableSAMLEntityContext},
 * defaulting to {@link SAMLPeerEntityContext}.
 */
public class SAMLMessageContextAuthenticationFunction implements Function<MessageContext,Boolean> {
    
    /** The context class representing the authenticatable SAML entity. Defaults to: {@link SAMLPeerEntityContext}. */
    @Nonnull private Class<? extends AbstractAuthenticatableSAMLEntityContext> entityContextClass = 
            SAMLPeerEntityContext.class;
    
    /**
     * Set the class type holding the authenticatable SAML entity data.
     * 
     * <p>Defaults to: {@link SAMLPeerEntityContext}.</p>
     * 
     * @param clazz the entity context class type
     */
    public void setEntityContextClass(@Nonnull final Class<? extends AbstractAuthenticatableSAMLEntityContext> clazz) {
        entityContextClass = Constraint.isNotNull(clazz, "The SAML entity context class may not be null");
    }

    /** {@inheritDoc} */
    @Override
    @Nullable public Boolean apply(@Nullable final MessageContext input) {
        
        if (input != null) {
            final AbstractAuthenticatableSAMLEntityContext entityCtx = input.getSubcontext(entityContextClass);
            if (entityCtx != null) {
                return entityCtx.isAuthenticated();
            }
        }
        
        return null;
    }

}