/*
 * Copyright 2011 University Corporation for Advanced Internet Development, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opensaml.core.config;

import java.util.Iterator;
import java.util.Properties;
import java.util.ServiceLoader;

import org.opensaml.core.config.provider.MapBasedConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A service which provides for the registration, retrieval and deregistration of objects
 * related to library module configuration. 
 * 
 * <p>
 * The service uses an internally-managed instance of {@link Configuration} to handle
 * the registration, retrieval and deregistration of the configuration objects under its
 * management.
 * </p>
 * 
 * <p>
 * The service first attempts to use the Java Services API to resolve the instance 
 * of Configuration to use. If multiple implementations of Configuration are registered
 * via the Services API mechanism, the first one returned by the {@link ServiceLoader} iterator
 * is used.  If no Configuration implementation is declared or resolvable using 
 * the Services API, then it uses the default implementation {@link MapBasedConfiguration}.
 * </p>
 * 
 * <p>
 * The Configuration instance to use may also be set externally via {@link #setConfiguration(Configuration)}.
 * This may be useful where an application-specific means such as Spring is used to configure the environment.
 * This overrides the resolution process described above.
 * </p>
 */
public class ConfigurationService {
    
    /** The default storage partition name, if none is specified using configuration properties. */
    public static final String DEFAULT_PARTITION_NAME = "default";
    
    /** The configuration property name for the storage partition name to use. */
    public static final String PROPERTY_PARTITION_NAME = "opensaml.config.partitionName";
    
    /** The service loader used to locate registered implementations of ConfigurationPropertiesSource. */
    private static ServiceLoader<ConfigurationPropertiesSource> configPropertiesLoader = 
        ServiceLoader.load(ConfigurationPropertiesSource.class) ;
    
    /** The configuration instance to use. */
    private static Configuration configuration;
    
    /** Constructor. */
    protected ConfigurationService() { }
    
    /**
     * Obtain the registered configuration instance. 
     * 
     * @param <T> the type of configuration being retrieved
     * 
     * @param configClass the configuration class identifier
     * 
     * @return the instance of the registered configuration object, or null
     */
    public static <T extends Object> T get(Class<T> configClass) {
        String partitionName = getPartitionName();
        return getConfiguration().get(configClass, partitionName);
    }
    
    /**
     * Register a configuration instance.
     * 
     * @param <T> the type of configuration being registered
     * @param <I> the configuration object instance type being registered, which must be an instance of <T>
     * 
     * @param configClass the type of configuration being registered
     * @param configInstance the configuration object instance being registered
     */
    public static <T extends Object, I extends T> void register(Class<T> configClass, I configInstance) {
        String partitionName = getPartitionName();
        getConfiguration().register(configClass, configInstance, partitionName);
    }

    /**
     * Deregister a configuration instance.
     * 
     * @param <T> the type of configuration being deregistered
     * 
     * @param configClass the type of configuration class being deregistered
     * 
     * @return the configuration object instance which was deregistered, or null
     */
    public static <T extends Object> T deregister(Class<T> configClass) {
        String partitionName = getPartitionName();
        return getConfiguration().deregister(configClass, partitionName);
    }
    
    /**
     * Get the set of configuration meta-properties, which determines the configuration of the configuration
     * service itself.
     * 
     * <p>
     * The properties set is obtained from the first registered instance of 
     * {@link ConfigurationPropertiesSource} which returns a non-null properties set.
     * The implementations of properties sources to use
     * are obtained via the Java Services API.
     * </p>
     * 
     * <p>
     * Properties made available in this meta-properties set may also be used by {@link Initializer} 
     * implementations.
     * </p>
     * 
     * @return the set of configuration meta-properties
     */
    public static Properties getConfigurationProperties() {
        //TODO make these immutable?
        Logger log = getLogger();
        log.trace("Resolving configuration propreties source");
        Iterator<ConfigurationPropertiesSource> iter = configPropertiesLoader.iterator();
        while (iter.hasNext()) {
            ConfigurationPropertiesSource source = iter.next();
            log.trace("Evaluating configuration properties implementation: {}", source.getClass().getName());
            Properties props = source.getProperties();
            if (props != null) {
                log.trace("Resolved non-null configuration properties using implementation: {}", 
                        source.getClass().getName());
                return props;
            }
        }
        log.trace("Unable to resolve non-null configuration properties from any ConfigurationPropertiesSource");
        return null;
    }
    
    /**
     * Set the {@link Configuration} instance to use.
     * 
     * <p>
     * The configuration instance to use is normally resolved via the Java Services API,
     * or is defaulted.  However, this method is provided to allow the configuration
     * instance to be supplied externally, perhaps using an application-specific
     * means such as Spring dependency injection.
     * </p>
     * 
     * @param newConfiguration the Configuration instance to use
     */
    public static void setConfiguration(Configuration newConfiguration) {
        configuration = newConfiguration;
    }
    
    /**
     * Return the partition name which will be used for storage of configuration objects.
     * 
     * <p>
     * This partition name is obtained from the configuration meta-properties.  If a value is not supplied
     * via that mechanism, then an internal default value is used.
     * </p>
     * 
     * @return the partition name
     */
    protected static String getPartitionName() {
        Logger log = getLogger();
        Properties configProperties = getConfigurationProperties();
        String partitionName = null;
        if (configProperties != null) {
            partitionName = configProperties.getProperty(PROPERTY_PARTITION_NAME, DEFAULT_PARTITION_NAME);
        } else {
            partitionName = DEFAULT_PARTITION_NAME;
        }
        log.trace("Resolved effective configuration partition name '{}'", partitionName);
        return partitionName;
    }

    /**
     * Get the {@link Configuration} instance to use.
     * 
     * <p>
     * The implementation to return is first resolved using the Java Services API.
     * If this produces no implementation, then an instance of the default implementation
     * of {@link MapBasedConfiguration} is used.
     * </p>
     * 
     * @return the Configuration implementation instance 
     */
    protected static Configuration getConfiguration() {
        if (configuration == null) {
            synchronized (ConfigurationService.class) {
                ServiceLoader<Configuration> loader = ServiceLoader.load(Configuration.class);
                Iterator<Configuration> iter = loader.iterator();
                if (iter.hasNext()) {
                    configuration = iter.next();
                } else {
                    // Default impl
                    configuration = new MapBasedConfiguration();
                }
            }
        }
        return configuration;
    }
    
    /**
     * Get a logger.
     * 
     * @return an SLF4J logger instance
     */
    private static Logger getLogger() {
        return LoggerFactory.getLogger(ConfigurationService.class);
    }
    
}
