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

package org.opensaml.saml.ext.saml2alg.impl;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.saml.common.AbstractSAMLObject;
import org.opensaml.saml.ext.saml2alg.SigningMethod;

/**
 * Implementation of {@link SigningMethod}.
 */
public class SigningMethodImpl extends AbstractSAMLObject implements SigningMethod {
    
    /** Wildcard child elements. */
    private final IndexedXMLObjectChildrenList<XMLObject> unknownChildren;
    
    /** Algorithm attribute value. */
    private String algorithm;
    
    /** MinKeySize attribute value. */
    private Integer minKeySize;

    /** MaxKeySize attribute value. */
    private Integer maxKeySize;
    
    /**
     * Constructor.
     *
     * @param namespaceURI the namespace URI
     * @param elementLocalName the element local name
     * @param namespacePrefix the namespace prefix
     */
    public SigningMethodImpl(@Nullable String namespaceURI, @Nonnull String elementLocalName,
            @Nullable String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        unknownChildren = new IndexedXMLObjectChildrenList<>(this);
    }
    
    /** {@inheritDoc} */
    @Nullable public String getAlgorithm() {
        return algorithm;
    }

    /** {@inheritDoc} */
    public void setAlgorithm(@Nullable String newValue) {
        algorithm = prepareForAssignment(algorithm, newValue);
    }
    
    /** {@inheritDoc} */
    @Nullable public Integer getMinKeySize() {
        return minKeySize;
    }

    /** {@inheritDoc} */
    public void setMinKeySize(@Nullable Integer newValue) {
        minKeySize = prepareForAssignment(minKeySize, newValue);
    }

    /** {@inheritDoc} */
    @Nullable public Integer getMaxKeySize() {
        return maxKeySize;
    }

    /** {@inheritDoc} */
    public void setMaxKeySize(@Nullable Integer newValue) {
        maxKeySize = prepareForAssignment(maxKeySize, newValue);
    }
    
    /**
     * {@inheritDoc}
     */
    public List<XMLObject> getUnknownXMLObjects() {
        return unknownChildren;
    }
    
    /** {@inheritDoc} */
    public List<XMLObject> getUnknownXMLObjects(QName typeOrName) {
        return (List<XMLObject>) unknownChildren.subList(typeOrName);
    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        return Collections.unmodifiableList(unknownChildren);
    }


}
