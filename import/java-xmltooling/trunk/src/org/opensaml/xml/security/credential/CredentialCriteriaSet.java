/*
 * Copyright [2007] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.xml.security.credential;

import java.util.HashSet;


/**
 * This class holds instances of {@link CredentialCriteria} which are used by
 * a {@link CredentialResolver} to resolve {@link Credential}'s.
 */
public class CredentialCriteriaSet  extends HashSet<CredentialCriteria> {
    
    /** Serial version UID. */
    private static final long serialVersionUID = -7386400782390910979L;
    
    /**
     * Constructor.
     *
     */
    public CredentialCriteriaSet() {
        super(6);
    }

    /**
     * A convenience constructor for constructing and adding a single criteria.
     *
     * @param criteria a single criteria 
     */
    public CredentialCriteriaSet(CredentialCriteria criteria) {
        super(1);
        add(criteria);
    }

    /** {@inheritDoc} */
    public boolean add(CredentialCriteria criteria) {
        return add(criteria, false);
    }
    
    /**
     * Add the specified criteria to the set, optionally replacing the existing type if it exists.
     * 
     * @param criteria  the criteria to add
     * @param replace flag indicating whether to replace an existing criteria type
     * @return true if criteria was added, false otherwise
     * @throws IllegalArgumentException thrown if an instance of the specified criteria
     *          already exists in the set, and replacement was not specified
     */
    public boolean add(CredentialCriteria criteria, boolean replace) throws IllegalArgumentException {
        if (criteria == null) {
            throw new NullPointerException("A null credential criteria is not allowed");
        }
        
        CredentialCriteria existing = getCriteria(criteria.getClass());
        if (existing != null) {
            if (replace) {
                this.remove(existing);
            } else {
                throw new IllegalArgumentException("CredentialCriteriaSet already contains a member of type: " 
                        + criteria.getClass().getName());
            }
        }
        return super.add(criteria);
    }
    
    /**
     * Check whether the set contains a criteria of the specified class.
     * 
     * @param clazz the class to check
     * @return true if an instance of the specified class if already stored, false otherwise
     */
    public boolean containsCriteria(Class<? extends CredentialCriteria> clazz) {
        return getCriteria(clazz)!=null;
    }
    
    
    /**
     * Get the criteria set member whose class corresponds to the one specified.
     * 
     * @param <T> the type of criteria being checked
     * @param clazz the class of the criteria being retrieved from the set
     * @return the retreived criteria member, or null if not found
     */
    @SuppressWarnings("unchecked")
    public <T extends CredentialCriteria> T getCriteria(Class<T> clazz) {
        // We could possibly use a map of class -> member to make lookups more efficient,
        // but that makes returning iterators, supporting removal, etc more complicated.
        // The number of criteria is likely to always be very small, so a linear 
        // search seems good enough.
        if (clazz == null) {
            return null;
        }
        for (CredentialCriteria criteria : this) {
           if (criteria.getClass().equals(clazz)) {
               return (T) criteria;
           }
        }
        return null;
    }
    
}
