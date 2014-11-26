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

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Collection;

import org.cryptacular.util.CertUtil;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.crypto.KeySupport;
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
    
    private String certDER = "/data/certificate.der";
    
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
        Assert.assertEquals(manager.getFactories().size(), 0, "Unexpected # of managed factories");
        
        manager.registerFactory(basicFactory);
        Assert.assertEquals(manager.getFactories().size(), 1, "Unexpected # of managed factories");
        Assert.assertTrue(manager.getFactories().contains(basicFactory), "Expected factory not found");
        
        manager.registerFactory(x509Factory);
        Assert.assertEquals(manager.getFactories().size(), 2, "Unexpected # of managed factories");
        Assert.assertTrue(manager.getFactories().contains(x509Factory), "Expected factory not found");
        
        // basicFactory2 should replace basicFactory
        manager.registerFactory(basicFactory2);
        Assert.assertEquals(manager.getFactories().size(), 2, "Unexpected # of managed factories");
        Assert.assertTrue(manager.getFactories().contains(basicFactory2), "Expected factory not found");
        Assert.assertFalse(manager.getFactories().contains(basicFactory), "Unexpected factory found");
    }
    
    /** Test factory de-registration. */
    @Test
    public void testDeregister() {
        Assert.assertEquals(manager.getFactories().size(), 0, "Unexpected # of managed factories");
        
        manager.registerFactory(basicFactory);
        manager.registerFactory(x509Factory);
        Assert.assertEquals(manager.getFactories().size(), 2, "Unexpected # of managed factories");
        Assert.assertTrue(manager.getFactories().contains(basicFactory), "Expected factory not found");
        Assert.assertTrue(manager.getFactories().contains(x509Factory), "Expected factory not found");
        
        manager.deregisterFactory(x509Factory);
        Assert.assertEquals(manager.getFactories().size(), 1, "Unexpected # of managed factories");
        Assert.assertTrue(manager.getFactories().contains(basicFactory), "Expected factory not found");
        Assert.assertFalse(manager.getFactories().contains(x509Factory), "Unexpected factory found");
        
        manager.deregisterFactory(basicFactory);
        Assert.assertEquals(manager.getFactories().size(), 0, "Unexpected # of managed factories");
        Assert.assertFalse(manager.getFactories().contains(basicFactory), "Unexpected factory found");
        Assert.assertFalse(manager.getFactories().contains(x509Factory), "Unexpected factory found");
    }
    
    /** Test that getFactories() works, and is unmodifiable. */
    @Test
    public void testGetFactories() {
        manager.registerFactory(basicFactory);
        manager.registerFactory(x509Factory);
        Assert.assertEquals(manager.getFactories().size(), 2, "Unexpected # of managed factories");
        
        Collection<KeyInfoGeneratorFactory> factories = manager.getFactories();
        
        try {
            factories.remove(basicFactory);
            Assert.fail("Returned factory collection should be unmodifiable");
        } catch (UnsupportedOperationException e) {
            // do nothing, should fail
        }        
    }
    
    /** Test lookup of factory from manager based on a credential instance. 
     * @throws NoSuchProviderException 
     * @throws NoSuchAlgorithmException 
     * @throws CryptException 
     * @throws IOException */
    @Test
    public void testLookupFactory() throws NoSuchAlgorithmException, NoSuchProviderException, IOException {
        manager.registerFactory(basicFactory);
        manager.registerFactory(x509Factory);
        Assert.assertEquals(manager.getFactories().size(), 2, "Unexpected # of managed factories");
        
        Credential basicCred = new BasicCredential(KeySupport.generateKey("AES", 128, null));
        X509Credential x509Cred = new BasicX509Credential(CertUtil.readCertificate(getClass().getResourceAsStream(certDER)));
        
        Assert.assertNotNull(manager.getFactory(basicCred), "Failed to find factory based on credential");
        Assert.assertTrue(basicFactory == manager.getFactory(basicCred), "Found incorrect factory based on credential");
        
        Assert.assertNotNull(manager.getFactory(x509Cred), "Failed to find factory based on credential");
        Assert.assertTrue(x509Factory == manager.getFactory(x509Cred), "Found incorrect factory based on credential");
        
        manager.registerFactory(x509Factory2);
        Assert.assertNotNull(manager.getFactory(x509Cred), "Failed to find factory based on credential");
        Assert.assertTrue(x509Factory2 == manager.getFactory(x509Cred), "Found incorrect factory based on credential");
    }

}
