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

package org.opensaml.storage.impl;

import java.io.IOException;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.net.CookieManager;
import net.shibboleth.utilities.java.support.net.HttpServletRequestResponseContext;
import net.shibboleth.utilities.java.support.net.ThreadLocalHttpServletRequestProxy;
import net.shibboleth.utilities.java.support.net.ThreadLocalHttpServletResponseProxy;
import net.shibboleth.utilities.java.support.resource.Resource;
import net.shibboleth.utilities.java.support.resource.TestResourceConverter;
import net.shibboleth.utilities.java.support.security.BasicKeystoreKeyStrategy;
import net.shibboleth.utilities.java.support.security.DataSealer;

import org.opensaml.storage.StorageRecord;
import org.opensaml.storage.StorageService;
import org.opensaml.storage.StorageServiceTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test of {@link ServletRequestScopedStorageService} implementation.
 */
public class ServletRequestScopedStorageServiceTest extends StorageServiceTest {

    private Resource keystoreResource;
    private Resource versionResource;

    /**
     * Convert the Spring resource to a java-support resource.
     * 
     * @throws ComponentInitializationException
     */
    @BeforeClass public void setUp() throws ComponentInitializationException {
        ClassPathResource resource = new ClassPathResource("/org/opensaml/storage/impl/SealerKeyStore.jks");
        Assert.assertTrue(resource.exists());
        keystoreResource = TestResourceConverter.of(resource);

        resource = new ClassPathResource("/org/opensaml/storage/impl/SealerKeyStore.kver");
        Assert.assertTrue(resource.exists());
        versionResource = TestResourceConverter.of(resource);
        
        super.setUp();
    }
    
    /** {@inheritDoc} */
    @Override
    @Nonnull protected StorageService getStorageService() {
        final ServletRequestScopedStorageService ss = new ServletRequestScopedStorageService();
        ss.setId("test");
        ss.setCleanupInterval(0);

        final BasicKeystoreKeyStrategy strategy = new BasicKeystoreKeyStrategy();
        
        strategy.setKeyAlias("secret");
        strategy.setKeyPassword("kpassword");
        strategy.setKeystorePassword("password");
        strategy.setKeystoreResource(keystoreResource);
        strategy.setKeyVersionResource(versionResource);

        final DataSealer sealer = new DataSealer();
        sealer.setKeyStrategy(strategy);

        final CookieManager cookieManager = new CookieManager();
        cookieManager.setHttpServletRequest(new ThreadLocalHttpServletRequestProxy());
        cookieManager.setHttpServletResponse(new ThreadLocalHttpServletResponseProxy());

        try {
            strategy.initialize();
            sealer.initialize();
            cookieManager.initialize();
        } catch (ComponentInitializationException e) {
            Assert.fail(e.getMessage());
        }

        ss.setDataSealer(sealer);
        ss.setCookieManager(cookieManager);
        ss.setCookieName("test");
        
        ss.setHttpServletRequest(new ThreadLocalHttpServletRequestProxy());
        ss.setHttpServletResponse(new ThreadLocalHttpServletResponseProxy());
        return ss;
    }

    /** {@inheritDoc} */
    @Override
    protected void threadInit() {
        super.threadInit();
        HttpServletRequestResponseContext.loadCurrent(new MockHttpServletRequest(), new MockHttpServletResponse());
    }
    
    @Test public void invalidConfig() {
        ServletRequestScopedStorageService ss = new ServletRequestScopedStorageService();
        
        try {
            ss.initialize();
            Assert.fail();
        } catch (ComponentInitializationException e) {
            
        }
    }
    
    @Test(threadPoolSize = 10, invocationCount = 10,  timeOut = 10000)
    public void loadSave() throws IOException {
        threadInit();
        
        MockHttpServletRequest mockRequest = (MockHttpServletRequest) HttpServletRequestResponseContext.getRequest();
        MockHttpServletResponse mockResponse = (MockHttpServletResponse) HttpServletRequestResponseContext.getResponse();
        
        ServletRequestScopedStorageService ss = (ServletRequestScopedStorageService) shared;
        
        ss.load();
        
        String context = Long.toString(random.nextLong());
        
        for (int i = 1; i <= 10; i++) {
            ss.create(context, Integer.toString(i), Integer.toString(i + 1), System.currentTimeMillis() + 300000);
        }
        
        ss.save();
        Assert.assertNotNull(mockResponse.getCookie("test"));
        mockRequest.setAttribute(ServletRequestScopedStorageService.CONTEXT_MAP_ATTRIBUTE, null);
        
        for (int i = 1; i <= 10; i++) {
            Assert.assertNull(ss.read(context, Integer.toString(i)));
        }
        
        mockRequest.setCookies(mockResponse.getCookie("test"));
        ss.load();
        for (int i = 1; i <= 10; i++) {
            StorageRecord record = ss.read(context, Integer.toString(i));
            Assert.assertNotNull(record);
            Assert.assertEquals(record.getValue(), Integer.toString(i + 1));
            Assert.assertTrue(record.getExpiration() < System.currentTimeMillis() + 300000);
        }
    }

}