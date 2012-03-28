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

package org.opensaml.security.credential.criteria.impl;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.shibboleth.utilities.java.support.resolver.Criterion;

import org.opensaml.core.xml.XMLObjectProviderRegistrySupport;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A registry which manages mappings from types of {@link Criterion} to the class type which can evaluate that criteria's
 * data against a {@link Credential} target. That latter class will be a subtype of {@link EvaluableCredentialCriterion}.
 * Each EvaluableCredentialCriterion implementation that is registered <strong>MUST</strong> implement a single-arg
 * constructor which takes an instance of the Criterion to be evaluated. The evaluable instance is instantiated
 * reflectively based on this requirement.
 */
public final class EvaluableCredentialCriteriaRegistry {

    /**
     * Properties file storing default mappings from criteria to evaluable credential criteria. Will be loaded as a
     * resource stream relative to this class.
     */
    public static final String DEFAULT_MAPPINGS_FILE = "/credential-criteria-registry.properties";

    /** Storage for the registry mappings. */
    private static Map<Class<? extends Criterion>, Class<? extends EvaluableCredentialCriterion>> registry;

    /** Flag to track whether registry is initialized. */
    private static boolean initialized;

    /** Constructor. */
    private EvaluableCredentialCriteriaRegistry() {
    }

    /**
     * Get an instance of EvaluableCredentialCriterion which can evaluate the supplied criteria's requirements against a
     * Credential target.
     * 
     * @param criteria the criteria to be evaluated against a credential
     * @return an instance of of EvaluableCredentialCriterion representing the specified criteria's requirements
     * @throws SecurityException thrown if there is an error reflectively instantiating a new instance of
     *             EvaluableCredentialCriterion based on class information stored in the registry
     */
    public static EvaluableCredentialCriterion getEvaluator(Criterion criteria) throws SecurityException {
        Logger log = getLogger();
        Class<? extends EvaluableCredentialCriterion> clazz = lookup(criteria.getClass());

        if (clazz != null) {
            log.debug("Registry located evaluable criteria class {} for criteria class {}", clazz.getName(), criteria
                    .getClass().getName());

            try {

                Constructor<? extends EvaluableCredentialCriterion> constructor = clazz
                        .getConstructor(new Class[] { criteria.getClass() });

                return constructor.newInstance(new Object[] { criteria });

            } catch (java.lang.SecurityException e) {
                log.error("Error instantiating new EvaluableCredentialCriterion instance", e);
                throw new SecurityException("Could not create new EvaluableCredentialCriterion", e);
            } catch (NoSuchMethodException e) {
                log.error("Error instantiating new EvaluableCredentialCriterion instance", e);
                throw new SecurityException("Could not create new EvaluableCredentialCriterion", e);
            } catch (IllegalArgumentException e) {
                log.error("Error instantiating new EvaluableCredentialCriterion instance", e);
                throw new SecurityException("Could not create new EvaluableCredentialCriterion", e);
            } catch (InstantiationException e) {
                log.error("Error instantiating new EvaluableCredentialCriterion instance", e);
                throw new SecurityException("Could not create new EvaluableCredentialCriterion", e);
            } catch (IllegalAccessException e) {
                log.error("Error instantiating new EvaluableCredentialCriterion instance", e);
                throw new SecurityException("Could not create new EvaluableCredentialCriterion", e);
            } catch (InvocationTargetException e) {
                log.error("Error instantiating new EvaluableCredentialCriterion instance", e);
                throw new SecurityException("Could not create new EvaluableCredentialCriterion", e);
            }

        } else {
            log.debug("Registry could not locate evaluable criteria for criteria class {}", criteria.getClass()
                    .getName());
        }
        return null;
    }

    /**
     * Lookup the class subtype of EvaluableCredentialCriterion which is registered for the specified Criterion class.
     * 
     * @param clazz the Criterion class subtype to lookup
     * @return the registered EvaluableCredentialCriterion class subtype
     */
    public static synchronized Class<? extends EvaluableCredentialCriterion> lookup(Class<? extends Criterion> clazz) {
        return registry.get(clazz);
    }

