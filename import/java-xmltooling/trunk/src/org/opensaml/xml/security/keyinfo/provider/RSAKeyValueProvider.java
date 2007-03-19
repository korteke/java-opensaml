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

package org.opensaml.xml.security.keyinfo.provider;

import java.security.KeyException;
import java.security.PublicKey;

import org.opensaml.xml.XMLObject;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.BasicCredential;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.keyinfo.KeyInfoCredentialCriteria;
import org.opensaml.xml.security.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xml.security.keyinfo.KeyInfoCredentialResolver.KeyInfoResolutionContext;
import org.opensaml.xml.signature.KeyInfoHelper;
import org.opensaml.xml.signature.KeyValue;
import org.opensaml.xml.signature.RSAKeyValue;

/**
 * Implementation of {@link KeyInfoProvider} which supports {@link RSAKeyValue}.
 */
public class RSAKeyValueProvider extends AbstractKeyInfoProvider {

    /** {@inheritDoc} */
    public boolean handles(XMLObject keyInfoChild) {
        return getRSAKeyValue(keyInfoChild) != null;
    }

    /** {@inheritDoc} */
    public Credential process(KeyInfoCredentialResolver resolver, XMLObject keyInfoChild, 
            KeyInfoCredentialCriteria criteria, KeyInfoResolutionContext kiContext) throws SecurityException {
        
        if (criteria.getKeyAlgorithm() != null && ! criteria.getKeyAlgorithm().equals("RSA")) {
            return null;
        }
        
        RSAKeyValue keyValue = getRSAKeyValue(keyInfoChild);
        if (keyValue == null) {
            return null;
        }
        
        PublicKey pubKey = null;
        try {
            pubKey = KeyInfoHelper.getRSAKey(keyValue);
        } catch (KeyException e) {
            throw new SecurityException("Error extracting RSA key value", e);
        }
        BasicCredential cred = new BasicCredential();
        cred.setPublicKey(pubKey);
        cred.setKeyNames(kiContext.getKeyNames());
        cred.setCredentialContext(buildContext(criteria.getKeyInfo(), resolver));
        
        return cred;
    }
    
    /**
     * Get the RSAKeyValue from the passed XML object.
     * 
     * @param xmlObject an XML object, presumably either a {@link KeyValue} or an {@link RSAKeyValue}
     * @return the RSAKeyValue which was found, or null if none
     */
    protected RSAKeyValue getRSAKeyValue(XMLObject xmlObject) {
        if (xmlObject == null) {return null; }
        
        if (xmlObject instanceof RSAKeyValue) {
            return (RSAKeyValue) xmlObject;
        }
        
        if (xmlObject instanceof KeyValue) {
            return ((KeyValue) xmlObject).getRSAKeyValue();
        }
        return null;
    }
}
