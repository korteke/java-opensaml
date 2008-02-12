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
package org.opensaml.ws.wsaddressing.impl;


import org.opensaml.ws.wsaddressing.Address;
import org.opensaml.ws.wsaddressing.EndpointReference;
import org.opensaml.ws.wsaddressing.Metadata;
import org.opensaml.ws.wsaddressing.ReferenceParameters;
import org.opensaml.xml.AbstractExtensibleXMLObjectUnmarshaller;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;

/**
 * Unmarshaller for the &lt;wsa:EndpointReference&gt; element.
 * 
 * @see EndpointReference
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class EndpointReferenceUnmarshaller extends
        AbstractExtensibleXMLObjectUnmarshaller {

    /**
     * Default constructor.
     * <p>
     * {@inheritDoc}
     */
    public EndpointReferenceUnmarshaller() {
        this(EndpointReference.ELEMENT_NAME.getNamespaceURI(),
             EndpointReference.ELEMENT_NAME.getLocalPart());
    }

    /**
     * Constructor for sub-classes.
     * <p>
     * {@inheritDoc}
     */
    protected EndpointReferenceUnmarshaller(String namespaceURI,
            String localPart) {
        super(namespaceURI, localPart);
    }

    /**
     * Unmarshalls the &lt;wsa:Address&gt;, the &lt;wsa:Metadata&gt; and the
     * &lt;wsa:ReferenceParameters&gt; child elements.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void processChildElement(XMLObject parentXMLObject,
            XMLObject childXMLObject) throws UnmarshallingException {
        EndpointReference epr= (EndpointReference) parentXMLObject;
        if (childXMLObject instanceof Address) {
            Address address= (Address) childXMLObject;
            epr.setAddress(address);
        }
        else if (childXMLObject instanceof Metadata) {
            Metadata metadata= (Metadata) childXMLObject;
            epr.setMetadata(metadata);
        }
        else if (childXMLObject instanceof ReferenceParameters) {
            ReferenceParameters ref= (ReferenceParameters) childXMLObject;
            epr.setReferenceParameters(ref);
        }
        else {
            super.processChildElement(parentXMLObject, childXMLObject);
        }
    }

}
