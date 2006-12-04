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
import java.util.NoSuchElementException;

import org.opensaml.xml.signature.KeyInfo;

/**
 * Wraps a {@link KeyInfo} object as a key info source.
 */
public class WrapperKeyInfoSource implements KeyInfoSource {

    /** Name of this source */
    private String name;
    
    /** KeyInfo to wrap as a source */
    private KeyInfo wrappedKeyInfo;
    
    /**
     * Constructor
     *
     * @param name name of this key source
     * @param keyInfo keyInfo to wrap
     */
    public WrapperKeyInfoSource(String name, KeyInfo keyInfo){
        this.name = name;
        wrappedKeyInfo = keyInfo;
    }
    
    /** {@inheritDoc} */
    public Iterator<KeyInfo> getKeyInfo() {
        return new KeyInfoIterator(wrappedKeyInfo);
    }

    /** {@inheritDoc} */
    public String getName() {
        return name;
    }
    
    /**
     * Simple iterator that "iterates" over the single wrapped key.
     */
    protected class KeyInfoIterator implements Iterator<KeyInfo>{
        
        /** KeyInfo to iterate over */
        private KeyInfo keyInfo;
        
        /** Whether the single key info has been travered */
        private boolean traversed;
        
        /**
         * Constructor
         *
         * @param keyInfo 
         */
        public KeyInfoIterator(KeyInfo keyInfo){
            this.keyInfo = keyInfo;
            traversed = false;
        }

        /** {@inheritDoc} */
        public boolean hasNext() {
            return !traversed;
        }

        /** {@inheritDoc} */
        public KeyInfo next() {
            if(!traversed){
                traversed = true;
                return keyInfo;
                
            }else{
                throw new NoSuchElementException();
            }
        }

        /** {@inheritDoc} */
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}