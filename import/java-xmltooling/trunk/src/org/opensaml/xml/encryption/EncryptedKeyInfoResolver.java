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

package org.opensaml.xml.encryption;

import java.security.KeyException;
import java.security.PublicKey;

import org.opensaml.xml.security.KeyInfoResolver;

/**
 * Resolves encrypted keys based on EncryptedData information or other external factors.
 */
public interface EncryptedKeyInfoResolver extends KeyInfoResolver<PublicKey> {

    /**
     * Returns an encrypted key based on a particular EncryptedData context.
     * 
     * @param encryptedData the EncryptedData context in which to look for EncryptedKey
     * @return the resolved EncryptedKey element
     * @throws KeyException thrown if there is a problem resolving a key
     */
    public EncryptedKey resolveKey(EncryptedData encryptedData) throws KeyException;

}