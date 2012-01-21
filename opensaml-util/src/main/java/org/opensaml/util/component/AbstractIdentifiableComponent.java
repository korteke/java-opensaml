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

import org.opensaml.util.Assert;
import org.opensaml.util.StringSupport;

/**
 * Simple implementation of {@link IdentifiableComponent}.
 * 
 * Note, while this implementation's {@link #setId(String)} is protected it can always be hoisted to a public method if
 * that is desired.
 */
@Deprecated
public abstract class AbstractIdentifiableComponent implements IdentifiableComponent {

    /** The unique identifier for this component. */
    private String id;

    /**
     * Gets the unique identifier for this component.
     * 
     * @return unique identifier for this component
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier for this component.
     * 
     * @param componentId unique identifier for this component, can not be null or empty
     */
    protected void setId(String componentId) {
        id = Assert.isNotNull(StringSupport.trimOrNull(componentId), "Component ID can not be null or empty");
    }
}
