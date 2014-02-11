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

package org.opensaml.xacml.ctx.provider;



import org.opensaml.xacml.policy.ObligationType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * test for org.opensaml.xacml.ctx.provider.BaseObligationHandler
 */
public class BaseObligationHandlerTest {

    private static final String TEST_ID = "testId";
    
    @Test
    public void constructorTest() {
        new TestClass(TEST_ID);
        
        try {
            new TestClass(null, 32);
            Assert.fail();
        }
        catch (IllegalArgumentException e) {
            //  OK
        }
    }
    
    @Test
    public void equalsHashTest() {
        TestClass tc = new TestClass(TEST_ID);
        
        Assert.assertFalse(tc.equals(null));
        Assert.assertTrue(tc.equals(tc));
        Assert.assertTrue(tc.equals(new TestClass(TEST_ID)));
        Assert.assertEquals(tc.hashCode(), new TestClass(TEST_ID).hashCode());
        Assert.assertFalse(tc.equals(new Integer(3)));
        
    }


    private static class TestClass extends BaseObligationHandler {

        protected TestClass(String obligationId, int handlerPrecedence) {
            super(obligationId, handlerPrecedence);
        }

        /**
         * Constructor.
         * 
         * @param obligationId
         */
        protected TestClass(String obligationId) {
            super(obligationId);
        }

        /** {@inheritDoc} */
        public void evaluateObligation(ObligationProcessingContext context, ObligationType obligation)
                throws ObligationProcessingException {
            // TODO Auto-generated method stub

        }

    }
}
