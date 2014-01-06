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

package org.opensaml.saml.saml1.binding;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.opensaml.core.OpenSAMLInitBaseTestCase;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml1.core.RequestAbstractType;
import org.opensaml.saml.saml1.profile.SAML1ActionTestingSupport;
import org.testng.Assert;
import org.testng.annotations.Test;

/** {@link CheckRequestVersionHandler} unit test. */
public class CheckRequestVersionHandlerTest extends OpenSAMLInitBaseTestCase {

    /** Test that the handler accepts SAML 1.0 and 1.1 messages. */
    @Test public void testSaml1Message() throws MessageHandlerException, ComponentInitializationException {
        MessageContext<RequestAbstractType> messageCtx = new MessageContext<>();
        messageCtx.setMessage(SAML1ActionTestingSupport.buildAttributeQueryRequest(null));

        CheckRequestVersionHandler handler = new CheckRequestVersionHandler();
        handler.initialize();
        
        handler.invoke(messageCtx);
    }

    /** Test that the handler errors out on SAML 2 messages. */
    @Test public void testSaml2Message() throws MessageHandlerException, ComponentInitializationException {
        MessageContext<RequestAbstractType> messageCtx = new MessageContext<>();
        messageCtx.setMessage(SAML1ActionTestingSupport.buildAttributeQueryRequest(null));
        messageCtx.getMessage().setVersion(SAMLVersion.VERSION_20);

        CheckRequestVersionHandler handler = new CheckRequestVersionHandler();
        handler.initialize();
        
        try {
            handler.invoke(messageCtx);
            Assert.fail();
        } catch (MessageHandlerException e) {
            
        }
    }
    
}