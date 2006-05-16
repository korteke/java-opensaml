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

package org.opensaml.xml.signature.impl;

import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.opensaml.xml.AbstractXMLObject;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.signature.KeyInfo;

/**
 * Concrete implement of {@link org.opensaml.xml.signature.KeyInfo} based on the Apache XML Security library.
 */
public class XMLSecKeyInfoImpl extends AbstractXMLObject implements KeyInfo{
    
    /** Key names within this info */
    private final ArrayList<String> keyNames;
    
    /** Keys within this info */
    private final ArrayList<PublicKey> keys;
    
    /** Certificates within this info */
    private final ArrayList<X509Certificate> certificates;
    
    /**
     * Constructor
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected XMLSecKeyInfoImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        
        keyNames = new ArrayList<String>();
        keys = new ArrayList<PublicKey>();
        certificates = new ArrayList<X509Certificate>();
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getKeyNames() {
        return keyNames;
    }

    /**
     * {@inheritDoc}
     */
    public List<PublicKey> getKeys() {
        return keys;
    }

    /**
     * {@inheritDoc}
     */
    public List<X509Certificate> getCertificates() {
        return certificates;
    }

    /**
     * {@inheritDoc}
     */
    public List<XMLObject> getOrderedChildren() {
        // No children
        return null;
    }
}