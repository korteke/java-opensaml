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

package org.opensaml.xmlsec.signature.support.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.xml.AttributeSupport;
import net.shibboleth.utilities.java.support.xml.ElementSupport;

import org.opensaml.xmlsec.SignatureValidationParameters;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * Component which validates a {@link Signature}'s signature and digest algorithm URI's against
 * a supplied algorithm whitelist and blacklist.
 * 
 * <p>
 * The evaluation is based on the Signature's underlying DOM structure, therefore the Signature must
 * have a cached DOM before this validator is used.
 * </p>
 */
public class SignatureAlgorithmValidator {
    
    /** QName of 'ds:SignedInfo' element. */
    private static final QName ELEMENT_NAME_SIGNED_INFO = new QName(SignatureConstants.XMLSIG_NS, "SignedInfo");
    
    /** QName of 'ds:SignatureMethod' element. */
    private static final QName ELEMENT_NAME_SIGNATURE_METHOD = new QName(SignatureConstants.XMLSIG_NS, 
            "SignatureMethod");
    
    /** QName of 'ds:Reference' element. */
    private static final QName ELEMENT_NAME_REFERENCE = new QName(SignatureConstants.XMLSIG_NS, "Reference");
    
    /** QName of 'ds:DigestMethod' element. */
    private static final QName ELEMENT_NAME_DIGEST_METHOD = new QName(SignatureConstants.XMLSIG_NS, "DigestMethod");
    
    /** Local name of 'Algorithm' attribute. */
    private static final String ATTR_NAME_ALGORTHM = "Algorithm";
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(SignatureAlgorithmValidator.class);
    
    /** The collection of algorithm URI's which are whitelisted. */
    private Collection<String> whitelistedAlgorithmURIs;
    
    /** The collection of algorithm URI's which are blacklisted. */
    private Collection<String> blacklistedAlgorithmURIs;
    
    /**
     * Constructor.
     *
     * @param params signature validation parameters containing the whitelist and blacklist
     */
    public SignatureAlgorithmValidator(@Nonnull final SignatureValidationParameters params) {
        Constraint.isNotNull(params, "SignatureValidationParameters may not be null");
        whitelistedAlgorithmURIs = params.getWhitelistedAlgorithmURIs();
        blacklistedAlgorithmURIs = params.getBlacklistedAlgorithmsURIs();
    }
    
    /**
     * Constructor.
     *
     * @param whitelistAlgos the algorithm whitelist
     * @param blacklistAlgos the algorithm blacklist
     */
    public SignatureAlgorithmValidator(@Nullable final Collection<String> whitelistAlgos,
            @Nullable final Collection<String> blacklistAlgos) {
        whitelistedAlgorithmURIs = whitelistAlgos;
        blacklistedAlgorithmURIs = blacklistAlgos;
    }
    
    /** {@inheritDoc}. */
    public void validate(@Nonnull final Signature signature) throws SignatureException {
        Constraint.isNotNull(signature, "Signature was null");
        checkDOM(signature);
        
        String signatureAlgorithm = getSignatureAlgorithm(signature);
        log.debug("Validating SignedInfo/SignatureMethod/@Algorithm against whitelist/blacklist: {}", 
                signatureAlgorithm);
        validateAlgorithmURI(signatureAlgorithm);
        
        for (String digestMethod : getDigestMethods(signature)) {
            log.debug("Validating SignedInfo/Reference/DigestMethod/@Algorithm against whitelist/blacklist: {}", 
                    digestMethod);
            validateAlgorithmURI(digestMethod);
        }
    }
    
    /**
     * Check that Signature XMLObject has a cached DOM Element.
     * @param signature the signature to evaluate
     * @throws SignatureException if signature does not have a cached DOM Element
     */
    protected void checkDOM(@Nonnull final Signature signature) throws SignatureException {
        if (signature.getDOM() == null) {
            log.warn("Signgaure does not have a cached DOM Element.");
            throw new SignatureException("Signature does not have a cached DOM Element.");
        }
    }
    
    /**
     * Get the signature algorithm.
     * 
     * @param signatureXMLObject the signature to evaluate
     * @return the signature algorithm
     * @throws SignatureException if signature algorithm can not be resolved
     */
    @Nonnull protected String getSignatureAlgorithm(@Nonnull final Signature signatureXMLObject) 
            throws SignatureException {
        Element signature = signatureXMLObject.getDOM();
        Element signedInfo = ElementSupport.getFirstChildElement(signature, ELEMENT_NAME_SIGNED_INFO);
        Element signatureMethod = ElementSupport.getFirstChildElement(signedInfo, ELEMENT_NAME_SIGNATURE_METHOD);
        String signatureMethodAlgorithm = StringSupport.trimOrNull(
                AttributeSupport.getAttributeValue(signatureMethod, null, ATTR_NAME_ALGORTHM));
        if (signatureMethodAlgorithm != null) {
            return signatureMethodAlgorithm;
        } else {
            throw new SignatureException("SignatureMethod Algorithm was null");
        }
    }

    
    /**
     * Get the list of Signature Reference DigestMethod algorithm URIs.
     * 
     * @param signatureXMLObject the signature to evaluate
     * @return list of algorithm URIs
     * @throws SignatureException if a DigestMethod is found to have a null or empty Algorithm attribute
     */
    @Nonnull protected List<String> getDigestMethods(@Nonnull Signature signatureXMLObject) throws SignatureException {
        ArrayList<String> digestMethodAlgorithms = new ArrayList<>();
        
        Element signature = signatureXMLObject.getDOM();
        
        Element signedInfo = ElementSupport.getFirstChildElement(signature, ELEMENT_NAME_SIGNED_INFO);
        
        for (Element reference : ElementSupport.getChildElements(signedInfo, ELEMENT_NAME_REFERENCE)) {
            Element digestMethod = ElementSupport.getFirstChildElement(reference, ELEMENT_NAME_DIGEST_METHOD);
            String digestMethodAlgorithm = StringSupport.trimOrNull(
                    AttributeSupport.getAttributeValue(digestMethod, null, ATTR_NAME_ALGORTHM));
            if (digestMethodAlgorithm != null) {
                digestMethodAlgorithms.add(digestMethodAlgorithm);
            } else {
                throw new SignatureException("Saw null DigestMethod Algorithm");
            }
        }
        
        return digestMethodAlgorithms;
    }

    /**
     * Validate the supplied algorithm URI against the configured whitelist and blacklist.
     * 
     * @param algorithmURI the algorithm URI to evaluate
     * @throws SignatureException if the algorithm URI does not satisfy the whitelist/blacklist policy
     */
    protected void validateAlgorithmURI(@Nonnull final String algorithmURI) throws SignatureException {
        log.debug("Validating algorithm URI against whitelist and blacklist: "
                + "algorithm: {}, whitelist: {}, blacklist: {}",
                algorithmURI, whitelistedAlgorithmURIs, blacklistedAlgorithmURIs);
        
        if (!AlgorithmSupport.validateAlgorithmURI(algorithmURI, whitelistedAlgorithmURIs, blacklistedAlgorithmURIs)) {
            throw new SignatureException("Algorithm failed whitelist/blacklist validation: " + algorithmURI);
        }
        
    }

}
