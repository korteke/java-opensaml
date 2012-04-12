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

package org.opensaml.security.x509;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;

import javax.crypto.SecretKey;

import net.shibboleth.utilities.java.support.collection.LazySet;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;

/**
 * A basic implementation of {@link X509Credential}.
 */
public class BasicX509Credential extends BasicCredential implements X509Credential {

    /** Entity certificate. */
    private X509Certificate entityCert;

    /** Entity certificate chain, must include entity certificate. */
    private Collection<X509Certificate> entityCertChain;

    /** CRLs for this credential. */
    private Collection<X509CRL> crls;
    
    /**
     * Constructor.
     *
     * @param entityCertificate the credential entity certificate
     */
    public BasicX509Credential(X509Certificate entityCertificate) {
        super();
        setEntityCertificate(entityCertificate);
    }
    
    /**
     * Constructor.
     *
     * @param entityCertificate the credential entity certificate
     * @param privateKey the credential private key
     */
    public BasicX509Credential(X509Certificate entityCertificate, PrivateKey privateKey) {
        super();
        setEntityCertificate(entityCertificate);
        setPrivateKey(privateKey);
    }

    /** {@inheritDoc} */
    public Class<? extends Credential> getCredentialType() {
        return X509Credential.class;
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
    public void setCRLs(Collection<X509CRL> newCRLs) {
        crls = newCRLs;
    }

    /** {@inheritDoc} */
    public X509Certificate getEntityCertificate() {
        return entityCert;
    }

    /**
     * Sets the entity certificate for this credential.
     * 
     * @param newEntityCertificate entity certificate for this credential
     */
    public void setEntityCertificate(X509Certificate newEntityCertificate) {
        Constraint.isNotNull(newEntityCertificate, "Credential certificate may not be null");
        entityCert = newEntityCertificate;
    }
    
    /** {@inheritDoc} */
    public PublicKey getPublicKey() {
        return getEntityCertificate().getPublicKey();
    }
    
    /**
     * This operation is unsupported for X.509 credentials. The public key will be retrieved
     * automatically from the entity certificate.
     * 
     * @param newPublicKey not supported
     */
    public void setPublicKey(PublicKey newPublicKey) {
        throw new UnsupportedOperationException("Public key may not be set explicitly on an X509 credential");
    }

    /** {@inheritDoc} */
    public Collection<X509Certificate> getEntityCertificateChain() {
        if (entityCertChain == null) {
            LazySet<X509Certificate> constructedChain = new LazySet<X509Certificate>();
            constructedChain.add(entityCert);
            return constructedChain;
        } else {
            return entityCertChain;
        }
    }

    /**
     * Sets the entity certificate chain for this credential. This <strong>MUST</strong> include the entity
     * certificate.
     * 
     * @param newCertificateChain entity certificate chain for this credential
     */
    public void setEntityCertificateChain(Collection<X509Certificate> newCertificateChain) {
        Constraint.isNotNull(newCertificateChain, "Certificate chain argument may not be null");
        Constraint.isNotEmpty(newCertificateChain, "Certificate chain collection argument may not be empty");
        entityCertChain = new ArrayList<X509Certificate>(newCertificateChain);
    }
    
    /**
     *  This operation is unsupported for X.509 credentials.
     *  
     *  @return null
     */
    public SecretKey getSecretKey() {
        return null;
    }
    
    /**
     *  This operation is unsupported for X.509 credentials.
     *  
     *  @param newSecretKey unsupported
     */
    public void setSecretKey(SecretKey newSecretKey) {
        throw new UnsupportedOperationException("An X509Credential may not contain a secret key");
    }

}