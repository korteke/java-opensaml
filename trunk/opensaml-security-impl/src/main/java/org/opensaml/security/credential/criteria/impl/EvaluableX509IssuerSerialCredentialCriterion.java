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

package org.opensaml.security.credential.criteria.impl;

import java.math.BigInteger;
import java.security.cert.X509Certificate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.security.auth.x500.X500Principal;

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.security.credential.Credential;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.security.x509.X509IssuerSerialCriterion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Instance of evaluable credential criteria for evaluating whether a credential's certificate contains a particular
 * issuer name and serial number.
 */
public class EvaluableX509IssuerSerialCredentialCriterion implements EvaluableCredentialCriterion {

    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(EvaluableX509IssuerSerialCredentialCriterion.class);

    /** Base criteria. */
    private final X500Principal issuer;

    /** Base criteria. */
    private final BigInteger serialNumber;

    /**
     * Constructor.
     * 
     * @param criteria the criteria which is the basis for evaluation
     */
    public EvaluableX509IssuerSerialCredentialCriterion(@Nonnull final X509IssuerSerialCriterion criteria) {
        issuer = Constraint.isNotNull(criteria, "Criterion instance cannot be null").getIssuerName();
        serialNumber = criteria.getSerialNumber();
    }

    /**
     * Constructor.
     * 
     * @param newIssuer the issuer name criteria value which is the basis for evaluation
     * @param newSerialNumber the serial number criteria value which is the basis for evaluation
     */
    public EvaluableX509IssuerSerialCredentialCriterion(@Nonnull final X500Principal newIssuer,
            @Nonnull final BigInteger newSerialNumber) {
        issuer = Constraint.isNotNull(newIssuer, "Issuer cannot be null");
        serialNumber = Constraint.isNotNull(newSerialNumber, "Serial number cannot be null");
    }

    /** {@inheritDoc} */
    @Nullable public Boolean evaluate(@Nullable final Credential target) {
        if (target == null) {
            log.error("Credential target was null");
            return null;
        } else if (!(target instanceof X509Credential)) {
            log.info("Credential is not an X509Credential, does not satisfy issuer name and serial number criteria");
            return Boolean.FALSE;
        }

        X509Certificate entityCert = ((X509Credential) target).getEntityCertificate();
        if (entityCert == null) {
            log.info("X509Credential did not contain an entity certificate, does not satisfy criteria");
            return Boolean.FALSE;
        }
        
        return entityCert.getIssuerX500Principal().equals(issuer) && entityCert.getSerialNumber().equals(serialNumber);
    }
    
    /** {@inheritDoc} */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("EvaluableX509IssuerSerialCredentialCriterion [issuer=");
        builder.append(issuer.getName());
        builder.append(", serialNumber=");
        builder.append(serialNumber);
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    public int hashCode() {
        int result = 17;
        result = result*37 + issuer.hashCode();
        result = result*37 + serialNumber.hashCode();
        return result;
    }

    /** {@inheritDoc} */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof EvaluableX509IssuerSerialCredentialCriterion) {
            EvaluableX509IssuerSerialCredentialCriterion other = (EvaluableX509IssuerSerialCredentialCriterion) obj;
            return issuer.equals(other.issuer) && serialNumber.equals(other.serialNumber);
        }

        return false;
    }

}