/*
 * Copyright 2008 Members of the EGEE Collaboration.
 * Copyright 2008 University Corporation for Advanced Internet Development, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opensaml.ws.wstrust;

import java.io.InputStream;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;
import org.opensaml.ws.WSBaseTestCase;
import org.opensaml.ws.wssecurity.Created;
import org.opensaml.ws.wssecurity.Password;
import org.opensaml.ws.wssecurity.Timestamp;
import org.opensaml.ws.wssecurity.Username;
import org.opensaml.ws.wssecurity.UsernameToken;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLConfigurator;
import org.opensaml.xml.schema.XSAny;
import org.opensaml.xml.schema.XSBooleanValue;
import org.opensaml.xml.schema.impl.XSAnyBuilder;
import org.opensaml.xml.schema.impl.XSAnyMarshaller;
import org.opensaml.xml.schema.impl.XSAnyUnmarshaller;

/**
 * WSTrustObjectsTestCase is the base test case for the WS-Trust objects.
 * 
 */
public class WSTrustObjectsTestCase extends WSBaseTestCase {
    
    /**
     * QName for test Claims element.
     */
    private static final QName TEST_CLAIMS_QNAME = new QName("urn:test:claims:ns", "TestClaim", "tc");

    /** {@inheritDoc} */
    protected void configureWS() throws Exception {
        // load ws-trust config
        InputStream is= getClass().getResourceAsStream("/wstrust-config.xml");
        XMLConfigurator configurator= new XMLConfigurator();
        configurator.load(is);
        // load ws-security config
        is= getClass().getResourceAsStream("/wssecurity-config.xml");
        configurator.load(is);
        
        // register provider for TestClaims supporting config
        Configuration.registerObjectProvider(TEST_CLAIMS_QNAME,  
                new XSAnyBuilder(), new XSAnyMarshaller(), new XSAnyUnmarshaller());
    }

    public void testAllowPostdating() throws Exception {
        AllowPostdating allowPostdating= buildXMLObject(AllowPostdating.ELEMENT_NAME);
        marshallAndUnmarshall(allowPostdating);
    }

    public void testAuthenticationType() throws Exception {
        AuthenticationType authenticationType= buildXMLObject(AuthenticationType.ELEMENT_NAME);
        authenticationType.setValue("urn:mace:switch.ch:SWITCHaai:loa:3");
        marshallAndUnmarshall(authenticationType);
    }

    public void testAuthenticator() throws Exception {
        Authenticator authenticator= buildXMLObject(Authenticator.ELEMENT_NAME);
        CombinedHash combinedHash= createCombinedHash("asdfasdfasdASDFASDFDASasdfasASd");
        authenticator.setCombinedHash(combinedHash);
        marshallAndUnmarshall(authenticator);
    }

    public void testBinaryExchange() throws Exception {
        BinaryExchange binaryExchange= buildXMLObject(BinaryExchange.ELEMENT_NAME);
        binaryExchange.setValueType("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3");
        binaryExchange.setValue("BASE64BINARY_X509_DATA...");
        marshallAndUnmarshall(binaryExchange);
    }

    public void testBinarySecret() throws Exception {
        BinarySecret binarySecret= buildXMLObject(BinarySecret.ELEMENT_NAME);
        binarySecret.setType(BinarySecret.TYPE_SYMMETRIC_KEY);
        binarySecret.setValue("BASE64BINARY_SYMMETRICKEY_DATA...\n...");
        marshallAndUnmarshall(binarySecret);
    }

    public void testCancelTarget() throws Exception {
        CancelTarget cancelTarget= buildXMLObject(CancelTarget.ELEMENT_NAME);
        UsernameToken usernameToken= createUsernameToken("testuser",
                                                         "testpassword");
        cancelTarget.setUnknownXMLObject(usernameToken);
        marshallAndUnmarshall(cancelTarget);
    }

    public void testCanonicalizationAlgorithm() throws Exception {
        CanonicalizationAlgorithm canonicalizationAlgorithm= buildXMLObject(CanonicalizationAlgorithm.ELEMENT_NAME);
        canonicalizationAlgorithm.setValue("CanonicalizationAlgorithmValue");
        marshallAndUnmarshall(canonicalizationAlgorithm);
    }

    public void testChallenge() throws Exception {
        Challenge challenge= buildXMLObject(Challenge.ELEMENT_NAME);
        challenge.setValue("CHALLENGE_VALUE");
        marshallAndUnmarshall(challenge);
    }

    public void testClaims() throws Exception {
        Claims claims= createClaims();
        marshallAndUnmarshall(claims);
    }

    public void testCode() throws Exception {
        Code code= buildXMLObject(Code.ELEMENT_NAME);
        code.setValue(Code.VALID);
        marshallAndUnmarshall(code);
    }

    public void testCombinedHash() throws Exception {
        CombinedHash combinedHash= createCombinedHash("ADSFJAKSLDFJASLKDFJALSKJkljasfiaskfjJASDFKLASdlf");
        marshallAndUnmarshall(combinedHash);
    }

    public void testComputedKey() throws Exception {
        ComputedKey computedKey= buildXMLObject(ComputedKey.ELEMENT_NAME);
        computedKey.setValue(ComputedKey.PSHA1);
        marshallAndUnmarshall(computedKey);
    }

    public void testComputedKeyAlgorithm() throws Exception {
        ComputedKeyAlgorithm computedKeyAlgorithm= buildXMLObject(ComputedKeyAlgorithm.ELEMENT_NAME);
        computedKeyAlgorithm.setValue("ComputedKeyAlgorithmValue");
        marshallAndUnmarshall(computedKeyAlgorithm);
    }

    public void testDelegatable() throws Exception {
        Delegatable delegatable= buildXMLObject(Delegatable.ELEMENT_NAME);
        XSBooleanValue value= delegatable.getValue();
        assertFalse(value.getValue());
        marshallAndUnmarshall(delegatable);

        delegatable= buildXMLObject(Delegatable.ELEMENT_NAME);
        delegatable.setValue(new XSBooleanValue(true, false));
        value= delegatable.getValue();
        assertTrue(value.getValue());
        marshallAndUnmarshall(delegatable);
    }

    public void testDelegateTo() throws Exception {
        DelegateTo delegateTo= buildXMLObject(DelegateTo.ELEMENT_NAME);
        delegateTo.setUnknownXMLObject(createUsernameToken("delegateUser", "delegatePassord"));
        marshallAndUnmarshall(delegateTo);
    }

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

        rst= unmarshallXML("/data/org/opensaml/ws/wstrust/RequestSecurityToken.xml");
        rst.releaseDOM();
        marshallAndUnmarshall(rst);
    }

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
