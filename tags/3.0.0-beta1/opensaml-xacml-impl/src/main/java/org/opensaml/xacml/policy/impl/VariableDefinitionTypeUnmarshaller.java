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

package org.opensaml.xacml.policy.impl;

import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.xacml.impl.AbstractXACMLObjectUnmarshaller;
import org.opensaml.xacml.policy.ExpressionType;
import org.opensaml.xacml.policy.VariableDefinitionType;
import org.w3c.dom.Attr;

/**
 * Unmarshaller for {@link org.opensaml.xacml.policy.VariableDefinitionType}.
 */
public class VariableDefinitionTypeUnmarshaller extends AbstractXACMLObjectUnmarshaller {
    
    /** {@inheritDoc} */
    @Override
    protected void processAttribute(XMLObject xmlObject, Attr attribute) throws UnmarshallingException {
        
        if(attribute.getLocalName().equals(VariableDefinitionType.VARIABLE_ID_ATTRIB_NAME)){
            VariableDefinitionType variableDefinitionType = (VariableDefinitionType) xmlObject;
            variableDefinitionType.setVariableId(StringSupport.trimOrNull(attribute.getValue()));
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }
    
    /** {@inheritDoc} */
    @Override
    protected void processChildElement(XMLObject parentXMLObject, XMLObject childXMLObject)
            throws UnmarshallingException {
        
        if(childXMLObject instanceof ExpressionType){
            VariableDefinitionType variableDefinition= (VariableDefinitionType) parentXMLObject;
            variableDefinition.setExpression((ExpressionType)childXMLObject);
        } else {
            super.processChildElement(parentXMLObject, childXMLObject);
        }
    }

    
}