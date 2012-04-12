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
        keyNames = new LazySet<String>();
        setUsageType(UsageType.UNSPECIFIED);
    }
    
    /** {@inheritDoc}  */
    public String getEntityId() {
        return entityId;
    }

    /** {@inheritDoc}  */
    public UsageType getUsageType() {
        return usageType;
    }

    /** {@inheritDoc} */
    public Collection<String> getKeyNames() {
        return keyNames;
    }
    
    /** {@inheritDoc}  */
    public PublicKey getPublicKey() {
        return publicKey;
    }
    
    /** {@inheritDoc} */
    public SecretKey getSecretKey() {
        return secretKey;
    }

    /** {@inheritDoc}  */
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    /** {@inheritDoc} */
    public CredentialContextSet getCredentalContextSet() {
        return credentialContextSet;
    }

    /**
     * Sets the ID of the entity this credential is for.
     * 
     * @param newEntityID ID of the entity this credential is for
     */
    protected void setEntityId(String newEntityID) {
        entityId = StringSupport.trimOrNull(newEntityID);
    }

    /**
     * Sets the usage type for this credential.
     * 
     * @param newUsageType usage type for this credential
     */
    protected void setUsageType(UsageType newUsageType) {
        Constraint.isNotNull(newUsageType, "Credential usage type may not be null");
        usageType = newUsageType;
    }

    /**
     * Sets the public key for this credential.
     * 
     * @param newPublicKey public key for this credential
     */
    protected void setPublicKey(PublicKey newPublicKey) {
        Constraint.isNull(getSecretKey(), "A credential with a secret key may not contain a public key");
        Constraint.isNotNull(newPublicKey, "Credential public key may not be null");
        publicKey = newPublicKey;
    }
    
    /**
     * Sets the private key for this credential.
     * 
     * @param newPrivateKey private key for this credential
     */
    protected void setPrivateKey(PrivateKey newPrivateKey) {
        Constraint.isNull(getSecretKey(), "A credential with a secret key may not contain a private key");
        privateKey = newPrivateKey;
    }

    /**
     * Sets the secret key for this credential.
     * 
     * @param newSecretKey secret key for this credential
     */ 
    protected void setSecretKey(SecretKey newSecretKey) {
        Constraint.isNull(getPublicKey(), "A credential with a public key may not contain a secret key");
        Constraint.isNull(getPrivateKey(), "A credential with a private key may not contain a secret key");
        Constraint.isNotNull(newSecretKey, "Credential secret key may not be null");
        secretKey = newSecretKey;
    }
    
}