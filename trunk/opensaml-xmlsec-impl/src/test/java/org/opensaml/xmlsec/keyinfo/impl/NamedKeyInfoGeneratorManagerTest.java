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

package org.opensaml.xmlsec.keyinfo.impl;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.Assert;
import java.util.Collection;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.xmlsec.keyinfo.KeyInfoGeneratorFactory;
import org.opensaml.xmlsec.keyinfo.KeyInfoGeneratorManager;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;
import org.opensaml.xmlsec.keyinfo.impl.BasicKeyInfoGeneratorFactory;
import org.opensaml.xmlsec.keyinfo.impl.X509KeyInfoGeneratorFactory;

/**
 * Test the NamedKeyInfoGeneratorFactory manager.
 */
public class NamedKeyInfoGeneratorManagerTest extends XMLObjectBaseTestCase {
    private NamedKeyInfoGeneratorManager manager;
    
    private BasicKeyInfoGeneratorFactory basicFactoryFoo;
    private BasicKeyInfoGeneratorFactory basicFactoryFoo2;
    private BasicKeyInfoGeneratorFactory basicFactoryBar;
    private X509KeyInfoGeneratorFactory x509FactoryFoo;
    private X509KeyInfoGeneratorFactory x509FactoryBar;
    
    private String nameFoo = "FOO";
    private String nameBar = "BAR";
    
    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        manager = new NamedKeyInfoGeneratorManager();
        basicFactoryFoo = new BasicKeyInfoGeneratorFactory();
        basicFactoryFoo2 = new BasicKeyInfoGeneratorFactory();
        basicFactoryBar = new BasicKeyInfoGeneratorFactory();
        x509FactoryFoo = new X509KeyInfoGeneratorFactory();
        x509FactoryBar = new X509KeyInfoGeneratorFactory();
        
    }
    
    /** Test factory registration. */
    @Test
    public void testRegister() {
        manager.registerFactory(nameFoo, basicFactoryFoo);
        manager.registerFactory(nameFoo, x509FactoryFoo);
        
        KeyInfoGeneratorManager fooManager = manager.getManager(nameFoo);
        Assert.assertNotNull(fooManager, "Expected named manager not present/created");
        Assert.assertEquals(fooManager.getFactories().size(), 2, "Unexpected # of managed factories");
        
        Assert.assertTrue(fooManager.getFactories().contains(basicFactoryFoo), "Expected factory not found");
        Assert.assertTrue(fooManager.getFactories().contains(x509FactoryFoo), "Expected factory not found");
        
        // basicFactoryFoo2 should replace basicFactoryFoo
        manager.registerFactory(nameFoo, basicFactoryFoo2);
        Assert.assertFalse(fooManager.getFactories().contains(basicFactoryFoo), "Unexpected factory found");
        Assert.assertTrue(fooManager.getFactories().contains(basicFactoryFoo2), "Expected factory not found");
    }
    
    /** Test factory de-registration. */
    @Test
    public void testDeregister() {
        manager.registerFactory(nameFoo, basicFactoryFoo);
        manager.registerFactory(nameFoo, x509FactoryFoo);
        
        KeyInfoGeneratorManager fooManager = manager.getManager(nameFoo);
        Assert.assertNotNull(fooManager, "Expected named manager not present/created");
        Assert.assertEquals(fooManager.getFactories().size(), 2, "Unexpected # of managed factories");
        
        manager.deregisterFactory(nameFoo, x509FactoryFoo);
        Assert.assertTrue(fooManager.getFactories().contains(basicFactoryFoo), "Expected factory not found");
        Assert.assertFalse(fooManager.getFactories().contains(x509FactoryFoo), "Unexpected factory found");
        
        try {
            manager.deregisterFactory("BAZ", x509FactoryFoo);
            Assert.fail("Use of non-existent manager name should have caused an exception");
        } catch (IllegalArgumentException e) {
            // do nothing, should fail
        }        
    }
    
    /** Test access to manager names, and that can not be modified. */
    @Test
    public void testGetManagerNames() {
        Collection<String> names = manager.getManagerNames();
        Assert.assertTrue(names.isEmpty(), "Names was not empty");
        
        manager.registerFactory(nameFoo, basicFactoryFoo);
        manager.registerFactory(nameBar, basicFactoryBar);
        names = manager.getManagerNames();
        Assert.assertEquals(names.size(), 2, "Unexpected # of manager names");
        
        Assert.assertTrue(names.contains(nameFoo), "Expected manager name not found");
        Assert.assertTrue(names.contains(nameBar), "Expected manager name not found");
        
        try {
            names.remove(basicFactoryFoo);
            Assert.fail("Returned names set should be unmodifiable");
        } catch (UnsupportedOperationException e) {
            // do nothing, should fail
        }        
        
    }
    
    /** Test that obtaining a manager by name works. */
    @Test
    public void testGetManagerByName() {
        manager.registerFactory(nameFoo, basicFactoryFoo);
        manager.registerFactory(nameBar, basicFactoryBar);
        Collection<String> names = manager.getManagerNames();
        Assert.assertEquals(names.size(), 2, "Unexpected # of manager names");
        
        Assert.assertNotNull(manager.getManager(nameFoo), "Failed to find manager by name");
        Assert.assertNotNull(manager.getManager(nameBar), "Failed to find manager by name");
        
        Assert.assertFalse(names.contains("BAZ"), "Non-existent manager name found in name set");
        Assert.assertNotNull(manager.getManager("BAZ"), "Failed to create new manager");
        Assert.assertTrue(names.contains("BAZ"), "Expected manager name not found");
    }
    
    /** Remove a manager by name. */
    @Test
    public void testRemoveManagerByName() {
        manager.registerFactory(nameFoo, basicFactoryFoo);
        manager.registerFactory(nameFoo, x509FactoryFoo);
        manager.registerFactory(nameBar, basicFactoryBar);
        Collection<String> names = manager.getManagerNames();
        Assert.assertEquals(names.size(), 2, "Unexpected # of manager names");
        
        Assert.assertNotNull(manager.getManager(nameFoo), "Failed to find manager by name");
        Assert.assertNotNull(manager.getManager(nameBar), "Failed to find manager by name");
        Assert.assertTrue(names.contains(nameFoo), "Expected manager name not found");
        Assert.assertTrue(names.contains(nameBar), "Expected manager name not found");
        
        manager.removeManager(nameFoo);
        Assert.assertEquals(names.size(), 1, "Unexpected # of manager names");
        Assert.assertNotNull(manager.getManager(nameBar), "Failed to find manager by name");
        Assert.assertFalse(names.contains(nameFoo), "Unexpected manager name found");
        Assert.assertTrue(names.contains(nameBar), "Expected manager name not found");
    }
    
    /** Test registering a factory in the default unnamed manager. */
    @Test
    public void testRegisterDefaultFactory() {
        KeyInfoGeneratorManager defaultManager = manager.getDefaultManager();
        Assert.assertEquals(defaultManager.getFactories().size(), 0, "Unexpected # of default factories");
        manager.registerDefaultFactory(basicFactoryFoo);
        manager.registerDefaultFactory(x509FactoryFoo);
        Assert.assertEquals(defaultManager.getFactories().size(), 2, "Unexpected # of default factories");
    }
    
    /** Test de-registering a factory in the default unnamed manager. */
    @Test
    public void testDeregisterDefaultFactory() {
        KeyInfoGeneratorManager defaultManager = manager.getDefaultManager();
        Assert.assertEquals(defaultManager.getFactories().size(), 0, "Unexpected # of default factories");
        manager.registerDefaultFactory(basicFactoryFoo);
        manager.registerDefaultFactory(x509FactoryFoo);
        Assert.assertEquals(defaultManager.getFactories().size(), 2, "Unexpected # of default factories");
        
        manager.deregisterDefaultFactory(x509FactoryFoo);
        Assert.assertEquals(defaultManager.getFactories().size(), 1, "Unexpected # of default factories");
    }
    
    /** Test lookup of factory from manager based on a credential instance. */
    @Test
    public void testLookupFactory() {
        manager.registerFactory(nameFoo, basicFactoryFoo);
        manager.registerFactory(nameFoo, x509FactoryFoo);
        manager.registerFactory(nameBar, basicFactoryBar);
        manager.registerFactory(nameBar, x509FactoryBar);
        manager.getManager("BAZ");
        Assert.assertEquals(manager.getManager(nameFoo).getFactories().size(), 2, "Unexpected # of managed factories");
        Assert.assertEquals(manager.getManager(nameBar).getFactories().size(), 2, "Unexpected # of managed factories");
        Assert.assertEquals(manager.getManager("BAZ").getFactories().size(), 0, "Unexpected # of managed factories");
        Assert.assertEquals(manager.getManagerNames().size(), 3, "Unexpected # of manager names");
        
        Credential basicCred = new BasicCredential();
        X509Credential x509Cred = new BasicX509Credential();
        
        Assert.assertNotNull(manager.getFactory(nameFoo, basicCred), 
                "Failed to find factory based on manager name and credential");
        Assert.assertTrue(basicFactoryFoo == manager.getFactory(nameFoo, basicCred), 
                "Found incorrect factory based on name and credential");
        
        Assert.assertNotNull(manager.getFactory(nameFoo, x509Cred), 
                "Failed to find factory based on manager name and credential");
        Assert.assertTrue(x509FactoryFoo == manager.getFactory(nameFoo, x509Cred), 
                "Found incorrect factory based on name and credential");
        
        Assert.assertNotNull(manager.getFactory(nameBar, x509Cred), 
                "Failed to find factory based on manager name and credential");
        Assert.assertTrue(x509FactoryBar == manager.getFactory(nameBar, x509Cred), 
                "Found incorrect factory based on name and credential");
        
        Assert.assertNull(manager.getFactory("BAZ", x509Cred), 
                "Found non-existent factory based on name and credential");
        try {
            manager.getFactory("ABC123", x509Cred);
            Assert.fail("Use of non-existent manager name should have caused an exception");
        } catch (IllegalArgumentException e) {
            // do nothing, should fail
        }        
    }
    
    /** Test proper functioning of option to use the default manager for unnamed factories. */
    @Test
    public void testFallThroughToDefaultManager() {
        KeyInfoGeneratorFactory defaultX509Factory = new X509KeyInfoGeneratorFactory();
        manager.registerDefaultFactory(defaultX509Factory);
        manager.registerFactory(nameFoo, basicFactoryFoo);
        
        X509Credential x509Cred = new BasicX509Credential();
        
        manager.setUseDefaultManager(true);
        
        Assert.assertNotNull(manager.getFactory(nameFoo, x509Cred), 
                "Failed to find factory based on manager name and credential");
        Assert.assertTrue(defaultX509Factory == manager.getFactory(nameFoo, x509Cred), 
                "Found incorrect factory based on name and credential");
        
        manager.setUseDefaultManager(false);
        Assert.assertNull(manager.getFactory(nameFoo, x509Cred),
                "Found factory in default manager even though useDefaultManager option set to false");
    }
}
