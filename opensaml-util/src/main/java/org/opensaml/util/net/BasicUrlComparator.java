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

package org.opensaml.util.net;

import java.net.MalformedURLException;

import javax.annotation.Nullable;


/**
 * A basic implementation of {@link UriComparator} that compares
 * URL's by canonicalizing them as per {@link SimpleUrlCanonicalizer},
 * and then compares the resulting string representations for equality 
 * using String equals(). If {link {@link #isCaseInsensitive()} is true,
 * then the equality test is instead performed using String equalsIgnoreCase().
 */
public class BasicUrlComparator implements UriComparator {
    
    /** The case-insensitivity flag. */
    private boolean caseInsensitive;

    /**
     * Get the case-insensitivity flag value.
     * @return Returns the caseInsensitive.
     */
    public boolean isCaseInsensitive() {
        return caseInsensitive;
    }

    /**
     * Set the case-insensitivity flag value.
     * @param flag The caseInsensitive to set.
     */
    public void setCaseInsensitive(boolean flag) {
        caseInsensitive = flag;
    }

    /** {@inheritDoc}.*/
    public boolean compare(@Nullable final String uri1, @Nullable final String uri2) throws UriException {
        if (uri1 == null) {
            return uri2 == null;
        } else if (uri2 == null) {
            return uri1 == null;
        } else {
            String uri1Canon = null;
            
            try {
                uri1Canon = SimpleUrlCanonicalizer.canonicalize(uri1);
            } catch (MalformedURLException e) {
                throw new UriException("URI was invalid: " + uri1Canon);
            }
            
            String uri2Canon = null;
            try {
                uri2Canon = SimpleUrlCanonicalizer.canonicalize(uri2);
            } catch (MalformedURLException e) {
                throw new UriException("URI was invalid: " + uri2Canon);
            }
            
            if (isCaseInsensitive()) {
                return uri1Canon.equalsIgnoreCase(uri2Canon);
            } else {
                return uri1Canon.equals(uri2Canon);
            }
        }
    }

}
