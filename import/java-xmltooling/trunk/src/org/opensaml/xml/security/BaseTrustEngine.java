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
 * Base class for trust engines.
 * 
 * @param <TokenType> the token type this trust engine evaluates
 * @param <KeyInfoResolverType> KeyInfo information resolver type
 */
public abstract class BaseTrustEngine<TokenType, KeyInfoResolverType extends KeyInfoResolver> implements
        TrustEngine<TokenType, KeyInfoResolverType> {

    /** KeyInfoSource to use if no other one is given. */
    private KeyInfoSource defaultKeyInfoSource;

    /** KeyResolver to use if none is given. */
    private KeyInfoResolverType defaultKeyResolver;

    /** {@inheritDoc} */
    public KeyInfoSource getDefaultKeyInfoSource() {
        return defaultKeyInfoSource;
    }

    /** {@inheritDoc} */
    public KeyInfoResolverType getDefaultKeyResolver() {
        return defaultKeyResolver;
    }

    /** {@inheritDoc} */
    public void setDefaultKeyResolver(KeyInfoResolverType keyResolver) {
        defaultKeyResolver = keyResolver;
    }

    /** {@inheritDoc} */
    public void setDefaultkeyInfoSource(KeyInfoSource keyInfo) {
        defaultKeyInfoSource = keyInfo;
    }
    
    /** {@inheritDoc} */
    public boolean validate(TokenType token) throws SecurityException {
        return validate(token, getDefaultKeyInfoSource(), getDefaultKeyResolver());
    }
}