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

package org.opensaml.xml.security.keyinfo;

import java.math.BigInteger;
import java.security.KeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.DSAParameterSpec;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.security.x509.X509Util;
import org.opensaml.xml.signature.DSAKeyValue;
import org.opensaml.xml.signature.Exponent;
import org.opensaml.xml.signature.G;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.signature.KeyName;
import org.opensaml.xml.signature.KeyValue;
import org.opensaml.xml.signature.Modulus;
import org.opensaml.xml.signature.P;
import org.opensaml.xml.signature.Q;
import org.opensaml.xml.signature.RSAKeyValue;
import org.opensaml.xml.signature.X509Data;
import org.opensaml.xml.signature.X509IssuerName;
import org.opensaml.xml.signature.X509IssuerSerial;
import org.opensaml.xml.signature.X509SKI;
import org.opensaml.xml.signature.X509SerialNumber;
import org.opensaml.xml.signature.X509SubjectName;
import org.opensaml.xml.signature.Y;
import org.opensaml.xml.util.Base64;
import org.opensaml.xml.util.DatatypeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for working with data inside a KeyInfo object.
 * 
 * Methods are provided for converting the representation stored in the XMLTooling KeyInfo to Java
 * java.security native types, and for storing these Java native types inside a KeyInfo.
 */
public class KeyInfoHelper {

    /** Class logger. */
    private static Logger log = LoggerFactory.getLogger(KeyInfoHelper.class);
    
    /** Factory for {@link java.security.cert.X509Certificate} and
     * {@link java.security.cert.X509CRL} creation. */
    private static CertificateFactory x509CertFactory;
    
    /** Constructor. */
    protected KeyInfoHelper(){
        
    }    

