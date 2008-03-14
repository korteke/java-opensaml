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
package org.opensaml.ws.wstrust;

import javax.xml.namespace.QName;


import org.opensaml.ws.wspolicy.AppliesTo;
import org.opensaml.ws.wspolicy.Policy;
import org.opensaml.ws.wspolicy.PolicyReference;
import org.opensaml.ws.wssecurity.Timestamp;
import org.opensaml.xml.AttributeExtensibleXMLObject;
import org.opensaml.xml.ElementExtensibleXMLObject;
import org.opensaml.xml.signature.SignableXMLObject;

/**
 * Abstract interface for the &lt;wst:RequestSecurityToken&gt; (RST) element or
 * the &lt;wst:RequestSecurityTokenResponse&gt; (RSTR) element.
 * <p>
 * The element have a &lt;wst:Context&gt; attribute.
 * <p>
 * The element have the following possible child elements:
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
 * <li><code>xs:any</code>
 * </ul>
 * 
 * @see RequestSecurityToken
 * @see RequestSecurityTokenResponse
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public abstract interface RequestSecurityTokenType extends
        AttributeExtensibleXMLObject, ElementExtensibleXMLObject, SignableXMLObject {

    /**
     * the &lt;WSTrustMessage/@wst:Context&gt; attribute local name
     */
    public final static String CONTEXT_ATTR_LOCAL_NAME= "Context";

    /**
     * the &lt;WSTrustMessage/@wst:Context&gt; attribute name
     */
    public final static QName CONTEXT_ATTR_NAME= new QName(WSTrustConstants.WST_NS,
                                                           CONTEXT_ATTR_LOCAL_NAME,
                                                           WSTrustConstants.WST_PREFIX);

    /**
     * Returns the &lt;WSTrustMessage/@wst:Context&gt; attribute value
     * 
     * @return The &lt;WSTrustMessage/@wst:Context&gt; attribute value or
     *         <code>null</code>.
     */
    public String getContext();

    /**
     * Sets the &lt;WSTrustMessage/@wst:Context&gt; attribute value
     * 
     * @param context
     *            The Context attribute value
     */
    public void setContext(String context);

    //
    // WS-Trust elements
    //

    /**
     * Returns the &lt;wst:RequestType&gt; child element.
     * 
     * @return the {@link RequestType} child element or <code>null</code>.
     */
    public RequestType getRequestType();

    /**
     * Sets the &lt;wst:RequestType&gt; child element.
     * 
     * @param requestType
     *            the {@link RequestType} child element to set.
     */
    public void setRequestType(RequestType requestType);

    /**
     * Returns the &lt;wst:TokenType&gt; child element.
     * 
     * @return the {@link TokenType} child element or <code>null</code>.
     */
    public TokenType getTokenType();

    /**
     * Sets the &lt;wst:TokenType&gt; child element.
     * 
     * @param tokenType
     *            the {@link TokenType} child element.
     */
    public void setTokenType(TokenType tokenType);

    /**
     * Returns the &lt;wst:Entropy&gt; child element.
     * 
     * @return the {@link Entropy} child element or <code>null</code>.
     */
    public Entropy getEntropy();

    /**
     * Sets the &lt;wst:Entropy&gt; child element.
     * 
     * @param entropy
     *            the {@link Entropy} child element.
     */
    public void setEntropy(Entropy entropy);

    /**
     * Returns the &lt;wst:Lifetime&gt; child element
     * 
     * @return the {@link Lifetime} child element or <code>null</code>.
     */
    public Lifetime getLifetime();

    /**
     * Sets the &lt;wst:Lifetime&gt; child element
     * 
     * @param lifetime
     *            the {@link Lifetime} child element to set
     */
    public void setLifetime(Lifetime lifetime);

    /**
     * Returns the &lt;wst:AllowPostdating&gt; child element.
     * 
     * @return {@link AllowPostdating} child element or <code>null</code>.
     */
    public AllowPostdating getAllowPostdating();

    /**
     * Sets the &lt;wst:AllowPostdating&gt; child element.
     * 
     * @param allowPostdating
     *            the {@link AllowPostdating} child element to set.
     */
    public void setAllowPostdating(AllowPostdating allowPostdating);

    /**
     * Returns the &lt;wst:Renewing&gt; child element.
     * 
     * @return the {@link Renewing} child element or <code>null</code>.
     */
    public Renewing getRenewing();

    /**
     * Sets the &lt;wst:Renewing&gt; child element.
     * 
     * @param renewing
     *            the {@link Renewing} child element to set.
     */
    public void setRenewing(Renewing renewing);

    /**
     * Returns the &lt;wst:OnBehalfOf&gt; child element.
     * 
     * @return the {@link OnBehalfOf} child element or <code>null</code>.
     */
    public OnBehalfOf getOnBehalfOf();

    /**
     * Sets the &lt;wst:OnBehalfOf&gt; child element.
     * 
     * @param onBehalfOf
     *            the {@link OnBehalfOf} child element to set.
     */
    public void setOnBehalfOf(OnBehalfOf onBehalfOf);

    /**
     * Returns the &lt;wst:Issuer&gt; child element.
     * 
     * @return the {@link Issuer} child element or <code>null</code>.
     */
    public Issuer getIssuer();

    /**
     * Sets the &lt;wst:Issuer&gt; child element.
     * 
     * @param issuer
     *            the {@link Issuer} child element to set.
     */
    public void setIssuer(Issuer issuer);

    /**
     * Returns the &lt;wst:AuthenticationType&gt; child element.
     * 
     * @return {@link AuthenticationType} child element or <code>null</code>.
     */
    public AuthenticationType getAuthenticationType();

    /**
     * Sets the &lt;wst:AuthenticationType&gt; child element.
     * 
     * @param authenticationType
     *            the {@link AuthenticationType} child element to set.
     */
    public void setAuthenticationType(AuthenticationType authenticationType);

    /**
     * Returns the &lt;wst:KeyType&gt; child element.
     * 
     * @return {@link KeyType} child element or <code>null</code>.
     */
    public KeyType getKeyType();

    /**
     * Sets the &lt;wst:KeyType&gt; child element.
     * 
     * @param keyType
     *            the {@link KeyType} child element to set.
     */
    public void setKeyType(KeyType keyType);

    /**
     * Returns the &lt;wst:KeySize&gt; child element.
     * 
     * @return the {@link KeySize} child element or <code>null</code>
     */
    public KeySize getKeySize();

    /**
     * Sets the &lt;wst:KeySize&gt; child element.
     * 
     * @param keySize
     *            the {@link KeySize} child element to set.
     */
    public void setKeySize(KeySize keySize);

    /**
     * Returns the &lt;wst:SignatureAlgorithm&gt; child element.
     * 
     * @return {@link SignatureAlgorithm} child element or <code>null</code>.
     */
    public SignatureAlgorithm getSignatureAlgorithm();

    /**
     * Sets the &lt;wst:SignatureAlgorithm&gt; child element.
     * 
     * @param signatureAlgorithm
     *            the &lt;wst:SignatureAlgorithm&gt; child element to set.
     */
    public void setSignatureAlgorithm(SignatureAlgorithm signatureAlgorithm);

    /**
     * Returns the &lt;wst:Encryption&gt; child element.
     * 
     * @return {@link Encryption} child element or <code>null</code>.
     */
    public Encryption getEncryption();

    /**
     * Sets the &lt;wst:Encryption&gt; child element.
     * 
     * @param encryption
     *            the {@link Encryption} child element to set.
     */
    public void setEncryption(Encryption encryption);

    /**
     * Returns the &lt;wst:EncryptionAlgorithm&gt; child element.
     * 
     * @return {@link EncryptionAlgorithm} child element or <code>null</code>.
     */
    public EncryptionAlgorithm getEncryptionAlgorithm();

    /**
     * Sets the &lt;wst:EncryptionAlgorithm&gt; child element.
     * 
     * @param encryptionAlgorithm
     *            the {@link EncryptionAlgorithm} child element to set.
     */
    public void setEncryptionAlgorithm(EncryptionAlgorithm encryptionAlgorithm);

    /**
     * Returns the &lt;wst:CanonicalizationAlgorithm&gt; child element.
     * 
     * @return {@link CancelTarget} child element or <code>null</code>.
     */
    public CanonicalizationAlgorithm getCanonicalizationAlgorithm();

    /**
     * Sets the &lt;wst:CanonicalizationAlgorithm&gt; child element.
     * 
     * @param canonicalizationAlgorithm
     *            the {@link CanonicalizationAlgorithm} child element to set.
     */
    public void setCanonicalizationAlgorithm(
            CanonicalizationAlgorithm canonicalizationAlgorithm);

    /**
     * Returns the &lt;wst:ProofEncryption&gt; child element.
     * 
     * @return {@link ProofEncryption} child element or <code>null</code>.
     */
    public ProofEncryption getProofEncryption();

    /**
     * Sets the &lt;wst:ProofEncryption&gt; child element.
     * 
     * @param proofEncryption
     *            the {@link ProofEncryption} child element to set.
     */
    public void setProofEncryption(ProofEncryption proofEncryption);

    /**
     * Returns the &lt;wst:UseKey&gt; child element.
     * 
     * @return {@link UseKey} child element or <code>null</code>.
     */
    public UseKey getUseKey();

    /**
     * Sets the &lt;wst:UseKey&gt; child element.
     * 
     * @param useKey
     *            the {@link UseKey} child element to set.
     */
    public void setUseKey(UseKey useKey);

    /**
     * Returns the &lt;wst:SignWith&gt; child element.
     * 
     * @return {@link SignWith} child element or <code>null</code>.
     */
    public SignWith getSignWith();

    /**
     * Sets the &lt;wst:SignWith&gt; child element.
     * 
     * @param signWith
     *            the {@link SignWith} child element to set.
     */
    public void setSignWith(SignWith signWith);

    /**
     * Returns the &lt;EncrypWith&gt; child element.
     * 
     * @return {@link EncryptWith} child element or <code>null</code>.
     */
    public EncryptWith getEncryptWith();

    /**
     * Sets the &lt;EncrypWith&gt; child element.
     * 
     * @param encryptWith
     *            the {@link EncryptWith} child element to set.
     */
    public void setEncryptWith(EncryptWith encryptWith);

    /**
     * Returns the &lt;wst:DelegateTo&gt; child element.
     * 
     * @return {@link DelegateTo} child element or <code>null</code>.
     */
    public DelegateTo getDelegateTo();

    /**
     * Sets the &lt;wst:DelegateTo&gt; child element.
     * 
     * @param delegateTo
     *            the {@link DelegateTo} child element to set.
     */
    public void setDelegateTo(DelegateTo delegateTo);

    /**
     * Returns the &lt;wst:Forwardable&gt; child element.
     * 
     * @return {@link Forwardable} child element or <code>null</code>.
     */
    public Forwardable getForwardable();

    /**
     * Sets the &lt;wst:Forwardable&gt; child element.
     * 
     * @param forwardable
     *            the {@link Forwardable} child element to set.
     */
    public void setForwardable(Forwardable forwardable);

    /**
     * Returns the &lt;wst:Delegatable&gt; child element.
     * 
     * @return {@link Delegatable} child element or <code>null</code>.
     */
    public Delegatable getDelegatable();

    /**
     * Sets the &lt;wst:Delegatable&gt; child element.
     * 
     * @param delegatable
     *            the {@link Delegatable} child element ot set.
     */
    public void setDelegatable(Delegatable delegatable);

    //
    // WS-Policy elements
    //

    /**
     * Returns the &lt;wsp:AppliesTo&gt; child element.
     * 
     * @return {@link AppliesTo} child element or <code>null</code>.
     */
    public AppliesTo getAppliesTo();

    /**
     * Sets the &lt;wsp:AppliesTo&gt; child element.
     * 
     * @param appliesTo
     *            the {@link AppliesTo} child element to set.
     */
    public void setAppliesTo(AppliesTo appliesTo);

    /**
     * Returns the &lt;wsp:Policy&gt; child element.
     * 
     * @return {@link Policy} child element or <code>null</code>.
     */
    public Policy getPolicy();

    /**
     * Sets the &lt;wsp:Policy&gt; child element.
     * 
     * @param policy
     *            the {@link Policy} child element to set.
     */
    public void setPolicy(Policy policy);

    /**
     * Returns the &lt;wsp:PolicyReference&gt; child element.
     * 
     * @return {@link PolicyReference} child element or <code>null</code>.
     */
    public PolicyReference getPolicyReference();

    /**
     * Sets the &lt;wsp:PolicyReference&gt; child element.
     * 
     * @param policyReference
     *            the {@link PolicyReference} child element or <code>null</code>.
     */
    public void setPolicyReference(PolicyReference policyReference);

    //
    // WS-Security utility elements
    //

    /**
     * Returns the &lt;wsu:Timestamp&gt; child element.
     * 
     * @return the {@link Timestamp} child element or <code>null</code>.
     */
    public Timestamp getTimestamp();

    /**
     * Sets the &lt;wsu:Timestamp&gt; element.
     * 
     * @param timestamp
     *            the {@link Timestamp} child element to set
     */
    public void setTimestamp(Timestamp timestamp);
}
