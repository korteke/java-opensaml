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

package org.opensaml.messaging.context.navigate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.messaging.context.BaseContext;

/**
 * A {@link ContextDataLookupFunction} that recurses up the parent chain of the input via 
 * {@link BaseContext#getParent()}, and returns the first parent context that is an instance 
 * of the specified type.
 * 
 * @param <StartContext> type of starting context
 * @param <ParentContext> type of the parent context
 */
public class RecursiveTypedParentContextLookup<StartContext extends BaseContext, ParentContext extends BaseContext>
        implements ContextDataLookupFunction<StartContext, ParentContext> {
    
    /** The target parent class. */
    private Class<ParentContext> parentClass;
    
    /**
     * Constructor.
     *
     * @param targetClass the target parent class 
     */
    public RecursiveTypedParentContextLookup(@Nonnull final Class<ParentContext> targetClass) {
        parentClass = Constraint.isNotNull(targetClass, "Parent Class may not be null");
    }

    /** {@inheritDoc} */
    @Override
    @Nullable public ParentContext apply(@Nullable final StartContext input) {
        if (input == null) {
            return null;
        }
        
        BaseContext current = input.getParent();
        while (current != null) {
            if (parentClass.isInstance(current)) {
                return (ParentContext) current;
            }
            current = current.getParent();
        }
        
        return null;
    }
}