/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package org.opensaml.storage.impl.memcached;

import net.shibboleth.utilities.java.support.collection.Pair;
import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.MemcachedClient;
import org.cryptacular.generator.IdGenerator;
import org.cryptacular.generator.RandomIdGenerator;
import org.opensaml.storage.StorageRecord;
import org.opensaml.storage.VersionMismatchException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.*;

/**
 * Unit test for {@link MemcachedStorageService} class.
 * <p>
 * <strong>NOTE</strong><br>
 * This test is not run by default since it requires an external test fixture, namely a memcached server running on
 * <code>localhost:11211</code>. This test may be executed by running the test goal with the <code>all</code> maven
 * profile if a local memcached service is available.
 */
@Test(groups = {"needs-external-fixture"})
public class MemcachedStorageServiceTest {

    private MemcachedStorageService service;

    private MemcachedStorageService keyTrackingService;

    @BeforeClass
    public void setUp() throws IOException {
        final MemcachedClient client = new MemcachedClient(
                new BinaryConnectionFactory(),
                Collections.singletonList(new InetSocketAddress("localhost", 11211)));
        try {
            client.asyncGet("does_not_exist").get(500, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            fail("Memcached operation failure. Is memcached running on localhost:11211?", e);
        }
        service = new MemcachedStorageService(client, 1);
        keyTrackingService = new MemcachedStorageService(client, 1, true);
    }

    @DataProvider
    public Object[][] testValues() {
        return new Object[][] {
                new Object[] {
                        "28f1b4f4de961204499bf1d378b9b8ec4f50d46a0ecde454f147f5bb053ca908",
                        "idp_session_key",
                        "qSMyPJN2KCT1z5ZoRCn5xqbkoNH18QgjLQVp68MNocurIaaBiaczZYoMO8WYBwdlrADrAJ0j6cyOwbEIjCtMcA==",
                        "ZufpQlaM2gMDKbzVx7qCWDu+/ke5A5d0TPuMF2lamj5Hd+w3uEGoxkidyxgekzT9Q8hAthyla3p1egoRPpCBqA==",
                },
                // Test long keys that require hashing to accommodate 250-byte memcached key limit
                new Object[] {
                        "2769c3850b1e8c7980cba7980cc48f045cfd31efa5e658723f441ac87a4f" +
                                "bac82ed4eee663752bb6f53d774b4fb944c28fe2f6e8d3930a1a72e714cc" +
                                "040d1f35d3c250d48308685fd2681c69fa356b9b45e26274b58f789456f0" +
                                "7af316218e344310fc5df3bdd6e9f8c89c780146f32cf7dd53ea1d3ccf6b" +
                                "cbe758e88037917f",
                        "70a0ef338d024c17dec31b8c728547b758430d38d6a594202a5956693cbf" +
                                "1f3708ff07dd02be4032596e6a597a60e3ca4312c874a7c99d84dc74fec6" +
                                "4f90221a838b1004ade64c300dbf5aae387c7a1df2e37b1250a3de10bf4a" +
                                "64fefe466fd5f9bffe00a166e976341b27314a4abea12266911943d1fba6" +
                                "564560d4a6eea543",
                        "8XLjjzODxU2Ub4uCKUYZSBoJpgqniyNrHdkYCBo2KTdgc7lZlc8vaTfASSk9P0JZ" +
                                "K7SX6LduqykBIiEmzdEzNMU/N16rJJfNweH55L1MLCiEX7bbTiGIiP/lcbqr6k6Q" +
                                "X7ZTdsI1R/RTrYENPwlwyfY8hoEEPUcluyf12T1o9heaRcK0ysDlnanSyVQARAWZ" +
                                "Tq76K3xTr1Ka9DEclT65tsofiFhLUehtbjUbfbLZ7ZXsjmz+ytKJXCtjSWrwO4gz" +
                                "qJ7HrGO7KqVu0ZiaRyJo0HgbzCLRmdfkzw+3bd5cxdjMxkeAuJ3vUH23DL8HcA+k" +
                                "KyJH1GOWXJK8gxRO6V9GEADv",
                        "vFDDoBw7+8SLueXQGHTA1f5DCzQKfGASCAWlX1SQLkNmz4rZ1C6mYiYs69xopBer" +
                                "Cc8J2gX5uk+u97f8J2BovRWQ2H10oJtySVbCHiexzxO1jhS2jq1GlFzPJEBqWsLy" +
                                "p4W7so0JKTeNVtBQPM/v/K3lNGeDQBPXYQO+H6QxBwDr9pOx3UZ3sBeYtLo6FVZU" +
                                "+kdzG+4k2B5Sg4F10ocxTpxreFU3yjrhRXWd/uMViOS0Z4sr89LU/YQJFglmys3H" +
                                "18bopaTaatvY76ZCF9ArlSWsnbQiw148Q9x12pz0hc2u1PXeAZKPxJp4Ne0aKAly" +
                                "9NtyQ3eGo0Snmfzy+S1Z4wfp"
                },
        };
    }

    @Test(dataProvider = "testValues")
    public void testCreateReadUpdateDelete(
            final String context, final String key, final String value, final String updatedValue)
            throws IOException {
        assertNull(service.read(context, key));
        assertTrue(service.create(context, key, value, 5000L));
        final StorageRecord r1 = service.read(context, key);
        assertNotNull(r1);
        assertEquals(r1.getValue(), value);
        assertTrue(service.update(context, key, updatedValue, 5000L));
        final StorageRecord r2 = service.read(context, key);
        assertNotNull(r2);
        assertEquals(r2.getValue(), updatedValue);
        assertTrue(service.delete(context, key));
        assertNull(service.read(context, key));
    }


    @Test(dataProvider = "testValues")
    public void testCreateReadUpdateDeleteVersion(
            final String context, final String key, final String value, final String updatedValue)
            throws IOException, VersionMismatchException {
        assertNull(service.read(context, key));
        assertTrue(service.create(context, key, value, 5000L));
        final StorageRecord r1 = service.read(context, key);
        assertNotNull(r1);
        assertEquals(r1.getValue(), value);
        final Long updatedVersion = service.updateWithVersion(r1.getVersion(), context, key, updatedValue, 5000L);
        assertTrue((updatedVersion > r1.getVersion()));
        final Pair<Long, StorageRecord> pair1 = service.read(context, key, r1.getVersion());
        assertEquals(pair1.getFirst(), updatedVersion);
        assertEquals(pair1.getSecond().getValue(), updatedValue);
        final Pair<Long, StorageRecord> pair2 = service.read(context, key, updatedVersion);
        assertEquals(pair2.getFirst(), updatedVersion);
        assertNull(pair2.getSecond());
        assertFalse(service.deleteWithVersion(r1.getVersion(), context, key));
        final Pair<Long, StorageRecord> pair3 = service.read(context, key, updatedVersion);
        assertEquals(pair3.getFirst(), updatedVersion);
        assertNull(pair3.getSecond());
        assertTrue(service.deleteWithVersion(updatedVersion, context, key));
        assertNull(service.read(context, key));
    }

    @Test
    public void testDeleteContextDeletesEntries() throws IOException {
        final IdGenerator generator = new RandomIdGenerator(50);
        final String context = generator.generate();
        final String key1 = generator.generate();
        final String val1 = generator.generate();
        final String key2 = generator.generate();
        final String val2 = generator.generate();
        final String key3 = generator.generate();
        final String val3 = generator.generate();
        assertTrue(service.create(context, key1, val1, 1000L));
        assertEquals(service.read(context, key1).getValue(), val1);
        assertTrue(service.create(context, key2, val2, 1000L));
        assertEquals(service.read(context, key2).getValue(), val2);
        assertTrue(service.create(context, key3, val3, 1000L));
        assertEquals(service.read(context, key3).getValue(), val3);
        service.deleteContext(context);
        assertNull(service.read(context, key1));
        assertNull(service.read(context, key2));
        assertNull(service.read(context, key3));
    }


    @Test
    public void testUpdateExpiration() throws IOException {
        final IdGenerator generator = new RandomIdGenerator(20);
        final String context = generator.generate();
        final String key = "expiration_test_key";
        final String value = "Oh well, oh well, oh well, oh well";
        assertTrue(service.create(context, key, value, 30000L));
        final StorageRecord record = service.read(context, key);
        assertNotNull(record);
        assertEquals(record.getValue(), value);
        assertTrue(service.updateExpiration(context, key, System.currentTimeMillis() - 5000));
        assertNull(service.read(context, key));
    }

    @Test
    public void testUpdateContextExpiration() throws Exception {
        final IdGenerator generator = new RandomIdGenerator(20);
        final String context = generator.generate();
        final Set<String> keySet = createContextKeys(context, generator, 20);
        // Set expiration of all context keys to time in the past
        // Should cause all entries to be expired
        keyTrackingService.updateContextExpiration(context, System.currentTimeMillis() - 5000);
        for (String k : keySet) {
            assertNull(keyTrackingService.read(context, k));
        }
    }

    @Test
    public void testUpdateContextExpirationWithBlacklisting() throws Exception {
        final IdGenerator generator = new RandomIdGenerator(20);
        final String context = generator.generate();
        final Set<String> keysTBD = createContextKeys(context, generator, 20);
        for (String key : keysTBD) {
            assertTrue(keyTrackingService.delete(context, key));
        }
        final Set<String> newKeys = createContextKeys(context, generator, 20);
        // Set expiration of all context keys to time in the past
        // Should cause all entries to be expired
        keyTrackingService.updateContextExpiration(context, System.currentTimeMillis() - 5000);
        final Set<String> allKeys = new HashSet<>(keysTBD);
        allKeys.addAll(newKeys);
        for (String k : allKeys) {
            assertNull(keyTrackingService.read(context, k));
        }
    }

    @AfterClass
    public void tearDown() {
        service.destroy();
        keyTrackingService.destroy();
    }

    private Set<String> createContextKeys(final String context, final IdGenerator generator, final int count)
            throws IOException {
        final String valueBase = "Context value ";
        final Set<String> keySet = new HashSet<>();
        String key;
        boolean result;
        for (int i = 0; i < count; i++) {
            key = generator.generate();
            result = keyTrackingService.create(context, key, valueBase + i, 30000L);
            if (result) {
                keySet.add(key);
            }
        }
        assertEquals(keySet.size(), count);
        for (String k : keySet) {
            assertNotNull(keyTrackingService.read(context, k));
        }
        return keySet;
    }
}