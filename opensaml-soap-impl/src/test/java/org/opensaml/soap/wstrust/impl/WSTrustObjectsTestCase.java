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

package org.opensaml.soap.wstrust.impl;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import javax.xml.namespace.QName;

import org.joda.time.DateTime;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.schema.impl.XSAnyBuilder;
import org.opensaml.core.xml.schema.impl.XSAnyMarshaller;
import org.opensaml.core.xml.schema.impl.XSAnyUnmarshaller;
import org.opensaml.soap.WSBaseTestCase;
import org.opensaml.soap.wssecurity.Created;
import org.opensaml.soap.wssecurity.Password;
import org.opensaml.soap.wssecurity.Timestamp;
import org.opensaml.soap.wssecurity.Username;
import org.opensaml.soap.wssecurity.UsernameToken;
import org.opensaml.soap.wstrust.AllowPostdating;
import org.opensaml.soap.wstrust.AuthenticationType;
import org.opensaml.soap.wstrust.Authenticator;
import org.opensaml.soap.wstrust.BinaryExchange;
import org.opensaml.soap.wstrust.BinarySecret;
import org.opensaml.soap.wstrust.CancelTarget;
import org.opensaml.soap.wstrust.CanonicalizationAlgorithm;
import org.opensaml.soap.wstrust.Challenge;
import org.opensaml.soap.wstrust.Claims;
import org.opensaml.soap.wstrust.Code;
import org.opensaml.soap.wstrust.CombinedHash;
import org.opensaml.soap.wstrust.ComputedKey;
import org.opensaml.soap.wstrust.ComputedKeyAlgorithm;
import org.opensaml.soap.wstrust.Delegatable;
import org.opensaml.soap.wstrust.DelegateTo;
import org.opensaml.soap.wstrust.RequestSecurityToken;
import org.opensaml.soap.wstrust.RequestType;
import org.opensaml.soap.wstrust.TokenType;

/**
 * WSTrustObjectsTestCase is the base test case for the WS-Trust objects.
 * 
 */
public class WSTrustObjectsTestCase extends WSBaseTestCase {
    
    /**
     * QName for test Claims element.
     */
    private static final QName TEST_CLAIMS_QNAME = new QName("urn:test:claims:ns", "TestClaim", "tc");

    @BeforeMethod
    protected void setUp() throws Exception {
        // register provider for TestClaims supporting config
        XMLObjectProviderRegistrySupport.registerObjectProvider(TEST_CLAIMS_QNAME,  
                new XSAnyBuilder(), new XSAnyMarshaller(), new XSAnyUnmarshaller());
    }

    @Test
    public void testAllowPostdating() throws Exception {
        AllowPostdating allowPostdating= buildXMLObject(AllowPostdating.ELEMENT_NAME);
        marshallAndUnmarshall(allowPostdating);
    }

    @Test
    public void testAuthenticationType() throws Exception {
        AuthenticationType authenticationType= buildXMLObject(AuthenticationType.ELEMENT_NAME);
        authenticationType.setValue("urn:mace:switch.ch:SWITCHaai:loa:3");
        marshallAndUnmarshall(authenticationType);
    }

    @Test
    public void testAuthenticator() throws Exception {
        Authenticator authenticator= buildXMLObject(Authenticator.ELEMENT_NAME);
        CombinedHash combinedHash= createCombinedHash("asdfasdfasdASDFASDFDASasdfasASd");
        authenticator.setCombinedHash(combinedHash);
        marshallAndUnmarshall(authenticator);
    }

    @Test
    public void testBinaryExchange() throws Exception {
        BinaryExchange binaryExchange= buildXMLObject(BinaryExchange.ELEMENT_NAME);
        binaryExchange.setValueType("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3");
        binaryExchange.setValue("BASE64BINARY_X509_DATA...");
        marshallAndUnmarshall(binaryExchange);
    }

    @Test
    public void testBinarySecret() throws Exception {
        BinarySecret binarySecret= buildXMLObject(BinarySecret.ELEMENT_NAME);
        binarySecret.setType(BinarySecret.TYPE_SYMMETRIC_KEY);
        binarySecret.setValue("BASE64BINARY_SYMMETRICKEY_DATA...\n...");
        marshallAndUnmarshall(binarySecret);
    }

    @Test
    public void testCancelTarget() throws Exception {
        CancelTarget cancelTarget= buildXMLObject(CancelTarget.ELEMENT_NAME);
        UsernameToken usernameToken= createUsernameToken("testuser",
                                                         "testpassword");
        cancelTarget.setUnknownXMLObject(usernameToken);
        marshallAndUnmarshall(cancelTarget);
    }

