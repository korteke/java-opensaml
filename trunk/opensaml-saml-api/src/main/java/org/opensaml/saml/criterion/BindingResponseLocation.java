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

package org.opensaml.saml.criterion;

import org.opensaml.util.Assert;
import org.opensaml.util.StringSupport;
import org.opensaml.util.criteria.Criterion;

/** {@link Criterion} representing a SAML binding resonse location. */
public final class BindingResponseLocation implements Criterion {

    /** The binding response location URI. */
    private final String location;

    /**
     * Constructor.
     * 
     * @param responseLocationUri the binding response location URI, never null or empty
     */
    public BindingResponseLocation(String responseLocationUri) {
        location = StringSupport.trimOrNull(responseLocationUri);
        Assert.isNotNull(location, "Response location can not be null or empty");
    }

    /**
     * Gets the binding response location URI.
     * 
     * @return the binding response location URI, never null or empty
     */
    public String getLocation() {
        return location;
    }
}