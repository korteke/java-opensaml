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

package org.opensaml.xml.security;

import net.jcip.annotations.ThreadSafe;

/**
 * An {@link EvaluableCriteria} implementation that always returns the same response.
 * 
 * @param <T> the type of object which may be evaluated
 */
@ThreadSafe
public class StaticResponseEvaluableCritieria<T> implements EvaluableCriteria<T> {

    /** {@link EvaluableCriteria} that always returns {@link Boolean#TRUE}. */
    public static final StaticResponseEvaluableCritieria TRUE_RESPONSE = new StaticResponseEvaluableCritieria(
            Boolean.TRUE);

    /** {@link EvaluableCriteria} that always returns {@link Boolean#FALSE}. */
    public static final StaticResponseEvaluableCritieria FALSE_RESPONSE = new StaticResponseEvaluableCritieria(
            Boolean.FALSE);

    /** {@link EvaluableCriteria} that always returns null. */
    public static final StaticResponseEvaluableCritieria NULL_RESPONSE = new StaticResponseEvaluableCritieria(null);

    /** Response returned for all requests. */
    private final Boolean response;

    /**
     * Constructor.
     * 
     * @param criteriaResponse response from this criteria
     */
    protected StaticResponseEvaluableCritieria(final Boolean criteriaResponse) {
        response = criteriaResponse;
    }

    /** {@inheritDoc} */
    public Boolean evaluate(T target) {
        return response;
    }
}