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

import java.security.interfaces.DSAParams;

import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;


/**
 * The configuration information to use when generating an XML signature.
 */
public interface SignatureSigningConfiguration {
    
    /**
     * Get the signing credential to use when signing.
     * 
     * @return the signing credential
     */
    Credential getSigningCredential();
    
    /**
     * Get the signature algorithm URI to use when signing.
     * 
     * @return a signature algorithm URI mapping, or null if no mapping is available
     */
    public String getSignatureAlgorithmURI();
    
    /**
     * Get a digest method algorithm URI suitable for use as a Signature Reference DigestMethod value.
     * 
     * @return a digest method algorithm URI
     */
    public String getSignatureReferenceDigestMethod();
    
    /**
     * Get a canonicalization algorithm URI suitable for use as a Signature CanonicalizationMethod value.
     * 
     * @return a canonicalization algorithm URI
     */
    public String getSignatureCanonicalizationAlgorithm();
    
    /**
     * Get the value to be used as the Signature SignatureMethod HMACOutputLength value, used
     * only when signing with an HMAC algorithm.  This value is optional when using HMAC.
     * 
     * @return the configured HMAC output length value
     */
    public Integer getSignatureHMACOutputLength();
    
    /**
     * Get a DSA parameters instance which defines the default DSA key information to be used 
     * within a DSA "key family".
     * 
     * @param keyLength length of the DSA key whose parameters are desired
     * @return the default DSA parameters instance, or null if no default is available
     */
    public DSAParams getDSAParams(int keyLength);
    
    /**
     * Get the KeyInfoGenerator to use when generating the Signature/KeyInfo.
     * 
     * @return the KeyInfoGenerator instance
     */
    public KeyInfoGenerator getKeyInfoGenerator();

}
