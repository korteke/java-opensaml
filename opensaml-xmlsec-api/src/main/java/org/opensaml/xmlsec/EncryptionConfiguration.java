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
import org.opensaml.xmlsec.encryption.support.RSAOAEPParameters;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;


/**
 * The configuration information to use when generating encrypted XML.
 */
public interface EncryptionConfiguration extends WhitelistBlacklistConfiguration {
    
    /**
     * Get the list of data encryption credentials to use, in preference order.
     * 
     * @return the list of encryption credentials, may be empty
     */
    @Nonnull @NonnullElements @Unmodifiable @NotLive public List<Credential> getDataEncryptionCredentials();
    
    /**
     * Get the list of preferred data encryption algorithm URIs, in preference order.
     * 
     * @return the list of algorithm URIs, may be empty
     */
    @Nonnull @NonnullElements @Unmodifiable @NotLive public List<String> getDataEncryptionAlgorithms();
    
    /**
     * Get the list of key transport encryption credentials to use, in preference order.
     * 
     * @return the list of encryption credentials, may be empty
     */
    @Nonnull @NonnullElements @Unmodifiable @NotLive public List<Credential> getKeyTransportEncryptionCredentials();
    
    /**
     * Get the list of preferred key transport encryption algorithm URIs, in preference order.
     * 
     * @return the list of algorithm URIs, may be empty
     */
    @Nonnull @NonnullElements @Unmodifiable @NotLive public List<String> getKeyTransportEncryptionAlgorithms();

    /**
     * Get the KeyInfoGenerator manager to use when generating the EncryptedData/KeyInfo.
     * 
     * @return the KeyInfoGenerator manager instance
     */
    @Nullable public NamedKeyInfoGeneratorManager getDataKeyInfoGeneratorManager();
    
    /**
     * Get the KeyInfoGenerator manager to use when generating the EncryptedKey/KeyInfo.
     * 
     * @return the KeyInfoGenerator manager instance
     */
    @Nullable public NamedKeyInfoGeneratorManager getKeyTransportKeyInfoGeneratorManager();
    
    /**
     * Get the instance of {@link RSAOAEPParameters}.
     * 
     * @return the parameters instance
     */
    @Nullable public RSAOAEPParameters getRSAOAEPParameters();
    
}