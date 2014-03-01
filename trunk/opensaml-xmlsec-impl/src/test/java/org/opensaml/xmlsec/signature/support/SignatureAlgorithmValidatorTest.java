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

package org.opensaml.xmlsec.signature.support;

import java.util.HashSet;
import java.util.Set;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.mock.SignableSimpleXMLObject;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.impl.SignatureAlgorithmValidator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test signature algorithm whitelist and blacklist evaluation.
 */
public class SignatureAlgorithmValidatorTest extends XMLObjectBaseTestCase {
    
    private Signature signature;
    
    private final String targetSignatureMethod = SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1;
    
    private final String targetDigestMethod = EncryptionConstants.ALGO_ID_DIGEST_SHA256;
    
    private Set<String> whitelist;
    
    private Set<String> blacklist;
    
    private SignatureAlgorithmValidator validator;
    
    @BeforeMethod
    public void setUp() {
        whitelist = new HashSet<>();
        blacklist = new HashSet<>();
        SignableSimpleXMLObject ssxo = unmarshallElement("/data/org/opensaml/xmlsec/signature/support/envelopedSignature.xml");
        signature = ssxo.getSignature();
    }
    
    @Test
    public void testEmptyLists() throws SignatureException {
        validator = new SignatureAlgorithmValidator(whitelist, blacklist);
        validator.validate(signature);
    }
    
    @Test
    public void testWhitelistedURIs() throws SignatureException {
        whitelist.add(targetSignatureMethod);
        whitelist.add(targetDigestMethod);
        validator = new SignatureAlgorithmValidator(whitelist, blacklist);
        validator.validate(signature);
    }
    
    @Test(expectedExceptions=SignatureException.class)
    public void testBlacklistedSignatureMethod() throws SignatureException {
        blacklist.add(targetSignatureMethod);
        validator = new SignatureAlgorithmValidator(whitelist, blacklist);
        validator.validate(signature);
    }
    
    @Test(expectedExceptions=SignatureException.class)
    public void testBlacklistedBlacklistedMethod() throws SignatureException {
        blacklist.add(targetDigestMethod);
        validator = new SignatureAlgorithmValidator(whitelist, blacklist);
        validator.validate(signature);
    }

}
