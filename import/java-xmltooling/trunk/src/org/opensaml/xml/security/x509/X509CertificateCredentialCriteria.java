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

import java.math.BigInteger;

import javax.security.auth.x500.X500Principal;

import org.opensaml.xml.security.credential.CredentialCriteria;

/**
 * An implementation of {@link CredentialCriteria} which specifies criteria pertaining 
 * to characteristics of an X.509 certificate to be resolved.
 */
public final class X509CertificateCredentialCriteria implements CredentialCriteria {
    
    /** X.509 certificate subject name. */
    private X500Principal subjectName;
    
    /** X.509 certificate issuer name. */
    private X500Principal issuerName;
    
    /** X.509 certificate serial number. */
    private BigInteger serialNumber;
    
    /** X.509 certificate subject key identifier. */
    private byte[] subjectKeyIdentifier;
    
    /**
     * Constructor.
     *
     * @param subject certificate subject name
     * @param issuer certificate issuer name
     * @param serial certificate serial number
     * @param ski certificate subject key identifier
     */
    public X509CertificateCredentialCriteria(X500Principal subject, byte[] ski, 
            X500Principal issuer, BigInteger serial) {
        setSubjectName(subject);
        setIssuerName(issuer);
        setSerialNumber(serial);
        setSubjectKeyIdentifier(ski);
    }
    
    /** Get the issuer name.
     * 
     * @return Returns the issuer name.
     */
    public X500Principal getIssuerName() {
        return issuerName;
    }

    /**
     * Set the issuer name.
     * 
     * @param issuer The issuer name to set.
     */
    public void setIssuerName(X500Principal issuer) {
        this.issuerName = issuer;
    }

    /**
     * Get the serial number.
     * 
     * @return Returns the serial number.
     */
    public BigInteger getSerialNumber() {
        return serialNumber;
    }

    /**
     * Set the serial number.
     * 
     * @param serial The serial number to set.
     */
    public void setSerialNumber(BigInteger serial) {
        this.serialNumber = serial;
    }

    /**
     * Get the subject name.
     * 
     * @return Returns the subject name
     */
    public X500Principal getSubjectName() {
        return subjectName;
    }

    /**
     * Set the serial number.
     * 
     * @param subject The subject name
     */
    public void setSubjectName(X500Principal subject) {
        this.subjectName = subject;
    }
    
    /**
     * Get the subject key identifier.
     * 
     * @return Returns the subject key identifier
     */
    public byte[] getSubjectKeyIdentifier() {
        return subjectKeyIdentifier;
    }

    /**
     * Set the subject key identifier.
     * 
     * @param ski The subject key identifier to set.
     */
    public void setSubjectKeyIdentifier(byte[] ski) {
        subjectKeyIdentifier = ski;
    }
    
    
    

}
