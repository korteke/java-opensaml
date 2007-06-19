/*
 * Copyright [2007] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.util.storage;

import junit.framework.TestCase;

import org.joda.time.DateTime;
import org.opensaml.util.storage.MapBasedStorageService;
import org.opensaml.util.storage.ReplayCache;
import org.opensaml.util.storage.ReplayCache.ReplayCacheEntry;

/**
 * Testing SAML message replay security policy rule.
 */
public class ReplayCacheTest extends TestCase {
    
    private DateTime now;
    private String messageID;
    
    private long defaultExpire;
    
    private ReplayCache replayCache;
    

    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
        
        messageID = "abc123";
        defaultExpire = 60*10*1000;
        now = new DateTime();
        
        MapBasedStorageService<String, ReplayCacheEntry> storage = 
            new MapBasedStorageService<String, ReplayCacheEntry>();
        replayCache = new ReplayCache(storage, defaultExpire);
    }
    
    /**
     * Test valid non-replayed message ID.
     */
    public void testNonReplayEmptyCache() {
        assertFalse("Message was not replay, insert into empty cache", replayCache.isReplay(messageID));
    }
    
    /**
     * Test valid non-replayed message ID.
     */
    public void testNonReplayDistinctIDs() {
        assertFalse("Message was not replay, insert into empty cache", replayCache.isReplay(messageID));
        assertFalse("Message was not replay, insert into empty cache",
                replayCache.isReplay("IDWhichIsNot" + messageID));
    }
    
    /**
     * Test invalid replayed message ID, using replay cache default expiration duration.
     */
    public void testReplay() {
        assertFalse("Message was not replay, insert into empty cache", replayCache.isReplay(messageID));
        assertTrue("Message was replay", replayCache.isReplay(messageID));
    }
    
    /**
     * Test invalid replayed message ID, setting expiration by millisecond duration.
     */
    public void testReplayByMillisecondExpiration() {
        // Expiration set to 5 minutes in the future
        assertFalse("Message was not replay, insert into empty cache", 
                replayCache.isReplay(messageID, 1000*60*5));
        assertTrue("Message was replay", replayCache.isReplay(messageID));
    }
    
    /**
     * Test invalid replayed message ID, setting expiration by future Datetime.
     */
    public void testReplayByDatetimeExpiration() {
        // Expiration set to 5 minutes in the future
        assertFalse("Message was not replay, insert into empty cache", 
                replayCache.isReplay(messageID, now.plusMinutes(5)));
        assertTrue("Message was replay", replayCache.isReplay(messageID));
    }
    
    /**
     * Test valid replayed message ID, setting expriation by millisecond duration.
     * @throws InterruptedException 
     */
    public void testNonReplayValidByMillisecondExpiriation() throws InterruptedException {
        // Expiration set to 5 milliseconds in the future
        assertFalse("Message was not replay, insert into empty cache",
                replayCache.isReplay(messageID, 5));
        // Sleep for 500 milliseconds to make sure replay cache entry has expired
        Thread.sleep(500);
        assertFalse("Message was not replay, previous cache entry should have expired", 
                replayCache.isReplay(messageID));
    }

    /**
     * Test valid replayed message ID, setting expriation by Datetime duration.
     */
    public void testNonReplayValidByDatetimeExpiriation() {
        // Expiration set to 5 seconds in the past
        assertFalse("Message was not replay, insert into empty cache", 
                replayCache.isReplay(messageID, now.minusSeconds(5)));
        assertFalse("Message was not replay, previous cache entry should have expired", 
                replayCache.isReplay(messageID));
    }

}
