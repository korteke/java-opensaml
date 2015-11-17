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

/**
 * 
 */

package org.opensaml.saml.saml2.metadata.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.saml.common.AbstractSAMLObject;
import org.opensaml.saml.saml2.metadata.AttributeConsumingService;
import org.opensaml.saml.saml2.metadata.RequestedAttribute;
import org.opensaml.saml.saml2.metadata.ServiceDescription;
import org.opensaml.saml.saml2.metadata.ServiceName;

/**
 * Concrete implementation of {@link org.opensaml.saml.saml2.metadata.AttributeConsumingService}.
 */
public class AttributeConsumingServiceImpl extends AbstractSAMLObject implements AttributeConsumingService {

    /** Index of this service. */
    private int index;

    /** isDefault attribute of this service. */
    private XSBooleanValue isDefault;

    /** ServiceName children. */
    private final XMLObjectChildrenList<ServiceName> serviceNames;

    /** ServiceDescription children. */
    private final XMLObjectChildrenList<ServiceDescription> serviceDescriptions;

    /** RequestedAttribute children. */
    private final XMLObjectChildrenList<RequestedAttribute> requestAttributes;
    
    /**
     * Constructor.
     * 
     * @param namespaceURI name space
     * @param elementLocalName local name
     * @param namespacePrefix prefix
     */
    protected AttributeConsumingServiceImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        serviceNames = new XMLObjectChildrenList<>(this);
        serviceDescriptions = new XMLObjectChildrenList<>(this);
        requestAttributes = new XMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    public int getIndex() {
        return index;
    }

    /** {@inheritDoc} */
    public void setIndex(int theIndex) {
        if (this.index != theIndex) {
            releaseThisandParentDOM();
            this.index = theIndex;
        }
    }
    
    /** {@inheritDoc} */
    public Boolean isDefault(){
        if(isDefault != null){
            return isDefault.getValue();
        }
        
        return Boolean.FALSE;
    }

    /** {@inheritDoc} */
    public XSBooleanValue isDefaultXSBoolean() {
        return isDefault;
    }
    
    /** {@inheritDoc} */
    public void setIsDefault(Boolean newIsDefault){
        if(newIsDefault != null){
            isDefault = prepareForAssignment(isDefault, new XSBooleanValue(newIsDefault, false));
        }else{
            isDefault = prepareForAssignment(isDefault, null);
        }
    }

    /** {@inheritDoc} */
    public void setIsDefault(XSBooleanValue newIsDefault) {
        isDefault = prepareForAssignment(isDefault, newIsDefault);
    }

    /** {@inheritDoc} */
    public List<ServiceName> getNames() {
        return serviceNames;
    }

    /** {@inheritDoc} */
    public List<ServiceDescription> getDescriptions() {
        return serviceDescriptions;
    }

    /** {@inheritDoc} */
    public List<RequestedAttribute> getRequestAttributes() {
        return requestAttributes;
    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        ArrayList<XMLObject> children = new ArrayList<>();

        children.addAll(serviceNames);
        children.addAll(serviceDescriptions);
        children.addAll(requestAttributes);

        return Collections.unmodifiableList(children);
    }
}