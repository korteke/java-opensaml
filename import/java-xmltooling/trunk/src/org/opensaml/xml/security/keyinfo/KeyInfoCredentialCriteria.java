/*
 * Copyright [2007] [University Corporation for Advanced Internet Development, Inc.]
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

package org.opensaml.xml.security.keyinfo;

import org.opensaml.xml.security.credential.CredentialCriteria;
import org.opensaml.xml.signature.KeyInfo;

/**
 * A class for representing credential resolution criteria which is based primarily
 * on the contents of a {@link KeyInfo} element.
 */
public class KeyInfoCredentialCriteria extends CredentialCriteria {
    
    /** The KeyInfo which serves as the source for credential criteria. */
    private KeyInfo keyInfo;
    
    /**
     * Constructor.
     *
     * @param newKeyInfo the KeyInfo credential criteria to use
     */
    public KeyInfoCredentialCriteria(KeyInfo newKeyInfo) {
        //TODO re-eval, this probably shouldn't be mandatory
        // If is null, KeyInfo resolver needs to use some app & context specfic logic to resolve
        /*
       if (newKeyInfo == null) {
           throw new IllegalArgumentException("KeyInfo parameter must be specified");
       }
       */
       keyInfo = newKeyInfo;
    }
    
    /**
     * Gets the KeyInfo which is the source of credential criteria.
     * 
     * @return the KeyInfo credential criteria
     */
    public KeyInfo getKeyInfo() {
        return keyInfo;
    }

}
