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
package org.opensaml.messaging.context;

/**
 * A subcontext is associated with a parent owning container.  It may represent state related to that owner,
 * or may encapsulate operational or functional logic that operates on the parent, such as
 * providing a "view" onto the parent's data.
 * 
 */
public interface Subcontext {
    
    /**
     * Get the owning subcontext container.
     * 
     * @return the owning container
     */
    public SubcontextContainer getOwner();
    
}
