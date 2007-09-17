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

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xml.security.keyinfo.KeyInfoCriteria;
import org.opensaml.xml.security.keyinfo.KeyInfoProvider;
import org.opensaml.xml.security.keyinfo.KeyInfoCredentialResolver.KeyInfoResolutionContext;
import org.opensaml.xml.security.x509.X509Credential;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.signature.RetrievalMethod;
import org.opensaml.xml.signature.SignatureConstants;
import org.opensaml.xml.signature.Transform;
import org.opensaml.xml.util.DatatypeHelper;

/**
 * Implementation of {@link KeyInfoProvider} which supports {@link RetrievalMethod} elements
 * whose URI is a local document fragment which resolves to a {@link KeyInfo} element.
 * 
 * <p>Processing of {@link Transform} children is not supported.</p>
 * 
 * <p>The RetrievalMethod's <code>Type</code> attribute must either be absent or must have one of the
 * following values:
 * <ul>
 * <li><code>http://www.w3.org/2000/09/xmldsig#DSAKeyValue</code></li>
 * <li><code>http://www.w3.org/2000/09/xmldsig#RSAKeyValue</code></li>
 * <li><code>http://www.w3.org/2000/09/xmldsig#X509Data</code></li>
 * </ul>
 * If type is absent, any credentials resolved from the referenced KeyInfo will be returned.
 * If type is present, only credentials consistent with that type will be returned,
 * that is they will be a non-{@link X509Credential} containing either a DSA key or an RSA key,
 * or will be an instance of {@link X509Credential}, respectively.
 * </p>
 */
public class DocumentFragmentRetrievalMethodProvider extends AbstractKeyInfoProvider {
    
    /** Class logger. */
    private static Logger log = Logger.getLogger(DocumentFragmentRetrievalMethodProvider.class);

    /** {@inheritDoc} */
    public boolean handles(XMLObject keyInfoChild) {
        if (! (keyInfoChild instanceof RetrievalMethod)) {
            return false;
        }
        RetrievalMethod rm = (RetrievalMethod) keyInfoChild;
        
        if (! DatatypeHelper.isEmpty(rm.getType())  
                && ! hasSupportedType(DatatypeHelper.safeTrimOrNullString(rm.getType()))) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("RetrievalMethod type '%s' is not supported by this provider",
                        rm.getType()));
            }
            return false;
        }
        
        if (rm.getTransforms() != null && ! rm.getTransforms().getTransforms().isEmpty()) {
            log.debug("RetrievalMethod Transforms are not supported by this provider");
            return false;
        }
        
        return true;
    }

    /** {@inheritDoc} */
    public Collection<Credential> process(KeyInfoCredentialResolver resolver, XMLObject keyInfoChild,
            CriteriaSet criteriaSet, KeyInfoResolutionContext kiContext) throws SecurityException {
        
        if (! handles(keyInfoChild)) {
            return null;
        }
        
        RetrievalMethod rm = (RetrievalMethod) keyInfoChild;
        
        KeyInfo keyInfo = dereferenceURI(rm);
        if (keyInfo == null) {
            return null;
        }
        
        CriteriaSet targetCriteriaSet = new CriteriaSet( new KeyInfoCriteria(keyInfo) );
        ArrayList<Credential> resolvedCreds = new ArrayList<Credential>();
        for (Credential cred : resolver.resolve(targetCriteriaSet)) {
            if (checkType(cred, DatatypeHelper.safeTrimOrNullString(rm.getType()))) {
                resolvedCreds.add(cred);
            }
            
        }
        return resolvedCreds;
    }
    
    /**
     * Check that the resolved credential is consistent with the RetrievalMethod Type attribute.
     * 
     * @param cred the credential to evaluate
     * @param type the Type attribute value
     * @return true if credential is consistent with the type indicated, false otherwise
     */
    protected boolean checkType(Credential cred, String type) {
        if (type == null) {
            return true;
        }
        PublicKey pk = cred.getPublicKey();
        // All types supported imply a public key 
        if (pk == null) {
            return false;
        }
        String keyType = pk.getAlgorithm();
        boolean isX509 = cred instanceof X509Credential;
        
        if (    (! isX509 && "RSA".equals(keyType) && SignatureConstants.TYPE_KEYINFO_RSA_KEYVALUE.equals(type)) 
             || (! isX509 && "DSA".equals(keyType) && SignatureConstants.TYPE_KEYINFO_DSA_KEYVALUE.equals(type))
             || (isX509 && SignatureConstants.TYPE_KEYINFO_X509DATA.equals(type))
            ) {
            return true;
        }
            
        return false;
    }

    /**
     * Check for supported Type attribute value.
     * 
     * @param type the type to check
     * @return true if the RetrievalMethod type is supported by this provider, false otherwise
     */
    protected boolean hasSupportedType(String type) {
        return SignatureConstants.TYPE_KEYINFO_DSA_KEYVALUE.equals(type)
            || SignatureConstants.TYPE_KEYINFO_RSA_KEYVALUE.equals(type)
            || SignatureConstants.TYPE_KEYINFO_X509DATA.equals(type);
    }
    
    /**
     * Dereference the URI attribute of the specified retrieval method into a KeyInfo.
     * 
     * @param rm the RetrievalMethod to process
     * @return the dereferenced KeyInfo
     */
    protected KeyInfo dereferenceURI(RetrievalMethod rm) {
        String uri = rm.getURI();
        if (! isSameDocumentReference(uri) ) {
            log.info("RetrievalMethod did not contain a same-document URI reference, can not process");
            return null;
        }
        XMLObject target = rm.resolveIDFromRoot(uri.substring(1));
        if (target == null) {
           log.error("RetrievalMethod URI could not be dereferenced");
           return null;
        }
        if ( ! (target instanceof KeyInfo) ) {
           log.error("The result of dereferencing the RetrievalMethod was not a KeyInfo");
           return null;
        }
        return (KeyInfo) target;
    }

    /**
     * Check for same document reference.
     * 
     * @param uri the URI to evaluate
     * @return true if URI value is a same-document reference, false otherwise
     */
    protected boolean isSameDocumentReference(String uri) {
        return (! DatatypeHelper.isEmpty(uri)) && uri.startsWith("#");
    }

}
