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

package org.opensaml.saml.common.assertion;

import java.util.Collections;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import net.shibboleth.utilities.java.support.collection.LazyMap;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

/**
 * Context which holds state related to a single validation event.
 */
@NotThreadSafe
public class ValidationContext {

    /** Static parameters used as input to the validation process. */
    private Map<String, Object> staticParameters;

    /** Dynamic parameters used as input to, and output from, the validation process. */
    private Map<String, Object> dynamicParameters;

    /** Error messaging describing what validation check an assertion failed. */
    private String validationFailureMessage;

    /** Constructor. Creates a validation context with no global environment. */
    public ValidationContext() {
        this(null);
    }

    /**
     * Constructor.
     * 
     * @param newStaticParameters static parameters for the validation evaluation
     */
    public ValidationContext(Map<String, Object> newStaticParameters) {
        if (newStaticParameters == null) {
            staticParameters = Collections.unmodifiableMap(Collections.EMPTY_MAP);
        } else {
            staticParameters = Collections.unmodifiableMap(newStaticParameters);
        }
        dynamicParameters = new LazyMap<String, Object>();
    }

    /**
     * Gets the static parameters used as input to the validation process. The returned map is immutable.
     * 
     * @return static parameters used as input to the validation process
     */
    public Map<String, Object> getStaticParameters() {
        return staticParameters;
    }

    /**
     * Gets the dynamic parameters used input to, and output from, the validation process. The returned map is mutable.
     * 
     * @return dynamic parameters used input to, and output from, the validation process
     */
    public Map<String, Object> getDynamicParameters() {
        return dynamicParameters;
    }

    /**
     * Gets the message describing why the validation process failed.
     * 
     * @return message describing why the validation process failed
     */
    public String getValidationFailureMessage() {
        return validationFailureMessage;
    }

    /**
     * Sets the message describing why the validation process failed.
     * 
     * @param message message describing why the validation process failed
     */
    public void setValidationFailureMessage(String message) {
        validationFailureMessage = StringSupport.trimOrNull(message);
    }
}