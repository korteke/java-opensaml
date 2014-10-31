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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.net.URISupport;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.velocity.Template;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    
    /** The Velocity context variable name for the entity ID. */
    public static final String CONTEXT_KEY_ENTITY_ID = "entityID";
    
    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(TemplateRequestURLBuilder.class);
    
    /** Velocity template instance used to render the request URL. */
    private Template template;
    
    /** The template text, for logging purposes. */
    private String templateText;
    
    /** Function which transforms the entityID prior to substitution into the template. */
    private Function<String, String> transformer;
    
    /** Flag indicating whether to URL-encode the entity ID value before substitution. */
    private boolean encodeEntityID;
    
    /**
     * Constructor.
     * 
     * <p>The template character set will be US ASCII.</p>
     *
     * @param engine the {@link VelocityEngine} instance to use
     * @param templateString the Velocity template string
     * @param encoded true if entity ID should be URL-encoded prior to substitution, false otherwise
     */
    public TemplateRequestURLBuilder(@Nonnull final VelocityEngine engine, 
            @Nonnull @NotEmpty final String templateString, final boolean encoded) {
        this(engine, templateString, encoded, null, StandardCharsets.US_ASCII);
    }
    /**
     * Constructor.
     * 
     * <p>The template character set will be US ASCII.</p>
     *
     * @param engine the {@link VelocityEngine} instance to use
     * @param templateString the Velocity template string
     * @param transform function which transforms the entityID prior to substitution, may be null
     * @param encoded true if entity ID should be URL-encoded prior to substitution, false otherwise
     */
    public TemplateRequestURLBuilder(@Nonnull final VelocityEngine engine, 
            @Nonnull @NotEmpty final String templateString, final boolean encoded, 
            @Nullable final Function<String, String> transform) {
        this(engine, templateString, encoded, transform, StandardCharsets.US_ASCII);
    }
    
    /**
     * Constructor.
     *
     * @param engine the {@link VelocityEngine} instance to use
     * @param templateString the Velocity template string
     * @param encoded true if entity ID should be URL-encoded prior to substitution, false otherwise
     * @param transform function which transforms the entityID prior to substitution, may be null
     * @param charSet character set of the template, may be null
     */
    public TemplateRequestURLBuilder(@Nonnull final VelocityEngine engine, 
            @Nonnull @NotEmpty final String templateString, final boolean encoded, 
            @Nullable final Function<String, String> transform, @Nullable final Charset charSet) {
        
        Constraint.isNotNull(engine, "VelocityEngine was null");
        
        String trimmedTemplate = StringSupport.trimOrNull(templateString);
        templateText = Constraint.isNotNull(trimmedTemplate, "Template string was null or empty");
        
        transformer = transform;
        
        if (charSet != null) {
            template = Template.fromTemplate(engine, trimmedTemplate, charSet);
        } else {
            template = Template.fromTemplate(engine, trimmedTemplate);
        }
        
        encodeEntityID = encoded;
    }

    /** {@inheritDoc} */
    @Nullable public String apply(@Nonnull String input) {
        String entityID = Constraint.isNotNull(input, "Entity ID was null");
        
        log.debug("Saw input entityID '{}'", entityID);
        
        if (transformer != null) {
            entityID = transformer.apply(entityID);
            log.debug("Transformed entityID is '{}'", entityID);
            if (entityID == null) {
                log.debug("Transformed entityID was null");
                return null;
            }
        }
        
        VelocityContext context = new VelocityContext();
        if (encodeEntityID) {
            context.put(CONTEXT_KEY_ENTITY_ID, URISupport.doURLEncode(entityID));
        } else {
            context.put(CONTEXT_KEY_ENTITY_ID, entityID);
        }
        
        try {
            String result = template.merge(context);
            log.debug("From entityID '{}' and template text '{}', built request URL: {}", 
                    entityID, templateText, result);
            return result;
        } catch (Throwable t) {
            log.error("Encountered fatal error attempting to build request URL", t);
            return null;
        }
    }


}
