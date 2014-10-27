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
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import com.google.common.base.Function;

/**
 *
 */
public class RegexRequestURLBuilder implements Function<String, String> {
    
    private String pattern;
    
    private String template;
    
    public RegexRequestURLBuilder(@Nonnull @NotEmpty final String regex, @Nonnull @NotEmpty final String replacement) {
        pattern = Constraint.isNotNull(StringSupport.trimOrNull(regex), "Regex was null or empty");
        template = Constraint.isNotNull(StringSupport.trimOrNull(replacement), "Replacement template was null or empty");
    }

    /** {@inheritDoc} */
    @Nullable public String apply(@Nonnull String entityID) {
        //TODO logging, exception handling
        
        //TODO not sure if this approach is fundamentally right
        return entityID.replaceAll(pattern, template);
    }

}
