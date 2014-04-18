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

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.profile.RequestContextBuilder;
import org.opensaml.profile.action.ActionTestingSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.SAMLException;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.binding.impl.SAMLMetadataLookupHandlerTest;
import org.opensaml.saml.common.profile.SAMLEventIds;
import org.opensaml.saml.common.profile.logic.AffiliationNameIDPolicyPredicate;
import org.opensaml.saml.metadata.resolver.impl.FilesystemMetadataResolver;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDPolicy;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.profile.AbstractSAML2NameIDGenerator;
import org.opensaml.saml.saml2.profile.SAML2ActionTestingSupport;
import org.opensaml.saml.saml2.profile.SAML2NameIDGenerator;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicates;

/** Test for {@link AddNameIDToSubjects}. */
public class AddNameIDToSubjectsTest extends XMLObjectBaseTestCase {

    private static final String NAME_QUALIFIER = "https://idp.example.org";
    
    private FilesystemMetadataResolver metadataResolver;
    
    private SAMLObjectBuilder<NameIDPolicy> policyBuilder;
    
    private List<SAML2NameIDGenerator> generators;

    private ProfileRequestContext<Object,Response> prc;
    
    private AddNameIDToSubjects action;
    
    @BeforeClass
    public void classSetUp() throws ResolverException, URISyntaxException, ComponentInitializationException {
        final URL mdURL = SAMLMetadataLookupHandlerTest.class
                .getResource("/data/org/opensaml/saml/saml2/profile/impl/affiliation-metadata.xml");
        final File mdFile = new File(mdURL.toURI());

        metadataResolver = new FilesystemMetadataResolver(mdFile);
        metadataResolver.setParserPool(parserPool);
        metadataResolver.initialize();
    }
    
    @AfterClass
    public void classTearDown() {
        metadataResolver.destroy();
    }
    
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

        MockSAML2NameIDGenerator mock4 = new MockSAML2NameIDGenerator("baf");
        mock4.setFormat(NameID.PERSISTENT);
        mock4.initialize();
        
        generators = Arrays.<SAML2NameIDGenerator>asList(mock, mock2, mock3, mock4);

        policyBuilder = (SAMLObjectBuilder<NameIDPolicy>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<NameIDPolicy>getBuilderOrThrow(
                        NameIDPolicy.DEFAULT_ELEMENT_NAME);
    }
    
    @Test
    public void testNoMessage() throws ComponentInitializationException {
        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }

    @Test
    public void testNoAssertions() throws ComponentInitializationException {
        prc.getOutboundMessageContext().setMessage(SAML2ActionTestingSupport.buildResponse());
        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        Assert.assertTrue(prc.getOutboundMessageContext().getMessage().getAssertions().isEmpty());
    }

    @Test void testRequiredFormat() throws ComponentInitializationException {
        addAssertions();
        final AuthnRequest request = SAML2ActionTestingSupport.buildAuthnRequest();
        final NameIDPolicy policy = policyBuilder.buildObject();
        policy.setFormat(NameID.EMAIL);
        request.setNameIDPolicy(policy);
        prc.getInboundMessageContext().setMessage(request);
        
        action.setNameIDGenerators(generators);
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

    @Test void testRequiredFormatError() throws ComponentInitializationException {
        addAssertions();
        final AuthnRequest request = SAML2ActionTestingSupport.buildAuthnRequest();
        final NameIDPolicy policy = policyBuilder.buildObject();
        policy.setFormat(NameID.KERBEROS);
        request.setNameIDPolicy(policy);
        prc.getInboundMessageContext().setMessage(request);
        
        action.setNameIDGenerators(generators);
        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, SAMLEventIds.INVALID_NAMEID_POLICY);
        
        Assertion assertion = prc.getOutboundMessageContext().getMessage().getAssertions().get(0);
        Subject subject = assertion.getSubject();
        Assert.assertNull(subject);
    }
    
    @Test void testQualifierAsIssuer() throws ComponentInitializationException {
        addAssertions();
        final AuthnRequest request = SAML2ActionTestingSupport.buildAuthnRequest();
        final NameIDPolicy policy = policyBuilder.buildObject();
        policy.setFormat(NameID.PERSISTENT);
        policy.setSPNameQualifier("foo");
        request.setNameIDPolicy(policy);
        prc.getInboundMessageContext().setMessage(request);
        
        action.setNameIDGenerators(generators);
        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, SAMLEventIds.INVALID_NAMEID_POLICY);

