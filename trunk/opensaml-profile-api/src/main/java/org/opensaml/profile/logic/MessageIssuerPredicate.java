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

package org.opensaml.profile.logic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.messaging.context.BaseContext;
import org.opensaml.messaging.context.BasicMessageMetadataContext;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/** A {@link Predicate} that checks if the issuer of the message matches a given {@link Predicate}. */
public class MessageIssuerPredicate implements Predicate<BaseContext> {

    /** Strategy used to lookup the {@link BasicMessageMetadataContext} associated with the message. */
    private final Function<BaseContext, BasicMessageMetadataContext> messageMetadataLookupStrategy;

    /** Strategy used to see if the message issuer is acceptable. */
    private final Predicate<String> issuerMatch;

    /**
     * Constructor.
     * 
     * @param lookupStrategy strategy used to lookup the {@link BasicMessageMetadataContext} associated with the message
     * @param issuerMatchingPredicate strategy used to see if the message issuer is acceptable
     */
    public MessageIssuerPredicate(@Nonnull final Function<BaseContext, BasicMessageMetadataContext> lookupStrategy,
            @Nonnull final Predicate<String> issuerMatchingPredicate) {
        messageMetadataLookupStrategy =
                Constraint.isNotNull(lookupStrategy, "Message metadata lookup strategy can not be null");
        issuerMatch = Constraint.isNotNull(issuerMatchingPredicate, "Issuer matching predicate can not be null");
    }

    /** {@inheritDoc} */
    public boolean apply(@Nullable final BaseContext input) {
        if (input == null) {
            return false;
        }

        final BasicMessageMetadataContext msgMdContext = messageMetadataLookupStrategy.apply(input);
        if (msgMdContext != null) {
            return issuerMatch.apply(msgMdContext.getMessageIssuer());
        }

        return false;
    }
}