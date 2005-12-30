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

import javax.xml.namespace.QName;

import org.opensaml.common.SAMLObject;
import org.opensaml.common.impl.AbstractSignableSAMLObject;
import org.opensaml.common.impl.TypeNameIndexedSAMLObjectList;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.Extensions;
import org.opensaml.saml2.metadata.AdditionalMetadataLocation;
import org.opensaml.saml2.metadata.AffiliationDescriptor;
import org.opensaml.saml2.metadata.AttributeAuthorityDescriptor;
import org.opensaml.saml2.metadata.AuthnAuthorityDescriptor;
import org.opensaml.saml2.metadata.ContactPerson;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml2.metadata.Organization;
import org.opensaml.saml2.metadata.PDPDescriptor;
import org.opensaml.saml2.metadata.RoleDescriptor;
import org.opensaml.saml2.metadata.SPSSODescriptor;
import org.opensaml.xml.IllegalAddException;

/**
 * Concretate implementation of {@link org.opensaml.saml2.metadata.EntitiesDescriptor}
 */
public class EntityDescriptorImpl extends AbstractSignableSAMLObject implements EntityDescriptor {

    /** Entity ID of this Entity */
    private String entityID;
    
    /** validUntil attribute */
    private GregorianCalendar validUntil;
    
    /** cacheDurection attribute */
    private Long cacheDuration;
    
    /** Extensions child */
    private Extensions extensions;

    /** Role descriptors for this entity */
    private TypeNameIndexedSAMLObjectList<RoleDescriptor> roleDescriptors = new TypeNameIndexedSAMLObjectList<RoleDescriptor>();

    /** Affiliatition descriptor for this entity */
    private AffiliationDescriptor affiliationDescriptor;

    /** Organization the administers this entity */
    private Organization organization;

    /** Contact persons for this entity */
    private ArrayList<ContactPerson> contactPersons = new ArrayList<ContactPerson>();

    /** Additional metadata locations for this entity */
    private ArrayList<AdditionalMetadataLocation> additionalMetadata = new ArrayList<AdditionalMetadataLocation>();

    public EntityDescriptorImpl() {
        super(SAMLConstants.SAML20MD_NS, EntityDescriptor.LOCAL_NAME);
        setElementNamespacePrefix(SAMLConstants.SAML20MD_PREFIX);
    }

    /*
     * @see org.opensaml.saml2.metadata.EntityDescriptor#getEntityID()
     */
    public String getEntityID() {
        return entityID;
    }

