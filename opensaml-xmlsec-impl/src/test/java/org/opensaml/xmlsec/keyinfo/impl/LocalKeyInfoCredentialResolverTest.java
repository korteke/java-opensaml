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
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.impl.CollectionCredentialResolver;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.keyinfo.KeyInfoCriterion;
import org.opensaml.xmlsec.keyinfo.KeyInfoSupport;
import org.opensaml.xmlsec.keyinfo.impl.KeyInfoProvider;
import org.opensaml.xmlsec.keyinfo.impl.LocalKeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.impl.provider.RSAKeyValueProvider;
import org.opensaml.xmlsec.signature.KeyInfo;

/**
 * Test the local credential resolver specialization of the KeyInfo credential resolver.
 */
public class LocalKeyInfoCredentialResolverTest extends XMLObjectBaseTestCase {
    
    private String keyName;
    private KeyPair keyPair;
    private BasicCredential localCred;
    
    private CollectionCredentialResolver localCredResolver;
    private LocalKeyInfoCredentialResolver keyInfoResolver;
    
    private KeyInfo keyInfo;

    @BeforeMethod
    protected void setUp() throws Exception {
        keyName = "MyKey";
        keyPair = KeySupport.generateKeyPair("RSA", 1024, null);
        
        localCred = new BasicCredential();
        localCred.setPublicKey(keyPair.getPublic());
        localCred.setPrivateKey(keyPair.getPrivate());
        localCred.getKeyNames().add(keyName);
        
        localCredResolver = new CollectionCredentialResolver();
        localCredResolver.getCollection().add(localCred);
        
        ArrayList<KeyInfoProvider> providers = new ArrayList<KeyInfoProvider>();
        providers.add( new RSAKeyValueProvider() );
        keyInfoResolver = new LocalKeyInfoCredentialResolver(providers, localCredResolver);
        
        keyInfo = (KeyInfo) buildXMLObject(KeyInfo.DEFAULT_ELEMENT_NAME);
    }
    
    @Test
    public void testKeyInfoWithKeyName() throws ResolverException {
        KeyInfoSupport.addKeyName(keyInfo, keyName);
        
        CriteriaSet criteriaSet = new CriteriaSet( new KeyInfoCriterion(keyInfo) );
        Credential resolvedCred = keyInfoResolver.resolveSingle(criteriaSet);
        
        Assert.assertEquals(resolvedCred, localCred, "Unexpected local credential resolved");
    }

    @Test
    public void testKeyInfoWithKnownPublicKey() throws ResolverException {
        KeyInfoSupport.addPublicKey(keyInfo, keyPair.getPublic());
        
        CriteriaSet criteriaSet = new CriteriaSet( new KeyInfoCriterion(keyInfo) );
        Credential resolvedCred = keyInfoResolver.resolveSingle(criteriaSet);
        
        Assert.assertEquals(resolvedCred, localCred, "Unexpected local credential resolved");
    }
    
    @Test
    public void testKeyInfoWithUnknownPublicKey() throws IllegalArgumentException,
        NoSuchAlgorithmException, NoSuchProviderException, ResolverException {
        
        KeyInfoSupport.addPublicKey(keyInfo, 
                KeySupport.generateKeyPair("RSA", 1024, null).getPublic());
        
        CriteriaSet criteriaSet = new CriteriaSet( new KeyInfoCriterion(keyInfo) );
        Credential resolvedCred = keyInfoResolver.resolveSingle(criteriaSet);
        
        Assert.assertNull(resolvedCred, "Expected no credential to be resolved");
    }
    
}
