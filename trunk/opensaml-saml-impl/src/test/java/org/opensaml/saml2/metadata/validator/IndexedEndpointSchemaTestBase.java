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

package org.opensaml.saml2.metadata.validator;

import org.opensaml.core.xml.validation.ValidationException;
import org.opensaml.saml.saml2.metadata.IndexedEndpoint;

/**
 * Test case for {@link org.opensaml.saml.saml2.metadata.AddtionalMetadataLocation}.
 */
public abstract class IndexedEndpointSchemaTestBase extends EndpointSchemaTestBase {

    /** Constructor */
    public IndexedEndpointSchemaTestBase() {

    }

    /** {@inheritDoc} */
    protected void populateRequiredData() {
        super.populateRequiredData();
        IndexedEndpoint indexedEndpoint = (IndexedEndpoint) target;
        
        indexedEndpoint.setIndex(4);
    }

    /**
     * Tests for improper index.
     * 
     * @throws ValidationException
     */
    public void testIndexFailure() throws ValidationException {
        IndexedEndpoint indexedEndpoint = (IndexedEndpoint) target;

        indexedEndpoint.setIndex(-3);
        assertValidationFail("Index was negative, should raise a Validation Exception.");
    }
}