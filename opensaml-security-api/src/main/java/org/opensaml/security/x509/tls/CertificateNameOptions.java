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

package org.opensaml.security.x509.tls;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotLive;
import net.shibboleth.utilities.java.support.annotation.constraint.Unmodifiable;

import org.opensaml.security.x509.InternalX500DNHandler;
import org.opensaml.security.x509.X500DNHandler;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Options for deriving message context issuer names from an client TLS X.509 certificate.
 */
public class CertificateNameOptions implements Cloneable {

    /** Evaluate the certificate subject DN as a derived issuer entity ID. */
    private boolean evaluateSubjectDN;

    /** Evaluate the certificate subject DN's common name (CN) as a derived issuer entity ID. */
    private boolean evaluateSubjectCommonName;

    /** The set of types of subject alternative names evaluate as derived issuer entity ID names. */
    private Set<Integer> subjectAltNames;

    /**
     * Responsible for serializing X.500 names to strings from certificate-derived
     * {@link javax.security.auth.x500.X500Principal} instances.
     */
    private X500DNHandler x500DNHandler;

    /** The format specifier for serializaing X.500 subject names to strings. */
    private String x500SubjectDNFormat;

    /** Constructor. */
    public CertificateNameOptions() {
        subjectAltNames = Collections.emptySet();
        x500DNHandler = new InternalX500DNHandler();
        x500SubjectDNFormat = X500DNHandler.FORMAT_RFC2253;
    }

    /**
     * Get whether to evaluate the certificate subject DN's common name (CN) as a derived issuer entity ID.
     * 
     * @return Returns the evaluateSubjectCommonName.
     */
    public boolean evaluateSubjectCommonName() {
        return evaluateSubjectCommonName;
    }
    
    /**
     * Set whether to evaluate the certificate subject DN's common name (CN) as a derived issuer entity ID.
     * 
     * @param flag new new evaluateSubjectCommonName value.
     */
    public void setEvaluateSubjectCommonName(boolean flag) {
        evaluateSubjectCommonName = flag;
    }

    /**
     * Get whether to evaluate the certificate subject DN as a derived issuer entity ID.
     * 
     * @return Returns the evaluateSubjectDN.
     */
    public boolean evaluateSubjectDN() {
        return evaluateSubjectDN;
    }

    /**
     * Set whether to evaluate the certificate subject DN as a derived issuer entity ID.
     * 
     * @param flag the new evaluateSubjectDN value.
     */
    public void setEvaluateSubjectDN(boolean flag) {
        evaluateSubjectDN = flag;
    }
    
    /**
     * Get the set of types of subject alternative names evaluate as derived issuer entity ID names,
     * using integer constants defined in {@link org.opensaml.security.X509Support}.
     * 
     * @return Returns the subjectAltNames.
     */
    @Nonnull @NonnullElements @NotLive @Unmodifiable public Set<Integer> getSubjectAltNames() {
        return ImmutableSet.copyOf(subjectAltNames);
    }
    
    /**
     * Set the set of types of subject alternative names evaluate as derived issuer entity ID names,
     * using integer constants defined in {@link org.opensaml.security.X509Support}.
     * 
     * @param names the set of types of subject alternative names
     */
    public void setSubjectAltNames(@Nullable final Set<Integer> names) {
        if (names == null) {
            subjectAltNames = Collections.emptySet();
            return;
        }
        
        subjectAltNames = Sets.newHashSet(Collections2.filter(names, Predicates.notNull()));
    }

    /**
     * Get the handler responsible for serializing X.500 names to strings from certificate-derived
     * {@link javax.security.auth.x500.X500Principal} instances.
     * 
     * @return Returns the x500DNHandler.
     */
    public X500DNHandler getX500DNHandler() {
        return x500DNHandler;
    }

    /**
     * Set the handler responsible for serializing X.500 names to strings from certificate-derived
     * {@link javax.security.auth.x500.X500Principal} instances.
     * 
     * @param handler the new x500DNHandler value.
     */
    public void setX500DNHandler(X500DNHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("X500DNHandler may not be null");
        }
        x500DNHandler = handler;
    }
    
    /**
     * Get the the format specifier for serializaing X.500 subject names to strings.
     * 
     * @return Returns the x500SubjectDNFormat.
     */
    public String getX500SubjectDNFormat() {
        return x500SubjectDNFormat;
    }

    /**
     * Set the the format specifier for serializaing X.500 subject names to strings.
     * 
     * @param format the new x500SubjectDNFormat value.
     */
    public void setX500SubjectDNFormat(String format) {
        x500SubjectDNFormat = format;
    }
    
    /** {@inheritDoc} */
    public CertificateNameOptions clone() {
        CertificateNameOptions clonedOptions;
        try {
            clonedOptions = (CertificateNameOptions) super.clone();
        } catch (CloneNotSupportedException e) {
            // we know we're cloneable, so this will never happen
            return null;
        }

        clonedOptions.subjectAltNames = new LinkedHashSet<Integer>();
        clonedOptions.subjectAltNames.addAll(this.subjectAltNames);

        clonedOptions.x500DNHandler = this.x500DNHandler.clone();

        return clonedOptions;
    }

}

