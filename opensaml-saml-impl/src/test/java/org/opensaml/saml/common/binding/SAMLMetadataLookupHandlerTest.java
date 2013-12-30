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

package org.opensaml.saml.common.binding;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.logic.ConstraintViolationException;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLProtocolContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.metadata.resolver.RoleDescriptorResolver;
import org.opensaml.saml.metadata.resolver.impl.BasicRoleDescriptorResolver;
import org.opensaml.saml.metadata.resolver.impl.FilesystemMetadataResolver;
import org.opensaml.saml.saml1.core.AttributeQuery;
import org.opensaml.saml.saml1.core.Request;
import org.opensaml.saml.saml1.profile.SAML1ActionTestingSupport;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test for {@link SAMLMetadataLookupHandler}.
 */
public class SAMLMetadataLookupHandlerTest extends XMLObjectBaseTestCase {

    private RoleDescriptorResolver roleResolver;
    private SAMLMetadataLookupHandler handler;
    private MessageContext<SAMLObject> messageContext;
    
    @BeforeClass
    public void classSetUp() throws ResolverException, URISyntaxException, ComponentInitializationException {
        final URL mdURL = SAMLMetadataLookupHandlerTest.class
                .getResource("/data/org/opensaml/saml/saml2/metadata/InCommon-metadata.xml");
        final File mdFile = new File(mdURL.toURI());

        final FilesystemMetadataResolver metadataProvider = new FilesystemMetadataResolver(mdFile);
        metadataProvider.setParserPool(parserPool);
        metadataProvider.initialize();
        
        roleResolver = new BasicRoleDescriptorResolver(metadataProvider);
        roleResolver.initialize();
    }
    
    @BeforeMethod
    public void setUp() {

        handler = new SAMLMetadataLookupHandler();
        messageContext = new MessageContext<SAMLObject>();
    }
    
    @Test
    public void testConfigFailure() {
        try {
            handler.initialize();
            Assert.fail();
        } catch (ComponentInitializationException e) {
            
        }
        
        try {
            handler.setRoleDescriptorResolver(null);
            Assert.fail();
        } catch (ConstraintViolationException e) {
            
        }
    }
    
    @Test
    public void testMissingContexts() throws ComponentInitializationException, MessageHandlerException {
        handler.setRoleDescriptorResolver(roleResolver);
        handler.initialize();
        
        handler.invoke(messageContext);
        Assert.assertNull(messageContext.getSubcontext(SAMLMetadataContext.class, false));
        
        messageContext.getSubcontext(SAMLPeerEntityContext.class, true).setRole(
                org.opensaml.saml.saml2.metadata.SPSSODescriptor.DEFAULT_ELEMENT_NAME);
        
        handler.invoke(messageContext);
        Assert.assertNull(messageContext.getSubcontext(SAMLMetadataContext.class, false));
    }
    
    @Test
    public void testNotFound() throws ComponentInitializationException, MessageHandlerException {
        handler.setRoleDescriptorResolver(roleResolver);
        handler.initialize();
        
        messageContext.getSubcontext(SAMLPeerEntityContext.class, true).setRole(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        
        Request request = SAML1ActionTestingSupport.buildAttributeQueryRequest(null);
        ((AttributeQuery) request.getQuery()).setResource("urn:notfound");
        messageContext.setMessage(request);

        handler.invoke(messageContext);
        Assert.assertNull(messageContext.getSubcontext(SAMLMetadataContext.class, false));
    }
 
    @Test
    public void testBadRole() throws ComponentInitializationException, MessageHandlerException {
        handler.setRoleDescriptorResolver(roleResolver);
        handler.initialize();
        
        messageContext.getSubcontext(SAMLPeerEntityContext.class, true).setRole(SPSSODescriptor.DEFAULT_ELEMENT_NAME);
        
        Request request = SAML1ActionTestingSupport.buildAttributeQueryRequest(null);
        ((AttributeQuery) request.getQuery()).setResource("urn:mace:incommon:osu.edu");
        messageContext.setMessage(request);

        handler.invoke(messageContext);
        Assert.assertNull(messageContext.getSubcontext(SAMLMetadataContext.class, false));
    }

    @Test
    public void testBadProtocol() throws ComponentInitializationException, MessageHandlerException {
        handler.setRoleDescriptorResolver(roleResolver);
        handler.initialize();
        
        messageContext.getSubcontext(SAMLPeerEntityContext.class, true).setRole(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        messageContext.getSubcontext(SAMLProtocolContext.class, true).setProtocol("urn:foo");
        
        Request request = SAML1ActionTestingSupport.buildAttributeQueryRequest(null);
        ((AttributeQuery) request.getQuery()).setResource("urn:mace:incommon:osu.edu");
        messageContext.setMessage(request);

        handler.invoke(messageContext);
        Assert.assertNull(messageContext.getSubcontext(SAMLMetadataContext.class, false));
    }
    
    @Test
    public void testSuccess() throws ComponentInitializationException, MessageHandlerException {
        handler.setRoleDescriptorResolver(roleResolver);
        handler.initialize();
        
        messageContext.getSubcontext(SAMLPeerEntityContext.class, true).setRole(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        messageContext.getSubcontext(SAMLProtocolContext.class, true).setProtocol(SAMLConstants.SAML11P_NS);
        
        Request request = SAML1ActionTestingSupport.buildAttributeQueryRequest(null);
        ((AttributeQuery) request.getQuery()).setResource("urn:mace:incommon:osu.edu");
        messageContext.setMessage(request);

        handler.invoke(messageContext);
        
        SAMLMetadataContext mdCtx = messageContext.getSubcontext(SAMLMetadataContext.class, false);
        Assert.assertNotNull(mdCtx);
        Assert.assertNotNull(mdCtx.getRoleDescriptor());
        Assert.assertNotNull(mdCtx.getEntityDescriptor());
    }
    
}