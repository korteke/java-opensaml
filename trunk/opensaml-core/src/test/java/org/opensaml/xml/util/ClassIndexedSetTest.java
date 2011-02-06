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

/**
 * Tests the ClassIndexedSet, using local Member interface as the underlying type.
 */
public class ClassIndexedSetTest extends TestCase {
    
    /** Set to use as target for tests. */
    private ClassIndexedSet<Member> memberSet;

    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
        memberSet = new ClassIndexedSet<Member>();
    }
    
    /**
     *  Test failure of adding a duplicate instance.
     */
    public void testDupInstance() {
        A  memberA = new A("owner");
        memberSet.add(memberA);
        
        try {
            memberSet.add(memberA);
            fail("Set already contained the specified instance");
        } catch (IllegalArgumentException e) {
            // it should fail
        }
    }
    
    /**
     *  Test failure of adding a duplicate member type.
     */
    public void testDupType() {
        A  memberA1 = 
            new A("owner");
        A  memberA2 = 
            new A("owner#2");
        memberSet.add(memberA1);
        
        try {
            memberSet.add(memberA2);
            fail("Set already contained an instance of the specified class");
        } catch (IllegalArgumentException e) {
            // it should fail
        }
    }
    
    /**
     *  Test success of adding a duplicate member type with replacement.
     */
    public void testDupTypeWithReplacement() {
        A  memberA1 = 
            new A("owner");
        A  memberA2 = 
            new A("owner#2");
        memberSet.add(memberA1);
        
        try {
            memberSet.add(memberA2, true);
        } catch (IllegalArgumentException e) {
            fail("Set should have replaced existing member type");
        }
        
        assertFalse("Did not find the expected member instance",
                memberA1 == memberSet.get(A.class) );
        assertTrue("Did not find the expected member instance",
                memberA2 == memberSet.get(A.class) );
        
    }
    
    /**
     *  Test getting member instance from set by type.
     */
    public void testGetType() {
        A  memberA = new A("owner");
        memberSet.add(memberA);
        B  memberB = new B("algorithm");
        memberSet.add(memberB);
        
        assertTrue("Did not find the expected member instance",
                memberA == memberSet.get(A.class) );
        assertTrue("Did not find the expected member instance",
                memberB == memberSet.get(B.class) );
        assertTrue("Did not find the expected (null) member instance",
                null == memberSet.get(C.class) );
    }
    
    /** Tests removing member from set by instance. */
    public void testRemove() {
        A  memberA = new A("owner");
        memberSet.add(memberA);
        B  memberB = new B("algorithm");
        memberSet.add(memberB);
        
        assertEquals("Set had unexpected size", 2, memberSet.size());
        
        memberSet.remove(memberB);
        assertEquals("Set had unexpected size", 1, memberSet.size());
        assertNull("Set returned removed value", memberSet.get(B.class));
        
        memberSet.remove(memberA);
        assertEquals("Set had unexpected size", 0, memberSet.size());
        assertNull("Set returned removed value", memberSet.get(A.class));
    }
    
    /** Tests clearing the set. */
    public void testClear() {
        A  memberA = new A("owner");
        memberSet.add(memberA);
        B  memberB = new B("algorithm");
        memberSet.add(memberB);
        
        assertEquals("Set had unexpected size", 2, memberSet.size());
        
        memberSet.clear();
        assertEquals("Set had unexpected size", 0, memberSet.size());
        
        assertNull("Set returned removed value", memberSet.get(B.class));
        assertNull("Set returned removed value", memberSet.get(A.class));
    }
    
    /** Tests proper iterator iterating behavior. */
    public void testIterator() {
        A  memberA = new A("owner");
        memberSet.add(memberA);
        B  memberB = new B("algorithm");
        memberSet.add(memberB);
        C memberC = new C(null);
        memberSet.add(memberC);
        
        assertEquals("Set had unexpected size", 3, memberSet.size());
        
        int count = 0;
        HashSet<Member> unique = new HashSet<Member>();
        for ( Member member : memberSet) {
            count++;
            assertTrue("Duplicate was returned by iterator", unique.add(member));
        }
        assertEquals("Set iteration had unexpected count", 3, count);
        
        Iterator<Member> iterator = memberSet.iterator();
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
        memberSet = new ClassIndexedSet<Member>();
        A  memberA = new A("owner");
        memberSet.add(memberA);
        B  memberB = new B("algorithm");
        memberSet.add(memberB);
        C memberC = new C(null);
        memberSet.add(memberC);
        D memberD = new D("128");
        memberSet.add(memberD);
        
        assertEquals("Set had unexpected size", 4, memberSet.size());
        
        Iterator<Member> iterator = memberSet.iterator();
        Member member = null;
        while ( iterator.hasNext() ) {
            member = iterator.next();
            if (member instanceof B) {
                iterator.remove();
            }
        }
        assertEquals("Set iteration had unexpected size", 3, memberSet.size());
        
        assertTrue("Set did not contain expected instance", memberSet.contains(memberA));
        assertTrue("Set did not contain expected instance", memberSet.contains(memberC));
        assertTrue("Set did not contain expected instance", memberSet.contains(memberD));
        assertFalse("Set contained unexpected instance", memberSet.contains(memberB));
        
        assertTrue("Set did not contain expected class type", 
                memberSet.contains(A.class));
        assertTrue("Set did not contain expected class type", 
                memberSet.contains(C.class));
        assertTrue("Set did not contain expected class type", 
                memberSet.contains(D.class));
        assertFalse("Set contained unexpected class type", 
                memberSet.contains(B.class));
    }
        
    /** Tests proper iterator remove() behavior when called illegally. */
    public void testIteratorRemoveIllegal() {
        memberSet = new ClassIndexedSet<Member>();
        A  memberA = new A("owner");
        memberSet.add(memberA);
        B  memberB = new B("algorithm");
        memberSet.add(memberB);
        C memberC = new C(null);
        memberSet.add(memberC);
        D memberD = new D("128");
        memberSet.add(memberD);
        
        assertEquals("Set had unexpected size", 4, memberSet.size());
        
        Iterator<Member> iterator = memberSet.iterator();
        try {
            iterator.remove();
            fail("Should have seen a iterator exception, remove() called before first next()");
        } catch (IllegalStateException e) {
            // do nothing, should fail
        }
        
        iterator = memberSet.iterator();
        iterator.next();
        iterator.remove();
        try {
            iterator.remove();
            fail("Should have seen a iterator exception, remove() called twice on same element");
        } catch (IllegalStateException e) {
            // do nothing, should fail
        }
    }
    
    
    /* Classes used for testing. */
    
    private interface Member {
       public String getData(); 
    }
    
    private abstract class AbstractMember implements Member {
        private String data;
        
        public AbstractMember(String newData) {
            data = newData;
        }
        
        public String getData() {
            return data;
        }
    }
    
    private class A extends AbstractMember {

        /**
         * Constructor.
         *
         * @param newData
         */
        public A(String newData) {
            super(newData);
        }
        
    }
    
    private class B extends AbstractMember {

        /**
         * Constructor.
         *
         * @param newData
         */
        public B(String newData) {
            super(newData);
        }
        
    }
    
    private class C extends AbstractMember {

        /**
         * Constructor.
         *
         * @param newData
         */
        public C(String newData) {
            super(newData);
        }
        
    }
    
    private class D extends AbstractMember {

        /**
         * Constructor.
         *
         * @param newData
         */
        public D(String newData) {
            super(newData);
        }
        
    }

}
