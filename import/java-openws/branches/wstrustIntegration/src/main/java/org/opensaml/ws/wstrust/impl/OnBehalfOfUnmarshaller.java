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
package org.opensaml.ws.wstrust.impl;


import org.opensaml.ws.wsaddressing.EndpointReference;
import org.opensaml.ws.wssecurity.SecurityTokenReference;
import org.opensaml.ws.wstrust.OnBehalfOf;
import org.opensaml.xml.AbstractElementExtensibleXMLObjectUnmarshaller;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;

/**
 * Unmarshaller for the &lt;wst:OnBehalfOf&gt; element.
 * 
 * @see OnBehalfOf
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class OnBehalfOfUnmarshaller extends
        AbstractElementExtensibleXMLObjectUnmarshaller {

    /**
     * Default constructor.
     * <p>
     * {@inheritDoc}
     */
    public OnBehalfOfUnmarshaller() {
        super(OnBehalfOf.ELEMENT_NAME.getNamespaceURI(),
              OnBehalfOf.ELEMENT_NAME.getLocalPart());
    }

    /**
     * Try to unmarshall the &lt;wsa:EndpointReference&gt; or the
     * &lt;SecurityTokenReference&gt; child elements, if any.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void processChildElement(XMLObject parentXMLObject,
            XMLObject childXMLObject) throws UnmarshallingException {
        OnBehalfOf container= (OnBehalfOf) parentXMLObject;
        if (childXMLObject instanceof EndpointReference) {
            EndpointReference endpointReference= (EndpointReference) childXMLObject;
            container.setEndpointReference(endpointReference);
        }
        else if (childXMLObject instanceof SecurityTokenReference) {
            SecurityTokenReference securityTokenReference= (SecurityTokenReference) childXMLObject;
            container.setSecurityTokenReference(securityTokenReference);
        }
        else {
            // other security token in xs:any
            super.processChildElement(parentXMLObject, childXMLObject);
        }
    }

}
