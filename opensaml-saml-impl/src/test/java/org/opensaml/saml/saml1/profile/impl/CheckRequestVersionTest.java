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

package org.opensaml.saml.saml1.profile.impl;

import org.opensaml.core.OpenSAMLInitBaseTestCase;
import org.opensaml.profile.RequestContextBuilder;
import org.opensaml.profile.action.ActionTestingSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml1.core.Request;
import org.opensaml.saml.saml1.profile.SAML1ActionTestingSupport;
import org.opensaml.saml.saml1.profile.impl.CheckRequestVersion;
import org.testng.annotations.Test;

/** {@link CheckRequestVersion} unit test. */
public class CheckRequestVersionTest extends OpenSAMLInitBaseTestCase {

    /** Test that the action accepts SAML 1.0 and 1.1 messages. */
    @Test public void testSaml1Message() throws Exception {
        Request request = SAML1ActionTestingSupport.buildAttributeQueryRequest(null);
        ProfileRequestContext prc = new RequestContextBuilder().setInboundMessage(request).buildProfileRequestContext();

        CheckRequestVersion action = new CheckRequestVersion();
        action.setId("test");
        action.initialize();

        ActionTestingSupport.assertProceedEvent(prc);
    }

    /** Test that the action errors out on SAML 2 messages. */
    @Test public void testSaml2Message() throws Exception {
        Request request = SAML1ActionTestingSupport.buildAttributeQueryRequest(null);
        request.setVersion(SAMLVersion.VERSION_20);

        ProfileRequestContext prc = new RequestContextBuilder().setInboundMessage(request).buildProfileRequestContext();

        CheckRequestVersion action = new CheckRequestVersion();
        action.setId("test");
        action.initialize();

        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MESSAGE_VERSION);
    }
    
}