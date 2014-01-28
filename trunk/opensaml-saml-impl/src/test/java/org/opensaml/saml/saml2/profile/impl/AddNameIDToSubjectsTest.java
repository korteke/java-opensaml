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

package org.opensaml.saml.saml2.profile.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.opensaml.core.OpenSAMLInitBaseTestCase;
import org.opensaml.profile.ProfileException;
import org.opensaml.profile.RequestContextBuilder;
import org.opensaml.profile.action.ActionTestingSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.profile.AbstractSAML2NameIDGenerator;
import org.opensaml.saml.saml2.profile.SAML2ActionTestingSupport;
import org.opensaml.saml.saml2.profile.SAML2NameIDGenerator;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;

/** Test for {@link AddNameIDToSubjects}. */
public class AddNameIDToSubjectsTest extends OpenSAMLInitBaseTestCase {

    private static final String NAME_QUALIFIER = "https://idp.example.org";
    
    private Map<String, List<SAML2NameIDGenerator>> generatorMap;

    private ProfileRequestContext<Object,Response> prc;
    
    private AddNameIDToSubjects action;
    
    @BeforeMethod
    public void setUp() throws ComponentInitializationException {
        prc = new RequestContextBuilder().buildProfileRequestContext();
        
        action = new AddNameIDToSubjects();
        
        MockSAML2NameIDGenerator mock = new MockSAML2NameIDGenerator("foo");
        mock.setFormat(NameID.X509_SUBJECT);
        mock.initialize();

        MockSAML2NameIDGenerator mock2 = new MockSAML2NameIDGenerator("bar");
        mock2.setFormat(NameID.EMAIL);
        mock2.setActivationCondition(Predicates.<ProfileRequestContext>alwaysFalse());
        mock2.initialize();

        MockSAML2NameIDGenerator mock3 = new MockSAML2NameIDGenerator("baz");
        mock3.setFormat(NameID.EMAIL);
        mock3.initialize();
        
        generatorMap = Maps.newHashMap();
        generatorMap.put(NameID.X509_SUBJECT, Collections.<SAML2NameIDGenerator>singletonList(mock));
        generatorMap.put(NameID.EMAIL, Arrays.<SAML2NameIDGenerator>asList(mock2, mock3));
    }
    
    @Test
    public void testNoMessage() throws ComponentInitializationException, ProfileException {
        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }

    @Test
    public void testNoAssertions() throws ComponentInitializationException, ProfileException {
        prc.getOutboundMessageContext().setMessage(SAML2ActionTestingSupport.buildResponse());
        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        Assert.assertTrue(prc.getOutboundMessageContext().getMessage().getAssertions().isEmpty());
    }


    @Test void testArbitraryFormat() throws ComponentInitializationException, ProfileException {
        addAssertions();
        
        action.setNameIDGenerators(generatorMap);
        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        Assertion assertion = prc.getOutboundMessageContext().getMessage().getAssertions().get(0);
        Subject subject = assertion.getSubject();
        Assert.assertNotNull(subject);
        Assert.assertNotNull(subject.getNameID());
        Assert.assertEquals(subject.getNameID().getValue(), "foo");
        Assert.assertEquals(subject.getNameID().getFormat(), NameID.X509_SUBJECT);

        assertion = prc.getOutboundMessageContext().getMessage().getAssertions().get(1);
        subject = assertion.getSubject();
        Assert.assertNotNull(subject);
        Assert.assertNotNull(subject.getNameID());
        Assert.assertEquals(subject.getNameID().getValue(), "foo");
        Assert.assertEquals(subject.getNameID().getFormat(), NameID.X509_SUBJECT);
    }
    
    @Test void testSingleGenerator() throws ComponentInitializationException, ProfileException {
        addAssertions();
        
        action.setFormatLookupStrategy(new X509FormatLookupStrategy());
        action.setNameIDGenerators(generatorMap);
        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        Assertion assertion = prc.getOutboundMessageContext().getMessage().getAssertions().get(0);
        Subject subject = assertion.getSubject();
        Assert.assertNotNull(subject);
        Assert.assertNotNull(subject.getNameID());
        Assert.assertEquals(subject.getNameID().getValue(), "foo");
        Assert.assertEquals(subject.getNameID().getFormat(), NameID.X509_SUBJECT);

        assertion = prc.getOutboundMessageContext().getMessage().getAssertions().get(1);
        subject = assertion.getSubject();
        Assert.assertNotNull(subject);
        Assert.assertNotNull(subject.getNameID());
        Assert.assertEquals(subject.getNameID().getValue(), "foo");
        Assert.assertEquals(subject.getNameID().getFormat(), NameID.X509_SUBJECT);
    }

    @Test void testMultipleGenerators() throws ComponentInitializationException, ProfileException {
        addAssertions();
        
        action.setFormatLookupStrategy(new EmailFormatLookupStrategy());
        action.setNameIDGenerators(generatorMap);
        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        Assertion assertion = prc.getOutboundMessageContext().getMessage().getAssertions().get(0);
        Subject subject = assertion.getSubject();
        Assert.assertNotNull(subject);
        Assert.assertNotNull(subject.getNameID());
        Assert.assertEquals(subject.getNameID().getValue(), "baz");
        Assert.assertEquals(subject.getNameID().getFormat(), NameID.EMAIL);

        assertion = prc.getOutboundMessageContext().getMessage().getAssertions().get(1);
        subject = assertion.getSubject();
        Assert.assertNotNull(subject);
        Assert.assertNotNull(subject.getNameID());
        Assert.assertEquals(subject.getNameID().getValue(), "baz");
        Assert.assertEquals(subject.getNameID().getFormat(), NameID.EMAIL);
    }
    
    /** Set up the test message with some assertions. */
    private void addAssertions() {
        final Response response = SAML2ActionTestingSupport.buildResponse();
        response.getAssertions().add(SAML2ActionTestingSupport.buildAssertion());
        response.getAssertions().add(SAML2ActionTestingSupport.buildAssertion());
        prc.getOutboundMessageContext().setMessage(response);
    }
    
    private class MockSAML2NameIDGenerator extends AbstractSAML2NameIDGenerator {

        private final String identifier;
        
        public MockSAML2NameIDGenerator(@Nonnull final String id) {
            identifier = id;
        }
        
        /** {@inheritDoc} */
        @Override
        protected String getIdentifier(ProfileRequestContext profileRequestContext) throws ProfileException {
            return identifier;
        }

        /** {@inheritDoc} */
        @Override
        protected String getDefaultIdPNameQualifier(ProfileRequestContext profileRequestContext) {
            return NAME_QUALIFIER;
        }
    }
    
    private class X509FormatLookupStrategy implements Function<ProfileRequestContext, List<String>> {

        /** {@inheritDoc} */
        @Override
        public List<String> apply(ProfileRequestContext input) {
            return Arrays.asList(NameID.WIN_DOMAIN_QUALIFIED, NameID.X509_SUBJECT);
        }
    }

    private class EmailFormatLookupStrategy implements Function<ProfileRequestContext, List<String>> {

        /** {@inheritDoc} */
        @Override
        public List<String> apply(ProfileRequestContext input) {
            return Arrays.asList(NameID.WIN_DOMAIN_QUALIFIED, NameID.EMAIL);
        }
    }
    
}