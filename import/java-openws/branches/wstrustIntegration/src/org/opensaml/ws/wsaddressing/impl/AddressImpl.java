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
import org.opensaml.xml.schema.impl.XSURIImpl;
import org.opensaml.xml.util.AttributeMap;

/**
 * AddressImpl is the concrete implementation of {@link Address}.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision$
 */
public class AddressImpl extends XSURIImpl implements Address {

    /** xs:anyAttribute for this element. */
    private AttributeMap anyAttributes_;

    /**
     * Constructor.
     * <p>
     * {@inheritDoc}
     */
    public AddressImpl(String namespaceURI, String elementLocalName,
            String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        anyAttributes_= new AttributeMap(this);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.xml.AttributeExtensibleXMLObject#getUnknownAttributes()
     */
    public AttributeMap getUnknownAttributes() {
        return anyAttributes_;
    }

}
