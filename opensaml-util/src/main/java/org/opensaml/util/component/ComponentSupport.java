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

/** Support class for working with {@link Component} objects. */
public final class ComponentSupport {

    /** Constructor. */
    private ComponentSupport() {
    }

    /**
     * If the given object is not null and an instance of {@link DestructableComponent}, then this method calls the
     * given object's {@link DestructableComponent#destroy()} method.
     * 
     * @param obj object to destroy, may be null
     */
    public static void destroy(Object obj) {
        if (obj == null) {
            return;
        }

        if (obj instanceof DestructableComponent) {
            ((DestructableComponent) obj).destroy();
        }
    }

    /**
     * If the given object is not null and an instance of {@link InitializableComponent}, then this method calls the
     * given object's {@link InitializableComponent#initialize()} method.
     * 
     * @param obj object to initialize, may be null
     * 
     * @throws ComponentInitializationException thrown if there is a problem initializing the object
     */
    public static void initialize(Object obj) throws ComponentInitializationException {
        if (obj == null) {
            return;
        }

        if (obj instanceof InitializableComponent) {
            ((InitializableComponent) obj).initialize();
        }
    }

    /**
     * If the given object is not null and an instance of {@link ValidatableComponent}, then this method calls the given
     * object's {@link ValidatableComponent#validate()} method.
     * 
     * @param obj object to validate, may be null
     * 
     * @throws ComponentValidationException thrown if there is a problem validating the object
     */
    public static void validate(Object obj) throws ComponentValidationException {
        if (obj == null) {
            return;
        }

        if (obj instanceof ValidatableComponent) {
            ((ValidatableComponent) obj).validate();
        }
    }
}