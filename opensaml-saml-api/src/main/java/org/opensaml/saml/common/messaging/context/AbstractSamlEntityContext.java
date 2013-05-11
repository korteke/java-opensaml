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

import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.messaging.context.BaseContext;

/**
 * Abstract base class for subcontexts that carry information about a SAML entity.  This context will often
 * contain subcontexts, whose data is construed to be scoped to that entity.
 */
public abstract class AbstractSamlEntityContext extends BaseContext {

    /** The entityId of the SAML entity. */
    private String entityId;
    
    /** The role under which the SAML entity is currently operating. */
    private QName role;

    /**
     * Gets the entityId of the SAML entity.
     * 
     * @return entityId of the SAML entity, may be null
     */
    @Nullable @NotEmpty public String getEntityId() {
        return entityId;
    }

    /**
     * Sets the entityId of the SAML entity.
     * 
     * @param id the new entityId
     */
    public void setEntityId(@Nullable final String id) {
        entityId = StringSupport.trimOrNull(id);
    }

    /**
     * Get the role under which the SAML entity is currently operating. 
     * 
     * @return Returns the role.
     */
    @Nullable public QName getRole() {
        return role;
    }

    /**
     * Set the role under which the SAML entity is currently operating. 
     * 
     * @param newRole The role to set.
     */
    public void setRole(@Nullable final QName newRole) {
        role = newRole;
    }

}