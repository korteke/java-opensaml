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

package org.opensaml.saml.saml1.profile;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.security.IdentifierGenerationStrategy;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml1.core.Assertion;
import org.opensaml.saml.saml1.core.Conditions;
import org.opensaml.saml.saml1.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Helper methods for SAML 1 profile actions. */
public final class SAML1ActionSupport {

    /** Constructor. */
    private SAML1ActionSupport() {

    }

    /**
     * Constructs and adds a {@link Assertion} to the given {@link Response}. The {@link Assertion} is constructed
     * using the parameters supplied, and its issue instant is set to the issue instant of the given {@link Response}.
     * 
     * @param action the current action
     * @param response the response to which the assertion will be added
     * @param idGenerator source of assertion ID
     * @param issuer value for assertion
     * 
     * @return the assertion that was added to the response
     */
    @Nonnull public static Assertion addAssertionToResponse(@Nonnull final AbstractProfileAction action,
            @Nonnull final Response response, @Nonnull final IdentifierGenerationStrategy idGenerator,
            @Nonnull @NotEmpty final String issuer) {

        final SAMLObjectBuilder<Assertion> assertionBuilder = (SAMLObjectBuilder<Assertion>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Assertion>getBuilderOrThrow(
                        Assertion.DEFAULT_ELEMENT_NAME);

        final Assertion assertion = assertionBuilder.buildObject();
        assertion.setID(idGenerator.generateIdentifier());
        assertion.setIssueInstant(response.getIssueInstant());
        assertion.setIssuer(issuer);
        assertion.setVersion(SAMLVersion.VERSION_11);
        
        getLogger().debug("Profile Action {}: Added Assertion {} to Response {}",
                new Object[] {action.getId(), assertion.getID(), response.getID(),});
        response.getAssertions().add(assertion);

        return assertion;
    }

    /**
     * Creates and adds a {@link Conditions} to a given {@link Assertion}. If the {@link Assertion} already contains an
     * {@link Conditions} this method just returns.
     * 
     * @param action current action
     * @param assertion assertion to which the condition will be added
     * 
     * @return the {@link Conditions} that already existed on, or the one that was added to, the {@link Assertion}
     */
    @Nonnull public static Conditions addConditionsToAssertion(@Nonnull final AbstractProfileAction action,
            @Nonnull final Assertion assertion) {
        Conditions conditions = assertion.getConditions();
        if (conditions == null) {
            final SAMLObjectBuilder<Conditions> conditionsBuilder = (SAMLObjectBuilder<Conditions>)
                    XMLObjectProviderRegistrySupport.getBuilderFactory().<Conditions>getBuilderOrThrow(
                            Conditions.DEFAULT_ELEMENT_NAME);
            conditions = conditionsBuilder.buildObject();
            assertion.setConditions(conditions);
            getLogger().debug("Profile Action {}: Assertion {} did not already contain Conditions, added",
                    action.getId(), assertion.getID());
        } else {
            getLogger().debug("Profile Action {}: Assertion {} already contains Conditions, nothing was done",
                    action.getId(), assertion.getID());
        }

        return conditions;
    }

    /**
     * Gets the logger for this class.
     * 
     * @return logger for this class, never null
     */
    @Nonnull private static Logger getLogger() {
        return LoggerFactory.getLogger(SAML1ActionSupport.class);
    }
    
}