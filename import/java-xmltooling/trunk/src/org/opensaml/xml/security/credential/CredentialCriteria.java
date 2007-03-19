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

package org.opensaml.xml.security.credential;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Class for specifying criteria by which a {@link CredentialResolver} should resolve credentials.
 */
public class CredentialCriteria {
    
    // TODO need to think about this a little more
    // - maybe all criteria should just be stored in the extensible map
    // - provide key constants with well-defined semantics
    
    /** EntityID for which to resolve credential. */
    private String entityID;
    
    /** Key usage type of resolved credentials. */
    private UsageType usage;
    
    /** Key algorithm type of resolved credentials. */
    private String keyAlgorithm;
    
    /** Key name of resolved credentials.  */
    private String keyName;
    
    /** Specifier of public key associated with resolved credentials. */
    private PublicKey publicKey;
    
    /** Extensible set of criteria which can be used by credential-type specific resolvers. */
    private Map<String, Object> extensionProperties;

    /** Constructor. */
    public CredentialCriteria() {
        extensionProperties = new HashMap<String, Object>();
    }
    
    /**
     * Get the extension criteria specified.
     * 
     * @param key name of the extension criteria to return
     * 
     * @return extension criteria value
     */
    public Object getExtension(String key) {
       return extensionProperties.get(key); 
    }
    
    /**
     * Set the extension criteria specified.
     * 
     * @param key name of the extension criteria 
     * @param value value of the extension criteria 
     * 
     * @return old value of the extension criteria key
     */
    public Object setExtension(String key, Object value) {
       return extensionProperties.put(key, value); 
    }
    
    /**
     * Get the key names of all extension criteria.
     * 
     * @return set of extension criteria names
     */
    public Set<String> getExtensionNames() {
       return extensionProperties.keySet(); 
    }

    /**
     * Get the entity ID criteria.
     * 
     * @return Returns the entityID.
     */
    public String getEntityID() {
        return entityID;
    }

    /**
     * Set the entity ID criteria.
     * 
     * @param entityID The entityID to set.
     */
    public void setEntityID(String entityID) {
        this.entityID = entityID;
    }

    /**
     * Get the key algorithm criteria.
     * 
     * @return returns the keyAlgorithm.
     */
    public String getKeyAlgorithm() {
        return keyAlgorithm;
    }

    /**
     * Set the key algorithm criteria.
     * 
     * @param keyAlgorithm The keyAlgorithm to set.
     */
    public void setKeyAlgorithm(String keyAlgorithm) {
        this.keyAlgorithm = keyAlgorithm;
    }

    /**
     * Get the key name criteria.
     * 
     * @return Returns the keyName.
     */
    public String getKeyName() {
        return keyName;
    }

    /**
     * Set the key name criteria.
     * 
     * @param keyName The keyName to set.
     */
    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    /**
     * Get the public key criteria.
     * 
     * @return Returns the publicKey.
     */
    public PublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * Set the public key criteria. 
     * 
     * @param publicKey The publicKey to set.
     */
    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    /**
     * Get the key usage criteria.
     * 
     * @return Returns the usage.
     */
    public UsageType getUsage() {
        return usage;
    }

    /**
     * Set the key usage criteria.
     * 
     * @param usage The usage to set.
     */
    public void setUsage(UsageType usage) {
        this.usage = usage;
    }

}
