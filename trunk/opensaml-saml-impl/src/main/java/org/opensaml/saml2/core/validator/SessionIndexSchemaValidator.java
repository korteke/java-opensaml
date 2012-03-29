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
package org.opensaml.saml2.core.validator;

import org.opensaml.core.xml.validation.ValidationException;
import org.opensaml.core.xml.validation.Validator;
import org.opensaml.saml.saml2.core.SessionIndex;

import com.google.common.base.Strings;

/**
 * Checks {@link org.opensaml.saml.saml2.core.SessionIndex} for Schema compliance.
 */
public class SessionIndexSchemaValidator implements Validator<SessionIndex> {

    /**
     * Constructor
     *
     */
    public SessionIndexSchemaValidator() {
        super();
    }

    /** {@inheritDoc} */
    public void validate(SessionIndex sessionIndex) throws ValidationException {
        validateSessionIndex(sessionIndex);
    }

    /**
     * Validates the SessionIndex element content.
     * 
     * @param si
     * @throws ValidationException 
     */
    protected void validateSessionIndex(SessionIndex si) throws ValidationException {
        if (Strings.isNullOrEmpty(si.getSessionIndex())) {
            throw new ValidationException("SessionIndex must be non-empty");
        }
    }
}
