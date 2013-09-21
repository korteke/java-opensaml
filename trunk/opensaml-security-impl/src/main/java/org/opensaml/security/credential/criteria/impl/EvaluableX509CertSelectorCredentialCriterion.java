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

import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.security.credential.Credential;
import org.opensaml.security.x509.X509Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Instance of evaluable credential criteria for evaluating whether a credential's certificate meets the criteria
 * specified by an instance of {@link X509CertSelector}.
 * 
 */
public class EvaluableX509CertSelectorCredentialCriterion implements EvaluableCredentialCriterion {

    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(EvaluableX509CertSelectorCredentialCriterion.class);

    /** Base criteria. */
    private final X509CertSelector certSelector;

    /**
     * Constructor.
     * 
     * @param selector X509 cert selector to use
     */
    public EvaluableX509CertSelectorCredentialCriterion(@Nonnull final X509CertSelector selector) {
        certSelector = Constraint.isNotNull(selector, "X.509 cert selector cannot be null");
    }

    /** {@inheritDoc} */
    @Nullable public Boolean evaluate(@Nullable final Credential target) {
        if (target == null) {
            log.error("Credential target was null");
            return null;
        } else if (!(target instanceof X509Credential)) {
            log.info("Credential is not an X509Credential, cannot evaluate X509CertSelector criteria");
            return Boolean.FALSE;
        }

        X509Certificate entityCert = ((X509Credential) target).getEntityCertificate();
        if (entityCert == null) {
            log.info("X509Credential did not contain an entity certificate, cannot evaluate X509CertSelector criteria");
            return Boolean.FALSE;
        }

        return certSelector.match(entityCert);
    }
    
    /** {@inheritDoc} */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("EvaluableX509CertSelectorCredentialCriterion [certSelector=");
        builder.append("<contents not displayable>");
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return certSelector.hashCode();
    }

    /** {@inheritDoc} */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof EvaluableX509CertSelectorCredentialCriterion) {
            return certSelector.equals(((EvaluableX509CertSelectorCredentialCriterion) obj).certSelector);
        }

        return false;
    }

}