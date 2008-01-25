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
import org.opensaml.xacml.ctx.XACMLSubject;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.MarshallingException;
import org.w3c.dom.Element;

/**
 *Marshaller for {@link org.opensaml.xacml.ctx.XACMLSubject} objects.
 *
 */

public class XACMLSubjectMarshaller extends AbstractSAMLObjectMarshaller {
	
	/**
     * Constructor
     */
	 public XACMLSubjectMarshaller() {
	      super();
	  }


    /**
     * Constructor
     * 
     * @param namespaceURI
     * @param elementLocalName
     */
    protected XACMLSubjectMarshaller(String namespaceURI, String elementLocalName) {
        super(namespaceURI, elementLocalName);
    }

    /** {@inheritDoc} */
    protected void marshallAttributes(XMLObject samlElement, Element domElement) throws MarshallingException {
    	XACMLSubject attribute = (XACMLSubject) samlElement;

        if (attribute.getSubjectCategory() != null) {
            domElement.setAttributeNS(null, XACMLSubject.SubjectCategory_ATTTRIB_NAME,attribute.getSubjectCategory());            		
        }      
    }
}
