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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.ConstraintViolationException;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.WhitelistBlacklistConfiguration;
import org.opensaml.xmlsec.WhitelistBlacklistConfiguration.Precedence;
import org.opensaml.xmlsec.WhitelistBlacklistParameters;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;
import org.opensaml.xmlsec.keyinfo.impl.BasicKeyInfoGeneratorFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

/**
 * Test various aspects of the {@link AbstractSecurityParametersResolver} so don't have to test
 * them in all the individual subclasses.
 */
public class AbstractSecurityParametersResolverTest extends XMLObjectBaseTestCase {
    
    private WhitelistBlacklistParametersResolver resolver;
    
    private BasicWhitelistBlacklistConfiguration config1, config2, config3;
    private  WhitelistBlacklistConfigurationCriterion criterion;
    private CriteriaSet criteriaSet;
    private Set<String> set1, set2, set3;
    
    @BeforeMethod
    public void setUp() {
        resolver = new WhitelistBlacklistParametersResolver();
        
        config1 = new BasicWhitelistBlacklistConfiguration();
        config2 = new BasicWhitelistBlacklistConfiguration();
        config3 = new BasicWhitelistBlacklistConfiguration();
        
        criterion = new WhitelistBlacklistConfigurationCriterion(config1, config2, config3);
        
        criteriaSet = new CriteriaSet(criterion);
        
        set1 = new HashSet<>(Arrays.asList("A", "B", "C", "D"));
        set2 = new HashSet<>(Arrays.asList("X", "Y", "Z"));
        set3 = new HashSet<>(Arrays.asList("foo", "bar", "baz"));
    }
    
    @Test
    public void testBlacklistOnlyDefaults() throws ResolverException {
        config1.setBlacklistedAlgorithms(set1);
        config2.setBlacklistedAlgorithms(set2);
        
        WhitelistBlacklistParameters params = resolver.resolveSingle(criteriaSet);
        
        HashSet<String> control = new HashSet<>();
        control.addAll(set1);
        control.addAll(set2);
        
        Assert.assertEquals(params.getWhitelistedAlgorithms(), Collections.emptySet());
        Assert.assertEquals(params.getBlacklistedAlgorithms(), control);
    }
    
    @Test
    public void testBlacklistOnlyNoMerge() throws ResolverException {
        config1.setBlacklistedAlgorithms(set1);
        config1.setBlacklistMerge(false);
        config2.setBlacklistedAlgorithms(set2);
        
        WhitelistBlacklistParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertEquals(params.getWhitelistedAlgorithms(), Collections.emptySet());
        Assert.assertEquals(params.getBlacklistedAlgorithms(), set1);
    }
    
    @Test
    public void testBlacklistOnlyWithSimpleMerge() throws ResolverException {
        config1.setBlacklistedAlgorithms(set1);
        config1.setBlacklistMerge(true);
        config2.setBlacklistedAlgorithms(set2);
        
        WhitelistBlacklistParameters params = resolver.resolveSingle(criteriaSet);
        
        HashSet<String> control = new HashSet<>();
        control.addAll(set1);
        control.addAll(set2);
        
        Assert.assertEquals(params.getWhitelistedAlgorithms(), Collections.emptySet());
        Assert.assertEquals(params.getBlacklistedAlgorithms(), control);
    }
    
    @Test
    public void testBlacklistOnlyWithTransitiveMerge() throws ResolverException {
        config1.setBlacklistedAlgorithms(set1);
        config1.setBlacklistMerge(true);
        config2.setBlacklistMerge(true);
        config3.setBlacklistedAlgorithms(set3);
        
        WhitelistBlacklistParameters params = resolver.resolveSingle(criteriaSet);
        
        HashSet<String> control = new HashSet<>();
        control.addAll(set1);
        control.addAll(set3);
        
        Assert.assertEquals(params.getWhitelistedAlgorithms(), Collections.emptySet());
        Assert.assertEquals(params.getBlacklistedAlgorithms(), control);
    }
    
