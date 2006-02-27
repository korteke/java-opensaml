/*
 * Copyright [2005] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.saml2.metadata.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.metadata.AssertionIDRequestService;
import org.opensaml.saml2.metadata.AttributeAuthorityDescriptor;
import org.opensaml.saml2.metadata.AttributeProfile;
import org.opensaml.saml2.metadata.AttributeService;
import org.opensaml.saml2.metadata.NameIDFormat;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.util.XMLObjectChildrenList;

/**
 * A concrete implementation of {@link org.opensaml.saml2.metadata.AttributeAuthorityDescriptor}.
 */
public class AttributeAuthorityDescriptorImpl extends RoleDescriptorImpl implements AttributeAuthorityDescriptor {
    
    /** Attribte query endpoints */
    private XMLObjectChildrenList<AttributeService> attributeServices;
    
    /** Assertion request endpoints */
    private XMLObjectChildrenList<AssertionIDRequestService> assertionIDRequestServices;
    
    /** Supported NameID formats */
    private XMLObjectChildrenList<NameIDFormat> nameFormats;
    
    /** Supported attribute profiles */
    private XMLObjectChildrenList<AttributeProfile> attributeProfiles;
    
    /** Supported attribute */
    private XMLObjectChildrenList<Attribute> attributes;
    
    /**
     * Constructor
     */
    public AttributeAuthorityDescriptorImpl(){
        super(AttributeAuthorityDescriptor.LOCAL_NAME);
        
        attributeServices = new XMLObjectChildrenList<AttributeService>(this);
        assertionIDRequestServices = new XMLObjectChildrenList<AssertionIDRequestService>(this);
        nameFormats = new XMLObjectChildrenList<NameIDFormat>(this);
        attributes = new XMLObjectChildrenList<Attribute>(this);
    }
    
    /*
     * @see org.opensaml.saml2.metadata.AttributeAuthorityDescriptor#getAttributeServices()
     */
    public List<AttributeService> getAttributeServices() {
        return attributeServices;
    }

    /*
     * @see org.opensaml.saml2.metadata.AssertionIDRequestDescriptorComp#getAssertionIDRequestServices()
     */
    public List<AssertionIDRequestService> getAssertionIDRequestServices() {
        return assertionIDRequestServices;
    }

    /*
     * @see org.opensaml.saml2.metadata.NameIDFormatDescriptorComp#getNameIDFormats()
     */
    public List<NameIDFormat> getNameIDFormats() {
        return nameFormats;
    }

    /*
     * @see org.opensaml.saml2.metadata.AttributeProfileDescriptorComp#getAttributeProfiles()
     */
    public List<AttributeProfile> getAttributeProfiles() {
        return attributeProfiles;
    }

    /*
     * @see org.opensaml.saml2.metadata.AttributeDescriptorComp#getAttributes()
     */
    public List<Attribute> getAttributes() {
        return attributes;
    }
    
    /*
     * @see org.opensaml.xml.XMLObject#getOrderedChildren()
     */
    public List<XMLObject> getOrderedChildren() {
        ArrayList<XMLObject> children = new ArrayList<XMLObject>();
        
        children.addAll(super.getOrderedChildren());
        children.addAll(attributeServices);
        children.addAll(assertionIDRequestServices);
        children.addAll(nameFormats);
        children.addAll(attributeProfiles);
        children.addAll(attributes);
        
        return Collections.unmodifiableList(children);
    }
}