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

package org.opensaml.saml.saml1.profile;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.saml.saml1.core.NameIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A helper class for working with SAMLObjects.
 */
public final class SAML1ObjectSupport {
    
    /** Constructor. */
    private SAML1ObjectSupport() {
        
    }
    
    /**
     * Return true iff the two input {@link NameIdentifier} formats are equivalent for SAML 1.x
     * purposes.
     * 
     * @param format1   first format to check
     * @param format2   second format to check
     * @return  true iff the two format values should be viewed as equivalent
     */
    public static boolean areNameIdentifierFormatsEquivalent(@Nullable final String format1,
            @Nullable final String format2) {
        
        return Objects.equals(
                format1 != null ? format1 : NameIdentifier.UNSPECIFIED,
                format2 != null ? format2 : NameIdentifier.UNSPECIFIED);
    }
    
    /**
     * Return true iff the two input {@link NameIdentifier} objects are equivalent for SAML 1.x purposes.
     * 
     * @param name1   first NameIdentifier to check
     * @param name2   second NameIdentifier to check
     * @return  true iff the two values should be viewed as equivalent
     */
    public static boolean areNameIdentifiersEquivalent(@Nonnull final NameIdentifier name1,
            @Nonnull final NameIdentifier name2) {
        return areNameIdentifierFormatsEquivalent(name1.getFormat(), name2.getFormat())
                && Objects.equals(name1.getValue(), name2.getValue())
                && Objects.equals(name1.getNameQualifier(), name2.getNameQualifier());
    }
    
    /**
     * Get an SLF4J Logger.
     * 
     * @return a Logger instance
     */
    @Nonnull private static Logger getLogger() {
        return LoggerFactory.getLogger(SAML1ObjectSupport.class);
    }

}