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

import java.util.Iterator;

import org.opensaml.xml.signature.KeyInfo;

/**
 * A source for key information.  Implementations may collect the information from any 
 * source and may do so in a lazy manner.
 */
public interface KeyInfoSource {

    /**
     * Returns the name of this source of keys, for example a peer entity name
     * or a principal's name.
     * 
     * @return  name of key source, or empty string
     */
    public String getName();
    
    /**
     * Provides access to the KeyInfo information associated with the source.
     * The caller must free the returned interface when finished with it.
     * 
     * The returned Iterator does not support the remove operation.
     * 
     * @return iterator over key info elements
     */
    public Iterator<KeyInfo> getKeyInfo();
}