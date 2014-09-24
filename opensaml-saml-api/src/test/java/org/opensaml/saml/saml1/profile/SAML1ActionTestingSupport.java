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

package org.opensaml.saml.saml1.profile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.joda.time.DateTime;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.profile.action.ActionTestingSupport;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml1.core.Assertion;
import org.opensaml.saml.saml1.core.AssertionArtifact;
import org.opensaml.saml.saml1.core.AttributeQuery;
import org.opensaml.saml.saml1.core.AttributeStatement;
import org.opensaml.saml.saml1.core.AuthenticationStatement;
import org.opensaml.saml.saml1.core.NameIdentifier;
import org.opensaml.saml.saml1.core.Request;
import org.opensaml.saml.saml1.core.Response;
import org.opensaml.saml.saml1.core.Subject;

/**
 * Helper methods for creating/testing SAML 1 objects within profile action tests. When methods herein refer to mock
 * objects they are always objects that have been created via Mockito unless otherwise noted.
 */
public class SAML1ActionTestingSupport {

    /** ID used for all generated {@link Response} objects. */
    public final static String REQUEST_ID = "request";

    /** ID used for all generated {@link Response} objects. */
    public final static String RESPONSE_ID = "response";

    /** ID used for all generated {@link Assertion} objects. */
    public final static String ASSERTION_ID = "assertion";

