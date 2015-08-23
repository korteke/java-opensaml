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

import org.springframework.core.io.ClassPathResource;
import org.testng.Assert;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.net.CookieManager;
import net.shibboleth.utilities.java.support.net.ThreadLocalHttpServletRequestProxy;
import net.shibboleth.utilities.java.support.net.ThreadLocalHttpServletResponseProxy;
import net.shibboleth.utilities.java.support.resource.Resource;
import net.shibboleth.utilities.java.support.resource.TestResourceConverter;
import net.shibboleth.utilities.java.support.security.BasicKeystoreKeyStrategy;
import net.shibboleth.utilities.java.support.security.DataSealer;

/** Base class for client storage tests. */
public class AbstractBaseClientStorageServiceTest {

    public static final String STORAGE_NAME = "foo";
    
    private Resource keystoreResource;
    private Resource versionResource;

    protected void init() throws ComponentInitializationException {
        ClassPathResource resource = new ClassPathResource("/org/opensaml/storage/impl/SealerKeyStore.jks");
        Assert.assertTrue(resource.exists());
        keystoreResource = TestResourceConverter.of(resource);

        resource = new ClassPathResource("/org/opensaml/storage/impl/SealerKeyStore.kver");
        Assert.assertTrue(resource.exists());
        versionResource = TestResourceConverter.of(resource);
    }

    protected ClientStorageService getStorageService() throws ComponentInitializationException {
        final ClientStorageService ss = new ClientStorageService();
        ss.setId("test");
        ss.setStorageName(STORAGE_NAME);

        final CookieManager cm = new CookieManager();
        cm.setHttpServletRequest(new ThreadLocalHttpServletRequestProxy());
        cm.setHttpServletResponse(new ThreadLocalHttpServletResponseProxy());
        cm.initialize();
        ss.setCookieManager(cm);

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