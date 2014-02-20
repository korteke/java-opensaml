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

import javax.annotation.Nullable;

import org.opensaml.messaging.context.BaseContext;

/**
 * A {@link ContextDataLookupFunction} that gets the root of context tree.
 * 
 * @param <StartContext> type of the starting context
 * @param <RootContext> type of the root context
 */
public class RootContextLookup<StartContext extends BaseContext, RootContext extends BaseContext> implements
        ContextDataLookupFunction<StartContext, RootContext> {

    /** {@inheritDoc} */
    @Override
    @Nullable public RootContext apply(@Nullable final BaseContext input) {
        if (input == null) {
            return null;
        }

        if (input.getParent() == null){
            return (RootContext) input;
        } else {
            return apply(input.getParent());
        }
    }
}