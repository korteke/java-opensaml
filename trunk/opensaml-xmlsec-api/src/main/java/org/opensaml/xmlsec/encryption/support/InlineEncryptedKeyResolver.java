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

import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptedKey;

/**
 * Implementation of {@link EncryptedKeyResolver} which finds {@link EncryptedKey} elements
 * within the {@link org.opensaml.xmlsec.signature.KeyInfo} of the {@link EncryptedData} context.
 */
public class InlineEncryptedKeyResolver extends AbstractEncryptedKeyResolver {
    
    /** Constructor. */
    public InlineEncryptedKeyResolver() {
        super();
    }

    /** Constructor. 
     * 
     * @param recipients the set of recipients
     */
    public InlineEncryptedKeyResolver(@Nullable final Set<String> recipients) {
        super(recipients);
    }

    /** {@inheritDoc} */
    @Nonnull public Iterable<EncryptedKey> resolve(@Nonnull final EncryptedData encryptedData) {
        Constraint.isNotNull(encryptedData, "EncryptedData cannot be null");
        
        List<EncryptedKey> resolvedEncKeys = new ArrayList<EncryptedKey>();
        
        if (encryptedData.getKeyInfo() == null) {
            return resolvedEncKeys;
        }
        
        for (EncryptedKey encKey : encryptedData.getKeyInfo().getEncryptedKeys()) {
            if (matchRecipient(encKey.getRecipient())) {
                resolvedEncKeys.add(encKey);
            }
        }
        
        return resolvedEncKeys;
    }

}