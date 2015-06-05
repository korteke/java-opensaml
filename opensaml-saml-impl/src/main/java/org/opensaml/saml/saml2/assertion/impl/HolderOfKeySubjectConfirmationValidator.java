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

package org.opensaml.saml.saml2.assertion.impl;

import java.security.KeyException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.collection.LazyList;
import net.shibboleth.utilities.java.support.collection.Pair;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.KeyInfoConfirmationDataType;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.opensaml.xmlsec.keyinfo.KeyInfoSupport;
import org.opensaml.xmlsec.signature.DEREncodedKeyValue;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.KeyValue;
import org.opensaml.xmlsec.signature.X509Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validates a Holder of Key subject confirmation.
 * 
 * <p>
 * A subject confirmation is considered confirmed if one of the
 * following checks has passed:
 * <ul>
 * <li>
 * the presenter's public key (either given explicitly or extracted from the given certificate) matches a
 * {@link KeyValue} or {@link DEREncodedKeyValue} within one of the {@link KeyInfo} entries in the confirmation data
 * </li>
 * <li>
 * the presenter's public cert matches an {@link org.opensaml.xml.signature.X509Certificate} within one of the
 * {@link KeyInfo} entries in the confirmation data
 * </li>
 * </ul>
 * In both cases a "match" is determined via Java <code>equals()</code> comparison.
 * </p>
 * 
 * <p>
 * This validator requires the {@link ValidationContext#getStaticParameters()} to carry the presenter's certificate or
 * public key. The certificate must be bound to the property {@link #PRESENTER_CERT_PARAM} and the key must be bound to
 * the property {@link #PRESENTER_KEY_PARAM}. If both are present the public key of the given certificate must match the
 * given key.
 * </p>
 * 
 * <p>
 * This validator populates the {@link ValidationContext#getDynamicParameters()} property
 * {@link #CONFIRMED_KEY_INFO_PARAM} with the {@link KeyInfo} that confirmed the subject.
 * </p>
 */
@ThreadSafe
public class HolderOfKeySubjectConfirmationValidator extends AbstractSubjectConfirmationValidator {

    /**
     * The name of the {@link ValidationContext#getStaticParameters()} carrying the {@link PublicKey} used by the
     * presenter.
     */
    public static final String PRESENTER_KEY_PARAM = HolderOfKeySubjectConfirmationValidator.class.getName()
            + ".PresenterKey";

    /**
     * The name of the {@link ValidationContext#getStaticParameters()} carrying the {@link X509Certificate} used by the
     * presenter.
     */
    public static final String PRESENTER_CERT_PARAM = HolderOfKeySubjectConfirmationValidator.class.getName()
            + ".PresenterCertificate";

    /**
     * The name of the {@link ValidationContext#getDynamicParameters()} carrying the {@link KeyInfo} that confirmed the
     * subject.
     */
    public static final String CONFIRMED_KEY_INFO_PARAM = HolderOfKeySubjectConfirmationValidator.class.getName()
            + ".ConfirmedKeyInfo";

    /** Class logger. */
    private Logger log = LoggerFactory.getLogger(HolderOfKeySubjectConfirmationValidator.class);

    /** {@inheritDoc} */
    @Nonnull public String getServicedMethod() {
        return SubjectConfirmation.METHOD_HOLDER_OF_KEY;
    }

    /** {@inheritDoc} */
    @Nonnull protected ValidationResult doValidate(@Nonnull final SubjectConfirmation confirmation, 
            @Nonnull final Assertion assertion, @Nonnull final ValidationContext context) 
                    throws AssertionValidationException {
        
        if (!Objects.equals(confirmation.getMethod(), SubjectConfirmation.METHOD_HOLDER_OF_KEY)) {
            return ValidationResult.INDETERMINATE;
        }
        
        log.debug("Attempting holder-of-key subject confirmation");
        if (!isValidConfirmationDataType(confirmation)) {
            String msg = String.format(
                    "Subject confirmation data is not of type '%s'", KeyInfoConfirmationDataType.TYPE_NAME);
            log.debug(msg);
            context.setValidationFailureMessage(msg);
            return ValidationResult.INVALID;
        }

        List<KeyInfo> possibleKeys = getSubjectConfirmationKeyInformation(confirmation, assertion, context);
        if (possibleKeys.isEmpty()) {
            String msg = String.format(
                    "No key information for holder of key subject confirmation in assertion '%s'", assertion.getID());
            log.debug(msg);
            context.setValidationFailureMessage(msg);
            return ValidationResult.INVALID;
        }

        Pair<PublicKey, X509Certificate> keyCertPair = null;
        try {
            keyCertPair = getKeyAndCertificate(context);
        } catch (IllegalArgumentException e) {
            log.warn("Problem with the validation context presenter key/cert params: {}", e.getMessage());
            context.setValidationFailureMessage("Unable to obtain presenter key/cert params from validation context");
            return ValidationResult.INDETERMINATE;
        }
        
        if (keyCertPair.getFirst() == null && keyCertPair.getSecond() == null) {
            log.debug("Neither the presenter's certificate nor its public key were provided");
            context.setValidationFailureMessage("Neither the presenter's certificate nor its public key were provided");
            return ValidationResult.INDETERMINATE;
        }

        for (KeyInfo keyInfo : possibleKeys) {
            if (matchesKeyValue(keyCertPair.getFirst(), keyInfo)) {
                log.debug("Successfully matched public key in subject confirmation data to supplied key param");
                context.getDynamicParameters().put(CONFIRMED_KEY_INFO_PARAM, keyInfo);
                return ValidationResult.VALID;
            } else if (matchesX509Certificate(keyCertPair.getSecond(), keyInfo)) {
                log.debug("Successfully matched certificate in subject confirmation data to supplied cert param");
                context.getDynamicParameters().put(CONFIRMED_KEY_INFO_PARAM, keyInfo);
                return ValidationResult.VALID;
            }
        }

        return ValidationResult.INVALID;
    }

    /**
     * Checks to see whether the schema type of the subject confirmation data, if present, is the required
     * {@link KeyInfoConfirmationDataType#TYPE_NAME}.
     * 
     * @param confirmation subject confirmation bearing the confirmation data to be checked
     * 
     * @return true if the confirmation data's schema type is correct, false otherwise
     * 
     * @throws AssertionValidationException thrown if there is a problem validating the confirmation data type
     */
    protected boolean isValidConfirmationDataType(@Nonnull final SubjectConfirmation confirmation) 
            throws AssertionValidationException {
        QName confirmationDataSchemaType = confirmation.getSubjectConfirmationData().getSchemaType();
        if (confirmationDataSchemaType != null
                && !confirmationDataSchemaType.equals(KeyInfoConfirmationDataType.TYPE_NAME)) {
            log.debug("SubjectConfirmationData xsi:type was non-null and did not match {}",
                    KeyInfoConfirmationDataType.TYPE_NAME);
            return false;
        }
        
        log.debug("SubjectConfirmationData xsi:type was either null or matched {}",
                KeyInfoConfirmationDataType.TYPE_NAME);
        
        return true;
    }

    /**
     * Extracts the presenter's key and/or certificate from the validation context.
     * 
     * @param context current validation context
     * 
     * @return the presenter's key/cert pair, information not available in the context is null
     * 
     * @throws AssertionValidationException thrown if there is a problem obtaining the data
     */
    @Nonnull protected Pair<PublicKey, X509Certificate> getKeyAndCertificate(@Nonnull final ValidationContext context) 
            throws AssertionValidationException {
        PublicKey presenterKey = null;
        try {
            presenterKey = (PublicKey) context.getStaticParameters().get(PRESENTER_KEY_PARAM);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(String.format(
                    "The value of the static validation parameter '%s' was not of the required type '%s'",
                    PRESENTER_KEY_PARAM, PublicKey.class.getName()));
        }

        X509Certificate presenterCert = null;
        try {
            presenterCert = (X509Certificate) context.getStaticParameters().get(PRESENTER_CERT_PARAM);
            if (presenterCert != null) {
                if (presenterKey != null) {
                    if (!presenterKey.equals(presenterCert.getPublicKey())) {
                        throw new IllegalArgumentException(
                                "Presenter's certificate contains a different public key " 
                                + "than the one explicitly given");
                    }
                } else {
                    presenterKey = presenterCert.getPublicKey();
                }
            }
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(String.format(
                    "The value of the static validation parameter '%s' was not of the required type '%s'",
                    PRESENTER_CERT_PARAM, X509Certificate.class.getName()));
        }

        return new Pair<PublicKey, X509Certificate>(presenterKey, presenterCert);
    }

    /**
     * Extracts the {@link KeyInfo}s from the given subject confirmation data.
     * 
     * @param confirmation subject confirmation data
     * @param assertion assertion bearing the subject to be confirmed
     * @param context current message processing context
     * 
     * @return list of key informations available in the subject confirmation data, never null
     * 
     * @throws AssertionValidationException if there is a problem processing the SubjectConfirmation
     *
     */
    @Nonnull protected List<KeyInfo> getSubjectConfirmationKeyInformation(
            @Nonnull final SubjectConfirmation confirmation, @Nonnull final Assertion assertion, 
            @Nonnull final ValidationContext context) throws AssertionValidationException {
        
        SubjectConfirmationData confirmationData = confirmation.getSubjectConfirmationData();

        List<KeyInfo> keyInfos = new LazyList<KeyInfo>();
        for (XMLObject object : confirmationData.getUnknownXMLObjects(KeyInfo.DEFAULT_ELEMENT_NAME)) {
            if (object != null) {
                keyInfos.add((KeyInfo) object);
            }
        }

        log.debug("Found '{}' KeyInfo children of SubjectConfirmationData", keyInfos.size());
        return keyInfos;
    }

    /**
     * Checks whether the supplied public key matches one of the keys in the given KeyInfo.
     * 
     * <p>
     * Evaluates both {@link KeyValue} and {@link DEREncodedKeyValue} children of the KeyInfo.
     * </p>
     * 
     * 
     * <p>
     * Matches are performed using Java <code>equals()</code> against {@link PublicKey}s decoded
     * from the KeyInfo data.
     * </p>
     * 
     * @param key public key presenter of the assertion
     * @param keyInfo key info from subject confirmation of the assertion
     * 
     * @return true if the public key in the certificate matches one of the key values in the key info, false otherwise
     * 
     * @throws AssertionValidationException thrown if there is a problem matching the key value
     */
    protected boolean matchesKeyValue(@Nullable final PublicKey key, @Nonnull final KeyInfo keyInfo) 
            throws AssertionValidationException {
        
        if (key == null) {
            log.debug("Presenter PublicKey was null, skipping KeyValue match");
            return false;
        }
        
        if (matchesKeyValue(key, keyInfo.getKeyValues())) {
            return true;
        }
        
        if (matchesDEREncodedKeyValue(key, keyInfo.getDEREncodedKeyValues())) {
            return true;
        }

        log.debug("Failed to match either a KeyInfo KeyValue or DEREncodedKeyValue against supplied PublicKey param");
        return false;
    }
    
    /**
     * Checks whether the supplied public key matches one of the supplied {@link KeyValue} elements.
     * 
     * <p>
     * Matches are performed using Java <code>equals()</code> against {@link PublicKey}s decoded
     * from the KeyInfo data.
     * </p>
     * 
     * @param key public key presenter of the assertion
     * @param keyValues candidate KeyValue elements
     * 
     * @return true if the public key in the certificate matches one of the key values, false otherwise
     * 
     * @throws AssertionValidationException thrown if there is a problem matching the key value
     */
    protected boolean matchesKeyValue(@Nonnull final PublicKey key, @Nullable final List<KeyValue> keyValues)  {
        
        if (keyValues == null || keyValues.isEmpty()) {
            log.debug("KeyInfo contained no KeyValue children");
            return false;
        }
        
        log.debug("Attempting to match KeyInfo KeyValue to supplied PublicKey param of type: {}", key.getAlgorithm());
        
        for (KeyValue keyValue : keyValues) {
            try {
                PublicKey kiPublicKey = KeyInfoSupport.getKey(keyValue);
                if (Objects.equals(key, kiPublicKey)) {
                    log.debug("Matched KeyValue PublicKey");
                    return true;
                }
            } catch (KeyException e) {
                log.warn("KeyInfo contained KeyValue that can not be parsed", e);
            }
        }
        
        log.debug("Failed to match any KeyValue");
        return false;
    }
    
    
    /**
     * Checks whether the supplied public key matches one of the supplied {@link DEREncodedKeyValue} elements.
     * 
     * <p>
     * Matches are performed using Java <code>equals()</code> against {@link PublicKey}s decoded
     * from the KeyInfo data.
     * </p>
     * 
     * @param key public key presenter of the assertion
     * @param derEncodedKeyValues candidate DEREncodedKeyValue elements
     * 
     * @return true if the public key in the certificate matches one of the DER-encoded key values, false otherwise
     * 
     * @throws AssertionValidationException thrown if there is a problem matching the key value
     */
    protected boolean matchesDEREncodedKeyValue(@Nonnull final PublicKey key, 
            @Nullable final List<DEREncodedKeyValue> derEncodedKeyValues)  {
        
        if (derEncodedKeyValues == null || derEncodedKeyValues.isEmpty()) {
            log.debug("KeyInfo contained no DEREncodedKeyValue children");
            return false;
        }
        
        log.debug("Attempting to match KeyInfo DEREncodedKeyValue to supplied PublicKey param of type: {}", 
                key.getAlgorithm());
        
        for (DEREncodedKeyValue derEncodedKeyValue : derEncodedKeyValues) {
            try {
                PublicKey kiPublicKey = KeyInfoSupport.getKey(derEncodedKeyValue);
                if (Objects.equals(key, kiPublicKey)) {
                    log.debug("Matched DEREncodedKeyValue PublicKey");
                    return true;
                }
            } catch (KeyException e) {
                log.warn("KeyInfo contained DEREncodedKeyValue that can not be parsed", e);
            }
        }
        
        log.debug("Failed to match any DEREncodedKeyValue");
        return false;
    }

    /**
     * Checks whether the presenter's certificate matches a certificate described by the X509Data within the KeyInfo.
     * 
     * 
     * 
     * <p>
     * Matches are performed using Java <code>equals()</code> against {@link X509Certificate}s decoded
     * from the KeyInfo data.
     * </p>
     * 
     * @param cert certificate of the presenter of the assertion
     * @param keyInfo key info from subject confirmation of the assertion
     * 
     * @return true if the presenter's certificate matches the key described by an X509Data within the KeyInfo, false
     *         otherwise.
     *         
     * @throws AssertionValidationException thrown if there is a problem matching the certificate
     */
    protected boolean matchesX509Certificate(@Nullable final X509Certificate cert, @Nonnull final KeyInfo keyInfo) 
            throws AssertionValidationException {
        if (cert == null) {
            log.debug("Presenter X509Certificate was null, skipping certificate match");
            return false;
        }

        List<X509Data> x509Datas = keyInfo.getX509Datas();
        if (x509Datas == null || x509Datas.isEmpty()) {
            log.debug("KeyInfo contained no X509Data children, skipping certificate match");
            return false;
        }
        
        log.debug("Attempting to match KeyInfo X509Data to supplied X509Certificate param");

        List<org.opensaml.xmlsec.signature.X509Certificate> xmlCertificates;
        for (X509Data data : x509Datas) {
            xmlCertificates = data.getX509Certificates();
            if (xmlCertificates == null || xmlCertificates.isEmpty()) {
                log.debug("X509Data contained no X509Certificate children, skipping certificate match");
                continue;
            }

            for (org.opensaml.xmlsec.signature.X509Certificate xmlCertificate : xmlCertificates) {
                try {
                    X509Certificate kiCert = KeyInfoSupport.getCertificate(xmlCertificate);
                    if (Objects.equals(cert, kiCert)) {
                        log.debug("Matched X509Certificate");
                        return true;
                    }
                } catch (CertificateException e) {
                    log.warn("KeyInfo contained Certificate value that can not be parsed", e);
                }
            }
        }

        log.debug("Failed to match a KeyInfo X509Data against supplied X509Certificate param");
        return false;
    }
}