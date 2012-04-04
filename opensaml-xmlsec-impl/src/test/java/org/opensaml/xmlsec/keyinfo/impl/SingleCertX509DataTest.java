/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.xmlsec.keyinfo.impl;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.AssertJUnit;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.security.SecurityHelper;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoCriterion;
import org.opensaml.xmlsec.keyinfo.impl.BasicProviderKeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.impl.KeyInfoProvider;
import org.opensaml.xmlsec.keyinfo.impl.provider.InlineX509DataProvider;
import org.opensaml.xmlsec.signature.KeyInfo;


/**
 * Test resolution of credentials from X509Data child of KeyInfo,
 * where the X509Data contains a single certificate.
 */
public class SingleCertX509DataTest extends XMLObjectBaseTestCase {
    
    private KeyInfoCredentialResolver resolver;
    
    private RSAPublicKey pubKey;
    private final String rsaBase64 = 
    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAw/WnsbA8frhQ+8EoPgMr" +
    "QjpINjt20U/MvsvmoAgQnAgEF4OYt9Vj9/2YvMO4NvX1fNDFzoYRyOMrypF7skAP" +
    "cITUhdcPSEpI4nsf5yFZLziK/tQ26RsccE7WhpGB8eHu9tfseelgyioorvmt+JCo" +
    "P15c5rYUuIfVC+eEsYolw344q6N61OACHETuySL0a1+GFu3WoISXte1pQIst7HKv" +
    "BbHH41HEWAxT6e0hlD5PyKL4lBJadGHXg8Zz4r2jV2n6+Ox7raEWmtVCGFxsAoCR" +
    "alu6nvs2++5Nnb4C1SE640esfYhfeMd5JYfsTNMaQ8sZLpsWdglAGpa/Q87K19LI" +
    "wwIDAQAB";
    
    private X509Certificate entityCert;
    private String entityCertBase64 = 
        "MIIDizCCAnOgAwIBAgIBATANBgkqhkiG9w0BAQUFADAuMRIwEAYDVQQKEwlJbnRl" +
        "cm5ldDIxGDAWBgNVBAMTD2Zvby5leGFtcGxlLm9yZzAeFw0wNzA0MDkwMzUxMTda" +
        "Fw0xNzA0MDYwMzUxMTdaMC4xEjAQBgNVBAoTCUludGVybmV0MjEYMBYGA1UEAxMP" +
        "Zm9vLmV4YW1wbGUub3JnMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA" +
        "w/WnsbA8frhQ+8EoPgMrQjpINjt20U/MvsvmoAgQnAgEF4OYt9Vj9/2YvMO4NvX1" +
        "fNDFzoYRyOMrypF7skAPcITUhdcPSEpI4nsf5yFZLziK/tQ26RsccE7WhpGB8eHu" +
        "9tfseelgyioorvmt+JCoP15c5rYUuIfVC+eEsYolw344q6N61OACHETuySL0a1+G" +
        "Fu3WoISXte1pQIst7HKvBbHH41HEWAxT6e0hlD5PyKL4lBJadGHXg8Zz4r2jV2n6" +
        "+Ox7raEWmtVCGFxsAoCRalu6nvs2++5Nnb4C1SE640esfYhfeMd5JYfsTNMaQ8sZ" +
        "LpsWdglAGpa/Q87K19LIwwIDAQABo4GzMIGwMAkGA1UdEwQCMAAwLAYJYIZIAYb4" +
        "QgENBB8WHU9wZW5TU0wgR2VuZXJhdGVkIENlcnRpZmljYXRlMB0GA1UdDgQWBBQO" +
        "uBH/fniYGRwnTeCejBF2Hg6W5jBWBgNVHSMETzBNgBQOuBH/fniYGRwnTeCejBF2" +
        "Hg6W5qEypDAwLjESMBAGA1UEChMJSW50ZXJuZXQyMRgwFgYDVQQDEw9mb28uZXhh" +
        "bXBsZS5vcmeCAQEwDQYJKoZIhvcNAQEFBQADggEBALJ7VxOwQDBpYEO3fAdVEL/m" +
        "WA2xPTw0fuLkmyecutqB7qOWdSxvjh8wQAGmpg8APV5wxIJrtQADqLvsgY8/Zrq/" +
        "aMZLfi6YFoNaKaDjvNlp96iarOLNU5fHI/HNh0W444EbiYzg2R/TBixwGYxSTmWA" +
        "rUIu4ILi+9ek49F5oKwkOKrPGLQmJmWKNgxvpyLJFlfnnJSEVLmazAGuTSmbQguY" +
        "F+AS4pRESlwMuViS9eX27VVi7Tx7SCxOZnoXUI0fPCuYE66jNtrcfSSWRkFE+DOW" +
        "rtpJVZqXa9Z1LPYjMZcfyC/jSgGMSSpl/7wCiU8ElDQvWOVsU8EtSM7UjgSWu+g=";


    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        List<KeyInfoProvider> providers = new ArrayList<KeyInfoProvider>();
        providers.add(new InlineX509DataProvider());
        resolver = new BasicProviderKeyInfoCredentialResolver(providers);
        
        pubKey = SecurityHelper.buildJavaRSAPublicKey(rsaBase64);
        entityCert = SecurityHelper.buildJavaX509Cert(entityCertBase64);
    }
    
    /**
     * Test basic credential resolution.
     * 
     * @throws ResolverException on error resolving credentials
     */
    @Test
    public void testCredResolution() throws ResolverException {
        KeyInfo keyInfo = 
            (KeyInfo) unmarshallElement("/data/org/opensaml/xmlsec/keyinfo/impl/SingleX509Certificate.xml");
        CriteriaSet criteriaSet = new CriteriaSet( new KeyInfoCriterion(keyInfo) );
        Iterator<Credential> iter = resolver.resolve(criteriaSet).iterator();
        
        AssertJUnit.assertTrue("No credentials were found", iter.hasNext());
        
        Credential credential = iter.next();
        AssertJUnit.assertNotNull("Credential was null", credential);
        AssertJUnit.assertFalse("Too many credentials returned", iter.hasNext());
        
        AssertJUnit.assertTrue("Credential is not of the expected type", credential instanceof X509Credential);
        X509Credential x509Credential = (X509Credential) credential;
        
        AssertJUnit.assertNotNull("Public key was null", x509Credential.getPublicKey());
        AssertJUnit.assertEquals("Expected public key value not found", pubKey, x509Credential.getPublicKey());
        
        AssertJUnit.assertNotNull("Entity certificate was null", x509Credential.getEntityCertificate());
        AssertJUnit.assertEquals("Expected X509Certificate value not found", entityCert, x509Credential.getEntityCertificate());
    }
    

}
