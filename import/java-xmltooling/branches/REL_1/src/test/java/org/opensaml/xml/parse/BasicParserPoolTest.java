/*
 * Copyright 2009 University Corporation for Advanced Internet Development, Inc.
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

package org.opensaml.xml.parse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;

import org.opensaml.xml.parse.BasicParserPool.DocumentBuilderProxy;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

/**
 * Test the basic parser pool implementation.
 */
public class BasicParserPoolTest extends TestCase {
    
    /** Pool instance to test. */
    private BasicParserPool pool;

    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
        
        pool = new BasicParserPool();
    }
    
    /**
     * Test issue reported in JXT-46 - a parser should not be checked into the pool multiple times
     * via the auto-checkin mechanism by the proxy finalize().
     * 
     * @throws XMLParserException 
     * @throws InterruptedException 
     */
    public void testFinalize() throws XMLParserException {
        
        assertEquals(0, pool.getPoolSize());
        
        // Check out and return a builder
        DocumentBuilder builder = pool.getBuilder();
        pool.returnBuilder(builder);
        
        assertEquals(1, pool.getPoolSize());
        
        // Get rid of any references to the first builder we got, so that it will be GCed
        //builder = null;
        // Do explicit GC and sleep a little make sure proxy finalize() gets called
        //System.out.println("Garbage collection and sleep");
        //System.gc();
        //Thread.sleep(3000);
        //System.out.println("Done sleeping");
        
        // Rather than relying on forcing GC behavior in the test as above, which was in initial debugging this problem,
        // explicitly invoke finalize() to simulate.
        // (which we can do b/c it's protected access *and* we're in the same package)
        try {
            ((DocumentBuilderProxy)builder).finalize();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        
        assertEquals(1, pool.getPoolSize());
        
        
        // Both of these would have been the same instance pre-bug fix.
        DocumentBuilder builder1 = ((DocumentBuilderProxy) pool.getBuilder()).getProxiedBuilder();
        assertNotNull(builder1);
        DocumentBuilder builder2 = ((DocumentBuilderProxy) pool.getBuilder()).getProxiedBuilder();
        assertNotNull(builder2);
        assertFalse(builder1.equals(builder2));
        
    }
    
    
    /**
     * Test for caller (illegally) returning a builder multiple times to pool.
     * 
     * @throws XMLParserException
     */
    public void testExplicitMultipleReturn() throws XMLParserException {
        assertEquals(0, pool.getPoolSize());
        
        // Check out and return a builder
        DocumentBuilder builder = pool.getBuilder();
        
        pool.returnBuilder(builder);
        assertEquals(1, pool.getPoolSize());
        
        // This isn't legal to do, but should be silently detected and ignored
        pool.returnBuilder(builder);
        assertEquals(1, pool.getPoolSize());
        
        DocumentBuilder builder1 = ((DocumentBuilderProxy) pool.getBuilder()).getProxiedBuilder();
        assertNotNull(builder1);
        DocumentBuilder builder2 = ((DocumentBuilderProxy) pool.getBuilder()).getProxiedBuilder();
        assertNotNull(builder2);
        assertFalse(builder1.equals(builder2));
        
    }
    
    /**
     * Test for a caller illegally using a parser proxy after it has been returned. 
     * 
     * @throws XMLParserException 
     * @throws URISyntaxException 
     * @throws IOException 
     * 
     */
    public void testParserUseAfterReturn() throws XMLParserException, URISyntaxException {
        String testPath = "/data/org/opensaml/xml/parse/foo.xml";
        InputStream is = BasicParserPoolTest.class.getResourceAsStream(testPath);
        File file = new File(this.getClass().getResource(testPath).toURI());
        
        // Check out and return a builder
        DocumentBuilder builder = pool.getBuilder();
        
        try {
            builder.parse(file);
        } catch (IllegalStateException e) {
            fail("Parser proxy was in a valid state");
        } catch (SAXException e) {
            fail("Parser proxy was in a valid state");
        } catch (IOException e) {
            fail("Parser proxy was in a valid state");
        }
        
        pool.returnBuilder(builder);
        
        
        try {
            builder.parse(file);
            fail("Parser proxy was in an illegal state");
        } catch (IllegalStateException e) {
            // do nothing, expected
        } catch (SAXException e) {
            fail("Parser proxy was in an illegal state");
        } catch (IOException e) {
            fail("Parser proxy was in an illegal state");
        }
        
        try {
            builder.parse(is);
            fail("Parser proxy was in an illegal state");
        } catch (IllegalStateException e) {
            // do nothing, expected
        } catch (SAXException e) {
            fail("Parser proxy was in an illegal state");
        } catch (IOException e) {
            fail("Parser proxy was in an illegal state");
        }
        
        try {
            builder.parse(new InputSource(is));
            fail("Parser proxy was in an illegal state");
        } catch (IllegalStateException e) {
            // do nothing, expected
        } catch (SAXException e) {
            fail("Parser proxy was in an illegal state");
        } catch (IOException e) {
            fail("Parser proxy was in an illegal state");
        }
        
        try {
            builder.parse(file.toURI().toString());
            fail("Parser proxy was in an illegal state");
        } catch (IllegalStateException e) {
            // do nothing, expected
        } catch (SAXException e) {
            fail("Parser proxy was in an illegal state");
        } catch (IOException e) {
            fail("Parser proxy was in an illegal state");
        }
        
    }
    
    /**
     * Test that only maxPoolSize parsers are ever cached.
     * 
     * @throws XMLParserException
     */
    public void testMaxPoolSize() throws XMLParserException {
        int poolSize = 5;
        
        pool.setMaxPoolSize(poolSize);
        
        assertEquals(0, pool.getPoolSize());
        
        ArrayList<DocumentBuilder> list = new ArrayList<DocumentBuilder>();
        
        // Get 3x the maxPoolSize number of builders
        for (int i=0; i < 3*poolSize; i++) {
            list.add(pool.getBuilder());
        }
        
        assertEquals(0, pool.getPoolSize());
        
        for (DocumentBuilder b : list) {
           pool.returnBuilder(b); 
        }
        
        // Even though we return 3*maxPoolSize builders, only maxPoolSize should be cached
        assertEquals(poolSize, pool.getPoolSize());
    }
    
    
    /**
     * Test that pool versioning is working properly.
     * 
     * @throws XMLParserException
     */
    public void testPoolVersioning() throws XMLParserException {
        DocumentBuilderProxy builder = null;
        
        pool.setMaxPoolSize(10);
        
        assertEquals(0, pool.getPoolSize());
        
        // Check basic version reporting
        builder = (DocumentBuilderProxy) pool.getBuilder();
        assertEquals(builder.getPoolVersion(), pool.getPoolVersion());
        long firstVersion = pool.getPoolVersion();
        pool.returnBuilder(builder);
        
        assertEquals(1, pool.getPoolSize());
        
        // Populate cache with a few more builders
        ArrayList<DocumentBuilder> list = new ArrayList<DocumentBuilder>();
        for (int i=0; i < 5; i++) {
            builder = (DocumentBuilderProxy) pool.getBuilder();
            assertEquals(firstVersion, builder.getPoolVersion());
            list.add(builder);
        }
        assertEquals(0, pool.getPoolSize());
        for (DocumentBuilder b : list) {
           pool.returnBuilder(b); 
        }
        assertEquals(5, pool.getPoolSize());
        
        // Get a version 1 builder
        DocumentBuilderProxy builderV1 = (DocumentBuilderProxy) pool.getBuilder();
        assertEquals(firstVersion, builderV1.getPoolVersion());
        
        assertEquals(4, pool.getPoolSize());
        
        // Cause the pool to be dirty, increment the version number
        pool.setNamespaceAware(!pool.isNamespaceAware());
        builder = (DocumentBuilderProxy) pool.getBuilder();
        assertEquals(builder.getPoolVersion(), pool.getPoolVersion());
        long secondVersion = pool.getPoolVersion();
        pool.returnBuilder(builder);
        
        // Make sure version incremented
        assertEquals(firstVersion+1, secondVersion);
        
        // This is the only cached builder, a V2
        assertEquals(1, pool.getPoolSize());
        
        // Now try and check-in the V1 builder.  It should be rejected.
        pool.returnBuilder(builderV1);
        // Should be 1 rather than 2, indicating it was not added.
        assertEquals(1, pool.getPoolSize());
        
    }
    
    


}
