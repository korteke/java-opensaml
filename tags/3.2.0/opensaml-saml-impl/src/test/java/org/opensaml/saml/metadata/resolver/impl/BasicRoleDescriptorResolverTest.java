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

package org.opensaml.saml.metadata.resolver.impl;

import java.util.Collections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.criterion.ProtocolCriterion;
import org.opensaml.saml.ext.saml2mdquery.AttributeQueryDescriptorType;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilter;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 */
public class BasicRoleDescriptorResolverTest extends XMLObjectBaseTestCase {
    
    private BasicRoleDescriptorResolver roleResolver;
    
    @BeforeMethod
    public void setUp() throws ComponentInitializationException {
        final EntityDescriptor entityDescriptor = buildTestDescriptor();
        
        MetadataResolver metadataResolver = new MetadataResolver() {
            
            @Nullable public String getId() { return "foo"; }
            
            @Nullable
            public EntityDescriptor resolveSingle(CriteriaSet criteria) throws ResolverException {
                return entityDescriptor;
            }
            
            @Nonnull
            public Iterable<EntityDescriptor> resolve(CriteriaSet criteria) throws ResolverException {
                return Collections.singletonList(entityDescriptor);
            }

            public boolean isInitialized() {
                return true;
            }

            public void initialize() throws ComponentInitializationException { }

            public boolean isDestroyed() {
                return false;
            }

            public void destroy() { }

            public boolean isRequireValidMetadata() {
                return false;
            }

            public void setRequireValidMetadata(boolean requireValidMetadata) { }

            public MetadataFilter getMetadataFilter() {
                return null;
            }

            public void setMetadataFilter(MetadataFilter newFilter) { }
        };
        
        roleResolver = new BasicRoleDescriptorResolver(metadataResolver);
        roleResolver.initialize();
    }
    
    @Test
    public void testResolveSingleNoProtocol() throws ResolverException {
        RoleDescriptor roleDescriptor = roleResolver.resolveSingle(new CriteriaSet(
                new EntityIdCriterion("http://www.example.org"), 
                new EntityRoleCriterion(SPSSODescriptor.DEFAULT_ELEMENT_NAME)));
        Assert.assertNotNull(roleDescriptor, "Resolved RoleDescriptor was null");
        Assert.assertEquals(SPSSODescriptor.DEFAULT_ELEMENT_NAME, roleDescriptor.getElementQName(),
                "Saw incorrect role type");
    }
    
    @Test
    public void testResolveMultiNoProtocol() throws ResolverException {
        Iterable<RoleDescriptor> roleDescriptors = roleResolver.resolve(new CriteriaSet(
                new EntityIdCriterion("http://www.example.org"), 
                new EntityRoleCriterion(SPSSODescriptor.DEFAULT_ELEMENT_NAME)));
       Assert.assertNotNull(roleDescriptors, "Resolved RoleDescriptor iterable was null");
       
       int count = 0;
       for (RoleDescriptor roleDescriptor : roleDescriptors) {
            Assert.assertEquals(SPSSODescriptor.DEFAULT_ELEMENT_NAME, roleDescriptor.getElementQName(),
                    "Saw incorrect role type");
           count++;
       }
       
       Assert.assertEquals(2, count, "Resolved unexpected number of RoleDescriptors");
    }
    
    @Test
    public void testResolveSingleWithProtocol() throws ResolverException {
        RoleDescriptor roleDescriptor = roleResolver.resolveSingle(new CriteriaSet(
                new EntityIdCriterion("http://www.example.org"), 
                new EntityRoleCriterion(SPSSODescriptor.DEFAULT_ELEMENT_NAME),
                new ProtocolCriterion(SAMLConstants.SAML20P_NS)));
        
        Assert.assertNotNull(roleDescriptor, "Resolved RoleDescriptor was null");
        Assert.assertEquals(SPSSODescriptor.DEFAULT_ELEMENT_NAME, roleDescriptor.getElementQName(),
                "Saw incorrect role type");
        Assert.assertTrue(roleDescriptor.getSupportedProtocols().contains(SAMLConstants.SAML20P_NS),
                "Returned RoleDescriptor didn't support specified protocol");
    }
    
    @Test
    public void testResolveMultiWithProtocol() throws ResolverException {
        Iterable<RoleDescriptor> roleDescriptors = roleResolver.resolve(new CriteriaSet(
                new EntityIdCriterion("http://www.example.org"), 
                new EntityRoleCriterion(SPSSODescriptor.DEFAULT_ELEMENT_NAME),
                new ProtocolCriterion(SAMLConstants.SAML20P_NS)));
       Assert.assertNotNull(roleDescriptors, "Resolved RoleDescriptor iterable was null");
       
       int count = 0;
       for (RoleDescriptor roleDescriptor : roleDescriptors) {
            Assert.assertEquals(SPSSODescriptor.DEFAULT_ELEMENT_NAME, roleDescriptor.getElementQName(),
                    "Saw incorrect role type");
            Assert.assertTrue(roleDescriptor.getSupportedProtocols().contains(SAMLConstants.SAML20P_NS),
                    "Returned RoleDescriptor didn't support specified protocol");
            count++;
       }
       
       Assert.assertEquals(1, count, "Resolved unexpected number of RoleDescriptors");
    }
    
    // Helper methods
    
    private EntityDescriptor buildTestDescriptor() {
        EntityDescriptor entityDescriptor = buildXMLObject(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        
        SPSSODescriptor spssoDescriptor1 = buildXMLObject(SPSSODescriptor.DEFAULT_ELEMENT_NAME);
        spssoDescriptor1.addSupportedProtocol(SAMLConstants.SAML11P_NS);
        entityDescriptor.getRoleDescriptors().add(spssoDescriptor1);
        
        AttributeQueryDescriptorType aqDescriptor = buildXMLObject(AttributeQueryDescriptorType.TYPE_NAME);
        aqDescriptor.addSupportedProtocol(SAMLConstants.SAML20P_NS);
        entityDescriptor.getRoleDescriptors().add(aqDescriptor);
        
        SPSSODescriptor spssoDescriptor2 = buildXMLObject(SPSSODescriptor.DEFAULT_ELEMENT_NAME);
        spssoDescriptor2.addSupportedProtocol(SAMLConstants.SAML20P_NS);
        entityDescriptor.getRoleDescriptors().add(spssoDescriptor2);
        
        return entityDescriptor;
    }


}
