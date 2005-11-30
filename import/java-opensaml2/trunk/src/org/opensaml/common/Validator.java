/*
 * Copyright [2005] [University Corporation for Advanced Internet Development, Inc.]
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

package org.opensaml.common;

import java.io.Serializable;

/**
 * An interface for classes that implement rules for checking the 
 * validity of a SAML element.
 */
public interface Validator extends Serializable{

    /**
     * Checks to see if a SAML element is valid.
     * 
     * @param element the SAML element
     * 
     * @throws ValidationException thrown if the element is not valid
     */
    public void validate(SAMLObject element) throws ValidationException;
}
