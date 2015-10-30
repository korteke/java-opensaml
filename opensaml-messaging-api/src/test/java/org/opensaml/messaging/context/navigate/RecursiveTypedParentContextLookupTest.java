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

package org.opensaml.messaging.context.navigate;

import net.shibboleth.utilities.java.support.logic.ConstraintViolationException;

import org.opensaml.messaging.context.BaseContext;
import org.opensaml.messaging.context.InOutOperationContext;
import org.opensaml.messaging.context.MessageContext;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 */
public class RecursiveTypedParentContextLookupTest {

    @Test
    public void testBasic() {
        MockContext1 mc1 = new MockContext1();
        MockContext2 mc2 = new MockContext2();
        MockContext3 mc3 = new MockContext3();
        MessageContext in = new MessageContext<>();
        InOutOperationContext opContext = new InOutOperationContext<>(null, null);
        
        opContext.setInboundMessageContext(in);
        opContext.getInboundMessageContext().addSubcontext(mc1);
        mc1.addSubcontext(mc2);
        mc2.addSubcontext(mc3);
        
        Assert.assertSame(new RecursiveTypedParentContextLookup<>(MockContext2.class).apply(mc3), mc2);
        Assert.assertSame(new RecursiveTypedParentContextLookup<>(MockContext1.class).apply(mc3), mc1);
        Assert.assertSame(new RecursiveTypedParentContextLookup<>(MessageContext.class).apply(mc3), in);
        Assert.assertSame(new RecursiveTypedParentContextLookup<>(InOutOperationContext.class).apply(mc3), opContext);
        
        Assert.assertNull(new RecursiveTypedParentContextLookup<>(MockContextNotThere.class).apply(mc3));
        Assert.assertNull(new RecursiveTypedParentContextLookup<>(InOutOperationContext.class).apply(null));
    }
    
    @Test(expectedExceptions=ConstraintViolationException.class)
    public void testCtor() {
        new RecursiveTypedParentContextLookup<>(null);
    }
    
    // Helpers
    
    public static class MockContext1 extends BaseContext {
        public String value;
    }
    public static class MockContext2 extends BaseContext {
        public String value;
    }
    public static class MockContext3 extends BaseContext {
        public String value;
    }
    public static class MockContextNotThere extends BaseContext {
        public String value;
    }
     
}
