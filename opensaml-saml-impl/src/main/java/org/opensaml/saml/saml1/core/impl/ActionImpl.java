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

package org.opensaml.saml.saml1.core.impl;

import java.util.List;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.AbstractSAMLObject;
import org.opensaml.saml.saml1.core.Action;

/**
 * Concrete implementation of {@link org.opensaml.saml.saml1.core.Action}.
 */
public class ActionImpl extends AbstractSAMLObject implements Action {

    /** Place to store the namespace. */
    private String namespace;

    /** Where to store the contents. */
    private String contents;
    
    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected ActionImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);   
    }

    /** {@inheritDoc} */
    @Override
    public String getNamespace() {
        return namespace;
    }

    /** {@inheritDoc} */
    @Override
    public void setNamespace(String ns) {
        namespace = prepareForAssignment(namespace, ns);
    }

    /** {@inheritDoc} */
    @Override
    public String getContents() {
        return contents;
    }

    /** {@inheritDoc} */
    @Override
    public void setContents(String c) {
        contents = prepareForAssignment(contents, c);
    }

    /** {@inheritDoc} */
    @Override
    public List<XMLObject> getOrderedChildren() {
        // No elements
        return null;
    }
}