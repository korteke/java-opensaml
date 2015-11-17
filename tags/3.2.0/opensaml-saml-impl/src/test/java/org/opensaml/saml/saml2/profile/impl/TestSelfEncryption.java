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

package org.opensaml.saml.saml2.profile.impl;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.collection.Pair;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.opensaml.core.OpenSAMLInitBaseTestCase;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.profile.RequestContextBuilder;
import org.opensaml.profile.action.ActionTestingSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.saml2.core.EncryptedID;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.profile.SAML2ActionTestingSupport;
import org.opensaml.saml.saml2.profile.context.EncryptionContext;
import org.opensaml.xmlsec.EncryptionParameters;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.encryption.support.EncryptionException;
import org.opensaml.xmlsec.keyinfo.impl.BasicKeyInfoGeneratorFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/** Unit test for self-encryption support in {@link AbstractEncryptAction} (using {@link EncryptNameIDs} as the concrete impl.) */
public class TestSelfEncryption extends OpenSAMLInitBaseTestCase {
    
    private EncryptionParameters encParams, encParamsSelf1, encParamsSelf2;
    
    private ProfileRequestContext<Object,Response> prc;
    
    private EncryptNameIDs action;
    
    @BeforeMethod
    public void setUp() throws NoSuchAlgorithmException, NoSuchProviderException {
        
        final BasicKeyInfoGeneratorFactory generator = new BasicKeyInfoGeneratorFactory();
        generator.setEmitPublicKeyValue(true);
        
        encParams = new EncryptionParameters();
        encParams.setDataEncryptionAlgorithm(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128);
        encParams.setDataKeyInfoGenerator(generator.newInstance());
        encParams.setKeyTransportEncryptionAlgorithm(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        encParams.setKeyTransportEncryptionCredential(
                AlgorithmSupport.generateKeyPairAndCredential(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP, 1024, false));
        encParams.setKeyTransportKeyInfoGenerator(generator.newInstance());
        
        encParamsSelf1 = new EncryptionParameters();
        encParamsSelf1.setDataEncryptionAlgorithm(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128);
        encParamsSelf1.setDataKeyInfoGenerator(generator.newInstance());
        encParamsSelf1.setKeyTransportEncryptionAlgorithm(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        encParamsSelf1.setKeyTransportEncryptionCredential(
                AlgorithmSupport.generateKeyPairAndCredential(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP, 1024, false));
        encParamsSelf1.setKeyTransportKeyInfoGenerator(generator.newInstance());
        
        encParamsSelf2 = new EncryptionParameters();
        encParamsSelf2.setDataEncryptionAlgorithm(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128);
        encParamsSelf2.setDataKeyInfoGenerator(generator.newInstance());
        encParamsSelf2.setKeyTransportEncryptionAlgorithm(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        encParamsSelf2.setKeyTransportEncryptionCredential(
                AlgorithmSupport.generateKeyPairAndCredential(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP, 1024, false));
        encParamsSelf2.setKeyTransportKeyInfoGenerator(generator.newInstance());
        
        prc = new RequestContextBuilder().buildProfileRequestContext();
        prc.getOutboundMessageContext().getSubcontext(EncryptionContext.class, true).setIdentifierEncryptionParameters(encParams);
        
        action = new EncryptNameIDs();
    }
    
    @Test
    public void testSelfEncryption() throws EncryptionException, ComponentInitializationException, MarshallingException {
        final Response response = SAML2ActionTestingSupport.buildResponse();
        prc.getOutboundMessageContext().setMessage(response);
        response.getAssertions().add(SAML2ActionTestingSupport.buildAssertion());
        response.getAssertions().get(0).setSubject(SAML2ActionTestingSupport.buildSubject("morpheus"));
        
        action.setEncryptToSelf(Predicates.<ProfileRequestContext>alwaysTrue());
        action.setEncryptToSelfParametersStrategy(new Function<Pair<ProfileRequestContext,EncryptionParameters>, List<EncryptionParameters>>() {
            public List<EncryptionParameters> apply(@Nullable Pair<ProfileRequestContext, EncryptionParameters> input) {
                return Lists.newArrayList(encParamsSelf1, encParamsSelf2);
            }
        });
        action.setSelfRecipientLookupStrategy(new Function<ProfileRequestContext, String>() {
            public String apply(@Nullable ProfileRequestContext input) {
                return "https://idp.example.org";
            }
        });
        
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        Assert.assertEquals(response.getAssertions().size(), 1);
        Assert.assertNull(response.getAssertions().get(0).getSubject().getNameID());
        Assert.assertNotNull(response.getAssertions().get(0).getSubject().getEncryptedID());
        
        final EncryptedID encTarget = response.getAssertions().get(0).getSubject().getEncryptedID();

        Assert.assertEquals(encTarget.getEncryptedData().getType(), EncryptionConstants.TYPE_ELEMENT, "Type attribute");
        Assert.assertEquals(encTarget.getEncryptedData().getEncryptionMethod().getAlgorithm(),
                EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128, "Algorithm attribute");
        Assert.assertNotNull(encTarget.getEncryptedData().getKeyInfo(), "KeyInfo");
        Assert.assertEquals(encTarget.getEncryptedData().getKeyInfo().getEncryptedKeys().size(), 3, 
                "Number of EncryptedKeys");
        Assert.assertFalse(Strings.isNullOrEmpty(encTarget.getEncryptedData().getID()),
                "EncryptedData ID attribute was empty");
    }
    
    @Test
    public void testFailureNoSelfEncryptionCreds() throws EncryptionException, ComponentInitializationException, MarshallingException {
        final Response response = SAML2ActionTestingSupport.buildResponse();
        prc.getOutboundMessageContext().setMessage(response);
        response.getAssertions().add(SAML2ActionTestingSupport.buildAssertion());
        response.getAssertions().get(0).setSubject(SAML2ActionTestingSupport.buildSubject("morpheus"));
        
        action.setEncryptToSelf(Predicates.<ProfileRequestContext>alwaysTrue());
        action.setEncryptToSelfParametersStrategy(new Function<Pair<ProfileRequestContext,EncryptionParameters>, List<EncryptionParameters>>() {
            public List<EncryptionParameters> apply(@Nullable Pair<ProfileRequestContext, EncryptionParameters> input) {
                return Collections.emptyList();
            }
        });
        action.setSelfRecipientLookupStrategy(new Function<ProfileRequestContext, String>() {
            public String apply(@Nullable ProfileRequestContext input) {
                return "https://idp.example.org";
            }
        });
        
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.UNABLE_TO_ENCRYPT);
        
        Assert.assertEquals(response.getAssertions().size(), 1);
        Assert.assertNotNull(response.getAssertions().get(0).getSubject().getNameID());
        Assert.assertNull(response.getAssertions().get(0).getSubject().getEncryptedID());
    }
    
}