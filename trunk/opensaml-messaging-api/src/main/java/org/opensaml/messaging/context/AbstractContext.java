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
        id = UUID.randomUUID().toString();
        creationTime = new DateTime();
        autoCreateSubcontexts = true;
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
        setParent(newParent);
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
        setParent(newParent);
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
        Context oldParent = parent;
        parent = newParent;
        
        if (parent != null) {
            log.trace("Setting context with type '{}' with id '{}' as parent of context with type '{}' with id '{}'", 
                    new String[]{parent.getClass().getName(), parent.getId(), this.getClass().getName(), this.getId()});
            Context currentChild = parent.getSubcontext(this.getClass());
            if (currentChild == null) {
                log.trace("Adding context to parent");
                parent.addSubcontext(this);
            } else if (currentChild == this) {
                log.trace("Context was already in parent's subcontext collection");
            } else {
                // TODO: not sure what to do here, perhaps either throw exception or else do a force add.
               log.warn("A different instance of type {} was a child of parent context, could not add this instance");
            }
        } else {
           log.trace("New parent context was null for context '{}' with id '{}'", this.getClass().getName(), this.getId());
        }
        
        if (oldParent != null) {
            log.trace("Removing context from old parent");
            oldParent.removeSubcontext(this);
        }
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
    public void addSubcontext(Context subContext, boolean replace) {
        subcontexts.add(subContext, replace);
    }
    
    /** {@inheritDoc} */
    public void removeSubcontext(Context subcontext) {
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
        return subcontexts.iterator();
    }
    
    /** {@inheritDoc} */
    public <T extends Context> boolean containsSubcontext(Class<T> clazz) {
        return subcontexts.contains(clazz);
    }
    
    /** {@inheritDoc} */
    public void clearSubcontexts() {
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
    
}
