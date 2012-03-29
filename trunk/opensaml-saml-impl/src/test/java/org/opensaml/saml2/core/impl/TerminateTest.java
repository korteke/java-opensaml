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
package org.opensaml.saml2.core.impl;

import org.opensaml.common.BaseSAMLObjectProviderTestCase;
import org.opensaml.saml.saml2.core.Terminate;

/**
 *
 */
public class TerminateTest extends BaseSAMLObjectProviderTestCase {

    /**
     * Constructor
     *
     */
    public TerminateTest() {
        singleElementFile = "/data/org/opensaml/saml2/core/impl/Terminate.xml";
    }

    /** {@inheritDoc} */
    public void testSingleElementUnmarshall() {
        Terminate term = (Terminate) unmarshallElement(singleElementFile);
        
        assertNotNull("Terminate", term);
    }

    /** {@inheritDoc} */
    public void testSingleElementMarshall() {
        Terminate term = (Terminate) buildXMLObject(Terminate.DEFAULT_ELEMENT_NAME);
        
        assertEquals(expectedDOM, term);
    }
}