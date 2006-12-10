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
import java.math.BigInteger;
import java.security.KeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.List;

import javolution.util.FastList;

import org.apache.log4j.Logger;
import org.opensaml.xml.util.Base64;

/**
 * Utility class for working with data inside a KeyInfo object.
 * 
 * Methods are provided for converting the representation stored in the XMLTooling KeyInfo to java.security native
 * types, and for storing java.security native types inside a KeyInfo.
 */
public class KeyInfoHelper {

    /** Class logger. */
    private static Logger log = Logger.getLogger(KeyInfoHelper.class);
    
    /** Factory for X509Certificate and X509CRL creation. */
    private static CertificateFactory x509CertFactory;
    
    /** Constructor. */
    protected KeyInfoHelper(){
        
    }    

    /**
     * Get the set of KeyNames inside the specified KeyInfo as a list of strings.
     * 
     * @param keyInfo KeyInfo to retrieve key names from
     * 
     * @return a list of key name strings
     */
    public static List<String> getKeyNames(KeyInfo keyInfo) {
        FastList<String> keynameList = new FastList<String>();

        if (keyInfo == null) {
            return keynameList;
        }

        List<KeyName> keyNames = keyInfo.getKeyNames();
        for (KeyName keyName : keyNames) {
            if (keyName.getValue() != null) {
                keynameList.add(keyName.getValue());
            }
        }

        return keynameList;
    }

    /**
     * Get a List of the Java X509Certificates within the given KeyInfo.
     * 
     * @param keyInfo key info to extract the certificates from
     * 
     * @return a list of Java X509Certificates
     * 
     * @throws CertificateException thrown if there is a problem converting the X509 data into {@link X509Certificate}s.
     */
    public static List<X509Certificate> getCertificates(KeyInfo keyInfo) throws CertificateException {
        FastList<X509Certificate> certList = new FastList<X509Certificate>();

        if (keyInfo == null) {
            return certList;
        }

        List<X509Data> x509Datas = keyInfo.getX509Datas();
        for (X509Data x509Data : x509Datas) {
            if (x509Data != null) {
                certList.addAll(getCertificates(x509Data));
            }
        }

        return certList;
    }

