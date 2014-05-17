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

package org.opensaml.saml.common;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.annotation.constraint.Live;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.collection.LazyList;
import net.shibboleth.utilities.java.support.collection.LazySet;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.transforms.params.InclusiveNamespaces;
import org.opensaml.core.xml.NamespaceManager;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.signature.support.ConfigurableContentReference;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * A content reference for SAML objects that will be signed. The reference is created per the SAML specification. 
 * 
 * <p>
 * The default digest algorithm used is {@link SignatureConstants#ALGO_ID_DIGEST_SHA256}.
 * </p>
 * 
 * <p>
 * The default set of transforms applied consists of {@link SignatureConstants#TRANSFORM_ENVELOPED_SIGNATURE}
 * and {@link SignatureConstants#TRANSFORM_C14N_EXCL_WITH_COMMENTS}.
 * </p>
 * 
 * <p>
 * When generating an exclusive canonicalization transform, an inclusive namespace list is 
 * generated from the namespaces, retrieved from {@link org.opensaml.core.xml.XMLObject#getNamespaces()},
 * used by the SAML object to be signed and all of it's descendants.
 * </p>
 * 
 * <p>
 * Note that the SAML specification states that:
 *   1) an exclusive canonicalization transform (either with or without comments) SHOULD be used.
 *   2) transforms other than enveloped signature and one of the two exclusive canonicalizations
 *      SHOULD NOT be used.
 * Careful consideration should be made before deviating from these recommendations.
 * </p>
 * 
 */
public class SAMLObjectContentReference implements ConfigurableContentReference {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(SAMLObjectContentReference.class);

    /** SAMLObject this reference refers to. */
    @Nonnull private final SignableSAMLObject signableObject;
    
    /** Algorithm used to digest the content. */
    @Nonnull @NotEmpty private String digestAlgorithm;

    /** Transforms applied to the content. */
    @Nonnull @NonnullElements private List<String> transforms;

    /**
     * Constructor.
     * 
     * @param newSignableObject the SAMLObject this reference refers to
     */
    public SAMLObjectContentReference(@Nonnull final SignableSAMLObject newSignableObject) {
        signableObject = newSignableObject;
        transforms = new LazyList<String>();
        
        // Set defaults
        digestAlgorithm = SignatureConstants.ALGO_ID_DIGEST_SHA256;
        
        transforms.add(SignatureConstants.TRANSFORM_ENVELOPED_SIGNATURE);
        transforms.add(SignatureConstants.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
    }
    
    /**
     * Gets the transforms applied to the content prior to digest generation.
     * 
     * @return the transforms applied to the content prior to digest generation
     */
    @Nonnull @NonnullElements @Live public List<String> getTransforms() {
        return transforms;
    }

    /** {@inheritDoc}. */
    @Nonnull @NotEmpty public String getDigestAlgorithm() {
        return digestAlgorithm;
    }

    /** {@inheritDoc}. */
    public void setDigestAlgorithm(@Nonnull @NotEmpty final String newAlgorithm) {
        digestAlgorithm = Constraint.isNotNull(StringSupport.trimOrNull(newAlgorithm),
                "Digest algorithm cannot be empty or null");
    }

    /** {@inheritDoc} */
    public void createReference(@Nonnull final XMLSignature signature) {
        try {
            Transforms dsigTransforms = new Transforms(signature.getDocument());
            for (int i=0; i<transforms.size(); i++) {
                String transform = transforms.get(i);
                dsigTransforms.addTransform(transform);
                
                if (transform.equals(SignatureConstants.TRANSFORM_C14N_EXCL_WITH_COMMENTS) ||
                    transform.equals(SignatureConstants.TRANSFORM_C14N_EXCL_OMIT_COMMENTS)) {
                    
                    processExclusiveTransform(signature, dsigTransforms.item(i));
                    
                }
            }

            if (!Strings.isNullOrEmpty(signableObject.getSignatureReferenceID()) ) {
                signature.addDocument("#" + signableObject.getSignatureReferenceID(), dsigTransforms, digestAlgorithm);
            } else {
                log.debug("SignableSAMLObject had no reference ID, signing using whole document Reference URI");
                signature.addDocument("" , dsigTransforms, digestAlgorithm);
            }
            
        } catch (TransformationException e) {
            log.error("Unsupported signature transformation", e);
        } catch (XMLSignatureException e) {
            log.error("Error adding content reference to signature", e);
        }
    }

    /**
     * Populate the inclusive namspace prefixes on the specified Apache (exclusive) transform object.
     * 
     * @param signature the Apache XMLSignature object
     * @param transform the Apache Transform object representing an exclusive transform
     */
    private void processExclusiveTransform(@Nonnull final XMLSignature signature, @Nonnull final Transform transform) {
        // Namespaces that aren't visibly used, such as those used in QName attribute values, would
        // be stripped out by exclusive canonicalization. Need to make sure they aren't by explicitly
        // telling the transformer about them.
        log.debug("Adding list of inclusive namespaces for signature exclusive canonicalization transform");
        LazySet<String> inclusiveNamespacePrefixes = new LazySet<String>();
        populateNamespacePrefixes(inclusiveNamespacePrefixes, signableObject);
        
        if (inclusiveNamespacePrefixes != null && inclusiveNamespacePrefixes.size() > 0) {
            InclusiveNamespaces inclusiveNamespaces = new InclusiveNamespaces(signature.getDocument(),
                    inclusiveNamespacePrefixes);
            transform.getElement().appendChild(inclusiveNamespaces.getElement());
        }
    }

    /**
     * Populates the given set with the non-visibly used namespace prefixes used by the given XMLObject 
     * and all of its descendants, as determined by the signature content object's namespace manager.
     * 
     * @param namespacePrefixes the namespace prefix set to be populated
     * @param signatureContent the XMLObject whose namespace prefixes will be used to populate the set
     */
    private void populateNamespacePrefixes(@Nonnull @NonnullElements final Set<String> namespacePrefixes,
            @Nonnull final XMLObject signatureContent) {
        for (String prefix: signatureContent.getNamespaceManager().getNonVisibleNamespacePrefixes()) {
            if (prefix != null) {
                // For the default namespace prefix, exclusive c14n uses the special token "#default".
                // Apache xmlsec requires this to be represented in the set with the
                // (completely undocumented) string "xmlns".
                if (NamespaceManager.DEFAULT_NS_TOKEN.equals(prefix)) {
                    namespacePrefixes.add("xmlns");
                } else {
                    namespacePrefixes.add(prefix);
                }
            }
        }
    }
}