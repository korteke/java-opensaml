/*
 * Copyright 2008 Members of the EGEE Collaboration.
 * Copyright 2008 University Corporation for Advanced Internet Development, Inc.
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

package org.opensaml.xacml.ctx.provider;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.opensaml.xacml.ctx.DecisionType.DECISION;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.ObligationType;
import org.opensaml.xacml.policy.ObligationsType;

/** A service for evaluating the obligations within a context. */
public class ObligationService {

    /** Registered obligation handlers. */
    private Set<BaseObligationHandler> obligationHandlers;

    /** Constructor. */
    public ObligationService() {
        obligationHandlers = new TreeSet<BaseObligationHandler>(new ObligationHandlerComparator());
    }

    /**
     * Processes the obligations within the effective XACML policy.
     * 
     * @param context current processing context
     * 
     * @throws ObligationProcessingException thrown if there is a problem evaluating an obligation
     */
    public void processObligations(ObligationProcessingContext context) throws ObligationProcessingException {
        Iterator<BaseObligationHandler> handlerItr = obligationHandlers.iterator();
        Map<String, ObligationType> effectiveObligations = preprocessObligations(context);

        BaseObligationHandler handler;
        while (handlerItr.hasNext()) {
            handler = handlerItr.next();
            if (effectiveObligations.containsKey(handler.getObligationId())) {
                handler.evaluateObligation(context, effectiveObligations.get(handler.getObligationId()));
            }
        }
    }

    /**
     * Preprocesses the obligations returned within the result. This preprocessing determines the active effect, based
     * on {@link org.opensaml.xacml.ctx.ResultType#getDecision()}, and creates an index that maps obligation IDs to the
     * {@link ObligationType} returned by the PDP.
     * 
     * @param context current processing context
     * 
     * @return preprocessed obligations
     */
    protected Map<String, ObligationType> preprocessObligations(ObligationProcessingContext context) {
        HashMap<String, ObligationType> effectiveObligations = new HashMap<String, ObligationType>();

        ObligationsType obligations = context.getAuthorizationDecisionResult().getObligations();
        if (obligations == null || obligations.getObligations() == null) {
            return effectiveObligations;
        }

        EffectType activeEffect;
        if (context.getAuthorizationDecisionResult().getDecision().getDecision() == DECISION.Permit) {
            activeEffect = EffectType.Permit;
        } else {
            activeEffect = EffectType.Deny;
        }

        for (ObligationType obligation : obligations.getObligations()) {
            if (obligation != null && obligation.getFulfillOn() == activeEffect) {
                effectiveObligations.put(obligation.getObligationId(), obligation);
            }
        }

        return effectiveObligations;
    }

    /** Comparator used to order obligation handlers by precedence. */
    private class ObligationHandlerComparator implements Comparator<BaseObligationHandler> {

        /** {@inheritDoc} */
        public int compare(BaseObligationHandler o1, BaseObligationHandler o2) {
            if (o1.getHandlerPrecedence() == o2.getHandlerPrecedence()) {
                return 0;
            }

            if (o1.getHandlerPrecedence() < o2.getHandlerPrecedence()) {
                return -1;
            }

            return 1;
        }

    }
}