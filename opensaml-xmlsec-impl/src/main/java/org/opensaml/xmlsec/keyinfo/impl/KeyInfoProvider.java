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

package org.opensaml.xmlsec.keyinfo.impl;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.signature.KeyInfo;

/**
 * Interface for providers used in conjunction with a {@link KeyInfoCredentialResolver} which 
 * support resolving {@link Credential}s based on a child element of {@link KeyInfo}.
 */
public interface KeyInfoProvider {
    
    /**
     * Process a specified KeyInfo child (XMLobject) and attempt to resolve a credential from it.
     * 
     * @param resolver reference to a resolver which is calling the provider
     * @param keyInfoChild the KeyInfo child being processed
     * @param criteriaSet the credential criteria the credential must satisfy
     * @param kiContext the resolution context, used for sharing state amongst resolvers and providers
     * 
     * @return a resolved Credential collection, or null
     * 
     * @throws SecurityException if there is an error during credential resolution.  
     *          Note: failure to resolve a credential is not an error.
     */
    @Nullable public Collection<Credential> process(@Nonnull final KeyInfoCredentialResolver resolver,
            @Nonnull final XMLObject keyInfoChild, @Nullable final CriteriaSet criteriaSet,
            @Nonnull final KeyInfoResolutionContext kiContext) throws SecurityException;
    
    /**
     * Evaluate whether the given provider should attempt to handle resolving a credential
     * from the specified KeyInfo child.
     * 
     * An evaluation of <code>true</code> does not guarantee that a credential can or will be 
     * extracted form the particular KeyInfo child, only that processing should be attempted.
     * 
     * @param keyInfoChild the KeyInfo child object to consider
     * 
     * @return true if the provider should attempt to resolve credentials, false otherwise
     */
    public boolean handles(@Nonnull final XMLObject keyInfoChild);

}