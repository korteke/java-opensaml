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

package org.opensaml.xml.security.credential.criteria;

import java.math.BigInteger;
import java.security.cert.X509Certificate;

import javax.security.auth.x500.X500Principal;

import org.apache.log4j.Logger;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.x509.X509Credential;
import org.opensaml.xml.security.x509.X509IssuerSerialCriteria;

/**
 * Instance of evaluable credential criteria for evaluating whether a credential's certificate contains a particular
 * issuer name and serial number.
 */
public class EvaluableX509IssuerSerialCredentialCriteria implements EvaluableCredentialCriteria {
    
    /** Logger. */
    private static Logger log = Logger.getLogger(EvaluableX509IssuerSerialCredentialCriteria.class);
    
    /** Base criteria. */
    private X500Principal issuer;
    
    /** Base criteria. */
    private BigInteger serialNumber;
    
    /**
     * Constructor.
     *
     * @param criteria the criteria which is the basis for evaluation
     */
    public EvaluableX509IssuerSerialCredentialCriteria(X509IssuerSerialCriteria criteria) {
        if (criteria == null) {
            throw new NullPointerException("Criteria instance may not be null");
        }
        issuer = criteria.getIssuerName();
        serialNumber = criteria.getSerialNumber();
    }
    
    /**
     * Constructor.
     *
     * @param newIssuer the issuer name criteria value which is the basis for evaluation
     * @param newSerialNumber the serial number criteria value which is the basis for evaluation
     */
    public EvaluableX509IssuerSerialCredentialCriteria(X500Principal newIssuer, BigInteger newSerialNumber) {
        if (newIssuer == null || newSerialNumber == null) {
            throw new IllegalArgumentException("Issuer and serial number may not be null");
        }
        issuer = newIssuer;
        serialNumber = newSerialNumber;
    }

    /** {@inheritDoc} */
    public Boolean evaluate(Credential target) {
        if (target == null) {
            log.error("Credential target was null");
            return null;
        }
        if (! (target instanceof X509Credential)) {
            log.info("Credential is not an X509Credential, does not satisfy issuer name and serial number criteria");
            return Boolean.FALSE;
        }
        X509Credential x509Cred = (X509Credential) target;
        
        X509Certificate entityCert = x509Cred.getEntityCertificate();
        if (entityCert == null) {
            log.info("X509Credential did not contain an entity certificate, does not satisfy criteria");
            return Boolean.FALSE;
        }
        
        if (! entityCert.getIssuerX500Principal().equals(issuer)) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Evaluation of credential data '%s' against criteria data '%s' was: '%s'",
                        entityCert.getIssuerX500Principal().getName(X500Principal.RFC2253),
                        issuer.getName(X500Principal.RFC2253), "false"));
            }
            return false;
        }
        Boolean result = entityCert.getSerialNumber().equals(serialNumber);
        if (log.isDebugEnabled()) {
            log.debug(String.format("Evaluation of credential data '%s' against criteria data '%s' was: '%s'",
                    entityCert.getSerialNumber(), serialNumber, result));
        }
        return result;
    }

}
