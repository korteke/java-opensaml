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

package org.opensaml.saml.saml2.metadata;

import java.io.InputStream;

import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

/**
 * Test cases that parses real, "in-the-wild", metadata files. Currently uses the InCommon and SWITCH federation
 * metadata files (current as of the time this test was written).
 */
public class MetadataTest extends XMLObjectBaseTestCase {

    /**
     * Constructor
     */
    public MetadataTest() {

    }

    /**
     * Tests unmarshalling an InCommon metadata document.
     * 
     * @throws XMLParserException
     * @throws UnmarshallingException
     */
    @Test
    public void testInCommonUnmarshall() throws XMLParserException, UnmarshallingException {
        String inCommonMDFile = "/org/opensaml/saml/saml2/metadata/InCommon-metadata.xml";

        try {
            InputStream in = MetadataTest.class.getResourceAsStream(inCommonMDFile);
            Document inCommonMDDoc = parserPool.parse(in);
            Unmarshaller unmarshaller = XMLObjectProviderRegistrySupport.getUnmarshallerFactory().getUnmarshaller(
                    inCommonMDDoc.getDocumentElement());

            XMLObject inCommonMD = unmarshaller.unmarshall(inCommonMDDoc.getDocumentElement());

            Assert.assertEquals(inCommonMD.getElementQName().getLocalPart(), "EntitiesDescriptor",
                    "First element of InCommon data was not expected EntitiesDescriptor");
        } catch (XMLParserException xe) {
            Assert.fail("Unable to parse XML file: " + xe);
        } catch (UnmarshallingException ue) {
            Assert.fail("Unable to unmarshall XML: " + ue);
        }
    }

    /**
     * Tests unmarshalling an SWITCH metadata document.
     * 
     * @throws XMLParserException
     * @throws UnmarshallingException
     */
    @Test
    public void testSWITCHUnmarshall() {
        String switchMDFile = "/org/opensaml/saml/saml2/metadata/metadata.switchaai_signed.xml";

        try {
            InputStream in = MetadataTest.class.getResourceAsStream(switchMDFile);
            Document switchMDDoc = parserPool.parse(in);
            Unmarshaller unmarshaller = XMLObjectProviderRegistrySupport.getUnmarshallerFactory().getUnmarshaller(
                    switchMDDoc.getDocumentElement());

            XMLObject switchMD = unmarshaller.unmarshall(switchMDDoc.getDocumentElement());

            Assert.assertEquals(switchMD.getElementQName().getLocalPart(), "EntitiesDescriptor",
                    "First element of SWITCH data was not expected EntitiesDescriptor");
        } catch (XMLParserException xe) {
            Assert.fail("Unable to parse XML file: " + xe);
        } catch (UnmarshallingException ue) {
            Assert.fail("Unable to unmarshall XML: " + ue);
        }
    }
    
    /**
     * Tests unmarshalling an SWITCH metadata document.
     * 
     * @throws XMLParserException
     * @throws UnmarshallingException
     */
    @Test
    public void testUKFedUnmarshall() {
        String switchMDFile = "/org/opensaml/saml/saml2/metadata/ukfederation-metadata.xml";

        try {
            InputStream in = MetadataTest.class.getResourceAsStream(switchMDFile);
            Document ukFedDoc = parserPool.parse(in);            
            Unmarshaller unmarshaller = XMLObjectProviderRegistrySupport.getUnmarshallerFactory().getUnmarshaller(
                    ukFedDoc.getDocumentElement());
            XMLObject ukFedMD = unmarshaller.unmarshall(ukFedDoc.getDocumentElement());

            Assert.assertEquals(ukFedMD.getElementQName().getLocalPart(), "EntitiesDescriptor",
                    "First element of UK Federation data was not expected EntitiesDescriptor");
        } catch (XMLParserException xe) {
            Assert.fail("Unable to parse XML file: " + xe);
        } catch (UnmarshallingException ue) {
            Assert.fail("Unable to unmarshall XML: " + ue);
        }
    }
}