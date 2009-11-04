/*
 * Copyright 2008 University Corporation for Advanced Internet Development, Inc.
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

package org.opensaml.util.resource;

import java.io.File;
import java.io.InputStream;

import junit.framework.TestCase;

import org.opensaml.xml.util.DatatypeHelper;

/** Unit test for {@link PropertyReplacementResourceFilter}. */
public class PropertyReplacementResourceFilterTest extends TestCase {

    /** Path to property file used to expand macros. */
    private final String propsFilePath = "/data/org/opensaml/util/resource/replacementFilterProperties.properties";

    /** Path to test file whose macros will be expanded. */
    private final String testFilePath = "/data/org/opensaml/util/resource/replacementFilterTest.txt";

    /**
     * Tests the application of the {@link PropertyReplacementResourceFilter}.
     * 
     * @throws Exception throw if there is a problem with the filtering
     */
    public void testFilter() throws Exception {
        File propsFile = new File(PropertyReplacementResourceFilterTest.class.getResource(propsFilePath).toURI());
        PropertyReplacementResourceFilter filter = new PropertyReplacementResourceFilter(propsFile);

        InputStream resultIns = filter.applyFilter(PropertyReplacementResourceFilterTest.class
                .getResourceAsStream(testFilePath));
        String result = DatatypeHelper.inputstreamToString(resultIns, null);

        assertEquals("value1\nvalue2\nvalue1\n${key3}", result.trim());
    }
}