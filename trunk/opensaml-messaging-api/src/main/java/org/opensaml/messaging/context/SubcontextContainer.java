/*
 * Copyright 2010 University Corporation for Advanced Internet Development, Inc.
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
package org.opensaml.messaging.context;

import java.util.Iterator;

/**
 * A component which serves as a container of {@link Subcontext} instances.
 * 
 * <p>
 * Access to subcontexts through the container is class-based.  The container may hold only
 * one instance of a given class at a given time.  This class-based indexing approach
 * is used to enforce type-safety over the subcontext instances returned from the container,
 * and avoids the need for casting.
 * </p>
 * 
 * <p>
 * When a subcontext is requested and it does not exist in the container, it may optionally be
 * auto-created by the container.  In order to auto-created in this manner, the subcontext type
 * <strong>MUST</strong> have a single-arg constructor which takes a <code>SubcontextContainer</code>.
 * If the requested subcontext does not conform to this convention, auto-creation will fail.
 * </p>
 */
public interface SubcontextContainer extends Iterable<Subcontext> {
    
    /**
     * Get a subcontext from the container.
     * 
     * @param <T> the type of subcontext being operated on
     * @param clazz the class type to obtain
     * @return the held instance of the class, or null
     */
    public <T extends Subcontext> T getSubcontext(Class<T> clazz);
    
    /**
     * Get a subcontext from the container.
     * 
     * @param <T> the type of subcontext being operated on
     * @param clazz the class type to obtain
     * @param autocreate flag indicating whether the subcontext instance should be auto-created
     * @return the held instance of the class, or null
     */
    public <T extends Subcontext> T getSubcontext(Class<T> clazz, boolean autocreate);
    
    /**
     * Add a subcontext to the container.
     * 
     * @param subContext the subcontext to add
     */
    public void addSubcontext(Subcontext subContext);
    
    /**
     * Add a subcontext to the container.
     * 
     * @param subContext the subcontext to add
     * @param replace flag indicating whether to replace the existing instance of the subcontext if present
     * 
     */
    public void addSubcontext(Subcontext subContext, boolean replace);
    
    /**
     * Remove a subcontext from the container.
     * 
     * @param <T> the type of subcontext being operated on
     * @param subcontext the subcontext to remove
     */
    public <T extends Subcontext> void removeSubcontext(Subcontext subcontext);
    
    /**
     * Remove the subcontext from the container which corresponds to the supplied class.
     * 
     * @param <T> the type of subcontext being operated on
     * @param clazz the subcontext class to remove
     */
    public <T extends Subcontext> void removeSubcontext(Class<T> clazz);
    
    /**
     * Return whether the container currently contains an instance of
     * the specified subcontext class.
     * 
     * @param <T> the type of subcontext being operated on
     * @param clazz the class to check
     * @return true if the container contains an instance of the class, false otherwise
     */
    public <T extends Subcontext> boolean containsSubcontext(Class<T> clazz);
    
    /**
     * Clear the subcontext container.
     */
    public void clearSubcontexts();
    
    /**
     * Get whether the container auto-creates subcontexts by default.
     * 
     * @return true if the container auto-creates subcontexts, false otherwise
     */
    public boolean isAutoCreateSubcontexts();
    
    /**
     * Set whether the container auto-creates subcontexts by default.
     * 
     * @param autoCreate whether the container should auto-create subcontexts
     */
    public void setAutoCreateSubcontexts(boolean autoCreate);
    
    /** {@inheritDoc} */
    public Iterator<Subcontext>  iterator();
    
}
