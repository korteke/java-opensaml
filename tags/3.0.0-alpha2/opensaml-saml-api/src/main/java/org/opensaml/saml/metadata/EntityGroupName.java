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

package org.opensaml.saml.metadata;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;

/**
 * A bean class which is used to represent an entity group to which an 
 * {@link EntityDescriptor} belongs.  It will typically be attached to an entity descriptor
 * via its {@link XMLObject#getObjectMetadata()}.
 */
public class EntityGroupName {
    
    /** The entities descriptor group name. */
    private String name;
    
    /**
     * Constructor.
     *
     * @param newName the entity group name
     */
    public EntityGroupName(@Nonnull @NotEmpty final String newName) {
        name = Constraint.isNotNull(StringSupport.trimOrNull(newName), 
                "Entity group name may not be null or empty");
    }
    
    /**
     * Get the entity group name.
     * 
     * @return the entity group name
     */
    @Nonnull @NotEmpty public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return name.hashCode();
    }

    /** {@inheritDoc} */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        
        if (obj instanceof EntityGroupName) {
            return name.equals(((EntityGroupName)obj).getName());
        } else {
            return false;
        }
    }

}
