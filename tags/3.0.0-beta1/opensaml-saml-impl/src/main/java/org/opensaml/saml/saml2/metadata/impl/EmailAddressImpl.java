/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * 
 */

package org.opensaml.saml.saml2.metadata.impl;

import java.util.List;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.AbstractSAMLObject;
import org.opensaml.saml.saml2.metadata.EmailAddress;

/**
 * Concrete implementation of {@link org.opensaml.saml.saml2.metadata.EmailAddress}.
 */
public class EmailAddressImpl extends AbstractSAMLObject implements EmailAddress {

    /** The email address. */
    private String address;

    /**
     * Constructor.
     * 
     * @param namespaceURI name space
     * @param elementLocalName local name
     * @param namespacePrefix prefix
     */
    protected EmailAddressImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Override
    public String getAddress() {
        return address;
    }

    /** {@inheritDoc} */
    @Override
    public void setAddress(String addr) {
        address = prepareForAssignment(address, addr);
    }

    /** {@inheritDoc} */
    @Override
    public List<XMLObject> getOrderedChildren() {
        return null;
    }
}