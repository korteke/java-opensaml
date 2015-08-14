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
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.net.HttpServletRequestResponseContext;
import net.shibboleth.utilities.java.support.net.ThreadLocalHttpServletRequestProxy;
import net.shibboleth.utilities.java.support.resource.Resource;
import net.shibboleth.utilities.java.support.resource.TestResourceConverter;
import net.shibboleth.utilities.java.support.security.BasicKeystoreKeyStrategy;
import net.shibboleth.utilities.java.support.security.DataSealer;

/** Unit test for {@link PopulateClientStorageLoadContext}. */
public class PopulateClientStorageLoadContextTest {

    private Resource keystoreResource;
    private Resource versionResource;

    private ProfileRequestContext prc;
    
    private PopulateClientStorageLoadContext action;

    @BeforeClass public void setUpClass() throws ComponentInitializationException {
        ClassPathResource resource = new ClassPathResource("/org/opensaml/storage/impl/SealerKeyStore.jks");
        Assert.assertTrue(resource.exists());
        keystoreResource = TestResourceConverter.of(resource);

        resource = new ClassPathResource("/org/opensaml/storage/impl/SealerKeyStore.kver");
        Assert.assertTrue(resource.exists());
        versionResource = TestResourceConverter.of(resource);
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
        Assert.assertTrue(ctx.getStorageKeys().contains("foo"));
    }

    @Test public void testLoaded() throws ComponentInitializationException {
        action.setStorageServices(Collections.singletonList(getStorageService()));
        action.initialize();
        
        HttpServletRequestResponseContext.loadCurrent(new MockHttpServletRequest(), new MockHttpServletResponse());
        HttpServletRequestResponseContext.getRequest().getSession().setAttribute(
                ClientStorageService.STORAGE_ATTRIBUTE + ".foo", new ClientStorageServiceStore());
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, PopulateClientStorageLoadContext.LOAD_NOT_NEEDED);
        
        final ClientStorageLoadContext ctx = prc.getSubcontext(ClientStorageLoadContext.class);
        Assert.assertNull(ctx);
    }

    private ClientStorageService getStorageService() throws ComponentInitializationException {
        final ClientStorageService ss = new ClientStorageService();
        ss.setId("test");
        ss.setStorageName("foo");

        final BasicKeystoreKeyStrategy strategy = new BasicKeystoreKeyStrategy();
        
        strategy.setKeyAlias("secret");
        strategy.setKeyPassword("kpassword");
        strategy.setKeystorePassword("password");
        strategy.setKeystoreResource(keystoreResource);
        strategy.setKeyVersionResource(versionResource);

        final DataSealer sealer = new DataSealer();
        sealer.setKeyStrategy(strategy);

        try {
            strategy.initialize();
            sealer.initialize();
        } catch (ComponentInitializationException e) {
            Assert.fail(e.getMessage());
        }

        ss.setDataSealer(sealer);
        
        ss.setHttpServletRequest(new ThreadLocalHttpServletRequestProxy());
        ss.initialize();
        
        return ss;
    }
    
}