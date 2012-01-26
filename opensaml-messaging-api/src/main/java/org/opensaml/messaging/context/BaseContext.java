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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.UUID;

import net.shibboleth.utilities.java.support.collection.ClassIndexedSet;
import net.shibboleth.utilities.java.support.component.IdentifiableComponent;
import net.shibboleth.utilities.java.support.logic.Assert;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.joda.time.DateTime;
import org.opensaml.messaging.MessageRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base implementation of a component which represents the context used to store state 
 * used for purposes related to messaging.
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
 * auto-created.  In order to be auto-created in this manner, the subcontext type
 * <strong>MUST</strong> have a no-arg constructor. If the requested subcontext does not conform 
 * to this convention, auto-creation will fail.
 * </p>
 */
public abstract class BaseContext implements IdentifiableComponent, Iterable<BaseContext> {

    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(BaseContext.class);
    
    /** The owning parent context. */
    private BaseContext parent;

    /** The subcontexts being managed. */
    private ClassIndexedSet<BaseContext> subcontexts;
    
    /** The context id. */
    private String id;
    
    /** The context creation time. */
    private DateTime creationTime;
    
    /** Flag indicating whether subcontexts should, by default, be created if they do not exist. */
    private boolean autoCreateSubcontexts;
    
    /** Constructor. Generates a random context id. */
    public BaseContext() {
        subcontexts = new ClassIndexedSet<BaseContext>();
        creationTime = new DateTime();
        
        setAutoCreateSubcontexts(false);
        setId(UUID.randomUUID().toString());
    }
    
    /** {@inheritDoc} */
    public String getId() {
        return id;
    }
    
    /**
     * Set the context id. 
     * 
     * @param newId the new context id
     */
    protected void setId(final String newId) {
        id = Assert.isNotNull(StringSupport.trimOrNull(newId), "Context ID can not be null or empty");
    }
    
    /**
     * Get the timestamp of the creation of the context.
     * 
     * @return the creation timestamp
     */
    public DateTime getCreationTime() {
        return creationTime;
    }
    
    /**
     * Get the parent context, if there is one.
     * 
     * @return the parent context or null 
     */
    public BaseContext getParent() {
        return parent;
    }
    
    /**
     * Set the context parent. 
     * 
     * @param newParent the new context parent
     */
    protected void setParent(final BaseContext newParent) {
        parent = newParent;
    }
    
    /**
     * Get a subcontext of the current context.
     * 
     * @param <T> the type of subcontext being operated on
     * @param clazz the class type to obtain
     * @return the held instance of the class, or null
     */
    public <T extends BaseContext> T getSubcontext(Class<T> clazz) {
        return getSubcontext(clazz, isAutoCreateSubcontexts());
    }
    
    /**
     * Get a subcontext of the current context.
     * 
     * @param <T> the type of subcontext being operated on
     * @param clazz the class type to obtain
     * @param autocreate flag indicating whether the subcontext instance should be auto-created
     * @return the held instance of the class, or null
     */ 
    public <T extends BaseContext> T getSubcontext(Class<T> clazz, boolean autocreate) {
        log.trace("Request for subcontext of type: {}", clazz.getName());
        T subcontext = subcontexts.get(clazz);
        if (subcontext != null) {
            log.trace("Subcontext found of type: {}", clazz.getName());
            return subcontext;
        }
        
        if (autocreate) {
            log.trace("Subcontext not found of type, autocreating: {}", clazz.getName());
            subcontext = createSubcontext(clazz);
            addSubcontext(subcontext);
            return subcontext;
        }
        
        log.trace("Subcontext not found of type: {}", clazz.getName());
        return null;
    }
    
    /**
     * Add a subcontext to the current context.
     * 
     * @param subContext the subcontext to add
     */
    public void addSubcontext(BaseContext subContext) {
        addSubcontext(subContext, false);
    }
    
    /**
     * Add a subcontext to the current context.
     * 
     * @param subContext the subcontext to add
     * @param replace flag indicating whether to replace the existing instance of the subcontext if present
     * 
     */
    public void addSubcontext(BaseContext subcontext, boolean replace) {
        BaseContext existing = subcontexts.get(subcontext.getClass());
        if (existing == subcontext) {
            log.trace("Subcontext to add was already a child of the current context, skipping");
            return;
        }
        
        // Note: This will throw if replace == false and existing != null.
        // In that case, no link management happens, which is what we want, to leave things in a consistent state.
        log.trace("Attempting to store a subcontext with type '{}' id '{}' with replace option '{}'", 
                new String[]{subcontext.getClass().getName(), subcontext.getId(), new Boolean(replace).toString()});
        subcontexts.add(subcontext, replace);
        
        // Manage parent/child links
        
        // If subcontext was formerly a child of another parent, remove that link
        BaseContext oldParent = subcontext.getParent();
        if (oldParent != null && oldParent != this) {
            log.trace("New subcontext with type '{}' id '{}' is currently a subcontext of parent with type '{}' id '{}', removing it",
                    new String[]{subcontext.getClass().getName(), subcontext.getId(), 
                    oldParent.getClass().getName(), oldParent.getId(),});
            subcontext.getParent().removeSubcontext(subcontext);
        }
        
        // Set parent pointer of new subcontext to this instance
        log.trace("New subcontext with type '{}' id '{}' is set to have parent with type '{}' id '{}', removing it",
                new String[]{subcontext.getClass().getName(), subcontext.getId(), 
                this.getClass().getName(), this.getId(),});
        subcontext.setParent(this);
        
        // If we're replacing an existing subcontext (if class was a duplicate, will only get here if replace == true),
        // then clear out its parent pointer.
        if (existing != null) {
            log.trace("Old subcontext with type '{}' id '{}' will have parent cleared",
                    new String[]{existing.getClass().getName(), existing.getId(), });
            existing.setParent(null);
        }
        
    }
    
