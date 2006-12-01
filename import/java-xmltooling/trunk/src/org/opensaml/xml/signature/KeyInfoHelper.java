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

package org.opensaml.xml.signature;

import java.io.ByteArrayInputStream;
import java.security.Key;
import java.security.PublicKey;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.List;

import org.opensaml.xml.util.Base64;

import javolution.util.FastList;

/**
 * Utility class for working with data inside a KeyInfo object.
 * 
 * Methods are provided for converting the representation stored in the XMLTooling
 * KeyInfo to java.security native types, and for storing java.security native
 * types inside a KeyInfo.
 */
public class KeyInfoHelper {
    
    /** Factory for X509Certificate and X509CRL creation */
    private static CertificateFactory x509CertFactory;
    
    /**
     * Get the Java certificate factory singleton
     * @return CertificateFactory
     * @throws CertificateException
     */
    protected static CertificateFactory getX509CertFactory() throws CertificateException {
        
        if (x509CertFactory == null) {
            x509CertFactory = CertificateFactory.getInstance("X.509");
        }

        return x509CertFactory;
    }
    
    /**
     * Get a List of the Java X509Certificates within the given KeyInfo
     * 
     * @param keyInfo
     * @return a list of Java X509Certificates
     * @throws CertificateException
     */
    public static List<X509Certificate> getCertificates(KeyInfo keyInfo) throws CertificateException {
        FastList<X509Certificate> certList = new FastList<X509Certificate>();
        
        if (keyInfo == null){
            return certList;
        }
        
        List<X509Data> x509Datas = keyInfo.getX509Datas();
        for (X509Data x509Data : x509Datas) {
           if  (x509Data != null) {
               certList.addAll(getCertificates(x509Data));
           }
        }
        
        return certList;
    }
    
    /**
     * Get a List of the Java X509Certificates within the given X509Data
     * 
     * @param x509Data
     * @return a list of Java X509Certificates
     * @throws CertificateException
     */
    public static List<X509Certificate> getCertificates(X509Data x509Data) throws CertificateException {
        FastList<X509Certificate> certList = new FastList<X509Certificate>();
        
        if (x509Data == null) {
            return certList;
        }
        
        for (org.opensaml.xml.signature.X509Certificate xmlCert: x509Data.getX509Certificates()) {
            if (xmlCert != null && xmlCert.getValue() != null) {
                X509Certificate newCert = getCertificate(xmlCert);
                certList.add(newCert);
            }
        }
        
        return certList;
    }
    
    /**
     * Convert an XMLTooling X509Certifcate object into a native Java representation.
     * 
     * @param xmlCert
     * @return a Java X509Certificate
     * @throws CertificateException
     */
    public static X509Certificate getCertificate(org.opensaml.xml.signature.X509Certificate xmlCert) 
        throws CertificateException {
        
        if (xmlCert == null || xmlCert.getValue() == null) {
            return null;
        }
        
        CertificateFactory cf = getX509CertFactory();
        
        ByteArrayInputStream input = new ByteArrayInputStream(Base64.decode(xmlCert.getValue()));
        X509Certificate newCert = (X509Certificate) cf.generateCertificate(input);
        
        return newCert;
    }
    
    /**
     * Get a List of the Java X509CRLs within the given KeyInfo
     * 
     * @param keyInfo
     * @return a list Java X509CRLs
     * @throws CRLException
     * @throws CertificateException
     */
    public static List<X509CRL> getCRLs(KeyInfo keyInfo) throws CRLException, CertificateException {
        FastList<X509CRL> crlList = new FastList<X509CRL>();
        
        if (keyInfo == null){
            return crlList;
        }
        
        List<X509Data> x509Datas = keyInfo.getX509Datas();
        for (X509Data x509Data : x509Datas) {
           if  (x509Data != null) {
               crlList.addAll(getCRLs(x509Data));
           }
        }
        
        return crlList;
        
    }
    
