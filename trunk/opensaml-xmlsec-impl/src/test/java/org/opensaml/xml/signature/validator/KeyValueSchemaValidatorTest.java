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

import org.opensaml.core.xml.BaseXMLObjectValidatorTestCase;
import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.opensaml.core.xml.mock.SimpleXMLObjectBuilder;
import org.opensaml.xmlsec.signature.DSAKeyValue;
import org.opensaml.xmlsec.signature.KeyValue;
import org.opensaml.xmlsec.signature.RSAKeyValue;
import org.opensaml.xmlsec.signature.SignatureConstants;
import org.opensaml.xmlsec.signature.validator.KeyValueSchemaValidator;

/**
 *
 */
public class KeyValueSchemaValidatorTest extends BaseXMLObjectValidatorTestCase {

    public KeyValueSchemaValidatorTest() {
        targetQName = KeyValue.DEFAULT_ELEMENT_NAME;
        validator = new KeyValueSchemaValidator();
    }

    protected void populateRequiredData() {
        super.populateRequiredData();
        KeyValue keyValue = (KeyValue) target;

        keyValue.setRSAKeyValue((RSAKeyValue) buildXMLObject(RSAKeyValue.DEFAULT_ELEMENT_NAME));
    }

    public void testEmptyChildren() {
        KeyValue keyValue = (KeyValue) target;

        keyValue.setRSAKeyValue(null);

        assertValidationFail("KeyValue child list was empty, should have failed validation");
    }

    public void testTooManyChildren() {
        KeyValue keyValue = (KeyValue) target;

        keyValue.setDSAKeyValue((DSAKeyValue) buildXMLObject(DSAKeyValue.DEFAULT_ELEMENT_NAME));

        assertValidationFail("KeyValue had too many children, should have failed validation");
    }

    public void testInvalidNamespaceExtensionChild() {
        KeyValue keyValue = (KeyValue) target;

        SimpleXMLObjectBuilder sxoBuilder = new SimpleXMLObjectBuilder();
        SimpleXMLObject sxo =
                sxoBuilder.buildObject(SignatureConstants.XMLSIG_NS, "Foo", SignatureConstants.XMLSIG_PREFIX);

        keyValue.setRSAKeyValue(null);
        keyValue.setUnknownXMLObject(sxo);

        assertValidationFail("KeyInfo contained a child with an invalid namespace, should have failed validation");
    }
}
