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

package org.opensaml.security.x509.impl;

import java.util.ArrayList;

import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialResolver;
import org.opensaml.security.credential.impl.StaticCredentialResolver;
import org.opensaml.security.messaging.CertificateNameOptions;
import org.opensaml.security.trust.impl.ExplicitX509CertificateTrustEngine;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class BasicX509CredentialValidationConfigurationTest {
    
    private BasicX509CredentialValidationConfiguration config;
    
    @BeforeMethod
    public void setUp() {
        config = new BasicX509CredentialValidationConfiguration();
    }
    
    @Test
    public void testTrustEngine() {
        Assert.assertNull(config.getX509TrustEngine());
        
        CredentialResolver credResolver = new StaticCredentialResolver(new ArrayList<Credential>());
        ExplicitX509CertificateTrustEngine trustEngine = new ExplicitX509CertificateTrustEngine(credResolver);
        config.setX509TrustEngine(trustEngine);
        
        Assert.assertNotNull(config.getX509TrustEngine());
    }
    
    @Test
    public void testNameOptions() {
        Assert.assertNull(config.getCertificateNameOptions());
        
        config.setCertificateNameOptions(new CertificateNameOptions());
        
        Assert.assertNotNull(config.getCertificateNameOptions());
    }

}
