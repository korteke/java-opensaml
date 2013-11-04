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

package org.opensaml.core.config;

import java.util.Iterator;
import java.util.ServiceLoader;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service which initializes OpenSAML library modules using the Java Services API.
 * 
 * <p>
 * See also {@link Initializer}.
 * </p>
 */
public class InitializationService {
    
    /** Constructor.*/
    protected InitializationService() { }
    
    /**
     *  Initialize all the registered library modules.
     *  
     * @throws InitializationException  if initialization did not complete successfully
     */
    public static synchronized void initialize() throws InitializationException {
        Logger log = getLogger();
        
        log.info("Initializing OpenSAML using the Java Services API");
        
        ServiceLoader<Initializer> serviceLoader = getServiceLoader();
        Iterator<Initializer> iter = serviceLoader.iterator();
        while (iter.hasNext()) {
            Initializer initializer  = iter.next();
            log.info("Initializing module initializer implementation: {}", initializer.getClass().getName());
            try {
                initializer.init();
            } catch (InitializationException e) {
                log.error("Error initializing module", e);
                throw e;
            }
        }
    }

    /**
     * Obtain the service loader instance used in the initialization process.
     * 
     * @return the service loader instance to use
     */
    private static ServiceLoader<Initializer> getServiceLoader() {
        // TODO ideally would store off loader and reuse on subsequent calls,
        // so inited state in providers would be persisted across calls,
        // avoiding re-initing problems
        // This would take advantage of the caching that the ServiceLoader does
        return ServiceLoader.load(Initializer.class);
    }
    
    /**
     * Get a logger.
     * 
     * @return an SLF4J logger instance
     */
    @Nonnull private static Logger getLogger() {
        return LoggerFactory.getLogger(InitializationService.class);
    }

}