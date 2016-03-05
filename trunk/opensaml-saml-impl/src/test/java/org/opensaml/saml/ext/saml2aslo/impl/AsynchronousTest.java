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

/**
 * 
 */
package org.opensaml.saml.ext.saml2aslo.impl;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.ext.saml2aslo.Asynchronous;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests {@link Asynchronous}.
 */
public class AsynchronousTest extends XMLObjectProviderBaseTestCase {

    /**
     * Constructor.
     */
    public AsynchronousTest() {
        singleElementFile = "/org/opensaml/saml/ext/saml2aslo/impl/Asynchronous.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        Asynchronous term = (Asynchronous) unmarshallElement(singleElementFile);
        
        Assert.assertNotNull(term, "Asynchronous");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        Asynchronous term = (Asynchronous) buildXMLObject(Asynchronous.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, term);
    }
}