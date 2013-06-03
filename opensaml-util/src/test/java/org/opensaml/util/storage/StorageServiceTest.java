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

package org.opensaml.util.storage;

import java.io.IOException;
import java.security.SecureRandom;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test of {@link StorageService} implementations.
 */
public abstract class StorageServiceTest {
    
    private SecureRandom random;
    
    protected StorageService shared;

    /**
     * Returns a fresh service instance to test.
     * 
     * @return  a new instance
     */
    @Nonnull protected abstract StorageService getStorageService();

    @BeforeClass
    protected void setUp() throws ComponentInitializationException {
        random = new SecureRandom();
        shared = getStorageService();
        shared.initialize();
    }
    
    @AfterClass
    protected void tearDown() {
        shared.destroy();
    }

    @Test(threadPoolSize = 10, invocationCount = 10,  timeOut = 10000)
    public void strings() throws IOException {
        String context = Long.toString(random.nextLong());
        
        for (int i = 1; i <= 100; i++) {
            shared.create(context, Integer.toString(i), Integer.toString(i + 1), System.currentTimeMillis() + 300000);
        }
        
        for (int i = 1; i <= 100; i++) {
            StorageRecord rec = shared.read(context, Integer.toString(i));
            Assert.assertNotNull(rec);
            Assert.assertEquals(rec.getValue(), Integer.toString(i + 1));
        }

        for (int i = 1; i <= 100; i++) {
            shared.update(context, Integer.toString(i), Integer.toString(i + 2));
        }

        for (int i = 1; i <= 100; i++) {
            StorageRecord rec = shared.read(context, Integer.toString(i));
            Assert.assertNotNull(rec);
            Assert.assertEquals(rec.getValue(), Integer.toString(i + 2));
        }

        for (int i = 1; i <= 100; i++) {
            boolean result = shared.create(context, Integer.toString(i), Integer.toString(i + 1));
            Assert.assertFalse(result, "createString should have failed");
        }        
        
        for (int i = 1; i <= 100; i++) {
            shared.delete(context, Integer.toString(i));
            StorageRecord rec = shared.read(context, Integer.toString(i));
            Assert.assertNull(rec);
        }
    }

    @Test(threadPoolSize = 10, invocationCount = 10,  timeOut = 10000)
    public void texts() throws IOException {
        String context = Long.toString(random.nextLong());
        
        for (int i = 1; i <= 100; i++) {
            shared.create(context, Integer.toString(i), Integer.toString(i + 1), System.currentTimeMillis() + 300000);
        }
        
        for (int i = 1; i <= 100; i++) {
            StorageRecord rec = shared.read(context, Integer.toString(i));
            Assert.assertNotNull(rec);
            Assert.assertEquals(rec.getValue(), Integer.toString(i + 1));
        }

        for (int i = 1; i <= 100; i++) {
            shared.update(context, Integer.toString(i), Integer.toString(i + 2));
        }

        for (int i = 1; i <= 100; i++) {
            StorageRecord rec = shared.read(context, Integer.toString(i));
            Assert.assertNotNull(rec);
            Assert.assertEquals(rec.getValue(), Integer.toString(i + 2));
        }

        for (int i = 1; i <= 100; i++) {
            boolean result = shared.create(context, Integer.toString(i), Integer.toString(i + 1));
            Assert.assertFalse(result, "createText should have failed");
        }        
        
        for (int i = 1; i <= 100; i++) {
            shared.delete(context, Integer.toString(i));
            StorageRecord rec = shared.read(context, Integer.toString(i));
            Assert.assertNull(rec);
        }
    }
    
    @Test
    public void expiration() throws IOException, InterruptedException {
        String context = Long.toString(random.nextLong());
        
        for (int i = 1; i <= 100; i++) {
            shared.create(context, Integer.toString(i), Integer.toString(i + 1), System.currentTimeMillis() + 5000);
        }

        Thread.sleep(5 * 1000);
        
        for (int i = 1; i <= 100; i++) {
            StorageRecord rec = shared.read(context, Integer.toString(i));
            Assert.assertNull(rec);
        }
    }
    
    @Test
    public void updates() throws IOException, VersionMismatchException {
        String key = "key";
        String context = Long.toString(random.nextLong());
        
        shared.create(context, key, "foo");
        
        shared.updateWithVersion(1, context, key, "bar");
        
        try {
            shared.updateWithVersion(1, context, key, "baz");
            Assert.fail("updateStringWithVersion should have failed");
        } catch (VersionMismatchException e) {
            // expected
        }
        
        StorageRecord rec = shared.read(context, key);
        Assert.assertNotNull(rec);
        Assert.assertEquals(rec.getVersion(), 2);
    }
}