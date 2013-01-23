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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.security.auth.x500.X500Principal;

import net.shibboleth.utilities.java.support.codec.Base64Support;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERString;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure;
import org.bouncycastle.x509.extension.X509ExtensionUtil;
import org.opensaml.security.SecurityException;
import org.opensaml.security.crypto.KeySupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.google.common.net.InetAddresses;

import edu.vt.middleware.crypt.CryptException;
import edu.vt.middleware.crypt.io.X509CertificateCredentialReader;
import edu.vt.middleware.crypt.io.X509CertificatesCredentialReader;
import edu.vt.middleware.crypt.util.HexConverter;

/**
 * Utility class for working with X509 objects.
 */
public class X509Support {

    /** Common Name (CN) OID. */
    public static final String CN_OID = "2.5.4.3";
    
    /** Subject Key Identifier (SKI) OID. */
    public static final String SKI_OID = "2.5.29.14";

    /** RFC 2459 Other Subject Alt Name type. */
    public static final Integer OTHER_ALT_NAME = new Integer(0);

    /** RFC 2459 RFC 822 (email address) Subject Alt Name type. */
    public static final Integer RFC822_ALT_NAME = new Integer(1);

    /** RFC 2459 DNS Subject Alt Name type. */
    public static final Integer DNS_ALT_NAME = new Integer(2);

    /** RFC 2459 X.400 Address Subject Alt Name type. */
    public static final Integer X400ADDRESS_ALT_NAME = new Integer(3);

    /** RFC 2459 Directory Name Subject Alt Name type. */
    public static final Integer DIRECTORY_ALT_NAME = new Integer(4);

    /** RFC 2459 EDI Party Name Subject Alt Name type. */
    public static final Integer EDI_PARTY_ALT_NAME = new Integer(5);

    /** RFC 2459 URI Subject Alt Name type. */
    public static final Integer URI_ALT_NAME = new Integer(6);

    /** RFC 2459 IP Address Subject Alt Name type. */
    public static final Integer IP_ADDRESS_ALT_NAME = new Integer(7);

    /** RFC 2459 Registered ID Subject Alt Name type. */
    public static final Integer REGISTERED_ID_ALT_NAME = new Integer(8);

    /** Constructed. */
    protected X509Support() {

    }
    
    /**
     * Determines the certificate, from the collection, associated with the private key.
     * 
     * @param certs certificates to check
     * @param privateKey entity's private key
     * 
     * @return the certificate associated with entity's private key or null if no certificate in the collection is
     *         associated with the given private key
     * 
     * @throws SecurityException thrown if the public or private keys checked are of an unsupported type
     * 
     * @since 1.2
     */
    @Nullable public static X509Certificate determineEntityCertificate(
            @Nullable final Collection<X509Certificate> certs, @Nullable final PrivateKey privateKey)
                    throws SecurityException {
        if (certs == null || privateKey == null) {
            return null;
        }

        for (X509Certificate certificate : certs) {
            try {
                if (KeySupport.matchKeyPair(certificate.getPublicKey(), privateKey)) {
                    return certificate;
                }
            } catch (SecurityException e) {
                // An exception here is just a false match.
                // Java 7 apparently throws in this case.
            }
        }

        return null;
    }

    /**
     * Gets the commons names that appear within the given distinguished name. The returned list provides the names in
     * the order they appeared in the DN.
     * 
     * @param dn the DN to extract the common names from
     * 
     * @return the common names that appear in the DN in the order they appear or null if the given DN is null
     */
    @Nullable public static List<String> getCommonNames(@Nullable final X500Principal dn) {
        if (dn == null) {
            return null;
        }

        Logger log = getLogger();
        log.debug("Extracting CNs from the following DN: {}", dn.toString());
        List<String> commonNames = new LinkedList<String>();
        try {
            ASN1InputStream asn1Stream = new ASN1InputStream(dn.getEncoded());
            DERObject parent = asn1Stream.readObject();

            String cn = null;
            DERObject dnComponent;
            DERSequence grandChild;
            DERObjectIdentifier componentId;
            for (int i = 0; i < ((DERSequence) parent).size(); i++) {
                dnComponent = ((DERSequence) parent).getObjectAt(i).getDERObject();
                if (!(dnComponent instanceof DERSet)) {
                    log.debug("No DN components.");
                    continue;
                }

                // Each DN component is a set
                for (int j = 0; j < ((DERSet) dnComponent).size(); j++) {
                    grandChild = (DERSequence) ((DERSet) dnComponent).getObjectAt(j).getDERObject();

                    if (grandChild.getObjectAt(0) != null
                            && grandChild.getObjectAt(0).getDERObject() instanceof DERObjectIdentifier) {
                        componentId = (DERObjectIdentifier) grandChild.getObjectAt(0).getDERObject();

                        if (CN_OID.equals(componentId.getId())) {
                            // OK, this dn component is actually a cn attribute
                            if (grandChild.getObjectAt(1) != null
                                    && grandChild.getObjectAt(1).getDERObject() instanceof DERString) {
                                cn = ((DERString) grandChild.getObjectAt(1).getDERObject()).getString();
                                commonNames.add(cn);
                            }
                        }
                    }
                }
            }

            asn1Stream.close();

            return commonNames;

        } catch (IOException e) {
            log.error("Unable to extract common names from DN: ASN.1 parsing failed: " + e);
            return null;
        }
    }

