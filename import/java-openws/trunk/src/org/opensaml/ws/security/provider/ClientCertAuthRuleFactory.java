/*
 * Copyright [2006] [University Corporation for Advanced Internet Development, Inc.]
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

package org.opensaml.ws.security.provider;

import java.util.LinkedHashSet;

import javax.security.auth.x500.X500Principal;
import javax.servlet.ServletRequest;

import org.opensaml.ws.security.SecurityPolicyRule;
import org.opensaml.xml.security.x509.InternalX500DNHandler;
import org.opensaml.xml.security.x509.X500DNHandler;
import org.opensaml.xml.security.x509.X509Credential;
import org.opensaml.xml.security.x509.X509Util;

/**
 * Factory for policy rules which check if the client cert used to authenticate the request is valid and trusted.
 * 
 */
public class ClientCertAuthRuleFactory extends BaseTrustEngineRuleFactory<X509Credential, ServletRequest> {
    
    /** Options for derving issuer names from an X.509 certificate. */
    private CertificateNameOptions certNameOptions;
    
    /**
     * Constructor.
     */
    public ClientCertAuthRuleFactory() {
        certNameOptions = newCertificateNameOptions();
    }
    
    /**
     * Get a new instance to hold options.  Used by the this class constructor.
     * Subclasses <strong>MUST</strong> override to produce an instance of the appropriate 
     * subclass of {@link CertificateNameOptions} if they extend this options class
     * to hold subclass-specific options.
     * 
     * @return a new instance of factory/generator options
     */
    protected CertificateNameOptions newCertificateNameOptions() {
        return new CertificateNameOptions();
    }
    
    /**
     * Get the current certificate name options.
     * 
     * @return cloned instance of the current name options
     */
    protected CertificateNameOptions getCertificateNameOptions() {
        return certNameOptions.clone();
    }

    /** {@inheritDoc} */
    public SecurityPolicyRule<ServletRequest> createRuleInstance() {
        CertificateNameOptions newOptions = certNameOptions.clone();
        return new ClientCertAuthRule(getTrustEngine(), newOptions);
    }
    
    /**
     * Get the option to evaluate the entity certificate subject common name (CN) as a derived issuer entity ID.
     * 
     * @return the option value
     */
    public boolean evaluateSubjectCommonName() {
        return certNameOptions.evaluateSubjectCommonName;
    }

    /**
     * Set the option to evaluate the entity certificate subject common name (CN) as a derived issuer entity ID.
     * 
     * @param newValue the new option value
     */
    public void setEvaluateSubjectCommonName(boolean newValue) {
        certNameOptions.evaluateSubjectCommonName = newValue;
    }
    
    /**
     * Get the option to evaluate the entity certificate subject DN as a derived issuer entity ID.
     * 
     * @return the option value
     */
    public boolean evaluateSubjectDN() {
        return certNameOptions.evaluateSubjectDN;
    }

    /**
     * Set the option to evaluate the entity certificate subject DN as a derived issuer entity ID.
     * 
     * @param newValue the new option value
     */
    public void setEvaluateSubjectDN(boolean newValue) {
        certNameOptions.evaluateSubjectDN = newValue;
    }
    
    /**
     * The set of types of subject alternative names to evaluate as derived issuer entity ID's.
     * 
     * Name types are represented using the constant OID tag name values defined 
     * in {@link X509Util}.  Note: A LinkedHashSet is used to provide predictable ordering
     * on iteration; consequently, the order of evaluation of the name types as derived
     * issuer names will be the same as insertion order into set.
     * 
     * 
     * @return the modifiable set of alt name identifiers
     */
    public LinkedHashSet<Integer> getSubjectAltNames() {
        return certNameOptions.subjectAltNames;
    }
    
    /**
     * Get the handler which process X.500 distinguished names.
     * 
     * Defaults to {@link InternalX500DNHandler}.
     * 
     * @return returns the X500DNHandler instance
     */
    public X500DNHandler getX500DNHandler() {
        return certNameOptions.x500DNHandler;
    }
    
    /**
     * Set the handler which process X.500 distinguished names.
     * 
     * Defaults to {@link InternalX500DNHandler}.
     * 
     * @param handler the new X500DNHandler instance
     */
    public void setX500DNHandler(X500DNHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("X500DNHandler may not be null");
        }
        certNameOptions.x500DNHandler = handler;
    }
    
    /**
     * Get the output format specifier for X.500 subject names.
     * 
     * Defaults to RFC2253 format. The meaning of this format specifier value
     * is dependent upon the implementation of {@link X500DNHandler} which is used.
     * 
     * @return returns the format specifier
     */
    public String getX500SubjectDNFormat() {
        return certNameOptions.x500SubjectDNFormat;
    }
    
    /**
     * Set the output format specifier for X.500 subject names.
     * 
     * Defaults to RFC2253 format. The meaning of this format specifier value
     * is dependent upon the implementation of {@link X500DNHandler} which is used.
     * 
     * @param format the new X500DNHandler instance
     */
    public void setX500SubjectDNFormat(String format) {
        certNameOptions.x500SubjectDNFormat = format;
    }
    
    /**
     * Options for deriving issuer names from an X.509 certificate.
     */
    public class CertificateNameOptions implements Cloneable {
        
        /** Evaluate the certificate subject DN as a derived issuer entity ID. */
        private boolean evaluateSubjectDN;
        
        /** Evaluate the certificate subject DN's common name (CN) as a derived issuer entity ID. */
        private boolean evaluateSubjectCommonName;
        
        /** The set of types of subject alternative names evaluate as derived issuer entity ID names. */
        private LinkedHashSet<Integer> subjectAltNames;
        
        /** Responsible for serializing X.500 names to strings from 
         * certificate-derived {@link X500Principal} instances. */
        private X500DNHandler x500DNHandler;
        
        /** The format specifier for serializaing X.500 subject names to strings. */
        private String x500SubjectDNFormat;
        
        /** Constructor. */
        protected CertificateNameOptions() {
            subjectAltNames = new LinkedHashSet<Integer>();
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
         * Get whether to evaluate the certificate subject DN as a derived issuer entity ID. 
         * 
         * @return Returns the evaluateSubjectDN.
         */
        public boolean evaluateSubjectDN() {
            return evaluateSubjectDN;
        }

        /**
         * Get the set of types of subject alternative names evaluate as derived issuer entity ID names.
         * 
         * @return Returns the subjectAltNames.
         */
        public LinkedHashSet<Integer> getSubjectAltNames() {
            return subjectAltNames;
        }

        /**
         * Get the handler responsible for serializing X.500 names to strings from 
         * certificate-derived {@link X500Principal} instances.
         * 
         * @return Returns the x500DNHandler.
         */
        public X500DNHandler getX500DNHandler() {
            return x500DNHandler;
        }

        /**
         * Get the the format specifier for serializaing X.500 subject names to strings. 
         * 
         * @return Returns the x500SubjectDNFormat.
         */
        public String getX500SubjectDNFormat() {
            return x500SubjectDNFormat;
        }

        /** {@inheritDoc} */
        protected CertificateNameOptions clone() {
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
    
}