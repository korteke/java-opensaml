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

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.net.URISupport;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.velocity.Template;

import com.google.common.base.Function;

/**
 * Function which produces a URL by substituting a an entity ID value into a Velocity template string.
 * 
 * <p>
 * The entity ID will be replaced in the template string according to the template variable <code>entityID</code>, 
 * e.g. "https://metadataservice.com/entity/${entityID}".
 * </p>
 * 
 * <p>
 * If the value of the <code>encoded</code> parameter is <code>true</code> then the entity ID will be URL encoded prior
 * to substitution.  Otherwise, the literal value of the entity ID will be substituted.
 * </p>
 * 
 */
public class TemplateRequestURLBuilder implements Function<String, String> {
    
    public static final String CONTEXT_KEY_ENTITY_ID = "entityID";
    
    private Template template;
    
    private boolean encodeEntityID;
    
    public TemplateRequestURLBuilder(@Nonnull final VelocityEngine engine, @Nonnull @NotEmpty final String templateString) {
        this(engine, templateString, true);
    }
    
    public TemplateRequestURLBuilder(@Nonnull final VelocityEngine engine, @Nonnull @NotEmpty final String templateString, 
            final boolean encoded) {
        Constraint.isNotNull(engine, "VelocityEngine was null");
        
        String trimmedTemplate = StringSupport.trimOrNull(templateString);
        Constraint.isNotNull(trimmedTemplate, "Template string was null or empty");
        template = Template.fromTemplate(engine, trimmedTemplate);
        
        encodeEntityID = encoded;
    }

    /** {@inheritDoc} */
    @Nullable public String apply(@Nonnull String entityID) {
        VelocityContext context = new VelocityContext();
        if (encodeEntityID) {
            context.put(CONTEXT_KEY_ENTITY_ID, URISupport.doURLEncode(entityID));
        } else {
            context.put(CONTEXT_KEY_ENTITY_ID, entityID);
        }
        
        //TODO logging, exception handling
        
        return template.merge(context);
    }


}
