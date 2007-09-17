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

import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;

import org.opensaml.xml.XMLObjectBaseTestCase;
import org.opensaml.xml.mock.SimpleXMLObject;
import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.keyinfo.provider.DSAKeyValueProvider;
import org.opensaml.xml.security.keyinfo.provider.DocumentFragmentRetrievalMethodProvider;
import org.opensaml.xml.security.keyinfo.provider.RSAKeyValueProvider;
import org.opensaml.xml.security.keyinfo.provider.InlineX509DataProvider;
import org.opensaml.xml.security.x509.X509Credential;
import org.opensaml.xml.signature.KeyInfo;

/**
 * Test same document fragment reference RetrievalMethod KeyInfo provider.
 */
public class DocumentFragmentRetrievalMethodProviderTest extends XMLObjectBaseTestCase {
    
    private KeyInfoCredentialResolver resolver;

    protected void setUp() throws Exception {
        super.setUp();
        
        ArrayList<KeyInfoProvider> providers = new ArrayList<KeyInfoProvider>();
        providers.add( new DSAKeyValueProvider() );
        providers.add( new RSAKeyValueProvider() );
        providers.add( new InlineX509DataProvider() );
        providers.add( new DocumentFragmentRetrievalMethodProvider() );
        
        resolver = new KeyInfoCredentialResolver(providers);
    }
    
    public void testRSAReference() throws SecurityException {
        SimpleXMLObject sxo = 
            (SimpleXMLObject) unmarshallElement("/data/org/opensaml/xml/security/keyinfo/FragmentRetrievalMethodRSAKeyValue.xml");
        KeyInfo rmKeyInfo = (KeyInfo) sxo.getUnknownXMLObjects().get(0);
        assertNotNull(rmKeyInfo);
        
        CriteriaSet criteriaSet = new CriteriaSet( new KeyInfoCriteria(rmKeyInfo) );
        Credential cred = resolver.resolveSingle(criteriaSet);
        assertNotNull("Resolved credential was null", cred);
        assertTrue("Expectecd credential with RSA public key", cred.getPublicKey() instanceof RSAPublicKey);
    }
    
    public void testDSAReference() throws SecurityException {
        SimpleXMLObject sxo = 
            (SimpleXMLObject) unmarshallElement("/data/org/opensaml/xml/security/keyinfo/FragmentRetrievalMethodDSAKeyValue.xml");
        KeyInfo rmKeyInfo = (KeyInfo) sxo.getUnknownXMLObjects().get(0);
        assertNotNull(rmKeyInfo);
        
        CriteriaSet criteriaSet = new CriteriaSet( new KeyInfoCriteria(rmKeyInfo) );
        Credential cred = resolver.resolveSingle(criteriaSet);
        assertNotNull("Resolved credential was null", cred);
        assertTrue("Expectecd credential with DSA public key", cred.getPublicKey() instanceof DSAPublicKey);
    }
    
    public void testX509DataReference() throws SecurityException {
        SimpleXMLObject sxo = 
            (SimpleXMLObject) unmarshallElement("/data/org/opensaml/xml/security/keyinfo/FragmentRetrievalMethodX509Data.xml");
        KeyInfo rmKeyInfo = (KeyInfo) sxo.getUnknownXMLObjects().get(0);
        assertNotNull(rmKeyInfo);
        
        CriteriaSet criteriaSet = new CriteriaSet( new KeyInfoCriteria(rmKeyInfo) );
        Credential cred = resolver.resolveSingle(criteriaSet);
        assertNotNull("Resolved credential was null", cred);
        assertTrue("Expectecd X509Credential", cred instanceof X509Credential);
    }
    
    public void testX509DataReferenceBadType() throws SecurityException {
        SimpleXMLObject sxo = 
            (SimpleXMLObject) unmarshallElement("/data/org/opensaml/xml/security/keyinfo/FragmentRetrievalMethodX509DataBadType.xml");
        KeyInfo rmKeyInfo = (KeyInfo) sxo.getUnknownXMLObjects().get(0);
        assertNotNull(rmKeyInfo);
        
        CriteriaSet criteriaSet = new CriteriaSet( new KeyInfoCriteria(rmKeyInfo) );
        Credential cred = resolver.resolveSingle(criteriaSet);
        assertNull("Resolved credential was NOT null", cred);
    }
}
