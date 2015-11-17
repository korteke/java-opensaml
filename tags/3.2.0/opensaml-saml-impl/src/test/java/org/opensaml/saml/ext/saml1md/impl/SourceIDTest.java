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

package org.opensaml.saml.ext.saml1md.impl;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.ext.saml1md.SourceID;
import org.opensaml.saml.ext.saml1md.impl.SourceIDBuilder;
import org.opensaml.saml.ext.saml1md.impl.SourceIDImpl;

/**
 * Tests {@link SourceIDImpl}
 */
public class SourceIDTest extends XMLObjectProviderBaseTestCase {

    /** Expected source ID value */
    private String expectedValue;

    /** Constructor */
    public SourceIDTest() {
        singleElementFile = "/data/org/opensaml/saml/ext/saml1md/impl/SourceID.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedValue = "9392kjc98";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        SourceIDBuilder builder = (SourceIDBuilder) builderFactory.getBuilder(SourceID.DEFAULT_ELEMENT_NAME);

        SourceID sourceID = builder.buildObject();
        sourceID.setValue(expectedValue);

        assertXMLEquals(expectedDOM, sourceID);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        SourceID sourceID = (SourceID) unmarshallElement(singleElementFile);

        Assert.assertNotNull(sourceID);
        Assert.assertEquals(sourceID.getValue(), expectedValue);
    }
}