    /**
     * Gets the list of alternative names of a given name type.
     * 
     * @param certificate the certificate to extract the alternative names from
     * @param nameTypes the name types
     * 
     * @return the alt names, of the given type, within the cert
     */
    @Nullable public static List getAltNames(@Nullable final X509Certificate certificate,
            @Nullable final Integer[] nameTypes) {
        if (certificate == null || nameTypes == null || nameTypes.length == 0) {
            return null;
        }

        List<Object> names = new LinkedList<Object>();
        Collection<List<?>> altNames = null;
        try {
            altNames = X509ExtensionUtil.getSubjectAlternativeNames(certificate);
        } catch (CertificateParsingException e) {
            getLogger().error("Encountered an problem trying to extract Subject Alternate "
                    + "Name from supplied certificate: " + e);
            return names;
        }

        if (altNames != null) {
            // 0th position represents the alt name type
            // 1st position contains the alt name data
            for (List altName : altNames) {
                for (Integer nameType : nameTypes) {
                    if (altName.get(0).equals(nameType)) {
                        names.add(convertAltNameType(nameType, altName.get(1)));
                        break;
                    }
                }
            }
        }

        return names;
    }

    /**
     * Gets the common name components of the issuer and all the subject alt names of a given type.
     * 
     * @param certificate certificate to extract names from
     * @param altNameTypes type of alt names to extract
     * 
     * @return list of subject names in the certificate
     */
    @Nullable public static List getSubjectNames(@Nullable final X509Certificate certificate,
            @Nullable final Integer[] altNameTypes) {
        List issuerNames = new LinkedList();
        
        if (certificate != null) {
            List<String> entityCertCNs = X509Support.getCommonNames(certificate.getSubjectX500Principal());
            if (entityCertCNs != null && !entityCertCNs.isEmpty()) {
                issuerNames.add(entityCertCNs.get(0));
            }
            List<String> entityAltNames = X509Support.getAltNames(certificate, altNameTypes);
            if (entityAltNames != null) {
                issuerNames.addAll(entityAltNames);
            }
        }

        return issuerNames;
    }

    /**
     * Get the plain (non-DER encoded) value of the Subject Key Identifier extension of an X.509 certificate, if
     * present.
     * 
     * @param certificate an X.509 certificate possibly containing a subject key identifier
     * @return the plain (non-DER encoded) value of the Subject Key Identifier extension, or null if the certificate
     *         does not contain the extension
     */
    @Nullable public static byte[] getSubjectKeyIdentifier(@Nonnull final X509Certificate certificate) {
        byte[] derValue = certificate.getExtensionValue(SKI_OID);
        if (derValue == null || derValue.length == 0) {
            return null;
        }

        try {
            SubjectKeyIdentifier ski = new SubjectKeyIdentifierStructure(derValue);
            return ski.getKeyIdentifier();
        } catch (IOException e) {
            getLogger().error("Unable to extract subject key identifier from certificate: ASN.1 parsing failed: " + e);
            return null;
        }
    }
    
    /**
     * Decodes X.509 certificates in DER or PEM format.
     * 
     * @param certs encoded certs
     * 
     * @return decoded certs
     * 
     * @throws CertificateException thrown if the certificates cannot be decoded
     * 
     * @since 1.2
     */
    @Nullable public static Collection<X509Certificate> decodeCertificates(@Nonnull final File certs)
            throws CertificateException {
        Constraint.isNotNull(certs, "Input file cannot be null");
        if (!certs.exists()) {
            throw new CertificateException("Certificate file " + certs.getAbsolutePath() + " does not exist");
        } else if (!certs.canRead()) {
            throw new CertificateException("Certificate file " + certs.getAbsolutePath() + " is not readable");
        }
        
        try {
            return decodeCertificates(Files.toByteArray(certs));
        } catch(IOException e) {
            throw new CertificateException("Error reading certificate file " + certs.getAbsolutePath(), e);
        }
    }

