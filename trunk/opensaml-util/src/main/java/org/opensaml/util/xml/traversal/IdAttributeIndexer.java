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

import java.util.HashMap;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.UserDataHandler;

/**
 * This {@link NodeVisitor} performs two tasks. One it creates an index, stored as use data on the {@link Document}
 * node, that maps an ID to the element that carries that ID. Second, it ensure thats all IDs in the document are
 * unique. This visitor only works if the first node in the traversal is either a {@link Document} or an {@link Element}
 * that is the document element.
 * 
 * In addition to attributes which are known to be ID attributes (i.e., {@link Attr#isId()} returns true) this visitor
 * can treat other attributes as ID attributes. This is helpful if the content model of the document is known ahead of
 * time and schema validation is not used (and thus ID attributes are not marked as such).
 * 
 * This visitor installs a {@link UserDataHandler} on all {@link Element} and {@link Attr} nodes and uses this to keep
 * the ID index up to date across DOM mutations.
 * 
 * If, at any point, the uniqueness constraint on IDs is violated, a {@link DuplicateIdException} will be thrown.
 */
public class IdAttributeIndexer implements NodeVisitor {

    /** The name under which the ID index will be stored as {@link Document} user data. */
    private static final String USER_DATA_NAME = IdAttributeIndexer.class.getPackage().getName() + ".IdIndex";

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
        Attr attribute = (Attr) node;

        // TODO Auto-generated method stub

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

    /**
     * {@link UserDataHandler} that keeps the ID index, located on the {@link Document}, up to date across DOM
     * mutations.
     */
    private class UpdatingIdIndexHandler implements UserDataHandler {

        /** {@inheritDoc} */
        public void handle(short operation, String key, Object data, Node src, Node dst) {
            // TODO Auto-generated method stub

        }

    }
}