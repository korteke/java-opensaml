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

import org.opensaml.common.impl.AbstractSAMLObjectMarshaller;
import org.opensaml.xacml.ctx.XACMLAttribute;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.MarshallingException;
import org.w3c.dom.Element;

/**
 *Marshaller for {@link org.opensaml.xacml.ctx.XACMLAttribute} objects.
 *
 */

public class XACMLAttributeMarshaller extends AbstractSAMLObjectMarshaller {
	
	/**
     * Constructor
     */
    public XACMLAttributeMarshaller() {
        super();
    }

    /**
     * Constructor
     * 
     * @param namespaceURI
     * @param elementLocalName
     */
    protected XACMLAttributeMarshaller(String namespaceURI, String elementLocalName) {
        super(namespaceURI, elementLocalName);
    }
    
    /** {@inheritDoc} */
    protected void marshallAttributes(XMLObject samlElement, Element domElement) throws MarshallingException {
    	XACMLAttribute attribute = (XACMLAttribute) samlElement;

        if (attribute.getIssuer() != null) {
            domElement.setAttributeNS(null, XACMLAttribute.ISSUER_ATTRIB_NAME, attribute.getIssuer());
        }

        if (attribute.getDataType() != null) {
            domElement.setAttributeNS(null, XACMLAttribute.DATATYPE_ATTRIB_NAME, attribute.getDataType());
        }

        if (attribute.getAttributeID() != null) {
            domElement.setAttributeNS(null, XACMLAttribute.ATTRIBUTEID_ATTTRIB_NAME, attribute.getAttributeID());
        }
    }

}
