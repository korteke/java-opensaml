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
import org.opensaml.saml.saml1.core.Assertion;
import org.opensaml.saml.saml1.core.NameIdentifier;
import org.opensaml.saml.saml1.core.Response;
import org.opensaml.saml.saml1.core.Subject;
import org.opensaml.saml.saml1.profile.AbstractSAML1NameIdentifierGenerator;
import org.opensaml.saml.saml1.profile.SAML1ActionTestingSupport;
import org.opensaml.saml.saml1.profile.SAML1NameIdentifierGenerator;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;

/** Test for {@link AddNameIdentifierToSubjects}. */
public class AddNameIdentifierToSubjectsTest extends OpenSAMLInitBaseTestCase {

    private static final String NAME_QUALIFIER = "https://idp.example.org";
    
    private Map<String, List<SAML1NameIdentifierGenerator>> generatorMap;

    private ProfileRequestContext<Object,Response> prc;
    
    private AddNameIdentifierToSubjects action;
    
    @BeforeMethod
    public void setUp() throws ComponentInitializationException {
        prc = new RequestContextBuilder().buildProfileRequestContext();
        
        action = new AddNameIdentifierToSubjects();
        
        MockSAML1NameIdentifierGenerator mock = new MockSAML1NameIdentifierGenerator("foo");
        mock.setFormat(NameIdentifier.X509_SUBJECT);
        mock.initialize();

        MockSAML1NameIdentifierGenerator mock2 = new MockSAML1NameIdentifierGenerator("bar");
        mock2.setFormat(NameIdentifier.EMAIL);
        mock2.setActivationCondition(Predicates.<ProfileRequestContext>alwaysFalse());
        mock2.initialize();

        MockSAML1NameIdentifierGenerator mock3 = new MockSAML1NameIdentifierGenerator("baz");
        mock3.setFormat(NameIdentifier.EMAIL);
        mock3.initialize();
        
        generatorMap = Maps.newHashMap();
        generatorMap.put(NameIdentifier.X509_SUBJECT, Collections.<SAML1NameIdentifierGenerator>singletonList(mock));
        generatorMap.put(NameIdentifier.EMAIL, Arrays.<SAML1NameIdentifierGenerator>asList(mock2, mock3));
    }
    
    @Test
    public void testNoMessage() throws ComponentInitializationException, ProfileException {
        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }

    @Test
    public void testNoAssertions() throws ComponentInitializationException, ProfileException {
        prc.getOutboundMessageContext().setMessage(SAML1ActionTestingSupport.buildResponse());
        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        Assert.assertTrue(prc.getOutboundMessageContext().getMessage().getAssertions().isEmpty());
    }

    @Test
    public void testNoStatements() throws ComponentInitializationException, ProfileException {
        prc.getOutboundMessageContext().setMessage(SAML1ActionTestingSupport.buildResponse());
        prc.getOutboundMessageContext().getMessage().getAssertions().add(SAML1ActionTestingSupport.buildAssertion());
        
        action.setFormatLookupStrategy(new X509FormatLookupStrategy());
        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        Assert.assertTrue(prc.getOutboundMessageContext().getMessage().getAssertions().get(0).getStatements().isEmpty());
    }
    
    @Test void testSingleGenerator() throws ComponentInitializationException, ProfileException {
        addStatements();
        
        action.setFormatLookupStrategy(new X509FormatLookupStrategy());
        action.setNameIdentifierGenerators(generatorMap);
        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        Assertion assertion = prc.getOutboundMessageContext().getMessage().getAssertions().get(0);
        Subject subject = assertion.getAuthenticationStatements().get(0).getSubject();
        Assert.assertNotNull(subject);
        Assert.assertNotNull(subject.getNameIdentifier());
        Assert.assertEquals(subject.getNameIdentifier().getNameIdentifier(), "foo");
        Assert.assertEquals(subject.getNameIdentifier().getFormat(), NameIdentifier.X509_SUBJECT);

        assertion = prc.getOutboundMessageContext().getMessage().getAssertions().get(1);
        subject = assertion.getAttributeStatements().get(0).getSubject();
        Assert.assertNotNull(subject);
        Assert.assertNotNull(subject.getNameIdentifier());
        Assert.assertEquals(subject.getNameIdentifier().getNameIdentifier(), "foo");
        Assert.assertEquals(subject.getNameIdentifier().getFormat(), NameIdentifier.X509_SUBJECT);
    }

    @Test void testMultipleGenerators() throws ComponentInitializationException, ProfileException {
        addStatements();
        
        action.setFormatLookupStrategy(new EmailFormatLookupStrategy());
        action.setNameIdentifierGenerators(generatorMap);
        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        Assertion assertion = prc.getOutboundMessageContext().getMessage().getAssertions().get(0);
        Subject subject = assertion.getAuthenticationStatements().get(0).getSubject();
        Assert.assertNotNull(subject);
        Assert.assertNotNull(subject.getNameIdentifier());
        Assert.assertEquals(subject.getNameIdentifier().getNameIdentifier(), "baz");
        Assert.assertEquals(subject.getNameIdentifier().getFormat(), NameIdentifier.EMAIL);

        assertion = prc.getOutboundMessageContext().getMessage().getAssertions().get(1);
        subject = assertion.getAttributeStatements().get(0).getSubject();
        Assert.assertNotNull(subject);
        Assert.assertNotNull(subject.getNameIdentifier());
        Assert.assertEquals(subject.getNameIdentifier().getNameIdentifier(), "baz");
        Assert.assertEquals(subject.getNameIdentifier().getFormat(), NameIdentifier.EMAIL);
    }
    
    /** Set up the test message with some statements. */
    private void addStatements() {
        final Response response = SAML1ActionTestingSupport.buildResponse();
        response.getAssertions().add(SAML1ActionTestingSupport.buildAssertion());
        response.getAssertions().add(SAML1ActionTestingSupport.buildAssertion());
        response.getAssertions().get(0).getAuthenticationStatements().add(SAML1ActionTestingSupport.buildAuthenticationStatement());
        response.getAssertions().get(1).getAttributeStatements().add(SAML1ActionTestingSupport.buildAttributeStatement());
        prc.getOutboundMessageContext().setMessage(response);
    }
    
    private class MockSAML1NameIdentifierGenerator extends AbstractSAML1NameIdentifierGenerator {

        private final String identifier;
        
        public MockSAML1NameIdentifierGenerator(@Nonnull final String id) {
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
            return Arrays.asList(NameIdentifier.WIN_DOMAIN_QUALIFIED, NameIdentifier.X509_SUBJECT);
        }
    }

    private class EmailFormatLookupStrategy implements Function<ProfileRequestContext, List<String>> {

        /** {@inheritDoc} */
        @Override
        public List<String> apply(ProfileRequestContext input) {
            return Arrays.asList(NameIdentifier.WIN_DOMAIN_QUALIFIED, NameIdentifier.EMAIL);
        }
    }
    
}