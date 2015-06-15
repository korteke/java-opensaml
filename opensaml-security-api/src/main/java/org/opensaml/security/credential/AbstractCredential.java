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

package org.opensaml.security.credential;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;

import net.shibboleth.utilities.java.support.collection.LazySet;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

/**
 * Base class for {@link org.opensaml.security.credential.Credential} implementations.
 */
public abstract class AbstractCredential implements Credential {

    /** ID of the entity owning this credential. */
    private String entityId;
    
    /** Usage type of this credential. */
    private UsageType usageType;
    
    /** Key names for this credential. */
    private Collection<String> keyNames;
    
    /** Public key of this credential. */
    private PublicKey publicKey;
    
    /** Secret key for this credential. */
    private SecretKey secretKey;
    
    /** Private key of this credential. */
    private PrivateKey privateKey;
    
    /** Credential context of this credential. */
    private final CredentialContextSet credentialContextSet;
    
    /**
     * Constructor.
     */
    public AbstractCredential() {
        credentialContextSet = new CredentialContextSet(); 
        keyNames = new LazySet<>();
        setUsageType(UsageType.UNSPECIFIED);
    }
    
    /** {@inheritDoc}  */
    @Nullable public String getEntityId() {
        return entityId;
    }

    /** {@inheritDoc}  */
    @Nullable public UsageType getUsageType() {
        return usageType;
    }

    /** {@inheritDoc} */
    @Nonnull public Collection<String> getKeyNames() {
        return keyNames;
    }
    
    /** {@inheritDoc}  */
    @Nullable public PublicKey getPublicKey() {
        return publicKey;
    }
    
    /** {@inheritDoc} */
    @Nullable public SecretKey getSecretKey() {
        return secretKey;
    }

    /** {@inheritDoc}  */
    @Nullable public PrivateKey getPrivateKey() {
        return privateKey;
    }

    /** {@inheritDoc} */
    @Nonnull public CredentialContextSet getCredentialContextSet() {
        return credentialContextSet;
    }

    /**
     * Sets the ID of the entity this credential is for.
     * 
     * @param newEntityID ID of the entity this credential is for
     */
    protected void setEntityId(@Nullable final String newEntityID) {
        entityId = StringSupport.trimOrNull(newEntityID);
    }

    /**
     * Sets the usage type for this credential.
     * 
     * @param newUsageType usage type for this credential
     */
    protected void setUsageType(@Nonnull final UsageType newUsageType) {
        Constraint.isNotNull(newUsageType, "Credential usage type cannot be null");
        usageType = newUsageType;
    }

    /**
     * Sets the public key for this credential.
     * 
     * @param newPublicKey public key for this credential
     */
    protected void setPublicKey(@Nonnull final PublicKey newPublicKey) {
        Constraint.isNull(getSecretKey(), "A credential with a secret key cannot contain a public key");
        Constraint.isNotNull(newPublicKey, "Credential public key cannot be null");
        publicKey = newPublicKey;
    }
    
    /**
     * Sets the private key for this credential.
     * 
     * @param newPrivateKey private key for this credential
     */
    protected void setPrivateKey(@Nonnull final PrivateKey newPrivateKey) {
        Constraint.isNull(getSecretKey(), "A credential with a secret key cannot contain a private key");
        Constraint.isNotNull(newPrivateKey, "Credential private key cannot be null");
        privateKey = newPrivateKey;
    }

    /**
     * Sets the secret key for this credential.
     * 
     * @param newSecretKey secret key for this credential
     */ 
    protected void setSecretKey(@Nonnull final SecretKey newSecretKey) {
        Constraint.isNull(getPublicKey(), "A credential with a public key cannot contain a secret key");
        Constraint.isNull(getPrivateKey(), "A credential with a private key cannot contain a secret key");
        Constraint.isNotNull(newSecretKey, "Credential secret key cannot be null");
        secretKey = newSecretKey;
    }
    
}