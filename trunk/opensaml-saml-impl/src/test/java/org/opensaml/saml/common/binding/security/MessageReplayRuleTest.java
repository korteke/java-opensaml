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

package org.opensaml.saml.common.binding.security;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;

import org.opensaml.saml.common.binding.security.MessageReplayRule;
import org.opensaml.saml.saml2.core.AttributeQuery;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.util.storage.ReplayCache;
import org.opensaml.util.storage.StorageService;
import org.opensaml.util.storage.impl.MemoryStorageService;

/**
 * Testing SAML message replay security policy rule.
 */
public class MessageReplayRuleTest extends BaseSAMLSecurityPolicyRuleTestCase<AttributeQuery, Response, NameID> {

    private String messageID;

    private StorageService storageService;

    private ReplayCache replayCache;

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        messageID = "abc123";

        messageContext.setInboundMessageIssuer("issuer");
        messageContext.setInboundSAMLMessageId(messageID);

        storageService = new MemoryStorageService();
        storageService.initialize();
        
        replayCache = new ReplayCache();
        replayCache.setStorage(storageService);
        replayCache.initialize();
        
        rule = new MessageReplayRule(replayCache);
    }

    @AfterMethod
    protected void tearDown() {
        rule = null;
        
        replayCache.destroy();
        replayCache = null;
        
        storageService.destroy();
        storageService = null;
    }
    
    /**
     * Test valid message ID.
     */
    @Test
    public void testNoReplay() {
        assertRuleSuccess("Message ID was valid");
    }

    /**
     * Test valid message ID, distinct ID.
     */
    @Test
    public void testNoReplayDistinctIDs() {
        assertRuleSuccess("Message ID was valid");

        messageContext.setInboundSAMLMessageId("someOther" + messageID);
        assertRuleSuccess("Message ID was valid, distinct message ID");

    }

    /**
     * Test invalid replay of message ID.
     */
    @Test
    public void testReplay() {
        assertRuleSuccess("Message ID was valid");

        assertRuleFailure("Message ID was a replay");
    }

    /**
     * Test valid replay of message ID due to replay cache expiration.
     * 
     * @throws InterruptedException
     */
    @Test
    public void testReplayValidWithExpiration() throws InterruptedException {
        // Set rule with 3 second expiration, with no clock skew
        ((MessageReplayRule) rule).setExpires(3);
        assertRuleSuccess("Message ID was valid");

        // Now sleep for 4 seconds to be sure has expired, and retry same message id
        Thread.sleep(4000L);
        assertRuleSuccess("Message ID was valid, no replay due to expiration");
    }

}
