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
import java.util.HashSet;
import java.util.Set;

import net.jcip.annotations.ThreadSafe;

import org.opensaml.util.constraint.documented.NoNullElements;
import org.opensaml.util.constraint.documented.NotNull;
import org.opensaml.util.constraint.documented.Unmodifiable;
import org.w3c.dom.Node;

/** A DOM tree walker that runs a set of {@link NodeVisitor} against each node in the DOM tree. */
@ThreadSafe
public class VisitingDomTreeWalker {

    /** Set of registered visitors. */
    private Set<NodeVisitor> visitors;

    /** Constructor. */
    public VisitingDomTreeWalker() {
        visitors = Collections.emptySet();
    }

    /**
     * Gets the set of currently available {@link NodeVisitor}.
     * 
     * @return set of currently available {@link NodeVisitor}
     */
    public @NotNull @NoNullElements @Unmodifiable Set<NodeVisitor> getVisitors() {
        return visitors;
    }

    /**
     * Adds the given collection of {@link NodeVisitor} to the set of {@link NodeVisitor} used by this tree walker.
     * Note, adding visitors does not have effect on any currently executing tree walks.
     * 
     * @param nodeVisitors visitors to add to this tree walkers set of {@link NodeVisitor}
     */
    public void addNodeVisitors(final NodeVisitor... nodeVisitors) {
        HashSet<NodeVisitor> newVisitors = new HashSet<NodeVisitor>(visitors);

        for (NodeVisitor visitor : nodeVisitors) {
            if (visitor != null) {
                newVisitors.add(visitor);
            }
        }

        visitors = Collections.unmodifiableSet(newVisitors);
    }

    /**
     * Removes the given collection of {@link NodeVisitor} to the set of {@link NodeVisitor} used by this tree walker.
     * Note, removing visitors does not have effect on any currently executing tree walks.
     * 
     * @param nodeVisitors visitors to remove from this tree walkers set of {@link NodeVisitor}
     */
    public void removeNodeVisitors(final NodeVisitor... nodeVisitors) {
        HashSet<NodeVisitor> newVisitors = new HashSet<NodeVisitor>(visitors);

        for (NodeVisitor visitor : nodeVisitors) {
            if (visitor != null) {
                newVisitors.remove(visitor);
            }
        }

        if (newVisitors.isEmpty()) {
            visitors = Collections.emptySet();
        } else {
            visitors = Collections.unmodifiableSet(newVisitors);
        }
    }

    /**
     * Traverses the DOM tree, using the given {@link Traverser}, and executes all applicable {@link NodeVisitor} for
     * each returned {@link Node}.
     * 
     * @param traverser the traverser used to select which {@link Node}s will be visited
     * 
     * @throws TraversalException thrown if there is a problem processing {@link Node}s as they are traversed
     */
    public void traverseTree(final Traverser traverser) throws TraversalException {
        final Set<NodeVisitor> currentVisitors = visitors;
        final HashSet<NodeVisitor> activatedVisitors = new HashSet<NodeVisitor>();

        if (traverser.hasNext()) {
            visitNode(traverser.next(), currentVisitors, activatedVisitors);
        }

        for (NodeVisitor visitor : activatedVisitors) {
            visitor.complete();
        }
    }

    /**
     * Provides the given node to all applicable {@link NodeVisitor}.
     * 
     * @param node node currently being visited
     * @param currentVisitors currently available set of visitors
     * @param activatedVisitors {@link NodeVisitor} that were "activated" for this node
     * 
     * @throws TraversalException thrown if there is a problem processing {@link Node}s as they are traversed
     */
    private void visitNode(Node node, Set<NodeVisitor> currentVisitors, Set<NodeVisitor> activatedVisitors)
            throws TraversalException {
        for (NodeVisitor visitor : currentVisitors) {
            if (visitor.supportsNodeType(node.getNodeType())) {
                visitor.visit(node);
                activatedVisitors.add(visitor);
            }
        }
    }
}