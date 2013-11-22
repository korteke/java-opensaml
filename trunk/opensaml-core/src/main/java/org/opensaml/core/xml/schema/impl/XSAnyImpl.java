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

package org.opensaml.core.xml.schema.impl;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;

/**
 * Concrete implementation of {@link XSAny}.
 */
public class XSAnyImpl extends AbstractXMLObject implements XSAny {

    /** Child XMLObjects. */
    @Nonnull private IndexedXMLObjectChildrenList<XMLObject> unknownXMLObjects;

    /** Attributes for this element. */
    @Nonnull private AttributeMap unknownAttributes;

    /** Text content of the element. */
    @Nullable private String textContent;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected XSAnyImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);

        unknownXMLObjects = new IndexedXMLObjectChildrenList<>(this);
        unknownAttributes = new AttributeMap(this);
    }

    /** {@inheritDoc} */
    public String getTextContent() {
        return textContent;
    }

    /** {@inheritDoc} */
    public void setTextContent(@Nullable final String newContent) {
        textContent = prepareForAssignment(textContent, newContent);
    }

    /** {@inheritDoc} */
    @Nonnull public List<XMLObject> getUnknownXMLObjects() {
        return unknownXMLObjects;
    }
    
    /** {@inheritDoc} */
    @Nonnull public List<XMLObject> getUnknownXMLObjects(@Nonnull final QName typeOrName) {
        return (List<XMLObject>) unknownXMLObjects.subList(typeOrName);
    }

    /** {@inheritDoc} */
    @Nullable public List<XMLObject> getOrderedChildren() {
        return Collections.unmodifiableList(unknownXMLObjects);
    }

    /** {@inheritDoc} */
    @Nonnull public AttributeMap getUnknownAttributes() {
        return unknownAttributes;
    }
}