    /**
     * Builds an empty response. The ID of the message is {@link ActionTestingSupport#OUTBOUND_MSG_ID}, the issue
     * instant is 1970-01-01T00:00:00Z and the SAML version is {@link SAMLVersion#VERSION_11}.
     * 
     * @return the constructed response
     */
    @Nonnull public static Response buildResponse() {
        final SAMLObjectBuilder<Response> responseBuilder = (SAMLObjectBuilder<Response>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Response>getBuilderOrThrow(
                        Response.DEFAULT_ELEMENT_NAME);

        final Response response = responseBuilder.buildObject();
        response.setID(ActionTestingSupport.OUTBOUND_MSG_ID);
        response.setIssueInstant(new DateTime(0));
        response.setVersion(SAMLVersion.VERSION_11);

        return response;
    }

    /**
     * Builds an empty assertion. The ID of the message is {@link #ASSERTION_ID}, the issue instant is
     * 1970-01-01T00:00:00Z and the SAML version is {@link SAMLVersion#VERSION_11}.
     * 
     * @return the constructed assertion
     */
    @Nonnull public static Assertion buildAssertion() {
        final SAMLObjectBuilder<Assertion> assertionBuilder = (SAMLObjectBuilder<Assertion>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Assertion>getBuilderOrThrow(
                        Assertion.DEFAULT_ELEMENT_NAME);

        final Assertion assertion = assertionBuilder.buildObject();
        assertion.setID(ASSERTION_ID);
        assertion.setIssueInstant(new DateTime(0));
        assertion.setVersion(SAMLVersion.VERSION_11);

        return assertion;
    }

    /**
     * Builds an authentication statement. The authn instant is set to 1970-01-01T00:00:00Z and the
     * method is set to password.
     * 
     * @return the constructed statement
     */
    @Nonnull public static AuthenticationStatement buildAuthenticationStatement() {
        final SAMLObjectBuilder<AuthenticationStatement> statementBuilder = (SAMLObjectBuilder<AuthenticationStatement>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<AuthenticationStatement>getBuilderOrThrow(
                        AuthenticationStatement.DEFAULT_ELEMENT_NAME);

        final AuthenticationStatement statement = statementBuilder.buildObject();
        statement.setAuthenticationInstant(new DateTime(0));
        statement.setAuthenticationMethod(AuthenticationStatement.PASSWORD_AUTHN_METHOD);

        return statement;
    }

    /**
     * Builds an empty attribute statement.
     * 
     * @return the constructed statement
     */
    @Nonnull public static AttributeStatement buildAttributeStatement() {
        final SAMLObjectBuilder<AttributeStatement> statementBuilder = (SAMLObjectBuilder<AttributeStatement>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<AttributeStatement>getBuilderOrThrow(
                        AttributeStatement.DEFAULT_ELEMENT_NAME);

        final AttributeStatement statement = statementBuilder.buildObject();

        return statement;
    }
    
    /**
     * Builds a {@link Subject}. If a principal name is given a {@link NameIdentifier}, whose value is the given
     * principal name, will be created and added to the {@link Subject}.
     * 
     * @param principalName the principal name to add to the subject
     * 
     * @return the built subject
     */
    @Nonnull public static Subject buildSubject(final @Nullable String principalName) {
        final SAMLObjectBuilder<Subject> subjectBuilder = (SAMLObjectBuilder<Subject>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Subject>getBuilderOrThrow(
                        Subject.DEFAULT_ELEMENT_NAME);
        final Subject subject = subjectBuilder.buildObject();

        if (principalName != null) {
            final SAMLObjectBuilder<NameIdentifier> nameIdBuilder = (SAMLObjectBuilder<NameIdentifier>)
                    XMLObjectProviderRegistrySupport.getBuilderFactory().<NameIdentifier>getBuilderOrThrow(
                            NameIdentifier.DEFAULT_ELEMENT_NAME);
            final NameIdentifier nameId = nameIdBuilder.buildObject();
            nameId.setValue(principalName);
            subject.setNameIdentifier(nameId);
        }

        return subject;
    }

    /**
     * Builds a {@link Request} containing an {@link AttributeQuery}. If a {@link Subject} is given, it will be added to
     * the constructed {@link AttributeQuery}.
     * 
     * @param subject the subject to add to the query
     * 
     * @return the built query
     */
    @Nonnull public static Request buildAttributeQueryRequest(final @Nullable Subject subject) {
        final SAMLObjectBuilder<AttributeQuery> queryBuilder = (SAMLObjectBuilder<AttributeQuery>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<AttributeQuery>getBuilderOrThrow(
                        AttributeQuery.DEFAULT_ELEMENT_NAME);
        final AttributeQuery query = queryBuilder.buildObject();

        if (subject != null) {
            query.setSubject(subject);
        }

        final SAMLObjectBuilder<Request> requestBuilder = (SAMLObjectBuilder<Request>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Request>getBuilderOrThrow(
                        Request.DEFAULT_ELEMENT_NAME);
        final Request request = requestBuilder.buildObject();
        request.setID(REQUEST_ID);
        request.setIssueInstant(new DateTime(0));
        request.setQuery(query);
        request.setVersion(SAMLVersion.VERSION_11);

        return request;
    }
    
    /**
     * Builds a {@link Request} containing {@link AssertionArtifact}s.
     * 
     * @param artifacts the artifacts to add to the request
     * 
     * @return the built request
     */
    @Nonnull public static Request buildArtifactRequest(final @Nullable String... artifacts) {
        final SAMLObjectBuilder<AssertionArtifact> artifactBuilder = (SAMLObjectBuilder<AssertionArtifact>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<AssertionArtifact>getBuilderOrThrow(
                        AssertionArtifact.DEFAULT_ELEMENT_NAME);

        final SAMLObjectBuilder<Request> requestBuilder = (SAMLObjectBuilder<Request>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Request>getBuilderOrThrow(
                        Request.DEFAULT_ELEMENT_NAME);
        final Request request = requestBuilder.buildObject();
        request.setID(REQUEST_ID);
        request.setIssueInstant(new DateTime(0));
        request.setVersion(SAMLVersion.VERSION_11);
        
        if (artifacts != null) {
            for (final String artifact : artifacts) {
                final AssertionArtifact aa = artifactBuilder.buildObject();
                aa.setAssertionArtifact(artifact);
                request.getAssertionArtifacts().add(aa);
            }
        }

        return request;
    }

}