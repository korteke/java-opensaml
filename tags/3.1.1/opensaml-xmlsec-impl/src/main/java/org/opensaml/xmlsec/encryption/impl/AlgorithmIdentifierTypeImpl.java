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

package org.opensaml.xmlsec.encryption.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.encryption.AlgorithmIdentifierType;

/**
 * Abstract implementation of {@link org.opensaml.xmlsec.encryption.AlgorithmIdentifierType}.
 */
public abstract class AlgorithmIdentifierTypeImpl extends AbstractXMLObject implements AlgorithmIdentifierType {
    
    /** Algorithm attribute value. */
    private String algorithm;
    
    /** Parameters child element. */
    private XMLObject parameters;
    
    /**
     * Constructor.
     *
     * @param namespaceURI namespace URI
     * @param elementLocalName local name
     * @param namespacePrefix namespace prefix
     */
    protected AlgorithmIdentifierTypeImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public String getAlgorithm() {
        return algorithm;
    }

    /** {@inheritDoc} */
    public void setAlgorithm(@Nullable String newAlgorithm) {
        algorithm = prepareForAssignment(this.algorithm, newAlgorithm);
    }

    /** {@inheritDoc} */
    @Nullable public XMLObject getParameters() {
        return parameters;
    }

    /** {@inheritDoc} */
    public void setParameters(@Nullable XMLObject newParameters) {
        parameters = prepareForAssignment(parameters, newParameters);
    }
    
    /** {@inheritDoc} */
    @Nullable public List<XMLObject> getOrderedChildren() {
        ArrayList<XMLObject> children = new ArrayList<XMLObject>();
        
        if (parameters != null) {
            children.add(parameters);
        }
        
        return Collections.unmodifiableList(children);
    }

}
