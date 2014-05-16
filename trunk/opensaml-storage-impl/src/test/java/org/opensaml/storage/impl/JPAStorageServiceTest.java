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
import java.security.SecureRandom;
import java.util.List;

import javax.annotation.Nonnull;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.opensaml.storage.StorageRecord;
import org.opensaml.storage.StorageService;
import org.opensaml.storage.StorageServiceTest;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test of {@link JPAStorageService} implementation.
 */
public class JPAStorageServiceTest extends StorageServiceTest {

    /** Storage service to test. */
    protected StorageService storageService;

    /** Entity manager factory. */
    private EntityManagerFactory factory;

    /** Contexts used for testing. */
    private Object[][] contexts;

    public JPAStorageServiceTest() {
        final SecureRandom random = new SecureRandom();
        contexts = new Object[10][1];
        for (int i = 0; i < 10; i++) {
            contexts[i] = new Object[] {Long.toString(random.nextLong()), };
        }
    }

    /**
     * Creates an entity manager factory instance.
     */
    @BeforeTest public void setupEntityManagerFactory() {
        factory = Persistence.createEntityManagerFactory("JPAStorageService");
    }

    /**
     * Removes all test data from the database.
     */
    @AfterTest public void clearDatabase() throws ComponentInitializationException, IOException {
        JPAStorageService ss = new JPAStorageService(Persistence.createEntityManagerFactory("JPAStorageService"));
        ss.initialize();
        List<String> contexts = ss.readContexts();
        for (String ctx : contexts) {
            ss.deleteContext(ctx);
        }
        List<StorageRecord> recs = ss.readAll();
        Assert.assertEquals(recs.size(), 0);
        ss.destroy();
    }

    @Nonnull protected StorageService getStorageService() {
        JPAStorageService ss = new JPAStorageService(factory);
        return ss;
    }

    @Test
    public void validConfig() throws ComponentInitializationException {
        JPAStorageService ss = new JPAStorageService(Persistence.createEntityManagerFactory("JPAStorageService"));
        
        ss.initialize();
        ss.destroy();
    }

    @Test
    public void cleanup() throws ComponentInitializationException, IOException {
        JPAStorageService ss = new JPAStorageService(Persistence.createEntityManagerFactory("JPAStorageService"));
        ss.setCleanupInterval(5);
        ss.initialize();

        String context = Long.toString(random.nextLong());
        for (int i = 1; i <= 100; i++) {
            ss.create(context, Integer.toString(i), Integer.toString(i + 1), System.currentTimeMillis() + 100);
        }
        try {
            Thread.sleep(7500);
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
        List<StorageRecord> recs = ss.readAll(context);
        Assert.assertEquals(recs.size(), 0);

        ss.destroy();
    }

    @DataProvider(name = "contexts")
    public Object[][] contexts() throws Exception {
        return contexts;
    }

    @Test(dataProvider = "contexts", singleThreaded = false, threadPoolSize = 25, invocationCount = 100)
    public void multithread(final String context) throws IOException {
        shared.create(context, "mt", "bar", System.currentTimeMillis() + 300000);
        StorageRecord rec = shared.read(context, "mt");
        Assert.assertNotNull(rec);
        shared.update(context, "mt", "baz", System.currentTimeMillis() + 300000);
        rec = shared.read(context, "mt");
        Assert.assertNotNull(rec);
        boolean result = shared.create(context, "mt", "qux", null);
        Assert.assertFalse(result, "createString should have failed");
    }
}