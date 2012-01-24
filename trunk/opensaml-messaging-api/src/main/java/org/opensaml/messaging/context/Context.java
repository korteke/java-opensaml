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

package org.opensaml.messaging.context;

import java.util.Iterator;

import net.shibboleth.utilities.java.support.component.IdentifiableComponent;

import org.joda.time.DateTime;

/**
 * Interface for a component which represents the context used to store state used for purposes related to messaging.
 * 
 * <p>
 * Specific implementations of contexts would normally add additional properties to the
 * context to represent the state that is to be stored by that particular context implementation.
 * </p>
 * 
 * <p>
 * A context may also function as a container of subcontexts.
 * Access to subcontexts is class-based.  The parent context may hold only
 * one instance of a given class at a given time.  This class-based indexing approach
 * is used to enforce type-safety over the subcontext instances returned from the parent context,
 * and avoids the need for casting.
 * </p>
 * 
 * <p>
 * When a subcontext is requested and it does not exist in the parent context, it may optionally be
 * auto-created.  In order to auto-created in this manner, the subcontext type
 * <strong>MUST</strong> have a single-arg constructor which takes a <code>Context</code>.
 * If the requested subcontext does not conform to this convention, auto-creation will fail.
 * </p>
 */
public interface Context extends IdentifiableComponent, Iterable<Context> {

    /**
     * Get the timestamp of the creation of the context.
     * 
     * @return the creation timestamp
     */
    public DateTime getCreationTime();
    
    /**
     * Get the parent context, if there is one.
     * 
     * @return the parent context or null 
     */
    public Context getParent();
    
    /**
     * Get a subcontext of the current context.
     * 
     * @param <T> the type of subcontext being operated on
     * @param clazz the class type to obtain
     * @return the held instance of the class, or null
     */
    public <T extends Context> T getSubcontext(Class<T> clazz);
    
    /**
     * Get a subcontext of the current context.
     * 
     * @param <T> the type of subcontext being operated on
     * @param clazz the class type to obtain
     * @param autocreate flag indicating whether the subcontext instance should be auto-created
     * @return the held instance of the class, or null
     */
    public <T extends Context> T getSubcontext(Class<T> clazz, boolean autocreate);
    
    /**
     * Add a subcontext to the current context.
     * 
     * @param subContext the subcontext to add
     */
    public void addSubcontext(Context subContext);
    
    /**
     * Add a subcontext to the current context.
     * 
     * @param subContext the subcontext to add
     * @param replace flag indicating whether to replace the existing instance of the subcontext if present
     * 
     */
    public void addSubcontext(Context subContext, boolean replace);
    
    /**
     * Remove a subcontext from the current context.
     * 
     * @param <T> the type of subcontext being operated on
     * @param subcontext the subcontext to remove
     */
    public <T extends Context> void removeSubcontext(Context subcontext);
    
    /**
     * Remove the subcontext from the current context which corresponds to the supplied class.
     * 
     * @param <T> the type of subcontext being operated on
     * @param clazz the subcontext class to remove
     */
    public <T extends Context> void removeSubcontext(Class<T> clazz);
    
    /**
     * Return whether the current context currently contains an instance of
     * the specified subcontext class.
     * 
     * @param <T> the type of subcontext being operated on
     * @param clazz the class to check
     * @return true if the current context contains an instance of the class, false otherwise
     */
    public <T extends Context> boolean containsSubcontext(Class<T> clazz);
    
    /**
     * Clear the subcontexts of the current context.
     */
    public void clearSubcontexts();
    
    /**
     * Get whether the context auto-creates subcontexts by default.
     * 
     * @return true if the context auto-creates subcontexts, false otherwise
     */
    public boolean isAutoCreateSubcontexts();
    
    /**
     * Set whether the context auto-creates subcontexts by default.
     * 
     * @param autoCreate whether the context should auto-create subcontexts
     */
    public void setAutoCreateSubcontexts(boolean autoCreate);
    
    /** {@inheritDoc} */
    public Iterator<Context>  iterator();

}