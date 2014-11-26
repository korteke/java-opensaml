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

package org.opensaml.xmlsec.signature.impl;

import java.util.List;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.signature.KeyInfoReference;

/**
 * Concrete implementation of {@link KeyInfoReference}.
 */
public class KeyInfoReferenceImpl extends AbstractXMLObject implements KeyInfoReference {
    
    /** Id attribute value. */
    private String id;
    
    /** URI attribute value. */
    private String uri;
    
    /**
     * Constructor.
     *
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected KeyInfoReferenceImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    public String getID() {
        return id;
    }

    /** {@inheritDoc} */
    public void setID(String newID) {
        String oldID = id;
        id = prepareForAssignment(id, newID);
        registerOwnID(oldID, id);
    }
    
    /** {@inheritDoc} */
    public String getURI() {
        return uri;
    }

    /** {@inheritDoc} */
    public void setURI(String newURI) {
        uri = prepareForAssignment(uri, newURI);
    }
    
    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        // no children
        return null;
    }
    
}