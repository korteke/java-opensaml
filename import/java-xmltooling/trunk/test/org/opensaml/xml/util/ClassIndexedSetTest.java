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
import org.opensaml.xml.security.credential.EntityCriteria;
import org.opensaml.xml.security.credential.KeyConstraintCriteria;
import org.opensaml.xml.security.keyinfo.KeyInfoCriteria;
import org.opensaml.xml.security.x509.X509CertificateCriteria;

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
        EntityCriteria  entityCriteria = new EntityCriteria("owner", "peer");
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
        EntityCriteria  entityCriteria1 = 
            new EntityCriteria("owner", "peer");
        EntityCriteria  entityCriteria2 = 
            new EntityCriteria("owner#2", "peer#2");
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
        EntityCriteria  entityCriteria1 = 
            new EntityCriteria("owner", "peer");
        EntityCriteria  entityCriteria2 = 
            new EntityCriteria("owner#2", "peer#2");
        criteriaSet.add(entityCriteria1);
        
        try {
            criteriaSet.add(entityCriteria2, true);
        } catch (IllegalArgumentException e) {
            fail("Set should have replaced existing criteria type");
        }
        
        assertFalse("Did not find the expected criteria instance",
                entityCriteria1 == criteriaSet.get(EntityCriteria.class) );
        assertTrue("Did not find the expected criteria instance",
                entityCriteria2 == criteriaSet.get(EntityCriteria.class) );
        
    }
    
    /**
     *  Test getting criteria instance from set by type.
     */
    public void testGetType() {
        EntityCriteria  entityCriteria = new EntityCriteria("owner", "peer");
        criteriaSet.add(entityCriteria);
        KeyConstraintCriteria  keyCriteria = new KeyConstraintCriteria("algorithm", 256);
        criteriaSet.add(keyCriteria);
        
        assertTrue("Did not find the expected criteria instance",
                entityCriteria == criteriaSet.get(EntityCriteria.class) );
        assertTrue("Did not find the expected criteria instance",
                keyCriteria == criteriaSet.get(KeyConstraintCriteria.class) );
        assertTrue("Did not find the expected (null) criteria instance",
                null == criteriaSet.get(KeyInfoCriteria.class) );
    }
    
    /** Tests removing criteria from set by instance. */
    public void testRemove() {
        EntityCriteria  entityCriteria = new EntityCriteria("owner", "peer");
        criteriaSet.add(entityCriteria);
        KeyConstraintCriteria  keyCriteria = new KeyConstraintCriteria("algorithm", 256);
        criteriaSet.add(keyCriteria);
        
        assertEquals("Set had unexpected size", 2, criteriaSet.size());
        
        criteriaSet.remove(keyCriteria);
        assertEquals("Set had unexpected size", 1, criteriaSet.size());
        assertNull("Set returned removed value", criteriaSet.get(KeyConstraintCriteria.class));
        
        criteriaSet.remove(entityCriteria);
        assertEquals("Set had unexpected size", 0, criteriaSet.size());
        assertNull("Set returned removed value", criteriaSet.get(EntityCriteria.class));
    }
    
    /** Tests clearing the set. */
    public void testClear() {
        EntityCriteria  entityCriteria = new EntityCriteria("owner", "peer");
        criteriaSet.add(entityCriteria);
        KeyConstraintCriteria  keyCriteria = new KeyConstraintCriteria("algorithm", 256);
        criteriaSet.add(keyCriteria);
        
        assertEquals("Set had unexpected size", 2, criteriaSet.size());
        
        criteriaSet.clear();
        assertEquals("Set had unexpected size", 0, criteriaSet.size());
        
        assertNull("Set returned removed value", criteriaSet.get(KeyConstraintCriteria.class));
        assertNull("Set returned removed value", criteriaSet.get(EntityCriteria.class));
    }
    
    /** Tests proper iterator iterating behavior. */
    public void testIterator() {
        EntityCriteria  entityCriteria = new EntityCriteria("owner", "peer");
        criteriaSet.add(entityCriteria);
        KeyConstraintCriteria  keyCriteria = new KeyConstraintCriteria("algorithm", 256);
        criteriaSet.add(keyCriteria);
        KeyInfoCriteria keyInfoCriteria = new KeyInfoCriteria(null);
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
        EntityCriteria  entityCriteria = new EntityCriteria("owner", "peer");
        criteriaSet.add(entityCriteria);
        KeyConstraintCriteria  keyCriteria = new KeyConstraintCriteria("algorithm", 256);
        criteriaSet.add(keyCriteria);
        KeyInfoCriteria keyInfoCriteria = new KeyInfoCriteria(null);
        criteriaSet.add(keyInfoCriteria);
        X509CertificateCriteria x509Criteria = new X509CertificateCriteria(null, null, null, null);
        criteriaSet.add(x509Criteria);
        
        assertEquals("Set had unexpected size", 4, criteriaSet.size());
        
        Iterator<CredentialCriteria> iterator = criteriaSet.iterator();
        CredentialCriteria criteria = null;
        while ( iterator.hasNext() ) {
            criteria = iterator.next();
            if (criteria instanceof KeyConstraintCriteria) {
                iterator.remove();
            }
        }
        assertEquals("Set iteration had unexpected size", 3, criteriaSet.size());
        
        assertTrue("Set did not contain expected instance", criteriaSet.contains(entityCriteria));
        assertTrue("Set did not contain expected instance", criteriaSet.contains(keyInfoCriteria));
        assertTrue("Set did not contain expected instance", criteriaSet.contains(x509Criteria));
        assertFalse("Set contained unexpected instance", criteriaSet.contains(keyCriteria));
        
        assertTrue("Set did not contain expected class type", 
                criteriaSet.contains(EntityCriteria.class));
        assertTrue("Set did not contain expected class type", 
                criteriaSet.contains(KeyInfoCriteria.class));
        assertTrue("Set did not contain expected class type", 
                criteriaSet.contains(X509CertificateCriteria.class));
        assertFalse("Set contained unexpected class type", 
                criteriaSet.contains(KeyConstraintCriteria.class));
    }
        
    /** Tests proper iterator remove() behavior when called illegally. */
    public void testIteratorRemoveIllegal() {
        criteriaSet = new ClassIndexedSet<CredentialCriteria>();
        EntityCriteria  entityCriteria = new EntityCriteria("owner", "peer");
        criteriaSet.add(entityCriteria);
        KeyConstraintCriteria  keyCriteria = new KeyConstraintCriteria("algorithm", 256);
        criteriaSet.add(keyCriteria);
        KeyInfoCriteria keyInfoCriteria = new KeyInfoCriteria(null);
        criteriaSet.add(keyInfoCriteria);
        X509CertificateCriteria x509Criteria = new X509CertificateCriteria(null, null, null, null);
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
