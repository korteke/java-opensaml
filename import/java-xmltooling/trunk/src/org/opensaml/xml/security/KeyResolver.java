/*
 * Copyright [2006] [University Corporation for Advanced Internet Development, Inc.]
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

package org.opensaml.xml.security;

import java.security.Key;
import java.util.List;

import org.opensaml.xml.signature.KeyInfo;

/**
 * Resolves information about a key into the referenced key.
 */
public interface KeyResolver {

    /**
     * A convience method for getting the primary key referenced by a KeyInfo.  If a KeyInfo element 
     * references more than one key the determination of which is the primary key is completely implementation 
     * dependent.
     * 
     * @param keyInfo the key information
     * 
     * @return the primary key referenced
     */
    public Key resolveKey(KeyInfo keyInfo);
    
    /**
     * Gets all the keys referenced by the given KeyInfo.
     * 
     * @param keyInfo key information
     * 
     * @return keys referenced by the given KeyInfo
     */
    public List<Key> resolveKeys(KeyInfo keyInfo);
}