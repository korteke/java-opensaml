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

package org.opensaml.saml.common.messaging.context;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml1.core.Assertion;
import org.opensaml.saml.saml1.core.AssertionArtifact;
import org.opensaml.saml.saml1.core.AttributeQuery;
import org.opensaml.saml.saml1.core.AuthorizationDecisionQuery;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameIDType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Subcontext that carries information about a SAML peer entity.  This context will often
 * contain subcontexts, whose data is construed to be scoped to that peer entity.
 * 
 * <p>
 * The method {@link #getEntityId()} will attempt to dynamically resolve the appropriate data 
 * from the SAML message held in the message context if the data has not been set statically 
 * by the corresponding setter method. This evaluation will be attempted only if the this 
 * context instance is an immediate child of the message context, as returned by {@link #getParent()}.
 * </p>
 */
public class SamlPeerEntityContext extends AbstractAuthenticatableSamlEntityContext {
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(SamlPeerEntityContext.class);
    
    /** Whether to use the resource of SAML 1 queries to resolve the entity ID. */
    private boolean useSaml1QueryResourceAsEntityId;
    
    
    /** Constructor. */
    public SamlPeerEntityContext() {
        super();
        useSaml1QueryResourceAsEntityId = true;
    }

    /** {@inheritDoc} */
    @Nullable public String getEntityId() {
        if (super.getEntityId() == null) {
            setEntityId(resolveEntityId());
        }
        return super.getEntityId();
    }
    
    /**
     * Gets whether to use the Resource attribute of some SAML 1 queries to resolve the entity 
     * ID.
     * 
     * @return whether to use the Resource attribute of some SAML 1 queries to resolve the entity ID 
     */
    public boolean getUseSaml1QueryResourceAsEntityId() {
        return useSaml1QueryResourceAsEntityId;
    }

    /**
     * Sets whether to use the Resource attribute of some SAML 1 queries to resolve the entity ID.
     * 
     * @param useResource whether to use the Resource attribute of some SAML 1 queries to resolve the entity ID
     */
    public void setUseSaml1QueryResourceAsEntityId(boolean useResource) {
        useSaml1QueryResourceAsEntityId = useResource;
    }

    /**
     * Dynamically resolve the SAML peer entity ID from the SAML protocol message held in 
     * {@link MessageContext#getMessage()}.
     * 
     * @return the entity ID, or null if it could not be resolved
     */
    @Nullable protected String resolveEntityId() {
        SAMLObject samlMessage = resolveSAMLMessage();
        //SAML 2 Request
        if (samlMessage instanceof org.opensaml.saml.saml2.core.RequestAbstractType) {
            org.opensaml.saml.saml2.core.RequestAbstractType request =  
                    (org.opensaml.saml.saml2.core.RequestAbstractType) samlMessage;
            return processSaml2Request(request);
        //SAML 2 Response
        } else if (samlMessage instanceof org.opensaml.saml.saml2.core.StatusResponseType) {
            org.opensaml.saml.saml2.core.StatusResponseType response = 
                    (org.opensaml.saml.saml2.core.StatusResponseType) samlMessage;
            return processSaml2Response(response);
        //SAML 1 Response
        } else if (samlMessage instanceof org.opensaml.saml.saml1.core.Response) {
            org.opensaml.saml.saml1.core.Response response = 
                    (org.opensaml.saml.saml1.core.Response) samlMessage;
            return processSaml1Response(response);
        //SAML 1 Request
        } else if (samlMessage instanceof org.opensaml.saml.saml1.core.Request) {
            org.opensaml.saml.saml1.core.Request request = 
                    (org.opensaml.saml.saml1.core.Request) samlMessage;
            return processSaml1Request(request);
        }
        
        return null;
    }
    
    /**
     * Resolve the SAML entity ID from a SAML 2 request.
     * 
     * @param request the request
     * 
     * @return the entity ID, or null if it could not be resolved
     */
    @Nullable protected String processSaml2Request(
            @Nonnull final org.opensaml.saml.saml2.core.RequestAbstractType request) {
        if (request.getIssuer() != null) {
            return processSaml2Issuer(request.getIssuer());
        }
        return null;
    }

    /**
     * Resolve the SAML entity ID from a SAML 2 response.
     * 
     * @param response the response
     * 
     * @return the entity ID, or null if it could not be resolved
     */
    @Nullable protected String processSaml2Response(
            @Nonnull final org.opensaml.saml.saml2.core.StatusResponseType response) {
        if (response.getIssuer() != null) {
            return processSaml2Issuer(response.getIssuer());
        }
        return null;
    }
    
    /**
     * Resolve the SAML entity ID from a SAML 2 Issuer.
     * 
     * @param issuer the issuer
     * 
     * @return the entity ID, or null if it could not be resolved
     */
    @Nullable protected String processSaml2Issuer(@Nonnull final Issuer issuer) {
        if (issuer.getFormat() == null || issuer.getFormat().equals(NameIDType.ENTITY)) {
            return issuer.getValue();
        } else { 
            log.warn("Couldn't dynamically resolve SAML 2 peer entity ID due to unsupported NameID format: {}", 
                    issuer.getFormat());
            return null;
        }
    }

    /**
     * Resolve the SAML entity ID from a SAML 1 response.
     * 
     * @param response the response
     * 
     * @return the entity ID, or null if it could not be resolved
     */
    @Nullable protected String processSaml1Response(@Nonnull final org.opensaml.saml.saml1.core.Response response) {
        String issuer = null;
        List<Assertion> assertions = response.getAssertions();
        if (assertions != null && assertions.size() > 0) {
            log.info("Attempting to extract issuer from enclosed SAML 1.x Assertion(s)");
            for (Assertion assertion : assertions) {
                if (assertion != null && assertion.getIssuer() != null) {
                    if (issuer != null && !issuer.equals(assertion.getIssuer())) {
                        log.warn("SAML 1.x assertions, within response '{}' contain different issuer IDs, " 
                                + "can not dynamically resolve SAML peer entity ID", response.getID());
                        return null;
                    }
                    issuer = assertion.getIssuer();
                }
            }
        }

        if (issuer == null) {
            log.warn("Issuer could not be extracted from standard SAML 1.x response message");
        }

        return issuer;
    }

    /**
     * Resolve the SAML entity ID from a SAML 1 request.
     * 
     * @param request the request
     * 
     * @return the entity ID, or null if it could not be resolved
     */
    @Nullable protected String processSaml1Request(@Nonnull final org.opensaml.saml.saml1.core.Request request) {
        String entityId = null;
        if (request.getAttributeQuery() != null) {
            entityId = processSaml1AttributeQuery(request.getAttributeQuery());
            if (entityId != null) {
                return entityId;
            }
        }

        if (request.getAuthorizationDecisionQuery() != null) {
            entityId = processSaml1AuthorizationDecisionQuery(request.getAuthorizationDecisionQuery());
            if (entityId != null) {
                return entityId;
            }
        }

        if (request.getAssertionArtifacts() != null) {
            entityId = processSaml1AssertionArtifacts(request.getAssertionArtifacts());
            if (entityId != null) {
                return entityId;
            }
        }
        
        return null;
    }

    /**
     * Resolve the SAML entity ID from a SAML 1 AttributeQuery.
     * 
     * @param query the query
     * 
     * @return the entity ID, or null if it could not be resolved
     */
    @Nullable protected String processSaml1AttributeQuery(@Nonnull final AttributeQuery query) {
        if (getUseSaml1QueryResourceAsEntityId()) {
            log.debug("Attempting to extract entity ID from SAML 1 AttributeQuery Resource attribute");
            String resource = StringSupport.trimOrNull(query.getResource());

            if (resource != null) {
                log.debug("Extracted entity ID from SAML 1.x AttributeQuery: {}", resource);
                return resource;
            }
        }
        return null;
    }

    /**
     * Resolve the SAML entityID from a SAML 1 AuthorizationDecisionQuery.
     * 
     * @param query the query
     * 
     * @return the entity ID, or null if it could not be resolved
     */
    @Nullable protected String processSaml1AuthorizationDecisionQuery(@Nonnull final AuthorizationDecisionQuery query) {
        if (getUseSaml1QueryResourceAsEntityId()) {
            log.debug("Attempting to extract entity ID from SAML 1 AuthorizationDecisionQuery Resource attribute");
            String resource = StringSupport.trimOrNull(query.getResource());

            if (resource != null) {
                log.debug("Extracted entity ID from SAML 1.x AuthorizationDecisionQuery: {}", resource);
                return resource;
            }
        }
        return null;
    }
    
    /**
     * Resolve the SAML entity ID from a SAML 1 AssertionArtifact list.
     * 
     * @param artifacts the artifact list
     * 
     * @return the entity ID, or null if it could not be resolved
     */
    @Nullable protected String processSaml1AssertionArtifacts(@Nonnull final List<AssertionArtifact> artifacts) {
        if (artifacts.size() == 0) {
            return null;
        }
        
        //TODO can we support this?  Would need the artifact map.
        /*
        log.debug("Attempting to extract issuer based on first AssertionArtifact in request");
        AssertionArtifact artifact = artifacts.get(0);
        SAMLArtifactMapEntry artifactEntry = artifactMap.get(artifact.getAssertionArtifact());
        String issuer = artifactEntry.getRelyingPartyId();
        log.debug("Extracted issuer from SAML 1.x AssertionArtifact: {}", issuer);
        return issuer;
        */
        
        log.info("Dynamic resolution of SAML peer entity ID from SAML 1 AssertionArtifacts is not currently supported");
        return null;
    }

    /**
     * Resolve the SAML message from the message context.
     * 
     * @return the SAML message, or null if it can not be resolved
     */
    @Nullable protected SAMLObject resolveSAMLMessage() {
        if (getParent() instanceof MessageContext) {
            MessageContext parent = (MessageContext) getParent();
            if (parent.getMessage() instanceof SAMLObject) {
                return (SAMLObject) parent.getMessage();
            } 
        }
        return null;
    }

}