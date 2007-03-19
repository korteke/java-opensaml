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

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opensaml.xml.XMLObject;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.AbstractCredentialResolver;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.credential.CredentialCriteria;
import org.opensaml.xml.security.credential.CredentialResolver;
import org.opensaml.xml.security.keyinfo.provider.DSAKeyValueProvider;
import org.opensaml.xml.security.keyinfo.provider.RSAKeyValueProvider;
import org.opensaml.xml.security.keyinfo.provider.X509DataProvider;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.signature.KeyInfoHelper;
import org.opensaml.xml.signature.KeyName;
import org.opensaml.xml.signature.KeyValue;

/**
 * Specialized credential resovlver interface which resolves credentials based on a {@link KeyInfo}  element.
 */
public class KeyInfoCredentialResolver extends AbstractCredentialResolver<KeyInfoCredentialContext>
    implements CredentialResolver<KeyInfoCredentialContext> {
    
    /**
     *
     */

    /** The default credential context class to use in resolved credentials.  */
    public static final Class<KeyInfoCredentialContext> DEFAULT_CONTEXT_CLASS = KeyInfoCredentialContext.class;
    
    /** List of KeyInfo providers that are registered on this instance.  */
    private List<KeyInfoProvider> providers;
    
    /** Constructor. */
    public KeyInfoCredentialResolver() {
        setContextClass(DEFAULT_CONTEXT_CLASS);
        providers = new ArrayList<KeyInfoProvider>();
        
        //TODO decide how/where providers will be registered
        // - programatically and/or declaratively
        // - global vs. instance local distinction
        
        //TODO TEMP just to do some testing 
        providers.add(new RSAKeyValueProvider());
        providers.add(new DSAKeyValueProvider());
        providers.add(new X509DataProvider());
    }

    /** {@inheritDoc} */
    public Iterable<Credential> resolveCredentials(CredentialCriteria criteria) throws SecurityException {
        if (! (criteria instanceof KeyInfoCredentialCriteria)) {
            throw new SecurityException("Credential criteria was not an instance of KeyInfoCredentialCriteria");
        }
        KeyInfoCredentialCriteria kiCriteria = (KeyInfoCredentialCriteria) criteria;
        KeyInfo keyInfo = kiCriteria.getKeyInfo();
        
        // This will be the list of credentials to return.
        List<Credential> credentials = new ArrayList<Credential>();
        
        // First extract all KeyNames, and the Credential based on the (singular) key 
        // from an existing KeyValue(s).  This credential will only be returned in the 
        // result set if no creds of a more specific type are found.
        KeyInfoResolutionContext kiContext = new KeyInfoResolutionContext();
        kiContext.setKeyNames(KeyInfoHelper.getKeyNames(keyInfo));
        resolveKeyValue(keyInfo, kiCriteria, kiContext);
        
        Key keyValueKey = extractKeyValue(kiContext.getKeyValueCredential());
        if (! evaluateKey(keyValueKey, kiCriteria)) {
            return credentials;
        }
        
        // Now process all other non-KeyName  and non-KeyValue children
        for (XMLObject keyInfoChild : keyInfo.getXMLObjects()) {
            if (keyInfoChild instanceof KeyName || keyInfoChild instanceof KeyValue) {
                continue;
            }
            for (KeyInfoProvider provider : providers) {
                Credential cred = provider.process(this, keyInfoChild, kiCriteria, kiContext);
                if (cred != null) {
                    credentials.add(cred);
                    continue;
                }
            }
        }
        
        //TODO criteria eval: key equality, key name, usage, etc
        
        // If no specific type of credential is found, just return the single Credential,
        // probably a BasicCredential, which was resolved based on KeyValue evaluation.
        if (credentials.isEmpty()) {
            if (kiContext.getKeyValueCredential() != null) {
                credentials.add(kiContext.getKeyValueCredential());
            }
        }
        
        return credentials;
    }

    /**
     * Resolve the key from any raw KeyValue element that may be present.
     * 
     * Note: this assumes that KeyInfo/KeyValue will not be abused via-a-vis the Signature specificiation,
     * and that therefore all KeyValue elements (if there is even more than one) will all resolve to the same
     * key value. Therefore, only the first KeyValue found is processed.
     * 
     * @param keyInfo the KeyInfo to evaluate
     * @param kiCriteria the credential criteria used to resolve credentials
     * @param kiContext KeyInfo resolution context containing 
     * 
     * @throws SecurityException  thrown if there is an error resolving the key from the KeyValue
     */
    protected void resolveKeyValue(KeyInfo keyInfo, KeyInfoCredentialCriteria kiCriteria, 
            KeyInfoResolutionContext kiContext)  throws SecurityException {
        
        Credential keyValueCred = null;
        boolean foundKeyValueCred = false;
        for (KeyValue keyValue : keyInfo.getKeyValues()) {
            for (KeyInfoProvider provider : providers) {
                if (! provider.handles(keyValue)) {
                    continue;
                }
                keyValueCred = provider.process(this, keyValue, kiCriteria, kiContext);
                if (keyValueCred != null) {
                    foundKeyValueCred = true;
                    break;
                }
            }
            if (foundKeyValueCred) {
                kiContext.setKeyValueCredential(keyValueCred);
                return;
            }
        }
    }
    
    /**
     * Build a new credential context based on the KeyInfo being processed.
     * 
     * @param keyInfo the KeyInfo element being processed
     * @return a new KeyInfoCredentialContext 
     * @throws SecurityException if the new credential context could not be built
     */
    protected KeyInfoCredentialContext buildContext(KeyInfo keyInfo) throws SecurityException {
       KeyInfoCredentialContext context = (KeyInfoCredentialContext) newCredentialContext(); 
       context.setKeyInfo(keyInfo);
       return context;
    }
    
    /**
     * Utility method to extract any key that might be present in the specified Credential.
     * 
     * @param cred the Credential to evaluate
     * @return the Key contained in the credential, or null if it does not contain a key.
     */
    protected Key extractKeyValue(Credential cred) {
        if (cred == null) {
            return null;
        }
        if (cred.getPublicKey() != null) {
            return cred.getPublicKey();
        } 
        // This could happen if key is derived, e.g. key agreement, etc
        if (cred.getSecretKey() != null) {
            return cred.getSecretKey();
        }
        // Perhaps unlikely, but go ahead and check
        if (cred.getPrivateKey() != null) {
            return cred.getPrivateKey(); 
        }
        return null;
    }
    
    /**
     *  Resolution context class that can be used to supply information to the providers within
     *  a given invocation of the resolver.
     */
    public class KeyInfoResolutionContext {
        
        /** List of key names. */
        private List<String> names;
        
        /** The credential resolved based on a KeValue, if any.  */
        private Credential credential;
        
        /** Extensible map of properties. */
        private final Map<String, Object> properties;
        
        /**
         * Constructor.
         */
        public KeyInfoResolutionContext() {
            properties = new HashMap<String, Object>();
        }

        /**
         * Get the names obtained from any KeyNames.
         * 
         * @return Returns the keyNames.
         */
        public List<String> getKeyNames() {
            return names;
        }

        /**
         * Set the names from any KeyNames.
         * 
         * @param keyNames The KeyNames to set.
         */
        public void setKeyNames(List<String> keyNames) {
            this.names = keyNames;
        }

        /**
         * Get the credential holding the key obtained from a KeyValue, if any.
         * 
         * @return Returns the keyValueCredential.
         */
        public Credential getKeyValueCredential() {
            return credential;
        }
        
        /**
         * Set the credential holding the key obtained from a KeyValue, if any.
         * 
         * @param keyValueCredential The credential to set.
         */
        public void setKeyValueCredential(Credential keyValueCredential) {
            this.credential = keyValueCredential;
        }

        /**
         * Get the extensible properties map.
         * 
         * @return Returns the properties.
         */
        public Map<String, Object> getProperties() {
            return properties;
        }

    }

}
