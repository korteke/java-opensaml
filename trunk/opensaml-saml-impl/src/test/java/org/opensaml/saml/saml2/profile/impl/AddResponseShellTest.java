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
import net.shibboleth.utilities.java.support.security.IdentifierGenerationStrategy;
import net.shibboleth.utilities.java.support.security.SecureRandomIdentifierGenerationStrategy;

import org.opensaml.core.OpenSAMLInitBaseTestCase;
import org.opensaml.messaging.context.BasicMessageMetadataContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.profile.ProfileException;
import org.opensaml.profile.RequestContextBuilder;
import org.opensaml.profile.action.ActionTestingSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.profile.SAML2ActionTestingSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Function;

/** {@link AddResponseShell} unit test. */
public class AddResponseShellTest extends OpenSAMLInitBaseTestCase {

    private String issuer;
    
    private AddResponseShell action;

    @BeforeMethod public void setUp() throws ComponentInitializationException {
        action = new AddResponseShell();
        action.setId("test");
        action.setIdentifierGeneratorLookupStrategy(new Function<ProfileRequestContext,IdentifierGenerationStrategy>() {
            public IdentifierGenerationStrategy apply(ProfileRequestContext input) {
                return new SecureRandomIdentifierGenerationStrategy();
            }
        });
        action.setIssuerLookupStrategy(new Function<ProfileRequestContext,String>() {
            public String apply(ProfileRequestContext input) {
                return issuer;
            }
        });
        action.initialize();
    }

    @Test public void testAddResponse() throws ProfileException, ComponentInitializationException {
        final ProfileRequestContext prc = new RequestContextBuilder().buildProfileRequestContext();

        issuer = null;
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        final MessageContext<Response> outMsgCtx = prc.getOutboundMessageContext();
        final Response response = outMsgCtx.getMessage();

        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getID());
        Assert.assertNotNull(response.getIssueInstant());
        Assert.assertEquals(response.getVersion(), SAMLVersion.VERSION_20);
        
        Assert.assertNull(response.getIssuer());

        final Status status = response.getStatus();
        Assert.assertNotNull(status);
        Assert.assertNotNull(status.getStatusCode());
        Assert.assertEquals(status.getStatusCode().getValue(), StatusCode.SUCCESS_URI);

        final BasicMessageMetadataContext messageMetadata = outMsgCtx.getSubcontext(BasicMessageMetadataContext.class);
        Assert.assertNotNull(messageMetadata);
        Assert.assertNull(messageMetadata.getMessageIssuer());
        Assert.assertEquals(messageMetadata.getMessageId(), response.getID());
        Assert.assertEquals(messageMetadata.getMessageIssueInstant(), response.getIssueInstant().getMillis());
    }

    @Test public void testAddResponseWithIssuer() throws ProfileException, ComponentInitializationException {
        final ProfileRequestContext prc = new RequestContextBuilder().buildProfileRequestContext();

        issuer = "foo";
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        final MessageContext<Response> outMsgCtx = prc.getOutboundMessageContext();
        final Response response = outMsgCtx.getMessage();

        Assert.assertNotNull(response);
        
        Assert.assertNotNull(response.getIssuer());
        Assert.assertEquals(response.getIssuer().getValue(), "foo");

        final BasicMessageMetadataContext messageMetadata = outMsgCtx.getSubcontext(BasicMessageMetadataContext.class);
        Assert.assertNotNull(messageMetadata);
        Assert.assertEquals(messageMetadata.getMessageIssuer(), "foo");
    }
    
    @Test public void testAddResponseWhenResponseAlreadyExist() throws ProfileException,
            ComponentInitializationException {
        final ProfileRequestContext prc = new RequestContextBuilder().setOutboundMessage(
                SAML2ActionTestingSupport.buildResponse()).buildProfileRequestContext();

        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }

}