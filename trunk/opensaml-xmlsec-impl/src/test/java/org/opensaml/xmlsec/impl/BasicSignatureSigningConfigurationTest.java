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

package org.opensaml.xmlsec.impl;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import java.util.Collections;

import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 */
public class BasicSignatureSigningConfigurationTest {
    
    private BasicSignatureSigningConfiguration config;
    
    private Credential cred1, cred2;
    
    @BeforeClass
    public void generateCredentials() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPair kp1 = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_RSA, 2048, null);
        cred1 = CredentialSupport.getSimpleCredential(kp1.getPublic(), kp1.getPrivate());
        
        KeyPair kp2 = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_RSA, 2048, null);
        cred2 = CredentialSupport.getSimpleCredential(kp2.getPublic(), kp2.getPrivate());
    }
    
    @BeforeMethod
    public void setUp() {
       config = new BasicSignatureSigningConfiguration(); 
    }
    
    @Test
    public void testDefaults() {
        Assert.assertNotNull(config.getSigningCredentials());
        Assert.assertTrue(config.getSigningCredentials().isEmpty());
        
        Assert.assertNotNull(config.getSignatureAlgorithms());
        Assert.assertTrue(config.getSignatureAlgorithms().isEmpty());
        
        Assert.assertNotNull(config.getSignatureReferenceDigestMethods());
        Assert.assertTrue(config.getSignatureReferenceDigestMethods().isEmpty());
        
        Assert.assertNull(config.getSignatureCanonicalizationAlgorithm());
        Assert.assertNull(config.getSignatureHMACOutputLength());
        Assert.assertNull(config.getKeyInfoGeneratorManager());
    }
    
    @Test
    public void testSigningCredentials() throws NoSuchAlgorithmException, NoSuchProviderException {
        Assert.assertNotNull(config.getSigningCredentials());
        Assert.assertEquals(config.getSigningCredentials().size(), 0);
        
        config.setSigningCredentials(Arrays.asList(cred1, null, cred2, null));
        
        Assert.assertNotNull(config.getSigningCredentials());
        Assert.assertEquals(config.getSigningCredentials().size(), 2);
        
        config.setSigningCredentials(null);
        
        Assert.assertNotNull(config.getSigningCredentials());
        Assert.assertEquals(config.getSigningCredentials().size(), 0);
    }
    
    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void testSigningCredentialsImmutable() throws NoSuchAlgorithmException, NoSuchProviderException {
        config.setSigningCredentials(Collections.singletonList(cred1));
        config.getSigningCredentials().add(cred2);
    }

    @Test
    public void testSignatureAlgorithmURIs() {
        Assert.assertNotNull(config.getSignatureAlgorithms());
        Assert.assertEquals(config.getSignatureAlgorithms().size(), 0);
        
        config.setSignatureAlgorithms(Arrays.asList("  A   ", null, null, "  B   ", null, "  C   "));
        
        Assert.assertNotNull(config.getSignatureAlgorithms());
        Assert.assertEquals(config.getSignatureAlgorithms().size(), 3);
        Assert.assertEquals(config.getSignatureAlgorithms().get(0), "A");
        Assert.assertEquals(config.getSignatureAlgorithms().get(1), "B");
        Assert.assertEquals(config.getSignatureAlgorithms().get(2), "C");
        
        config.setSignatureAlgorithms(null);
        
        Assert.assertNotNull(config.getSignatureAlgorithms());
        Assert.assertEquals(config.getSignatureAlgorithms().size(), 0);
    }

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void testSignatureAlgorithmURIsImmutable() {
        config.setSignatureAlgorithms(Arrays.asList("A", "B", "C"));
        config.getSignatureAlgorithms().add("D");
    }

    @Test
    public void testSignatureReferenceDigestMethods() {
        Assert.assertNotNull(config.getSignatureReferenceDigestMethods());
        Assert.assertEquals(config.getSignatureReferenceDigestMethods().size(), 0);
        
        config.setSignatureReferenceDigestMethods(Arrays.asList("   A  ", null, null, "   B   ", null, "  C    "));
        
        Assert.assertNotNull(config.getSignatureReferenceDigestMethods());
        Assert.assertEquals(config.getSignatureReferenceDigestMethods().size(), 3);
        Assert.assertEquals(config.getSignatureReferenceDigestMethods().get(0), "A");
        Assert.assertEquals(config.getSignatureReferenceDigestMethods().get(1), "B");
        Assert.assertEquals(config.getSignatureReferenceDigestMethods().get(2), "C");
        
        config.setSignatureReferenceDigestMethods(null);
        
        Assert.assertNotNull(config.getSignatureReferenceDigestMethods());
        Assert.assertEquals(config.getSignatureReferenceDigestMethods().size(), 0);
    }

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void testSignatureReferenceDigestMethodsImmutable() {
        config.setSignatureReferenceDigestMethods(Arrays.asList("A", "B", "C"));
        config.getSignatureReferenceDigestMethods().add("D");
    }

    @Test
    public void testSignatureCanonicalizationAlgorithm() {
        Assert.assertNull(config.getSignatureCanonicalizationAlgorithm());
        
        config.setSignatureCanonicalizationAlgorithm("   foo   ");
        
        Assert.assertEquals(config.getSignatureCanonicalizationAlgorithm(), "foo");
        
        config.setSignatureCanonicalizationAlgorithm("    ");
        
        Assert.assertNull(config.getSignatureCanonicalizationAlgorithm());
        
        config.setSignatureCanonicalizationAlgorithm(null);
        
        Assert.assertNull(config.getSignatureCanonicalizationAlgorithm());
    }

    @Test
    public void testSignatureHMACOutputLength() {
        Assert.assertNull(config.getSignatureHMACOutputLength());
        
        config.setSignatureHMACOutputLength(128);
        
        Assert.assertEquals(config.getSignatureHMACOutputLength(), new Integer(128));
        
        config.setSignatureHMACOutputLength(null);
        
        Assert.assertNull(config.getSignatureHMACOutputLength());
    }

    @Test
    public void testKeyInfoGeneratorManager() {
        Assert.assertNull(config.getKeyInfoGeneratorManager());
        
        config.setKeyInfoGeneratorManager(new NamedKeyInfoGeneratorManager());
        
        Assert.assertNotNull(config.getKeyInfoGeneratorManager());
        
        config.setKeyInfoGeneratorManager(null);
        
        Assert.assertNull(config.getKeyInfoGeneratorManager());
    }
    
}
