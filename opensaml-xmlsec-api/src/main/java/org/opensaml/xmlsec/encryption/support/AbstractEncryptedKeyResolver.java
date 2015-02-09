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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotLive;
import net.shibboleth.utilities.java.support.annotation.constraint.Unmodifiable;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.xmlsec.encryption.DataReference;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptedKey;
import org.opensaml.xmlsec.keyinfo.KeyInfoSupport;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

/**
 * Abstract class implementation for {@link EncryptedKeyResolver}.
 */
public abstract class AbstractEncryptedKeyResolver implements EncryptedKeyResolver {
    
    /** Recipient attribute criteria against which to match.*/
    private final Collection<String> recipients;
    
    /** Constructor. */
    public AbstractEncryptedKeyResolver() {
        recipients = Collections.emptySet();
    }

    /** 
     * Constructor. 
     * 
     * @param newRecipents set of recipients
     */
    public AbstractEncryptedKeyResolver(@Nullable final Set<String> newRecipents) {
        recipients = new HashSet<>(StringSupport.normalizeStringCollection(newRecipents));
    }

    /** 
     * Constructor. 
     * 
     * @param recipient the recipient
     */
    public AbstractEncryptedKeyResolver(@Nullable final String recipient) {
        final String trimmed = StringSupport.trimOrNull(recipient);
        if (trimmed != null) {
            recipients = Collections.singleton(trimmed);
        } else {
            recipients = Collections.emptySet();
        }
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull @NonnullElements @Unmodifiable @NotLive public Set<String> getRecipients() {
        return ImmutableSet.copyOf(recipients);
    }
    
    /**
     * Evaluate whether the specified recipient attribute value matches this resolver's
     * recipient criteria.
     * 
     * @param recipient the recipient value to evaluate
     * @return true if the recipient value matches the resolver's criteria, false otherwise
     */
    protected boolean matchRecipient(@Nullable final String recipient) {
        String trimmedRecipient = StringSupport.trimOrNull(recipient);
        if (trimmedRecipient == null || recipients.isEmpty()) {
            return true;
        }
        
        return recipients.contains(trimmedRecipient);
    }
    
    /**
     * Evaluate whether an EncryptedKey's CarriedKeyName matches one of the KeyName values
     * from the EncryptedData context.
     * 
     * @param encryptedData the EncryptedData context
     * @param encryptedKey the candidate Encryptedkey to evaluate
     * @return true if the encrypted key's carried key name matches that of the encrytped data, 
     *          false otherwise
     */
    protected boolean matchCarriedKeyName(@Nonnull final EncryptedData encryptedData,
            @Nonnull final EncryptedKey encryptedKey) {
        Constraint.isNotNull(encryptedData, "EncryptedData cannot be null");
        Constraint.isNotNull(encryptedKey, "EncryptedKey cannot be null");
        
        if (encryptedKey.getCarriedKeyName() == null 
                || Strings.isNullOrEmpty(encryptedKey.getCarriedKeyName().getValue()) ) {
            return true;
        } else if (encryptedData.getKeyInfo() == null 
                || encryptedData.getKeyInfo().getKeyNames().isEmpty() ) {
            return false;
        }
        
        String keyCarriedKeyName = encryptedKey.getCarriedKeyName().getValue();
        List<String> dataKeyNames = KeyInfoSupport.getKeyNames(encryptedData.getKeyInfo());
        
        return dataKeyNames.contains(keyCarriedKeyName);
    }
    
    /**
     * Evaluate whether any of the EncryptedKey's DataReferences refer to the EncryptedData
     * context.
     * 
     * @param encryptedData the EncryptedData context
     * @param encryptedKey the candidate Encryptedkey to evaluate
     * @return true if any of the encrypted key's data references refer to the encrypted data context,
     *          false otherwise
     */
    protected boolean matchDataReference(@Nonnull final EncryptedData encryptedData,
            @Nonnull final EncryptedKey encryptedKey) {
        Constraint.isNotNull(encryptedData, "EncryptedData cannot be null");
        Constraint.isNotNull(encryptedKey, "EncryptedKey cannot be null");

        if (encryptedKey.getReferenceList() == null 
                || encryptedKey.getReferenceList().getDataReferences().isEmpty() ) {
            return true;
        } else if (Strings.isNullOrEmpty(encryptedData.getID())) {
            return false;
        }
        
        List<DataReference> drlist = encryptedKey.getReferenceList().getDataReferences();
        for (DataReference dr : drlist) {
            if (Strings.isNullOrEmpty(dr.getURI()) || !dr.getURI().startsWith("#") ) {
                continue;
            } else if (dr.resolveIDFromRoot(dr.getURI().substring(1)) == encryptedData) {
                return true;
            }
        }
        return false;
    }
}