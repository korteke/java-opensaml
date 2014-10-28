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
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.xacml.impl.AbstractXACMLObjectUnmarshaller;
import org.opensaml.xacml.policy.AttributeSelectorType;
import org.w3c.dom.Attr;

/**
 *Unmarshaller for {@link AttributeSelectorType}.
 */
public class AttributeSelectorTypeUnmarshaller extends AbstractXACMLObjectUnmarshaller {

    /** Constructor. */
    public AttributeSelectorTypeUnmarshaller() {
        super();
    }
    
    /** {@inheritDoc} */
    protected void processAttribute(XMLObject xmlObject, Attr attribute) throws UnmarshallingException {
        AttributeSelectorType attributeSelectorType = (AttributeSelectorType) xmlObject;
        
        if (attribute.getLocalName().equals(AttributeSelectorType.REQUEST_CONTEXT_PATH_ATTRIB_NAME)){
            attributeSelectorType.setRequestContextPath(StringSupport.trimOrNull(attribute.getValue()));
        } else  if (attribute.getLocalName().equals(AttributeSelectorType.DATA_TYPE_ATTRIB_NAME)){
            attributeSelectorType.setDataType(StringSupport.trimOrNull(attribute.getValue()));
        } else  if (attribute.getLocalName().equals(AttributeSelectorType.MUST_BE_PRESENT_ATTRIB_NAME)){
            if ("True".equals(attribute.getValue()) || "true".equals(attribute.getValue())) {
                attributeSelectorType.setMustBePresentXSBoolean(XSBooleanValue.valueOf("1"));
            } else {
                attributeSelectorType.setMustBePresentXSBoolean(XSBooleanValue.valueOf("0"));
            }          
        } else {
            super.processAttribute(xmlObject, attribute);
        }

    }

}