    /**
     * Remove a subcontext from the current context.
     * 
     * @param <T> the type of subcontext being operated on
     * @param subcontext the subcontext to remove
     */
    public void removeSubcontext(BaseContext subcontext) {
        log.trace("Removing subcontext with type '{}' id '{}' from parent with type '{}' id '{}'",
                new String[]{subcontext.getClass().getName(), subcontext.getId(), 
                this.getClass().getName(), this.getId(),});
        subcontext.setParent(null);
        subcontexts.remove(subcontext);
    }
    
    /**
     * Remove the subcontext from the current context which corresponds to the supplied class.
     * 
     * @param <T> the type of subcontext being operated on
     * @param clazz the subcontext class to remove
     */
    public <T extends BaseContext>void removeSubcontext(Class<T> clazz) {
        BaseContext subcontext = getSubcontext(clazz, false);
        if (subcontext != null) {
            removeSubcontext(subcontext);
        }
    }
    
    /**
     * Return whether the current context currently contains an instance of
     * the specified subcontext class.
     * 
     * @param <T> the type of subcontext being operated on
     * @param clazz the class to check
     * @return true if the current context contains an instance of the class, false otherwise
     */
    public <T extends BaseContext> boolean containsSubcontext(Class<T> clazz) {
        return subcontexts.contains(clazz);
    }
    
    /**
     * Clear the subcontexts of the current context.
     */
    public void clearSubcontexts() {
        log.trace("Clearing all subcontexts from context with type '{}' id '{}'", this.getClass().getName(), this.getId());
        for (BaseContext subcontext : subcontexts) {
            subcontext.setParent(null);
        }
        subcontexts.clear();
    }
    
    /**
     * Get whether the context auto-creates subcontexts by default.
     * 
     * @return true if the context auto-creates subcontexts, false otherwise
     */
    public boolean isAutoCreateSubcontexts() {
        return autoCreateSubcontexts;
    }
    
    /**
     * Set whether the context auto-creates subcontexts by default.
     * 
     * @param autoCreate whether the context should auto-create subcontexts
     */
    public void setAutoCreateSubcontexts(boolean autoCreate) {
        autoCreateSubcontexts = autoCreate;
    }
    
    /** {@inheritDoc} */
    public Iterator<BaseContext>  iterator() {
        return new ContextSetNoRemoveIteratorDecorator(subcontexts.iterator());
    }
    
    /**
     * Create an instance of the specified subcontext class.
     * 
     * @param <T> the type of subcontext
     * @param clazz the class of the subcontext instance to create
     * @return the new subcontext instance
     */
    protected <T extends BaseContext> T createSubcontext(Class<T> clazz) {
        Constructor<T> constructor;
        try {
            constructor = clazz.getConstructor();
            return constructor.newInstance();
        } catch (SecurityException e) {
            log.error("Security error on creating subcontext", e);
            throw new MessageRuntimeException("Error creating subcontext", e);
        } catch (NoSuchMethodException e) {
            log.error("No such method error on creating subcontext", e);
            throw new MessageRuntimeException("Error creating subcontext", e);
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument error on creating subcontext", e);
            throw new MessageRuntimeException("Error creating subcontext", e);
        } catch (InstantiationException e) {
            log.error("Instantiation error on creating subcontext", e);
            throw new MessageRuntimeException("Error creating subcontext", e);
        } catch (IllegalAccessException e) {
            log.error("Illegal access error on creating subcontext", e);
            throw new MessageRuntimeException("Error creating subcontext", e);
        } catch (InvocationTargetException e) {
            log.error("Invocation target error on creating subcontext", e);
            throw new MessageRuntimeException("Error creating subcontext", e);
        }
    }
    
    /**
     * Iterator decorator which disallows the remove() operation on the iterator.
     */
    protected class ContextSetNoRemoveIteratorDecorator implements Iterator<BaseContext> {
        
        /** The decorated iterator. */
        private Iterator<BaseContext> wrappedIterator;
        
        /**
         * Constructor.
         *
         * @param iterator the iterator instance to decorator
         */
        protected ContextSetNoRemoveIteratorDecorator(Iterator<BaseContext> iterator) {
            wrappedIterator = iterator;
        }

        /** {@inheritDoc} */
        public boolean hasNext() {
            return wrappedIterator.hasNext();
        }

        /** {@inheritDoc} */
        public BaseContext next() {
            return wrappedIterator.next();
        }

        /** {@inheritDoc} */
        public void remove() {
            throw new UnsupportedOperationException("Removal of subcontexts via the iterator is unsupported");
        }
        
    }
    
}
