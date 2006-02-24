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

/**
 * 
 */
package org.opensaml.saml2.core;


/**
 * SAML 2.0 Core NameIDMappingRequest
 */
public interface NameIDMappingRequest extends Request {
    
    /** Element local name */
    public static final String LOCAL_NAME = "NameIDMappingRequest";
    
    /**
     * Get the Identifier of the request
     * 
     * @return the Identifier of the request
     */
    public Identifier getIdentifier();

    /**
     * Set the Identifier of the request
     * 
     * @param newIdentifier the new Identifier of the request
     */
    public void setIdentifier(Identifier newIdentifier);
    
    /**
     * Get the NameIDPolicy of the request
     * 
     * @return the NameIDPolicy of the request
     */
    public NameIDPolicy getNameIDPolicy();

    /**
     * Set the NameIDPolicy of the request
     * 
     * @param newNameIDPolicy the new NameIDPolicy of the request
     */
    public void setNameIDPolicy(NameIDPolicy newNameIDPolicy);


}