    /**
     * Decodes X.509 certificates in DER or PEM format.
     * 
     * @param certs encoded certs
     * 
     * @return decoded certs
     * 
     * @throws CertificateException thrown if the certificates cannot be decoded
     */
    @Nullable public static Collection<X509Certificate> decodeCertificates(@Nonnull final byte[] certs)
            throws CertificateException {
        X509CertificatesCredentialReader credReader = new X509CertificatesCredentialReader();
        ByteArrayInputStream bais = new ByteArrayInputStream(certs);
        try {
            return Arrays.asList(credReader.read(bais));
        } catch (IOException e) {
            throw new CertificateException("Unable to decode X.509 certificates", e);
        } catch (CryptException e) {
            throw new CertificateException("Unable to decode X.509 certificates", e);
        }
    }
    
    /**
     * Decodes a single X.509 certificate in DER or PEM format.
     * 
     * @param cert encoded cert
     * 
     * @return decoded cert
     * 
     * @throws CertificateException thrown if the certificate can not be decoded
     * 
     * @since 1.2
     */
    @Nullable public static X509Certificate decodeCertificate(@Nonnull final File cert) throws CertificateException {
        Constraint.isNotNull(cert, "Input file cannot be null");
        if (!cert.exists()) {
            throw new CertificateException("Certificate file " + cert.getAbsolutePath() + " does not exist");
        } else if (!cert.canRead()) {
            throw new CertificateException("Certificate file " + cert.getAbsolutePath() + " is not readable");
        }
        
        try {
            return decodeCertificate(Files.toByteArray(cert));
        } catch(IOException e) {
            throw new CertificateException("Error reading certificate file " + cert.getAbsolutePath(), e);
        }
    }
    
    /**
     * Decodes a single X.509 certificate in DER or PEM format.
     * 
     * @param cert encoded cert
     * 
     * @return decoded cert
     * 
     * @throws CertificateException thrown if the certificate cannot be decoded
     */
    @Nullable public static X509Certificate decodeCertificate(@Nonnull final byte[] cert) throws CertificateException {
        X509CertificateCredentialReader credReader = new X509CertificateCredentialReader();
        ByteArrayInputStream bais = new ByteArrayInputStream(cert);
        try {
            return credReader.read(bais);
        } catch (IOException e) {
            throw new CertificateException("Unable to decode X.509 certificate", e);
        } catch (CryptException e) {
            throw new CertificateException("Unable to decode X.509 certificate", e);
        }
    }
    
    /**
     * Decode a single Java certificate from base64 encoded form without PEM headers and footers.
     * 
     * @param base64Cert base64-encoded certificate
     * @return a native Java X509 certificate
     * @throws CertificateException thrown if there is an error constructing certificate
     */
    @Nullable public static X509Certificate decodeCertificate(@Nonnull final String base64Cert)
            throws CertificateException {
        return decodeCertificate(Base64Support.decode(base64Cert));
    }
    
    /**
     * Decodes CRLs in DER or PKCS#7 format. If in PKCS#7 format only the CRLs are decoded; the rest of the content is
     * ignored.
     * 
     * @param crls encoded CRLs
     * 
     * @return decoded CRLs
     * 
     * @throws CRLException thrown if the CRLs can not be decoded
     * 
     * @since 1.2
     */
    @Nullable public static Collection<X509CRL> decodeCRLs(@Nonnull final File crls) throws CRLException{
        Constraint.isNotNull(crls, "Input file cannot be null");
        if (!crls.exists()) {
            throw new CRLException("CRL file " + crls.getAbsolutePath() + " does not exist");
        } else if (!crls.canRead()) {
            throw new CRLException("CRL file " + crls.getAbsolutePath() + " is not readable");
        }
        
        try {
            return decodeCRLs(Files.toByteArray(crls));
        } catch(IOException e) {
            throw new CRLException("Error reading CRL file " + crls.getAbsolutePath(), e);
        }
    }

