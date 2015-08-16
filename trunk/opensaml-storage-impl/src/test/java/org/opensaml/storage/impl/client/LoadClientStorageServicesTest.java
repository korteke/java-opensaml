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

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.locks.Lock;

import javax.servlet.http.Cookie;

import org.opensaml.profile.RequestContextBuilder;
import org.opensaml.profile.action.ActionTestingSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.storage.StorageRecord;
import org.opensaml.storage.StorageService;
import org.opensaml.storage.impl.client.ClientStorageService.ClientStorageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Optional;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.logic.ConstraintViolationException;
import net.shibboleth.utilities.java.support.net.HttpServletRequestResponseContext;
import net.shibboleth.utilities.java.support.net.ThreadLocalHttpServletRequestProxy;
import net.shibboleth.utilities.java.support.net.URISupport;
import net.shibboleth.utilities.java.support.resource.Resource;
import net.shibboleth.utilities.java.support.resource.TestResourceConverter;
import net.shibboleth.utilities.java.support.security.BasicKeystoreKeyStrategy;
import net.shibboleth.utilities.java.support.security.DataSealer;

/** Unit test for {@link LoadClientStorageServices}. */
public class LoadClientStorageServicesTest {

    private Resource keystoreResource;
    private Resource versionResource;

    private ProfileRequestContext prc;
    private ClientStorageLoadContext loadCtx;
    
    private LoadClientStorageServices action;

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
        loadCtx = prc.getSubcontext(ClientStorageLoadContext.class, true);
        loadCtx.getStorageKeys().add("foo");
        
        action = new LoadClientStorageServices();
        action.setHttpServletRequest(new ThreadLocalHttpServletRequestProxy());
        HttpServletRequestResponseContext.loadCurrent(new MockHttpServletRequest(), new MockHttpServletResponse());
    }

    @Test public void testNoContext() throws ComponentInitializationException {
        action.setStorageServices(Collections.singletonList(getStorageService()));
        action.initialize();
        
        prc.removeSubcontext(loadCtx);
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_PROFILE_CTX);
    }

    @Test public void testNoKeys() throws ComponentInitializationException {
        action.initialize();
                
        loadCtx.getStorageKeys().clear();
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
    }

    @Test public void testNoServices() throws ComponentInitializationException {
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
    }
 
    @Test public void testEmpty() throws ComponentInitializationException {
        final ClientStorageService ss = getStorageService();

        final Lock lock = ss.getLock().readLock();
        try {
            lock.lock();
            ss.getContextMap();
            Assert.fail("getContextMap should have failed for unloaded storage service");
        } catch(final ConstraintViolationException e) {
            
        } finally {
            lock.unlock();
        }

        action.setStorageServices(Collections.singletonList(ss));
        action.initialize();

        final Cookie cookie = new Cookie("bar", "ignored");
        ((MockHttpServletRequest) HttpServletRequestResponseContext.getRequest()).setCookies(cookie);
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        try {
            lock.lock();
            Assert.assertTrue(ss.getContextMap().isEmpty());
        } finally {
            lock.unlock();
        }
    }

    @Test public void testInvalid() throws ComponentInitializationException {
        final ClientStorageService ss = getStorageService();
        action.setStorageServices(Collections.singletonList(ss));
        action.initialize();

        final Cookie cookie = new Cookie("foo", "error");
        ((MockHttpServletRequest) HttpServletRequestResponseContext.getRequest()).setCookies(cookie);
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        final Lock lock = ss.getLock().readLock();
        try {
            lock.lock();
            Assert.assertTrue(ss.getContextMap().isEmpty());
        } finally {
            lock.unlock();
        }
    }

    @Test public void testCookieLoad() throws ComponentInitializationException, IOException {
        final ClientStorageService ss = getStorageService();
        ss.load(null, ClientStorageSource.COOKIE);
        ss.create("context1", "key1", "value1", null);
        ss.create("context1", "key2", "value2", null);
        ss.create("context2", "key", "value", null);
        
        final Optional<String> saved = ss.save();
        Assert.assertTrue(saved.isPresent());

        HttpServletRequestResponseContext.loadCurrent(new MockHttpServletRequest(), new MockHttpServletResponse());

        Assert.assertFalse(ss.isLoaded());
        
        final Cookie cookie = new Cookie("foo", URISupport.doURLEncode(saved.get()));
        ((MockHttpServletRequest) HttpServletRequestResponseContext.getRequest()).setCookies(cookie);

        action.setUseLocalStorage(true);
        action.setStorageServices(Collections.singletonList(ss));
        action.initialize();

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        checkStorageContent(ss);
    }

    @Test public void testFormLoad() throws ComponentInitializationException, IOException {
        final ClientStorageService ss = getStorageService();
        ss.load(null, ClientStorageSource.COOKIE);
        ss.create("context1", "key1", "value1", null);
        ss.create("context1", "key2", "value2", null);
        ss.create("context2", "key", "value", null);
        
        final Optional<String> saved = ss.save();
        Assert.assertTrue(saved.isPresent());

        HttpServletRequestResponseContext.loadCurrent(new MockHttpServletRequest(), new MockHttpServletResponse());

        Assert.assertFalse(ss.isLoaded());
        
        final MockHttpServletRequest request = (MockHttpServletRequest) HttpServletRequestResponseContext.getRequest();
        request.setParameter(LoadClientStorageServices.SUPPORT_FORM_FIELD, "true");
        request.setParameter(LoadClientStorageServices.SUCCESS_FORM_FIELD + '.' + ss.getStorageName(), "true");
        request.setParameter(LoadClientStorageServices.VALUE_FORM_FIELD + '.' + ss.getStorageName(), saved.get());

        action.setUseLocalStorage(true);
        action.setStorageServices(Collections.singletonList(ss));
        action.initialize();

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        checkStorageContent(ss);
    }

    private void checkStorageContent(final StorageService ss) throws IOException {
        StorageRecord record = ss.read("context1", "key1");
        Assert.assertNotNull(record);
        Assert.assertEquals(record.getValue(), "value1");
        Assert.assertNull(record.getExpiration());
        
        record = ss.read("context1", "key2");
        Assert.assertNotNull(record);
        Assert.assertEquals(record.getValue(), "value2");
        Assert.assertNull(record.getExpiration());

        record = ss.read("context2", "key");
        Assert.assertNotNull(record);
        Assert.assertEquals(record.getValue(), "value");
        Assert.assertNull(record.getExpiration());
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