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

/**
 * Base class for {@link EvaluableCriterion} implementations. This base class allows for implementations to determine if
 * {@link EvaluationException} should be thrown or, in the event of an exception, a particular "bias" should be
 * returned. That is to say, if the a criterion is biased towards <code>false</code>, then that is returned in the event
 * of the exception and similarly for criterion biased towards <code>true</code> and <code>null</code>.
 * 
 * @param <T> the type of object which may be evaluated
 */
public abstract class AbstractBiasedEvaluableCriterion<T> implements EvaluableCriterion<T> {

    /** Whether {@link EvaluationException} should be propagated up or swallowed and the bias returned. */
    private boolean propagatingEvaluationException;

    /** The bias used in place of a {@link EvaluationException} if the exception is not propagated. */
    private Boolean bias;

    /**
     * Gets whether {@link EvaluationException} should be propagated up or swallowed and the bias returned.
     * 
     * @return whether {@link EvaluationException} should be propagated up or swallowed and the bias returned
     */
    public boolean isPropagatingEvaluationException() {
        return propagatingEvaluationException;
    }

    /**
     * Sets whether {@link EvaluationException} should be propagated up or swallowed and the bias returned.
     * 
     * @param isPropagating whether {@link EvaluationException} should be propagated up or swallowed and the bias
     *            returned
     */
    protected void setPropagatingEvaluationException(boolean isPropagating) {
        propagatingEvaluationException = isPropagating;
    }

    /**
     * Gets the bias used in place of a {@link EvaluationException} if the exception is not propagated.
     * 
     * @return the bias used in place of a {@link EvaluationException} if the exception is not propagated
     */
    public Boolean getBias() {
        return bias;
    }

    /**
     * Sets the the bias used in place of a {@link EvaluationException} if the exception is not propagated.
     * 
     * @param criterionBias the bias used in place of a {@link EvaluationException} if the exception is not propagated
     */
    protected void setBias(Boolean criterionBias) {
        bias = criterionBias;
    }

    /** {@inheritDoc} */
    public Boolean evaluate(T target) throws EvaluationException {

        try {
            return doEvaluate(target);
        } catch (EvaluationException e) {
            if (propagatingEvaluationException) {
                throw e;
            } else {
                return bias;
            }
        }
    };

    /**
     * Evaluates this criterion against the given target.
     * 
     * The result of evaluation is one of the following values:
     * <ul>
     * <li><code>Boolean.TRUE</code> if the target satisfies the criteria</li>
     * <li><code>Boolean.FALSE</code> if the target does not satisfy criteria</li>
     * <li><code>null</code> if the target can not be evaluated against the criteria</li>
     * </ul>
     * 
     * @param target the target, may be null
     * 
     * @return the result of the evaluation
     * 
     * @throws EvaluationException thrown if there is a problem evaluating this criterion
     */
    protected abstract Boolean doEvaluate(T target) throws EvaluationException;
}