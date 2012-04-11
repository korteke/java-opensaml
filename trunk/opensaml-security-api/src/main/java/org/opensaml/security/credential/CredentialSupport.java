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

package org.opensaml.security.credential;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import javax.crypto.SecretKey;

import org.opensaml.security.x509.BasicX509Credential;

/**
 * Helper methods for working with {@link Credential} instances.
 */
public final class CredentialSupport {
    
    
    /** Constructor. */
    private CredentialSupport() { }

    /**
     * Extract the encryption key from the credential.
     * 
     * @param credential the credential containing the encryption key
     * @return the encryption key (either a public key or a secret (symmetric) key
     */
    public static Key extractEncryptionKey(Credential credential) {
        if (credential == null) {
            return null;
        }
        if (credential.getPublicKey() != null) {
            return credential.getPublicKey();
        } else {
            return credential.getSecretKey();
        }
    }

    /**
     * Extract the decryption key from the credential.
     * 
     * @param credential the credential containing the decryption key
     * @return the decryption key (either a private key or a secret (symmetric) key
     */
    public static Key extractDecryptionKey(Credential credential) {
        if (credential == null) {
            return null;
        }
        if (credential.getPrivateKey() != null) {
            return credential.getPrivateKey();
        } else {
            return credential.getSecretKey();
        }
    }

    /**
     * Extract the signing key from the credential.
     * 
     * @param credential the credential containing the signing key
     * @return the signing key (either a private key or a secret (symmetric) key
     */
    public static Key extractSigningKey(Credential credential) {
        if (credential == null) {
            return null;
        }
        if (credential.getPrivateKey() != null) {
            return credential.getPrivateKey();
        } else {
            return credential.getSecretKey();
        }
    }

    /**
     * Extract the verification key from the credential.
     * 
     * @param credential the credential containing the verification key
     * @return the verification key (either a public key or a secret (symmetric) key
     */
    public static Key extractVerificationKey(Credential credential) {
        if (credential == null) {
            return null;
        }
        if (credential.getPublicKey() != null) {
            return credential.getPublicKey();
        } else {
            return credential.getSecretKey();
        }
    }

    /**
     * Get a simple, minimal credential containing a secret (symmetric) key.
     * 
     * @param secretKey the symmetric key to wrap
     * @return a credential containing the secret key specified
     */
    public static BasicCredential getSimpleCredential(SecretKey secretKey) {
        if (secretKey == null) {
            throw new IllegalArgumentException("A secret key is required");
        }
        BasicCredential cred = new BasicCredential();
        cred.setSecretKey(secretKey);
        return cred;
    }

    /**
     * Get a simple, minimal credential containing a public key, and optionally a private key.
     * 
     * @param publicKey the public key to wrap
     * @param privateKey the private key to wrap, which may be null
     * @return a credential containing the key(s) specified
     */
    public static BasicCredential getSimpleCredential(PublicKey publicKey, PrivateKey privateKey) {
        if (publicKey == null) {
            throw new IllegalArgumentException("A public key is required");
        }
        BasicCredential cred = new BasicCredential();
        cred.setPublicKey(publicKey);
        cred.setPrivateKey(privateKey);
        return cred;
    }

    /**
     * Get a simple, minimal credential containing an end-entity X.509 certificate, and optionally a private key.
     * 
     * @param cert the end-entity certificate to wrap
     * @param privateKey the private key to wrap, which may be null
     * @return a credential containing the certificate and key specified
     */
    public static BasicX509Credential getSimpleCredential(X509Certificate cert, PrivateKey privateKey) {
        if (cert == null) {
            throw new IllegalArgumentException("A certificate is required");
        }
        BasicX509Credential cred = new BasicX509Credential();
        cred.setEntityCertificate(cert);
        cred.setPrivateKey(privateKey);
        return cred;
    }

}
