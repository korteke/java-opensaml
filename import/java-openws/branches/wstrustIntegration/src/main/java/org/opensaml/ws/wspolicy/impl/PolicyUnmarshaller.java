/*
 * Copyright 2008 Members of the EGEE Collaboration.
 * Copyright 2008 University Corporation for Advanced Internet Development, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.ws.wspolicy.impl;

import org.opensaml.ws.wspolicy.Policy;
import org.opensaml.xml.AbstractExtensibleXMLObjectUnmarshaller;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;
import org.w3c.dom.Attr;

/**
 * Unmarshaller for the &lt;wsp:Policy&gt; element.
 * 
 * @see Policy
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class PolicyUnmarshaller extends AbstractExtensibleXMLObjectUnmarshaller {

    /**
     * Default constructor.
     * <p>
     * {@inheritDoc}
     */
    public PolicyUnmarshaller() {
        super();
    }

    /**
     * Unmarshalls the <code>wsu:I</code> and the <code>Name</code> attributes.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void processAttribute(XMLObject xmlObject, Attr attribute) throws UnmarshallingException {
        Policy policy = (Policy) xmlObject;
        String attrName = attribute.getLocalName();
        if (Policy.ID_ATTR_LOCAL_NAME.equals(attrName)) {
            String id = attribute.getValue();
            policy.setId(id);
        } else if (Policy.NAME_ATTR_LOCAL_NAME.equals(attrName)) {
            String name = attribute.getValue();
            policy.setName(name);
        } else {
            // xs:anyAttribute attributes
            super.processAttribute(xmlObject, attribute);
        }
    }

}
