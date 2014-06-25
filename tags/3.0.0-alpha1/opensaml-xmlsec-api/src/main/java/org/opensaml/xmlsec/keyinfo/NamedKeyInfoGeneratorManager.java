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

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.collection.LazyMap;

import org.opensaml.security.credential.Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A manager for named sets of {@link KeyInfoGeneratorFactory} instances. Each name key serves as an index to an
 * instance of {@link KeyInfoGeneratorManager}.
 */
public class NamedKeyInfoGeneratorManager {
    
    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(NamedKeyInfoGeneratorManager.class);
    
    /** The set of named factory managers. */
    private final Map<String, KeyInfoGeneratorManager> managers;
    
    /** The default manager for unnamed factories. */
    private final KeyInfoGeneratorManager defaultManager;
    
    /** Flag indicating whether the default (unnamed) factory manager will be used to 
     * lookup factories for credentials. */
    private boolean useDefaultManager;
    
    /** Constructor. */
    public NamedKeyInfoGeneratorManager() {
        managers = new LazyMap<String, KeyInfoGeneratorManager>();
        defaultManager = new KeyInfoGeneratorManager();
        useDefaultManager = true;
    }
    
    /**
     * Set the option as to whether the default (unnamed) manager will be used to lookup factories
     * for credentials if there is no appropriate named factory for the credential type.
     * 
     * @param newValue the new option value
     */
    public void setUseDefaultManager(boolean newValue) {
        useDefaultManager = newValue;
    }
    
    /**
     * Get the (unmodifiable) set of names of factory managers currently available.
     * 
     * @return the set of all manager names currently configured
     */
    @Nonnull public Set<String> getManagerNames() {
        return Collections.unmodifiableSet(managers.keySet());
    }
 
    /**
     * Get the named factory manager.  If it doesn't exist yet, one will be created.
     * 
     * @param name the name of the manager to obtain
     * @return the named manager
     */
    @Nonnull public KeyInfoGeneratorManager getManager(@Nonnull final String name) {
        KeyInfoGeneratorManager manager = managers.get(name);
        if (manager == null) {
            manager = new KeyInfoGeneratorManager();
            managers.put(name, manager);
        }
        return manager;
    }
    
    /**
     * Remove the named factory manager, and all its managed factories.
     * 
     * @param name the name of the manager to remove
     */
    public void removeManager(@Nonnull final String name) {
        managers.remove(name);
    }
    
    /**
     * Register a factory within the specified named manager. If that 
     * named manager does not currently exist, it will be created.
     * 
     * @param name the name of the factory manager
     * @param factory the factory to register
     */
    public void registerFactory(@Nonnull final String name, @Nonnull final KeyInfoGeneratorFactory factory) {
        KeyInfoGeneratorManager manager = getManager(name);
        manager.registerFactory(factory);
    }
    
    /**
     * De-register a factory within the specified named manager.
     * 
     * @param name the name of the factory manager
     * @param factory the factory to de-register
     */
    public void deregisterFactory(@Nonnull final String name, @Nonnull final KeyInfoGeneratorFactory factory) {
        KeyInfoGeneratorManager manager = managers.get(name);
        if (manager == null) {
            throw new IllegalArgumentException("Manager with name '" + name + "' does not exist");
        }
        
        manager.deregisterFactory(factory);
    }

    /**
     * Register a factory with the default (unnamed) manager.
     * 
     * @param factory the factory to register
     */
    public void registerDefaultFactory(@Nonnull final KeyInfoGeneratorFactory factory) {
        defaultManager.registerFactory(factory);
    }
    
    /**
     * De-register a factory with the default (unnamed) manager.
     * 
     * @param factory the factory to de-register
     */
    public void deregisterDefaultFactory(@Nonnull final KeyInfoGeneratorFactory factory) {
        defaultManager.deregisterFactory(factory);
    }
    
    /**
     * Get the default (unnamed) factory manager.
     * 
     * @return the default factory manager
     */
    @Nonnull public KeyInfoGeneratorManager getDefaultManager() {
        return defaultManager;
    }
    
    /**
     * Lookup and return the named generator factory for the type of the credential specified.
     * 
     * @param name the name of the factory manger
     * @param credential the credential to evaluate
     * @return a factory for generators appropriate for the specified credential
     */
    @Nullable public KeyInfoGeneratorFactory getFactory(@Nonnull final String name,
            @Nonnull final Credential credential) {
        KeyInfoGeneratorManager manager = managers.get(name);
        if (manager == null) {
            if (useDefaultManager) {
                log.debug("Manger with name '{}' was not registered, using default manager", name);
                manager = defaultManager;
            } else {
                log.warn("Manager with name '{}' was not registered, and 'useDefaultManager' is false", name);
                return null;
            }
        }
            
        KeyInfoGeneratorFactory factory = manager.getFactory(credential);
        if (factory == null) {
            if (useDefaultManager && manager != defaultManager) {
                log.debug("Factory not found in manager with name '{}', attempting lookup in default manager", name);
                factory = defaultManager.getFactory(credential);
            }
        }
        return factory;
    }

}