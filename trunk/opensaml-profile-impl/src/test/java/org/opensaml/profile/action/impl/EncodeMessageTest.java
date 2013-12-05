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

package org.opensaml.profile.action.impl;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.messaging.encoder.AbstractMessageEncoder;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.profile.action.ActionTestingSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.action.impl.EncodeMessage;
import org.opensaml.profile.context.ProfileRequestContext;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/** Unit test for {@link EncodeMessage}. */
public class EncodeMessageTest {
    
    private MockMessage message; 
    
    private MockMessageEncoder encoder;
    
    private MessageContext<MockMessage> messageContext;
    
    private ProfileRequestContext profileCtx;
    
    private String expectedMessage;
    
    @BeforeMethod
    public void setUp() throws ComponentInitializationException {
        message = new MockMessage();
        message.getProperties().put("foo", "3");
        message.getProperties().put("bar", "1");
        message.getProperties().put("baz", "2");
        
        // Encoded mock message, keys sorted alphabetically, per MockMessage#toString
        expectedMessage = "bar=1&baz=2&foo=3";
        
        messageContext = new MessageContext<>();
        messageContext.setMessage(message);
        
        profileCtx = new ProfileRequestContext();
        profileCtx.setOutboundMessageContext(messageContext);
        
        encoder = new MockMessageEncoder();
        // Note: we don't init the encoder, b/c that is done by the action after setting the message context
    }

    /** Test that the action proceeds properly if the message can be decoded. */
    @Test public void testDecodeMessage() throws Exception {
        EncodeMessage action = new EncodeMessage(encoder);
        action.setId("test");
        action.initialize();

        action.execute(profileCtx);
        ActionTestingSupport.assertProceedEvent(profileCtx);

        Assert.assertEquals(encoder.getEncodedMessage(), expectedMessage);
    }

    /** Test that the action errors out properly if the message can not be decoded. */
    @Test public void testThrowException() throws Exception {
        encoder.setThrowException(true);

        EncodeMessage action = new EncodeMessage(encoder);
        action.setId("test");
        action.initialize();

        action.execute(profileCtx);
        ActionTestingSupport.assertEvent(profileCtx, EventIds.UNABLE_TO_ENCODE);
    }

    /**
     * Mock implementation of {@link MessageEncoder } which either returns a  
     * {@link MessageContext} with a mock message or throws a {@link MessageDecodingException}.
     */
    /**
     *
     */
    class MockMessageEncoder extends AbstractMessageEncoder<MockMessage> {

        /** Whether a {@link MessageDecodingException} should be thrown by {@link #doDecode()}. */
        private boolean throwException = false;
        
        /** Mock encoded message. */
        private String message;
        
        /**
         * Get the encoded message
         * 
         * @return the string buffer
         */
        public String getEncodedMessage() {
            return message;
        }

        /**
         * Sets whether a {@link MessageDecodingException} should be thrown by {@link #doDecode()}.
         * 
         * @param shouldThrowDecodeException true if an exception should be thrown, false if not
         */
        public void setThrowException(final boolean shouldThrowDecodeException) {
            throwException = shouldThrowDecodeException;
        }

        /** {@inheritDoc} */
        protected void doEncode() throws MessageEncodingException {
            if (throwException) {
                throw new MessageEncodingException();
            } else {
                message = getMessageContext().getMessage().getEncoded();
            }
        }
        
    }
}