    /*
     * @see org.opensaml.saml2.metadata.EntityDescriptor#setEntityID(java.lang.String)
     */
    public void setEntityID(String id) {
        entityID = prepareForAssignment(entityID, id);
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
     * @see org.opensaml.saml2.metadata.EntityDescriptor#getExtensions()
     */
    public Extensions getExtensions() {
        return extensions;
    }

    /*
     * @see org.opensaml.saml2.metadata.EntityDescriptor#setExtensions(org.opensaml.saml2.core.Extensions)
     */
    public void setExtensions(Extensions extensions) throws IllegalAddException {
        this.extensions = prepareForAssignment(this.extensions, extensions);
    }

    /*
     * @see org.opensaml.saml2.metadata.EntityDescriptor#getRoleDescriptors()
     */
    public List<RoleDescriptor> getRoleDescriptors() {
        return Collections.unmodifiableList(roleDescriptors);
    }

    /*
     * @see org.opensaml.saml2.metadata.EntityDescriptor#getRoleDescriptors(javax.xml.namespace.QName)
     */
    public List<RoleDescriptor> getRoleDescriptors(QName type) {
        return Collections.unmodifiableList(roleDescriptors.get(type));
    }

    /*
     * @see org.opensaml.saml2.metadata.EntityDescriptor#getRoleDescriptors(javax.xml.namespace.QName, java.lang.String)
     */
    public RoleDescriptor getRoleDescriptors(QName type, String protocol) {
        for (RoleDescriptor descriptor : getRoleDescriptors(type)) {
            if (descriptor.isSupportedProtocol(protocol)) {
                return descriptor;
            }
        }

        return null;
    }

    /*
     * @see org.opensaml.saml2.metadata.EntityDescriptor#addRoleDescriptor(org.opensaml.saml2.metadata.RoleDescriptor)
     */
    public void addRoleDescriptor(RoleDescriptor descriptor) throws IllegalAddException {
        addXMLObject(roleDescriptors, descriptor);
    }

    /*
     * @see org.opensaml.saml2.metadata.EntityDescriptor#removeRoleDescriptor(org.opensaml.saml2.metadata.RoleDescriptor)
     */
    public void removeRoleDescriptor(RoleDescriptor descriptor) {
        removeXMLObject(roleDescriptors, descriptor);
    }

    /*
     * @see org.opensaml.saml2.metadata.EntityDescriptor#removeRoleDescriptors(java.util.Set)
     */
    public void removeRoleDescriptors(Collection<RoleDescriptor> descriptors) {
        for (RoleDescriptor descriptor : descriptors) {
            removeRoleDescriptor(descriptor);
        }
    }

    /*
     * @see org.opensaml.saml2.metadata.EntityDescriptor#removeAllRoleDescriptors()
     */
    public void removeAllRoleDescriptors() {
        for (RoleDescriptor descriptor : roleDescriptors) {
            removeRoleDescriptor(descriptor);
        }
    }
    
    /*
     * @see org.opensaml.saml2.metadata.EntityDescriptor#removeAllRoleDescriptors(javax.xml.namespace.QName)
     */
    public void removeAllRoleDescriptors(QName typeOrName){
        for (RoleDescriptor descriptor : roleDescriptors.get(typeOrName)) {
            removeRoleDescriptor(descriptor);
        }
    }
    
    /*
     * @see org.opensaml.saml2.metadata.EntityDescriptor#getIDPSSODescriptor()
     */
    public List<RoleDescriptor> getIDPSSODescriptor(){
        QName descriptorQName = new QName(SAMLConstants.SAML20MD_NS, IDPSSODescriptor.LOCAL_NAME);
        return Collections.unmodifiableList(roleDescriptors.get(descriptorQName));
    }
    
    /*
     * @see org.opensaml.saml2.metadata.EntityDescriptor#getSPSSODescriptor()
     */
    public List<RoleDescriptor> getSPSSODescriptor(){
        QName descriptorQName = new QName(SAMLConstants.SAML20MD_NS, SPSSODescriptor.LOCAL_NAME);
        return Collections.unmodifiableList(roleDescriptors.get(descriptorQName));
    }
    
    /*
     * @see org.opensaml.saml2.metadata.EntityDescriptor#getAuthnAuthorityDescriptor()
     */
    public List<RoleDescriptor> getAuthnAuthorityDescriptor(){
        QName descriptorQName = new QName(SAMLConstants.SAML20MD_NS, AuthnAuthorityDescriptor.LOCAL_NAME);
        return Collections.unmodifiableList(roleDescriptors.get(descriptorQName));
    }
    
    /*
     * @see org.opensaml.saml2.metadata.EntityDescriptor#getAttributeAuthorityDescriptor()
     */
    public List<RoleDescriptor> getAttributeAuthorityDescriptor(){
        QName descriptorQName = new QName(SAMLConstants.SAML20MD_NS, AttributeAuthorityDescriptor.LOCAL_NAME);
        return Collections.unmodifiableList(roleDescriptors.get(descriptorQName));
    }
    
    /*
     * @see org.opensaml.saml2.metadata.EntityDescriptor#getPDPDescriptor()
     */
    public List<RoleDescriptor> getPDPDescriptor(){
        QName descriptorQName = new QName(SAMLConstants.SAML20MD_NS, PDPDescriptor.LOCAL_NAME);
        return Collections.unmodifiableList(roleDescriptors.get(descriptorQName));
    }

    /*
     * @see org.opensaml.saml2.metadata.EntityDescriptor#getAffiliationDescriptor()
     */
    public AffiliationDescriptor getAffiliationDescriptor() {
        return affiliationDescriptor;
    }

    /*
     * @see org.opensaml.saml2.metadata.EntityDescriptor#setAffiliationDescriptor(org.opensaml.saml2.metadata.AffiliationDescriptor)
     */
    public void setAffiliationDescriptor(AffiliationDescriptor descriptor) throws IllegalAddException {
        affiliationDescriptor = prepareForAssignment(affiliationDescriptor, descriptor);
    }

    /*
     * @see org.opensaml.saml2.metadata.EntityDescriptor#getOrganization()
     */
    public Organization getOrganization() {
        return organization;
    }

    /*
     * @see org.opensaml.saml2.metadata.EntityDescriptor#setOrganization(org.opensaml.saml2.metadata.Organization)
     */
    public void setOrganization(Organization organization) throws IllegalAddException {
        organization = prepareForAssignment(this.organization, organization);
    }

    /*
     * @see org.opensaml.saml2.metadata.EntityDescriptor#getContactPersons()
     */
    public List<ContactPerson> getContactPersons() {
        return Collections.unmodifiableList(contactPersons);
    }

    /*
     * @see org.opensaml.saml2.metadata.EntityDescriptor#addContactPerson(org.opensaml.saml2.metadata.ContactPerson)
     */
    public void addContactPerson(ContactPerson person) throws IllegalAddException {
        addXMLObject(contactPersons, person);
    }

    /*
     * @see org.opensaml.saml2.metadata.EntityDescriptor#removeContactPerson(org.opensaml.saml2.metadata.ContactPerson)
     */
    public void removeContactPerson(ContactPerson person) {
        removeXMLObject(contactPersons, person);
    }

    /*
     * @see org.opensaml.saml2.metadata.EntityDescriptor#removeContactPersons(java.util.Set)
     */
    public void removeContactPersons(Collection<ContactPerson> persons) {
        removeXMLObjects(contactPersons, persons);
    }

    /*
     * @see org.opensaml.saml2.metadata.EntityDescriptor#removeAllContactPersons()
     */
    public void removeAllContactPersons() {
        for (ContactPerson person : contactPersons) {
            removeContactPerson(person);
        }
    }

    /*
     * @see org.opensaml.saml2.metadata.EntityDescriptor#getAdditionalMetadataLocations()
     */
    public List<AdditionalMetadataLocation> getAdditionalMetadataLocations() {
        return Collections.unmodifiableList(additionalMetadata);
    }

    /*
     * @see org.opensaml.saml2.metadata.EntityDescriptor#addAdditionalMetadataLocation(org.opensaml.saml2.metadata.AdditionalMetadataLocation)
     */
    public void addAdditionalMetadataLocation(AdditionalMetadataLocation location) throws IllegalAddException {
        addXMLObject(additionalMetadata, location);
    }

    /*
     * @see org.opensaml.saml2.metadata.EntityDescriptor#removeAdditionalMetadataLocation(org.opensaml.saml2.metadata.AdditionalMetadataLocation)
     */
    public void removeAdditionalMetadataLocation(AdditionalMetadataLocation location) {
        removeXMLObject(additionalMetadata, location);
    }

    /*
     * @see org.opensaml.saml2.metadata.EntityDescriptor#removeAdditionalMetadataLocations(java.util.Set)
     */
    public void removeAdditionalMetadataLocations(Collection<AdditionalMetadataLocation> locations) {
        removeXMLObjects(additionalMetadata, locations);
    }

    /*
     * @see org.opensaml.saml2.metadata.EntityDescriptor#removeAllAdditionalMetadataLocation()
     */
    public void removeAllAdditionalMetadataLocation() {
        for (AdditionalMetadataLocation location : additionalMetadata) {
            removeAdditionalMetadataLocation(location);
        }
    }

    /*
     * @see org.opensaml.saml2.common.impl.AbstractSAMLElement#getOrderedChildren()
     */
    public List<SAMLObject> getOrderedChildren() {
        ArrayList<SAMLObject> children = new ArrayList<SAMLObject>();
        
        children.add(getExtensions());
        children.addAll(roleDescriptors);
        children.add(getAffiliationDescriptor());
        children.add(getOrganization());
        children.addAll(contactPersons);
        children.addAll(additionalMetadata);

        return Collections.unmodifiableList(children);
    }
}