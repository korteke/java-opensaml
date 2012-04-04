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
import org.testng.AssertJUnit;
import java.util.Collection;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.xmlsec.keyinfo.KeyInfoGeneratorFactory;
import org.opensaml.xmlsec.keyinfo.KeyInfoGeneratorManager;
import org.opensaml.xmlsec.keyinfo.impl.BasicKeyInfoGeneratorFactory;
import org.opensaml.xmlsec.keyinfo.impl.X509KeyInfoGeneratorFactory;

/**
 * Test the KeyInfoGeneratorFactory manager.
 */
public class KeyInfoGeneratorManagerTest extends XMLObjectBaseTestCase {
    
    private KeyInfoGeneratorManager manager;
    
    private BasicKeyInfoGeneratorFactory basicFactory;
    private BasicKeyInfoGeneratorFactory basicFactory2;
    private X509KeyInfoGeneratorFactory x509Factory;
    private X509KeyInfoGeneratorFactory x509Factory2;
    
    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        manager = new KeyInfoGeneratorManager();
        basicFactory = new BasicKeyInfoGeneratorFactory();
        basicFactory2 = new BasicKeyInfoGeneratorFactory();
        x509Factory = new X509KeyInfoGeneratorFactory();
        x509Factory2 = new X509KeyInfoGeneratorFactory();
        
    }

    /** Test factory registration. */
    @Test
    public void testRegister() {
        AssertJUnit.assertEquals("Unexpected # of managed factories", 0, manager.getFactories().size());
        
        manager.registerFactory(basicFactory);
        AssertJUnit.assertEquals("Unexpected # of managed factories", 1, manager.getFactories().size());
        AssertJUnit.assertTrue("Expected factory not found", manager.getFactories().contains(basicFactory));
        
        manager.registerFactory(x509Factory);
        AssertJUnit.assertEquals("Unexpected # of managed factories", 2, manager.getFactories().size());
        AssertJUnit.assertTrue("Expected factory not found", manager.getFactories().contains(x509Factory));
        
        // basicFactory2 should replace basicFactory
        manager.registerFactory(basicFactory2);
        AssertJUnit.assertEquals("Unexpected # of managed factories", 2, manager.getFactories().size());
        AssertJUnit.assertTrue("Expected factory not found", manager.getFactories().contains(basicFactory2));
        AssertJUnit.assertFalse("Unexpected factory found", manager.getFactories().contains(basicFactory));
    }
    
    /** Test factory de-registration. */
    @Test
    public void testDeregister() {
        AssertJUnit.assertEquals("Unexpected # of managed factories", 0, manager.getFactories().size());
        
        manager.registerFactory(basicFactory);
        manager.registerFactory(x509Factory);
        AssertJUnit.assertEquals("Unexpected # of managed factories", 2, manager.getFactories().size());
        AssertJUnit.assertTrue("Expected factory not found", manager.getFactories().contains(basicFactory));
        AssertJUnit.assertTrue("Expected factory not found", manager.getFactories().contains(x509Factory));
        
        manager.deregisterFactory(x509Factory);
        AssertJUnit.assertEquals("Unexpected # of managed factories", 1, manager.getFactories().size());
        AssertJUnit.assertTrue("Expected factory not found", manager.getFactories().contains(basicFactory));
        AssertJUnit.assertFalse("Unexpected factory found", manager.getFactories().contains(x509Factory));
        
        manager.deregisterFactory(basicFactory);
        AssertJUnit.assertEquals("Unexpected # of managed factories", 0, manager.getFactories().size());
        AssertJUnit.assertFalse("Unexpected factory found", manager.getFactories().contains(basicFactory));
        AssertJUnit.assertFalse("Unexpected factory found", manager.getFactories().contains(x509Factory));
    }
    
    /** Test that getFactories() works, and is unmodifiable. */
    @Test
    public void testGetFactories() {
        manager.registerFactory(basicFactory);
        manager.registerFactory(x509Factory);
        AssertJUnit.assertEquals("Unexpected # of managed factories", 2, manager.getFactories().size());
        
        Collection<KeyInfoGeneratorFactory> factories = manager.getFactories();
        
        try {
            factories.remove(basicFactory);
            Assert.fail("Returned factory collection should be unmodifiable");
        } catch (UnsupportedOperationException e) {
            // do nothing, should fail
        }        
    }
    
    /** Test lookup of factory from manager based on a credential instance. */
    @Test
    public void testLookupFactory() {
        manager.registerFactory(basicFactory);
        manager.registerFactory(x509Factory);
        AssertJUnit.assertEquals("Unexpected # of managed factories", 2, manager.getFactories().size());
        
        Credential basicCred = new BasicCredential();
        X509Credential x509Cred = new BasicX509Credential();
        
        AssertJUnit.assertNotNull("Failed to find factory based on credential", manager.getFactory(basicCred));
        AssertJUnit.assertTrue("Found incorrect factory based on credential", basicFactory == manager.getFactory(basicCred));
        
        AssertJUnit.assertNotNull("Failed to find factory based on credential", manager.getFactory(x509Cred));
        AssertJUnit.assertTrue("Found incorrect factory based on credential", x509Factory == manager.getFactory(x509Cred));
        
        manager.registerFactory(x509Factory2);
        AssertJUnit.assertNotNull("Failed to find factory based on credential", manager.getFactory(x509Cred));
        AssertJUnit.assertTrue("Found incorrect factory based on credential", x509Factory2 == manager.getFactory(x509Cred));
    }

}