    /**
     * Get the set of key names inside the specified {@link KeyInfo} as a list of strings.
     * 
     * @param keyInfo {@link KeyInfo} to retrieve key names from
     * 
     * @return a list of key name strings
     */
    public static List<String> getKeyNames(KeyInfo keyInfo) {
        List<String> keynameList = new LinkedList<String>();

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
     * Add a new {@link KeyName} value to a KeyInfo.
     * 
     * @param keyInfo the KeyInfo to which to add the new value
     * @param keyNameValue the new key name value to add
     */
    public static void addKeyName(KeyInfo keyInfo, String keyNameValue) {
        KeyName keyName = (KeyName) Configuration.getBuilderFactory()
            .getBuilder(KeyName.DEFAULT_ELEMENT_NAME)
            .buildObject(KeyName.DEFAULT_ELEMENT_NAME);
        keyName.setValue(keyNameValue);
        keyInfo.getKeyNames().add(keyName);
    }

    /**
     * Get a list of the Java {@link java.security.cert.X509Certificate} within the given KeyInfo.
     * 
     * @param keyInfo key info to extract the certificates from
     * 
     * @return a list of Java {@link java.security.cert.X509Certificate}s
     * 
     * @throws CertificateException thrown if there is a problem converting the 
     *          X509 data into {@link java.security.cert.X509Certificate}s.
     */
    public static List<X509Certificate> getCertificates(KeyInfo keyInfo) throws CertificateException {
        List<X509Certificate> certList = new LinkedList<X509Certificate>();

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
     * Get a list of the Java {@link java.security.cert.X509Certificate} within the given {@link X509Data}.
     * 
     * @param x509Data {@link X509Data} from which to extract the certificate
     * 
     * @return a list of Java {@link java.security.cert.X509Certificate}s
     * 
     * @throws CertificateException thrown if there is a problem converting the 
     *          X509 data into {@link java.security.cert.X509Certificate}s.
     */
    public static List<X509Certificate> getCertificates(X509Data x509Data) throws CertificateException {
        List<X509Certificate> certList = new LinkedList<X509Certificate>();

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
     * Convert an {@link org.opensaml.xml.signature.X509Certificate} into a native Java representation.
     * 
     * @param xmlCert an {@link org.opensaml.xml.signature.X509Certificate}
     * 
     * @return a {@link java.security.cert.X509Certificate}
     * 
     * @throws CertificateException thrown if there is a problem converting the 
     *           X509 data into {@link java.security.cert.X509Certificate}s.
     */
    public static X509Certificate getCertificate(org.opensaml.xml.signature.X509Certificate xmlCert)
            throws CertificateException {

        if (xmlCert == null || xmlCert.getValue() == null) {
            return null;
        }

        Collection<X509Certificate> certs = X509Util.decodeCertificate(Base64.decode(xmlCert.getValue()));
        if (certs != null && certs.iterator().hasNext()) {
            return certs.iterator().next();
        } else {
            return null;
        }
    }

    /**
     * Get a list of the Java {@link java.security.cert.X509CRL}s within the given {@link KeyInfo}.
     * 
     * @param keyInfo the {@link KeyInfo} to extract the CRL's from
     * 
     * @return a list of Java {@link java.security.cert.X509CRL}s
     * 
     * @throws CRLException thrown if there is a problem converting the 
     *          CRL data into {@link java.security.cert.X509CRL}s
     */
    public static List<X509CRL> getCRLs(KeyInfo keyInfo) throws CRLException {
        List<X509CRL> crlList = new LinkedList<X509CRL>();

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
     * Get a list of the Java {@link java.security.cert.X509CRL}s within the given {@link X509Data}.
     * 
     * @param x509Data {@link X509Data} to extract the CRLs from
     * 
     * @return a list of Java {@link java.security.cert.X509CRL}s
     * 
     * @throws CRLException thrown if there is a problem converting the 
     *          CRL data into {@link java.security.cert.X509CRL}s
     */
    public static List<X509CRL> getCRLs(X509Data x509Data) throws CRLException {
        List<X509CRL> crlList = new LinkedList<X509CRL>();

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
     * Convert an {@link org.opensaml.xml.signature.X509CRL} into a native Java representation.
     * 
     * @param xmlCRL object to extract the CRL from
     * 
     * @return a native Java {@link java.security.cert.X509CRL} object
     * 
     * @throws CRLException thrown if there is a problem converting the 
     *          CRL data into {@link java.security.cert.X509CRL}s
     */
    public static X509CRL getCRL(org.opensaml.xml.signature.X509CRL xmlCRL) throws CRLException {

        if (xmlCRL == null || xmlCRL.getValue() == null) {
            return null;
        }
        
        Collection<X509CRL> crls = X509Util.decodeCRLs(Base64.decode(xmlCRL.getValue()));
        return crls.iterator().next();
    }

    /**
     * Converts a native Java {@link java.security.cert.X509Certificate} into the corresponding 
     * XMLObject and stores it in a {@link KeyInfo} in the first {@link X509Data} element. 
     * The X509Data element will be created if necessary.
     * 
     * @param keyInfo the {@link KeyInfo} object into which to add the certificate
     * @param cert the Java {@link java.security.cert.X509Certificate} to add
     * @throws CertificateEncodingException thrown when there is an error converting the Java 
     *           certificate representation to the XMLObject representation
     */
    public static void addCertificate(KeyInfo keyInfo, X509Certificate cert) throws CertificateEncodingException {
        X509Data x509Data;
        if (keyInfo.getX509Datas().size() == 0) {
            x509Data = (X509Data) Configuration.getBuilderFactory()
                .getBuilder(X509Data.DEFAULT_ELEMENT_NAME)
                .buildObject(X509Data.DEFAULT_ELEMENT_NAME);
            keyInfo.getX509Datas().add(x509Data);
        } else {
            x509Data = keyInfo.getX509Datas().get(0);
        }
        x509Data.getX509Certificates().add(buildX509Certificate(cert));
    }

    /**
     * Converts a native Java {@link java.security.cert.X509CRL} into the corresponding XMLObject and stores it
     * in a {@link KeyInfo} in the first {@link X509Data} element.  The X509Data element
     * will be created if necessary.
     * 
     * @param keyInfo the {@link KeyInfo} object into which to add the CRL
     * @param crl the Java {@link java.security.cert.X509CRL} to add
     * @throws CRLException thrown when there is an error converting the Java 
     *           CRL representation to the XMLObject representation
     */
    public static void addCRL(KeyInfo keyInfo, X509CRL crl) throws CRLException {
        X509Data x509Data;
        if (keyInfo.getX509Datas().size() == 0) {
            x509Data = (X509Data) Configuration.getBuilderFactory()
                .getBuilder(X509Data.DEFAULT_ELEMENT_NAME)
                .buildObject(X509Data.DEFAULT_ELEMENT_NAME);
            keyInfo.getX509Datas().add(x509Data);
        } else {
            x509Data = keyInfo.getX509Datas().get(0);
        }
        x509Data.getX509CRLs().add(buildX509CRL(crl));
    }
    
    /**
     * Builds an {@link org.opensaml.xml.signature.X509Certificate} XMLObject from a native 
     * Java {@link java.security.cert.X509Certificate}.
     * 
     * @param cert the Java {@link java.security.cert.X509Certificate} to convert
     * @return a {@link org.opensaml.xml.signature.X509Certificate} XMLObject
     * @throws CertificateEncodingException thrown when there is an error converting the Java 
     *           certificate representation to the XMLObject representation
     */
    public static org.opensaml.xml.signature.X509Certificate 
    buildX509Certificate(X509Certificate cert) throws CertificateEncodingException {
        org.opensaml.xml.signature.X509Certificate xmlCert =
            (org.opensaml.xml.signature.X509Certificate) Configuration.getBuilderFactory()
            .getBuilder(org.opensaml.xml.signature.X509Certificate.DEFAULT_ELEMENT_NAME)
            .buildObject(org.opensaml.xml.signature.X509Certificate.DEFAULT_ELEMENT_NAME);
        
        xmlCert.setValue(Base64.encodeBytes(cert.getEncoded()));
        
        return xmlCert;
    }
    
    /**
     * Builds an {@link org.opensaml.xml.signature.X509CRL} XMLObject from
     * a native Java {@link java.security.cert.X509CRL}.
     * 
     * @param crl the Java {@link java.security.cert.X509CRL} to convert
     * @return a {@link org.opensaml.xml.signature.X509CRL} XMLObject
     * @throws CRLException thrown when there is an error converting the Java 
     *           CRL representation to the XMLObject representation
     */
    public static org.opensaml.xml.signature.X509CRL buildX509CRL(X509CRL crl) throws CRLException {
        org.opensaml.xml.signature.X509CRL xmlCRL =
            (org.opensaml.xml.signature.X509CRL) Configuration.getBuilderFactory()
            .getBuilder(org.opensaml.xml.signature.X509CRL.DEFAULT_ELEMENT_NAME)
            .buildObject(org.opensaml.xml.signature.X509CRL.DEFAULT_ELEMENT_NAME);
        
        xmlCRL.setValue(Base64.encodeBytes(crl.getEncoded()));
        
        return xmlCRL;
    }
    
    /**
     * Build an {@link X509SubjectName} containing a given subject name.
     * 
     * @param subjectName the name content
     * @return the new X509SubjectName
     */
    public static X509SubjectName buildX509SubjectName(String subjectName) {
        X509SubjectName xmlSubjectName = (X509SubjectName) Configuration.getBuilderFactory()
            .getBuilder(X509SubjectName.DEFAULT_ELEMENT_NAME)
            .buildObject(X509SubjectName.DEFAULT_ELEMENT_NAME);
        xmlSubjectName.setValue(subjectName); 
        return xmlSubjectName;
    }
    
    /**
     * Build an {@link X509IssuerSerial} containing a given issuer name and serial number.
     * 
     * @param issuerName the name content
     * @param serialNumber the serial number content
     * @return the new X509IssuerSerial
     */
    public static X509IssuerSerial buildX509IssuerSerial(String issuerName, BigInteger serialNumber) {
        X509IssuerName xmlIssuerName = (X509IssuerName) Configuration.getBuilderFactory()
            .getBuilder(X509IssuerName.DEFAULT_ELEMENT_NAME)
        .buildObject(X509IssuerName.DEFAULT_ELEMENT_NAME);
        xmlIssuerName.setValue(issuerName);
        
        X509SerialNumber xmlSerialNumber = (X509SerialNumber) Configuration.getBuilderFactory()
            .getBuilder(X509SerialNumber.DEFAULT_ELEMENT_NAME)
            .buildObject(X509SerialNumber.DEFAULT_ELEMENT_NAME);
        xmlSerialNumber.setValue(serialNumber);
        
        X509IssuerSerial xmlIssuerSerial = (X509IssuerSerial) Configuration.getBuilderFactory()
            .getBuilder(X509IssuerSerial.DEFAULT_ELEMENT_NAME)
            .buildObject(X509IssuerSerial.DEFAULT_ELEMENT_NAME);
        xmlIssuerSerial.setX509IssuerName(xmlIssuerName);
        xmlIssuerSerial.setX509SerialNumber(xmlSerialNumber);
        
        return xmlIssuerSerial;
    }
    
    /**
     * Build an {@link X509SKI} containing the subject key identifier extension value contained within
     * a certificate.
     * 
     * @param javaCert the Java X509Certificate from which to extract the subject key identifier value.
     * @return a new X509SKI object, or null if the certificate did not contain the subject key identifier extension
     */
    public static X509SKI buildX509SKI(X509Certificate javaCert) {
        byte[] skiPlainValue = X509Util.getSubjectKeyIdentifier(javaCert);
        if (skiPlainValue == null || skiPlainValue.length == 0) {
            return null;
        }
        
        X509SKI xmlSKI = (X509SKI) Configuration.getBuilderFactory()
            .getBuilder(X509SKI.DEFAULT_ELEMENT_NAME)
            .buildObject(X509SKI.DEFAULT_ELEMENT_NAME);
        xmlSKI.setValue(Base64.encodeBytes(skiPlainValue));
        
        return xmlSKI;
    }

    /**
     * Converts a Java DSA or RSA public key into the corresponding XMLObject and stores it
     * in a {@link KeyInfo} in a new {@link KeyValue} element.
     * 
     * As input, only supports {@link PublicKey}s which are instances of either
     * {@link java.security.interfaces.DSAPublicKey} or
     * {@link java.security.interfaces.RSAPublicKey}
     * 
     * @param keyInfo the {@link KeyInfo} element to which to add the key
     * @param pk the native Java {@link PublicKey} to add
     * @throws IllegalArgumentException thrown if an unsupported public key
     *          type is passed
     */
    public static void addPublicKey(KeyInfo keyInfo, PublicKey pk) throws IllegalArgumentException {
        KeyValue keyValue = (KeyValue) Configuration.getBuilderFactory()
            .getBuilder(KeyValue.DEFAULT_ELEMENT_NAME)
            .buildObject(KeyValue.DEFAULT_ELEMENT_NAME);
        
        if (pk instanceof RSAPublicKey) {
            keyValue.setRSAKeyValue(buildRSAKeyValue((RSAPublicKey) pk));
        } else if (pk instanceof DSAPublicKey) {
            keyValue.setDSAKeyValue(buildDSAKeyValue((DSAPublicKey) pk));
        } else {
           throw new IllegalArgumentException("Only RSAPublicKey and DSAPublicKey are supported");
        }
        
        keyInfo.getKeyValues().add(keyValue);
    }
    
    /**
     * Builds an {@link RSAKeyValue} XMLObject from the Java security RSA public key type.
     * 
     * @param rsaPubKey a native Java {@link RSAPublicKey}
     * @return an {@link RSAKeyValue} XMLObject
     */
    public static RSAKeyValue buildRSAKeyValue(RSAPublicKey rsaPubKey) {
        XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();
        RSAKeyValue rsaKeyValue = (RSAKeyValue) builderFactory
            .getBuilder(RSAKeyValue.DEFAULT_ELEMENT_NAME)
            .buildObject(RSAKeyValue.DEFAULT_ELEMENT_NAME);
        Modulus modulus = (Modulus) builderFactory
            .getBuilder(Modulus.DEFAULT_ELEMENT_NAME)
            .buildObject(Modulus.DEFAULT_ELEMENT_NAME);
        Exponent exponent = (Exponent) builderFactory
            .getBuilder(Exponent.DEFAULT_ELEMENT_NAME)
            .buildObject(Exponent.DEFAULT_ELEMENT_NAME);
        
        modulus.setValueBigInt(rsaPubKey.getModulus());
        rsaKeyValue.setModulus(modulus);
        
        exponent.setValueBigInt(rsaPubKey.getPublicExponent());
        rsaKeyValue.setExponent(exponent);
        
        return rsaKeyValue;
    }
    
    /**
     * Builds a {@link DSAKeyValue} XMLObject from the Java security DSA public key type.
     * 
     * @param dsaPubKey a native Java {@link DSAPublicKey}
     * @return an {@link DSAKeyValue} XMLObject
     */
    public static DSAKeyValue buildDSAKeyValue(DSAPublicKey dsaPubKey) {
        XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();
        DSAKeyValue dsaKeyValue = (DSAKeyValue) builderFactory
            .getBuilder(DSAKeyValue.DEFAULT_ELEMENT_NAME)
            .buildObject(DSAKeyValue.DEFAULT_ELEMENT_NAME);
        Y y = (Y) builderFactory.getBuilder(Y.DEFAULT_ELEMENT_NAME).buildObject(Y.DEFAULT_ELEMENT_NAME);
        G g = (G) builderFactory.getBuilder(G.DEFAULT_ELEMENT_NAME).buildObject(G.DEFAULT_ELEMENT_NAME);
        P p = (P) builderFactory.getBuilder(P.DEFAULT_ELEMENT_NAME).buildObject(P.DEFAULT_ELEMENT_NAME);
        Q q = (Q) builderFactory.getBuilder(Q.DEFAULT_ELEMENT_NAME).buildObject(Q.DEFAULT_ELEMENT_NAME);
        
        y.setValueBigInt(dsaPubKey.getY());
        dsaKeyValue.setY(y);
        
        g.setValueBigInt(dsaPubKey.getParams().getG());
        dsaKeyValue.setG(g);
        
        p.setValueBigInt(dsaPubKey.getParams().getP());
        dsaKeyValue.setP(p);
        
        q.setValueBigInt(dsaPubKey.getParams().getQ());
        dsaKeyValue.setQ(q);
        
        return dsaKeyValue;
    }


    /**
     * Extracts all the public keys within the given {@link KeyInfo}'s {@link KeyValue}s.  This method only
     * supports DSA and RSA key types.
     * 
     * @param keyInfo {@link KeyInfo} to extract the keys out of
     * 
     * @return a list of native Java {@link PublicKey} objects
     * 
     * @throws KeyException thrown if the given key data can not be converted into {@link PublicKey}
     */
    public static List<PublicKey> getPublicKeys(KeyInfo keyInfo) throws KeyException{
        List<PublicKey> keys = new LinkedList<PublicKey>();

        if (keyInfo == null || keyInfo.getKeyValues() == null) {
            return keys;
        }
        
        for(KeyValue keyDescriptor : keyInfo.getKeyValues()){
            keys.add(getKey(keyDescriptor));
        }

        return keys;
    }

    /**
     * Extracts the DSA or RSA public key within the {@link KeyValue}.
     * 
     * @param keyValue the {@link KeyValue} to extract the key from
     * 
     * @return a native Java security {@link java.security.Key} object
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
     * Builds an DSA key from a {@link DSAKeyValue} element.  The element must contain values
     * for all required DSA public key parameters, including values for shared key family
     * values P, Q and G.
     * 
     * @param keyDescriptor the {@link DSAKeyValue} key descriptor
     * 
     * @return a new {@link DSAPublicKey} instance of {@link PublicKey}
     * 
     * @throws KeyException thrown if the key algorithm is not supported by the JCE or the key spec does not
     *             contain valid information
     */
    public static PublicKey getDSAKey(DSAKeyValue keyDescriptor) throws KeyException {
        if (! hasCompleteDSAParams(keyDescriptor)) {
            throw new KeyException("DSAKeyValue element did not contain at least one of DSA parameters P, Q or G");
        }
        
        BigInteger gComponent = keyDescriptor.getG().getValueBigInt();
        BigInteger pComponent = keyDescriptor.getP().getValueBigInt();
        BigInteger qComponent = keyDescriptor.getQ().getValueBigInt();

        DSAParams  dsaParams = new DSAParameterSpec(pComponent, qComponent, gComponent);
        return getDSAKey(keyDescriptor, dsaParams);
    }
    
    /**
     * Builds a DSA key from an {@link DSAKeyValue} element and the supplied Java {@link DSAParams},
     * which supplies key material from a shared key family.
     * 
     * @param keyDescriptor the {@link DSAKeyValue} key descriptor
     * @param dsaParams the {@link DSAParams} DSA key family parameters
     * 
     * @return a new {@link DSAPublicKey} instance of {@link PublicKey}
     * 
     * @throws KeyException thrown if the key algorithm is not supported by the JCE or the key spec does not
     *             contain valid information
     */
    public static PublicKey getDSAKey(DSAKeyValue keyDescriptor, DSAParams dsaParams) throws KeyException {
        BigInteger yComponent = keyDescriptor.getY().getValueBigInt();

        DSAPublicKeySpec keySpec = 
            new DSAPublicKeySpec(yComponent, dsaParams.getP(), dsaParams.getQ(), dsaParams.getG());
        return buildKey(keySpec, "DSA");
    }
    
    /**
     * Check whether the specified {@link DSAKeyValue} element has the all optional DSA
     * values which can be shared amongst many keys in a DSA "key family", and
     * are presumed to be known from context.
     * 
     * @param keyDescriptor the {@link DSAKeyValue} element to check
     * @return true if all parameters are present and non-empty, false otherwise
     */
    public static boolean hasCompleteDSAParams(DSAKeyValue keyDescriptor) {
        if (       keyDescriptor.getG() == null || DatatypeHelper.isEmpty(keyDescriptor.getG().getValue())
                || keyDescriptor.getP() == null || DatatypeHelper.isEmpty(keyDescriptor.getP().getValue())
                || keyDescriptor.getQ() == null || DatatypeHelper.isEmpty(keyDescriptor.getQ().getValue())
        ) {
            return false;
        }
        return true;
    }

    /**
     * Builds an RSA key from an {@link RSAKeyValue} element.
     * 
     * @param keyDescriptor the {@link RSAKeyValue} key descriptor
     * 
     * @return a new {@link RSAPublicKey} instance of {@link PublicKey}
     * 
     * @throws KeyException thrown if the key algorithm is not supported by the JCE or the key spec does not
     *             contain valid information
     */
    public static PublicKey getRSAKey(RSAKeyValue keyDescriptor) throws KeyException {
        BigInteger modulus = keyDescriptor.getModulus().getValueBigInt();
        BigInteger exponent = keyDescriptor.getExponent().getValueBigInt();

        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, exponent);
        return buildKey(keySpec, "RSA");
    }
    
    /**
     * Decode a base64-encoded ds:CryptoBinary value to a native Java BigInteger type.
     *
     * @param base64Value base64-encoded CryptoBinary value
     * @return the decoded BigInteger
     */
    public static final BigInteger decodeBigIntegerFromCryptoBinary(String base64Value) {
       return new BigInteger(1, Base64.decode(base64Value));
    }
    
    /**
     * Encode a native Java BigInteger type to a base64-encoded ds:CryptoBinary value.
     *
     * @param bigInt the BigInteger value
     * @return the encoded CryptoBinary value
     */
    public static final String encodeCryptoBinaryFromBigInteger(BigInteger bigInt) {
        // This code is really complicated, for now just use the Apache xmlsec lib code directly.
        byte[] bigIntBytes = org.apache.xml.security.utils.Base64.encode(bigInt, bigInt.bitLength());
        return Base64.encodeBytes(bigIntBytes);
    }

    /**
     * Generates a public key from the given key spec.
     * 
     * @param keySpec {@link KeySpec} specification for the key
     * @param keyAlgorithm key generation algorithm, only DSA and RSA supported
     * 
     * @return the generated {@link PublicKey}
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
            throw new KeyException(keyAlgorithm + "algorithm is not supported by the JCE", e);
        } catch (InvalidKeySpecException e) {
            log.error("Invalid key information", e);
            throw new KeyException("Invalid key information", e);
        }
    }
    
    /**
     * Get the Java certificate factory singleton.
     * 
     * @return {@link CertificateFactory} the factory used to create X509 certificate objects
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