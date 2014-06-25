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

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.opensaml.core.OpenSAMLInitBaseTestCase;
import org.opensaml.profile.RequestContextBuilder;
import org.opensaml.profile.action.ActionTestingSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml1.core.Assertion;
import org.opensaml.saml.saml1.core.ConfirmationMethod;
import org.opensaml.saml.saml1.core.Response;
import org.opensaml.saml.saml1.core.Subject;
import org.opensaml.saml.saml1.profile.SAML1ActionTestingSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


/** Test for {@link AddSubjectConfirmationToSubjects}. */
public class AddSubjectConfirmationToSubjectsTest extends OpenSAMLInitBaseTestCase {
    
    private ProfileRequestContext<Object,Response> prc;
    
    private AddSubjectConfirmationToSubjects action;
    
    @BeforeMethod
    public void setUp() throws ComponentInitializationException {
        prc = new RequestContextBuilder().buildProfileRequestContext();
        
        action = new AddSubjectConfirmationToSubjects();
    }

    @Test(expectedExceptions = ComponentInitializationException.class)
    public void testBadConfig() throws ComponentInitializationException {
        action.initialize();
    }
    
    @Test
    public void testNoMessage() throws ComponentInitializationException {
        action.setMethods(Collections.singleton(ConfirmationMethod.METHOD_BEARER));
        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }

    @Test
    public void testNoAssertions() throws ComponentInitializationException {
        prc.getOutboundMessageContext().setMessage(SAML1ActionTestingSupport.buildResponse());
        
        action.setMethods(Collections.singleton(ConfirmationMethod.METHOD_BEARER));
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        Assert.assertTrue(prc.getOutboundMessageContext().getMessage().getAssertions().isEmpty());
    }

    @Test
    public void testNoStatements() throws ComponentInitializationException {
        prc.getOutboundMessageContext().setMessage(SAML1ActionTestingSupport.buildResponse());
        prc.getOutboundMessageContext().getMessage().getAssertions().add(SAML1ActionTestingSupport.buildAssertion());
        
        action.setMethods(Collections.singleton(ConfirmationMethod.METHOD_BEARER));
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        Assert.assertTrue(prc.getOutboundMessageContext().getMessage().getAssertions().get(0).getStatements().isEmpty());
    }

    @Test void testSingle() throws ComponentInitializationException {
        addStatements();
        
        action.setMethods(Collections.singleton(ConfirmationMethod.METHOD_BEARER));
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        Assertion assertion = prc.getOutboundMessageContext().getMessage().getAssertions().get(0);
        Subject subject = assertion.getAuthenticationStatements().get(0).getSubject();
        Assert.assertNotNull(subject);
        Assert.assertNotNull(subject.getSubjectConfirmation());
        Assert.assertEquals(subject.getSubjectConfirmation().getConfirmationMethods().size(), 1);
        Assert.assertEquals(subject.getSubjectConfirmation().getConfirmationMethods().get(0).getConfirmationMethod(),
                ConfirmationMethod.METHOD_BEARER);

        assertion = prc.getOutboundMessageContext().getMessage().getAssertions().get(1);
        subject = assertion.getAttributeStatements().get(0).getSubject();
        Assert.assertNotNull(subject);
        Assert.assertNotNull(subject.getSubjectConfirmation());
        Assert.assertEquals(subject.getSubjectConfirmation().getConfirmationMethods().size(), 1);
        Assert.assertEquals(subject.getSubjectConfirmation().getConfirmationMethods().get(0).getConfirmationMethod(),
                ConfirmationMethod.METHOD_BEARER);
    }

    @Test void testMultiple() throws ComponentInitializationException {
        addStatements();
        
        action.setMethods(Arrays.asList(ConfirmationMethod.METHOD_BEARER, ConfirmationMethod.METHOD_SENDER_VOUCHES));
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        Assertion assertion = prc.getOutboundMessageContext().getMessage().getAssertions().get(0);
        Subject subject = assertion.getAuthenticationStatements().get(0).getSubject();
        Assert.assertNotNull(subject);
        Assert.assertNotNull(subject.getSubjectConfirmation());
        Assert.assertEquals(subject.getSubjectConfirmation().getConfirmationMethods().size(), 2);
        Assert.assertEquals(subject.getSubjectConfirmation().getConfirmationMethods().get(0).getConfirmationMethod(),
                ConfirmationMethod.METHOD_BEARER);
        Assert.assertEquals(subject.getSubjectConfirmation().getConfirmationMethods().get(1).getConfirmationMethod(),
                ConfirmationMethod.METHOD_SENDER_VOUCHES);

        assertion = prc.getOutboundMessageContext().getMessage().getAssertions().get(1);
        subject = assertion.getAttributeStatements().get(0).getSubject();
        Assert.assertNotNull(subject);
        Assert.assertNotNull(subject.getSubjectConfirmation());
        Assert.assertEquals(subject.getSubjectConfirmation().getConfirmationMethods().size(), 2);
        Assert.assertEquals(subject.getSubjectConfirmation().getConfirmationMethods().get(0).getConfirmationMethod(),
                ConfirmationMethod.METHOD_BEARER);
        Assert.assertEquals(subject.getSubjectConfirmation().getConfirmationMethods().get(1).getConfirmationMethod(),
                ConfirmationMethod.METHOD_SENDER_VOUCHES);
    }
    
    @Test void testArtifact() throws ComponentInitializationException {
        addStatements();
        prc.getOutboundMessageContext().getSubcontext(SAMLBindingContext.class, true).setBindingUri(
                SAMLConstants.SAML1_ARTIFACT_BINDING_URI);
        
        action.setMethods(Collections.singleton(ConfirmationMethod.METHOD_BEARER));
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        Assertion assertion = prc.getOutboundMessageContext().getMessage().getAssertions().get(0);
        Subject subject = assertion.getAuthenticationStatements().get(0).getSubject();
        Assert.assertNotNull(subject);
        Assert.assertNotNull(subject.getSubjectConfirmation());
        Assert.assertEquals(subject.getSubjectConfirmation().getConfirmationMethods().size(), 1);
        Assert.assertEquals(subject.getSubjectConfirmation().getConfirmationMethods().get(0).getConfirmationMethod(),
                ConfirmationMethod.METHOD_ARTIFACT);
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
    
}