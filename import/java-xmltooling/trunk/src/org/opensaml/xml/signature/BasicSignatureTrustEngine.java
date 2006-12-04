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

import org.opensaml.xml.security.InlineKeyResolver;
import org.opensaml.xml.security.KeyInfoSource;
import org.opensaml.xml.security.KeyResolver;
import org.opensaml.xml.security.WrapperKeyInfoSource;

/**
 * A signature validation and trust engine that checks the signature against 
 * keys embedded within KeyInfo elements.
 */
public class BasicSignatureTrustEngine implements SignatureTrustEngine {
    
    /** KeyInfoSource to use if no other one is given */
    private KeyInfoSource defaultKeyInfoSource;
    
    /** KeyResolver to use if none is given */
    private KeyResolver defaultKeyResolver;
    
    /** Constructor */
    public BasicSignatureTrustEngine(){
        defaultKeyResolver = new InlineKeyResolver();
    }

    /** {@inheritDoc} */
    public KeyInfoSource getDefaultKeyInfoSource() {
        return defaultKeyInfoSource;
    }

    /** {@inheritDoc} */
    public KeyResolver getDefaultKeyResolver() {
       return defaultKeyResolver;
    }

    /** {@inheritDoc} */
    public void setDefaultKeyResolver(KeyResolver keyResolver) {
        defaultKeyResolver = keyResolver;
    }

    /** {@inheritDoc} */
    public void setDefaultkeyInfoSource(KeyInfoSource keyInfo) {
        defaultKeyInfoSource = keyInfo;
    }

    /** {@inheritDoc} */
    public boolean validate(Signature token) {
        return validate(token, defaultKeyInfoSource, defaultKeyResolver);
    }

    /** {@inheritDoc} */
    public boolean validate(Signature token, KeyInfoSource keyInfo, KeyResolver keyResolver) {
        // TODO Auto-generated method stub
        return false;
    }

    /** {@inheritDoc} */
    public boolean validate(byte[] signature, byte[] content, String sigAlg, KeyInfoSource keyInfo,
            KeyResolver keyResolver) {
        // TODO Auto-generated method stub
        return false;
    }
}