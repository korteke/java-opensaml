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

package org.opensaml.xmlsec.impl;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotLive;
import net.shibboleth.utilities.java.support.annotation.constraint.Unmodifiable;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.EncryptionConfiguration;
import org.opensaml.xmlsec.KeyTransportAlgorithmPredicate;
import org.opensaml.xmlsec.encryption.support.RSAOAEPParameters;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;


/**
 * Basic implementation of {@link EncryptionConfiguration}.
 */
public class BasicEncryptionConfiguration extends BasicWhitelistBlacklistConfiguration 
        implements EncryptionConfiguration {
    
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(BasicEncryptionConfiguration.class);
    
    /** Data encryption credentials. */
    @Nonnull @NonnullElements private List<Credential> dataEncryptionCredentials;
    
    /** Data encryption algorithm URIs. */
    @Nonnull @NonnullElements private List<String> dataEncryptionAlgorithms;

    /** Key transport encryption credentials. */
    @Nonnull @NonnullElements private List<Credential> keyTransportEncryptionCredentials;
    
    /** Key transport encryption algorithm URIs. */
    @Nonnull @NonnullElements private List<String> keyTransportEncryptionAlgorithms;
    
    /** Manager for named KeyInfoGenerator instances for encrypting data. */
    @Nullable private NamedKeyInfoGeneratorManager dataKeyInfoGeneratorManager;
    
    /** Manager for named KeyInfoGenerator instances for encrypting keys. */
    @Nullable private NamedKeyInfoGeneratorManager keyTransportKeyInfoGeneratorManager;
    
    /** RSA OAEP parameters. */
    @Nullable private RSAOAEPParameters rsaOAEPParameters;
    
    /** Flag whether to merge RSA OAEP parameters. */
    private boolean rsaOAEPParametersMerge;
    
    /** Key transport algorithm predicate. */
    @Nullable private KeyTransportAlgorithmPredicate keyTransportPredicate;
    
    //TODO chaining to parent config instance on getters? or use a wrapping proxy, etc?
    
    //TODO update for modern coding conventions, Guava, etc
    
    /** Constructor. */
    public BasicEncryptionConfiguration() {
        super();
        dataEncryptionCredentials = Collections.emptyList();
        dataEncryptionAlgorithms = Collections.emptyList();
        keyTransportEncryptionCredentials = Collections.emptyList();
        keyTransportEncryptionAlgorithms = Collections.emptyList();
        
        rsaOAEPParametersMerge = true;
    }
    
    /** {@inheritDoc} */
    @Override
    @Nonnull @NonnullElements @Unmodifiable @NotLive public List<Credential> getDataEncryptionCredentials() {
        return ImmutableList.copyOf(dataEncryptionCredentials);
    }
    
    /**
     * Set the data encryption credentials to use.
     * 
     * @param credentials the list of data encryption credentials
     */
    public void setDataEncryptionCredentials(@Nullable final List<Credential> credentials) {
        if (credentials == null) {
           dataEncryptionCredentials  = Collections.emptyList();
            return;
        }
        dataEncryptionCredentials = Lists.newArrayList(Collections2.filter(credentials, Predicates.notNull()));
    }
    
    /** {@inheritDoc} */
    @Override
    @Nonnull @NonnullElements @Unmodifiable @NotLive public List<String> getDataEncryptionAlgorithms() {
        return ImmutableList.copyOf(dataEncryptionAlgorithms);
    }
    
    /**
     * Set the data encryption algorithms to use.
     * 
     * @param algorithms the list of algorithms
     */
    public void setDataEncryptionAlgorithms(@Nullable final List<String> algorithms) {
        if (algorithms == null) {
            dataEncryptionAlgorithms = Collections.emptyList();
            return;
        }
        dataEncryptionAlgorithms = Lists.newArrayList(StringSupport.normalizeStringCollection(algorithms));
    }
    
    /** {@inheritDoc} */
    @Override
    @Nonnull @NonnullElements @Unmodifiable @NotLive public List<Credential> getKeyTransportEncryptionCredentials() {
        return ImmutableList.copyOf(keyTransportEncryptionCredentials);
    }
    
    /**
     * Set the key transport encryption credentials to use.
     * 
     * @param credentials the list of key transport encryption credentials
     */
    public void setKeyTransportEncryptionCredentials(@Nullable final List<Credential> credentials) {
        if (credentials == null) {
           keyTransportEncryptionCredentials  = Collections.emptyList();
            return;
        }
        keyTransportEncryptionCredentials = Lists.newArrayList(Collections2.filter(credentials, Predicates.notNull()));
    }
    
    /** {@inheritDoc} */
    @Override
    @Nonnull @NonnullElements @Unmodifiable @NotLive public List<String> getKeyTransportEncryptionAlgorithms() {
        return ImmutableList.copyOf(keyTransportEncryptionAlgorithms);
    }
    
    /**
     * Set the key transport encryption algorithms to use.
     * 
     * @param algorithms the list of algorithms
     */
    public void setKeyTransportEncryptionAlgorithms(@Nullable final List<String> algorithms) {
        if (algorithms == null) {
            keyTransportEncryptionAlgorithms = Collections.emptyList();
            return;
        }
        keyTransportEncryptionAlgorithms = Lists.newArrayList(StringSupport.normalizeStringCollection(algorithms));
    }
    
    /** {@inheritDoc} */
    @Override
    @Nullable public NamedKeyInfoGeneratorManager getDataKeyInfoGeneratorManager() {
        return dataKeyInfoGeneratorManager;
    }
    
    /**
     * Set the manager for named KeyInfoGenerator instances encrypting data.
     * 
     * @param keyInfoManager the KeyInfoGenerator manager to use
     */
    public void setDataKeyInfoGeneratorManager(@Nullable final NamedKeyInfoGeneratorManager keyInfoManager) {
        dataKeyInfoGeneratorManager = keyInfoManager;
    }
    
    /** {@inheritDoc} */
    @Override
    @Nullable public NamedKeyInfoGeneratorManager getKeyTransportKeyInfoGeneratorManager() {
        return keyTransportKeyInfoGeneratorManager;
    }
    
    /**
     * Set the manager for named KeyInfoGenerator instances for encrypting keys.
     * 
     * @param keyInfoManager the KeyInfoGenerator manager to use
     */
    public void setKeyTransportKeyInfoGeneratorManager(@Nullable final NamedKeyInfoGeneratorManager keyInfoManager) {
        keyTransportKeyInfoGeneratorManager = keyInfoManager;
    }
    
    /** {@inheritDoc} */
    @Override
    @Nullable public RSAOAEPParameters getRSAOAEPParameters() {
        return rsaOAEPParameters;
    }

    /**
     * Set the instance of {@link RSAOAEPParameters}.
     * 
     * @param params the new parameters instance
     */
    public void setRSAOAEPParameters(@Nullable final RSAOAEPParameters params) {
        rsaOAEPParameters = params;
    }
    
    /** {@inheritDoc}.
     * 
     * <p>Defaults to: <code>true</code>
     * 
     * */
    public boolean isRSAOAEPParametersMerge() {
        return rsaOAEPParametersMerge;
    }
    
    /**
     * Set the flag indicating whether to merge this configuration's {@link RSAOAEPParameters} values with those of 
     * a lower order of precedence, or to treat this configuration's parameters set as authoritative.
     * 
     * <p>Defaults to: <code>true</code>
     * 
     * @param flag true if should merge, false otherwise
     */
    public void setRSAOAEPParametersMerge(boolean flag) {
        rsaOAEPParametersMerge = flag;
    }
    
    /** {@inheritDoc} */
    @Override
    @Nullable public KeyTransportAlgorithmPredicate getKeyTransportAlgorithmPredicate() {
        return keyTransportPredicate;
    }
    
    /**
     * Set the instance of {@link KeyTransportAlgorithmPredicate}.
     * 
     * @param predicate the new predicate instance
     */
    public void setKeyTransportAlgorithmPredicate(KeyTransportAlgorithmPredicate predicate) {
        keyTransportPredicate = predicate;
    }

}