    @Test
    public void testCanonicalizationAlgorithm() throws Exception {
        CanonicalizationAlgorithm canonicalizationAlgorithm= buildXMLObject(CanonicalizationAlgorithm.ELEMENT_NAME);
        canonicalizationAlgorithm.setValue("CanonicalizationAlgorithmValue");
        marshallAndUnmarshall(canonicalizationAlgorithm);
    }

    @Test
    public void testChallenge() throws Exception {
        Challenge challenge= buildXMLObject(Challenge.ELEMENT_NAME);
        challenge.setValue("CHALLENGE_VALUE");
        marshallAndUnmarshall(challenge);
    }

    @Test
    public void testClaims() throws Exception {
        Claims claims= createClaims();
        marshallAndUnmarshall(claims);
    }

    @Test
    public void testCode() throws Exception {
        Code code= buildXMLObject(Code.ELEMENT_NAME);
        code.setValue(Code.VALID);
        marshallAndUnmarshall(code);
    }

    @Test
    public void testCombinedHash() throws Exception {
        CombinedHash combinedHash= createCombinedHash("ADSFJAKSLDFJASLKDFJALSKJkljasfiaskfjJASDFKLASdlf");
        marshallAndUnmarshall(combinedHash);
    }

    @Test
    public void testComputedKey() throws Exception {
        ComputedKey computedKey= buildXMLObject(ComputedKey.ELEMENT_NAME);
        computedKey.setValue(ComputedKey.PSHA1);
        marshallAndUnmarshall(computedKey);
    }

    @Test
    public void testComputedKeyAlgorithm() throws Exception {
        ComputedKeyAlgorithm computedKeyAlgorithm= buildXMLObject(ComputedKeyAlgorithm.ELEMENT_NAME);
        computedKeyAlgorithm.setValue("ComputedKeyAlgorithmValue");
        marshallAndUnmarshall(computedKeyAlgorithm);
    }

    @Test
    public void testDelegatable() throws Exception {
        Delegatable delegatable= buildXMLObject(Delegatable.ELEMENT_NAME);
        XSBooleanValue value= delegatable.getValue();
        Assert.assertFalse(value.getValue());
        marshallAndUnmarshall(delegatable);

        delegatable= buildXMLObject(Delegatable.ELEMENT_NAME);
        delegatable.setValue(new XSBooleanValue(true, false));
        value= delegatable.getValue();
        Assert.assertTrue(value.getValue());
        marshallAndUnmarshall(delegatable);
    }

    @Test
    public void testDelegateTo() throws Exception {
        DelegateTo delegateTo= buildXMLObject(DelegateTo.ELEMENT_NAME);
        delegateTo.setUnknownXMLObject(createUsernameToken("delegateUser", "delegatePassord"));
        marshallAndUnmarshall(delegateTo);
    }
    
    @Test
    public void testEncryption() throws Exception {
        //TODO
    }

    @Test
    public void testEncryptionAlgorithm() throws Exception {
        //TODO
    }

    @Test
    public void testEncryptWith() throws Exception {
        //TODO
    }

    @Test
    public void testEntropy() throws Exception {
        //TODO
    }

    @Test
    public void testForwardable() throws Exception {
        //TODO
    }

    @Test
    public void testIssuedTokens() throws Exception {
        //TODO
    }

    @Test
    public void testIssuer() throws Exception {
        //TODO
    }

    @Test
    public void testKeyExchangeToken() throws Exception {
        //TODO
    }

    @Test
    public void testKeySize() throws Exception {
        //TODO
    }
    
    @Test
    public void testKeyType() throws Exception {
        //TODO
    }

    @Test
    public void testKeyWrapAlgorithm() throws Exception {
        //TODO
    }
    
    @Test
    public void testLifetime() throws Exception {
        //TODO
    }

    @Test
    public void testOnBehalfOf() throws Exception {
        //TODO
    }

    @Test
    public void testParticipant() throws Exception {
        //TODO
    }

    @Test
    public void testPrimary() throws Exception {
        //TODO
    }

    @Test
    public void testProofEncryption() throws Exception {
        //TODO
    }

    @Test
    public void testReason() throws Exception {
        //TODO
    }

    @Test
    public void testRenewing() throws Exception {
        //TODO
    }

    @Test
    public void testRenewTarget() throws Exception {
        //TODO
    }

    @Test
    public void testRequestedAttachedReference() throws Exception {
        //TODO
    }

    @Test
    public void testRequestedProofToken() throws Exception {
        //TODO
    }

