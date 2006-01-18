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

package org.opensaml.saml2.core;

import org.opensaml.common.SAMLObject;

/**
 * SAML 2.0 Core AttributeValue
 */
public interface AttributeValue extends SAMLObject {
    /** Localname of the AttributeValue */
    public static final String LOCAL_NAME = "AttributeValue";

    /**
     * Gets the name of this attribute.
     * 
     * @return AttibuteValue name
     */
    public String getValue();

    /**
     * Sets the name of this value.
     * 
     * @param newValue the name of this attribute
     */
    public void setValue(String newValue);

}
