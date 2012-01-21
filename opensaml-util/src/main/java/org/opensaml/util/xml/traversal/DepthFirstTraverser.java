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

import net.jcip.annotations.NotThreadSafe;

import org.opensaml.util.constraint.documented.NotNull;
import org.w3c.dom.Node;

/** An implementation of {@link Traverser} that returns {@link Node} in depth first order. */
@NotThreadSafe
@Deprecated
public class DepthFirstTraverser implements Traverser {

    /**
     * Constructor.
     * 
     * @param start the node where the traversal begins
     * @param includeStartingNode whether the starting node is included in the traversed set of nodes
     */
    public DepthFirstTraverser(@NotNull final Node start, boolean includeStartingNode) {
        // TODO
    }

    /** {@inheritDoc} */
    public boolean hasNext() {
        // TODO Auto-generated method stub
        return false;
    }

    /** {@inheritDoc} */
    public Node next() {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    public void remove() {
        throw new UnsupportedOperationException();
    }
}