    @Test
    public void testWhitelistOnlyDefaults() throws ResolverException {
        config1.setWhitelistedAlgorithms(set1);
        config2.setWhitelistedAlgorithms(set2);
        
        WhitelistBlacklistParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertEquals(params.getWhitelistedAlgorithms(), set1);
        Assert.assertEquals(params.getBlacklistedAlgorithms(), Collections.emptySet());
    }
    
    @Test
    public void testWhitelistOnlyWithSimpleMerge() throws ResolverException {
        config1.setWhitelistedAlgorithms(set1);
        config1.setWhitelistMerge(true);
        config2.setWhitelistedAlgorithms(set2);
        
        WhitelistBlacklistParameters params = resolver.resolveSingle(criteriaSet);
        
        HashSet<String> control = new HashSet<>();
        control.addAll(set1);
        control.addAll(set2);
        
        Assert.assertEquals(params.getWhitelistedAlgorithms(), control);
        Assert.assertEquals(params.getBlacklistedAlgorithms(), Collections.emptySet());
    }
    
    @Test
    public void testWhitelistOnlyWithTransitiveMerge() throws ResolverException {
        config1.setWhitelistedAlgorithms(set1);
        config1.setWhitelistMerge(true);
        config2.setWhitelistMerge(true);
        config3.setWhitelistedAlgorithms(set3);
        
        WhitelistBlacklistParameters params = resolver.resolveSingle(criteriaSet);
        
        HashSet<String> control = new HashSet<>();
        control.addAll(set1);
        control.addAll(set3);
        
        Assert.assertEquals(params.getWhitelistedAlgorithms(), control);
        Assert.assertEquals(params.getBlacklistedAlgorithms(), Collections.emptySet());
    }
    
    @Test
    public void testPrecedence() throws ResolverException {
        config1.setWhitelistedAlgorithms(set1);
        config1.setBlacklistedAlgorithms(set2);
        
        config1.setWhitelistBlacklistPrecedence(Precedence.WHITELIST);
        
        WhitelistBlacklistParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertEquals(params.getWhitelistedAlgorithms(), set1);
        Assert.assertEquals(params.getBlacklistedAlgorithms(), Collections.emptySet());
        
        config1.setWhitelistBlacklistPrecedence(Precedence.BLACKLIST);
        
        params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertEquals(params.getWhitelistedAlgorithms(), Collections.emptySet());
        Assert.assertEquals(params.getBlacklistedAlgorithms(), set2);
    }

    
    @Test
    public void testResolvePredicate() {
        Predicate<String> predicate;
        
        config1.setWhitelistedAlgorithms(set1);
        config1.setBlacklistedAlgorithms(set2);
        
        config1.setWhitelistBlacklistPrecedence(Precedence.WHITELIST);
        
        predicate = resolver.resolveWhitelistBlacklistPredicate(criteriaSet, Arrays.asList(config1, config2, config3));
        
        // Note: Have effective whitelist based on set1
        
        Assert.assertTrue(predicate.apply("A"));
        Assert.assertTrue(predicate.apply("B"));
        Assert.assertTrue(predicate.apply("C"));
        Assert.assertTrue(predicate.apply("D"));
        
        Assert.assertFalse(predicate.apply("X"));
        Assert.assertFalse(predicate.apply("Y"));
        Assert.assertFalse(predicate.apply("Z"));
        Assert.assertFalse(predicate.apply("foo"));
        Assert.assertFalse(predicate.apply("bar"));
        Assert.assertFalse(predicate.apply("bax"));
        
        config1.setWhitelistBlacklistPrecedence(Precedence.BLACKLIST);
        
        predicate = resolver.resolveWhitelistBlacklistPredicate(criteriaSet, Arrays.asList(config1, config2, config3));
        
        // Note: Have effective blacklist based on set2
        
        Assert.assertTrue(predicate.apply("A"));
        Assert.assertTrue(predicate.apply("B"));
        Assert.assertTrue(predicate.apply("C"));
        Assert.assertTrue(predicate.apply("D"));
        Assert.assertTrue(predicate.apply("foo"));
        Assert.assertTrue(predicate.apply("bar"));
        Assert.assertTrue(predicate.apply("bax"));
        
        Assert.assertFalse(predicate.apply("X"));
        Assert.assertFalse(predicate.apply("Y"));
        Assert.assertFalse(predicate.apply("Z"));
    }
    
