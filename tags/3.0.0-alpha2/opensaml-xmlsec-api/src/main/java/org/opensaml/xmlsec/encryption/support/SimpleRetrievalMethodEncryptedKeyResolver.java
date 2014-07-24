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

package org.opensaml.xmlsec.encryption.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptedKey;
import org.opensaml.xmlsec.signature.RetrievalMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;

/**
 * Implementation of {@link EncryptedKeyResolver} which finds {@link EncryptedKey} elements by dereferencing
 * {@link RetrievalMethod} children of the {@link org.opensaml.xmlsec.signature.KeyInfo} of the {@link EncryptedData}
 * context.
 * 
 * The RetrievalMethod must have a <code>Type</code> attribute with the value of
 * {@link EncryptionConstants#TYPE_ENCRYPTED_KEY}. The <code>URI</code> attribute value must be a same-document
 * fragment identifier (via ID attribute). Processing of transforms children of RetrievalMethod is not supported by this
 * implementation.
 */
public class SimpleRetrievalMethodEncryptedKeyResolver extends AbstractEncryptedKeyResolver {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(SimpleRetrievalMethodEncryptedKeyResolver.class);
    
    /** Constructor. */
    public SimpleRetrievalMethodEncryptedKeyResolver() {
        super();
    }

    /** 
     * Constructor. 
     * 
     * @param recipients the set of recipients
     */
    public SimpleRetrievalMethodEncryptedKeyResolver(@Nullable final Set<String> recipients) {
        super(recipients);
    }

    /** 
     * Constructor. 
     * 
     * @param recipient the recipient
     */
    public SimpleRetrievalMethodEncryptedKeyResolver(@Nullable final String recipient) {
        this(Sets.newHashSet(recipient));
    }

    /** {@inheritDoc} */
    @Nonnull public Iterable<EncryptedKey> resolve(@Nonnull final EncryptedData encryptedData) {
        Constraint.isNotNull(encryptedData, "EncryptedData cannot be null");
        
        List<EncryptedKey> resolvedEncKeys = new ArrayList<EncryptedKey>();

        if (encryptedData.getKeyInfo() == null) {
            return resolvedEncKeys;
        }

        for (RetrievalMethod rm : encryptedData.getKeyInfo().getRetrievalMethods()) {
            if (!Objects.equal(rm.getType(), EncryptionConstants.TYPE_ENCRYPTED_KEY)) {
                continue;
            } else if (rm.getTransforms() != null) {
                log.warn("EncryptedKey RetrievalMethod has transforms, cannot process");
                continue;
            }

            EncryptedKey encKey = dereferenceURI(rm);
            if (encKey == null) {
                continue;
            } else if (matchRecipient(encKey.getRecipient())) {
                resolvedEncKeys.add(encKey);
            }
        }

        return resolvedEncKeys;
    }

    /**
     * Dereference the URI attribute of the specified retrieval method into an EncryptedKey.
     * 
     * @param rm the RetrievalMethod to process
     * @return the dereferenced EncryptedKey
     */
    @Nullable protected EncryptedKey dereferenceURI(@Nonnull final RetrievalMethod rm) {
        String uri = rm.getURI();
        if (Strings.isNullOrEmpty(uri) || !uri.startsWith("#")) {
            log.warn("EncryptedKey RetrievalMethod did not contain a same-document URI reference, cannot process");
            return null;
        }
        
        XMLObject target = rm.resolveIDFromRoot(uri.substring(1));
        if (target == null) {
            log.warn("EncryptedKey RetrievalMethod URI could not be dereferenced");
            return null;
        } else if (!(target instanceof EncryptedKey)) {
            log.warn("The product of dereferencing the EncryptedKey RetrievalMethod was not an EncryptedKey");
            return null;
        }
        return (EncryptedKey) target;
    }

}