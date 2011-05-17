/*
 * Copyright 2011 University Corporation for Advanced Internet Development, Inc.
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
package org.opensaml.core.config.provider;

import java.io.File;
import java.util.Properties;

import org.opensaml.core.config.ConfigurationPropertiesSource;
import org.opensaml.core.config.provider.AbstractFilesystemConfigurationPropertiesSource;
import org.opensaml.core.config.provider.ClasspathConfigurationPropertiesSource;
import org.opensaml.util.FileSupport;

import junit.framework.TestCase;

/**
 * Test {@link AbstractFilesystemConfigurationPropertiesSource}.
 */
public class FilesystemConfigurationPropertiesSourceTest extends TestCase {
    
    /** The source test to test. */
    private ConfigurationPropertiesSource source;
    
    /** Master file with test data. */
    private File masterFile;
    
    /** Actual target file test runs against. */
    private File targetFile;
    
    /** Constructor. */
    public FilesystemConfigurationPropertiesSourceTest() {
        masterFile = new File("src/test/resources/opensaml-config.properties");
        targetFile = new File("/tmp/opensaml-config.properties");
        System.out.println(masterFile.getAbsolutePath());
        System.out.println(targetFile.getAbsolutePath()); 
    }
    
    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
        FileSupport.copyFile(masterFile, targetFile);
    }

    /** {@inheritDoc} */
    protected void tearDown() throws Exception {
        super.tearDown();
        if (targetFile.exists()) {
            targetFile.delete();
        }
    }
    
    /**
     *  Test basic retrieval of properties from properties source.
     */
    public void testSource() {
        source = new TestFilesystemConfigurationPropertiesSource();
        Properties props = source.getProperties();
        assertNotNull("Properties was null", props);
        
        assertEquals("Incorrect property value", "myapp", props.getProperty("opensaml.config.partitionName"));
        assertEquals("Incorrect property value", "true", props.getProperty("opensaml.initializer.foo.flag"));
    }
    
    /**
     * Test class which supplies the location of the test resource.
     */
    public class TestFilesystemConfigurationPropertiesSource 
        extends AbstractFilesystemConfigurationPropertiesSource {

        /** {@inheritDoc} */
        protected String getFilename() {
            return targetFile.getAbsolutePath();
        }
    }

}
