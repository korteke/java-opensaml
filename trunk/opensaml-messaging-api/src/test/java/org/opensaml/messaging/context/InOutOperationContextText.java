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

package org.opensaml.messaging.context;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 */
public class InOutOperationContextText {
    
    @Test
    public void testInboundContextParentLinkage() {
        MessageContext mc1 = new MessageContext<>();
        MessageContext mc2 = new MessageContext<>();
        
       InOutOperationContext opContext = new InOutOperationContext<>(mc1, null);
       
       Assert.assertSame(opContext.getInboundMessageContext(), mc1);
       Assert.assertSame(mc1.getParent(), opContext);
       
       opContext.setInboundMessageContext(mc2);
       
       Assert.assertNull(mc1.getParent());
       Assert.assertSame(opContext.getInboundMessageContext(), mc2);
       Assert.assertSame(mc2.getParent(), opContext);
       
       opContext.setInboundMessageContext(null);
       
       Assert.assertNull(mc2.getParent());
       Assert.assertNull(opContext.getInboundMessageContext());
    }
    
    @Test
    public void testOutboundContextParentLinkage() {
        MessageContext mc1 = new MessageContext<>();
        MessageContext mc2 = new MessageContext<>();
        
       InOutOperationContext opContext = new InOutOperationContext<>(null, mc1);
       
       Assert.assertSame(opContext.getOutboundMessageContext(), mc1);
       Assert.assertSame(mc1.getParent(), opContext);
       
       opContext.setOutboundMessageContext(mc2);
       
       Assert.assertNull(mc1.getParent());
       Assert.assertSame(opContext.getOutboundMessageContext(), mc2);
       Assert.assertSame(mc2.getParent(), opContext);
       
       opContext.setOutboundMessageContext(null);
       
       Assert.assertNull(mc2.getParent());
       Assert.assertNull(opContext.getOutboundMessageContext());
    }

}
