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

import org.opensaml.util.storage.impl.MemoryStorageService;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;

/**
 * Tests for {@link ReplayCache}
 */
public class ReplayCacheTest {

    private String context;
    
    private String messageID;
    
    private long expiration;

    private StorageService storageService;
    
    private ReplayCache replayCache;

    @BeforeMethod
    protected void setUp() throws Exception {
        context = getClass().getName();
        messageID = "abc123";
        expiration = System.currentTimeMillis() + 180000;

        storageService = new MemoryStorageService();
        storageService.initialize();
        
        replayCache = new ReplayCache();
        replayCache.setStorage(storageService);
        replayCache.initialize();
    }
    
    @AfterMethod
    protected void tearDown() {
        replayCache.destroy();
        replayCache = null;
        
        storageService.destroy();
        storageService = null;
    }

    /**
     * Test valid non-replayed message ID.
     */
    @Test
    public void testNonReplayEmptyCache() {
        
        Assert.assertTrue(replayCache.check(context, messageID, expiration),
                "Message was not replay, insert into empty cache");
    }

    /**
     * Test valid non-replayed message ID.
     */
    @Test
    public void testNonReplayDistinctIDs() {

        Assert.assertTrue(replayCache.check(context, messageID, expiration),
                "Message was not replay, insert into empty cache");
        Assert.assertTrue(replayCache.check(context, "IDWhichIsNot" + messageID, expiration),
                "Message was not replay, insert into empty cache");
    }

    /**
     * Test invalid replayed message ID, using replay cache default expiration duration.
     */
    @Test
    public void testReplay() {
        
        Assert.assertTrue(replayCache.check(context, messageID, expiration),
                "Message was not replay, insert into empty cache");
        Assert.assertFalse(replayCache.check(context, messageID, expiration),
                "Message was replay");
    }

    /**
     * Test valid replayed message ID, setting expiration by short duration.
     * 
     * @throws InterruptedException
     */
    @Test
    public void testNonReplayValidByMillisecondExpiriation() throws InterruptedException {

        Assert.assertTrue(replayCache.check(context, messageID, System.currentTimeMillis() + 1000),
                "Message was not replay, insert into empty cache");
        
        // Sleep for 2 seconds to make sure replay cache entry has expired
        Thread.sleep(2000L);
        
        Assert.assertTrue(replayCache.check(context, messageID, System.currentTimeMillis() + 1000),
                "Message was not replay, previous cache entry should have expired");
    }
}