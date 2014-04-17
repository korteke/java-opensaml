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

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.opensaml.core.OpenSAMLInitBaseTestCase;
import org.opensaml.profile.RequestContextBuilder;
import org.opensaml.profile.action.ActionTestingSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.saml1.core.Assertion;
import org.opensaml.saml.saml1.core.Request;
import org.opensaml.saml.saml1.core.Response;
import org.opensaml.saml.saml1.core.Subject;
import org.opensaml.saml.saml1.profile.SAML1ActionTestingSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


/** Test for {@link CopyNameIdentifierFromRequest}. */
public class CopyNameIdentifierFromRequestTest extends OpenSAMLInitBaseTestCase {

    private static final String NAME_QUALIFIER = "https://idp.example.org";

    private ProfileRequestContext<Request,Response> prc;
    
    private CopyNameIdentifierFromRequest action;
    
    @BeforeMethod
    public void setUp() throws ComponentInitializationException {
        prc = new RequestContextBuilder().buildProfileRequestContext();
        
        action = new CopyNameIdentifierFromRequest();
        action.setId("test");
        action.initialize();
    }
    
    @Test
    public void testNoResponse() {
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }

    @Test
    public void testNoAssertions() {
        prc.getOutboundMessageContext().setMessage(SAML1ActionTestingSupport.buildResponse());

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        Assert.assertTrue(prc.getOutboundMessageContext().getMessage().getAssertions().isEmpty());
    }

    @Test
    public void testNoRequest() {
        prc.getOutboundMessageContext().setMessage(SAML1ActionTestingSupport.buildResponse());
        prc.getOutboundMessageContext().getMessage().getAssertions().add(SAML1ActionTestingSupport.buildAssertion());
        addStatements();

        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }

    @Test
    public void testNoName() {
        prc.getOutboundMessageContext().setMessage(SAML1ActionTestingSupport.buildResponse());
        prc.getOutboundMessageContext().getMessage().getAssertions().add(SAML1ActionTestingSupport.buildAssertion());
        addStatements();

        prc.getInboundMessageContext().setMessage(SAML1ActionTestingSupport.buildAttributeQueryRequest(null));
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }
    
    @Test void testCopy() {
        
        Subject subject = SAML1ActionTestingSupport.buildSubject("jdoe");
        subject.getNameIdentifier().setNameQualifier(NAME_QUALIFIER);
        prc.getInboundMessageContext().setMessage(SAML1ActionTestingSupport.buildAttributeQueryRequest(subject));
        addStatements();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        Assertion assertion = prc.getOutboundMessageContext().getMessage().getAssertions().get(0);
        subject = assertion.getAttributeStatements().get(0).getSubject();
        Assert.assertNotNull(subject);
        Assert.assertNotNull(subject.getNameIdentifier());
        Assert.assertEquals(subject.getNameIdentifier().getNameIdentifier(), "jdoe");
        Assert.assertEquals(subject.getNameIdentifier().getNameQualifier(), NAME_QUALIFIER);
    }

    /** Set up the test message with some statements. */
    private void addStatements() {
        final Response response = SAML1ActionTestingSupport.buildResponse();
        response.getAssertions().add(SAML1ActionTestingSupport.buildAssertion());
        response.getAssertions().get(0).getAttributeStatements().add(SAML1ActionTestingSupport.buildAttributeStatement());
        prc.getOutboundMessageContext().setMessage(response);
    }
    
}