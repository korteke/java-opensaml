/*
 * Copyright 2010 University Corporation for Advanced Internet Development, Inc.
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
package org.opensaml.messaging.context.impl;

import org.opensaml.messaging.context.Subcontext;
import org.opensaml.messaging.context.SubcontextContainer;

/**
 * Base abstract implementation of {@link Subcontext}.
 */
public abstract class BaseSubcontext implements Subcontext {
    
    /** The owning container. */
    private SubcontextContainer owner;
    
    /**
     * Constructor.
     *
     * @param container the owning subcontext container.
     */
    public BaseSubcontext(SubcontextContainer container) {
        owner = container;
    }
    
    /** {@inheritDoc} */
    public SubcontextContainer getOwner() {
        return owner;
    }
    
}