    /**
     * Decodes CRLs in DER or PKCS#7 format. If in PKCS#7 format only the CRLs are decoded; the rest of the content is
     * ignored.
     * 
     * @param crls encoded CRLs
     * 
     * @return decoded CRLs
     * 
     * @throws CRLException thrown if the CRLs can not be decoded
     */
    @Nullable public static Collection<X509CRL> decodeCRLs(@Nonnull final byte[] crls) throws CRLException {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return (Collection<X509CRL>) cf.generateCRLs(new ByteArrayInputStream(crls));
        } catch (GeneralSecurityException e) {
            throw new CRLException("Unable to decode X.509 certificates");
        }
    }
    
    /**
     * Decode CRL in base64 encoded form without PEM headers and footers.
     * 
     * @param base64CRL base64-encoded CRL
     * @return a native Java X509 CRL
     * @throws CertificateException thrown if there is an error constructing certificate
     * @throws CRLException thrown if there is an error constructing CRL
     */
    @Nullable public static X509CRL decodeCRL(@Nonnull final String base64CRL)
            throws CertificateException, CRLException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        ByteArrayInputStream input = new ByteArrayInputStream(Base64Support.decode(base64CRL));
        return (java.security.cert.X509CRL) cf.generateCRL(input);
    }

    /**
     * Gets a formatted string representing identifier information from the supplied credential.
     * 
     * <p>
     * This could for example be used in logging messages.
     * </p>
     * 
     * <p>
     * Often it will be the case that a given credential that is being evaluated will NOT have a value for the entity ID
     * property. So extract the certificate subject DN, and if present, the credential's entity ID.
     * </p>
     * 
     * @param credential the credential for which to produce a token.
     * @param handler the X.500 DN handler to use. If null, a new instance of {@link InternalX500DNHandler} will be
     *            used.
     * 
     * @return a formatted string containing identifier information present in the credential
     */
    @Nonnull public static String getIdentifiersToken(@Nonnull final X509Credential credential,
            @Nullable final X500DNHandler handler) {
        Constraint.isNotNull(credential, "Credential cannot be null");
        
        X500DNHandler x500DNHandler;
        if (handler != null) {
            x500DNHandler = handler;
        } else {
            x500DNHandler = new InternalX500DNHandler();
        }
        X500Principal x500Principal = credential.getEntityCertificate().getSubjectX500Principal();
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        builder.append(String.format("subjectName='%s'", x500DNHandler.getName(x500Principal)));
        if (!Strings.isNullOrEmpty(credential.getEntityId())) {
            builder.append(String.format(" |credential entityID='%s'", StringSupport.trimOrNull(credential
                    .getEntityId())));
        }
        builder.append(']');
        return builder.toString();
    }

    /**
     * Convert types returned by Bouncy Castle X509ExtensionUtil.getSubjectAlternativeNames(X509Certificate) to be
     * consistent with what is documented for: java.security.cert.X509Certificate#getSubjectAlternativeNames.
     * 
     * @param nameType the alt name type
     * @param nameValue the alt name value
     * @return converted representation of name value, based on type
     */
    @Nullable private static Object convertAltNameType(@Nonnull final Integer nameType,
            @Nonnull final Object nameValue) {
        Logger log = getLogger();
        
        if (DIRECTORY_ALT_NAME.equals(nameType) || DNS_ALT_NAME.equals(nameType) || RFC822_ALT_NAME.equals(nameType)
                || URI_ALT_NAME.equals(nameType) || REGISTERED_ID_ALT_NAME.equals(nameType)) {

            // these are just strings in the appropriate format already, return as-is
            return nameValue;
        } else if (IP_ADDRESS_ALT_NAME.equals(nameType)) {
            // this is a byte[], IP addr in network byte order
            byte [] nameValueBytes = (byte[]) nameValue;
            try {
                return InetAddresses.toAddrString(InetAddress.getByAddress(nameValueBytes));
            } catch (UnknownHostException e) {
                HexConverter hexConverter = new HexConverter(true);
                log.warn("Was unable to convert IP address alt name byte[] to string: " +
                        hexConverter.fromBytes(nameValueBytes), e);
                return null;
            }
        } else if (EDI_PARTY_ALT_NAME.equals(nameType) || X400ADDRESS_ALT_NAME.equals(nameType)
                || OTHER_ALT_NAME.equals(nameType)) {

            // these have no defined representation, just return a DER-encoded byte[]
            return ((DERObject) nameValue).getDEREncoded();
        } else {
            log.warn("Encountered unknown alt name type '{}', adding as-is", nameType);
            return nameValue;
        }
    }
    
    /**
     * Get an SLF4J Logger.
     * 
     * @return a Logger instance
     */
    @Nonnull private static Logger getLogger() {
        return LoggerFactory.getLogger(X509Support.class);
    }
    
}