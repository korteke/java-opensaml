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

package org.opensaml.xmlsec.encryption.validator;

import org.opensaml.core.xml.BaseXMLObjectValidatorTestCase;
import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.opensaml.core.xml.mock.SimpleXMLObjectBuilder;
import org.opensaml.xmlsec.encryption.DataReference;
import org.opensaml.xmlsec.encryption.ReferenceType;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.encryption.validator.ReferenceTypeSchemaValidator;

/**
 *
 */
public class ReferenceTypeSchemaValidatorTest extends BaseXMLObjectValidatorTestCase {

    public ReferenceTypeSchemaValidatorTest() {
        // Don't want to create a builder just for this test,
        // so just use DataReference to test, it doesn't add anything.
        targetQName = DataReference.DEFAULT_ELEMENT_NAME;
        validator = new ReferenceTypeSchemaValidator();
    }

    protected void populateRequiredData() {
        super.populateRequiredData();
        ReferenceType ref = (ReferenceType) target;

        ref.setURI("urn:string:foo");
    }

    public void testMissingURI() {
        ReferenceType ref = (ReferenceType) target;

        ref.setURI(null);
        assertValidationFail("ReferenceType URI was null, should have failed validation");

        ref.setURI("");
        assertValidationFail("ReferenceType URI was empty, should have failed validation");

        ref.setURI("       ");
        assertValidationFail("ReferenceType URI was all whitespace, should have failed validation");
    }

    public void testInvalidNamespaceChildren() {
        ReferenceType rt = (ReferenceType) target;

        SimpleXMLObjectBuilder sxoBuilder = new SimpleXMLObjectBuilder();
        SimpleXMLObject sxo =
                sxoBuilder.buildObject(EncryptionConstants.XMLENC_NS, "Foo", EncryptionConstants.XMLENC_PREFIX);

        rt.getUnknownXMLObjects().add(sxo);

        assertValidationFail("ReferenceType contained a child with an invalid namespace, should have failed validation");
    }

}