    @Test
    public void testResolveEffectiveWhitelist() {
        Collection<String> whitelist;
        
        whitelist = resolver.resolveEffectiveWhitelist(criteriaSet, criterion.getConfigurations());
        Assert.assertTrue(whitelist.isEmpty());
        
        config1.setWhitelistedAlgorithms(set1);
        config2.setWhitelistedAlgorithms(set2);
        config3.setWhitelistedAlgorithms(set3);   
        
        whitelist = resolver.resolveEffectiveWhitelist(criteriaSet, criterion.getConfigurations());
        Assert.assertTrue(whitelist.containsAll(set1));
        Assert.assertFalse(whitelist.containsAll(set2));
        Assert.assertFalse(whitelist.containsAll(set3));
        
        config1.setWhitelistMerge(true);
        
        whitelist = resolver.resolveEffectiveWhitelist(criteriaSet, criterion.getConfigurations());
        
        Assert.assertTrue(whitelist.containsAll(set1));
        Assert.assertTrue(whitelist.containsAll(set2));
        Assert.assertFalse(whitelist.containsAll(set3));
        
        config1.setWhitelistMerge(true);
        config2.setWhitelistMerge(true);
        
        whitelist = resolver.resolveEffectiveWhitelist(criteriaSet, criterion.getConfigurations());
        
        Assert.assertTrue(whitelist.containsAll(set1));
        Assert.assertTrue(whitelist.containsAll(set2));
        Assert.assertTrue(whitelist.containsAll(set3));
        
        
        // Set 1 and 2 empty
        config1.setWhitelistedAlgorithms(new HashSet<String>());
        config2.setWhitelistedAlgorithms(new HashSet<String>());
        
        config1.setWhitelistMerge(true);
        config2.setWhitelistMerge(true);
        
        whitelist = resolver.resolveEffectiveWhitelist(criteriaSet, criterion.getConfigurations());
        
        Assert.assertFalse(whitelist.containsAll(set1));
        Assert.assertFalse(whitelist.containsAll(set2));
        Assert.assertTrue(whitelist.containsAll(set3));
    }
    
    @Test
    public void testResolveEffectiveBlacklist() {
        Collection<String> blacklist;
        
        blacklist = resolver.resolveEffectiveBlacklist(criteriaSet, criterion.getConfigurations());
        Assert.assertTrue(blacklist.isEmpty());
        
        config1.setBlacklistedAlgorithms(set1);
        config2.setBlacklistedAlgorithms(set2);
        config3.setBlacklistedAlgorithms(set3);   
        
        blacklist = resolver.resolveEffectiveBlacklist(criteriaSet, criterion.getConfigurations());
        Assert.assertTrue(blacklist.containsAll(set1));
        Assert.assertTrue(blacklist.containsAll(set2));
        Assert.assertTrue(blacklist.containsAll(set3));
        
        config2.setBlacklistMerge(false);
        
        blacklist = resolver.resolveEffectiveBlacklist(criteriaSet, criterion.getConfigurations());
        
        Assert.assertTrue(blacklist.containsAll(set1));
        Assert.assertTrue(blacklist.containsAll(set2));
        Assert.assertFalse(blacklist.containsAll(set3));
        
        config1.setBlacklistMerge(false);
        config2.setBlacklistMerge(false);
        
        blacklist = resolver.resolveEffectiveBlacklist(criteriaSet, criterion.getConfigurations());
        
        Assert.assertTrue(blacklist.containsAll(set1));
        Assert.assertFalse(blacklist.containsAll(set2));
        Assert.assertFalse(blacklist.containsAll(set3));
        
        
        // Set 1 and 2 empty
        config1.setBlacklistedAlgorithms(new HashSet<String>());
        config2.setBlacklistedAlgorithms(new HashSet<String>());
        
        config1.setBlacklistMerge(true);
        config2.setBlacklistMerge(true);
        
        blacklist = resolver.resolveEffectiveBlacklist(criteriaSet, criterion.getConfigurations());
        
        Assert.assertFalse(blacklist.containsAll(set1));
        Assert.assertFalse(blacklist.containsAll(set2));
        Assert.assertTrue(blacklist.containsAll(set3));
    }
    
