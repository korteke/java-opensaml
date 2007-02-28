/*
 * Copyright [2007] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.xml.security.x509;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;

import javax.crypto.SecretKey;

import org.opensaml.xml.security.credential.UsageType;
import org.opensaml.xml.util.DatatypeHelper;

/**
 * A basic implementation of {@link X509Credential}.
 */
public class BasicX509Credential implements X509Credential {
    
    /** ID of the entity the credential is for. */
    private String entityId;
    
    /** Usage the credential is meant for. */
    private UsageType credentialUsage;
    
    /** Key names. */
    private Collection<String> keyNames;
    
    /** Public keys. */
    private Collection<PublicKey> publicKeys;
    
    /** Secret key for the entity. */
    private SecretKey secretKey;
    
    /** Private key. */
    private PrivateKey privateKey;
    
    /** Entity certificate. */
    private X509Certificate entityCert;
        
    /** Entity certificate chain, must include entity certificate. */
    private Collection<X509Certificate> certChain;

    /** CRLs for this credential. */
    private Collection<X509CRL> crls;
    
    /**
     * Constructor.
     *
     * @param id ID of the entity the credential is for
     * 
     * @throws IllegalArgumentException thrown if the given ID is null or empty
     */
    public BasicX509Credential(String id) throws IllegalArgumentException{
        if(DatatypeHelper.isEmpty(id)){
            throw new IllegalArgumentException("Entity ID may not be null or empty.");
        }
        
        entityId = DatatypeHelper.safeTrim(id);
    }
    
    /** {@inheritDoc} */
    public String getEntityId() {
        return entityId;
    }

    /** {@inheritDoc} */
    public Collection<X509CRL> getCRLs() {
        return crls;
    }
    
    /**
     * Sets the CRLs for this credential.
     * 
     * @param newCRLs CRLs for this credential
     */
    public void setCRLs(Collection<X509CRL> newCRLs){
        crls = newCRLs;
    }

    /** {@inheritDoc} */
    public X509Certificate getEntityCertificate() {
        return entityCert;
    }
    
    /**
     * Sets the entity certificate for this credential.
     * 
     * @param cert entity certificate for this credential
     */
    public void setEntityCertificate(X509Certificate cert){
        entityCert = cert;
    }

    /** {@inheritDoc} */
    public Collection<X509Certificate> getEntityCertificateChain() {
        return certChain;
    }
    
    /**
     * Sets the entity certificate chain for this credential.  This <strong>MUST</strong> include 
     * the entity certificate.
     * 
     * @param certs ntity certificate chain for this credential
     */
    public void setEntityCertificateChain(Collection<X509Certificate> certs){
        certChain = new ArrayList<X509Certificate>(certs);
    }

    /** {@inheritDoc} */
    public Collection<String> getKeyNames() {
        return keyNames;
    }
    
    /**
     * Sets the names for the keys of this credential.
     * 
     * @param names names for the keys of this credential
     */
    public void setKeyNames(Collection<String> names){
        keyNames = new ArrayList<String>(names);
    }

    /** {@inheritDoc} */
    public PrivateKey getPrivateKey() {
        return privateKey;
    }
   
    /**
     * Sets the private key for this credential.
     * 
     * @param key private key for this credential
     */
    public void setPrivateKey(PrivateKey key){
        privateKey = key;
    }
    
    /** {@inheritDoc} */
    public SecretKey getSecretyKey() {
        return secretKey;
    }
    
    /**
     * Sets the secret key for this credential.
     * 
     * @param key secret key for this credential
     */
    public void setSecretKey(SecretKey key){
        secretKey = key;
    }

    /** {@inheritDoc} */
    public Collection<PublicKey> getPublicKeys() {
        return publicKeys;
    }
    
    /**
     * Sets the public keys for this credential.
     * 
     * @param keys public keys for this credential
     */
    public void setPublicKeys(Collection<PublicKey> keys){
        publicKeys = new ArrayList<PublicKey>(keys);
    }

    /** {@inheritDoc} */
    public UsageType getUsageType() {
        return credentialUsage;
    }
    
    /**
     * Sets the usage type for this credential.
     * 
     * @param usage usage type for this credential
     */
    public void setUsageType(UsageType usage){
        credentialUsage = usage;
    }
}