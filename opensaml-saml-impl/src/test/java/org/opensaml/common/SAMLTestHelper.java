/*
 * Copyright 2011 University Corporation for Advanced Internet Development, Inc.
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
package org.opensaml.common;

import java.util.ArrayList;
import java.util.List;

import org.opensaml.xml.security.keyinfo.BasicProviderKeyInfoCredentialResolver;
import org.opensaml.xml.security.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xml.security.keyinfo.KeyInfoProvider;
import org.opensaml.xml.security.keyinfo.provider.DSAKeyValueProvider;
import org.opensaml.xml.security.keyinfo.provider.InlineX509DataProvider;
import org.opensaml.xml.security.keyinfo.provider.RSAKeyValueProvider;

/**
 * Helper methods for SAML testing.
 */
public final class SAMLTestHelper {
    
    /** Constructor. */
    private SAMLTestHelper() { }
    
    /**
     * Get a basic KeyInfo credential resolver which can process standard inline
     * data - RSAKeyValue, DSAKeyValue, X509Data.
     * 
     * @return a new KeyInfoCredentialResolver instance
     */
    public static KeyInfoCredentialResolver buildBasicInlineKeyInfoResolver() {
        List<KeyInfoProvider> providers = new ArrayList<KeyInfoProvider>();
        providers.add( new RSAKeyValueProvider() );
        providers.add( new DSAKeyValueProvider() );
        providers.add( new InlineX509DataProvider() );
        return new BasicProviderKeyInfoCredentialResolver(providers);
    }

}
