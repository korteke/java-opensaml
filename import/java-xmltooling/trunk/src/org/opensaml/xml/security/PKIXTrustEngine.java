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

package org.opensaml.xml.security;

/**
 * Trust engine that validates security tokens using PKIX validation.
 * 
 * @param <TokenType> token to validate
 * @param <KeyInfoResolverType> source of key information
 */
public interface PKIXTrustEngine<TokenType, KeyInfoResolverType extends X509KeyInfoResolver> extends
        TrustEngine<TokenType, KeyInfoResolverType> {

    /**
     * Gets the information necessary to perform the PKIX validation.
     * 
     * @return information necessary to perform the PKIX validation
     */
    public PKIXValidationInformation getValidationInformation();

    /**
     * Sets the information necessary to perform the PKIX validation.
     * 
     * @param validationInformation information necessary to perform the PKIX validation
     */
    public void setValidationInformation(PKIXValidationInformation validationInformation);
}