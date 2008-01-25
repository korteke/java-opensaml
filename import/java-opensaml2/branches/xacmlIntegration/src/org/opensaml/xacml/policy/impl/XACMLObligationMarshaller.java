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
package org.opensaml.xacml.policy.impl;

import org.opensaml.common.impl.AbstractSAMLObjectMarshaller;
import org.opensaml.xacml.policy.XACMLObligation;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.MarshallingException;
import org.w3c.dom.Element;
/**
 *Marshaller for {@link org.opensaml.xacml.policy.XACMLObligation} 
 *
 */
public class XACMLObligationMarshaller extends AbstractSAMLObjectMarshaller {
	
	 /**
	   * Constructor
	   */
	  public XACMLObligationMarshaller() {
	      super();
	  }

	  /**
	   * Constructor
	   * 
	   * @param namespaceURI
	   * @param elementLocalName
	   */
	  protected XACMLObligationMarshaller(String namespaceURI, String elementLocalName) {
	      super(namespaceURI, elementLocalName);
	  }
	  

	    /** {@inheritDoc} */
	  protected void marshallAttributes(XMLObject samlElement, Element domElement) throws MarshallingException {
		  XACMLObligation attribute = (XACMLObligation) samlElement;

		  if (attribute.getObligationId() != null) {
			  domElement.setAttributeNS(null, XACMLObligation.ObligationId_ATTTRIB_NAME,attribute.getObligationId());            		
		  }
		  if(attribute.getFulfillOn() != null && attribute.getFulfillOn().equals(XACMLObligation.DENY)){
			  domElement.setAttributeNS(null, XACMLObligation.FulfillOn_ATTTRIB_NAME,attribute.getFulfillOn());
		  }
		  if(attribute.getFulfillOn() != null && attribute.getFulfillOn().equals(XACMLObligation.PERMIT)){
			  domElement.setAttributeNS(null, XACMLObligation.FulfillOn_ATTTRIB_NAME,attribute.getFulfillOn());
		  }
	  }

}
