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

package org.opensaml.profile.logic;

import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.ConstraintViolationException;

import org.opensaml.messaging.context.BaseContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

/**
 *
 */
public class MessageContextPredicateAdapterTest {
    
    @Test
    public void testBasic() {
        MessageContext mc = new MessageContext<>();
        ProfileRequestContext prc = new ProfileRequestContext<>();
        
        MockPredicate wrapped = new MockPredicate();
        MessageContextPredicateAdapter adapter = new MessageContextPredicateAdapter(wrapped);
        
        mc.addSubcontext(new MockContext());
        prc.setOutboundMessageContext(mc);
        
        Assert.assertTrue(adapter.apply(mc));
        
        mc.clearSubcontexts();
        Assert.assertFalse(adapter.apply(mc));
        
        Assert.assertFalse(adapter.apply(null));
        
        // No parent, unresolved PRC doesn't satisfy (default)
        mc = new MessageContext<>();
        mc.addSubcontext(new MockContext());
        Assert.assertFalse(adapter.apply(mc));
        
        // No parent, unresolved PRC does satisfy
        adapter = new MessageContextPredicateAdapter(wrapped, true);
        Assert.assertTrue(adapter.apply(mc));
    }
    
    @Test(expectedExceptions=ConstraintViolationException.class)
    public void testCtor() {
        new MessageContextPredicateAdapter(null);
    }

    
    // Helpers
    
    public static class MockContext extends BaseContext {
        public String value; 
    }
    
    public static class MockPredicate implements Predicate<ProfileRequestContext> {
        public boolean apply(@Nullable ProfileRequestContext input) {
            if (input == null || input.getOutboundMessageContext() == null) {
                return false;
            }
            return input.getOutboundMessageContext().containsSubcontext(MockContext.class);
        }
    }
}
