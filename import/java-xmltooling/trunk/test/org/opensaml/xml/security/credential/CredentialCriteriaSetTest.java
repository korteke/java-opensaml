/*
 * Copyright [2007] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.xml.security.credential;

import org.opensaml.xml.security.keyinfo.KeyInfoCredentialCriteria;

import junit.framework.TestCase;

/**
 * Tests the CredentialCriteriaSet used to hold CredentialCriteria.
 */
public class CredentialCriteriaSetTest extends TestCase {
    
    /** Criteria set to use as target for tests. */
    private CredentialCriteriaSet criteraSet;

    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
        criteraSet = new CredentialCriteriaSet();
    }
    
    /**
     *  Test failure of adding a duplicate instance.
     */
    public void testDupInstance() {
        EntityCredentialCriteria  entityCriteria = new EntityCredentialCriteria("owner", "peer", UsageType.SIGNING);
        criteraSet.add(entityCriteria);
        
        try {
            criteraSet.add(entityCriteria);
            fail("Set already contained the criteria instance");
        } catch (IllegalArgumentException e) {
            // it should fail
        }
    }
    
    /**
     *  Test failure of adding a duplicate criteria type.
     */
    public void testDupType() {
        EntityCredentialCriteria  entityCriteria1 = 
            new EntityCredentialCriteria("owner", "peer", UsageType.SIGNING);
        EntityCredentialCriteria  entityCriteria2 = 
            new EntityCredentialCriteria("owner#2", "peer#2", UsageType.ENCRYPTION);
        criteraSet.add(entityCriteria1);
        
        try {
            criteraSet.add(entityCriteria2);
            fail("Set already contained an instance of BasicCredentialCriteria");
        } catch (IllegalArgumentException e) {
            // it should fail
        }
    }
    
    /**
     *  Test success of adding a duplicate criteria type with replacement.
     */
    public void testDupTypeWithReplacement() {
        EntityCredentialCriteria  entityCriteria1 = 
            new EntityCredentialCriteria("owner", "peer", UsageType.SIGNING);
        EntityCredentialCriteria  entityCriteria2 = 
            new EntityCredentialCriteria("owner#2", "peer#2", UsageType.ENCRYPTION);
        criteraSet.add(entityCriteria1);
        
        try {
            criteraSet.add(entityCriteria2, true);
        } catch (IllegalArgumentException e) {
            fail("Set should have replaced existing criteria type");
        }
        
        assertFalse("Did not find the expected criteria instance",
                entityCriteria1 == criteraSet.getCriteria(EntityCredentialCriteria.class) );
        assertTrue("Did not find the expected criteria instance",
                entityCriteria2 == criteraSet.getCriteria(EntityCredentialCriteria.class) );
        
    }
    
    /**
     *  Test getting criteria instance from set by type.
     */
    public void testGetType() {
        EntityCredentialCriteria  entityCriteria = new EntityCredentialCriteria("owner", "peer", UsageType.SIGNING);
        criteraSet.add(entityCriteria);
        KeyCredentialCriteria  keyCriteria = new KeyCredentialCriteria("keyName", "algorithm", 256, null);
        criteraSet.add(keyCriteria);
        
        assertTrue("Did not find the expected criteria instance",
                entityCriteria == criteraSet.getCriteria(EntityCredentialCriteria.class) );
        assertTrue("Did not find the expected criteria instance",
                keyCriteria == criteraSet.getCriteria(KeyCredentialCriteria.class) );
        assertTrue("Did not find the expected (null) criteria instance",
                null == criteraSet.getCriteria(KeyInfoCredentialCriteria.class) );
    }

}
