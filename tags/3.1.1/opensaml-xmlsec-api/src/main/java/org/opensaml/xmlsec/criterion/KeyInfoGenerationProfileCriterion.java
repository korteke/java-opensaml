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

package org.opensaml.xmlsec.criterion;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.resolver.Criterion;

/**
 * {@link Criterion} representing an a KeyInfo generation "profile" name used
 * when looking up a {@link org.opensaml.xmlsec.keyinfo.KeyInfoGeneratorManager} from a
 * {@link org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager}.
 */
public final class KeyInfoGenerationProfileCriterion implements Criterion {

    /** The KeyInfo generation "profile" name. */
    @Nonnull @NotEmpty private final String name;

    /**
     * Constructor.
     * 
     * @param profileName the name, can not be null or empty
     */
    public KeyInfoGenerationProfileCriterion(@Nonnull @NotEmpty final String profileName) {
        name = Constraint.isNotNull(StringSupport.trimOrNull(profileName), "Name cannot be null or empty");
    }

    /**
     * Gets the KeyInfo generation "profile" name.
     * 
     * @return the name, never null or empty
     */
    @Nonnull @NotEmpty public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("KeyInfoGenerationProfileCriterion [name=");
        builder.append(name);
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return name.hashCode();
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

        if (obj instanceof KeyInfoGenerationProfileCriterion) {
            return name.equals(((KeyInfoGenerationProfileCriterion) obj).getName());
        }

        return false;
    }
}