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

package org.opensaml.saml.common.profile;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;

/**
 * SAML-specific constants to use for {@link org.opensaml.profile.action.ProfileAction}
 * {@link org.opensaml.profile.context.EventContext}s.
 */
public final class SAMLEventIds {

    /** ID of event returned upon detection of the {@link org.opensaml.saml.ext.saml2aslo.Asynchronous} extension. */
    @Nonnull @NotEmpty public static final String ASYNC_LOGOUT = "AsyncLogout";

    /** ID of event returned upon failure to verify {@link org.opensaml.saml.ext.saml2cb.ChannelBindings}. */
    @Nonnull @NotEmpty public static final String CHANNEL_BINDINGS_ERROR = "ChannelBindingsError";
    
    /** ID of event returned upon failure to decrypt an {@link org.opensaml.saml.saml2.core.EncryptedAssertion}. */
    @Nonnull @NotEmpty public static final String DECRYPT_ASSERTION_FAILED = "DecryptAssertionFailed";

    /** ID of event returned upon failure to decrypt an {@link org.opensaml.saml.saml2.core.EncryptedAttribute}. */
    @Nonnull @NotEmpty public static final String DECRYPT_ATTRIBUTE_FAILED = "DecryptAttributeFailed";
    
    /** ID of event returned upon failure to decrypt an {@link org.opensaml.saml.saml2.core.EncryptedID}. */
    @Nonnull @NotEmpty public static final String DECRYPT_NAMEID_FAILED = "DecryptNameIDFailed";

    /** ID of event returned upon failure to resolve an outgoing message endpoint to use. */
    @Nonnull @NotEmpty public static final String ENDPOINT_RESOLUTION_FAILED = "EndpointResolutionFailed";

    /** ID of event returned if the requested {@link org.opensaml.saml.saml2.core.NameIDPolicy} can't be satisfied. */
    @Nonnull @NotEmpty public static final String INVALID_NAMEID_POLICY = "InvalidNameIDPolicy";
    
    /** ID of event returned if a SAML artifact cannot be resolved. */
    @Nonnull @NotEmpty public static final String UNABLE_RESOLVE_ARTIFACT = "UnableToResolveArtifact";
    
    /** Constructor. */
    private SAMLEventIds() {
        
    }
    
}