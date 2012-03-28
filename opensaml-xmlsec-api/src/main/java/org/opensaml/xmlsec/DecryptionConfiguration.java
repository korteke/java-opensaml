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

package org.opensaml.xmlsec;

import java.util.List;

import org.opensaml.xmlsec.encryption.support.EncryptedKeyResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;

/**
 * The configuration information to use when decrypting encrypted XML.
 */
public interface DecryptionConfiguration {
    
    /**
     * Get the list of whitelisted algorithm URI's.
     * 
     * @return the list of algorithms
     */
    List<String> getWhitelistedAlgorithmURIs();
    
    /**
     * Get the list of blacklisted algorithm URI's.
     * 
     * @return the list of algorithms
     */
    List<String> getBlacklistedAlgorithmsURIs();
    
    /**
     * The KeyInfoCredentialResolver to use when processing the EncryptedData/KeyInfo.
     * 
     * @return the KeyInfoCredentialResolver instance
     */
    KeyInfoCredentialResolver getDataKeyInfoCredentialResolver();
    
    /**
     * The KeyInfoCredentialResolver to use when processing the EncryptedKey/KeyInfo (the
     * Key Encryption Key or KEK).
     * 
     * @return the KeyInfoCredentialResolver instance
     */
    KeyInfoCredentialResolver getKEKKeyInfoCredentialResolver();
    
    /**
     * Get the EncryptedKeyResolver to use when resolving the EncryptedKey(s) to process.
     * 
     * @return the EncryptedKeyResolver instance
     */
    EncryptedKeyResolver geEncryptedKeyResolver();
    
}