    /**
     * Register a credential evaluator class for a criteria class.
     * 
     * @param criteriaClass class subtype of {@link Criterion}
     * @param evaluableClass class subtype of {@link EvaluableCredentialCriterion}
     */
    public static synchronized void register(Class<? extends Criterion> criteriaClass,
            Class<? extends EvaluableCredentialCriterion> evaluableClass) {
        Logger log = getLogger();

        log.debug("Registering class {} as evaluator for class {}", evaluableClass.getName(), criteriaClass.getName());

        registry.put(criteriaClass, evaluableClass);

    }

    /**
     * Deregister a criteria-evaluator mapping.
     * 
     * @param criteriaClass class subtype of {@link Criterion}
     */
    public static synchronized void deregister(Class<? extends Criterion> criteriaClass) {
        Logger log = getLogger();

        log.debug("Deregistering evaluator for class {}", criteriaClass.getName());
        registry.remove(criteriaClass);
    }

    /**
     * Clear all mappings from the registry.
     */
    public static synchronized void clearRegistry() {
        Logger log = getLogger();
        log.debug("Clearing evaluable criteria registry");

        registry.clear();
    }

    /**
     * Check whether the registry has been initialized.
     * 
     * @return true if registry is already initialized, false otherwise
     */
    public static synchronized boolean isInitialized() {
        return initialized;
    }

    /**
     * Initialize the registry.
     */
    public static synchronized void init() {
        if (isInitialized()) {
            return;
        }

        registry = new HashMap<Class<? extends Criterion>, Class<? extends EvaluableCredentialCriterion>>();

        loadDefaultMappings();

        initialized = true;
    }

    /**
     * Load the default set of criteria-evaluator mappings from the default mappings properties file.
     */
    public static synchronized void loadDefaultMappings() {
        Logger log = getLogger();
        log.debug("Loading default evaluable credential criteria mappings");
        InputStream inStream = EvaluableCredentialCriteriaRegistry.class.getResourceAsStream(DEFAULT_MAPPINGS_FILE);
        if (inStream == null) {
            log.error(String.format("Could not open resource stream from default mappings file '%s'",
                    DEFAULT_MAPPINGS_FILE));
            return;
        }

        Properties defaultMappings = new Properties();
        try {
            defaultMappings.load(inStream);
        } catch (IOException e) {
            log.error("Error loading properties file from resource stream", e);
            return;
        }

        loadMappings(defaultMappings);
    }

    /**
     * Load a set of criteria-evaluator mappings from the supplied properties set.
     * 
     * @param mappings properies set where the key is the criteria class name, the value is the evaluator class name
     */
    @SuppressWarnings("unchecked")
    public static synchronized void loadMappings(Properties mappings) {
        Logger log = getLogger();
        for (Object key : mappings.keySet()) {
            if (!(key instanceof String)) {
                log.error(String.format("Properties key was not an instance of String, was '%s', skipping...", key
                        .getClass().getName()));
                continue;
            }
            String criteriaName = (String) key;
            String evaluatorName = mappings.getProperty(criteriaName);

            ClassLoader classLoader = XMLObjectProviderRegistrySupport.class.getClassLoader();
            Class criteriaClass = null;
            try {
                criteriaClass = classLoader.loadClass(criteriaName);
            } catch (ClassNotFoundException e) {
                log.error(
                        String.format("Could not find criteria class name '%s', skipping registration", criteriaName),
                        e);
                return;
            }

            Class evaluableClass = null;
            try {
                evaluableClass = classLoader.loadClass(evaluatorName);
            } catch (ClassNotFoundException e) {
                log.error(String
                        .format("Could not find evaluator class name '%s', skipping registration", criteriaName), e);
                return;
            }

            register(criteriaClass, evaluableClass);
        }

    }
    
    /**
     * Get an SLF4J Logger.
     * 
     * @return a Logger instance
     */
    private static Logger getLogger() {
        return LoggerFactory.getLogger(EvaluableCredentialCriteriaRegistry.class);
    }

    static {
        init();
    }
}
