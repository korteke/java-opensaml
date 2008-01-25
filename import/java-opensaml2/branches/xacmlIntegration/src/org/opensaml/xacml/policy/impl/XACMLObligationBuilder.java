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

import org.opensaml.common.impl.AbstractSAMLObjectBuilder;
import org.opensaml.xacml.ctx.XACMLConstants;
import org.opensaml.xacml.policy.XACMLObligation;
import org.opensaml.xacml.policy.impl.XACMLObligationImpl;
/**
 *Builder for {@link org.opensaml.xacml.policy.XACMLObligation} 
 *
 */
public class XACMLObligationBuilder extends AbstractSAMLObjectBuilder<XACMLObligation> {

	@Override
	 /** {@inheritDoc} */
	public XACMLObligation buildObject() {
		return buildObject(XACMLConstants.XACML20_NS,
				XACMLObligation.DEFAULT_ELEMENT_LOCAL_NAME,
				XACMLConstants.XACML_PREFIX);
	}

	@Override
	 /** {@inheritDoc} */
	public XACMLObligation buildObject(String namespaceURI, String localName,
			String namespacePrefix) {
		return new XACMLObligationImpl(namespaceURI,localName,namespacePrefix);
	}

}
