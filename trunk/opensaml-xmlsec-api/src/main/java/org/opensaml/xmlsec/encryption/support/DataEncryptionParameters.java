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

package org.opensaml.xmlsec.encryption.support;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.EncryptionParameters;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;

/**
 * Parameters for encrypting XMLObjects.
 */
public class DataEncryptionParameters {

    /** Credential used to encrypt. */
    private Credential encryptionCredential;

    /** XML Encryption algorithm URI used to encrypt. */
    private String algorithm;

    /** Generator for dynamically generating a KeyInfo instance containing information
     * from the encryption credential. */
    private KeyInfoGenerator keyInfoGenerator;

    /**
     * Constructor.
     */
    public DataEncryptionParameters() {
        // This will be the default for auto encryption key generation
        setAlgorithm(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256);
    }

    /**
     * Convenience constructor which allows copying the relevant data encryption parameters from
     * an instance of {@link EncryptionParameters}.
     * 
     * @param params the encryption parameters instance
     */
    public DataEncryptionParameters(@Nonnull final EncryptionParameters params) {
        this();
        Constraint.isNotNull(params, "EncryptionParameters instance was null");
        setEncryptionCredential(params.getDataEncryptionCredential());
        setAlgorithm(params.getDataEncryptionAlgorithmURI());
        setKeyInfoGenerator(params.getDataKeyInfoGenerator());
    }

    /**
     * Gets the XML Encryption algorithm URI used to encrypt.
     * 
     * @return the algorithm URI used to encrypt, or null
     */
    @Nullable public String getAlgorithm() {
        return algorithm;
    }

    /**
     * Sets the XML Encryption algorithm URI used to encrypt.
     * 
     * @param newAlgorithm the algorithm URI used to encrypt
     */
    public void setAlgorithm(@Nullable final String newAlgorithm) {
        algorithm = newAlgorithm;
    }

    /**
     * Gets the credential used to encrypt.
     * 
     * @return the credential used to encrypt, or null
     */
    @Nullable public Credential getEncryptionCredential() {
        return encryptionCredential;
    }

    /**
     * Sets the credential used to encrypt.
     * 
     * @param newEncryptionCredential the credential used to encrypt
     */
    public void setEncryptionCredential(@Nullable final Credential newEncryptionCredential) {
        encryptionCredential = newEncryptionCredential;
    }

    /**
     * Gets the instance which will be used to generate a KeyInfo
     * object from the encryption credential.
     * 
     * @return the generator instance, or null
     */
    @Nullable public KeyInfoGenerator getKeyInfoGenerator() {
        return keyInfoGenerator;
    }

    /**
     * Sets the instance which will be used to generate a KeyInfo
     * object from the encryption credential.
     * 
     * @param newKeyInfoGenerator the new generator instance
     */
    public void setKeyInfoGenerator(@Nullable final KeyInfoGenerator newKeyInfoGenerator) {
        keyInfoGenerator = newKeyInfoGenerator;
    }
}