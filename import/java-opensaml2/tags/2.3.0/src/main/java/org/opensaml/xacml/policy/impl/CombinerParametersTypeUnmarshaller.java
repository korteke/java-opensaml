/*
 * Copyright 2008 University Corporation for Advanced Internet Development, Inc.
 * Copyright 2008 Members of the EGEE Collaboration.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.xacml.policy.impl;

import org.opensaml.xacml.policy.CombinerParameterType;
import org.opensaml.xacml.policy.CombinerParametersType;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.AbstractXMLObjectUnmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.w3c.dom.Attr;

/**
 * Unmarshaller for {@link CombinerParametersType}.
 */
public class CombinerParametersTypeUnmarshaller extends AbstractXMLObjectUnmarshaller {

    /** Constructor. */
    public CombinerParametersTypeUnmarshaller() {
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
    public CombinerParametersTypeUnmarshaller(String targetNamespaceURI,String targetLocalName ) {
        super(targetNamespaceURI,targetLocalName);
    }

    
    /** {@inheritDoc} */
    protected void processAttribute(XMLObject xmlObject, Attr attribute) throws UnmarshallingException {
           
    }

    /** {@inheritDoc} */
    protected void processChildElement(XMLObject parentXMLObject, XMLObject childXMLObject)
            throws UnmarshallingException {
        CombinerParametersType combinerParametersType = (CombinerParametersType) parentXMLObject;
        
        if(childXMLObject instanceof CombinerParameterType){
            combinerParametersType.getCombinerParameters().add((CombinerParameterType)childXMLObject);
        }
    }

    /** {@inheritDoc} */
    protected void processElementContent(XMLObject xmlObject, String elementContent) {
    }
}
