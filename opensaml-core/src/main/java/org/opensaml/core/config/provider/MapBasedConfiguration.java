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
package org.opensaml.core.config.provider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.opensaml.core.config.Configuration;

/**
 * A configuration implementation which stores registered configuration instances in a
 * local instance of {@link ConcurrentHashMap}.
 */
public class MapBasedConfiguration implements Configuration {
    
    /** Storage for registered configuration objects. */
    private Map<String, Map<String, Object>> storage;
    
    /** Constructor. */
    public MapBasedConfiguration() {
        storage = new ConcurrentHashMap<String, Map<String, Object>>();
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
    public <T extends Object> T get(Class<T> configClass, String partitionName) {
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
    public <T extends Object, I extends T> void register(Class<T> configClass, I configuration, String partitionName) {
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
    public <T extends Object> T deregister(Class<T> configClass, String partitionName) {
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
    private synchronized Map<String, Object> getPartition(String partitionName) {
        Map<String, Object> partition = storage.get(partitionName);
        if (partition == null) {
            partition = new ConcurrentHashMap<String, Object>();
            storage.put(partitionName, partition);
        }
        return partition;
    }

}
