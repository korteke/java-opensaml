/*
 * Copyright 2008 Members of the EGEE Collaboration.
 * Copyright 2008 University Corporation for Advanced Internet Development, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opensaml.ws.wstrust.impl;


import org.opensaml.ws.wspolicy.AppliesTo;
import org.opensaml.ws.wspolicy.Policy;
import org.opensaml.ws.wspolicy.PolicyReference;
import org.opensaml.ws.wssecurity.Timestamp;
import org.opensaml.ws.wstrust.AllowPostdating;
import org.opensaml.ws.wstrust.AuthenticationType;
import org.opensaml.ws.wstrust.CanonicalizationAlgorithm;
import org.opensaml.ws.wstrust.Delegatable;
import org.opensaml.ws.wstrust.DelegateTo;
import org.opensaml.ws.wstrust.EncryptWith;
import org.opensaml.ws.wstrust.Encryption;
import org.opensaml.ws.wstrust.EncryptionAlgorithm;
import org.opensaml.ws.wstrust.Entropy;
import org.opensaml.ws.wstrust.Forwardable;
import org.opensaml.ws.wstrust.Issuer;
import org.opensaml.ws.wstrust.KeySize;
import org.opensaml.ws.wstrust.KeyType;
import org.opensaml.ws.wstrust.Lifetime;
import org.opensaml.ws.wstrust.OnBehalfOf;
import org.opensaml.ws.wstrust.ProofEncryption;
import org.opensaml.ws.wstrust.Renewing;
import org.opensaml.ws.wstrust.RequestSecurityTokenType;
import org.opensaml.ws.wstrust.RequestType;
import org.opensaml.ws.wstrust.SignWith;
import org.opensaml.ws.wstrust.SignatureAlgorithm;
import org.opensaml.ws.wstrust.TokenType;
import org.opensaml.ws.wstrust.UseKey;
import org.opensaml.xml.AbstractExtensibleXMLObjectUnmarshaller;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.signature.Signature;
import org.w3c.dom.Attr;

/**
 * AbstractRequestSecurityTokenTypeUnmarshaller is an abstract unmarshaller for
 * the element of type {@link RequestSecurityTokenType}.
 * 
 * @see RequestSecurityTokenType
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public abstract class AbstractRequestSecurityTokenTypeUnmarshaller extends
        AbstractExtensibleXMLObjectUnmarshaller {

    /**
     * Constructor.
     * <p>
     * {@inheritDoc}
     */
    protected AbstractRequestSecurityTokenTypeUnmarshaller(
            String targetNamespaceURI, String targetLocalName) {
        super(targetNamespaceURI, targetLocalName);
    }

    /**
     * Unmarshalls the &lt;wst:Context&gt; attribute.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void processAttribute(XMLObject xmlObject, Attr attribute)
            throws UnmarshallingException {
        String attrName= attribute.getLocalName();
        if (RequestSecurityTokenType.CONTEXT_ATTR_LOCAL_NAME.equals(attrName)) {
            RequestSecurityTokenType rst= (RequestSecurityTokenType) xmlObject;
            String context= attribute.getValue();
            rst.setContext(context);
        }
        else {
            // xs:anyAttribute
            super.processAttribute(xmlObject, attribute);
        }
    }

    /**
     * Unmarshalls the following child elements:
     * <ul>
     * <li>{@link AllowPostdating}
     * <li>{@link AppliesTo}
     * <li>{@link AuthenticationType}
     * <li>{@link CanonicalizationAlgorithm}
     * <li>{@link Delegatable}
     * <li>{@link DelegateTo}
     * <li>{@link Encryption}
     * <li>{@link EncryptionAlgorithm}
     * <li>{@link EncryptWith}
     * <li>{@link Entropy}
     * <li>{@link Forwardable}
     * <li>{@link Issuer}
     * <li>{@link KeySize}
     * <li>{@link KeyType}
     * <li>{@link Lifetime}
     * <li>{@link OnBehalfOf}
     * <li>{@link Policy}
     * <li>{@link PolicyReference}
     * <li>{@link ProofEncryption}
     * <li>{@link Renewing}
     * <li>{@link RequestType}
     * <li>{@link SignatureAlgorithm}
     * <li>{@link SignWith}
     * <li>{@link Timestamp}
     * <li>{@link TokenType}
     * <li>{@link UseKey}
     * <li>{@link Signature}
     * </ul>
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void processChildElement(XMLObject parentXMLObject,
            XMLObject childXMLObject) throws UnmarshallingException {
        RequestSecurityTokenType message= (RequestSecurityTokenType) parentXMLObject;
        if (childXMLObject instanceof AllowPostdating) {
            AllowPostdating allowPostdating= (AllowPostdating) childXMLObject;
            message.setAllowPostdating(allowPostdating);
        }
        else if (childXMLObject instanceof AppliesTo) {
            AppliesTo appliesTo= (AppliesTo) childXMLObject;
            message.setAppliesTo(appliesTo);
        }
        else if (childXMLObject instanceof AuthenticationType) {
            AuthenticationType authenticationType= (AuthenticationType) childXMLObject;
            message.setAuthenticationType(authenticationType);
        }
        else if (childXMLObject instanceof CanonicalizationAlgorithm) {
            CanonicalizationAlgorithm canonicalizationAlgorithm= (CanonicalizationAlgorithm) childXMLObject;
            message.setCanonicalizationAlgorithm(canonicalizationAlgorithm);
        }
        else if (childXMLObject instanceof Delegatable) {
            Delegatable delegatable= (Delegatable) childXMLObject;
            message.setDelegatable(delegatable);
        }
        else if (childXMLObject instanceof DelegateTo) {
            DelegateTo delegateTo= (DelegateTo) childXMLObject;
            message.setDelegateTo(delegateTo);
        }
        else if (childXMLObject instanceof Encryption) {
            Encryption encryption= (Encryption) childXMLObject;
            message.setEncryption(encryption);
        }
        else if (childXMLObject instanceof EncryptionAlgorithm) {
            EncryptionAlgorithm encryptionAlgorithm= (EncryptionAlgorithm) childXMLObject;
            message.setEncryptionAlgorithm(encryptionAlgorithm);
        }
        else if (childXMLObject instanceof EncryptWith) {
            EncryptWith encryptWith= (EncryptWith) childXMLObject;
            message.setEncryptWith(encryptWith);
        }
        else if (childXMLObject instanceof Entropy) {
            Entropy entropy= (Entropy) childXMLObject;
            message.setEntropy(entropy);
        }
        else if (childXMLObject instanceof Forwardable) {
            Forwardable forwardable= (Forwardable) childXMLObject;
            message.setForwardable(forwardable);
        }
        else if (childXMLObject instanceof Issuer) {
            Issuer issuer= (Issuer) childXMLObject;
            message.setIssuer(issuer);
        }
        else if (childXMLObject instanceof KeySize) {
            KeySize keySize= (KeySize) childXMLObject;
            message.setKeySize(keySize);
        }
        else if (childXMLObject instanceof KeyType) {
            KeyType keyType= (KeyType) childXMLObject;
            message.setKeyType(keyType);
        }
        else if (childXMLObject instanceof Lifetime) {
            Lifetime lifetime= (Lifetime) childXMLObject;
            message.setLifetime(lifetime);
        }
        else if (childXMLObject instanceof OnBehalfOf) {
            OnBehalfOf onBehalfOf= (OnBehalfOf) childXMLObject;
            message.setOnBehalfOf(onBehalfOf);
        }
        else if (childXMLObject instanceof Policy) {
            Policy policy= (Policy) childXMLObject;
            message.setPolicy(policy);
        }
        else if (childXMLObject instanceof PolicyReference) {
            PolicyReference policyReference= (PolicyReference) childXMLObject;
            message.setPolicyReference(policyReference);
        }
        else if (childXMLObject instanceof ProofEncryption) {
            ProofEncryption proofEncryption= (ProofEncryption) childXMLObject;
            message.setProofEncryption(proofEncryption);
        }
        else if (childXMLObject instanceof Renewing) {
            Renewing renewing= (Renewing) childXMLObject;
            message.setRenewing(renewing);
        }
        else if (childXMLObject instanceof RequestType) {
            RequestType requestType= (RequestType) childXMLObject;
            message.setRequestType(requestType);
        }
        else if (childXMLObject instanceof SignatureAlgorithm) {
            SignatureAlgorithm signatureAlgorithm= (SignatureAlgorithm) childXMLObject;
            message.setSignatureAlgorithm(signatureAlgorithm);
        }
        else if (childXMLObject instanceof SignWith) {
            SignWith signWith= (SignWith) childXMLObject;
            message.setSignWith(signWith);
        }
        else if (childXMLObject instanceof Timestamp) {
            Timestamp timestamp= (Timestamp) childXMLObject;
            message.setTimestamp(timestamp);
        }
        else if (childXMLObject instanceof TokenType) {
            TokenType tokenType= (TokenType) childXMLObject;
            message.setTokenType(tokenType);
        }
        else if (childXMLObject instanceof UseKey) {
            UseKey useKey= (UseKey) childXMLObject;
            message.setUseKey(useKey);
        }
        else if (childXMLObject instanceof Signature) {
            Signature signature= (Signature) childXMLObject;
            message.setSignature(signature);
        }
        else {
            // xs:any elements
            super.processChildElement(parentXMLObject, childXMLObject);
        }
    }

}
