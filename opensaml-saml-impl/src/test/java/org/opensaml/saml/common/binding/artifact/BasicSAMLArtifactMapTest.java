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

package org.opensaml.saml.common.binding.artifact;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import java.util.HashMap;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLAssert;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.artifact.BasicSAMLArtifactMap;
import org.opensaml.saml.common.binding.artifact.SAMLArtifactMap.SAMLArtifactMapEntry;
import org.w3c.dom.Document;

/**
 *
 */
public class BasicSAMLArtifactMapTest extends XMLObjectBaseTestCase {

    private BasicSAMLArtifactMap artifactMap;

    private HashMap<String, SAMLArtifactMapEntry> storageService;

    private SAMLObject samlObject;

    private String artifact = "the-artifact";

    private String issuerId = "urn:test:issuer";

    private String rpId = "urn:test:rp";

    private long lifetime = 60 * 60 * 1000;

    private Document origDocument;

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        samlObject = (SAMLObject) unmarshallElement("/data/org/opensaml/saml/saml2/core/ResponseSuccessAuthnAttrib.xml");
        origDocument = samlObject.getDOM().getOwnerDocument();
        // Drop the DOM for a more realistic test, usuallly the artifact SAMLObject will be built, not unmarshalled
        samlObject.releaseChildrenDOM(true);
        samlObject.releaseDOM();

        storageService = new HashMap<String, SAMLArtifactMapEntry>();

        artifactMap = new BasicSAMLArtifactMap(storageService, lifetime);
    }

    @Test
    public void testBasicPutGet() throws MarshallingException {
        Assert.assertFalse(artifactMap.contains(artifact));

        artifactMap.put(artifact, rpId, issuerId, samlObject);

        Assert.assertTrue(artifactMap.contains(artifact));

        SAMLArtifactMapEntry entry = artifactMap.get(artifact);
        Assert.assertNotNull(entry);

        Assert.assertEquals(entry.getArtifact(), artifact, "Invalid value for artifact");
        Assert.assertEquals(entry.getIssuerId(), issuerId, "Invalid value for issuer ID");
        Assert.assertEquals(entry.getRelyingPartyId(), rpId, "Invalid value for relying party ID");

        // Test SAMLObject reconstitution
        SAMLObject retrievedObject = entry.getSamlMessage();
        Document newDocument =
                marshallerFactory.getMarshaller(retrievedObject).marshall(retrievedObject).getOwnerDocument();
        XMLAssert.assertXMLIdentical(new Diff(origDocument, newDocument), true);
    }

    @Test
    public void testRemove() throws MarshallingException {
        Assert.assertFalse(artifactMap.contains(artifact));

        artifactMap.put(artifact, rpId, issuerId, samlObject);

        Assert.assertTrue(artifactMap.contains(artifact));

        artifactMap.remove(artifact);

        Assert.assertFalse(artifactMap.contains(artifact));

        SAMLArtifactMapEntry entry = artifactMap.get(artifact);
        Assert.assertNull(entry, "Entry was removed");
    }

    @Test
    public void testEntryExpiration() throws MarshallingException, InterruptedException {
        // lifetime of 1 second should do it
        artifactMap = new BasicSAMLArtifactMap(storageService, 1000);

        Assert.assertFalse(artifactMap.contains(artifact));

        artifactMap.put(artifact, rpId, issuerId, samlObject);

        // Hopefully this doesn't get deferred for more than 1000 milliseconds after the put()...
        Assert.assertTrue(artifactMap.contains(artifact));

        // Sleep for 3 seconds, entry should expire
        Thread.sleep(3000);

        SAMLArtifactMapEntry entry = artifactMap.get(artifact);
        Assert.assertNull(entry, "Entry should have expired");
    }

}
