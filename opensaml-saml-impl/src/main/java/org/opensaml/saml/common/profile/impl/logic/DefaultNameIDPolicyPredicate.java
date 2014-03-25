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

package org.opensaml.saml.common.profile.impl.logic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.saml.common.profile.logic.AbstractNameIDPolicyPredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Policy that requires that qualifiers, if set, match the requester and responder. */
public class DefaultNameIDPolicyPredicate extends AbstractNameIDPolicyPredicate {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(DefaultNameIDPolicyPredicate.class);

    /** {@inheritDoc} */
    @Override
    protected boolean doApply(@Nullable final String requesterId, @Nullable final String responderId,
            @Nullable final String format, @Nullable final String nameQualifier,
            @Nullable final String spNameQualifier) {
        
        if (spNameQualifier != null) {
            if (requesterId == null || !spNameQualifier.equals(requesterId)) {
                log.debug("Requested SPNameQualifier {} did not match requester ID", spNameQualifier);
                return false;
            }
        }
        
        if (nameQualifier != null) {
            if (responderId == null || !nameQualifier.equals(responderId)) {
                log.debug("Requested NameQualifier {} did not match responder ID", nameQualifier);
                return false;
            }
        }
        
        return true;
    }
    
}