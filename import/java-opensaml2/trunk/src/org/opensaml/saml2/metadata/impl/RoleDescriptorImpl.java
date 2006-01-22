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
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import org.opensaml.common.SAMLObject;
import org.opensaml.common.impl.AbstractSignableSAMLObject;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.Extensions;
import org.opensaml.saml2.metadata.ContactPerson;
import org.opensaml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml2.metadata.Organization;
import org.opensaml.saml2.metadata.RoleDescriptor;
import org.opensaml.xml.util.DatatypeHelper;
import org.opensaml.xml.util.XMLObjectChildrenList;

/**
 * Concretate implementation of {@link org.opensaml.saml2.metadata.RoleDescriptor}
 */
public abstract class RoleDescriptorImpl extends AbstractSignableSAMLObject implements RoleDescriptor {
    
    /** validUntil attribute */
    private GregorianCalendar validUntil;

    /** cacheDurection attribute */
    private Long cacheDuration;

    /** Set of supported protocols */
    private ArrayList<String> supportedProtocols = new ArrayList<String>();
    
    /** Error URL */
    private String errorURL;
    
    /** Extensions child */
    private Extensions extensions;
    
    /** Organization administering this role */
    private Organization organization;
    
    /** Contact persons for this role */
    private XMLObjectChildrenList<ContactPerson> contactPersons;
    
    /** Key descriptors for this role */
    private XMLObjectChildrenList<KeyDescriptor> keyDescriptors;
    
    /**
     * Constructor
     * 
     * @param namespaceURI the namespace URI of the element this saml object represents
     * @param localName the local name of the element this SAML object represents
     */
    public RoleDescriptorImpl(String namespaceURI, String localName) {
        super(SAMLConstants.SAML20MD_NS, localName);
        setElementNamespacePrefix(SAMLConstants.SAML20MD_PREFIX);
        contactPersons = new XMLObjectChildrenList<ContactPerson>(this);
        keyDescriptors = new XMLObjectChildrenList<KeyDescriptor>(this);
    }
    
    /*
     * @see org.opensaml.saml2.common.TimeBoundSAMLObject#isValid()
     */
    public boolean isValid() {
        return validUntil.before(GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC")));
    }

    /*
     * @see org.opensaml.saml2.common.TimeBoundSAMLObject#getValidUntil()
     */
    public GregorianCalendar getValidUntil() {
        return validUntil;
    }

    /*
     * @see org.opensaml.saml2.common.TimeBoundSAMLObject#setValidUntil(java.util.GregorianCalendar)
     */
    public void setValidUntil(GregorianCalendar validUntil) {
        this.validUntil = prepareForAssignment(this.validUntil, validUntil);
    }

    /*
     * @see org.opensaml.saml2.common.CacheableSAMLObject#getCacheDuration()
     */
    public Long getCacheDuration() {
        return cacheDuration;
    }

    /*
     * @see org.opensaml.saml2.common.CacheableSAMLObject#setCacheDuration(java.lang.Long)
     */
    public void setCacheDuration(Long duration) {
        cacheDuration = prepareForAssignment(cacheDuration, duration);
    }
    
    /*
     * @see org.opensaml.saml2.metadata.RoleDescriptor#getSupportedProtocols()
     */
    public List<String> getSupportedProtocols() {
        return Collections.unmodifiableList(supportedProtocols);
    }

    /*
     * @see org.opensaml.saml2.metadata.RoleDescriptor#isSupportedProtocol(java.lang.String)
     */
    public boolean isSupportedProtocol(String protocol) {
        return supportedProtocols.contains(protocol);
    }

    /*
     * @see org.opensaml.saml2.metadata.RoleDescriptor#addSupportedProtocol(java.lang.String)
     */
    public void addSupportedProtocol(String protocol) {
        protocol = DatatypeHelper.safeTrimOrNullString(protocol);
        if(protocol != null && !supportedProtocols.contains(protocol)) {
            releaseThisandParentDOM();
            supportedProtocols.add(protocol);
        }
    }

    /*
     * @see org.opensaml.saml2.metadata.RoleDescriptor#removeProtocol(java.net.URI)
     */
    public void removeSupportedProtocol(String protocol) {
        protocol = DatatypeHelper.safeTrimOrNullString(protocol);
        if(protocol != null && supportedProtocols.contains(protocol)) {
            releaseThisandParentDOM();
            supportedProtocols.remove(protocol);
        }
    }

    /*
     * @see org.opensaml.saml2.metadata.RoleDescriptor#removeProtocols(java.util.Set)
     */
    public void removeSupportedProtocols(Collection<String> protocols) {
       for(String protocol : protocols) {
           removeSupportedProtocol(protocol);
       }
    }

    /*
     * @see org.opensaml.saml2.metadata.RoleDescriptor#removeAllProtocols()
     */
    public void removeAllSupportedProtocols() {
        for(String protocol : supportedProtocols) {
            removeSupportedProtocol(protocol);
        }
    }

    /*
     * @see org.opensaml.saml2.metadata.RoleDescriptor#getErrorURL()
     */
    public String getErrorURL() {
        return errorURL;
    }

    /*
     * @see org.opensaml.saml2.metadata.RoleDescriptor#setErrorURL(java.lang.String)
     */
    public void setErrorURL(String errorURL) {
        
        this.errorURL = prepareForAssignment(this.errorURL, errorURL);
    }
    
    /*
     * @see org.opensaml.saml2.metadata.RoleDescriptor#getExtensions()
     */
    public Extensions getExtensions() {
        return extensions;
    }

    /*
     * @see org.opensaml.saml2.metadata.RoleDescriptor#setExtensions(org.opensaml.saml2.core.Extensions)
     */
    public void setExtensions(Extensions extensions) throws IllegalArgumentException {
        this.extensions = prepareForAssignment(this.extensions, extensions);
    }


    /*
     * @see org.opensaml.saml2.metadata.RoleDescriptor#getOrganization()
     */
    public Organization getOrganization() {
        return organization;
    }

    /*
     * @see org.opensaml.saml2.metadata.RoleDescriptor#setOrganization(org.opensaml.saml2.metadata.Organization)
     */
    public void setOrganization(Organization organization) throws IllegalArgumentException{
        this.organization = prepareForAssignment(this.organization, organization);
    }

    /*
     * @see org.opensaml.saml2.metadata.EntityDescriptor#getContactPersons()
     */
    public List<ContactPerson> getContactPersons() {
        return contactPersons;
    }

    /*
     * @see org.opensaml.saml2.metadata.KeyDescriptorDescriptorComp#getKeyDescriptors()
     */
    public List<KeyDescriptor> getKeyDescriptors() {
        return keyDescriptors;
    }
    
    /*
     * @see org.opensaml.common.SAMLObject#getOrderedChildren()
     */
    public List<SAMLObject> getOrderedChildren() {
        ArrayList<SAMLObject> children = new ArrayList<SAMLObject>();
        
        children.add(getExtensions());
        children.addAll(getKeyDescriptors());        
        children.add(getOrganization());
        children.addAll(getContactPersons());
        
        return Collections.unmodifiableList(children);
    }
}