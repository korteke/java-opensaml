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

package org.opensaml.saml.common.profile.impl;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.annotation.constraint.NullableElements;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.SAMLException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.profile.FormatSpecificNameIdentifierGenerator;
import org.opensaml.saml.common.profile.NameIdentifierGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicates;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.ListMultimap;

/**
 * A compound implementation of the {@link NameIdentifierGenerator} interface that wraps a sequence of
 * candidate generators along with a default to try if no format-specific options are available.
 * 
 * @param <NameIdType> the type of identifier object supported
 */
public class ChainingNameIdentifierGenerator<NameIdType extends SAMLObject>
        implements NameIdentifierGenerator<NameIdType> {
    
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(ChainingNameIdentifierGenerator.class);
    
    /** Map of formats to generators. */
    @Nonnull @NonnullElements private ListMultimap<String,NameIdentifierGenerator<NameIdType>> nameIdGeneratorMap;

    /** Fallback generator, generally for legacy support. */
    @Nullable private NameIdentifierGenerator<NameIdType> defaultNameIdGenerator;
    
    /** Constructor. */
    public ChainingNameIdentifierGenerator() {
        nameIdGeneratorMap = ArrayListMultimap.create();
    }

    /**
     * Set the format-specific generators to use.
     * 
     * <p>Only generators that support the {@link FormatSpecificNameIdentifierGenerator} interface are
     * installed, and the generators are prioritized for a given format by the order they are supplied.</p> 
     * 
     * @param generators generators to use
     */
    public void setGenerators(
            @Nonnull @NullableElements List<NameIdentifierGenerator<NameIdType>> generators) {
        Constraint.isNotNull(generators, "NameIdentifierGenerator list cannot be null");
        
        nameIdGeneratorMap.clear();
        for (final NameIdentifierGenerator<NameIdType> generator
                : Collections2.filter(generators, Predicates.notNull())) {
            if (generator instanceof FormatSpecificNameIdentifierGenerator) {
                nameIdGeneratorMap.put(
                        ((FormatSpecificNameIdentifierGenerator<NameIdType>) generator).getFormat(), generator);
            } else {
                log.warn("Unable to install NameIdentifierGenerator of type {}, not format-specific",
                        generator.getClass().getName());
            }
        }
    }

    /**
     * Set the generator to try if no generator(s) are mapped to a desired format.
     * 
     * @param generator a fallback default generator, if any
     */
    public void setDefaultGenerator(@Nullable final NameIdentifierGenerator<NameIdType> generator) {
        defaultNameIdGenerator = generator;
    }

    /** {@inheritDoc} */
    @Override
    @Nullable public NameIdType generate(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull @NotEmpty final String format) throws SAMLException {
        
        log.debug("Trying to generate identifier with Format {}", format);
        
        List<NameIdentifierGenerator<NameIdType>> generators = nameIdGeneratorMap.get(format);
        if (generators.isEmpty() && defaultNameIdGenerator != null) {
            log.debug("No generators installed for Format {}, trying default/fallback method", format);
            generators = Collections.singletonList(defaultNameIdGenerator);
        }
        
        for (final NameIdentifierGenerator<NameIdType> generator : generators) {
            try {
                final NameIdType nameIdentifier = generator.generate(profileRequestContext, format);
                if (nameIdentifier != null) {
                    log.debug("Successfully generated identifier with Format {}", format);
                    return nameIdentifier;
                }
            } catch (final SAMLException e) {
                log.error("Error while generating identifier", e);
            }
        }
        
        return null;
    }

}