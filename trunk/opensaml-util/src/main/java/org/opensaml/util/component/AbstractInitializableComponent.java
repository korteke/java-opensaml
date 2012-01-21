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

package org.opensaml.util.component;

/** Base class for things that implement {@link InitializableComponent}. */
@Deprecated
public abstract class AbstractInitializableComponent implements InitializableComponent {

    /** Whether this component has been initialized. */
    private boolean isInitialized;

    /** {@inheritDoc} */
    public boolean isInitialized() {
        return isInitialized;
    }

    /** {@inheritDoc} */
    public final synchronized void initialize() throws ComponentInitializationException {
        if (isInitialized()) {
            return;
        }
        
        doInitialize();
        isInitialized = true;
    }

    /**
     * Performs the initialization of the component. This method is executed within the lock on the object being
     * initialized.
     * 
     * The default implementation of this method is a no-op.
     * 
     * @throws ComponentInitializationException thrown if there is a problem initializing the component
     */
    protected void doInitialize() throws ComponentInitializationException {

    }
}