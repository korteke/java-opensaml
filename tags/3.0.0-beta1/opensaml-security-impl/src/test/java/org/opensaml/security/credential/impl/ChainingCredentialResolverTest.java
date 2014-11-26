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

package org.opensaml.security.credential.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialResolver;
import org.opensaml.security.crypto.KeySupport;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

/**
 * Testing the chaining credential resolver.
 */
public class ChainingCredentialResolverTest {
    
    private ChainingCredentialResolver chainingResolver;
    private CriteriaSet criteriaSet;
    
    private CredentialResolver staticResolver12, staticResolver3, staticResolver45, staticResolverEmpty;
    private Credential cred1, cred2, cred3, cred4, cred5;
    
    
    /** Constructor. */
    public ChainingCredentialResolverTest() {
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        cred1 = new BasicCredential(KeySupport.generateKey("AES", 128, null));
        cred2 = new BasicCredential(KeySupport.generateKey("AES", 128, null));
        cred3 = new BasicCredential(KeySupport.generateKey("AES", 128, null));
        cred4 = new BasicCredential(KeySupport.generateKey("AES", 128, null));
        cred5 = new BasicCredential(KeySupport.generateKey("AES", 128, null));
        
        criteriaSet = new CriteriaSet();
        
        ArrayList<Credential> temp;
        
        temp  = new ArrayList<Credential>();
        temp.add(cred1);
        temp.add(cred2);
        staticResolver12 = new StaticCredentialResolver(temp);
        
        temp  = new ArrayList<Credential>();
        temp.add(cred3);
        staticResolver3 = new StaticCredentialResolver(temp);
        
        temp  = new ArrayList<Credential>();
        temp.add(cred4);
        temp.add(cred5);
        staticResolver45 = new StaticCredentialResolver(temp);
        
        temp = new ArrayList<Credential>();
        staticResolverEmpty = new StaticCredentialResolver(temp);
    }
    
    /**
     * Test a single chain member, which returns no credentials.
     * @throws ResolverException 
     */
    @Test
    public void testOneEmptyMember() throws ResolverException {
        chainingResolver = new ChainingCredentialResolver(Lists.newArrayList(staticResolverEmpty));
        
        List<Credential> resolved = getResolved(chainingResolver.resolve(criteriaSet));
        checkResolved(resolved, 0);
    }
    
    /**
     * Test multiple chain members, all of which return no credentials.
     * @throws ResolverException 
     */
    @Test
    public void testMultipleEmptyMember() throws ResolverException {
        chainingResolver = new ChainingCredentialResolver(Lists.newArrayList(staticResolverEmpty, staticResolverEmpty, staticResolverEmpty));
        
        List<Credential> resolved = getResolved(chainingResolver.resolve(criteriaSet));
        checkResolved(resolved, 0);
    }
    
    /**
     * Test one chain member, returning credentials.
     * @throws ResolverException 
     */
    @Test
    public void testOneMember() throws ResolverException {
        chainingResolver = new ChainingCredentialResolver(Lists.newArrayList(staticResolver12));
        
        List<Credential> resolved = getResolved(chainingResolver.resolve(criteriaSet));
        checkResolved(resolved, 2, cred1, cred2);
    }
    
    /**
     * Test multiple chain members, returning credentials.
     * @throws ResolverException 
     */
    @Test
    public void testMultipleMembers() throws ResolverException {
        chainingResolver = new ChainingCredentialResolver(
                Lists.newArrayList(staticResolver12, staticResolver3, staticResolverEmpty, staticResolver45));
        
        List<Credential> resolved = getResolved(chainingResolver.resolve(criteriaSet));
        checkResolved(resolved, 5, cred1, cred2, cred3, cred4, cred5);
    }
    
    /**
     * Test that order of returned credentials is the expected ordering,
     * based on the ordering in the resolver chain.
     * @throws ResolverException 
     */
    @Test
    public void testOrderingMultipleMembers() throws ResolverException {
        chainingResolver = new ChainingCredentialResolver(
                Lists.newArrayList(staticResolverEmpty, staticResolver45, staticResolverEmpty, staticResolver3, staticResolver12));
        
        List<Credential> resolved = getResolved(chainingResolver.resolve(criteriaSet));
        checkResolved(resolved, 5, cred1, cred2, cred3, cred4, cred5);
        
        Assert.assertEquals(resolved.get(0), cred4, "Credential found out-of-order");
        Assert.assertEquals(resolved.get(1), cred5, "Credential found out-of-order");
        Assert.assertEquals(resolved.get(2), cred3, "Credential found out-of-order");
        Assert.assertEquals(resolved.get(3), cred1, "Credential found out-of-order");
        Assert.assertEquals(resolved.get(4), cred2, "Credential found out-of-order");
    }
    
    /**
     * Test empty resolver chain, i.e. no underlying resolver members.
     * @throws ResolverException 
     */
    @Test(expectedExceptions=IllegalStateException.class)
    public void testEmptyResolverChain() throws ResolverException {
        chainingResolver = new ChainingCredentialResolver(new ArrayList<CredentialResolver>());
        chainingResolver.resolve(criteriaSet);
    }
    
    /**
     * Test exception on attempt to call remove() on iterator.
     * @throws ResolverException 
     */
    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void testRemove() throws ResolverException {
        chainingResolver = new ChainingCredentialResolver(Lists.newArrayList(staticResolver12));
        
        Iterator<Credential> iter = chainingResolver.resolve(criteriaSet).iterator();
        Assert.assertTrue(iter.hasNext(), "Iterator was empty");
        iter.next();
        iter.remove();
    }
    
    /**
     * Test exception on attempt to call next() on iterator when no more members.
     * @throws ResolverException 
     */
    @Test(expectedExceptions=NoSuchElementException.class)
    public void testNoMoreMembers() throws ResolverException {
        chainingResolver = new ChainingCredentialResolver(Lists.newArrayList(staticResolver12, staticResolver3));
        
        Iterator<Credential> iter = chainingResolver.resolve(criteriaSet).iterator();
        Assert.assertTrue(iter.hasNext(), "Should have next member");
        iter.next();
        Assert.assertTrue(iter.hasNext(), "Should have next member");
        iter.next();
        Assert.assertTrue(iter.hasNext(), "Should have next member");
        iter.next();
        
        Assert.assertFalse(iter.hasNext(), "Should NOT have next member");
        iter.next();
    }
    
    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void testChainUnmodifiable() {
        chainingResolver = new ChainingCredentialResolver(Lists.newArrayList(staticResolver12));
        chainingResolver.getResolverChain().add(staticResolver3);
    }
    
    /**
     * Get a set of the things that matched the set of criteria.
     * 
     * @param iter credential iterator
     * @return set of all credentials that were resolved
     */
    private List<Credential> getResolved(Iterable<Credential> iter) {
        ArrayList<Credential> resolved = new ArrayList<Credential>();
        for (Credential cred : iter) {
            resolved.add(cred);
        }
        return resolved;
    }
    
    /**
     * Helper method to evaluate the results of getResolved.
     * 
     * @param resolved set of resolved credentials
     * @param expectedNum expected number of resolved credentials
     * @param expectedCreds the vararg list of the credentials expected
     */
    private void checkResolved(List<Credential> resolved, int expectedNum, Credential... expectedCreds) {
        Assert.assertEquals(resolved.size(), expectedNum, "Unexpected number of matches");
        for (Credential expectedCred : expectedCreds) {
            Assert.assertTrue(resolved.contains(expectedCred), "Expected member not found: " + expectedCred);
        }
    }
    

}
