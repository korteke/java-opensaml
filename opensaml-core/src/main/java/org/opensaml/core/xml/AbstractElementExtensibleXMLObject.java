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

package org.opensaml.core.xml;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;

/**
 * AbstractElementExtensible is an element of type <code>xs:any</code>, but without <code>xs:anyAttribute</code>
 * attribute or text content.
 */
public abstract class AbstractElementExtensibleXMLObject extends AbstractXMLObject implements
        ElementExtensibleXMLObject {

    /** xs:any {@link XMLObject} child elements. */
    private final IndexedXMLObjectChildrenList<XMLObject> anyXMLObjects;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    public AbstractElementExtensibleXMLObject(@Nullable final String namespaceURI,
            @Nonnull final String elementLocalName, @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        anyXMLObjects = new IndexedXMLObjectChildrenList<XMLObject>(this);
    }

    /** {@inheritDoc} */
    @Nullable public List<XMLObject> getOrderedChildren() {
        return Collections.unmodifiableList(anyXMLObjects);
    }

    /** {@inheritDoc} */
    @Nullable public List<XMLObject> getUnknownXMLObjects() {
        return anyXMLObjects;
    }

    /** {@inheritDoc} */
    @Nullable public List<XMLObject> getUnknownXMLObjects(@Nonnull final QName typeOrName) {
        return (List<XMLObject>) anyXMLObjects.subList(typeOrName);
    }
}