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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.opensaml.ws.WSBaseTestCase;
import org.opensaml.ws.wssecurity.BinarySecurityToken;
import org.opensaml.ws.wssecurity.Created;
import org.opensaml.ws.wssecurity.AttributedDateTime;
import org.opensaml.ws.wssecurity.Embedded;
import org.opensaml.ws.wssecurity.Expires;
import org.opensaml.ws.wssecurity.Iteration;
import org.opensaml.ws.wssecurity.Nonce;
import org.opensaml.ws.wssecurity.Password;
import org.opensaml.ws.wssecurity.Reference;
import org.opensaml.ws.wssecurity.Salt;
import org.opensaml.ws.wssecurity.Timestamp;
import org.opensaml.ws.wssecurity.Username;
import org.opensaml.ws.wssecurity.UsernameToken;
import org.opensaml.xml.XMLConfigurator;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.util.XMLHelper;
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

    public void testTimestamp() throws Exception {
        Timestamp timestamp= buildXMLObject(Timestamp.ELEMENT_NAME);
        Created created= buildXMLObject(Created.ELEMENT_NAME);
        DateTime now= new DateTime();
        created.setDateTime(now);
        timestamp.setCreated(created);

        Expires expires= buildXMLObject(Expires.ELEMENT_NAME);
        expires.setDateTime(now.plusMinutes(10));
        timestamp.setExpires(expires);

        timestamp.setId("Timestamp-" + System.currentTimeMillis());

        marshallAndUnmarshall(timestamp);
    }

    public void testUsername() throws Exception {
        Username username= buildXMLObject(Username.ELEMENT_NAME);
        username.setValue("test");
        marshallAndUnmarshall(username);
    }

    public void testNonce() throws Exception {
        Nonce nonce= buildXMLObject(Nonce.ELEMENT_NAME);
        nonce.setValue("Base64EncodedValue...");
        marshallAndUnmarshall(nonce);
    }

    public void testSalt() throws Exception {
        Salt salt= buildXMLObject(Salt.ELEMENT_NAME);
        salt.setValue("Base64Encoded_Salt_VALUE...");
        marshallAndUnmarshall(salt);
    }

    public void testIteration() throws Exception {
        Iteration iteration= buildXMLObject(Iteration.ELEMENT_NAME);
        iteration.setValue(new Integer(1000));
        marshallAndUnmarshall(iteration);
    }

    public void testPassword() throws Exception {

        Password password= buildXMLObject(Password.ELEMENT_NAME);
        password.setValue("test");
        // check default
        assertEquals(Password.TYPE_PASSWORD_TEXT, password.getType());
        marshallAndUnmarshall(password);
    }

    public void testUsernameToken() throws Exception {
        String refId= "UsernameToken-007";
        String refDateTimeStr= "2007-12-19T09:53:08.335Z";

        UsernameToken usernameToken= createUsernameToken("test", "test");
        usernameToken.setId(refId);
        DateTimeFormatter formatter= DateTimeFormat.forPattern(AttributedDateTime.DEFAULT_DATETIME_FORMAT);
        DateTime refDateTime= formatter.parseDateTime(refDateTimeStr);
        usernameToken.getCreated().setDateTime(refDateTime);

        // check default password type
        Password password= usernameToken.getPassword();
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
        System.out.println("XXX: "
                + XMLHelper.nodeToString(refDocument.getDocumentElement()));

        Marshaller marshaller= getMarshaller(usernameToken);
        Element element= marshaller.marshall(usernameToken);
        Document document= element.getOwnerDocument();

        // compare with XMLUnit
        assertXMLEqual(refDocument, document);

        // unmarshall directly from file
        UsernameToken ut= unmarshallXML("/data/org/opensaml/ws/wssecurity/UsernameToken.xml");
        assertEquals("test", ut.getUsername().getValue());
        assertEquals("test", ut.getPassword().getValue());
        DateTime created= ut.getCreated().getDateTime();
        System.out.println(created);

    }

    public void testBinarySecurityToken() throws Exception {
        BinarySecurityToken token= buildXMLObject(BinarySecurityToken.ELEMENT_NAME);
        token.setId("BinarySecurityToken-" + System.currentTimeMillis());
        token.setValue("Base64Encoded_X509_CERTIFICATE...");
        token.setValueType("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3");
        // check default encoding type
        assertEquals(BinarySecurityToken.ENCODING_TYPE_BASE64_BINARY, token.getEncodingType());

        marshallAndUnmarshall(token);

    }

    public void testReference() throws Exception {
        Reference reference= buildXMLObject(Reference.ELEMENT_NAME);

        reference.setValueType("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#UsernameToken");
        reference.setURI("#UsernameToken-0000001");

        marshallAndUnmarshall(reference);
    }

    public void testEmbedded() throws Exception {
        Embedded embedded= buildXMLObject(Embedded.ELEMENT_NAME);

        UsernameToken usernameToken= createUsernameToken("EmbeddedUT",
                                                         "EmbeddedUT");

        embedded.setValueType("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#UsernameToken");
        embedded.getUnknownXMLObjects().add(usernameToken);

        marshallAndUnmarshall(embedded);

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
        usernameToken.setId(id);
        usernameToken.setUsername(username);
        usernameToken.setPassword(password);
        usernameToken.setCreated(created);

        return usernameToken;

    }

}
