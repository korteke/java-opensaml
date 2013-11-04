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

package org.opensaml.core.xml;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.xml.DomTypeSupport;
import net.shibboleth.utilities.java.support.xml.QNameSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * A factory for {@link org.opensaml.core.xml.XMLObjectBuilder}s. XMLObjectBuilders are stored and retrieved by a
 * {@link javax.xml.namespace.QName} key. This key is either the XML Schema Type or element QName of the XML element the
 * built XMLObject object represents.
 */
public class XMLObjectBuilderFactory {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(XMLObjectBuilderFactory.class);

    /** Registered builders. */
    @Nonnull private final Map<QName, XMLObjectBuilder<?>> builders;

    /** Constructor. */
    public XMLObjectBuilderFactory() {
        builders = new ConcurrentHashMap<>();
    }

    /**
     * Retrieves an {@link XMLObjectBuilder} using the key it was registered with.
     * 
     * @param key the key used to register the builder
     * 
     * @return the builder, or null
     */
    @Nullable public XMLObjectBuilder<?> getBuilder(@Nullable final QName key) {
        if(key == null){
            return null;
        }
        return builders.get(key);
    }

    /**
     * Retrieves the XMLObject builder for the given element. The schema type, if present, is tried first as the key
     * with the element QName used if no schema type is present or does not have a builder registered under it.
     * 
     * @param domElement the element to retrieve the builder for
     * 
     * @return the builder for the XMLObject the given element can be unmarshalled into, or null
     */
    @Nullable public XMLObjectBuilder<?> getBuilder(@Nullable final Element domElement) {

        XMLObjectBuilder<?> builder = getBuilder(DomTypeSupport.getXSIType(domElement));

        if (builder == null) {
            builder = getBuilder(QNameSupport.getNodeQName(domElement));
        }

        return builder;
    }

    /**
     * Gets an immutable list of all the builders currently registered.
     * 
     * @return list of all the builders currently registered
     */
    @Nonnull public Map<QName, XMLObjectBuilder<?>> getBuilders() {
        return Collections.unmodifiableMap(builders);
    }

    /**
     * Registers a new builder for the given name.
     * 
     * @param builderKey the key used to retrieve this builder later
     * @param builder the builder
     */
    public void registerBuilder(@Nonnull final QName builderKey, @Nonnull final XMLObjectBuilder<?> builder) {
        Constraint.isNotNull(builderKey, "Builder key cannot be null");
        Constraint.isNotNull(builder, "Builder cannot be null");
        log.debug("Registering builder {} under key {}",  builder.getClass().getName(), builderKey);

        builders.put(builderKey, builder);
    }

    /**
     * Deregisters a builder.
     * 
     * @param builderKey the key for the builder to be deregistered
     * 
     * @return the builder that was registered for the given QName
     */
    @Nullable public XMLObjectBuilder<?> deregisterBuilder(@Nonnull final QName builderKey) {
        Constraint.isNotNull(builderKey, "Builder key QName cannot be null");
        
        log.debug("Deregistering builder for object type {}", builderKey);
        return builders.remove(builderKey);
    }
}