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

package org.opensaml.saml.metadata.resolver.filter.impl;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.metadata.EntityGroupName;
import org.opensaml.saml.metadata.resolver.filter.FilterException;
import org.opensaml.saml.metadata.resolver.filter.MetadataNodeProcessor;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A metadata node processor implementation which attaches an instance of
 * {@link EntityGroupName} to an {@link EntityDescriptor} for
 * each ancestor {@link EntitiesDescriptor} in the metadata tree.
 */
public class EntitiesDescriptorNameProcessor implements MetadataNodeProcessor {
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(EntitiesDescriptorNameProcessor.class);

    /** {@inheritDoc} */
    @Override
    public void process(XMLObject metadataNode) throws FilterException {
        if (metadataNode instanceof EntityDescriptor) {
            XMLObject currentParent = metadataNode.getParent();
            while (currentParent != null) {
                if (currentParent instanceof EntitiesDescriptor) {
                    String name = StringSupport.trimOrNull(((EntitiesDescriptor)currentParent).getName());
                    if (name != null) {
                        if (log.isDebugEnabled()) {
                            log.debug("Attaching EntityGroupName '{}' to EntityDescriptor: {}", 
                                    name, ((EntityDescriptor)metadataNode).getEntityID());
                        }
                        metadataNode.getObjectMetadata().put(new EntityGroupName(name));
                    }
                }
                currentParent = currentParent.getParent();
            }
        }
    }

}