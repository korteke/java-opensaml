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

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.opensaml.core.OpenSAMLInitBaseTestCase;
import org.opensaml.profile.ProfileException;
import org.opensaml.profile.RequestContextBuilder;
import org.opensaml.profile.action.ActionTestingSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.messaging.context.SAMLMessageInfoContext;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.opensaml.saml.saml2.profile.SAML2ActionTestingSupport;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


/** Test for {@link AddSubjectConfirmationToSubjects}. */
public class AddSubjectConfirmationToSubjectsTest extends OpenSAMLInitBaseTestCase {
    
    private ProfileRequestContext<AuthnRequest,Response> prc;
    
    private AddSubjectConfirmationToSubjects action;
    
    @BeforeMethod
    public void setUp() throws ComponentInitializationException {
        prc = new RequestContextBuilder().buildProfileRequestContext();
        
        action = new AddSubjectConfirmationToSubjects();
        action.setId("test");
        action.setHttpServletRequest(new MockHttpServletRequest());
    }

    @Test(expectedExceptions = ComponentInitializationException.class)
    public void testBadConfig() throws ComponentInitializationException, ProfileException {
        action.initialize();
    }
    
    @Test
    public void testNoMessage() throws ComponentInitializationException, ProfileException {
        action.setMethod(SubjectConfirmation.METHOD_BEARER);
        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }

    @Test
    public void testNoAssertions() throws ComponentInitializationException, ProfileException {
        prc.getOutboundMessageContext().setMessage(SAML2ActionTestingSupport.buildResponse());
        
        action.setMethod(SubjectConfirmation.METHOD_BEARER);
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        Assert.assertTrue(prc.getOutboundMessageContext().getMessage().getAssertions().isEmpty());
    }

    @Test void testSuccess() throws ComponentInitializationException, ProfileException {
        addAssertions();
        
        action.setMethod(SubjectConfirmation.METHOD_BEARER);
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        Assertion assertion = prc.getOutboundMessageContext().getMessage().getAssertions().get(0);
        Subject subject = assertion.getSubject();
        Assert.assertNotNull(subject);
        Assert.assertEquals(subject.getSubjectConfirmations().size(), 1);
        Assert.assertEquals(subject.getSubjectConfirmations().get(0).getMethod(), SubjectConfirmation.METHOD_BEARER);
        
        assertion = prc.getOutboundMessageContext().getMessage().getAssertions().get(1);
        subject = assertion.getSubject();
        Assert.assertNotNull(subject);
        Assert.assertEquals(subject.getSubjectConfirmations().size(), 1);
        Assert.assertEquals(subject.getSubjectConfirmations().get(0).getMethod(), SubjectConfirmation.METHOD_BEARER);
        
        final SubjectConfirmationData data = subject.getSubjectConfirmations().get(0).getSubjectConfirmationData();
        Assert.assertNotNull(data);
        Assert.assertNull(data.getRecipient());
        Assert.assertNotNull(data.getNotOnOrAfter());
        Assert.assertEquals(data.getAddress(), "127.0.0.1");
        Assert.assertEquals(data.getInResponseTo(), prc.getInboundMessageContext().getMessage().getID());
    }
    
    /** Set up the test message with some assertions. */
    private void addAssertions() {
        final Response response = SAML2ActionTestingSupport.buildResponse();
        response.getAssertions().add(SAML2ActionTestingSupport.buildAssertion());
        response.getAssertions().add(SAML2ActionTestingSupport.buildAssertion());
        prc.getOutboundMessageContext().setMessage(response);
        prc.getInboundMessageContext().setMessage(SAML2ActionTestingSupport.buildAuthnRequest());
        prc.getInboundMessageContext().getSubcontext(SAMLMessageInfoContext.class, true);
    }
    
}