    @Test
    public void testRequestedSecurityToken() throws Exception {
        //TODO
    }

    @Test
    public void testRequestedTokenCancelled() throws Exception {
        //TODO
    }

    @Test
    public void testRequestedUnattachedReference() throws Exception {
        //TODO
    }

    @Test
    public void testKET() throws Exception {
        //TODO
    }
    
    @Test
    public void testRequestSecurityToken() throws Exception {
        RequestSecurityToken rst= buildXMLObject(RequestSecurityToken.ELEMENT_NAME);
        String context= "Context-" + System.currentTimeMillis();
        rst.setContext(context);
        RequestType requestType= buildXMLObject(RequestType.ELEMENT_NAME);
        requestType.setValue(RequestType.ISSUE);
        rst.getUnknownXMLObjects().add(requestType);
        TokenType tokenType= buildXMLObject(TokenType.ELEMENT_NAME);
        tokenType.setValue("http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.1#SAMLV2.0");
        rst.getUnknownXMLObjects().add(tokenType);
        
        Claims claims= createClaims();
        rst.getUnknownXMLObjects().add(claims);
        
        Timestamp timestamp= createTimestamp();
        rst.getUnknownXMLObjects().add(timestamp);
        marshallAndUnmarshall(rst);

        rst= unmarshallElement("/data/org/opensaml/soap/wstrust/impl/RequestSecurityToken.xml");
        rst.releaseDOM();
        marshallAndUnmarshall(rst);
    }

    @Test
    public void testRequestSecurityTokenCollection() throws Exception {
        //TODO
    }

    @Test
    public void testRequestSecurityTokenResponse() throws Exception {
        //TODO
    }

    @Test
    public void testRequestSecurityTokenResponseCollection() throws Exception {
        //TODO
    }

    @Test
    public void testRequestType() throws Exception {
        //TODO
    }

    @Test
    public void testSignatureAlgorithm() throws Exception {
        //TODO
    }

    @Test
    public void testSignChallenge() throws Exception {
        //TODO
    }

    @Test
    public void testSignChallengeResponse() throws Exception {
        //TODO
    }

    @Test
    public void testSignWith() throws Exception {
        //TODO
    }

    @Test
    public void testStatus() throws Exception {
        //TODO
    }

    @Test
    public void testTokenType() throws Exception {
        //TODO
    }

    @Test
    public void testUseKey() throws Exception {
        //TODO
    }

    @Test
    public void testValidateTarget() throws Exception {
        //TODO
    }

    
    /*--------------------------*/
    /* Utility methods          */
    /*--------------------------*/
    
    protected Claims createClaims() throws Exception {
        Claims claims= buildXMLObject(Claims.ELEMENT_NAME);
        claims.setDialect("urn:test:claims:some-test-dialect");
        
        XSAny claimData  = (XSAny) getBuilder(TEST_CLAIMS_QNAME).buildObject(TEST_CLAIMS_QNAME);
        claimData.setTextContent("urn:test:claims:attribute-bundle-Foo");
        
        claims.getUnknownXMLObjects().add(claimData);
        
        return claims;
    }

    protected Timestamp createTimestamp() throws Exception {
        Timestamp timestamp= buildXMLObject(Timestamp.ELEMENT_NAME);
        Created created= buildXMLObject(Created.ELEMENT_NAME);
        created.setDateTime(new DateTime());
        timestamp.setCreated(created);
        return timestamp;
    }

    protected CombinedHash createCombinedHash(String value) throws Exception {
        CombinedHash combinedHash= buildXMLObject(CombinedHash.ELEMENT_NAME);
        if (value == null) {
            combinedHash.setValue("BASE64BINARY_CombinedHASH..");
        }
        else {
            combinedHash.setValue(value);
        }
        return combinedHash;
    }

    protected UsernameToken createUsernameToken(String user, String pass)
            throws Exception {
        UsernameToken usernameToken= buildXMLObject(UsernameToken.ELEMENT_NAME);
        Username username= buildXMLObject(Username.ELEMENT_NAME);
        username.setValue(user);
        Password password= buildXMLObject(Password.ELEMENT_NAME);
        password.setValue(pass);
        Created created= buildXMLObject(Created.ELEMENT_NAME);
        created.setDateTime(new DateTime());
        String id= "UsernameToken-" + System.currentTimeMillis();
        usernameToken.setWSUId(id);
        usernameToken.setUsername(username);
        usernameToken.getUnknownXMLObjects().add(password);
        usernameToken.getUnknownXMLObjects().add(created);

        return usernameToken;

    }

}
