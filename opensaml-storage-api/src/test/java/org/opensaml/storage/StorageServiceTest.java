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

package org.opensaml.storage;

import java.io.IOException;
import java.security.SecureRandom;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.opensaml.storage.StorageRecord;
import org.opensaml.storage.StorageService;
import org.opensaml.storage.VersionMismatchException;
import org.opensaml.storage.annotation.Context;
import org.opensaml.storage.annotation.Expiration;
import org.opensaml.storage.annotation.Key;
import org.opensaml.storage.annotation.Value;
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
            shared.update(context, Integer.toString(i), Integer.toString(i + 2), System.currentTimeMillis() + 300000);
        }

        for (int i = 1; i <= 100; i++) {
            StorageRecord rec = shared.read(context, Integer.toString(i));
            Assert.assertNotNull(rec);
            Assert.assertEquals(rec.getValue(), Integer.toString(i + 2));
        }

        for (int i = 1; i <= 100; i++) {
            boolean result = shared.create(context, Integer.toString(i), Integer.toString(i + 1), null);
            Assert.assertFalse(result, "createString should have failed");
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

        Thread.sleep(5000);
        
        for (int i = 1; i <= 100; i++) {
            StorageRecord rec = shared.read(context, Integer.toString(i));
            Assert.assertNull(rec);
        }
    }
    
    @Test
    public void updates() throws IOException, VersionMismatchException {
        String key = "key";
        String context = Long.toString(random.nextLong());
        
        shared.create(context, key, "foo", null);
        
        shared.updateWithVersion(1, context, key, "bar", null);
        
        try {
            shared.updateWithVersion(1, context, key, "baz", null);
            Assert.fail("updateStringWithVersion should have failed");
        } catch (VersionMismatchException e) {
            // expected
        }
        
        StorageRecord rec = shared.read(context, key);
        Assert.assertNotNull(rec);
        Assert.assertEquals(rec.getVersion(), 2);
    }
    
    @Test
    public void objects() throws IOException, InterruptedException {
        AnnotatedObject o1 = new AnnotatedObject();
        AnnotatedObject o2 = new AnnotatedObject();
        
        o1.generate();
        shared.create(o1);
        
        o2.setContext(o1.getContext());
        o2.setKey(o1.getKey());
        Assert.assertSame(o2, shared.read(o2));
        Assert.assertEquals(o1.getValue(), o2.getValue());
        
        o2.setValue("foo");
        o2.setExpiration(System.currentTimeMillis() + 5000);
        shared.update(o2);
        
        shared.read(o1);
        Assert.assertEquals(o1.getValue(), "foo");
        Assert.assertEquals(o1.getExpiration(), o2.getExpiration());
        
        Thread.sleep(5000);
        
        Assert.assertNull(shared.read(o2));
    }
    
    @Context("context")
    @Key("key")
    @Value("value")
    @Expiration("expiration")
    private class AnnotatedObject {

        private String context;
        private String key;
        private String value;
        private Long expiration;
        
        public void generate() {
            context = Long.toString(random.nextLong());
            key = Long.toString(random.nextLong());
            value = Long.toString(random.nextLong());
            expiration = System.currentTimeMillis() + 5000;
        }
        
        public String getContext() {
            return context;
        }
        
        public void setContext(String context) {
            this.context = context;
        }
        
        public String getKey() {
            return key;
        }
        
        public void setKey(String key) {
            this.key = key;
        }
        
        public String getValue() {
            return value;
        }
        
        public void setValue(String value) {
            this.value = value;
        }
        
        public long getExpiration() {
            return expiration;
        }
        
        public void setExpiration(long expiration) {
            this.expiration = expiration;
        }
        
    }
}