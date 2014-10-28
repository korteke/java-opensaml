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

package org.opensaml.saml.common.profile.impl;

import java.util.Collection;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.opensaml.core.OpenSAMLInitBaseTestCase;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.profile.RequestContextBuilder;
import org.opensaml.profile.action.ActionTestingSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.profile.impl.AddAudienceRestrictionToAssertions;
import org.opensaml.saml.saml1.core.Assertion;
import org.opensaml.saml.saml1.core.AudienceRestrictionCondition;
import org.opensaml.saml.saml1.core.Conditions;
import org.opensaml.saml.saml1.core.Response;
import org.opensaml.saml.saml1.profile.SAML1ActionTestingSupport;
import org.opensaml.saml.saml2.core.AudienceRestriction;
import org.opensaml.saml.saml2.profile.SAML2ActionTestingSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

/** {@link AddAudienceRestrictionToAssertions} unit test. */
public class AddAudienceRestrictionToAssertionsTest extends OpenSAMLInitBaseTestCase {

    private static final String AUDIENCE1 = "foo";
    private static final String AUDIENCE2 = "foo2";
    
    private AddAudienceRestrictionToAssertions action;
    
    @BeforeMethod public void setUp() throws ComponentInitializationException {
        action = new AddAudienceRestrictionToAssertions();
        action.setAudienceRestrictionsLookupStrategy(new Function<ProfileRequestContext,Collection<String>>() {
            public Collection<String> apply(ProfileRequestContext input) {
                return ImmutableList.of(AUDIENCE1, AUDIENCE2);
            }
        });
        action.initialize();
    }
    
    /** Test that action errors out properly if there is no response. */
    @Test public void testNoResponse() throws Exception {
        final ProfileRequestContext prc = new RequestContextBuilder().buildProfileRequestContext();
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }

    /** Test that action behaves properly if there is no assertion in the response. */
    @Test public void testNoAssertion() throws Exception {
        final ProfileRequestContext prc = new RequestContextBuilder().setOutboundMessage(
                SAML1ActionTestingSupport.buildResponse()).buildProfileRequestContext();

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

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        Assert.assertNotNull(response.getAssertions());
        Assert.assertEquals(response.getAssertions().size(), 1);

        Assert.assertNotNull(assertion.getConditions());
        Assert.assertEquals(assertion.getConditions().getAudienceRestrictionConditions().size(), 1);
        final AudienceRestrictionCondition audcond = assertion.getConditions().getAudienceRestrictionConditions().get(0);
        Assert.assertEquals(audcond.getAudiences().size(), 2);
        Assert.assertEquals(audcond.getAudiences().get(0).getUri(), AUDIENCE1);
        Assert.assertEquals(audcond.getAudiences().get(1).getUri(), AUDIENCE2);
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

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        Assert.assertNotNull(assertion.getConditions());
        Assert.assertEquals(assertion.getConditions().getAudienceRestrictionConditions().size(), 1);
        final AudienceRestrictionCondition audcond = assertion.getConditions().getAudienceRestrictionConditions().get(0);
        Assert.assertEquals(audcond.getAudiences().size(), 2);
        Assert.assertEquals(audcond.getAudiences().get(0).getUri(), AUDIENCE1);
        Assert.assertEquals(audcond.getAudiences().get(1).getUri(), AUDIENCE2);
    }

    /** Test that an addition condition is not added if an assertion already contains one. */
    @Test public void testSingleAssertionWithExistingAudienceCondition() throws Exception {
        final SAMLObjectBuilder<AudienceRestrictionCondition> conditionBuilder =
                (SAMLObjectBuilder<AudienceRestrictionCondition>) XMLObjectProviderRegistrySupport.getBuilderFactory()
                        .getBuilder(AudienceRestrictionCondition.DEFAULT_ELEMENT_NAME);
        final AudienceRestrictionCondition condition = conditionBuilder.buildObject();

        final SAMLObjectBuilder<Conditions> conditionsBuilder =
                (SAMLObjectBuilder<Conditions>) XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(
                        Conditions.DEFAULT_ELEMENT_NAME);
        final Conditions conditions = conditionsBuilder.buildObject();
        conditions.getAudienceRestrictionConditions().add(condition);

        final Assertion assertion = SAML1ActionTestingSupport.buildAssertion();
        assertion.setConditions(conditions);

        final Response response = SAML1ActionTestingSupport.buildResponse();
        response.getAssertions().add(assertion);

        final ProfileRequestContext prc = new RequestContextBuilder().setOutboundMessage(response).buildProfileRequestContext();

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        Assert.assertNotNull(assertion.getConditions());
        Assert.assertEquals(assertion.getConditions().getAudienceRestrictionConditions().size(), 1);
        final AudienceRestrictionCondition audcond = assertion.getConditions().getAudienceRestrictionConditions().get(0);
        Assert.assertEquals(audcond.getAudiences().size(), 2);
        Assert.assertEquals(audcond.getAudiences().get(0).getUri(), AUDIENCE1);
        Assert.assertEquals(audcond.getAudiences().get(1).getUri(), AUDIENCE2);
    }

    /** Test that the condition is properly added if there are multiple assertions in the response. */
    @Test public void testMultipleAssertion() throws Exception {
        final Response response = SAML1ActionTestingSupport.buildResponse();
        response.getAssertions().add(SAML1ActionTestingSupport.buildAssertion());
        response.getAssertions().add(SAML1ActionTestingSupport.buildAssertion());
        response.getAssertions().add(SAML1ActionTestingSupport.buildAssertion());

        final ProfileRequestContext prc = new RequestContextBuilder().setOutboundMessage(response).buildProfileRequestContext();

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        Assert.assertNotNull(response.getAssertions());
        Assert.assertEquals(response.getAssertions().size(), 3);

        for (Assertion assertion : response.getAssertions()) {
            Assert.assertNotNull(assertion.getConditions());
            Assert.assertEquals(assertion.getConditions().getAudienceRestrictionConditions().size(), 1);
            final AudienceRestrictionCondition audcond = assertion.getConditions().getAudienceRestrictionConditions().get(0);
            Assert.assertEquals(audcond.getAudiences().size(), 2);
            Assert.assertEquals(audcond.getAudiences().get(0).getUri(), AUDIENCE1);
            Assert.assertEquals(audcond.getAudiences().get(1).getUri(), AUDIENCE2);
        }
    }
    
    /**
     * Test that the condition is properly added if there is a single assertion, without a Conditions element, in the
     * response.
     */
    @Test public void testSAML2Assertion() throws Exception {
        final org.opensaml.saml.saml2.core.Assertion assertion = SAML2ActionTestingSupport.buildAssertion();
        final org.opensaml.saml.saml2.core.Response response = SAML2ActionTestingSupport.buildResponse();
        response.getAssertions().add(assertion);

        final ProfileRequestContext prc = new RequestContextBuilder().setOutboundMessage(response).buildProfileRequestContext();

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        Assert.assertNotNull(response.getAssertions());
        Assert.assertEquals(response.getAssertions().size(), 1);

        Assert.assertNotNull(assertion.getConditions());
        Assert.assertEquals(assertion.getConditions().getAudienceRestrictions().size(), 1);
        final AudienceRestriction audcond = assertion.getConditions().getAudienceRestrictions().get(0);
        Assert.assertEquals(audcond.getAudiences().size(), 2);
        Assert.assertEquals(audcond.getAudiences().get(0).getAudienceURI(), AUDIENCE1);
        Assert.assertEquals(audcond.getAudiences().get(1).getAudienceURI(), AUDIENCE2);
    }

}