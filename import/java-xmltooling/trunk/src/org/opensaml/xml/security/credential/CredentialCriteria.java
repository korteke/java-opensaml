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

package org.opensaml.xml.security.credential;

import java.math.BigInteger;
import java.security.PublicKey;

/**
 * Class for specifying criteria by which a {@link CredentialResolver} should resolve credentials.
 */
public class CredentialCriteria {
    
    /** EntityID for which to resolve credential. */
    private String entityID;
    
    /** Key usage type of resolved credentials. */
    private UsageType usage;
    
    /** Key algorithm type of resolved credentials. */
    private String keyAlgorithm;
    
    /** Key name of resolved credentials.  */
    private String keyName;
    
    /** Specifier of public key associated with resolved credentials. */
    private PublicKey publicKey;
    
    /** Specifier of X.509 certificate subject name (DN) associated with resolved credentials. */
    private String x509SubjectName;
    
    /** Specifier of X.509 certificate issuername (DN) associated with resolved credentials. */
    private String x509IssuerName;
    
    /** Specifier of X.509 certificate serial number associated with resolved credentials. */
    private BigInteger x509SerialNumber;
    
    // TODO - X509 SubjectKeyIdentifier format/type can vary, need to clarify

    /**
     * Get the entity ID criteria.
     * 
     * @return Returns the entityID.
     */
    public String getEntityID() {
        return entityID;
    }

    /**
     * Set the entity ID criteria.
     * 
     * @param entityID The entityID to set.
     */
    public void setEntityID(String entityID) {
        this.entityID = entityID;
    }

    /**
     * Get the key algorithm criteria.
     * 
     * @return returns the keyAlgorithm.
     */
    public String getKeyAlgorithm() {
        return keyAlgorithm;
    }

    /**
     * Set the key algorithm criteria.
     * 
     * @param keyAlgorithm The keyAlgorithm to set.
     */
    public void setKeyAlgorithm(String keyAlgorithm) {
        this.keyAlgorithm = keyAlgorithm;
    }

    /**
     * Get the key name criteria.
     * 
     * @return Returns the keyName.
     */
    public String getKeyName() {
        return keyName;
    }

    /**
     * Set the key name criteria.
     * 
     * @param keyName The keyName to set.
     */
    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    /**
     * Get the public key criteria.
     * 
     * @return Returns the publicKey.
     */
    public PublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * Set the public key criteria. 
     * 
     * @param publicKey The publicKey to set.
     */
    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    /**
     * Get the key usage criteria.
     * 
     * @return Returns the usage.
     */
    public UsageType getUsage() {
        return usage;
    }

    /**
     * Set the key usage criteria.
     * 
     * @param usage The usage to set.
     */
    public void setUsage(UsageType usage) {
        this.usage = usage;
    }

    /**
     * Get the X.509 issuer name criteria.
     * 
     * @return Returns the x509IssuerName.
     */
    public String getX509IssuerName() {
        return x509IssuerName;
    }

    /**
     * Set the X.509 issuer name criteria.
     * 
     * @param issuerName The x509IssuerName to set.
     */
    public void setX509IssuerName(String issuerName) {
        x509IssuerName = issuerName;
    }

    /**
     * Get the X.509 serial number criteria.
     * 
     * @return Returns the x509SerialNumber.
     */
    public BigInteger getX509SerialNumber() {
        return x509SerialNumber;
    }

    /**
     * Set the X.509 serial number criteria.
     * 
     * @param serialNumber The x509SerialNumber to set.
     */
    public void setX509SerialNumber(BigInteger serialNumber) {
        x509SerialNumber = serialNumber;
    }

    /**
     * Get the X.509 subject name criteria.
     * 
     * @return Returns the x509SubjectName.
     */
    public String getX509SubjectName() {
        return x509SubjectName;
    }

    /**
     * Set the X.509 subject name criteria.
     * 
     * @param subjectDN The x509SubjectDN to set.
     */
    public void setX509SubjectName(String subjectDN) {
        x509SubjectName = subjectDN;
    }
    
 

}
