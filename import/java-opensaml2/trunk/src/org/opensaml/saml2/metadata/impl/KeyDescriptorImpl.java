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

package org.opensaml.saml2.metadata.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opensaml.common.impl.AbstractSAMLObject;
import org.opensaml.saml2.metadata.EncryptionMethod;
import org.opensaml.saml2.metadata.KeyDescriptor;
import org.opensaml.security.CredentialUsageTypeEnumeration;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.util.XMLObjectChildrenList;

/**
 * Concrete implementation of {@link org.opensaml.saml2.metadata.KeyDescriptor}.
 */
public class KeyDescriptorImpl extends AbstractSAMLObject implements KeyDescriptor {

    /** Key usage type */
    private CredentialUsageTypeEnumeration keyUseType;

    /** Key information */
    private KeyInfo keyInfo;

    /** Encryption methods supported by the entity */
    private final XMLObjectChildrenList<EncryptionMethod> encryptionMethods;

    /**
     * Constructor
     * 
     * @param namespaceURI
     * @param elementLocalName
     * @param namespacePrefix
     */
    protected KeyDescriptorImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        encryptionMethods = new XMLObjectChildrenList<EncryptionMethod>(this);
    }

    /** {@inheritDoc} */
    public CredentialUsageTypeEnumeration getUse() {
        return keyUseType;
    }

    /** {@inheritDoc} */
    public void setUse(CredentialUsageTypeEnumeration newType) {
        keyUseType = prepareForAssignment(keyUseType, newType);
    }

    /** {@inheritDoc} */
    public KeyInfo getKeyInfo() {
        return keyInfo;
    }

    /** {@inheritDoc} */
    public void setKeyInfo(KeyInfo newKeyInfo) {
        keyInfo = prepareForAssignment(keyInfo, newKeyInfo);
    }

    /** {@inheritDoc} */
    public List<EncryptionMethod> getEncryptionMethods() {
        return encryptionMethods;
    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        ArrayList<XMLObject> children = new ArrayList<XMLObject>();

        children.add(keyInfo);
        children.addAll(encryptionMethods);

        return Collections.unmodifiableList(children);
    }
}