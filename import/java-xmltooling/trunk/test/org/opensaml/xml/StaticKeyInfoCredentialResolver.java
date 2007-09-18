/*
 * Copyright [2007] [University Corporation for Advanced Internet Development, Inc.]
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

package org.opensaml.xml;

import java.util.ArrayList;
import java.util.List;

import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.AbstractCredentialResolver;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.keyinfo.KeyInfoCredentialResolver;

/**
 * Simple implementation of {@link KeyInfoCredentialResolver} which just stores and returns
 * a static set of credentials.
 */
public class StaticKeyInfoCredentialResolver extends AbstractCredentialResolver
        implements KeyInfoCredentialResolver {
    
    /** List of credentials held by this resolver. */
    private List<Credential> creds;
    
    /**
     * Constructor.
     *
     * @param credentials collection of credentials to be held by this resolver
     */
    public StaticKeyInfoCredentialResolver(List<Credential> credentials) {
        creds = new ArrayList<Credential>();
        creds.addAll(credentials);
    }
    
    /**
     * Constructor.
     *
     * @param credential a single credential to be held by this resolver
     */
    public StaticKeyInfoCredentialResolver(Credential credential) {
        creds = new ArrayList<Credential>();
        creds.add(credential);
    }

    /** {@inheritDoc} */
    public Iterable<Credential> resolve(CriteriaSet criteria) throws SecurityException {
        return creds;
    }

}
