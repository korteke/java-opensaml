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

/**
 * 
 */

package org.opensaml.saml.saml1.core.impl;

import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.AssertJUnit;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml1.core.Action;
import org.opensaml.saml.saml1.core.AuthorizationDecisionStatement;
import org.opensaml.saml.saml1.core.DecisionTypeEnumeration;
import org.opensaml.saml.saml1.core.Evidence;
import org.opensaml.saml.saml1.core.Subject;
import org.opensaml.saml.saml1.core.impl.AuthorizationDecisionStatementUnmarshaller;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 */
public class AuthorizationDecisionStatementTest extends XMLObjectProviderBaseTestCase {

    /** name used to generate objects */
    private final QName qname;

    /** Value for Resource attribute specified in test file with attributes */
    private final String expectedResource = "resource";

    /** Value for Resource attribute specified in test file with attributes */
    private final DecisionTypeEnumeration expectedDecision = DecisionTypeEnumeration.PERMIT;

    /** File with the AuthorizationDecisionStatement with illegal Decision type */
    private String illegalAttributesFile;

    /**
     * Constructor
     */
    public AuthorizationDecisionStatementTest() {
        super();
        singleElementFile = "/data/org/opensaml/saml/saml1/impl/singleAuthorizationDecisionStatement.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml/saml1/impl/singleAuthorizationDecisionStatementAttributes.xml";
        childElementsFile = "/data/org/opensaml/saml/saml1/impl/AuthorizationDecisionStatementWithChildren.xml";
        illegalAttributesFile = "/data/org/opensaml/saml/saml1/impl/singleAuthorizationDecisionStatementAttributesInvalid.xml";
        
        qname = new QName(SAMLConstants.SAML1_NS, AuthorizationDecisionStatement.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementUnmarshall() {

        AuthorizationDecisionStatement authorizationDecisionStatement;
        authorizationDecisionStatement = (AuthorizationDecisionStatement) unmarshallElement(singleElementFile);

        AssertJUnit.assertNull("Decision attribute null", authorizationDecisionStatement.getDecision());
        AssertJUnit.assertNull("Resource attribute null", authorizationDecisionStatement.getResource());
        AssertJUnit.assertEquals("<Actions> elements present", 0, authorizationDecisionStatement.getActions().size());
        AssertJUnit.assertNull("<Evidence> element present", authorizationDecisionStatement.getEvidence());
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {

        AuthorizationDecisionStatement authorizationDecisionStatement;
        authorizationDecisionStatement = (AuthorizationDecisionStatement) unmarshallElement(singleElementOptionalAttributesFile);

        AssertJUnit.assertEquals("Resource attribute ", expectedResource, authorizationDecisionStatement.getResource());
        AssertJUnit.assertEquals("Decision attribute ", expectedDecision.toString(), authorizationDecisionStatement.getDecision().toString());

        try {
            Document doc = parserPool.parse(this.getClass().getResourceAsStream(illegalAttributesFile));
            Element samlElement = doc.getDocumentElement();

            authorizationDecisionStatement = (AuthorizationDecisionStatement) new AuthorizationDecisionStatementUnmarshaller()
                    .unmarshall(samlElement);

            Assert.fail("illegal attribute successfully parsed");
        } catch (UnmarshallingException e) {
            ;
        } catch (XMLParserException e) {
            Assert.fail("couldn't parse file" + e);
            e.printStackTrace();
        }
    }

    /** {@inheritDoc} */

    @Test
    public void testChildElementsUnmarshall() {
        AuthorizationDecisionStatement authorizationDecisionStatement;

        authorizationDecisionStatement = (AuthorizationDecisionStatement) unmarshallElement(childElementsFile);

        AssertJUnit.assertNotNull("<Subject> element not present", authorizationDecisionStatement.getSubject());
        AssertJUnit.assertNotNull("<Evidence> element not present", authorizationDecisionStatement.getEvidence());
        AssertJUnit.assertNotNull("<Action> elements not present", authorizationDecisionStatement.getActions());
        AssertJUnit.assertEquals("Count of <Action> elements ", 3, authorizationDecisionStatement.getActions().size());
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementMarshall() {
        assertXMLEquals(expectedDOM, buildXMLObject(qname));
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        AuthorizationDecisionStatement authorizationDecisionStatement;

        authorizationDecisionStatement = (AuthorizationDecisionStatement) buildXMLObject(qname);
        authorizationDecisionStatement.setDecision(expectedDecision);
        authorizationDecisionStatement.setResource(expectedResource);

        assertXMLEquals(expectedOptionalAttributesDOM, authorizationDecisionStatement);
    }

    /** {@inheritDoc} */

    @Test
    public void testChildElementsMarshall() {
        AuthorizationDecisionStatement authorizationDecisionStatement;

        authorizationDecisionStatement = (AuthorizationDecisionStatement) buildXMLObject(qname);

        QName oqname = new QName(SAMLConstants.SAML1_NS, Subject.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
        authorizationDecisionStatement.setSubject((Subject) buildXMLObject(oqname));
        oqname = new QName(SAMLConstants.SAML1_NS, Action.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
        authorizationDecisionStatement.getActions().add((Action) buildXMLObject(oqname));
        authorizationDecisionStatement.getActions().add((Action) buildXMLObject(oqname));
        authorizationDecisionStatement.getActions().add((Action) buildXMLObject(oqname));
        oqname = new QName(SAMLConstants.SAML1_NS, Evidence.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
        authorizationDecisionStatement.setEvidence((Evidence) buildXMLObject(oqname));

        authorizationDecisionStatement.setEvidence((Evidence) buildXMLObject(oqname));

        assertXMLEquals(expectedChildElementsDOM, authorizationDecisionStatement);
    }

}
