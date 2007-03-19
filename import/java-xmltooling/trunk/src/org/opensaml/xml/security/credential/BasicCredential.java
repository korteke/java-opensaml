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

package org.opensaml.xml.security.credential;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Collection;
import java.util.HashSet;

import javax.crypto.SecretKey;

import org.opensaml.xml.util.DatatypeHelper;

/**
 * A basic implementation of {@link Credential}.
 */
public class BasicCredential extends AbstractCredential {

    /** Constructor. */
    public BasicCredential() {
        keyNames = new HashSet<String>();
        usageType = UsageType.UNSPECIFIED;
    }

    /**
     * Sets the ID of the entity this credential is for.
     * 
     * @param id ID of the entity this credential is for
     */
    public void setEntityId(String id) {
        entityID = DatatypeHelper.safeTrimOrNullString(id);
    }

    /**
     * Sets the usage type for this credential.
     * 
     * @param usage usage type for this credential
     */
    public void setUsageType(UsageType usage) {
        usageType = usage;
    }

    /**
     * Sets the key names for this credential.
     * 
     * @param names key names for this credential
     */
    public void setKeyNames(Collection<String> names) {
        keyNames = names;
    }

    /**
     * Sets the public key for this credential.
     * 
     * @param key public key for this credential
     */
    public void setPublicKey(PublicKey key) {
        publicKey = key;
    }

    /**
     * Sets the secret key for this credential.
     * 
     * @param key secret key for this credential
     */
    public void setSecretKey(SecretKey key) {
        secretKey = key;
    }

    /**
     * Sets the private key for this credential.
     * 
     * @param key private key for this credential
     */
    public void setPrivateKey(PrivateKey key) {
        privateKey = key;
    }
    
    /**
     * Set the credential context information, which provides additional information
     * specific to the context in which the credential was resolved.
     * 
     * @param context resolution context of the credential
     */
    public void setCredentialContext(CredentialContext context) {
        credentialContext = context;
    }
}