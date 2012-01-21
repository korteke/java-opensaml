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

package org.opensaml.util.criteria;

import net.jcip.annotations.ThreadSafe;

/**
 * An {@link EvaluableCriterion} implementation that always returns the same response.
 * 
 * @param <T> the type of object which may be evaluated
 */
@ThreadSafe
@Deprecated
public class StaticResponseEvaluableCriterion<T> implements EvaluableCriterion<T> {

    /** {@link EvaluableCriterion} that always returns {@link Boolean#TRUE}. */
    public static final StaticResponseEvaluableCriterion TRUE_RESPONSE = new StaticResponseEvaluableCriterion(
            Boolean.TRUE);

    /** {@link EvaluableCriterion} that always returns {@link Boolean#FALSE}. */
    public static final StaticResponseEvaluableCriterion FALSE_RESPONSE = new StaticResponseEvaluableCriterion(
            Boolean.FALSE);

    /** {@link EvaluableCriterion} that always returns null. */
    public static final StaticResponseEvaluableCriterion NULL_RESPONSE = new StaticResponseEvaluableCriterion(null);

    /** Response returned for all requests. */
    private final Boolean response;

    /**
     * Constructor.
     * 
     * @param criteriaResponse response from this criteria
     */
    protected StaticResponseEvaluableCriterion(final Boolean criteriaResponse) {
        response = criteriaResponse;
    }

    /** {@inheritDoc} */
    public Boolean evaluate(T target) {
        return response;
    }
}