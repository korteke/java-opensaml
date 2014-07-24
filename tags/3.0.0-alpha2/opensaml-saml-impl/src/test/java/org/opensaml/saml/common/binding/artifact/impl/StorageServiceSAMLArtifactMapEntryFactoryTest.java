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

package org.opensaml.saml.common.binding.artifact.impl;

import java.io.IOException;

import net.shibboleth.utilities.java.support.xml.XMLAssertTestNG;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.w3c.dom.Document;
import org.custommonkey.xmlunit.Diff;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.artifact.BasicSAMLArtifactMapEntry;
import org.opensaml.saml.common.binding.artifact.SAMLArtifactMap.SAMLArtifactMapEntry;
import org.opensaml.saml.common.binding.artifact.impl.StorageServiceSAMLArtifactMap;
import org.opensaml.saml.common.binding.artifact.impl.StorageServiceSAMLArtifactMapEntryFactory;
import org.opensaml.saml.saml1.core.Assertion;
import org.opensaml.saml.saml1.core.Response;

/**
 * Test the storage-backed SAML artifact map entry factory.
 */
public class StorageServiceSAMLArtifactMapEntryFactoryTest extends XMLObjectBaseTestCase {

    private String artifact = "the-artifact";
    private String issuerId = "urn:test:issuer";
    private String rpId = "urn:test:rp";
    
    private StorageServiceSAMLArtifactMapEntryFactory factory;
    private SAMLObject samlObject;
    
    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        factory = new StorageServiceSAMLArtifactMapEntryFactory();
        
        samlObject = (SAMLObject) unmarshallElement("/data/org/opensaml/saml/saml1/core/SignedAssertion.xml");
    }

    @Test
    public void testNoParent() {
        SAMLArtifactMapEntry entry = factory.newEntry(artifact, issuerId, rpId, samlObject);
        Assert.assertTrue(samlObject == entry.getSamlMessage(),
                "Parent-less SAMLObject resulted in different object in entry");
    }
    
    @Test
    public void testWithParent() {
        Response response = (Response) buildXMLObject(Response.DEFAULT_ELEMENT_NAME);
        response.getAssertions().add((Assertion)samlObject);
        Assert.assertTrue(samlObject.hasParent());
        
        SAMLArtifactMapEntry entry = factory.newEntry(artifact, issuerId, rpId, samlObject);
        Assert.assertFalse(samlObject == entry.getSamlMessage(),
                "Parent-ed SAMLObject resulted in the same object in entry");
    }
    
    @Test
    public void testWithSerialization() throws IOException, MarshallingException {
        SAMLArtifactMapEntry entry = factory.newEntry(artifact, issuerId, rpId, samlObject);
        BasicSAMLArtifactMapEntry basicEntry = (BasicSAMLArtifactMapEntry) entry;
        
        String s = factory.serialize(basicEntry);
        BasicSAMLArtifactMapEntry newEntry = (BasicSAMLArtifactMapEntry) factory.deserialize(
                1, StorageServiceSAMLArtifactMap.STORAGE_CONTEXT, basicEntry.getArtifact(), s, null);
        
        Assert.assertEquals(basicEntry.getArtifact(), newEntry.getArtifact());
        Assert.assertEquals(basicEntry.getIssuerId(), newEntry.getIssuerId());
        Assert.assertEquals(basicEntry.getRelyingPartyId(), newEntry.getRelyingPartyId());

        Document origDocument = samlObject.getDOM().getOwnerDocument();
        origDocument.appendChild(samlObject.getDOM());
        Document newDocument = newEntry.getSamlMessage().getDOM().getOwnerDocument();
        XMLAssertTestNG.assertXMLIdentical(new Diff(origDocument, newDocument), true);
    }

}