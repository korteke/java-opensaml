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

import java.util.List;

import org.opensaml.common.impl.AbstractSAMLObject;
import org.opensaml.xacml.policy.XACMLPolicyIdReference;
import org.opensaml.xml.XMLObject;

/**
 * Implementation of {@link org.opensaml.xacml.policy.XACMLPolicyIdReference}
 * 
 */
public class XACMLPolicyIdReferenceImpl extends AbstractSAMLObject implements
		XACMLPolicyIdReference {
	
	/**
     * Constructor
     * 
     * @param namespaceURI
     * @param elementLocalName
     * @param namespacePrefix
     */
	protected XACMLPolicyIdReferenceImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }
	
	/** {@inheritDoc}*/
	public List<XMLObject> getOrderedChildren() {
		// TODO Auto-generated method stub
		return null;
	}

}
