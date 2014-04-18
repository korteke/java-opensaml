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

package org.opensaml.saml.saml2.binding.impl;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.opensaml.core.OpenSAMLInitBaseTestCase;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.messaging.context.SAMLConsentContext;
import org.opensaml.saml.saml2.binding.impl.AddConsentToResponseHandler;
import org.opensaml.saml.saml2.core.StatusResponseType;
import org.opensaml.saml.saml2.profile.SAML2ActionTestingSupport;
import org.testng.Assert;
import org.testng.annotations.Test;

/** {@link AddConsentToResponseHandler} unit test. */
public class AddConsentToResponseHandlerTest extends OpenSAMLInitBaseTestCase {
    
    /** Test that the handler errors on a missing context. */
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testMissingContext() throws MessageHandlerException, ComponentInitializationException {
        final MessageContext<StatusResponseType> messageCtx = new MessageContext<>();
        messageCtx.setMessage(SAML2ActionTestingSupport.buildResponse());

        final AddConsentToResponseHandler handler = new AddConsentToResponseHandler();
        handler.initialize();
        
        handler.invoke(messageCtx);
    }

    /** Test that the handler works. */
    @Test public void testSuccess() throws MessageHandlerException, ComponentInitializationException {
        final MessageContext<StatusResponseType> messageCtx = new MessageContext<>();
        messageCtx.setMessage(SAML2ActionTestingSupport.buildResponse());
        messageCtx.getSubcontext(SAMLConsentContext.class, true).setConsent(StatusResponseType.EXPLICIT_CONSENT);
        
        final AddConsentToResponseHandler handler = new AddConsentToResponseHandler();
        handler.initialize();
        
        handler.invoke(messageCtx);
        Assert.assertEquals(messageCtx.getMessage().getConsent(), StatusResponseType.EXPLICIT_CONSENT);
    }
    
}