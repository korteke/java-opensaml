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

package org.opensaml.storage.impl.client;

import java.util.Collections;

import org.opensaml.profile.RequestContextBuilder;
import org.opensaml.profile.action.ActionTestingSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.storage.impl.client.ClientStorageService.ClientStorageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.net.HttpServletRequestResponseContext;

/** Unit test for {@link PopulateClientStorageLoadContext}. */
public class PopulateClientStorageLoadContextTest extends AbstractBaseClientStorageServiceTest {

    private ProfileRequestContext prc;
    
    private PopulateClientStorageLoadContext action;

    @BeforeClass public void setUpClass() throws ComponentInitializationException {
        init();
    }

    @BeforeMethod public void setUp() {
        prc = new RequestContextBuilder().buildProfileRequestContext();
        action = new PopulateClientStorageLoadContext();
    }
        
    @Test public void testNoServices() throws ComponentInitializationException {
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, PopulateClientStorageLoadContext.LOAD_NOT_NEEDED);
        Assert.assertNull(prc.getSubcontext(ClientStorageLoadContext.class));
    }
 
    @Test public void testUnloaded() throws ComponentInitializationException {
        action.setStorageServices(Collections.singletonList(getStorageService()));
        action.initialize();
        
        HttpServletRequestResponseContext.loadCurrent(new MockHttpServletRequest(), new MockHttpServletResponse());

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        final ClientStorageLoadContext ctx = prc.getSubcontext(ClientStorageLoadContext.class);
        Assert.assertNotNull(ctx);
        Assert.assertEquals(ctx.getStorageKeys().size(), 1);
        Assert.assertTrue(ctx.getStorageKeys().contains(STORAGE_NAME));
    }

    @Test public void testLoaded() throws ComponentInitializationException {
        final ClientStorageService ss = getStorageService();
        action.setStorageServices(Collections.singletonList(ss));
        action.initialize();
        
        HttpServletRequestResponseContext.loadCurrent(new MockHttpServletRequest(), new MockHttpServletResponse());

        ss.load(null, ClientStorageSource.HTML_LOCAL_STORAGE);
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, PopulateClientStorageLoadContext.LOAD_NOT_NEEDED);
        
        final ClientStorageLoadContext ctx = prc.getSubcontext(ClientStorageLoadContext.class);
        Assert.assertNull(ctx);
    }
    
}