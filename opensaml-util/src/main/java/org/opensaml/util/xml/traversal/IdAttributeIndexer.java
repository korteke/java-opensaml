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

package org.opensaml.util.xml.traversal;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.opensaml.util.constraint.documented.NoNullElements;
import org.opensaml.util.constraint.documented.NotNull;
import org.opensaml.util.constraint.documented.Unmodifiable;
import org.opensaml.util.xml.QNameSupport;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This {@link NodeVisitor} performs two tasks. One it creates an index, stored as use data on the {@link Document}
 * node, that maps an ID to the element that carries that ID. Second, it ensure thats all IDs in the document are
 * unique.
 * 
 * In addition to attributes which are known to be ID attributes (i.e., {@link Attr#isId()} returns true) this visitor
 * can treat other attributes as ID attributes. This is helpful if the content model of the document is known ahead of
 * time and schema validation is not used (and thus ID attributes are not marked as such).
 * 
 * This visitor only works if the first node in the traversal is either a {@link Document} or an {@link Element} that is
 * the document element. If more than one element has the same ID a {@link DuplicateIdException} is thrown.
 */
public class IdAttributeIndexer implements NodeVisitor {

    /** The name under which the ID index will be stored as {@link Document} user data. */
    private static final String USER_DATA_NAME = IdAttributeIndexer.class.getPackage().getName() + ".IdIndex";

    /** Name of attributes that should be considered ID attribute, regardless of whether {@link Attr#isId()} is true. */
    private Set<QName> idAttributes;

    /** Constructor. */
    public IdAttributeIndexer() {
        idAttributes = Collections.emptySet();
    }

    /**
     * Gets attributes considered ID attributes.
     * 
     * @return attributes considered ID attributes
     */
    public @NotNull @NoNullElements @Unmodifiable Set<QName> getIdAttributes() {
        return idAttributes;
    }

    /**
     * Sets the attributes considered ID attributes.
     * 
     * @param attributes attributes considered ID attributes
     */
    public void setIdAttributes(final Set<QName> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            idAttributes = Collections.emptySet();
        }

        HashSet<QName> newIdAttributes = new HashSet<QName>();
        for (QName attribute : attributes) {
            if (attribute != null) {
                newIdAttributes.add(attribute);
            }
        }

        if (newIdAttributes.isEmpty()) {
            idAttributes = Collections.emptySet();
        } else {
            idAttributes = Collections.unmodifiableSet(attributes);
        }
    }

    /** {@inheritDoc} */
    public boolean supportsNodeType(Short nodeType) {
        return Node.ATTRIBUTE_NODE == nodeType;
    }

    /** {@inheritDoc} */
    public void initialize(Node startingNode) throws TraversalException {
        if (startingNode.getNodeType() != Node.DOCUMENT_NODE
                || !startingNode.isSameNode(startingNode.getOwnerDocument().getDocumentElement())) {
            throw new InvalidStartingNode(
                    "This visitor is only valid when the starting node is a document or a document element");
        }

        final Document doc;
        if (startingNode.getNodeType() == Node.DOCUMENT_NODE) {
            doc = (Document) startingNode;
        } else {
            doc = startingNode.getOwnerDocument();
        }

        doc.setUserData(USER_DATA_NAME, new HashMap<String, Element>(), null);
    }

    /** {@inheritDoc} */
    public void visit(Node node) throws TraversalException {
        final Attr attribute = (Attr) node;
        final QName attributeName = QNameSupport.getNodeQName(attribute);

        if (idAttributes.contains(attributeName)) {
            final HashMap<String, Element> idIndex =
                    (HashMap<String, Element>) node.getOwnerDocument().getUserData(USER_DATA_NAME);
            final Element element = attribute.getOwnerElement();

            final String id = attribute.getValue();
            if (idIndex.containsKey(id)) {
                final Element previousElement = idIndex.get(id);
                throw new DuplicateIdException("An element with a name of " + QNameSupport.getNodeQName(element)
                        + " with ID attribute " + attributeName + " has an ID of " + id
                        + " which already refers to an element with a name of "
                        + QNameSupport.getNodeQName(previousElement));
            } else {
                idIndex.put(id, element);
            }
        }
    }

    /** {@inheritDoc} */
    public void complete() throws TraversalException {
        // nothing to do here
    }

    /** Exception thrown indicating that more than one element exists with the same ID. */
    public class DuplicateIdException extends TraversalException {

        /** Serial version UID. */
        private static final long serialVersionUID = 6749604462576818064L;

        /** Constructor. */
        public DuplicateIdException() {

        }

        /**
         * Constructor.
         * 
         * @param message exception message
         */
        public DuplicateIdException(String message) {
            super(message);
        }

        /**
         * Constructor.
         * 
         * @param wrappedException exception to be wrapped by this one
         */
        public DuplicateIdException(Exception wrappedException) {
            super(wrappedException);
        }

        /**
         * Constructor.
         * 
         * @param message exception message
         * @param wrappedException exception to be wrapped by this one
         */
        public DuplicateIdException(String message, Exception wrappedException) {
            super(message, wrappedException);
        }
    }
}