    /**
     * Get a List of the Java X509Certificates within the given X509Data.
     * 
     * @param x509Data X509 data to extract the certificate from
     * 
     * @return a list of Java X509Certificates
     * 
     * @throws CertificateException thrown if there is a problem converting the X509 data into {@link X509Certificate}s.
     */
    public static List<X509Certificate> getCertificates(X509Data x509Data) throws CertificateException {
        FastList<X509Certificate> certList = new FastList<X509Certificate>();

        if (x509Data == null) {
            return certList;
        }

        for (org.opensaml.xml.signature.X509Certificate xmlCert : x509Data.getX509Certificates()) {
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
     * @param xmlCert the XML object representing the X509 certificate
     * 
     * @return a Java X509Certificate
     * 
     * @throws CertificateException thrown if there is a problem converting the X509 data into {@link X509Certificate}s.
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
     * Get a List of the Java X509CRLs within the given KeyInfo.
     * 
     * @param keyInfo the key info to extract the CRLs from
     * 
     * @return a list Java X509CRLs
     * 
     * @throws CRLException thrown if there is a problem converting the CRL data into {@link X509CRL}s
     */
    public static List<X509CRL> getCRLs(KeyInfo keyInfo) throws CRLException {
        FastList<X509CRL> crlList = new FastList<X509CRL>();

        if (keyInfo == null) {
            return crlList;
        }

        List<X509Data> x509Datas = keyInfo.getX509Datas();
        for (X509Data x509Data : x509Datas) {
            if (x509Data != null) {
                crlList.addAll(getCRLs(x509Data));
            }
        }

        return crlList;
    }

    /**
     * Get a List of the Java X509CRLs within the given X509Data.
     * 
     * @param x509Data X509 data to extract the CRLs from
     * 
     * @return a list of Java X509CRLs
     * 
     * @throws CRLException thrown if there is a problem converting the CRL data into {@link X509CRL}s
     */
    public static List<X509CRL> getCRLs(X509Data x509Data) throws CRLException {
        FastList<X509CRL> crlList = new FastList<X509CRL>();

        if (x509Data == null) {
            return crlList;
        }

        for (org.opensaml.xml.signature.X509CRL xmlCRL : x509Data.getX509CRLs()) {
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
     * @param xmlCRL object to extract the CRL from
     * 
     * @return a native Java security X509CRL object
     * 
     * @throws CRLException thrown if there is a problem converting the CRL data into {@link X509CRL}s
     */
    public static X509CRL getCRL(org.opensaml.xml.signature.X509CRL xmlCRL) throws CRLException {

        if (xmlCRL == null || xmlCRL.getValue() == null) {
            return null;
        }
        try {
            CertificateFactory cf = getX509CertFactory();

            ByteArrayInputStream input = new ByteArrayInputStream(Base64.decode(xmlCRL.getValue()));
            X509CRL newCRL = (X509CRL) cf.generateCRL(input);

            return newCRL;
        } catch (CertificateException e) {
            throw new CRLException("Unable to create X509 certificate factory", e);
        }
    }

    /**
     * TODO
     * 
     * @param keyInfo
     * @param cert
     */
    public static void addCertificate(KeyInfo keyInfo, X509Certificate cert) {
        // TODO
        // if an X509Data child exists, add to it (if multiple the first?),
        // else create a new one
    }

    /**
     * TODO
     * 
     * @param x509Data
     * @param cert
     */
    public static void addCertificate(X509Data x509Data, X509Certificate cert) {
        // TODO
    }

    /**
     * TODO
     * 
     * @param keyInfo
     * @param crl
     */
    public static void addCRL(KeyInfo keyInfo, X509CRL crl) {
        // TODO
    }

    /**
     * @param x509Data
     * @param crl
     */
    public static void addCRL(X509Data x509Data, X509CRL crl) {
        // TODO
    }

    /**
     * TODO
     * 
     * @param keyInfo
     * @param pk
     */
    public static void addPublicKey(KeyInfo keyInfo, PublicKey pk) {
        // TODO
        // differentiate between RSA and DSA keys, and add to KeyValue
    }

    /**
     * Extracts all the public keys within the given KeyInfo's {@link KeyValue}s.  This method only
     * supports DSA and RSA key types.
     * 
     * @param keyInfo key info to extract the keys out of
     * 
     * @return a list of native Java security PublicKey objects
     * 
     * @throws KeyException thrown if the given key data can not be converted into {@link PublicKey}
     */
    public static List<PublicKey> getPublicKeys(KeyInfo keyInfo) throws KeyException{
        FastList<PublicKey> keys = new FastList<PublicKey>();

        if (keyInfo == null || keyInfo.getKeyValues() == null) {
            return keys;
        }
        
        for(KeyValue keyDescriptor : keyInfo.getKeyValues()){
            keys.add(getKey(keyDescriptor));
        }

        return keys;
    }

    /**
     * Extracts the DSA or RSA public key within the KeyValue.
     * 
     * @param keyValue the key value to extract the key from
     * 
     * @return a native Java security Key object
     * 
     * @throws KeyException thrown if the given key data can not be converted into {@link PublicKey}
     */
    public static PublicKey getKey(KeyValue keyValue) throws KeyException{
        if(keyValue.getDSAKeyValue() != null){
            return getDSAKey(keyValue.getDSAKeyValue());
        }else if(keyValue.getRSAKeyValue() != null){
            return getRSAKey(keyValue.getRSAKeyValue());
        }else{
            return null;
        }
    }
    
    /**
     * Builds an DSA key from an DSAKeyValue element.
     * 
     * @param keyDescriptor the key descriptor
     * 
     * @return the key described
     * 
     * @throws KeyException thrown if the key algorithm is not supported by the JCE or the key spec does not
     *             contain valid information
     */
    public static PublicKey getDSAKey(DSAKeyValue keyDescriptor) throws KeyException {
        String encodedG = keyDescriptor.getG().getValue();
        BigInteger gComponent = new BigInteger(Base64.decode(encodedG));

        String encodedP = keyDescriptor.getP().getValue();
        BigInteger pComponent = new BigInteger(Base64.decode(encodedP));

        String encodedQ = keyDescriptor.getQ().getValue();
        BigInteger qComponent = new BigInteger(Base64.decode(encodedQ));

        String encodedY = keyDescriptor.getY().getValue();
        BigInteger yComponent = new BigInteger(Base64.decode(encodedY));

        DSAPublicKeySpec keySpec = new DSAPublicKeySpec(yComponent, pComponent, qComponent, gComponent);
        return buildKey(keySpec, "DSA");
    }

    /**
     * Builds an RSA key from an RSAKeyValue element.
     * 
     * @param keyDescriptor the key descriptor
     * 
     * @return the key described
     * 
     * @throws KeyException thrown if the key algorithm is not supported by the JCE or the key spec does not
     *             contain valid information
     */
    public static PublicKey getRSAKey(RSAKeyValue keyDescriptor) throws KeyException {
        String encodedModulus = keyDescriptor.getModulus().getValue();
        BigInteger modulus = new BigInteger(Base64.decode(encodedModulus));

        String encodedExponent = keyDescriptor.getExponent().getValue();
        BigInteger exponent = new BigInteger(Base64.decode(encodedExponent));

        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, exponent);
        return buildKey(keySpec, "RSA");
    }

    /**
     * Generates a public key from the given key spec.
     * 
     * @param keySpec specification for the key
     * @param keyAlgorithm key generation algorithm, only DSA and RSA supported
     * 
     * @return the generated key
     * 
     * @throws KeyException thrown if the key algorithm is not supported by the JCE or the key spec does not
     *             contain valid information
     */
    protected static PublicKey buildKey(KeySpec keySpec, String keyAlgorithm) throws KeyException {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            log.error(keyAlgorithm + " algorithm is not supported by this VM", e);
            throw new KeyException(keyAlgorithm + "RSA algorithm is not supported by the JCE", e);
        } catch (InvalidKeySpecException e) {
            log.error("Invalid key information", e);
            throw new KeyException("Invalid key information", e);
        }
    }
    
    /**
     * Get the Java certificate factory singleton.
     * 
     * @return CertificateFactory the factory used to create X509 certificate objects
     * 
     * @throws CertificateException thrown if the factory can not be created
     */
    protected static CertificateFactory getX509CertFactory() throws CertificateException {

        if (x509CertFactory == null) {
            x509CertFactory = CertificateFactory.getInstance("X.509");
        }

        return x509CertFactory;
    }
}