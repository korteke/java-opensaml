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

package org.opensaml.xmlsec.keyinfo;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.security.credential.Credential;

/**
 * A manager for {@link KeyInfoGeneratorFactory} instances.  Factories are uniquely indexed according to the 
 * value returned by {@link KeyInfoGeneratorFactory#getCredentialType()}.
 */
public class KeyInfoGeneratorManager {
    
    /** The factories being managed, indexed by credential type. */
    private final Map<Class<? extends Credential>, KeyInfoGeneratorFactory> factories;
    
    /** Constructor. */
    public KeyInfoGeneratorManager() {
        factories = new HashMap<Class<? extends Credential>, KeyInfoGeneratorFactory>(5);
    }

    /**
     * Register a factory within this manager instance. If a factory already exists
     * for that credential type, it will be replaced.
     * 
     * @param factory the factory to register
     */
    public void registerFactory(@Nonnull final KeyInfoGeneratorFactory factory) {
        factories.put(factory.getCredentialType(), factory);
    }
    
    /**
     * De-register a factory within this manager instance.
     * 
     * @param factory the factory to de-register
     */
    public void deregisterFactory(@Nonnull final KeyInfoGeneratorFactory factory) {
        factories.remove(factory.getCredentialType());
    }
    
    /**
     * Get the (unmodifiable) collection of all factories managed by this manager.
     * 
     * @return the collection of managed factories
     */
    @Nonnull public Collection<KeyInfoGeneratorFactory> getFactories() {
        return Collections.unmodifiableCollection(factories.values());
    }
    
    /**
     * Get the factory which produces KeyInfoGenerators which can handle
     * the specified credential.
     * 
     * @param credential the credential for which to locate a factory
     * @return a KeyInfoGeneratorFactory instance appropriate for the credential
     */
    @Nullable public KeyInfoGeneratorFactory getFactory(@Nonnull final Credential credential) {
        Constraint.isNotNull(credential, "Credential cannot be null");
        
        return factories.get(credential.getCredentialType());
    }

}