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

package org.opensaml.saml.metadata.resolver.index.impl;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.codec.Base64Support;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.opensaml.saml.criterion.ArtifactSourceIDCriterion;
import org.opensaml.saml.metadata.resolver.index.MetadataIndex;
import org.opensaml.saml.metadata.resolver.index.MetadataIndexKey;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.security.crypto.JCAConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

/**
 * An implementation of {@link MetadataIndex} which indexes entities by their artifact SourceID values.
 */
public class ArtifactSourceIDMetadataIndex implements MetadataIndex {
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(ArtifactSourceIDMetadataIndex.class);
    
    /** {@inheritDoc} */
    @Nullable public Set<MetadataIndexKey> generateKeys(@Nonnull EntityDescriptor descriptor) {
        Constraint.isNotNull(descriptor, "EntityDescriptor was null");
        String entityID = StringSupport.trimOrNull(descriptor.getEntityID());
        if (entityID == null) {
            return null;
        }
        
        try {
            MessageDigest sha1Digester = MessageDigest.getInstance(JCAConstants.DIGEST_SHA1);
            byte[] sourceID = sha1Digester.digest(entityID.getBytes("UTF-8"));
            if (log.isTraceEnabled()) {
                log.trace("For entityID '{}' produced artifact SourceID index value '{}'", 
                        entityID, Base64Support.encode(sourceID, false));
            }
            return Collections.<MetadataIndexKey>singleton(new ArtifactSourceIDMetadataIndexKey(sourceID));
        } catch (NoSuchAlgorithmException e) {
            // SHA-1 should be supported in every JVM, so this should never happen.
            log.error("Digest algorithm '{}' was invalid for encoding artifact SourceID", JCAConstants.DIGEST_SHA1, e);
            return null;
        } catch (UnsupportedEncodingException e) {
            // UTF-8 should be supported in every JVM, this should never happen.
            log.error("UTF-8 was unsupported for encoding artifact SourceID!");
            return null;
        }
        
    }

    /** {@inheritDoc} */
    @Nullable public Set<MetadataIndexKey> generateKeys(@Nonnull CriteriaSet criteriaSet) {
        Constraint.isNotNull(criteriaSet, "CriteriaSet was null");
        ArtifactSourceIDCriterion sourceIDCrit = criteriaSet.get(ArtifactSourceIDCriterion.class);
        if (sourceIDCrit != null) {
            return Collections.<MetadataIndexKey>singleton(
                    new ArtifactSourceIDMetadataIndexKey(sourceIDCrit.getSourceID()));
        } else {
            return null;
        }
    }
    
    /**
     * An implementation of {@link MetadataIndexKey} representing a SAML artifact SourceID value.
     */
    protected static class ArtifactSourceIDMetadataIndexKey implements MetadataIndexKey {
        
        /** The SourceID value. */
        @Nonnull @NotEmpty private final byte[] sourceID;

        /**
         * Constructor.
         * 
         * @param newSourceID the artifact SourceID value
         */
        public ArtifactSourceIDMetadataIndexKey(@Nonnull @NotEmpty final byte[] newSourceID) {
            sourceID = Constraint.isNotNull(newSourceID, "SourceID cannot be null");
            Constraint.isGreaterThan(0, sourceID.length, "SourceID length must be greater than zero");
        }

        /**
         * Get the SourceID value.
         * 
         * @return the SourceID value
         */
        @Nonnull @NotEmpty public byte[] getSourceID() {
            return sourceID;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).addValue(Base64Support.encode(sourceID, false)).toString();
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return Arrays.hashCode(sourceID);
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj instanceof ArtifactSourceIDMetadataIndexKey) {
                return Arrays.equals(sourceID, ((ArtifactSourceIDMetadataIndexKey) obj).getSourceID());
            }

            return false;
        }
        
    }

}
