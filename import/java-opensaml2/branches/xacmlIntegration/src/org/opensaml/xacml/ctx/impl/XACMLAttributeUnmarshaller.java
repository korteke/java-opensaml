/*
Copyright 2008 Members of the EGEE Collaboration.
Copyright 2008 University Corporation for Advanced Internet Development,
Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.opensaml.xacml.ctx.impl;

import javax.xml.namespace.QName;

import org.opensaml.common.impl.AbstractSAMLObjectUnmarshaller;
import org.opensaml.xacml.ctx.XACMLAttribute;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;
import org.w3c.dom.Attr;

/**
 * Unmarshaller for {@link org.opensaml.xacml.ctx.XACMLAttribute} objects.
 *
 */

public class XACMLAttributeUnmarshaller extends AbstractSAMLObjectUnmarshaller {
	
	 /**
     * Constructor
     */
    public XACMLAttributeUnmarshaller() {
        super();
    }

    /**
     * Constructor
     * 
     * @param namespaceURI
     * @param elementLocalName
     */
    protected XACMLAttributeUnmarshaller(String namespaceURI, String elementLocalName) {
        super(namespaceURI, elementLocalName);
    }

    /** {@inheritDoc} */
    protected void processChildElement(XMLObject parentSAMLObject, XMLObject childSAMLObject)
            throws UnmarshallingException {

        XACMLAttribute attribute = (XACMLAttribute) parentSAMLObject;

        QName childQName = childSAMLObject.getElementQName();
        if (childQName.getLocalPart().equals("AttributeValue")) {
            attribute.getAttributeValues().add(childSAMLObject);
        } else {
            super.processChildElement(parentSAMLObject, childSAMLObject);
        }
    }
    
    /** {@inheritDoc} */
    protected void processAttribute(XMLObject samlObject, Attr attribute) throws UnmarshallingException {

    	XACMLAttribute attrib = (XACMLAttribute) samlObject;

        if (attribute.getLocalName().equals(XACMLAttribute.ATTRIBUTEID_ATTTRIB_NAME)) {
        	attrib.setAttributeID(attribute.getValue());          
        } else if (attribute.getLocalName().equals(XACMLAttribute.DATATYPE_ATTRIB_NAME)) {
        	attrib.setDataType(attribute.getValue());            
        } else if(attribute.getLocalName().equals(XACMLAttribute.ISSUER_ATTRIB_NAME)){        	
            attrib.setIssuer(attribute.getValue());
        } 
        super.processAttribute(samlObject, attribute);
    }

}
