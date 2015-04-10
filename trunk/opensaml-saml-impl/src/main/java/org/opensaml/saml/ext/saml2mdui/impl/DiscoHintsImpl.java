/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.saml.ext.saml2mdui.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.saml.common.AbstractSAMLObject;
import org.opensaml.saml.ext.saml2mdui.DiscoHints;
import org.opensaml.saml.ext.saml2mdui.DomainHint;
import org.opensaml.saml.ext.saml2mdui.GeolocationHint;
import org.opensaml.saml.ext.saml2mdui.IPHint;

/** Concrete implementation of {@link org.opensaml.saml.ext.saml2mdui.DiscoHints}. */
public class DiscoHintsImpl extends AbstractSAMLObject implements DiscoHints {
    
    /** Children of the UIInfo. */
    private final IndexedXMLObjectChildrenList<XMLObject> discoHintsChildren;

    /**
     * Constructor.
     * 
     * @param namespaceURI namespaceURI
     * @param elementLocalName elementLocalName
     * @param namespacePrefix namespacePrefix
     */
    protected DiscoHintsImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        discoHintsChildren = new IndexedXMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Override
    public List<XMLObject> getXMLObjects() {
        return discoHintsChildren;
    }

    /** {@inheritDoc} */
    @Override
    public List<XMLObject> getXMLObjects(QName typeOrName) {
        return (List<XMLObject>) discoHintsChildren.subList(typeOrName);
    }

    /** {@inheritDoc} */
    @Override
    public List<DomainHint> getDomainHints() {
        return (List<DomainHint>) discoHintsChildren.subList(DomainHint.DEFAULT_ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Override
    public List<GeolocationHint> getGeolocationHints() {
        return (List<GeolocationHint>) discoHintsChildren.subList(GeolocationHint.DEFAULT_ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Override
    public List<IPHint> getIPHints() {
        return (List<IPHint>) discoHintsChildren.subList(IPHint.DEFAULT_ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Override
    public List<XMLObject> getOrderedChildren() {
        ArrayList<XMLObject> children = new ArrayList<>();

        children.addAll(discoHintsChildren);
        return children;
    }
}
