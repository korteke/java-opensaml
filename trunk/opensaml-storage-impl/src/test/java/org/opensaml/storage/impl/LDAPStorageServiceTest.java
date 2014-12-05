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

import org.ldaptive.DefaultConnectionFactory;
import org.ldaptive.LdapAttribute;
import org.ldaptive.pool.BlockingConnectionPool;
import org.ldaptive.pool.PooledConnectionFactory;
import org.opensaml.storage.StorageRecord;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.sdk.LDAPException;

/**
 * Test of {@link LDAPStorageService} implementation.
 */
public class LDAPStorageServiceTest {

    /** Storage service to test. */
    protected LDAPStorageService storageService;

    /** In-memory directory server. */
    private InMemoryDirectoryServer directoryServer;

    /** LDAP DN to test. */
    private final String context = "cn=Principal,ou=people,dc=shibboleth,dc=net";

    @BeforeClass
    protected void setUp() throws ComponentInitializationException {
        storageService = getStorageService();
        storageService.initialize();
    }
    
    @AfterClass
    protected void tearDown() {
        storageService.destroy();
    }

    /**
     * Creates an UnboundID in-memory directory server. Leverages LDIF found in test resources.
     * 
     * @throws LDAPException if the in-memory directory server cannot be created
     */
    @BeforeTest public void setupDirectoryServer() throws LDAPException {

        InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig("dc=shibboleth,dc=net");
        config.setListenerConfigs(InMemoryListenerConfig.createLDAPConfig("default", 10389));
        config.addAdditionalBindCredentials("cn=Directory Manager", "password");
        directoryServer = new InMemoryDirectoryServer(config);
        directoryServer.importFromLDIF(true,
                "src/test/resources/org/opensaml/storage/impl/LDAPStorageServiceTest.ldif");
        directoryServer.startListening();
    }

    /**
     * Shutdown the in-memory directory server.
     */
    @AfterTest public void teardownDirectoryServer() {
        directoryServer.shutDown(true);
    }

    @Nonnull protected PooledConnectionFactory getPooledConnectionFactory() {
        return new PooledConnectionFactory(new BlockingConnectionPool(new DefaultConnectionFactory(
                "ldap://localhost:10389")));
    }

    @Nonnull protected LDAPStorageService getStorageService() {
        LDAPStorageService ss = new LDAPStorageService(
                getPooledConnectionFactory(),
                new LdapAttribute("objectClass", "inetOrgPerson", "organizationalPerson", "person", "top"),
                new LdapAttribute("cn", "Principal"),
                new LdapAttribute("sn", "Lastname"));
        ss.setId("test");
        return ss;
    }

    @Test
    public void throwException() throws IOException {
        try {
            storageService.create(context, "mail", "principal@shibboleth.net", 5000L);
            Assert.fail("Should have thrown exception");
        } catch (UnsupportedOperationException e){ 
            Assert.assertEquals(e.getClass(), UnsupportedOperationException.class);
        }

        try {
            storageService.read(context, "mail", 3);
            Assert.fail("Should have thrown exception");
        } catch (UnsupportedOperationException e){ 
            Assert.assertEquals(e.getClass(), UnsupportedOperationException.class);
        }

        try {
            storageService.update(context, "mail", "principal@shibboleth.net", 10000L);
            Assert.fail("Should have thrown exception");
        } catch (UnsupportedOperationException e){ 
            Assert.assertEquals(e.getClass(), UnsupportedOperationException.class);
        }

        try {
            storageService.updateWithVersion(2, context, "mail", "principal@shibboleth.net", 10000L);
            Assert.fail("Should have thrown exception");
        } catch (UnsupportedOperationException e){ 
            Assert.assertEquals(e.getClass(), UnsupportedOperationException.class);
        } catch (Exception e) {
            Assert.fail("Threw exception", e);
        }

        try {
            storageService.updateExpiration(context, "mail", 8000L);
            Assert.fail("Should have thrown exception");
        } catch (UnsupportedOperationException e){ 
            Assert.assertEquals(e.getClass(), UnsupportedOperationException.class);
        }

        try {
            storageService.updateContextExpiration(context, 15000L);
            Assert.fail("Should have thrown exception");
        } catch (UnsupportedOperationException e){ 
            Assert.assertEquals(e.getClass(), UnsupportedOperationException.class);
        }
    }

    @Test
    public void create() throws IOException {
        storageService.create(context, "mail", "principal@shibboleth.net", null);
        StorageRecord rec = storageService.read(context, "mail");
        Assert.assertNotNull(rec);
        Assert.assertEquals(rec.getValue(), "principal@shibboleth.net");

        storageService.update(context, "mail", "principal2@shibboleth.net", null);
        rec = storageService.read(context, "mail");
        Assert.assertNotNull(rec);
        Assert.assertEquals(rec.getValue(), "principal2@shibboleth.net");

        storageService.create(context, "mail", "principal3@shibboleth.net", null);
        
        storageService.update(context, "description", "test user", null);
        rec = storageService.read(context, "description");
        Assert.assertNotNull(rec);
        Assert.assertEquals(rec.getValue(), "test user");

        storageService.delete(context, "description");
        rec = storageService.read(context, "description");
        Assert.assertNull(rec);
        rec = storageService.read(context, "mail");
        Assert.assertNotNull(rec);
        Assert.assertEquals(rec.getValue(), "principal3@shibboleth.net");

        storageService.deleteContext(context);
        rec = storageService.read(context, "mail");
        Assert.assertNull(rec);
    }

    @Test public void invalidConfig() {
        LDAPStorageService ss = new LDAPStorageService(getPooledConnectionFactory());
        ss.setCleanupInterval(1000);

        try {
            ss.initialize();
            Assert.fail("Storage service should have failed to initialize");
        } catch (ComponentInitializationException e) {
            // expected
        }

        ss.destroy();
    }

    @Test public void validConfig() throws ComponentInitializationException {
        LDAPStorageService ss = new LDAPStorageService(getPooledConnectionFactory());
        ss.setId("test");
        ss.initialize();
        ss.destroy();
    }

}