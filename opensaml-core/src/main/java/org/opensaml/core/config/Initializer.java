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

/**
 * Interface for classes responsible for initializing some bit of library configuration or state.
 * 
 * <p>
 * An initializer would typically be called by invoking the {@link InitializationService}, which in turn
 * locates the registered initializers via the Java Services API.
 * </p>
 */
public interface Initializer {
    
    /**
     * Perform the initialization process encompassed by the implementation.
     * 
     * @throws InitializationException if initialization could not be completed successfully
     */
    public void init() throws InitializationException;

}
