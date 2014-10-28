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

package org.opensaml.saml.metadata.resolver.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;

/**
 * Function which examines an entity ID and returns it as a metadata request URL if and only if the entity ID 
 * is an HTTP or HTTPS URL.
 */
public class IdentityRequestURLBuilder implements Function<String, String> {
    
    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(IdentityRequestURLBuilder.class);

    /** {@inheritDoc} */
    @Nullable public String apply(@Nonnull String entityID) {
        Constraint.isNotNull(entityID, "Entity ID was null");
        
        if (entityID.toLowerCase().startsWith("http:") || entityID.toLowerCase().startsWith("https:")) {
            log.debug("Saw entityID with HTTP/HTTPS URL syntax, returning the entityID itself as request URL");
            return entityID;
        } else {
            log.debug("EntityID was not an HTTP or HTTPS URL, could not construct request URL on that basis");
            return null;
        }
    }


}
