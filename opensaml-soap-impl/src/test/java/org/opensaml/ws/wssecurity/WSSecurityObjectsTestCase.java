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
package org.opensaml.ws.wssecurity;

import java.io.InputStream;
import java.util.List;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLAssert;
import org.joda.time.DateTime;
import org.opensaml.ws.WSBaseTestCase;
import org.opensaml.xml.XMLConfigurator;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Marshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * WSSecurityObjectsTestCase is the base test case for the WS-Security
 * objects.
 * 
 */
public class WSSecurityObjectsTestCase extends WSBaseTestCase {

    public Logger log= LoggerFactory.getLogger(WSSecurityObjectsTestCase.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.WSBaseTestCase#configureWS()
     */
    @Override
    protected void configureWS() throws Exception {
        // load ws-security config
        InputStream is= getClass().getResourceAsStream("/wssecurity-config.xml");
        XMLConfigurator configurator= new XMLConfigurator();
        configurator.load(is);
    }

    protected void unmarshallAndMarshall(String filename) throws Exception {
        // TODO implementation
    }

    public void testBinarySecurityToken() throws Exception {
        BinarySecurityToken token= buildXMLObject(BinarySecurityToken.ELEMENT_NAME);
        token.setWSUId("BinarySecurityToken-" + System.currentTimeMillis());
        token.setValue("Base64Encoded_X509_CERTIFICATE...");
        token.setValueType("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3");
        // check default encoding type
        assertEquals(BinarySecurityToken.ENCODING_TYPE_BASE64_BINARY, token.getEncodingType());
    
        marshallAndUnmarshall(token);
    
    }
    
    public void testCreated() throws Exception {
        //TODO
    }

    public void testEmbedded() throws Exception {
        Embedded embedded= buildXMLObject(Embedded.ELEMENT_NAME);
    
        UsernameToken usernameToken= createUsernameToken("EmbeddedUT",
                                                         "EmbeddedUT");
    
        embedded.setValueType("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#UsernameToken");
        embedded.getUnknownXMLObjects().add(usernameToken);
    
        marshallAndUnmarshall(embedded);
    
    }

    public void testEncryptedHeader() throws Exception {
        EncryptedHeader eh = buildXMLObject(EncryptedHeader.ELEMENT_NAME);
        eh.setWSUId("abc123");
        eh.setSOAP11MustUnderstand(true);
        eh.setSOAP11Actor("urn:test:soap11actor");
        eh.setSOAP12MustUnderstand(true);
        eh.setSOAP12Role("urn:test:soap12role");
        eh.setSOAP12Relay(true);
        marshallAndUnmarshall(eh);
    }

    public void testExpires() throws Exception {
        //TODO
    }

    public void testIteration() throws Exception {
        Iteration iteration= buildXMLObject(Iteration.ELEMENT_NAME);
        iteration.setValue(new Integer(1000));
        marshallAndUnmarshall(iteration);
    }
    
    public void testKeyIdentifier() throws Exception {
        //TODO
    }

    public void testNonce() throws Exception {
        Nonce nonce= buildXMLObject(Nonce.ELEMENT_NAME);
        nonce.setValue("Base64EncodedValue...");
        marshallAndUnmarshall(nonce);
    }

    public void testPassword() throws Exception {
    
        Password password= buildXMLObject(Password.ELEMENT_NAME);
        password.setValue("test");
        // check default
        assertEquals(Password.TYPE_PASSWORD_TEXT, password.getType());
        marshallAndUnmarshall(password);
    }

    public void testReference() throws Exception {
        Reference reference= buildXMLObject(Reference.ELEMENT_NAME);
    
        reference.setValueType("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#UsernameToken");
        reference.setURI("#UsernameToken-0000001");
    
        marshallAndUnmarshall(reference);
    }

    public void testSalt() throws Exception {
        Salt salt= buildXMLObject(Salt.ELEMENT_NAME);
        salt.setValue("Base64Encoded_Salt_VALUE...");
        marshallAndUnmarshall(salt);
    }

    public void testSecurity() throws Exception {
        //TODO
    }
    
    public void testSecurityTokenReference() throws Exception {
        //TODO
    }
    
    public void testSignatureConfirmation() throws Exception {
        //TODO
    }

    public void testTimestamp() throws Exception {
        Timestamp timestamp= buildXMLObject(Timestamp.ELEMENT_NAME);
        Created created= buildXMLObject(Created.ELEMENT_NAME);
        DateTime now= new DateTime();
        created.setDateTime(now);
        timestamp.setCreated(created);

        Expires expires= buildXMLObject(Expires.ELEMENT_NAME);
        expires.setDateTime(now.plusMinutes(10));
        timestamp.setExpires(expires);

        timestamp.setWSUId("Timestamp-" + System.currentTimeMillis());

        marshallAndUnmarshall(timestamp);
    }
    
    public void testTransformationParameters() throws Exception {
        //TODO
    }

    public void testUsername() throws Exception {
        Username username= buildXMLObject(Username.ELEMENT_NAME);
        username.setValue("test");
        marshallAndUnmarshall(username);
    }

    public void testUsernameToken() throws Exception {
        String refId= "UsernameToken-007";
        String refDateTimeStr= "2007-12-19T09:53:08.335Z";

        UsernameToken usernameToken= createUsernameToken("test", "test");
        usernameToken.setWSUId(refId);
        DateTime refDateTime= new DateTime(refDateTimeStr);
        Created usernameCreated = (Created) usernameToken.getUnknownXMLObjects(Created.ELEMENT_NAME).get(0);
        usernameCreated.setDateTime(refDateTime);

        // check default password type
        Password password= (Password) usernameToken.getUnknownXMLObjects(Password.ELEMENT_NAME).get(0);
        assertNotNull(password);
        assertEquals(Password.TYPE_PASSWORD_TEXT, password.getType());

        List<XMLObject> children= usernameToken.getOrderedChildren();
        assertEquals(3, children.size());

        marshallAndUnmarshall(usernameToken);

        // TODO impl unmarshallAndMarshall method
        // UsernameToken refUsernameToken=
        // unmarshallXML("/data/usernametoken.xml");
        // Document refDocument= refUsernameToken.getDOM().getOwnerDocument();
        // refUsernameToken.releaseDOM();
        Document refDocument= parseXMLDocument("/data/org/opensaml/ws/wssecurity/UsernameToken.xml");
        //System.out.println("XXX: " + XMLHelper.nodeToString(refDocument.getDocumentElement()));

        Marshaller marshaller= getMarshaller(usernameToken);
        Element element= marshaller.marshall(usernameToken);
        Document document= element.getOwnerDocument();

        // compare with XMLUnit
        XMLAssert.assertXMLIdentical(new Diff(refDocument, document), true);

        // unmarshall directly from file
        UsernameToken ut= unmarshallXML("/data/org/opensaml/ws/wssecurity/UsernameToken.xml");
        assertEquals("test", ut.getUsername().getValue());
        Password utPassword = (Password) ut.getUnknownXMLObjects(Password.ELEMENT_NAME).get(0);
        assertNotNull(utPassword);
        assertEquals("test", utPassword.getValue());
        Created utCreated = (Created) ut.getUnknownXMLObjects(Created.ELEMENT_NAME).get(0);
        assertNotNull(utCreated);
        DateTime created= utCreated.getDateTime();
        System.out.println(created);

    }

    protected UsernameToken createUsernameToken(String user, String pass)
            throws Exception {
        UsernameToken usernameToken= buildXMLObject(UsernameToken.ELEMENT_NAME);
        Username username= buildXMLObject(Username.ELEMENT_NAME);
        username.setValue(user);
        Password password= buildXMLObject(Password.ELEMENT_NAME);
        password.setValue(pass);
        Created created= buildXMLObject(Created.ELEMENT_NAME);
        DateTime now= new DateTime();
        created.setDateTime(now);

        String id= "UsernameToken-" + System.currentTimeMillis();
        usernameToken.setWSUId(id);
        usernameToken.setUsername(username);
        usernameToken.getUnknownXMLObjects().add(password);
        usernameToken.getUnknownXMLObjects().add(created);

        return usernameToken;

    }

}
