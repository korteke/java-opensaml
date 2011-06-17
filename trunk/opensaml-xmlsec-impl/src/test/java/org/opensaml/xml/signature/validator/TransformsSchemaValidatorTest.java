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

package org.opensaml.xml.signature.validator;

import org.opensaml.xml.BaseXMLObjectValidatorTestCase;
import org.opensaml.xml.signature.Transform;
import org.opensaml.xml.signature.Transforms;

/**
 *
 */
public class TransformsSchemaValidatorTest extends BaseXMLObjectValidatorTestCase {
    
    public TransformsSchemaValidatorTest() {
        targetQName = Transforms.DEFAULT_ELEMENT_NAME;
        validator = new TransformsSchemaValidator();
    }

    protected void populateRequiredData() {
        super.populateRequiredData();
        Transforms transforms = (Transforms) target;
        
        transforms.getTransforms().add((Transform) buildXMLObject(Transform.DEFAULT_ELEMENT_NAME));
    }
    
    public void testEmptyTransforms() {
        Transforms transforms = (Transforms) target;
        
        transforms.getTransforms().clear();
        assertValidationFail("Transform child list was empty, should have failed validation");
    }

}
