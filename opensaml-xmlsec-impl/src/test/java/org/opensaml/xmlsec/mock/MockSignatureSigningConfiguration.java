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

package org.opensaml.xmlsec.mock;

import java.security.interfaces.DSAParams;
import java.util.Map;

import javax.annotation.Nullable;

import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.SignatureSigningConfiguration;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;

/**
 *
 */
public class MockSignatureSigningConfiguration implements SignatureSigningConfiguration {
    
    private Credential signingCredential;
    private String signatureAlgorithmURI;
    private String signatureReferenceDigestMethod;
    private String signatureCanonicalizationAlgorithm;
    private Integer signatureHMACOutputLength;
    private Map<Integer, DSAParams> dsaParamsMap;
    private KeyInfoGenerator keyInfoGenerator;

    /** {@inheritDoc} */
    @Nullable public Credential getSigningCredential() {
        return signingCredential;
    }

    /** {@inheritDoc} */
    @Nullable public String getSignatureAlgorithmURI() {
        return signatureAlgorithmURI;
    }

    /** {@inheritDoc} */
    @Nullable public String getSignatureReferenceDigestMethod() {
        return signatureReferenceDigestMethod;
    }

    /** {@inheritDoc} */
    @Nullable public String getSignatureCanonicalizationAlgorithm() {
        return signatureCanonicalizationAlgorithm;
    }

    /** {@inheritDoc} */
    @Nullable public Integer getSignatureHMACOutputLength() {
        return signatureHMACOutputLength;
    }

    /** {@inheritDoc} */
    @Nullable public DSAParams getDSAParams(int keyLength) {
        return dsaParamsMap.get(keyLength);
    }

    /** {@inheritDoc} */
    @Nullable public KeyInfoGenerator getKeyInfoGenerator() {
        return keyInfoGenerator;
    }

    /**
     * @param signingCredential The signingCredential to set.
     */
    public void setSigningCredential(Credential signingCredential) {
        this.signingCredential = signingCredential;
    }

    /**
     * @param signatureAlgorithmURI The signatureAlgorithmURI to set.
     */
    public void setSignatureAlgorithmURI(String signatureAlgorithmURI) {
        this.signatureAlgorithmURI = signatureAlgorithmURI;
    }

    /**
     * @param signatureReferenceDigestMethod The signatureReferenceDigestMethod to set.
     */
    public void setSignatureReferenceDigestMethod(String signatureReferenceDigestMethod) {
        this.signatureReferenceDigestMethod = signatureReferenceDigestMethod;
    }

    /**
     * @param signatureCanonicalizationAlgorithm The signatureCanonicalizationAlgorithm to set.
     */
    public void setSignatureCanonicalizationAlgorithm(String signatureCanonicalizationAlgorithm) {
        this.signatureCanonicalizationAlgorithm = signatureCanonicalizationAlgorithm;
    }

    /**
     * @param signatureHMACOutputLength The signatureHMACOutputLength to set.
     */
    public void setSignatureHMACOutputLength(Integer signatureHMACOutputLength) {
        this.signatureHMACOutputLength = signatureHMACOutputLength;
    }

    /**
     * @param dsaParamsMap The dsaParamsMap to set.
     */
    public void setDsaParamsMap(Map<Integer, DSAParams> dsaParamsMap) {
        this.dsaParamsMap = dsaParamsMap;
    }

    /**
     * @param keyInfoGenerator The keyInfoGenerator to set.
     */
    public void setKeyInfoGenerator(KeyInfoGenerator keyInfoGenerator) {
        this.keyInfoGenerator = keyInfoGenerator;
    }

}
