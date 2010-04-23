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

import org.joda.time.DateTime;

/** Interface for a component which represents the context used to store state used for purposes 
 * related to messaging. */
public interface Context {
    
    // TODO serializable here or in sub-interfaces
    
    /**
     * Get the context's unique identifier.
     * 
     * @return the context identifier
     */
    public String getId();
    
    /**
     * Get the timestamp of the creation of the context.
     * 
     * @return the creation timestamp
     */
    public DateTime getCreationTime();
    
}
