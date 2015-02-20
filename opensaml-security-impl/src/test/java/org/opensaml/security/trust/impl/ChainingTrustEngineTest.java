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

package org.opensaml.security.trust.impl;

import java.util.ArrayList;
import java.util.List;

import net.shibboleth.utilities.java.support.logic.ConstraintViolationException;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.security.SecurityException;
import org.opensaml.security.trust.TrustEngine;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test the chaining trust engine.
 */
public class ChainingTrustEngineTest {
    
    private CriteriaSet criteriaSet;
    
    private ChainingTrustEngine<FooToken> engine;
    
    private List<TrustEngine<? super FooToken>> chain;
    
    private FooToken token;

    @BeforeMethod
    protected void setUp() throws Exception {
        
        token = new FooToken();
        
        chain = new ArrayList<>();
        
        criteriaSet = new CriteriaSet();
        criteriaSet.add( new EntityIdCriterion("dummyEntityID") );
    }
    
    @Test
    public void testFirstTrusted() throws SecurityException {
        chain.add( new FooEngine(Boolean.TRUE));
        chain.add( new FooEngine(Boolean.FALSE));
        engine = new ChainingTrustEngine<>(chain);
        Assert.assertTrue(engine.validate(token, criteriaSet), "Engine # 1 evaled token as trusted");
    }

    @Test
    public void testSecondTrusted() throws SecurityException {
        chain.add( new FooEngine(Boolean.FALSE));
        chain.add( new FooEngine(Boolean.TRUE));
        engine = new ChainingTrustEngine<>(chain);
        Assert.assertTrue(engine.validate(token, criteriaSet), "Engine # 2 evaled token as trusted");
    }
    
    @Test
    public void testNoneTrusted() throws SecurityException {
        chain.add( new FooEngine(Boolean.FALSE));
        chain.add( new FooEngine(Boolean.FALSE));
        engine = new ChainingTrustEngine<>(chain);
        Assert.assertFalse(engine.validate(token, criteriaSet), "No engine evaled token as trusted");
    }
    
    @Test
    public void testMixedEngines() throws SecurityException {
        // Testing generically-mixed trust engine types, and input of a mixed List<TrustEngine<? super FooToken>>.
        chain.add( new FooEngine(Boolean.FALSE));
        chain.add( new SuperEngine(Boolean.TRUE));
        engine = new ChainingTrustEngine<>(chain);
        Assert.assertTrue(engine.validate(token, criteriaSet), "SuperEngine evaled token as trusted");
    }
    
    @Test
    public void testNullEngineOK() throws SecurityException {
        chain.add( new FooEngine(Boolean.FALSE));
        chain.add( null );
        chain.add( new FooEngine(Boolean.TRUE));
        engine = new ChainingTrustEngine<>(chain);
        Assert.assertTrue(engine.validate(token, criteriaSet), 
                "Engine # 3 evaled token as trusted with intervening null engine");
    }
    
    @Test(expectedExceptions=SecurityException.class)
    public void testException() throws SecurityException {
        chain.add( new FooEngine(Boolean.FALSE));
        chain.add( new FooEngine(null));
        engine = new ChainingTrustEngine<>(chain);
        engine.validate(token, criteriaSet);
    }
    
    @Test(expectedExceptions=ConstraintViolationException.class)
    public void testNullChain() {
        engine = new ChainingTrustEngine<>(null);
    }
    
    /** Mock token types. */
    private class SuperToken {
        
    }
    private class FooToken extends SuperToken {
        
    }
    
    /** Mock trust engine for FooToken. */
    private class FooEngine implements TrustEngine<FooToken> {
        
        private Boolean trusted;
        
        private FooEngine(Boolean trusted) {
            this.trusted = trusted;
        }

        /** {@inheritDoc} */
        public boolean validate(FooToken token, CriteriaSet trustBasisCriteria) throws SecurityException {
            if (trusted == null) {
                throw new SecurityException("This means an error happened");
            }
            return trusted;
        }
        
    }

    /** Mock trust engine for SuperToken. */
    private class SuperEngine implements TrustEngine<SuperToken> {
        
        private Boolean trusted;
        
        private SuperEngine(Boolean trusted) {
            this.trusted = trusted;
        }

        /** {@inheritDoc} */
        public boolean validate(SuperToken token, CriteriaSet trustBasisCriteria) throws SecurityException {
            if (trusted == null) {
                throw new SecurityException("This means an error happened");
            }
            return trusted;
        }
        
    }

}
