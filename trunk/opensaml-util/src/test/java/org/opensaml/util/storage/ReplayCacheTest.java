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

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.AssertJUnit;
import java.util.HashMap;


/**
 * Testing SAML message replay security policy rule.
 */
public class ReplayCacheTest {

    private String messageID;

    private HashMap<String, ReplayCacheEntry> storageEngine;

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        messageID = "abc123";

        storageEngine = new HashMap<String, ReplayCacheEntry>();
    }

    /**
     * Test valid non-replayed message ID.
     */
    @Test
    public void testNonReplayEmptyCache() {
        ReplayCache replayCache = new ReplayCache(storageEngine, 10000);
        AssertJUnit.assertFalse("Message was not replay, insert into empty cache", replayCache.isReplay("test", messageID));
    }

    /**
     * Test valid non-replayed message ID.
     */
    @Test
    public void testNonReplayDistinctIDs() {
        ReplayCache replayCache = new ReplayCache(storageEngine, 10000);
        AssertJUnit.assertFalse("Message was not replay, insert into empty cache", replayCache.isReplay("test", messageID));
        AssertJUnit.assertFalse("Message was not replay, insert into empty cache", replayCache.isReplay("test", "IDWhichIsNot"
                + messageID));
    }

    /**
     * Test invalid replayed message ID, using replay cache default expiration duration.
     */
    @Test
    public void testReplay() {
        ReplayCache replayCache = new ReplayCache(storageEngine, 10000);
        AssertJUnit.assertFalse("Message was not replay, insert into empty cache", replayCache.isReplay("test", messageID));
        AssertJUnit.assertTrue("Message was replay", replayCache.isReplay("test", messageID));
    }

    /**
     * Test valid replayed message ID, setting expriation by millisecond duration.
     * 
     * @throws InterruptedException
     */
    @Test
    public void testNonReplayValidByMillisecondExpiriation() throws InterruptedException {
        ReplayCache replayCache = new ReplayCache(storageEngine, 5);

        // Expiration set to 5 milliseconds in the future
        AssertJUnit.assertFalse("Message was not replay, insert into empty cache", replayCache.isReplay("test", messageID));
        // Sleep for 500 milliseconds to make sure replay cache entry has expired
        Thread.sleep(500);
        AssertJUnit.assertFalse("Message was not replay, previous cache entry should have expired", replayCache.isReplay("test",
                messageID));
    }
}