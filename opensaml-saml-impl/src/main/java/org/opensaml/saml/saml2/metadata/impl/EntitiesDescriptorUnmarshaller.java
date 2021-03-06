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

package org.opensaml.saml.saml2.metadata.impl;

import net.shibboleth.utilities.java.support.xml.DOMTypeSupport;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import org.opensaml.saml.saml2.common.CacheableSAMLObject;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.common.TimeBoundSAMLObject;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.xmlsec.signature.Signature;
import org.w3c.dom.Attr;

import com.google.common.base.Strings;

/**
 * A thread safe Unmarshaller for {@link org.opensaml.saml.saml2.metadata.EntitiesDescriptor} objects.
 */
public class EntitiesDescriptorUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processChildElement(XMLObject parentSAMLObject, XMLObject childSAMLObject)
            throws UnmarshallingException {
        EntitiesDescriptor entitiesDescriptor = (EntitiesDescriptor) parentSAMLObject;

        if (childSAMLObject instanceof Extensions) {
            entitiesDescriptor.setExtensions((Extensions) childSAMLObject);
        } else if (childSAMLObject instanceof EntitiesDescriptor) {
            entitiesDescriptor.getEntitiesDescriptors().add((EntitiesDescriptor) childSAMLObject);
        } else if (childSAMLObject instanceof EntityDescriptor) {
            entitiesDescriptor.getEntityDescriptors().add((EntityDescriptor) childSAMLObject);
        } else if (childSAMLObject instanceof Signature) {
            entitiesDescriptor.setSignature((Signature) childSAMLObject);
        } else {
            super.processChildElement(parentSAMLObject, childSAMLObject);
        }
    }

    /** {@inheritDoc} */
    protected void processAttribute(XMLObject samlObject, Attr attribute) throws UnmarshallingException {
        EntitiesDescriptor entitiesDescriptor = (EntitiesDescriptor) samlObject;

        if (attribute.getLocalName().equals(EntitiesDescriptor.ID_ATTRIB_NAME)) {
            entitiesDescriptor.setID(attribute.getValue());
            attribute.getOwnerElement().setIdAttributeNode(attribute, true);
        } else if (attribute.getLocalName().equals(TimeBoundSAMLObject.VALID_UNTIL_ATTRIB_NAME)
                && !Strings.isNullOrEmpty(attribute.getValue())) {
            entitiesDescriptor.setValidUntil(new DateTime(attribute.getValue(), ISOChronology.getInstanceUTC()));
        } else if (attribute.getLocalName().equals(CacheableSAMLObject.CACHE_DURATION_ATTRIB_NAME)) {
            entitiesDescriptor.setCacheDuration(new Long(DOMTypeSupport.durationToLong(attribute.getValue())));
        } else if (attribute.getLocalName().equals(EntitiesDescriptor.NAME_ATTRIB_NAME)) {
            entitiesDescriptor.setName(attribute.getValue());
        } else {
            super.processAttribute(samlObject, attribute);
        }
    }
}