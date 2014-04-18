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

package org.opensaml.messaging.handler.impl;

import javax.annotation.Nullable;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.messaging.handler.impl.CheckMandatoryIssuer;
import org.testng.annotations.Test;

import com.google.common.base.Function;

/** Unit test for {@link CheckMandatoryIssuer}. */
public class CheckMandatoryIssuerTest {

    @Test public void testWithIssuer() throws Exception {
        final CheckMandatoryIssuer action = new CheckMandatoryIssuer();
        action.setIssuerLookupStrategy(new MockIssuer("issuer"));
        action.initialize();

        final MessageContext mc = new MessageContext();
        action.invoke(mc);
    }

    @Test(expectedExceptions=MessageHandlerException.class) public void testNoIssuer() throws Exception {
        final CheckMandatoryIssuer action = new CheckMandatoryIssuer();
        action.setIssuerLookupStrategy(new MockIssuer(null));
        action.initialize();

        final MessageContext mc = new MessageContext();
        action.invoke(mc);
    }
    
    private class MockIssuer implements Function<MessageContext,String> {

        final String issuer;
        
        MockIssuer(@Nullable final String s) {
            issuer = s;
        }
        
        /** {@inheritDoc} */
        public String apply(MessageContext input) {
            return issuer;
        }
        
    }
}