        Assertion assertion = prc.getOutboundMessageContext().getMessage().getAssertions().get(0);
        Subject subject = assertion.getSubject();
        Assert.assertNull(subject);
        
        policy.setSPNameQualifier(request.getIssuer().getValue());
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        assertion = prc.getOutboundMessageContext().getMessage().getAssertions().get(0);
        subject = assertion.getSubject();
        Assert.assertNotNull(subject);
        Assert.assertNotNull(subject.getNameID());
        Assert.assertEquals(subject.getNameID().getValue(), "baf");
        Assert.assertEquals(subject.getNameID().getFormat(), NameID.PERSISTENT);
    }
    
    @Test void testAffiliation() throws ComponentInitializationException {
        addAssertions();
        final AuthnRequest request = SAML2ActionTestingSupport.buildAuthnRequest();
        final NameIDPolicy policy = policyBuilder.buildObject();
        policy.setFormat(NameID.PERSISTENT);
        policy.setSPNameQualifier("foo");
        request.setNameIDPolicy(policy);
        prc.getInboundMessageContext().setMessage(request);
        
        final AffiliationNameIDPolicyPredicate predicate = new AffiliationNameIDPolicyPredicate();
        predicate.setMetadataResolver(metadataResolver);
        predicate.setRequesterIdLookupStrategy(new AddNameIDToSubjects.RequesterIdFromIssuerFunction());
        predicate.setObjectLookupStrategy(new AddNameIDToSubjects.NameIDPolicyLookupFunction());
        predicate.initialize();
        action.setNameIDGenerators(generators);
        action.setNameIDPolicyPredicate(predicate);
        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, SAMLEventIds.INVALID_NAMEID_POLICY);

        Assertion assertion = prc.getOutboundMessageContext().getMessage().getAssertions().get(0);
        Subject subject = assertion.getSubject();
        Assert.assertNull(subject);
        
        policy.setSPNameQualifier("http://affiliation.example.org");
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        assertion = prc.getOutboundMessageContext().getMessage().getAssertions().get(0);
        subject = assertion.getSubject();
        Assert.assertNotNull(subject);
        Assert.assertNotNull(subject.getNameID());
        Assert.assertEquals(subject.getNameID().getValue(), "baf");
        Assert.assertEquals(subject.getNameID().getFormat(), NameID.PERSISTENT);
    }
    
    @Test void testArbitraryFormat() throws ComponentInitializationException {
        addAssertions();
        
        action.setNameIDGenerators(generators);
        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        Assertion assertion = prc.getOutboundMessageContext().getMessage().getAssertions().get(0);
        Subject subject = assertion.getSubject();
        Assert.assertNotNull(subject);
        Assert.assertNotNull(subject.getNameID());
        Assert.assertNotNull(subject.getNameID().getValue());
        Assert.assertNotNull(subject.getNameID().getFormat());

        assertion = prc.getOutboundMessageContext().getMessage().getAssertions().get(1);
        subject = assertion.getSubject();
        Assert.assertNotNull(subject);
        Assert.assertNotNull(subject.getNameID());
        Assert.assertNotNull(subject.getNameID().getValue());
        Assert.assertNotNull(subject.getNameID().getFormat());
    }
    
    @Test void testSingleGenerator() throws ComponentInitializationException {
        addAssertions();
        
        action.setFormatLookupStrategy(new X509FormatLookupStrategy());
        action.setNameIDGenerators(generators);
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

    @Test void testMultipleGenerators() throws ComponentInitializationException {
        addAssertions();
        
        action.setFormatLookupStrategy(new EmailFormatLookupStrategy());
        action.setNameIDGenerators(generators);
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
            setId("test");
            setDefaultIdPNameQualifierLookupStrategy(new Function<ProfileRequestContext,String>() {
                public String apply(ProfileRequestContext input) {
                    return NAME_QUALIFIER;
                }
            });
            identifier = id;
        }
        
        /** {@inheritDoc} */
        @Override
        protected String getIdentifier(ProfileRequestContext profileRequestContext) throws SAMLException {
            return identifier;
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