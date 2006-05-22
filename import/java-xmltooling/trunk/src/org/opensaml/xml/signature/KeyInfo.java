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

package org.opensaml.xml.signature;

import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.opensaml.xml.AbstractDOMCachingXMLObject;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.util.XMLConstants;

/**
 * XMLObject representing XML Digital Signature, version 20020212, KeyInfo element.
 * 
 * Note that this does not support every possible key information type, only the ones most commonly used.
 */
public class KeyInfo extends AbstractDOMCachingXMLObject {
    
    /** Element local name */
    public static String DEFAULT_ELEMENT_LOCAL_NAME = "KeyInfo";
    
    /** Default element name */
    public final static QName DEFAULT_ELEMENT_NAME = new QName(XMLConstants.XMLSIG_NS, DEFAULT_ELEMENT_LOCAL_NAME, XMLConstants.XMLSIG_PREFIX);
    
    /** Local name of the XSI type */
    public final static String TYPE_LOCAL_NAME = "KeyInfoType"; 
        
    /** QName of the XSI type */
    public final static QName TYPE_NAME = new QName(XMLConstants.XMLSIG_NS, TYPE_LOCAL_NAME, XMLConstants.XMLSIG_PREFIX);
    
    /** Key names within this info */
    private final ArrayList<String> keyNames;
    
    /** Keys within this info */
    private PublicKey publicKey;
    
    /** Certificates within this info */
    private final ArrayList<X509Certificate> certificates;
    
    /**
     * Constructor
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected KeyInfo(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        
        keyNames = new ArrayList<String>();
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
    public PublicKey getPublicKey() {
        return publicKey;
    }
    
    public void setPublicKey(PublicKey newKey){
        publicKey = newKey;
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