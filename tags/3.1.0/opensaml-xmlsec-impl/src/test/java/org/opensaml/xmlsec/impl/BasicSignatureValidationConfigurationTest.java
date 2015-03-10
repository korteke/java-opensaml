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

package org.opensaml.xmlsec.impl;

import java.util.ArrayList;

import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialResolver;
import org.opensaml.security.credential.impl.StaticCredentialResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.impl.StaticKeyInfoCredentialResolver;
import org.opensaml.xmlsec.signature.support.impl.ExplicitKeySignatureTrustEngine;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class BasicSignatureValidationConfigurationTest {
    
    private BasicSignatureValidationConfiguration config;
    
    @BeforeMethod
    public void setUp() {
        config = new BasicSignatureValidationConfiguration();
    }
    
    @Test
    public void testSignatureTrustEngine() {
        Assert.assertNull(config.getSignatureTrustEngine());
        
        CredentialResolver credResolver = new StaticCredentialResolver(new ArrayList<Credential>());
        KeyInfoCredentialResolver keyInfoResolver = new StaticKeyInfoCredentialResolver(new ArrayList<Credential>());
        ExplicitKeySignatureTrustEngine trustEngine = new ExplicitKeySignatureTrustEngine(credResolver, keyInfoResolver);
        config.setSignatureTrustEngine(trustEngine);
        
        Assert.assertNotNull(config.getSignatureTrustEngine());
    }

}
