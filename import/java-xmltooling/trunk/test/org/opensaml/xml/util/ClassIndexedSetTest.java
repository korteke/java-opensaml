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

package org.opensaml.xml.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import junit.framework.TestCase;

import org.opensaml.xml.security.credential.CredentialCriteria;
import org.opensaml.xml.security.credential.EntityCredentialCriteria;
import org.opensaml.xml.security.credential.KeyCredentialCriteria;
import org.opensaml.xml.security.credential.UsageType;
import org.opensaml.xml.security.keyinfo.KeyInfoCredentialCriteria;
import org.opensaml.xml.security.x509.X509CertificateCredentialCriteria;

/**
 * Tests the ClassIndexedSet, using CredentialCriteria as the underlying type.
 */
public class ClassIndexedSetTest extends TestCase {
    
    /** Criteria set to use as target for tests. */
    private ClassIndexedSet<CredentialCriteria> criteriaSet;

    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
        criteriaSet = new ClassIndexedSet<CredentialCriteria>();
    }
    
    /**
     *  Test failure of adding a duplicate instance.
     */
    public void testDupInstance() {
        EntityCredentialCriteria  entityCriteria = new EntityCredentialCriteria("owner", "peer", UsageType.SIGNING);
        criteriaSet.add(entityCriteria);
        
        try {
            criteriaSet.add(entityCriteria);
            fail("Set already contained the specified instance");
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
        criteriaSet.add(entityCriteria1);
        
        try {
            criteriaSet.add(entityCriteria2);
            fail("Set already contained an instance of the specified class");
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
        criteriaSet.add(entityCriteria1);
        
        try {
            criteriaSet.add(entityCriteria2, true);
        } catch (IllegalArgumentException e) {
            fail("Set should have replaced existing criteria type");
        }
        
        assertFalse("Did not find the expected criteria instance",
                entityCriteria1 == criteriaSet.get(EntityCredentialCriteria.class) );
        assertTrue("Did not find the expected criteria instance",
                entityCriteria2 == criteriaSet.get(EntityCredentialCriteria.class) );
        
    }
    
    /**
     *  Test getting criteria instance from set by type.
     */
    public void testGetType() {
        EntityCredentialCriteria  entityCriteria = new EntityCredentialCriteria("owner", "peer", UsageType.SIGNING);
        criteriaSet.add(entityCriteria);
        KeyCredentialCriteria  keyCriteria = new KeyCredentialCriteria("keyName", "algorithm", 256, null);
        criteriaSet.add(keyCriteria);
        
        assertTrue("Did not find the expected criteria instance",
                entityCriteria == criteriaSet.get(EntityCredentialCriteria.class) );
        assertTrue("Did not find the expected criteria instance",
                keyCriteria == criteriaSet.get(KeyCredentialCriteria.class) );
        assertTrue("Did not find the expected (null) criteria instance",
                null == criteriaSet.get(KeyInfoCredentialCriteria.class) );
    }
    
    /** Tests removing criteria from set by instance. */
    public void testRemove() {
        EntityCredentialCriteria  entityCriteria = new EntityCredentialCriteria("owner", "peer", UsageType.SIGNING);
        criteriaSet.add(entityCriteria);
        KeyCredentialCriteria  keyCriteria = new KeyCredentialCriteria("keyName", "algorithm", 256, null);
        criteriaSet.add(keyCriteria);
        
        assertEquals("Set had unexpected size", 2, criteriaSet.size());
        
        criteriaSet.remove(keyCriteria);
        assertEquals("Set had unexpected size", 1, criteriaSet.size());
        assertNull("Set returned removed value", criteriaSet.get(KeyCredentialCriteria.class));
        
        criteriaSet.remove(entityCriteria);
        assertEquals("Set had unexpected size", 0, criteriaSet.size());
        assertNull("Set returned removed value", criteriaSet.get(EntityCredentialCriteria.class));
    }
    
    /** Tests clearing the set. */
    public void testClear() {
        EntityCredentialCriteria  entityCriteria = new EntityCredentialCriteria("owner", "peer", UsageType.SIGNING);
        criteriaSet.add(entityCriteria);
        KeyCredentialCriteria  keyCriteria = new KeyCredentialCriteria("keyName", "algorithm", 256, null);
        criteriaSet.add(keyCriteria);
        
        assertEquals("Set had unexpected size", 2, criteriaSet.size());
        
        criteriaSet.clear();
        assertEquals("Set had unexpected size", 0, criteriaSet.size());
        
        assertNull("Set returned removed value", criteriaSet.get(KeyCredentialCriteria.class));
        assertNull("Set returned removed value", criteriaSet.get(EntityCredentialCriteria.class));
    }
    
    /** Tests proper iterator iterating behavior. */
    public void testIterator() {
        EntityCredentialCriteria  entityCriteria = new EntityCredentialCriteria("owner", "peer", UsageType.SIGNING);
        criteriaSet.add(entityCriteria);
        KeyCredentialCriteria  keyCriteria = new KeyCredentialCriteria("keyName", "algorithm", 256, null);
        criteriaSet.add(keyCriteria);
        KeyInfoCredentialCriteria keyInfoCriteria = new KeyInfoCredentialCriteria(null);
        criteriaSet.add(keyInfoCriteria);
        
        assertEquals("Set had unexpected size", 3, criteriaSet.size());
        
        int count = 0;
        HashSet<CredentialCriteria> unique = new HashSet<CredentialCriteria>();
        for ( CredentialCriteria criteria : criteriaSet) {
            count++;
            assertTrue("Duplicate was returned by iterator", unique.add(criteria));
        }
        assertEquals("Set iteration had unexpected count", 3, count);
        
        Iterator<CredentialCriteria> iterator = criteriaSet.iterator();
        assertTrue("Iterator should have more elements", iterator.hasNext());
        iterator.next();
        assertTrue("Iterator should have more elements", iterator.hasNext());
        iterator.next();
        assertTrue("Iterator should have more elements", iterator.hasNext());
        iterator.next();
        assertFalse("Iterator should have no more elements", iterator.hasNext());
        try {
            iterator.next();
            fail("Should have seen a iterator exception, no more elements available in set");
        } catch (NoSuchElementException e) {
            // do nothing, should fail
        }
        
    }
    
    /** Tests proper iterator remove() behavior. */
    public void testIteratorRemove() {
        criteriaSet = new ClassIndexedSet<CredentialCriteria>();
        EntityCredentialCriteria  entityCriteria = new EntityCredentialCriteria("owner", "peer", UsageType.SIGNING);
        criteriaSet.add(entityCriteria);
        KeyCredentialCriteria  keyCriteria = new KeyCredentialCriteria("keyName", "algorithm", 256, null);
        criteriaSet.add(keyCriteria);
        KeyInfoCredentialCriteria keyInfoCriteria = new KeyInfoCredentialCriteria(null);
        criteriaSet.add(keyInfoCriteria);
        X509CertificateCredentialCriteria x509Criteria = new X509CertificateCredentialCriteria(null, null, null, null);
        criteriaSet.add(x509Criteria);
        
        assertEquals("Set had unexpected size", 4, criteriaSet.size());
        
        Iterator<CredentialCriteria> iterator = criteriaSet.iterator();
        CredentialCriteria criteria = null;
        while ( iterator.hasNext() ) {
            criteria = iterator.next();
            if (criteria instanceof KeyCredentialCriteria) {
                iterator.remove();
            }
        }
        assertEquals("Set iteration had unexpected size", 3, criteriaSet.size());
        
        assertTrue("Set did not contain expected instance", criteriaSet.contains(entityCriteria));
        assertTrue("Set did not contain expected instance", criteriaSet.contains(keyInfoCriteria));
        assertTrue("Set did not contain expected instance", criteriaSet.contains(x509Criteria));
        assertFalse("Set contained unexpected instance", criteriaSet.contains(keyCriteria));
        
        assertTrue("Set did not contain expected class type", 
                criteriaSet.contains(EntityCredentialCriteria.class));
        assertTrue("Set did not contain expected class type", 
                criteriaSet.contains(KeyInfoCredentialCriteria.class));
        assertTrue("Set did not contain expected class type", 
                criteriaSet.contains(X509CertificateCredentialCriteria.class));
        assertFalse("Set contained unexpected class type", 
                criteriaSet.contains(KeyCredentialCriteria.class));
    }
        
    /** Tests proper iterator remove() behavior when called illegally. */
    public void testIteratorRemoveIllegal() {
        criteriaSet = new ClassIndexedSet<CredentialCriteria>();
        EntityCredentialCriteria  entityCriteria = new EntityCredentialCriteria("owner", "peer", UsageType.SIGNING);
        criteriaSet.add(entityCriteria);
        KeyCredentialCriteria  keyCriteria = new KeyCredentialCriteria("keyName", "algorithm", 256, null);
        criteriaSet.add(keyCriteria);
        KeyInfoCredentialCriteria keyInfoCriteria = new KeyInfoCredentialCriteria(null);
        criteriaSet.add(keyInfoCriteria);
        X509CertificateCredentialCriteria x509Criteria = new X509CertificateCredentialCriteria(null, null, null, null);
        criteriaSet.add(x509Criteria);
        
        assertEquals("Set had unexpected size", 4, criteriaSet.size());
        
        Iterator<CredentialCriteria> iterator = criteriaSet.iterator();
        try {
            iterator.remove();
            fail("Should have seen a iterator exception, remove() called before first next()");
        } catch (IllegalStateException e) {
            // do nothing, should fail
        }
        
        iterator = criteriaSet.iterator();
        iterator.next();
        iterator.remove();
        try {
            iterator.remove();
            fail("Should have seen a iterator exception, remove() called twice on same element");
        } catch (IllegalStateException e) {
            // do nothing, should fail
        }
    }

}
