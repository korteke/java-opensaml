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

package org.opensaml.xacml.policy.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

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
        Collection<String> obligationIds = extractObligationIDs(context);
        Iterator<BaseObligationHandler> handlerItr = obligationHandlers.iterator();
        BaseObligationHandler handler;
        while (handlerItr.hasNext()) {
            handler = handlerItr.next();
            if (obligationIds.contains(handler.getObligationId())) {
                handler.evaluateObligation(context);
            }
        }
    }

    /**
     * Extracts ID of the obligations within the effective XACML policy.
     * 
     * @param context current processing context.
     * 
     * @return obligation IDs within the effect XACML policy
     */
    protected Collection<String> extractObligationIDs(ObligationProcessingContext context) {
        ArrayList<String> obligationIds = new ArrayList<String>();

        // TODO

        return obligationIds;
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
