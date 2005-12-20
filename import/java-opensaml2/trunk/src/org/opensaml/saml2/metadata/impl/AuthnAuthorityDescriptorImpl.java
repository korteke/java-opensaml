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

import java.util.Collection;

import org.opensaml.common.IllegalAddException;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.util.OrderedSet;
import org.opensaml.common.util.UnmodifiableOrderedSet;
import org.opensaml.saml2.metadata.AuthnAuthorityDescriptor;
import org.opensaml.saml2.metadata.Endpoint;
import org.opensaml.saml2.metadata.NameIDFormat;

/**
 * Concreate implementation of {@link org.opensaml.saml2.metadata.AuthnAuthorityDescriptor}
 */
public class AuthnAuthorityDescriptorImpl extends RoleDescriptorImpl implements AuthnAuthorityDescriptor {

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 422459752889774280L;

    /** AuthnQueryService endpoints */
    private OrderedSet<Endpoint> authnQueryEndpoints = new OrderedSet<Endpoint>();
    
    /** AuthnQueryService endpoints */
    private OrderedSet<Endpoint> assertionIDRequestEndpoints = new OrderedSet<Endpoint>();
    
    /** NameID formats supported by this descriptor */
    private OrderedSet<NameIDFormat> nameIDFormats= new OrderedSet<NameIDFormat>();
    
    /**
     * Constrcutor
     */
    public AuthnAuthorityDescriptorImpl() {
        super();
    }
    
    /*
     * @see org.opensaml.saml2.metadata.AuthnAuthorityDescriptor#getAuthnQueryServices()
     */
    public UnmodifiableOrderedSet<Endpoint> getAuthnQueryServices() {
        return new UnmodifiableOrderedSet<Endpoint>(authnQueryEndpoints);
    }

    /*
     * @see org.opensaml.saml2.metadata.AuthnAuthorityDescriptor#addAuthnQueryService(org.opensaml.saml2.metadata.Endpoint)
     */
    public void addAuthnQueryService(Endpoint service) throws IllegalAddException {
        addSAMLObject(authnQueryEndpoints, service);
    }

    /*
     * @see org.opensaml.saml2.metadata.AuthnAuthorityDescriptor#removeAuthnQueryService(org.opensaml.saml2.metadata.Endpoint)
     */
    public void removeAuthnQueryService(Endpoint service) {
        removeSAMLObject(authnQueryEndpoints, service);
    }

    /*
     * @see org.opensaml.saml2.metadata.AuthnAuthorityDescriptor#removeAuthnQueryServices(java.util.Set)
     */
    public void removeAuthnQueryServices(Collection<Endpoint> services) {
        removeSAMLObjects(authnQueryEndpoints, services);
    }

    /*
     * @see org.opensaml.saml2.metadata.AuthnAuthorityDescriptor#removeAllAuthnQueryServices()
     */
    public void removeAllAuthnQueryServices() {
        for(Endpoint service : authnQueryEndpoints) {
            removeAuthnQueryService(service);
        }
    }

    /*
     * @see org.opensaml.saml2.metadata.AssertionIDRequestDescriptorComp#getAssertionIDRequestServices()
     */
    public UnmodifiableOrderedSet<Endpoint> getAssertionIDRequestServices() {
        return new UnmodifiableOrderedSet<Endpoint>(assertionIDRequestEndpoints);
    }

    /*
     * @see org.opensaml.saml2.metadata.AssertionIDRequestDescriptorComp#addAssertionIDRequestService(org.opensaml.saml2.metadata.Endpoint)
     */
    public void addAssertionIDRequestService(Endpoint service) throws IllegalAddException {
        addSAMLObject(assertionIDRequestEndpoints, service);
    }

    /*
     * @see org.opensaml.saml2.metadata.AssertionIDRequestDescriptorComp#removeAssertionIDRequestService(org.opensaml.saml2.metadata.Endpoint)
     */
    public void removeAssertionIDRequestService(Endpoint service) {
        removeSAMLObject(assertionIDRequestEndpoints, service);
    }

    /*
     * @see org.opensaml.saml2.metadata.AssertionIDRequestDescriptorComp#removeAssertionIDRequestServices(java.util.Set)
     */
    public void removeAssertionIDRequestServices(Collection<Endpoint> services) {
        removeSAMLObjects(assertionIDRequestEndpoints, services);
    }

    /*
     * @see org.opensaml.saml2.metadata.AssertionIDRequestDescriptorComp#removeAllAssertionIDRequestServices()
     */
    public void removeAllAssertionIDRequestServices() {
        for(Endpoint service : assertionIDRequestEndpoints) {
            removeAssertionIDRequestService(service);
        }
    }

    /*
     * @see org.opensaml.saml2.metadata.NameIDFormatDescriptorComp#isSupportedNameIDFormat(java.lang.String)
     */
    public boolean isSupportedNameIDFormat(String format) {
        return nameIDFormats.contains(format);
    }

    /*
     * @see org.opensaml.saml2.metadata.NameIDFormatDescriptorComp#getNameIDFormats()
     */
    public UnmodifiableOrderedSet<NameIDFormat> getNameIDFormats() {
        return new UnmodifiableOrderedSet<NameIDFormat>(nameIDFormats);
    }

    /*
     * @see org.opensaml.saml2.metadata.NameIDFormatDescriptorComp#addNameIDFormat(java.lang.String)
     */
    public void addNameIDFormat(NameIDFormat format) throws IllegalAddException{
        addSAMLObject(nameIDFormats, format);
    }

    /*
     * @see org.opensaml.saml2.metadata.NameIDFormatDescriptorComp#removeNameIDFormat(java.lang.String)
     */
    public void removeNameIDFormat(NameIDFormat format) {
        removeSAMLObject(nameIDFormats, format);
    }

    /*
     * @see org.opensaml.saml2.metadata.NameIDFormatDescriptorComp#removeNameIDFormats(java.util.Set)
     */
    public void removeNameIDFormats(Collection<NameIDFormat> formats) {
        removeSAMLObjects(nameIDFormats, formats);
    }

    /*
     * @see org.opensaml.saml2.metadata.NameIDFormatDescriptorComp#removeAllNameIDFormats()
     */
    public void removeAllNameIDFormats() {
        for(NameIDFormat format : nameIDFormats) {
            removeNameIDFormat(format);
        }
    }
    
    /*
     * @see org.opensaml.common.SAMLObject#getOrderedChildren()
     */
    public UnmodifiableOrderedSet<SAMLObject> getOrderedChildren(){
        OrderedSet<SAMLObject> children = new OrderedSet<SAMLObject>();
        
        children.addAll(super.getOrderedChildren());
        
        for(Endpoint service : authnQueryEndpoints) {
            children.add(service);
        }
        
        for(Endpoint service : assertionIDRequestEndpoints) {
            children.add(service);
        }
        
        for(NameIDFormat nameIDFormat : nameIDFormats) {
            children.add(nameIDFormat);
        }
        
        return new UnmodifiableOrderedSet<SAMLObject>(children);
    }
    
    /*
     * @see org.opensaml.common.SAMLObject#equals(org.opensaml.common.SAMLObject)
     */
    public boolean equals(SAMLObject obj) {
        //TODO
        return false;
    }
}