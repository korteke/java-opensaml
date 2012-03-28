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

import javax.security.auth.x500.X500Principal;

import net.shibboleth.utilities.java.support.resolver.Criterion;

/**
 * An implementation of {@link Criterion} which specifies criteria based on
 * X.509 certificate subject name.
 */
public final class X509SubjectNameCriterion implements Criterion {
    
    /** X.509 certificate subject name. */
    private X500Principal subjectName;
    
    /**
     * Constructor.
     *
     * @param subject certificate subject name
     */
    public X509SubjectNameCriterion(X500Principal subject) {
        setSubjectName(subject);
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
        if (subject == null) {
            throw new IllegalArgumentException("Subject principal criteria value must be supplied");
        }
        this.subjectName = subject;
    }

}
