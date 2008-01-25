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

import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.opensaml.common.impl.AbstractSAMLObject;
import org.opensaml.xacml.ctx.XACMLStatusDetail;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.util.IndexedXMLObjectChildrenList;


/**
 * Implementation of {@link org.opensaml.xacml.ctx.XACMLStatusDetail} objects.
 *
 */

public class XACMLStatusDetailImpl extends AbstractSAMLObject implements
		XACMLStatusDetail {

	/** "any" children. */
    private final IndexedXMLObjectChildrenList<XMLObject> unknownChildren;
    
    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected XACMLStatusDetailImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        unknownChildren = new IndexedXMLObjectChildrenList<XMLObject>(this);
    }
    
    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        return Collections.unmodifiableList(unknownChildren);
    }
    
    /** {@inheritDoc} */
	public List<XMLObject> getUnknownXMLObjects() {
		return unknownChildren;
	}

	/** {@inheritDoc} */
	public List<XMLObject> getUnknownXMLObjects(QName typeOrName) {
		return unknownChildren.get(typeOrName);
	}
}
