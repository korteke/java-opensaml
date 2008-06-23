/*
 * Copyright 2008 Members of the EGEE Collaboration.
 * Copyright 2008 University Corporation for Advanced Internet Development, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.ws.wstrust.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opensaml.ws.wstrust.BinarySecret;
import org.opensaml.ws.wstrust.Entropy;
import org.opensaml.xml.AbstractExtensibleXMLObject;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.encryption.EncryptedKey;

/**
 * EntropyImpl
 * 
 */
public class EntropyImpl extends AbstractExtensibleXMLObject implements Entropy {

    /** the &lt;wst:BinarySecret&gt; child element */
    private BinarySecret binarySecret_ = null;

    /** the &lt;xenc:EncryptedKey&gt; child element */
    private EncryptedKey encryptedKey_ = null;

    /**
     * Constructor.
     * 
     * @param namespaceURI The namespace of the element
     * @param elementLocalName The local name of the element
     * @param namespacePrefix The namespace prefix of the element
     */
    public EntropyImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.Entropy#getBinarySecret()
     */
    public BinarySecret getBinarySecret() {
        return binarySecret_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.Entropy#getEncryptedKey()
     */
    public EncryptedKey getEncryptedKey() {
        return encryptedKey_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.Entropy#setBinarySecret(org.opensaml.ws.wstrust.BinarySecret)
     */
    public void setBinarySecret(BinarySecret binarySecret) {
        binarySecret_ = prepareForAssignment(binarySecret_, binarySecret);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.Entropy#setEncryptedKey(org.opensaml.xml.encryption.EncryptedKey)
     */
    public void setEncryptedKey(EncryptedKey encryptedKey) {
        encryptedKey_ = prepareForAssignment(encryptedKey_, encryptedKey);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.impl.AbstractWSTrustObject#getOrderedChildren()
     */
    @Override
    public List<XMLObject> getOrderedChildren() {
        List<XMLObject> children = getChildren();
        // xs:any elements
        if (!getUnknownXMLObjects().isEmpty()) {
            children.addAll(getUnknownXMLObjects());
        }
        return Collections.unmodifiableList(children);
    }

    /**
     * Returns a modifiable list of children elements.
     * 
     * @return list of children elements.
     */
    protected List<XMLObject> getChildren() {
        List<XMLObject> children = new ArrayList<XMLObject>();
        if (encryptedKey_ != null) {
            children.add(encryptedKey_);
        }
        if (binarySecret_ != null) {
            children.add(binarySecret_);
        }
        return children;
    }
}
