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

package org.opensaml.saml.metadata.resolver.index;

import javax.annotation.Nonnull;

import com.google.common.base.MoreObjects;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

/**
 * A simple implementation of {@link of MetadataIndexKey} based on a single input string.
 */
public class SimpleStringMetadataIndexKey implements MetadataIndexKey {
    
    /** The indexed value. */
    private String value;
    
    /**
     * Constructor.
     *
     * @param newValue the string value to use as the index key
     */
    public SimpleStringMetadataIndexKey(@Nonnull final String newValue) {
        value = Constraint.isNotNull(StringSupport.trimOrNull(newValue), "String index value was null");
    }
    
    /**
     * Get the string value used as the index key.
     * 
     * @return the string value
     */
    @Nonnull public String getValue() {
        return value;
    }
    
    /** {@inheritDoc} */
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).addValue(value).toString();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof SimpleStringMetadataIndexKey) {
            return value.equals(((SimpleStringMetadataIndexKey) obj).value);
        }

        return false;
    }

}
