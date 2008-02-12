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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import org.opensaml.ws.wsaddressing.Address;
import org.opensaml.ws.wsaddressing.EndpointReference;
import org.opensaml.ws.wsaddressing.Metadata;
import org.opensaml.ws.wsaddressing.ReferenceParameters;
import org.opensaml.xml.AbstractExtensibleXMLObject;
import org.opensaml.xml.XMLObject;

/**
 * EndpointReferenceImpl
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class EndpointReferenceImpl extends AbstractExtensibleXMLObject
        implements EndpointReference {

    /** {@link Address} child element */
    private Address address_= null;

    /** Optional {@link Metadata} child element */
    private Metadata metadata_= null;

    /** Optional {@link ReferenceParameters} child element */
    private ReferenceParameters referenceParameters_= null;

    /**
     * Constructor.
     * <p>
     * {@inheritDoc}
     */
    public EndpointReferenceImpl(String namespaceURI, String elementLocalName,
            String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wsaddressing.EndpointReference#getAddress()
     */
    public Address getAddress() {
        return address_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wsaddressing.EndpointReference#setAddress(org.opensaml.ws.wsaddressing.Address)
     */
    public void setAddress(Address address) {
        address_= prepareForAssignment(address_, address);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.xml.XMLObject#getOrderedChildren()
     */
    public List<XMLObject> getOrderedChildren() {
        ArrayList<XMLObject> children= new ArrayList<XMLObject>();
        if (address_ != null) {
            children.add(address_);
        }
        if (referenceParameters_ != null) {
            children.add(referenceParameters_);
        }
        if (metadata_ != null) {
            children.add(metadata_);
        }

        // xs:any element
        if (!getUnknownXMLObjects().isEmpty()) {
            children.addAll(getUnknownXMLObjects());
        }

        return Collections.unmodifiableList(children);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wsaddressing.EndpointReference#getMetadata()
     */
    public Metadata getMetadata() {
        return metadata_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wsaddressing.EndpointReference#setMetadata(org.opensaml.ws.wsaddressing.Metadata)
     */
    public void setMetadata(Metadata metadata) {
        metadata_= prepareForAssignment(metadata_, metadata);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wsaddressing.EndpointReference#getReferenceParameters()
     */
    public ReferenceParameters getReferenceParameters() {
        return referenceParameters_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wsaddressing.EndpointReference#setReferenceParameters(org.opensaml.ws.wsaddressing.ReferenceParameters)
     */
    public void setReferenceParameters(ReferenceParameters referenceParameters) {
        referenceParameters_= prepareForAssignment(referenceParameters_,
                                                   referenceParameters);
    }

}
