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

package org.opensaml.util.collections;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests the ClassIndexedSet, using local Member interface as the underlying type.
 */
public class ClassIndexedSetTest {

    /** Set to use as target for tests. */
    private ClassIndexedSet<Member> memberSet;

    @BeforeMethod
    protected void setUp() throws Exception {
        memberSet = new ClassIndexedSet<Member>();
    }

    /** Test failure of adding a duplicate instance. */
    @Test
    public void testDupInstance() {
        A memberA = new A("owner");
        memberSet.add(memberA);

        try {
            memberSet.add(memberA);
            Assert.fail("Set already contained the specified instance");
        } catch (IllegalArgumentException e) {
            // it should fail
        }
    }

    /** Test failure of adding a duplicate member type. */
    @Test
    public void testDupType() {
        A memberA1 = new A("owner");
        A memberA2 = new A("owner#2");
        memberSet.add(memberA1);

        try {
            memberSet.add(memberA2);
            Assert.fail("Set already contained an instance of the specified class");
        } catch (IllegalArgumentException e) {
            // it should fail
        }
    }

    /** Test success of adding a duplicate member type with replacement. */
    @Test
    public void testDupTypeWithReplacement() {
        A memberA1 = new A("owner");
        A memberA2 = new A("owner#2");
        memberSet.add(memberA1);

        try {
            memberSet.add(memberA2, true);
        } catch (IllegalArgumentException e) {
            Assert.fail("Set should have replaced existing member type");
        }

        Assert.assertFalse(memberA1 == memberSet.get(A.class), "Did not find the expected member instance");
        Assert.assertTrue(memberA2 == memberSet.get(A.class), "Did not find the expected member instance");

    }

    /**
     * Test getting member instance from set by type.
     */
    @Test
    public void testGetType() {
        A memberA = new A("owner");
        memberSet.add(memberA);
        B memberB = new B("algorithm");
        memberSet.add(memberB);

        Assert.assertTrue(memberA == memberSet.get(A.class), "Did not find the expected member instance");
        Assert.assertTrue(memberB == memberSet.get(B.class), "Did not find the expected member instance");
        Assert.assertTrue(null == memberSet.get(C.class), "Did not find the expected (null) member instance");
    }

    /** Tests removing member from set by instance. */
    @Test
    public void testRemove() {
        A memberA = new A("owner");
        memberSet.add(memberA);
        B memberB = new B("algorithm");
        memberSet.add(memberB);

        Assert.assertEquals(memberSet.size(), 2, "Set had unexpected size");

        memberSet.remove(memberB);
        Assert.assertEquals(memberSet.size(), 1, "Set had unexpected size");
        Assert.assertNull(memberSet.get(B.class), "Set returned removed value");

        memberSet.remove(memberA);
        Assert.assertEquals(memberSet.size(), 0, "Set had unexpected size");
        Assert.assertNull(memberSet.get(A.class), "Set returned removed value");
    }

    /** Tests clearing the set. */
    @Test
    public void testClear() {
        A memberA = new A("owner");
        memberSet.add(memberA);
        B memberB = new B("algorithm");
        memberSet.add(memberB);

        Assert.assertEquals(memberSet.size(), 2, "Set had unexpected size");

        memberSet.clear();
        Assert.assertEquals(memberSet.size(), 0, "Set had unexpected size");

        Assert.assertNull(memberSet.get(B.class), "Set returned removed value");
        Assert.assertNull(memberSet.get(A.class), "Set returned removed value");
    }

    /** Tests proper iterator iterating behavior. */
    @Test
    public void testIterator() {
        A memberA = new A("owner");
        memberSet.add(memberA);
        B memberB = new B("algorithm");
        memberSet.add(memberB);
        C memberC = new C(null);
        memberSet.add(memberC);

        Assert.assertEquals(memberSet.size(), 3, "Set had unexpected size");

        int count = 0;
        HashSet<Member> unique = new HashSet<Member>();
        for (Member member : memberSet) {
            count++;
            Assert.assertTrue(unique.add(member), "Duplicate was returned by iterator");
        }
        Assert.assertEquals(count, 3, "Set iteration had unexpected count");

        Iterator<Member> iterator = memberSet.iterator();
        Assert.assertTrue(iterator.hasNext(), "Iterator should have more elements");
        iterator.next();
        Assert.assertTrue(iterator.hasNext(), "Iterator should have more elements");
        iterator.next();
        Assert.assertTrue(iterator.hasNext(), "Iterator should have more elements");
        iterator.next();
        Assert.assertFalse(iterator.hasNext(), "Iterator should have no more elements");
        try {
            iterator.next();
            Assert.fail("Should have seen a iterator exception, no more elements available in set");
        } catch (NoSuchElementException e) {
            // do nothing, should fail
        }

    }

    /** Tests proper iterator remove() behavior. */
    @Test
    public void testIteratorRemove() {
        memberSet = new ClassIndexedSet<Member>();
        A memberA = new A("owner");
        memberSet.add(memberA);
        B memberB = new B("algorithm");
        memberSet.add(memberB);
        C memberC = new C(null);
        memberSet.add(memberC);
        D memberD = new D("128");
        memberSet.add(memberD);

        Assert.assertEquals(memberSet.size(), 4, "Set had unexpected size");

        Iterator<Member> iterator = memberSet.iterator();
        Member member = null;
        while (iterator.hasNext()) {
            member = iterator.next();
            if (member instanceof B) {
                iterator.remove();
            }
        }
        Assert.assertEquals(memberSet.size(), 3, "Set iteration had unexpected size");

        Assert.assertTrue(memberSet.contains(memberA), "Set did not contain expected instance");
        Assert.assertTrue(memberSet.contains(memberC), "Set did not contain expected instance");
        Assert.assertTrue(memberSet.contains(memberD), "Set did not contain expected instance");
        Assert.assertFalse(memberSet.contains(memberB), "Set contained unexpected instance");

        Assert.assertTrue(memberSet.contains(A.class), "Set did not contain expected class type");
        Assert.assertTrue(memberSet.contains(C.class), "Set did not contain expected class type");
        Assert.assertTrue(memberSet.contains(D.class), "Set did not contain expected class type");
        Assert.assertFalse(memberSet.contains(B.class), "Set contained unexpected class type");
    }

    /** Tests proper iterator remove() behavior when called illegally. */
    @Test
    public void testIteratorRemoveIllegal() {
        memberSet = new ClassIndexedSet<Member>();
        A memberA = new A("owner");
        memberSet.add(memberA);
        B memberB = new B("algorithm");
        memberSet.add(memberB);
        C memberC = new C(null);
        memberSet.add(memberC);
        D memberD = new D("128");
        memberSet.add(memberD);

        Assert.assertEquals(memberSet.size(), 4, "Set had unexpected size");

        Iterator<Member> iterator = memberSet.iterator();
        try {
            iterator.remove();
            Assert.fail("Should have seen a iterator exception, remove() called before first next()");
        } catch (IllegalStateException e) {
            // do nothing, should fail
        }

        iterator = memberSet.iterator();
        iterator.next();
        iterator.remove();
        try {
            iterator.remove();
            Assert.fail("Should have seen a iterator exception, remove() called twice on same element");
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
