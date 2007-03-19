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

package org.opensaml.xml.security.keyinfo;

import org.opensaml.xml.XMLObject;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.keyinfo.KeyInfoCredentialResolver.KeyInfoResolutionContext;

/**
 * Interface for providers which support extracting a {@link Credential} from 
 * a child element of {@link KeyInfo}.
 */
public interface KeyInfoProvider {
    
    /**
     * Process a specified KeyInfo child (XMLobject) and attempt to resolve a credential from it.
     * 
     * @param resolver reference to the resolver which is calling the provider
     * @param keyInfoChild the KeyInfo child being processed
     * @param criteria the credential criteria the credential must satisfy
     * @param kiContext the resolution context, used for sharing state between the resolver and the providers
     * 
     * @return a resolved Credential, or null
     * 
     * @throws SecurityException if there is an error during credential resolution.  
     *          Note: failure to resolve a credential is not an error.
     */
    public Credential process(KeyInfoCredentialResolver resolver, XMLObject keyInfoChild, 
            KeyInfoCredentialCriteria criteria, KeyInfoResolutionContext kiContext) throws SecurityException;
    
    /**
     * Evaluate whether the given provider can attempt to resolve a credential from the specified KeyInfo child.
     * 
     * @param keyInfoChild the KeyInfo child object to consider
     * 
     * @return true if the provider an attempt to resolve, false otherwise
     */
    public boolean handles(XMLObject keyInfoChild);

}
