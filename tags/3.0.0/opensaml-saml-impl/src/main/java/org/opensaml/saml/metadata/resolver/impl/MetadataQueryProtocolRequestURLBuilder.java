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

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.net.URISupport;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;

//TODO reference to protocol spec pending in Javadoc.

/**
 * Function which produces a URL according the the Metadata Query Protocol specification.
 */
public class MetadataQueryProtocolRequestURLBuilder implements Function<String, String> {
    
    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(MetadataQueryProtocolRequestURLBuilder.class);
    
    /** The request base URL per the specification. */
    private String base;
    
    /** Function which transforms the entityID prior to substitution into the URL. */
    private Function<String, String> transformer;
    
    /**
     * Constructor.
     *
     * @param baseURL the base URL for the metadata responder
     */
    public MetadataQueryProtocolRequestURLBuilder(@Nonnull @NotEmpty final String baseURL) {
        this(baseURL, null);
    }
    
    /**
     * Constructor.
     *
     * @param baseURL the base URL for the metadata responder
     * @param transform function which transforms the entityID prior to URL construction substitution, may be null
     */
    public MetadataQueryProtocolRequestURLBuilder(@Nonnull @NotEmpty final String baseURL, 
            @Nullable final Function<String,String> transform) {
        base = Constraint.isNotNull(StringSupport.trimOrNull(baseURL), "Base URL was null or empty");
        if (!base.endsWith("/")) {
            log.debug("Base URL did not end in a trailing '/', one will be added");
            base = base + "/";
        }
        log.debug("Effective base URL value was: {}", base);
        
        transformer = transform;
    }

    /** {@inheritDoc} */
    @Nullable public String apply(@Nonnull String input) {
        String entityID = Constraint.isNotNull(input, "Entity ID was null");
        
        if (transformer != null) {
            entityID = transformer.apply(entityID);
            log.debug("Transformed entityID is '{}'", entityID);
            if (entityID == null) {
                log.debug("Transformed entityID was null");
                return null;
            }
        }
        
        try {
            String result = base +  "entities/" + URISupport.doURLEncode(entityID);
            log.debug("From entityID '{}' and base URL '{}', built request URL: {}", 
                    entityID, base, result);
            return result;
        } catch (Throwable t) {
            log.error("Encountered fatal error attempting to build request URL", t);
            return null;
        }
    }

}
