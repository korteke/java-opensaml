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

package org.opensaml.saml.saml1.profile.impl;

import java.security.KeyPair;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.opensaml.core.OpenSAMLInitBaseTestCase;
import org.opensaml.profile.RequestContextBuilder;
import org.opensaml.profile.action.ActionTestingSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.saml1.core.Assertion;
import org.opensaml.saml.saml1.core.Response;
import org.opensaml.saml.saml1.profile.SAML1ActionTestingSupport;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/** {@link SignAssertions} unit test. */
public class SignAssertionsTest extends OpenSAMLInitBaseTestCase {

    private SignAssertions action;

    private ProfileRequestContext<Object,Response> prc;

    @BeforeMethod public void setUp() throws ComponentInitializationException {

        action = new SignAssertions();
        action.setId("test");
        action.initialize();

        prc = new RequestContextBuilder().setOutboundMessage(
                SAML1ActionTestingSupport.buildResponse()).buildProfileRequestContext();
    }

    @Test public void testNoOutboundMessageContext() throws Exception {
        prc.setOutboundMessageContext(null);
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }

    @Test public void testNoResponse() throws Exception {
        prc.getOutboundMessageContext().setMessage(null);
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }
    
    @Test public void testNoSecurityParametersContext() throws Exception {
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
    }

    @Test public void testNoSignatureSigningParameters() throws Exception {
        prc.addSubcontext(new SecurityParametersContext());
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
    }

    @Test public void testNoAssertions() throws Exception {
        final SecurityParametersContext secParamCtx = new SecurityParametersContext();
        secParamCtx.setSignatureSigningParameters(new SignatureSigningParameters());
        prc.addSubcontext(secParamCtx);

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
    }

    @Test public void testSignAssertions() throws Exception {
        final Assertion assertion = SAML1ActionTestingSupport.buildAssertion();
        prc.getOutboundMessageContext().getMessage().getAssertions().add(assertion);

        final SignatureSigningParameters signingParameters = new SignatureSigningParameters();
        final KeyPair kp = KeySupport.generateKeyPair("RSA", 1024, null);
        signingParameters.setSigningCredential(CredentialSupport.getSimpleCredential(kp.getPublic(), kp.getPrivate()));

        final SecurityParametersContext secParamCtx = new SecurityParametersContext();
        secParamCtx.setSignatureSigningParameters(signingParameters);
        prc.addSubcontext(secParamCtx);

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
    }

    // TODO Test that assertion was signed correctly ?

    // TODO Test event id when signing throws an exception.
}