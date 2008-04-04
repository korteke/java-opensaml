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

import org.opensaml.xacml.policy.ApplyType;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.AbstractXMLObjectMarshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.util.DatatypeHelper;
import org.w3c.dom.Element;

/**
 * Marshaller for {@link ApplyType}.
 */
public class ApplyTypeMarshaller extends AbstractXMLObjectMarshaller {

    /** Constructor. */
    public ApplyTypeMarshaller() {
        super();
    }
    
    
    /** {@inheritDoc} */
    protected void marshallAttributes(XMLObject xmlObject, Element domElement) throws MarshallingException {
        ApplyType applyType = (ApplyType)xmlObject;
        
        if(!DatatypeHelper.isEmpty(applyType.getFunctionId())){
            domElement.setAttribute(ApplyType.FUNCTION_ID_ATTRIB_NAME, applyType.getFunctionId());
        }

    }

    /** {@inheritDoc} */
    protected void marshallElementContent(XMLObject xmlObject, Element domElement) throws MarshallingException {
   
    }

}
