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

package org.opensaml.messaging.handler;

import javax.annotation.Nonnull;

import org.opensaml.messaging.context.MessageContext;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Unit test for {@link AbstractMessageHandler}. */
public class AbstractMessageHandlerTest {


    /** Test a successful action run. */
    @Test
    public void testSuccess() throws Exception {

        final BaseMessageHandler handler = new BaseMessageHandler();
        handler.setId("test");
        handler.initialize();
        handler.invoke(new MessageContext());

        Assert.assertTrue(handler.didPre);
        Assert.assertTrue(handler.didExec);
        Assert.assertTrue(handler.didPost);
    }

    /** Test a failure in the preexec step. */
    @Test
    public void testPreFailure() throws Exception {

        final BaseMessageHandler handler = new PreFailMessageHandler();
        handler.setId("test");
        handler.initialize();

        try {
            handler.invoke(new MessageContext());
        } catch (MessageHandlerException e) {
            Assert.assertFalse(handler.didPre);
            Assert.assertFalse(handler.didExec);
            Assert.assertFalse(handler.didPost);
        }
    }

    /** Test a failure in the exec step. */
    @Test
    public void testExecFailure() throws Exception {

        final BaseMessageHandler handler = new ExecFailMessageHandler();
        handler.setId("test");
        handler.initialize();

        try {
            handler.invoke(new MessageContext());
        } catch (NullPointerException e) {
            Assert.assertTrue(e.getSuppressed()[0] instanceof MessageHandlerException);
            Assert.assertTrue(handler.didPre);
            Assert.assertFalse(handler.didExec);
            Assert.assertFalse(handler.didPost);
        }
    }

    /** Test an unchecked error in the exec step. */
    @Test
    public void testExecUnchecked() throws Exception {

        final BaseMessageHandler handler = new ExecUncheckedMessageHandler();
        handler.setId("test");
        handler.initialize();

        try {
            handler.invoke(new MessageContext());
        } catch (NullPointerException e) {
            Assert.assertTrue(e.getSuppressed()[0] instanceof IllegalArgumentException);
            Assert.assertTrue(handler.didPre);
            Assert.assertFalse(handler.didExec);
            Assert.assertTrue(handler.didPost);
        }
    }

    /** Test a failure in the post step. */
    @Test
    public void testPostFailure() throws Exception {

        final BaseMessageHandler handler = new PostFailMessageHandler();
        handler.setId("test");
        handler.initialize();

        try {
            handler.invoke(new MessageContext());
        } catch (NullPointerException e) {
            Assert.assertTrue(handler.didPre);
            Assert.assertTrue(handler.didExec);
            Assert.assertFalse(handler.didPost);
        }
    }

    private class BaseMessageHandler extends AbstractMessageHandler {
        private boolean didPre = false;
        private boolean didExec = false;
        private boolean didPost = false;
        
        protected boolean doPreInvoke(@Nonnull final MessageContext mc) throws MessageHandlerException {
            return didPre = true;
        }
        
        protected void doInvoke(@Nonnull final MessageContext mc) throws MessageHandlerException {
            didExec = true;
        }

        protected void doPostInvoke(@Nonnull final MessageContext mc) {
            didPost = true; 
        }
    }

    private class PreFailMessageHandler extends BaseMessageHandler {
        
        protected boolean doPreInvoke(@Nonnull final MessageContext mc) throws MessageHandlerException {
            throw new MessageHandlerException();
        }
    }
    
    private class ExecFailMessageHandler extends BaseMessageHandler {
        
        protected void doInvoke(@Nonnull final MessageContext mc) throws MessageHandlerException {
            throw new MessageHandlerException();
        }

        protected void doPostInvoke(@Nonnull final MessageContext mc) {
            throw new NullPointerException();
        }
    }

    private class ExecUncheckedMessageHandler extends BaseMessageHandler {
        
        protected void doInvoke(@Nonnull final MessageContext mc) throws MessageHandlerException {
            throw new IllegalArgumentException();
        }

        protected void doPostInvoke(@Nonnull final MessageContext mc) {
            super.doPostInvoke(mc);
            throw new NullPointerException();
        }
    }

    private class PostFailMessageHandler extends BaseMessageHandler {
        
        protected void doPostInvoke(@Nonnull final MessageContext mc) {
            throw new NullPointerException();
        }
    }

}