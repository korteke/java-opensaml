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
import java.util.List;

import javolution.util.FastList;

import org.opensaml.xml.signature.KeyInfo;

/**
 * Wraps a {@link KeyInfo} object as a key info source.
 */
public class WrapperKeyInfoSource implements KeyInfoSource {

    /** Name of this source. */
    private String name;
    
    /** KeyInfo to wrap as a source. */
    private List<KeyInfo> wrappedKeyInfos;
    
    /**
     * Constructor.
     *
     * @param peerName name of this key source
     * @param keyInfo keyInfo to wrap
     */
    public WrapperKeyInfoSource(String peerName, KeyInfo keyInfo){
        name = peerName;
        wrappedKeyInfos = new FastList<KeyInfo>();
        wrappedKeyInfos.add(keyInfo);
    }
    
    /**
     * Constructor.
     *
     * @param peerName name of this key source
     * @param keyInfos key infos to wrap
     */
    public WrapperKeyInfoSource(String peerName, List<KeyInfo> keyInfos){
        name = peerName;
        wrappedKeyInfos = new FastList<KeyInfo>();
        wrappedKeyInfos.addAll(keyInfos);
    }
    
    /** {@inheritDoc} */
    public Iterator<KeyInfo> iterator() {
        return wrappedKeyInfos.iterator();
    }

    /** {@inheritDoc} */
    public String getName() {
        return name;
    }
}