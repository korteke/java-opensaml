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

package org.opensaml.xml.encryption.validator;

import org.opensaml.xml.encryption.EncryptionProperties;
import org.opensaml.xml.validation.ValidationException;
import org.opensaml.xml.validation.Validator;

/**
 * Checks {@link org.opensaml.xml.encryption.EncryptionProperties} for Schema compliance. 
 */
public class EncryptionPropertiesSchemaValidator implements Validator<EncryptionProperties> {

    /** {@inheritDoc} */
    public void validate(EncryptionProperties xmlObject) throws ValidationException {
        validateTransforms(xmlObject);
    }

    /**
     * Validate the encryption properties list.
     * 
     * @param xmlObject the object to validate
     * @throws ValidationException  thrown if the object is invalid
     */
    protected void validateTransforms(EncryptionProperties xmlObject) throws ValidationException {
        if (xmlObject.getEncryptionProperties().isEmpty()) {
            throw new ValidationException("No EncryptionProperty children were present in the EncryptionProperties object");
        }
    }

}
