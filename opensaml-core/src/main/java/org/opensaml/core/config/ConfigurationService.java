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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A service which provides for the registration, retrieval and deregistration of objects
 * related to library module configuration. 
 */
public class ConfigurationService {
    
    /** The default storage partition name, if none is specified using configuration properties. */
    public static final String DEFAULT_PARTITION_NAME = "default";
    
    /** The configuration property name for the storage partition name to use. */
    public static final String PROPERTY_PARTITION_NAME = "opensaml.config.partitionName";
    
    /** The service loader used to locate registered implementations of ConfigurationPropertiesSource. */
    private static ServiceLoader<ConfigurationPropertiesSource> configPropertiesLoader = 
        ServiceLoader.load(ConfigurationPropertiesSource.class) ;
    
    /** Storage for registered configuration objects. */
    //TODO this is perhaps temporary pending a possible reimplementation using JNDI 
    private static Map<String, Map<String, Object>> storage = new HashMap<String, Map<String, Object>>();
    
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
        return get(configClass, partitionName);
    }
    
    /**
     * Register a configuration instance.
     * 
     * @param <T> the type of configuration being registered
     * @param <I> the configuration object instance type being registered, which must be an instance of <T>
     * 
     * @param configClass the type of configuration being registered
     * @param configuration the configuration object instance being registered
     */
    public static <T extends Object, I extends T> void register(Class<T> configClass, I configuration) {
        String partitionName = getPartitionName();
        register(configClass, configuration, partitionName);
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
        return deregister(configClass, partitionName);
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
        Iterator<ConfigurationPropertiesSource> iter = configPropertiesLoader.iterator();
        while (iter.hasNext()) {
            ConfigurationPropertiesSource source = iter.next();
            Properties props = source.getProperties();
            if (props != null) {
                log.debug("Resolved non-null configuration properties using implementation: {}", 
                        source.getClass().getName());
                return props;
            }
        }
        log.debug("Unable to resolve non-null configuration properties from any ConfigurationPropertiesSource");
        return null;
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
        log.debug("Resolved effective configuration partition name '{}'", partitionName);
        return partitionName;
    }
    
    /**
     * Obtain the registered configuration instance. 
     * 
     * @param <T> the type of configuration being retrieved, typically an interface
     * 
     * @param configClass the configuration class identifier, typically an interface
     * @param partitionName the partition name to use
     * 
     * @return the instance of the registered configuration interface, or null
     */
    @SuppressWarnings("unchecked")
    private static <T extends Object> T get(Class<T> configClass, String partitionName) {
        Map<String, Object> partition = getPartition(partitionName);
        return (T) partition.get(configClass.getName());
    }
    
    /**
     * Register a configuration instance.
     * 
     * @param <T> the type of configuration being registered, typically an interface
     * @param <I> the configuration implementation being registered, which will be an instance of <T>
     * 
     * @param configClass the type of configuration class being registered, typically an interface
     * @param configuration the configuration implementation instance being registered
     * @param partitionName the partition name to use
     */
    private static <T extends Object, I extends T> void register(Class<T> configClass, I configuration,
            String partitionName) {
        Map<String, Object> partition = getPartition(partitionName);
        partition.put(configClass.getName(), configuration);
    }
    
    /**
     * Deregister a configuration instance.
     * 
     * @param <T> the type of configuration being deregistered, typically an interface
     * 
     * @param configClass the type of configuration class being deregistered , typically an interface
     * @param partitionName the partition name to use
     * 
     * @return the configuration implementation instance which was deregistered, or null
     */
    private static <T extends Object> T deregister(Class<T> configClass, String partitionName) {
        Map<String, Object> partition = getPartition(partitionName);
        synchronized (partition) {
            @SuppressWarnings("unchecked")
            T old = (T) partition.get(configClass.getName());
            partition.remove(configClass.getName());
            return old;
        }
    }
    
    /**
     * Get the Map instance which corresponds to the specified partition name.
     * 
     * @param partitionName the partition name to use
     * 
     * @return the Map corresponding to the partition name.  A new empty Map will be created if necessary
     */
    private static synchronized Map<String, Object> getPartition(String partitionName) {
        Map<String, Object> partition = storage.get(partitionName);
        if (partition == null) {
            partition = new HashMap<String, Object>();
            storage.put(partitionName, partition);
        }
        return partition;
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
