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
package org.opensaml.saml.ext.saml2mdrpi.impl;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.saml.common.AbstractSAMLObject;
import org.opensaml.saml.ext.saml2mdrpi.RegistrationInfo;
import org.opensaml.saml.ext.saml2mdrpi.RegistrationPolicy;

/**
 * Concrete Implementation of {@link RegistrationInfo}.
 */
public class RegistrationInfoImpl extends AbstractSAMLObject implements RegistrationInfo {

    /** The policies. */
    private XMLObjectChildrenList<RegistrationPolicy> registrationPolicies;

    /** The authority. */
    private String registrationAuthority;

    /** The registration instant. */
    private DateTime registrationInstant;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected RegistrationInfoImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        registrationPolicies = new IndexedXMLObjectChildrenList<RegistrationPolicy>(this);
    }

    /** {@inheritDoc} */
    public String getRegistrationAuthority() {
        return registrationAuthority;
    }

    /** {@inheritDoc} */
    public void setRegistrationAuthority(String authority) {
        registrationAuthority = prepareForAssignment(registrationAuthority, authority);
    }

    /** {@inheritDoc} */
    public DateTime getRegistrationInstant() {
        return registrationInstant;
    }

    /** {@inheritDoc} */
    public void setRegistrationInstant(DateTime dateTime) {
        registrationInstant = prepareForAssignment(registrationInstant, dateTime);
    }

    /** {@inheritDoc} */
    public List<RegistrationPolicy> getRegistrationPolicies() {
        return registrationPolicies;
    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        ArrayList<XMLObject> children = new ArrayList<XMLObject>();
        children.addAll(registrationPolicies);
        return children;
    }
}
