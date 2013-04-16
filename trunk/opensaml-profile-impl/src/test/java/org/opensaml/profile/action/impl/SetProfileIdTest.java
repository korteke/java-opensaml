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

import org.opensaml.profile.action.impl.SetProfileId;
import org.opensaml.profile.context.EventContext;
import org.opensaml.profile.context.ProfileRequestContext;
import net.shibboleth.utilities.java.support.logic.ConstraintViolationException;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 */
public class SetProfileIdTest {

    @Test
    public void testInstantiation() {
        SetProfileId action = new SetProfileId("foo");
        Assert.assertEquals(action.getProfileId(), "foo");

        try {
            new SetProfileId(null);
            Assert.fail();
        } catch (ConstraintViolationException e) {
            // expected this
        }

        try {
            new SetProfileId("  ");
            Assert.fail();
        } catch (ConstraintViolationException e) {
            // expected this
        }
    }

    @Test
    public void testExecute() throws Exception {
        ProfileRequestContext context = new ProfileRequestContext();

        SetProfileId action = new SetProfileId("foo");
        action.execute(context);
        Assert.assertEquals(context.getProfileId(), "foo");
        Assert.assertNull(context.getSubcontext(EventContext.class));
    }
}