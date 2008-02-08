/*
 * Copyright 2008 University Corporation for Advanced Internet Development, Inc.
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

import org.opensaml.xacml.policy.AttributeValueType;
import org.opensaml.xacml.policy.CombinerParameterType;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.AbstractXMLObjectUnmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.util.DatatypeHelper;
import org.w3c.dom.Attr;

/**
 * Unmarshaller for {@link CombinerParameterType}.
 */
public class CombinerParameterTypeUnmarshaller extends AbstractXMLObjectUnmarshaller {

    /** Constructor. */
    public CombinerParameterTypeUnmarshaller() {
        super();
    }

    
    /** {@inheritDoc} */
    protected void processAttribute(XMLObject xmlObject, Attr attribute) throws UnmarshallingException {
        CombinerParameterType combinerParameterType = (CombinerParameterType) xmlObject;
      
        if(attribute.getLocalName().equals(CombinerParameterType.PARAMETER_NAMEATTRIB_NAME)){
            combinerParameterType.setParameterName(DatatypeHelper.safeTrimOrNullString(attribute.getValue()));
        } 
    }

    /** {@inheritDoc} */
    protected void processChildElement(XMLObject parentXMLObject, XMLObject childXMLObject)
            throws UnmarshallingException {
        CombinerParameterType combinerParameterType = (CombinerParameterType) parentXMLObject;
        
        if(childXMLObject instanceof AttributeValueType){
            combinerParameterType.setAttributeValue((AttributeValueType)childXMLObject);
        }
    }

    /** {@inheritDoc} */
    protected void processElementContent(XMLObject xmlObject, String elementContent) {
    }


}