    /**
     * Get a List of the Java X509CRLs within the given X509Data
     * 
     * @param x509Data
     * @return a list of Java X509CRLs
     * @throws CRLException
     * @throws CertificateException
     */
    public static List<X509CRL> getCRLs(X509Data x509Data) throws CRLException, CertificateException {
        FastList<X509CRL> crlList = new FastList<X509CRL>();
        
        if (x509Data == null) {
            return crlList;
        }
        
        for (org.opensaml.xml.signature.X509CRL xmlCRL: x509Data.getX509CRLs()) {
            if (xmlCRL != null && xmlCRL.getValue() != null) {
                X509CRL newCRL = getCRL(xmlCRL);
                crlList.add(newCRL);
            }
        }
        
        return crlList;
    }
    
    /**
     * Convert an XMLTooling X509CRL object into a native Java representation.
     * 
     * @param xmlCRL
     * @return a native Java security X509CRL object
     * @throws CertificateException
     * @throws CRLException
     */
    public static X509CRL getCRL(org.opensaml.xml.signature.X509CRL xmlCRL) 
        throws CertificateException, CRLException {
        
        if (xmlCRL == null || xmlCRL.getValue() == null) {
            return null;
        }
        
        CertificateFactory cf = getX509CertFactory();
        
        ByteArrayInputStream input = new ByteArrayInputStream(Base64.decode(xmlCRL.getValue()));
        X509CRL newCRL = (X509CRL) cf.generateCRL(input);
        
        return newCRL;
    }
    
    /** TODO
     * 
     * @param keyInfo
     * @param cert
     */
    public static void addCertificate(KeyInfo keyInfo, X509Certificate cert) {
        //TODO
        // if an X509Data child exists, add to it (if multiple the first?),
        // else create a new one
    }
    
    /** TODO 
     * 
     * @param x509Data
     * @param cert
     */
    public static void addCertificate(X509Data x509Data, X509Certificate cert) {
        //TODO
    }
    
    /** TODO
     * 
     * @param keyInfo
     * @param crl
     */
    public static void addCRL(KeyInfo keyInfo, X509CRL crl) {
        //TODO
    }
    
    /**
     * @param x509Data
     * @param crl
     */
    public static void addCRL(X509Data x509Data, X509CRL crl) {
        //TODO
    }
    
    /** TODO 
     * 
     * @param keyInfo
     * @param pk
     */
    public static void addPublicKey(KeyInfo keyInfo, PublicKey pk) {
        //TODO
        // differentiate between RSA and DSA keys, and add to KeyValue
    }
    
    /** TODO
     * 
     * @param keyInfo
     * @return a list of native Java security PublicKey objects
     */
    public static List<PublicKey> getPublicKeys(KeyInfo keyInfo) {
        FastList<PublicKey> pkList = new FastList<PublicKey>();
        
        if (keyInfo == null){
            return pkList;
        }
        
        //TODO
        // What exactly are we talking about here... ?
        
        return pkList;
    }
    
    /** TODO
     * 
     * @param keyValue
     * @return a native Java security PublicKey object
     */
    public PublicKey getPublicKey(KeyValue keyValue) {
        //TODO
        //What exactly are we talking about here... ?
        return null;
    }
    
    /** TODO
     * 
     * @param keyValue
     * @return a native Java security Key object
     */
    public Key getKey(KeyValue keyValue) {
        //TODO
        //What exactly are we talking about here... ?
        return null;
    }
    
    /** 
     * Get the set of KeyNames inside the specified KeyInfo as 
     * a list of strings.
     * 
     * @param keyInfo
     * @return a list of key name strings
     */
    public static List<String> getKeyNames(KeyInfo keyInfo) {
        FastList<String> keynameList = new FastList<String>();
        
        if (keyInfo == null){
            return keynameList;
        }
        
        List<KeyName> keyNames = keyInfo.getKeyNames();
        for (KeyName keyName: keyNames) {
           if (keyName.getValue() != null) {
               keynameList.add(keyName.getValue());
           }
        }
        
        return keynameList;
    }
    
}
