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

package org.opensaml.xmlsec;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotLive;
import net.shibboleth.utilities.java.support.annotation.constraint.Unmodifiable;

import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;


/**
 * The configuration information to use when generating an XML signature.
 */
public interface SignatureSigningConfiguration extends WhitelistBlacklistConfiguration {
    
    /**
     * Get the list of signing credentials to use when signing, in preference order.
     * 
     * @return the list of signing credentials, may be empty
     */
    @Nonnull @NonnullElements @Unmodifiable @NotLive public List<Credential> getSigningCredentials();
    
    /**
     * Get the list of preferred signature algorithm URIs, in preference order.
     * 
     * @return the list of algorithm URIs, may be empty
     */
    @Nonnull @NonnullElements @Unmodifiable @NotLive public List<String> getSignatureAlgorithms();
    
    /**
     * Get the list of digest method algorithm URIs suitable for use as a Signature Reference DigestMethod value,
     * in preference order.
     * 
     * @return a digest method algorithm URI
     */
    @Nonnull @NonnullElements @Unmodifiable @NotLive public List<String> getSignatureReferenceDigestMethods();
    
    /**
     * Get a canonicalization algorithm URI suitable for use as a Signature CanonicalizationMethod value.
     * 
     * @return a canonicalization algorithm URI
     */
    @Nullable public String getSignatureCanonicalizationAlgorithm();
    
    /**
     * Get the value to be used as the Signature SignatureMethod HMACOutputLength value, used
     * only when signing with an HMAC algorithm.  This value is optional when using HMAC.
     * 
     * @return the configured HMAC output length value
     */
    @Nullable public Integer getSignatureHMACOutputLength();
    
    /**
     * Get the manager for named KeyInfoGenerator instances.
     * 
     * @return the KeyInfoGenerator manager, or null if none is configured
     */
    @Nullable public NamedKeyInfoGeneratorManager getKeyInfoGeneratorManager();

}