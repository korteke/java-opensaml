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

import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;

import org.opensaml.xml.XMLObject;

/**
 * XMLObject representing XML Digital Signature, version 20020212, KeyInfo element.
 * 
 * Note that this does not support every possible key information type, only the ones most commonly used.
 */
public interface KeyInfo extends XMLObject {
    
    /** Element local name */
    public static String LOCAL_NAME = "KeyInfo";

    /**
     * Gets the list of key names within the key info.
     * 
     * @return the list of key names within the key info
     */
    public List<String> getKeyNames();
    
    /**
     * Gets the list of public keys within the key info.
     * 
     * @return the list of key names within the key info
     */
    public List<PublicKey> getKeys();
    
    /**
     * Gets the list of X509 certificates within the key info.
     * 
     * @return the list of X509 certificates within the key info
     */
    public List<X509Certificate> getCertificates();
}