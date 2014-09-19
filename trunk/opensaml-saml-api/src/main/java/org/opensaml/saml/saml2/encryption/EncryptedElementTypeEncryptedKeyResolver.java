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

package org.opensaml.saml.saml2.encryption;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.opensaml.saml.saml2.core.EncryptedElementType;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptedKey;
import org.opensaml.xmlsec.encryption.support.AbstractEncryptedKeyResolver;

import com.google.common.collect.Sets;

/**
 * An implementation of {@link org.opensaml.xmlsec.encryption.support.EncryptedKeyResolver}
 * which resolves {@link EncryptedKey} elements which appear as immediate children of the
 * {@link EncryptedElementType} which is the parent of the {@link EncryptedData} context.
 */
public class EncryptedElementTypeEncryptedKeyResolver extends AbstractEncryptedKeyResolver {
    
    /** Constructor. */
    public EncryptedElementTypeEncryptedKeyResolver() {
        super();
    }

    /** 
     * Constructor. 
     * 
     * @param recipients the set of recipients
     */
    public EncryptedElementTypeEncryptedKeyResolver(@Nullable final Set<String> recipients) {
        super(recipients);
    }

    /** 
     * Constructor. 
     * 
     * @param recipient the recipient
     */
    public EncryptedElementTypeEncryptedKeyResolver(@Nullable final String recipient) {
        this(Sets.newHashSet(recipient));
    }

    /** {@inheritDoc} */
    public Iterable<EncryptedKey> resolve(EncryptedData encryptedData) {
        List<EncryptedKey> resolvedEncKeys = new ArrayList<EncryptedKey>();
        
        if (! (encryptedData.getParent() instanceof EncryptedElementType) ) {
            return resolvedEncKeys;
        }
        
        EncryptedElementType encElementType = (EncryptedElementType) encryptedData.getParent();
        
        for (EncryptedKey encKey : encElementType.getEncryptedKeys()) {
            if (matchRecipient(encKey.getRecipient())) {
                resolvedEncKeys.add(encKey);
            }
        }
        
        return resolvedEncKeys;
    }
}