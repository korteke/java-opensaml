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
import java.security.KeyException;
import java.util.ArrayList;
import java.util.List;

import org.opensaml.xml.signature.KeyInfo;

/**
 * A resolver that always returns a specified set of keying information.
 */
public class DirectEncryptionKeyInfoResolver implements KeyInfoResolver{
    
    /** The  key which is wrapped by this resolver. */
    private Key key;
    
    /**
     * Constructor.
     *
     * @param newKey the encryption key to resolve
     */
    public DirectEncryptionKeyInfoResolver(Key newKey) {
       this.key = newKey; 
    }

    /** {@inheritDoc} */
    public Key resolveKey(KeyInfo keyInfo) throws KeyException {
        return this.key;
    }

    /** {@inheritDoc} */
    public List<Key> resolveKeys(KeyInfo keyInfo) throws KeyException {
        ArrayList<Key> list = new ArrayList<Key>();
        list.add(this.key);
        return list;
    }
 
    /** {@inheritDoc} */
    public List<String> resolveKeyNames(KeyInfo keyInfo) {
        return null;
    }
}