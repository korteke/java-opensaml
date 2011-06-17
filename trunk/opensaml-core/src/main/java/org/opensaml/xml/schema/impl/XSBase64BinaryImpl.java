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

package org.opensaml.xml.schema.impl;

import java.util.List;

import org.opensaml.xml.XMLObject;
import org.opensaml.xml.schema.XSBase64Binary;
import org.opensaml.xml.validation.AbstractValidatingXMLObject;

/**
 * Concrete implementation of {@link org.opensaml.xml.schema.XSBase64Binary}.
 */
public class XSBase64BinaryImpl extends AbstractValidatingXMLObject implements XSBase64Binary {

    /** Value of this base64Binary element. */
    private String value;
    
    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected XSBase64BinaryImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public void setValue(String newValue) {
        value = prepareForAssignment(value, newValue);
    }

    /**
     * {@inheritDoc}
     */
    public List<XMLObject> getOrderedChildren() {
        // no children
        return null;
    }
}