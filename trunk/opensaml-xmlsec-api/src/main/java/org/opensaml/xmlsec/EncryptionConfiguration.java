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

import javax.annotation.Nullable;

import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;


/**
 * The configuration information to use when generating encrypted XML.
 */
public interface EncryptionConfiguration {
    
    /**
     * Get the encryption credential to use when encrypting the EncryptedData.
     * 
     * @return the encryption credential
     */
    @Nullable public Credential getDataEncryptionCredential();
    
    /**
     * Get the encryption credential to use when encrypting the EncryptedKey.
     * 
     * @return the encryption credential
     */
    @Nullable public Credential getKeyTransportEncryptionCredential();
    
    /**
     * Get the encryption algorithm URI to use when encrypting the EncryptedData.
     * 
     * @return an encryption algorithm URI, or null if no mapping is available
     */
    @Nullable public String getDataEncryptionAlgorithmURI();
    
    /**
     * Get the encryption algorithm URI to use when encrypting the EncryptedKey.
     * 
     * @return an encryption algorithm URI, or null if no mapping is available
     */
    @Nullable public String getKeyTransportEncryptionAlgorithmURI();

    /**
     * Get the KeyInfoGenerator to use when generating the EncryptedData/KeyInfo.
     * 
     * @return the KeyInfoGenerator instance
     */
    @Nullable public KeyInfoGenerator getDataKeyInfoGenerator();
    
    /**
     * Get the KeyInfoGenerator to use when generating the EncryptedKey/KeyInfo.
     * 
     * @return the KeyInfoGenerator instance
     */
    @Nullable public KeyInfoGenerator getKeyTransportKeyInfoGenerator();
    
}