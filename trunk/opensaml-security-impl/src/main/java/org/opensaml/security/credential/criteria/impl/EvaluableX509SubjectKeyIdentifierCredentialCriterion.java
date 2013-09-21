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

import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.apache.commons.codec.binary.Hex;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.security.x509.X509SubjectKeyIdentifierCriterion;
import org.opensaml.security.x509.X509Support;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Instance of evaluable credential criteria for evaluating whether a credential's certificate contains a particular
 * subject key identifier.
 */
public class EvaluableX509SubjectKeyIdentifierCredentialCriterion implements EvaluableCredentialCriterion {
    
    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(EvaluableX509SubjectKeyIdentifierCredentialCriterion.class);
    
    /** Base criteria. */
    private final byte[] ski;
    
    /**
     * Constructor.
     *
     * @param criteria the criteria which is the basis for evaluation
     */
    public EvaluableX509SubjectKeyIdentifierCredentialCriterion(
            @Nonnull final X509SubjectKeyIdentifierCriterion criteria) {
        ski = Constraint.isNotNull(criteria, "Criterion instance cannot be null").getSubjectKeyIdentifier();
    }
    
    /**
     * Constructor.
     *
     * @param newSKI the criteria value which is the basis for evaluation
     */
    public EvaluableX509SubjectKeyIdentifierCredentialCriterion(@Nonnull final byte[] newSKI) {
        ski = Constraint.isNotEmpty(newSKI, "Subject key identifier cannot be null or empty");
    }

    /** {@inheritDoc} */
    @Nullable public Boolean evaluate(@Nullable final Credential target) {
        if (target == null) {
            log.error("Credential target was null");
            return null;
        } else if (!(target instanceof X509Credential)) {
            log.info("Credential is not an X509Credential, does not satisfy subject key identifier criteria");
            return Boolean.FALSE;
        }
        
        X509Certificate entityCert = ((X509Credential) target).getEntityCertificate();
        if (entityCert == null) {
            log.info("X509Credential did not contain an entity certificate, does not satisfy criteria");
            return Boolean.FALSE;
        }
        
        byte[] credSKI = X509Support.getSubjectKeyIdentifier(entityCert);
        if (credSKI == null || credSKI.length == 0) {
            log.info("Could not evaluate criteria, certificate contained no subject key identifier extension");
            return null;
        }
        
        return Arrays.equals(ski, credSKI);
    }
    
    /** {@inheritDoc} */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("EvaluableX509SubjectKeyIdentifierCredentialCriterion [ski=");
        builder.append(Hex.encodeHexString(ski));
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return ski.hashCode();
    }

    /** {@inheritDoc} */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof EvaluableX509SubjectKeyIdentifierCredentialCriterion) {
            return ski.equals(((EvaluableX509SubjectKeyIdentifierCredentialCriterion) obj).ski);
        }

        return false;
    }

}
