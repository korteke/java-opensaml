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

import java.util.ArrayList;
import java.util.List;

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
import org.opensaml.ws.wstrust.RequestSecurityToken;
import org.opensaml.ws.wstrust.RequestSecurityTokenResponse;
import org.opensaml.ws.wstrust.RequestSecurityTokenType;
import org.opensaml.ws.wstrust.RequestType;
import org.opensaml.ws.wstrust.SignWith;
import org.opensaml.ws.wstrust.SignatureAlgorithm;
import org.opensaml.ws.wstrust.TokenType;
import org.opensaml.ws.wstrust.UseKey;
import org.opensaml.xml.AbstractExtensibleXMLObject;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.util.XMLConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * AbstractRequestSecurityTokenType is an abstract signable, validating &lt;wst:RequestSecurityToken&gt; or
 * &lt;wst:RequestSecurityTokenResponse&gt; element, with a &lt;wst:Context&gt; attribute.
 * 
 * @see RequestSecurityTokenType
 * @see RequestSecurityToken
 * @see RequestSecurityTokenResponse
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public abstract class AbstractRequestSecurityTokenType extends AbstractExtensibleXMLObject implements
        RequestSecurityTokenType {

    /** The &lt;wst:Context&gt; attribute value */
    private String context_ = null;

    /** The &lt;wst:TokenType&gt; child element */
    private TokenType tokenType_ = null;

    /** The &lt;wst:RequestType&gt; child element */
    private RequestType requestType_ = null;

    /** The &lt;wst:AllowPostdating&gt; child element */
    private AllowPostdating allowPostdating_ = null;

    /** The &lt;wsp:AppliesTo&gt; child element */
    private AppliesTo appliesTo_ = null;

    /** The &lt;wst:AuthenticationType&gt; child element */
    private AuthenticationType authenticationType_ = null;

    /** The &lt;wst:CanonicalizationAlgorithm&gt; child element */
    private CanonicalizationAlgorithm canonicalizationAlgorithm_ = null;

    /** The &lt;wst:Delegatable&gt; child element */
    private Delegatable delegatable_ = null;

    /** The &lt;wst:DelegateTo&gt; child element */
    private DelegateTo delegateTo_ = null;

    /** The &lt;wst:Encryption&gt; child element */
    private Encryption encryption_ = null;

    /** The &lt;wst:EncryptionAlgorithm&gt; child element */
    private EncryptionAlgorithm encryptionAlgorithm_ = null;

    /** The &lt;wst:EncryptWith&gt; child element */
    private EncryptWith encryptWith_ = null;

    /** The &lt;wst:Entropy&gt; child element */
    private Entropy entropy_ = null;

    /** The &lt;wst:Forwardable&gt; child element */
    private Forwardable forwardable_ = null;

    /** The &lt;wst:Issuer&gt; child element */
    private Issuer issuer_ = null;

    /** The &lt;wst:KeySize&gt; child element */
    private KeySize keySize_ = null;

    /** The &lt;wst:KeyType&gt; child element */
    private KeyType keyType_ = null;

    /** The &lt;wst:Lifetime&gt; child element */
    private Lifetime lifetime_ = null;

    /** The &lt;wst:OnBehalfOf&gt; child element */
    private OnBehalfOf onBehalfOf_ = null;

    /** The &lt;wsp:Policy&gt; child element */
    private Policy policy_ = null;

    /** The &lt;wsp:PolicyReference&gt; child element */
    private PolicyReference policyReference_ = null;

    /** The &lt;wst:ProofEncryption&gt; child element */
    private ProofEncryption proofEncryption_ = null;

    /** The &lt;wst:Renewing&gt; child element */
    private Renewing renewing_ = null;

    /** The &lt;wst:SignatureAlgorithm&gt; child element */
    private SignatureAlgorithm signatureAlgorithm_ = null;

    /** The &lt;wst:SignWith&gt; child element */
    private SignWith signWith_ = null;

    /** The &lt;wst:UseKey&gt; child element */
    private UseKey useKey_ = null;

    /** The &lt;wsu:Timestamp&gt; child element */
    private Timestamp timestamp_ = null;

    /** The &lt;ds:Signature&gt; element */
    private Signature signature_ = null;

    /**
     * Constructor.
     * 
     * @param namespaceURI namespace of the element
     * @param elementLocalName name of the element
     * @param namespacePrefix namespace prefix of the element
     */
    public AbstractRequestSecurityTokenType(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#getAllowPostdating()
     */
    public AllowPostdating getAllowPostdating() {
        return allowPostdating_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#getAppliesTo()
     */
    public AppliesTo getAppliesTo() {
        return appliesTo_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#getAuthenticationType()
     */
    public AuthenticationType getAuthenticationType() {
        return authenticationType_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#getCanonicalizationAlgorithm()
     */
    public CanonicalizationAlgorithm getCanonicalizationAlgorithm() {
        return canonicalizationAlgorithm_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#getContext()
     */
    public String getContext() {
        return context_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#getDelegatable()
     */
    public Delegatable getDelegatable() {
        return delegatable_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#getDelegateTo()
     */
    public DelegateTo getDelegateTo() {
        return delegateTo_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#getEncryption()
     */
    public Encryption getEncryption() {
        return encryption_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#getEncryptionAlgorithm()
     */
    public EncryptionAlgorithm getEncryptionAlgorithm() {
        return encryptionAlgorithm_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#getEncryptWith()
     */
    public EncryptWith getEncryptWith() {
        return encryptWith_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#getEntropy()
     */
    public Entropy getEntropy() {
        return entropy_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#getForwardable()
     */
    public Forwardable getForwardable() {
        return forwardable_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#getIssuer()
     */
    public Issuer getIssuer() {
        return issuer_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#getKeySize()
     */
    public KeySize getKeySize() {
        return keySize_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#getKeyType()
     */
    public KeyType getKeyType() {
        return keyType_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#getLifetime()
     */
    public Lifetime getLifetime() {
        return lifetime_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#getOnBehalfOf()
     */
    public OnBehalfOf getOnBehalfOf() {
        return onBehalfOf_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#getPolicy()
     */
    public Policy getPolicy() {
        return policy_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#getPolicyReference()
     */
    public PolicyReference getPolicyReference() {
        return policyReference_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#getProofEncryption()
     */
    public ProofEncryption getProofEncryption() {
        return proofEncryption_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#getRenewing()
     */
    public Renewing getRenewing() {
        return renewing_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#getRequestType()
     */
    public RequestType getRequestType() {
        return requestType_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#getSignatureAlgorithm()
     */
    public SignatureAlgorithm getSignatureAlgorithm() {
        return signatureAlgorithm_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#getSignWith()
     */
    public SignWith getSignWith() {
        return signWith_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#getTimestamp()
     */
    public Timestamp getTimestamp() {
        return timestamp_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#getTokenType()
     */
    public TokenType getTokenType() {
        return tokenType_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#getUseKey()
     */
    public UseKey getUseKey() {
        return useKey_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#setAllowPostdating(org.opensaml.ws.wstrust.AllowPostdating)
     */
    public void setAllowPostdating(AllowPostdating allowPostdating) {
        allowPostdating_ = prepareForAssignment(allowPostdating_, allowPostdating);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#setAppliesTo(org.opensaml.ws.wspolicy.AppliesTo)
     */
    public void setAppliesTo(AppliesTo appliesTo) {
        appliesTo_ = prepareForAssignment(appliesTo_, appliesTo);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#setAuthenticationType(org.opensaml.ws.wstrust.AuthenticationType)
     */
    public void setAuthenticationType(AuthenticationType authenticationType) {
        authenticationType_ = prepareForAssignment(authenticationType_, authenticationType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#setCanonicalizationAlgorithm(org.opensaml.ws.wstrust.CanonicalizationAlgorithm)
     */
    public void setCanonicalizationAlgorithm(CanonicalizationAlgorithm canonicalizationAlgorithm) {
        canonicalizationAlgorithm_ = prepareForAssignment(canonicalizationAlgorithm_, canonicalizationAlgorithm);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#setContext(java.lang.String)
     */
    public void setContext(String context) {
        context_ = prepareForAssignment(context_, context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#setDelegatable(org.opensaml.ws.wstrust.Delegatable)
     */
    public void setDelegatable(Delegatable delegatable) {
        delegatable_ = prepareForAssignment(delegatable_, delegatable);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#setDelegateTo(org.opensaml.ws.wstrust.DelegateTo)
     */
    public void setDelegateTo(DelegateTo delegateTo) {
        delegateTo_ = prepareForAssignment(delegateTo_, delegateTo);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#setEncryption(org.opensaml.ws.wstrust.Encryption)
     */
    public void setEncryption(Encryption encryption) {
        encryption_ = prepareForAssignment(encryption_, encryption);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#setEncryptionAlgorithm(org.opensaml.ws.wstrust.EncryptionAlgorithm)
     */
    public void setEncryptionAlgorithm(EncryptionAlgorithm encryptionAlgorithm) {
        encryptionAlgorithm_ = prepareForAssignment(encryptionAlgorithm_, encryptionAlgorithm);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#setEncryptWith(org.opensaml.ws.wstrust.EncryptWith)
     */
    public void setEncryptWith(EncryptWith encryptWith) {
        encryptWith_ = prepareForAssignment(encryptWith_, encryptWith);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#setEntropy(org.opensaml.ws.wstrust.Entropy)
     */
    public void setEntropy(Entropy entropy) {
        entropy_ = prepareForAssignment(entropy_, entropy);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#setForwardable(org.opensaml.ws.wstrust.Forwardable)
     */
    public void setForwardable(Forwardable forwardable) {
        forwardable_ = prepareForAssignment(forwardable_, forwardable);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#setIssuer(org.opensaml.ws.wstrust.Issuer)
     */
    public void setIssuer(Issuer issuer) {
        issuer_ = prepareForAssignment(issuer_, issuer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#setKeySize(org.opensaml.ws.wstrust.KeySize)
     */
    public void setKeySize(KeySize keySize) {
        keySize_ = prepareForAssignment(keySize_, keySize);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#setKeyType(org.opensaml.ws.wstrust.KeyType)
     */
    public void setKeyType(KeyType keyType) {
        keyType_ = prepareForAssignment(keyType_, keyType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#setLifetime(org.opensaml.ws.wstrust.Lifetime)
     */
    public void setLifetime(Lifetime lifetime) {
        lifetime_ = prepareForAssignment(lifetime_, lifetime);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#setOnBehalfOf(org.opensaml.ws.wstrust.OnBehalfOf)
     */
    public void setOnBehalfOf(OnBehalfOf onBehalfOf) {
        onBehalfOf_ = prepareForAssignment(onBehalfOf_, onBehalfOf);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#setPolicy(org.opensaml.ws.wspolicy.Policy)
     */
    public void setPolicy(Policy policy) {
        policy_ = prepareForAssignment(policy_, policy);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#setPolicyReference(org.opensaml.ws.wspolicy.PolicyReference)
     */
    public void setPolicyReference(PolicyReference policyReference) {
        policyReference_ = prepareForAssignment(policyReference_, policyReference);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#setProofEncryption(org.opensaml.ws.wstrust.ProofEncryption)
     */
    public void setProofEncryption(ProofEncryption proofEncryption) {
        proofEncryption_ = prepareForAssignment(proofEncryption_, proofEncryption);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#setRenewing(org.opensaml.ws.wstrust.Renewing)
     */
    public void setRenewing(Renewing renewing) {
        renewing_ = prepareForAssignment(renewing_, renewing);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#setRequestType(org.opensaml.ws.wstrust.RequestType)
     */
    public void setRequestType(RequestType requestType) {
        requestType_ = prepareForAssignment(requestType_, requestType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#setSignatureAlgorithm(org.opensaml.ws.wstrust.SignatureAlgorithm)
     */
    public void setSignatureAlgorithm(SignatureAlgorithm signatureAlgorithm) {
        signatureAlgorithm_ = prepareForAssignment(signatureAlgorithm_, signatureAlgorithm);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#setSignWith(org.opensaml.ws.wstrust.SignWith)
     */
    public void setSignWith(SignWith signWith) {
        signWith_ = prepareForAssignment(signWith_, signWith);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#setTimestamp(org.opensaml.ws.wssecurity.Timestamp)
     */
    public void setTimestamp(Timestamp timestamp) {
        timestamp_ = prepareForAssignment(timestamp_, timestamp);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#setTokenType(org.opensaml.ws.wstrust.TokenType)
     */
    public void setTokenType(TokenType tokenType) {
        tokenType_ = prepareForAssignment(tokenType_, tokenType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.WSTrustMessage#setUseKey(org.opensaml.ws.wstrust.UseKey)
     */
    public void setUseKey(UseKey useKey) {
        useKey_ = prepareForAssignment(useKey_, useKey);
    }

    /**
     * Returns an ordered list of the common child elements and the <code>xs:any</code> unknown elements.
     * 
     * @return the ordered {@link List} of child elements.
     */
    protected List<XMLObject> getCommonChildren() {
        List<XMLObject> children = new ArrayList<XMLObject>();
        // FIXME: CHECK ORDER of the child elements.
        if (tokenType_ != null) {
            children.add(tokenType_);
        }
        if (requestType_ != null) {
            children.add(requestType_);
        }
        if (appliesTo_ != null) {
            children.add(appliesTo_);
        }
        if (entropy_ != null) {
            children.add(entropy_);
        }
        if (lifetime_ != null) {
            children.add(lifetime_);
        }
        if (allowPostdating_ != null) {
            children.add(allowPostdating_);
        }
        if (renewing_ != null) {
            children.add(renewing_);
        }
        if (onBehalfOf_ != null) {
            children.add(onBehalfOf_);
        }
        if (issuer_ != null) {
            children.add(issuer_);
        }
        if (authenticationType_ != null) {
            children.add(authenticationType_);
        }
        if (keyType_ != null) {
            children.add(keyType_);
        }
        if (keySize_ != null) {
            children.add(keySize_);
        }
        if (signatureAlgorithm_ != null) {
            children.add(signatureAlgorithm_);
        }
        if (encryption_ != null) {
            children.add(encryption_);
        }
        if (encryptionAlgorithm_ != null) {
            children.add(encryptionAlgorithm_);
        }
        if (canonicalizationAlgorithm_ != null) {
            children.add(canonicalizationAlgorithm_);
        }
        if (proofEncryption_ != null) {
            children.add(proofEncryption_);
        }
        if (useKey_ != null) {
            children.add(useKey_);
        }
        if (signWith_ != null) {
            children.add(signWith_);
        }
        if (encryptWith_ != null) {
            children.add(encryptWith_);
        }
        if (delegateTo_ != null) {
            children.add(delegateTo_);
        }
        if (forwardable_ != null) {
            children.add(forwardable_);
        }
        if (delegatable_ != null) {
            children.add(delegatable_);
        }
        if (policy_ != null) {
            children.add(policy_);
        }
        if (policyReference_ != null) {
            children.add(policyReference_);
        }
        if (timestamp_ != null) {
            children.add(timestamp_);
        }
        // xs:any element
        if (!getUnknownXMLObjects().isEmpty()) {
            children.addAll(getUnknownXMLObjects());
        }
        // add signature
        if(signature_ != null){
            children.add(signature_);
        }

        return children;
    }

    /** {@inheritDoc} */
    public Signature getSignature() {
        return signature_;
    }

    /** {@inheritDoc} */
    public void setSignature(Signature newSignature) {
        signature_ = prepareForAssignment(signature_, newSignature);
    }
    
    /** {@inheritDoc} */
    public boolean isSigned() {
        Element domElement = getDOM();

        if (domElement == null) {
            return false;
        }

        NodeList children = domElement.getChildNodes();
        Element childElement;
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            childElement = (Element) children.item(i);
            if (childElement.getNamespaceURI().equals(XMLConstants.XMLSIG_NS)
                    && childElement.getLocalName().equals(Signature.DEFAULT_ELEMENT_LOCAL_NAME)) {
                return true;
            }
        }

        return false;
    }

    /*
     * Hides the super method.
     * 
     * @see org.glite.xml.AbstractElementExtensibleXMLObject#getOrderedChildren()
     */
    abstract public List<XMLObject> getOrderedChildren();

}
