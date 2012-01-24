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
import net.shibboleth.utilities.java.support.logic.Assert;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.joda.time.DateTime;
import org.opensaml.messaging.MessageRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Abstract implementation of {@link Context}.
 */
public abstract class AbstractContext implements Context {

    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(AbstractContext.class);
    
    /** The owning parent context. */
    private Context parent;

    /** The subcontexts being managed. */
    private ClassIndexedSet<Context> subcontexts;
    
    /** The context id. */
    private String id;
    
    /** The context creation time. */
    private DateTime creationTime;
    
    /** Flag indicating whether subcontexts should, by default, be created if they do not exist. */
    private boolean autoCreateSubcontexts;
    
    /** Constructor. Generates a random context id. */
    public AbstractContext() {
        subcontexts = new ClassIndexedSet<Context>();
        creationTime = new DateTime();
        
        setAutoCreateSubcontexts(false);
        setId(UUID.randomUUID().toString());
    }
    
    /**
     * Constructor.
     * 
     * @param contextId ID for this context, not null nor empty
     */
    public AbstractContext(final String contextId) {
        this();
        setId(contextId);
    }
    
    /**
     * Constructor. Adds this context as a child of the given parent.
     * Generates a random context id.
     * 
     * @param newParent the owning parent context.
     */
    public AbstractContext(final Context newParent) {
        this();
        newParent.addSubcontext(this);
    }
    
    /**
     * Constructor.
     * 
     * @param contextId ID for this context, not null nor empty
     * @param newParent the owning parent context.
     */
    public AbstractContext(final String contextId, final Context newParent) {
        this();
        setId(contextId);
        newParent.addSubcontext(this);
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
    
    /** {@inheritDoc} */
    public DateTime getCreationTime() {
        return creationTime;
    }
    
    /** {@inheritDoc} */
    public Context getParent() {
        return parent;
    }
    
    /**
     * Set the context parent. 
     * 
     * @param newParent the new context parent
     */
    protected void setParent(final Context newParent) {
        parent = newParent;
    }
    
    /** {@inheritDoc} */
    public <T extends Context> T getSubcontext(Class<T> clazz) {
        return getSubcontext(clazz, isAutoCreateSubcontexts());
    }
    
    /** {@inheritDoc} */
    public <T extends Context> T getSubcontext(Class<T> clazz, boolean autocreate) {
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
    
    /** {@inheritDoc} */
    public void addSubcontext(Context subContext) {
        addSubcontext(subContext, false);
    }
    
    /** {@inheritDoc} */
    public void addSubcontext(Context subcontext, boolean replace) {
        Context existing = subcontexts.get(subcontext.getClass());
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
        Context oldParent = subcontext.getParent();
        if (oldParent != null && oldParent != this) {
            log.trace("New subcontext with type '{}' id '{}' is currently a subcontext of parent with type '{}' id '{}', removing it",
                    new String[]{subcontext.getClass().getName(), subcontext.getId(), 
                    oldParent.getClass().getName(), oldParent.getId(),});
            subcontext.getParent().removeSubcontext(subcontext);
        }
        
        // Set parent pointer of new subcontext to this instance
        if (subcontext instanceof AbstractContext) {
            log.trace("New subcontext with type '{}' id '{}' is set to have parent with type '{}' id '{}', removing it",
                    new String[]{subcontext.getClass().getName(), subcontext.getId(), 
                    this.getClass().getName(), this.getId(),});
            ((AbstractContext)subcontext).setParent(this);
        }
        
        // If we're replacing an existing subcontext (if class was a duplicate, will only get here if replace == true),
        // then clear out its parent pointer.
        if (existing != null) {
            if (existing instanceof AbstractContext) {
                log.trace("Old subcontext with type '{}' id '{}' will have parent cleared",
                        new String[]{existing.getClass().getName(), existing.getId(), });
                ((AbstractContext)existing).setParent(null);
            }
        }
        
    }
    
    /** {@inheritDoc} */
    public void removeSubcontext(Context subcontext) {
        log.trace("Removing subcontext with type '{}' id '{}' from parent with type '{}' id '{}'",
                new String[]{subcontext.getClass().getName(), subcontext.getId(), 
                this.getClass().getName(), this.getId(),});
        if (subcontext instanceof AbstractContext) {
            ((AbstractContext)subcontext).setParent(null);
        }
        subcontexts.remove(subcontext);
    }
    
    /** {@inheritDoc} */
    public <T extends Context>void removeSubcontext(Class<T> clazz) {
        Context subcontext = getSubcontext(clazz, false);
        if (subcontext != null) {
            removeSubcontext(subcontext);
        }
    }
    
    /** {@inheritDoc} */
    public Iterator<Context>  iterator() {
        return new ContextSetNoRemoveIteratorDecorator(subcontexts.iterator());
    }
    
    /** {@inheritDoc} */
    public <T extends Context> boolean containsSubcontext(Class<T> clazz) {
        return subcontexts.contains(clazz);
    }
    
    /** {@inheritDoc} */
    public void clearSubcontexts() {
        log.trace("Clearing all subcontexts from context with type '{}' id '{}'", this.getClass().getName(), this.getId());
        for (Context subcontext : subcontexts) {
            if (subcontext instanceof AbstractContext) {
                ((AbstractContext)subcontext).setParent(null);
            }
        }
        subcontexts.clear();
    }
    
    /** {@inheritDoc} */
    public boolean isAutoCreateSubcontexts() {
        return autoCreateSubcontexts;
    }
    
    /** {@inheritDoc} */
    public void setAutoCreateSubcontexts(boolean autoCreate) {
        autoCreateSubcontexts = autoCreate;
    }
    
    /**
     * Create an instance of the specified subcontext class.
     * 
     * @param <T> the type of subcontext
     * @param clazz the class of the subcontext instance to create
     * @return the new subcontext instance
     */
    protected <T extends Context> T createSubcontext(Class<T> clazz) {
        Constructor<T> constructor;
        try {
            constructor = clazz.getConstructor(new Class[] {Context.class});
            return constructor.newInstance(new Object[] { this });
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
    protected class ContextSetNoRemoveIteratorDecorator implements Iterator<Context> {
        
        /** The decorated iterator. */
        private Iterator<Context> wrappedIterator;
        
        /**
         * Constructor.
         *
         * @param iterator the iterator instance to decorator
         */
        protected ContextSetNoRemoveIteratorDecorator(Iterator<Context> iterator) {
            wrappedIterator = iterator;
        }

        /** {@inheritDoc} */
        public boolean hasNext() {
            return wrappedIterator.hasNext();
        }

        /** {@inheritDoc} */
        public Context next() {
            return wrappedIterator.next();
        }

        /** {@inheritDoc} */
        public void remove() {
            throw new UnsupportedOperationException("Removal of subcontexts via the iterator is unsupported");
        }
        
    }
    
}
