
package org.opensaml.xmlsec.impl;

import net.shibboleth.utilities.java.support.logic.ConstraintViolationException;

import org.opensaml.xmlsec.WhitelistBlacklistConfiguration.Precedence;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;

public class BasicWhitelistBlacklistConfigurationTest {
    
    private BasicWhitelistBlacklistConfiguration config;
    
    @BeforeMethod
    public void setUp() {
        config = new BasicWhitelistBlacklistConfiguration();
    }
    
    @Test
    public void testDefaults() {
        Assert.assertEquals(config.isWhitelistMerge(), false);
        Assert.assertNotNull(config.getWhitelistedAlgorithmURIs());
        Assert.assertTrue(config.getWhitelistedAlgorithmURIs().isEmpty());
        
        Assert.assertEquals(config.isBlacklistMerge(), false);
        Assert.assertNotNull(config.getBlacklistedAlgorithmURIs());
        Assert.assertTrue(config.getBlacklistedAlgorithmURIs().isEmpty());
        
        Assert.assertEquals(config.getWhitelistBlacklistPrecedence(), Precedence.WHITELIST);
    }
    
    @Test
    public void testValidWhitelist() {
        config.setWhitelistedAlgorithmURIs(Sets.newHashSet("  A   ", null, "   B   ", null, "   C   "));
        
        Assert.assertEquals(config.getWhitelistedAlgorithmURIs().size(), 3);
        Assert.assertTrue(config.getWhitelistedAlgorithmURIs().contains("A"));
        Assert.assertTrue(config.getWhitelistedAlgorithmURIs().contains("B"));
        Assert.assertTrue(config.getWhitelistedAlgorithmURIs().contains("C"));
    }

    @Test
    public void testNullWhitelist() {
        config.setWhitelistedAlgorithmURIs(null);
        Assert.assertNotNull(config.getWhitelistedAlgorithmURIs());
        Assert.assertTrue(config.getWhitelistedAlgorithmURIs().isEmpty());
    }

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void testWhitelistImmutable() {
        config.setWhitelistedAlgorithmURIs(Sets.newHashSet("A", "B", "C"));
        config.getWhitelistedAlgorithmURIs().add("D");
    }

    @Test
    public void testWhitelistMerge() {
        // Test default
        Assert.assertFalse(config.isWhitelistMerge());
        
        config.setWhitelistMerge(true);
        Assert.assertTrue(config.isWhitelistMerge());
        
        config.setWhitelistMerge(false);
        Assert.assertFalse(config.isWhitelistMerge());
    }

    @Test
    public void testValidBlacklist() {
        config.setBlacklistedAlgorithmURIs(Sets.newHashSet("   A   ", null, "   B   ", null, "   C   "));
        
        Assert.assertEquals(config.getBlacklistedAlgorithmURIs().size(), 3);
        Assert.assertTrue(config.getBlacklistedAlgorithmURIs().contains("A"));
        Assert.assertTrue(config.getBlacklistedAlgorithmURIs().contains("B"));
        Assert.assertTrue(config.getBlacklistedAlgorithmURIs().contains("C"));
    }
    
    @Test
    public void testNullBlacklist() {
        config.setBlacklistedAlgorithmURIs(null);
        Assert.assertNotNull(config.getBlacklistedAlgorithmURIs());
        Assert.assertTrue(config.getBlacklistedAlgorithmURIs().isEmpty());
    }
    
    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void testBlacklistImmutable() {
        config.setBlacklistedAlgorithmURIs(Sets.newHashSet("A", "B", "C"));
        config.getBlacklistedAlgorithmURIs().add("D");
    }
    
    @Test
    public void testBlacklistMerge() {
        // Test default
        Assert.assertFalse(config.isBlacklistMerge());
        
        config.setBlacklistMerge(true);
        Assert.assertTrue(config.isBlacklistMerge());
        
        config.setBlacklistMerge(false);
        Assert.assertFalse(config.isBlacklistMerge());
    }

    @Test
    public void testValidPrecedence() {
        // Test default
        Assert.assertEquals(config.getWhitelistBlacklistPrecedence(), Precedence.WHITELIST);
        
        config.setWhitelistBlacklistPrecedence(Precedence.WHITELIST);
        Assert.assertEquals(config.getWhitelistBlacklistPrecedence(), Precedence.WHITELIST);
        
        config.setWhitelistBlacklistPrecedence(Precedence.BLACKLIST);
        Assert.assertEquals(config.getWhitelistBlacklistPrecedence(), Precedence.BLACKLIST);
    }
    
    @Test(expectedExceptions=ConstraintViolationException.class)
    public void testNullPrecedence() {
        config.setWhitelistBlacklistPrecedence(null);
    }
    
}
