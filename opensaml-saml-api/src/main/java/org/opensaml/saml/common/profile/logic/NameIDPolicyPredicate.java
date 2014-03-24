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

package org.opensaml.saml.common.profile.logic;

import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.component.InitializableComponent;

import org.opensaml.profile.context.ProfileRequestContext;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * Evaluates a request to produce or consume a SAML name identifier based on the associated qualifiers
 * on the identifier and the parties involved in the transaction.
 * 
 * <p>The chief use of this interface is to control the generation and decoding of transient and persistent
 * identifiers, which have well-defined rules governing the qualifiers and are focused on preserving
 * privacy in specific cases. Other kinds of identifiers may not need these kinds of checks and may simply
 * be associated with specific requesters and responders. That is, policy based on who the requester is
 * belongs elsewhere; this interface is for defining rules that pertain to the relationship between
 * the requester and the qualifier(s).</p>
 */
public interface NameIDPolicyPredicate extends InitializableComponent, Predicate<ProfileRequestContext> {
    
    /**
     * Set the strategy used to locate the requester involved in the check.
     * 
     * @param strategy requester lookup function
     */
    void setRequesterIdLookupStrategy(@Nullable final Function<ProfileRequestContext,String> strategy);

    /**
     * Set the strategy used to locate the responder involved in the check.
     * 
     * @param strategy responder lookup function
     */
    void setResponderIdLookupStrategy(@Nullable final Function<ProfileRequestContext,String> strategy);

    /**
     * Set the NameQualifier involved in the check.
     * 
     * @param strategy NameQualifier lookup function
     */
    void setNameQualifierLookupStrategy(@Nullable final Function<ProfileRequestContext,String> strategy);
    
    /**
     * Set the SPNameQualifier involved in the check.
     * 
     * @param strategy SPNameQualifier lookup function
     */
    void setSPNameQualifierLookupStrategy(@Nullable final Function<ProfileRequestContext,String> strategy);

}