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

package org.opensaml.saml.saml1.profile.impl;

import org.opensaml.core.OpenSAMLInitBaseTestCase;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.profile.RequestContextBuilder;
import org.opensaml.profile.action.ActionTestingSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.saml1.core.Assertion;
import org.opensaml.saml.saml1.core.Conditions;
import org.opensaml.saml.saml1.core.DoNotCacheCondition;
import org.opensaml.saml.saml1.core.Response;
import org.opensaml.saml.saml1.profile.SAML1ActionTestingSupport;
import org.testng.Assert;
import org.testng.annotations.Test;

/** {@link AddDoNotCacheConditionToAssertions} unit test. */
public class AddDoNotCacheConditionToAssertionsTest extends OpenSAMLInitBaseTestCase {

    /** Test that action errors out properly if there is no response. */
    @Test public void testNoResponse() throws Exception {
        final ProfileRequestContext prc = new RequestContextBuilder().buildProfileRequestContext();
        final AddDoNotCacheConditionToAssertions action = new AddDoNotCacheConditionToAssertions();
        action.initialize();

        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }

    /** Test that action errors out properly if there is no assertion in the response. */
    @Test public void testNoAssertion() throws Exception {
        final ProfileRequestContext prc = new RequestContextBuilder().setOutboundMessage(
                SAML1ActionTestingSupport.buildResponse()).buildProfileRequestContext();

        final AddDoNotCacheConditionToAssertions action = new AddDoNotCacheConditionToAssertions();
        action.initialize();

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
    }

    /**
     * Test that the condition is properly added if there is a single assertion, without a Conditions element, in the
     * response.
     */
    @Test public void testSingleAssertion() throws Exception {
        final Assertion assertion = SAML1ActionTestingSupport.buildAssertion();

        final Response response = SAML1ActionTestingSupport.buildResponse();
        response.getAssertions().add(assertion);

        final ProfileRequestContext prc = new RequestContextBuilder().setOutboundMessage(response).buildProfileRequestContext();

        final AddDoNotCacheConditionToAssertions action = new AddDoNotCacheConditionToAssertions();
        action.initialize();

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        Assert.assertNotNull(response.getAssertions());
        Assert.assertEquals(response.getAssertions().size(), 1);

        Assert.assertNotNull(assertion.getConditions());
        Assert.assertNotNull(assertion.getConditions().getDoNotCacheConditions());
        Assert.assertEquals(assertion.getConditions().getDoNotCacheConditions().size(), 1);
    }

    /**
     * Test that the condition is properly added if there is a single assertion, with a Conditions element, in the
     * response.
     */
    @Test public void testSingleAssertionWithExistingCondition() throws Exception {
        final SAMLObjectBuilder<Conditions> conditionsBuilder =
                (SAMLObjectBuilder<Conditions>) XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(
                        Conditions.DEFAULT_ELEMENT_NAME);
        final Conditions conditions = conditionsBuilder.buildObject();

        final Assertion assertion = SAML1ActionTestingSupport.buildAssertion();
        assertion.setConditions(conditions);

        final Response response = SAML1ActionTestingSupport.buildResponse();
        response.getAssertions().add(assertion);

        final ProfileRequestContext prc = new RequestContextBuilder().setOutboundMessage(response).buildProfileRequestContext();

        final AddDoNotCacheConditionToAssertions action = new AddDoNotCacheConditionToAssertions();
        action.initialize();

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        Assert.assertNotNull(assertion.getConditions());
        Assert.assertNotNull(assertion.getConditions().getDoNotCacheConditions());
        Assert.assertEquals(assertion.getConditions().getDoNotCacheConditions().size(), 1);
    }

    /** Test that an addition DoNotCache is not added if an assertion already contains one. */
    @Test public void testSingleAssertionWithExistingDoNotCacheCondition() throws Exception {
        final SAMLObjectBuilder<DoNotCacheCondition> dncConditionBuilder =
                (SAMLObjectBuilder<DoNotCacheCondition>) XMLObjectProviderRegistrySupport.getBuilderFactory()
                        .getBuilder(DoNotCacheCondition.DEFAULT_ELEMENT_NAME);
        final DoNotCacheCondition dncCondition = dncConditionBuilder.buildObject();

        final SAMLObjectBuilder<Conditions> conditionsBuilder =
                (SAMLObjectBuilder<Conditions>) XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(
                        Conditions.DEFAULT_ELEMENT_NAME);
        final Conditions conditions = conditionsBuilder.buildObject();
        conditions.getDoNotCacheConditions().add(dncCondition);

        final Assertion assertion = SAML1ActionTestingSupport.buildAssertion();
        assertion.setConditions(conditions);

        final Response response = SAML1ActionTestingSupport.buildResponse();
        response.getAssertions().add(assertion);

        final ProfileRequestContext prc = new RequestContextBuilder().setOutboundMessage(response).buildProfileRequestContext();

        final AddDoNotCacheConditionToAssertions action = new AddDoNotCacheConditionToAssertions();
        action.initialize();

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        Assert.assertNotNull(assertion.getConditions());
        Assert.assertNotNull(assertion.getConditions().getDoNotCacheConditions());
        Assert.assertEquals(assertion.getConditions().getDoNotCacheConditions().size(), 1);
    }

    /** Test that the condition is properly added if there are multiple assertions in the response. */
    @Test public void testMultipleAssertion() throws Exception {
        final Response response = SAML1ActionTestingSupport.buildResponse();
        response.getAssertions().add(SAML1ActionTestingSupport.buildAssertion());
        response.getAssertions().add(SAML1ActionTestingSupport.buildAssertion());
        response.getAssertions().add(SAML1ActionTestingSupport.buildAssertion());

        final ProfileRequestContext prc = new RequestContextBuilder().setOutboundMessage(response).buildProfileRequestContext();

        final AddDoNotCacheConditionToAssertions action = new AddDoNotCacheConditionToAssertions();
        action.initialize();

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        Assert.assertNotNull(response.getAssertions());
        Assert.assertEquals(response.getAssertions().size(), 3);

        for (final Assertion assertion : response.getAssertions()) {
            Assert.assertNotNull(assertion.getConditions());
            Assert.assertNotNull(assertion.getConditions().getDoNotCacheConditions());
            Assert.assertEquals(assertion.getConditions().getDoNotCacheConditions().size(), 1);
        }
    }
    
}