    @Test
    public void testResolveEffectivePrecedence() {
        WhitelistBlacklistConfiguration.Precedence precedence;
        
        config1.setWhitelistBlacklistPrecedence(Precedence.WHITELIST);
        precedence = resolver.resolveWhitelistBlacklistPrecedence(criteriaSet, criterion.getConfigurations());
        Assert.assertEquals(precedence, WhitelistBlacklistConfiguration.Precedence.WHITELIST);
        
        config1.setWhitelistBlacklistPrecedence(Precedence.BLACKLIST);
        precedence = resolver.resolveWhitelistBlacklistPrecedence(criteriaSet, criterion.getConfigurations());
        Assert.assertEquals(precedence, WhitelistBlacklistConfiguration.Precedence.BLACKLIST);
    }
    
    @Test
    public void testKeyInfoGeneratorLookup() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPair kp = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_RSA, 2048, null);
        Credential cred = CredentialSupport.getSimpleCredential(kp.getPublic(), kp.getPrivate());
        NamedKeyInfoGeneratorManager manager;
        
        manager = new NamedKeyInfoGeneratorManager();
        manager.setUseDefaultManager(false);
        manager.registerDefaultFactory(new BasicKeyInfoGeneratorFactory());
        Assert.assertNotNull(resolver.lookupKeyInfoGenerator(cred, manager, null));
        Assert.assertNull(resolver.lookupKeyInfoGenerator(cred, manager, "test"));
        
        manager = new NamedKeyInfoGeneratorManager();
        manager.setUseDefaultManager(true);
        manager.registerDefaultFactory(new BasicKeyInfoGeneratorFactory());
        Assert.assertNotNull(resolver.lookupKeyInfoGenerator(cred, manager, null));
        Assert.assertNotNull(resolver.lookupKeyInfoGenerator(cred, manager, "test"));
        
        manager = new NamedKeyInfoGeneratorManager();
        manager.registerFactory("test", new BasicKeyInfoGeneratorFactory());
        Assert.assertNull(resolver.lookupKeyInfoGenerator(cred, manager, null));
        Assert.assertNotNull(resolver.lookupKeyInfoGenerator(cred, manager, "test"));
        
        Assert.assertNull(resolver.lookupKeyInfoGenerator(cred, null, null));
        Assert.assertNull(resolver.lookupKeyInfoGenerator(cred, null, "test"));
        
        try {
            resolver.lookupKeyInfoGenerator(null, manager, "test");
            Assert.fail("Null credential should have thrown");
        } catch (ConstraintViolationException e) {
            // expected
        }
    }
    
    
    /* Supporting classes */
    
    /** Concrete class used for testing the abstract class. */
    public class WhitelistBlacklistParametersResolver extends AbstractSecurityParametersResolver<WhitelistBlacklistParameters> {

        /** {@inheritDoc} */
        @Nonnull
        public Iterable<WhitelistBlacklistParameters> resolve(CriteriaSet criteria) throws ResolverException {
            WhitelistBlacklistParameters params = resolveSingle(criteria);
            if (params != null) {
                return Collections.singletonList(params);
            } else {
                return Collections.emptyList();
            }
        }

        /** {@inheritDoc} */
        @Nullable
        public WhitelistBlacklistParameters resolveSingle(CriteriaSet criteria) throws ResolverException {
            WhitelistBlacklistParameters params = new WhitelistBlacklistParameters();
            resolveAndPopulateWhiteAndBlacklists(params, criteria, 
                    criteria.get(WhitelistBlacklistConfigurationCriterion.class).getConfigurations());
            return params;
        }
        
    }

}
