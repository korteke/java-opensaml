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

package org.opensaml.saml.criterion;

import java.util.Arrays;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.codec.Base64Support;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.Criterion;

/** {@link Criterion} representing a SAML artifact SourceID. */
public final class ArtifactSourceIDCriterion implements Criterion {

    /** The SourceID value. */
    @Nonnull @NotEmpty private final byte[] sourceID;

    /**
     * Constructor.
     * 
     * @param newSourceID the artifact SourceID value
     */
    public ArtifactSourceIDCriterion(@Nonnull @NotEmpty final byte[] newSourceID) {
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
        StringBuilder builder = new StringBuilder();
        builder.append("ArtifactSourceIDCriterion [value=");
        builder.append(Base64Support.encode(sourceID, false));
        builder.append("]");
        return builder.toString();
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

        if (obj == null) {
            return false;
        }

        if (obj instanceof ArtifactSourceIDCriterion) {
            return Arrays.equals(sourceID, ((ArtifactSourceIDCriterion) obj).getSourceID());
        }

        return false;
    }
}