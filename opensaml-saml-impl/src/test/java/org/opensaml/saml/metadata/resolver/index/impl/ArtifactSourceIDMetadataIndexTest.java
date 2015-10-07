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

package org.opensaml.saml.metadata.resolver.index.impl;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.opensaml.core.OpenSAMLInitBaseTestCase;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.criterion.ArtifactSourceIDCriterion;
import org.opensaml.saml.metadata.resolver.index.MetadataIndexKey;
import org.opensaml.saml.metadata.resolver.index.impl.ArtifactSourceIDMetadataIndex.ArtifactSourceIDMetadataIndexKey;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.security.crypto.JCAConstants;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 *
 */
public class ArtifactSourceIDMetadataIndexTest extends OpenSAMLInitBaseTestCase {
    
    private ArtifactSourceIDMetadataIndex metadataIndex;
    
    private String entityID = "https://www.example.com/sp";
    private byte[] sourceID;
            
    private EntityDescriptor descriptor;
    
    private MetadataIndexKey key;
    
    @BeforeClass
    protected void setUp() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        metadataIndex = new ArtifactSourceIDMetadataIndex();
        
        descriptor = (EntityDescriptor) XMLObjectSupport.buildXMLObject(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        descriptor.setEntityID(entityID);
        
        MessageDigest sha1Digester = MessageDigest.getInstance(JCAConstants.DIGEST_SHA1);
        sourceID = sha1Digester.digest(entityID.getBytes("UTF-8"));
        
        key = new ArtifactSourceIDMetadataIndex.ArtifactSourceIDMetadataIndexKey(sourceID);
    }
    
    @Test
    public void testGenerateKeysFromDescriptor() {
        Set<MetadataIndexKey> keys = metadataIndex.generateKeys(descriptor);
        
        Assert.assertEquals(keys.size(), 1);
        Assert.assertTrue(keys.contains(key));
        Assert.assertEquals(((ArtifactSourceIDMetadataIndexKey)keys.iterator().next()).getSourceID(), sourceID);
    }
    
    @Test
    public void testGenerateKeysFromCriteria() {
        CriteriaSet criteriaSet = new CriteriaSet();
        
        criteriaSet.add(new ArtifactSourceIDCriterion(sourceID));
        
        Set<MetadataIndexKey> keys = metadataIndex.generateKeys(criteriaSet);
        
        Assert.assertEquals(keys.size(), 1);
        Assert.assertTrue(keys.contains(key));
        Assert.assertEquals(((ArtifactSourceIDMetadataIndexKey)keys.iterator().next()).getSourceID(), sourceID);
    }
    
    @Test
    public void testKey() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest sha1Digester = MessageDigest.getInstance(JCAConstants.DIGEST_SHA1);
        
        MetadataIndexKey keySame = new ArtifactSourceIDMetadataIndex.ArtifactSourceIDMetadataIndexKey(
                sha1Digester.digest(entityID.getBytes("UTF-8")));
                
        sha1Digester.reset();
        MetadataIndexKey keyDifferent = new ArtifactSourceIDMetadataIndex.ArtifactSourceIDMetadataIndexKey(
                sha1Digester.digest("foobar".getBytes("UTF-8")));
        
        Assert.assertEquals(key, keySame);
        Assert.assertTrue(key.hashCode() == keySame.hashCode());
        
        Assert.assertNotEquals(key, keyDifferent);
    }

}
