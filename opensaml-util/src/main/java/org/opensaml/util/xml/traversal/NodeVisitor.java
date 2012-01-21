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

import net.jcip.annotations.ThreadSafe;

import org.opensaml.util.constraint.documented.NotNull;
import org.w3c.dom.Node;

/**
 * A visitor for {@link Node} instances. These visitors are generally meant to be used in conjunction with a
 * {@link VisitingDomTreeWalker}.
 */
@ThreadSafe
@Deprecated
public interface NodeVisitor {

    /**
     * Gets whether this visitor operates upon a particular node type as given by {@link Node#getNodeType()}.
     * 
     * @param nodeType the node type
     * 
     * @return true if this visitor operates on the given node type, false if not
     */
    public boolean supportsNodeType(final Short nodeType);

    /**
     * Initializes the visitor. Note, the given node is the first node in a traversal and while its given here it will
     * also be given during the first execution of {@link #visit(Node)}.
     * 
     * @param startingNode first node in a traversal
     * 
     * @throws TraversalException thrown if there is a problem initializing this visitor
     */
    public void initialize(@NotNull final Node startingNode) throws TraversalException;

    /**
     * Performs the visitation logic on the given node.
     * 
     * @param node the node being visited
     * 
     * @throws TraversalException thrown if there is a problem processing the current node
     */
    public void visit(@NotNull final Node node) throws TraversalException;

    /**
     * Once all nodes within a given traversal have been visited this method is invoked so that this visitor can perform
     * any post-processing logic.
     * 
     * @throws TraversalException thrown if there is a problem executing the post-processing logic
     */
    public void complete() throws TraversalException;
}