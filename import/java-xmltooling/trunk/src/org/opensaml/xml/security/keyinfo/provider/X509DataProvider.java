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

package org.opensaml.xml.security.keyinfo.provider;

import java.math.BigInteger;
import java.security.Key;
import java.security.PublicKey;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import org.opensaml.xml.XMLObject;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.keyinfo.KeyInfoCredentialCriteria;
import org.opensaml.xml.security.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xml.security.keyinfo.KeyInfoProvider;
import org.opensaml.xml.security.keyinfo.KeyInfoCredentialResolver.KeyInfoResolutionContext;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.signature.KeyInfoHelper;
import org.opensaml.xml.signature.X509Data;
import org.opensaml.xml.signature.X509IssuerSerial;
import org.opensaml.xml.signature.X509SubjectName;

/**
 * Implementation of {@link KeyInfoProvider} which supports {@link X509Data}.
 */
public class X509DataProvider extends AbstractKeyInfoProvider {

    /** {@inheritDoc} */
    public boolean handles(XMLObject keyInfoChild) {
        return keyInfoChild instanceof X509Data;
    }

    /** {@inheritDoc} */
    public Credential process(KeyInfoCredentialResolver resolver, XMLObject keyInfoChild, 
            KeyInfoCredentialCriteria criteria, KeyInfoResolutionContext kiContext) throws SecurityException {
        
        if (! handles(keyInfoChild)) {
            return null;
        }
        
        X509Data x509Data = (X509Data) keyInfoChild;
        
        List<X509Certificate> certs = null;
        try {
            certs = KeyInfoHelper.getCertificates(x509Data);
        } catch (CertificateException e) {
            throw new SecurityException("Error extracting certificates from KeyInfo", e);
        }
        
        List<X509CRL> crls = null;
        try {
            crls = KeyInfoHelper.getCRLs(x509Data);
        } catch (CRLException e) {
            throw new SecurityException("Error extracting CRL's from KeyInfo", e);
        }
        
        Key resolvedKey = extractKeyValue(kiContext.getKeyValueCredential());
        X509Certificate entityCert = findEntityCert(certs, x509Data, resolvedKey);
        
        if (entityCert == null && resolvedKey == null) {
            return null;
        }
        
        BasicX509Credential cred = new BasicX509Credential();
        if (entityCert != null) {
           cred.setPublicKey(entityCert.getPublicKey());
        } else {
            //TODO should do this? - have previously resolved KeyValue key, but no entity cert.
            // This cast is potentially dangerous, although in reality probably not
            cred.setPublicKey((PublicKey) resolvedKey);
        }
        cred.setCRLs(crls);
        cred.setEntityCertificateChain(certs);
        
        cred.setKeyNames(kiContext.getKeyNames());
        cred.setCredentialContext(buildContext(criteria.getKeyInfo(), resolver));
        
        return cred;
    }

    /**
     * Find the end-entity cert in the list of certs contained in the X509Data.
     * 
     * @param certs list of {@link java.security.cert.X509Certificate}
     * @param x509Data X509Data element which might contain other info helping to finding the end-entity cert
     * @param resolvedKey a key which might have previously been resolved from a KeyValue
     * @return the end-entity certificate, if found
     */
    protected X509Certificate findEntityCert(List<X509Certificate> certs, X509Data x509Data, Key resolvedKey) {
        if (certs == null || certs.isEmpty()) {
            return null;
        }
        X509Certificate cert = null;
        
        //Check against key already resolved
        cert = findCertFromKey(certs, resolvedKey);
        if (cert != null) {
            return cert;
        }
 
        //Check against any subject names
        cert = findCertFromSubjectNames(certs, x509Data.getX509SubjectNames());
        if (cert != null) {
            return cert;
        }

        //Check against issuer serial
        cert = findCertFromIssuerSerials(certs, x509Data.getX509IssuerSerials());
        if (cert != null) {
            return cert;
        }

        // TODO Check against subject key identifier (SKI) - need to clarify format and encoding
        
        // TODO use some heuristic algorithm to try and figure it out based on the cert list alone.
        //      This would be in X509Utils or somewhere else external to this class.
        return null;
    }
    
    /**
     * Find the certificate from the chain that contains the specified key.
     * 
     * @param certs list of certificates to evaluate
     * @param key key to use as search criteria
     * @return the matching certificate, or null
     */
    protected X509Certificate findCertFromKey(List<X509Certificate> certs, Key key) {
        if (key != null) {
            for (X509Certificate cert : certs) {
                if (cert.getPublicKey().equals(key)) {
                    return cert;
                }
            }
        }
        return null;
    }
    
    /**
     * Find the certificate from the chain that contains one of the specified subject names.
     * 
     * @param certs list of certificates to evaluate
     * @param names X509 subject names to use as search criteria
     * @return the matching certificate, or null
     */
    protected X509Certificate findCertFromSubjectNames(List<X509Certificate> certs, List<X509SubjectName> names) {
        if (names.isEmpty()) {
            return null;
        }
        for (X509SubjectName subjectName : names) {
            X500Principal subjectX500Principal = new X500Principal(subjectName.getValue());
            for (X509Certificate cert : certs) {
                if (cert.getSubjectX500Principal().equals(subjectX500Principal)) {
                    return cert;
                }
            }
        }
        return null;
    }
    
    /**
     * Find the certificate from the chain identified by one of the specified issuer serials.
     * 
     * @param certs list of certificates to evaluate
     * @param serials X509 issuer serials to use as search criteria
     * @return the matching certificate, or null
     */
    protected X509Certificate findCertFromIssuerSerials(List<X509Certificate> certs, List<X509IssuerSerial> serials) {
        for (X509IssuerSerial issuerSerial : serials) {
            X500Principal issuerX500Principal = new X500Principal(issuerSerial.getX509IssuerName().getValue());
            BigInteger serialNumber  = new BigInteger(issuerSerial.getX509SerialNumber().getValue().toString());
            for (X509Certificate cert : certs) {
                if (cert.getIssuerX500Principal().equals(issuerX500Principal) &&
                        cert.getSerialNumber().equals(serialNumber)) {
                    return cert;
                }
            }
        }
        return null;
    }
    
}
