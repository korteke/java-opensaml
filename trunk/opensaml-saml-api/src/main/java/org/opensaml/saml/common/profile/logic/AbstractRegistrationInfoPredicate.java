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

package org.opensaml.saml.common.profile.logic;

import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.ext.saml2mdrpi.RegistrationInfo;
import org.opensaml.saml.saml2.common.Extensions;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;

import com.google.common.base.Predicate;

/**
 * Base class for predicate that acts on {@link RegistrationInfo} content.
 */
public abstract class AbstractRegistrationInfoPredicate implements Predicate<EntityDescriptor> {
    
    /**
     * Get the {@link RegistrationInfo} extension associated with an entity, if any.
     * 
     * @param entity the entity to examine
     * 
     * @return  the associated extension, or null
     */
    @Nullable protected RegistrationInfo getRegistrationInfo(@Nullable final EntityDescriptor entity) {
        
        if (null == entity) {
            return null;
        }
        
        Extensions extensions = entity.getExtensions();
        if (null != extensions) {
            for (final XMLObject object : extensions.getUnknownXMLObjects(RegistrationInfo.DEFAULT_ELEMENT_NAME)) {
                if (object instanceof RegistrationInfo) {
                    return (RegistrationInfo) object;
                }
            }
        }

        EntitiesDescriptor group = (EntitiesDescriptor) entity.getParent();
        while (null != group) {
            extensions = group.getExtensions();
            if (null != extensions) {
                for (final XMLObject object : extensions.getUnknownXMLObjects(RegistrationInfo.DEFAULT_ELEMENT_NAME)) {
                    if (object instanceof RegistrationInfo) {
                        return (RegistrationInfo) object;
                    }
                }
            }
            group = (EntitiesDescriptor) group.getParent();
        }
        
        return null;
    }
    
}