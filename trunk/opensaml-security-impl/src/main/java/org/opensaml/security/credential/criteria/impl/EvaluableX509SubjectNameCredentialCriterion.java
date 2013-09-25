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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.security.auth.x500.X500Principal;

import net.shibboleth.utilities.java.support.logic.AbstractTriStatePredicate;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.security.credential.Credential;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.security.x509.X509SubjectNameCriterion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Instance of evaluable credential criteria for evaluating whether a credential's certificate contains a particular
 * subject name.
 */
public class EvaluableX509SubjectNameCredentialCriterion extends AbstractTriStatePredicate<Credential> 
        implements EvaluableCredentialCriterion {

    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(EvaluableX509SubjectNameCredentialCriterion.class);

    /** Base criteria. */
    private final X500Principal subjectName;

    /**
     * Constructor.
     * 
     * @param criteria the criteria which is the basis for evaluation
     */
    public EvaluableX509SubjectNameCredentialCriterion(@Nonnull final X509SubjectNameCriterion criteria) {
        subjectName = Constraint.isNotNull(criteria, "Criterion instance cannot be null").getSubjectName();
    }

    /**
     * Constructor.
     * 
     * @param newSubjectName the subject name criteria value which is the basis for evaluation
     */
    public EvaluableX509SubjectNameCredentialCriterion(@Nonnull final X500Principal newSubjectName) {
        subjectName = Constraint.isNotNull(newSubjectName, "Subject name cannot be null");
    }

    /** {@inheritDoc} */
    @Nullable public boolean apply(@Nullable final Credential target) {
        if (target == null) {
            log.error("Credential target was null");
            return isNullInputSatisfies();
        } else if (!(target instanceof X509Credential)) {
            log.info("Credential is not an X509Credential, does not satisfy subject name criteria");
            return false;
        }

        X509Certificate entityCert = ((X509Credential) target).getEntityCertificate();
        if (entityCert == null) {
            log.info("X509Credential did not contain an entity certificate, does not satisfy criteria");
            return false;
        }

        return subjectName.equals(entityCert.getSubjectX500Principal());
    }
    
    /** {@inheritDoc} */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("EvaluableX509SubjectNameCredentialCriterion [subjectName=");
        builder.append(subjectName.getName());
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return subjectName.hashCode();
    }

    /** {@inheritDoc} */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof EvaluableX509SubjectNameCredentialCriterion) {
            return subjectName.equals(((EvaluableX509SubjectNameCredentialCriterion) obj).subjectName);
        }

        return false;
    }

}