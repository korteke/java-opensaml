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

package org.opensaml.xacml.ctx.impl;

import net.shibboleth.utilities.java.support.xml.ElementSupport;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.xacml.ctx.ResourceContentType;
import org.opensaml.xacml.impl.AbstractXACMLObjectMarshaller;
import org.w3c.dom.Element;

/** Marshaller for {@link ResourceContentType} objects. */
public class ResourceContentTypeMarshaller extends AbstractXACMLObjectMarshaller {
    
    /** Constructor. */
    public ResourceContentTypeMarshaller() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param targetNamespaceURI the namespace URI of either the schema type QName or element QName of the elements this
     *            marshaller operates on
     * @param targetLocalName the local name of either the schema type QName or element QName of the elements this
     *            marshaller operates on
     */
    protected ResourceContentTypeMarshaller(String targetNamespaceURI, String targetLocalName) {
        super(targetNamespaceURI, targetLocalName);
    }

    /** {@inheritDoc} */
    protected void marshallAttributes(XMLObject xmlObject, Element domElement) throws MarshallingException {
        ResourceContentType resourceContent  = (ResourceContentType)xmlObject;
        
        marshallUnknownAttributes(resourceContent, domElement);
    }

    /** {@inheritDoc} */
    protected void marshallElementContent(XMLObject xmlObject, Element domElement) throws MarshallingException {
        ResourceContentType resourceContent = (ResourceContentType) xmlObject;
        
        if(resourceContent.getValue() != null){
            ElementSupport.appendTextContent(domElement, resourceContent.getValue());
        }
    }
}