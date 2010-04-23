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
package org.opensaml.messaging.context.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.opensaml.messaging.MessageRuntimeException;
import org.opensaml.messaging.context.Subcontext;
import org.opensaml.messaging.context.SubcontextContainer;
import org.opensaml.util.collections.ClassIndexedSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Abstract implementation of {@link SubcontextContainer}.
 */
public abstract class AbstractSubcontextContainer implements SubcontextContainer {
    
    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(AbstractSubcontextContainer.class);
    
    /** The subcontexts being managed. */
    private ClassIndexedSet<Subcontext> subcontexts;
    
    /** Flag indicating whether subcontexts should, by default, be created if they do not exist. */
    private boolean autoCreateSubcontexts;
    
    /** Constructor. */
    public AbstractSubcontextContainer() {
        subcontexts = new ClassIndexedSet<Subcontext>();
        autoCreateSubcontexts = true;
    }
    
    /** {@inheritDoc} */
    public <T extends Subcontext> T getSubcontext(Class<T> clazz) {
        return getSubcontext(clazz, isAutoCreateSubcontexts());
    }
    
    /** {@inheritDoc} */
    public <T extends Subcontext> T getSubcontext(Class<T> clazz, boolean autocreate) {
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
    public void addSubcontext(Subcontext subContext) {
        addSubcontext(subContext, false);
    }
    
    /** {@inheritDoc} */
    public void addSubcontext(Subcontext subContext, boolean replace) {
        subcontexts.add(subContext, replace);
    }
    
    /** {@inheritDoc} */
    public void removeSubcontext(Subcontext subcontext) {
        subcontexts.remove(subcontext);
    }
    
    /** {@inheritDoc} */
    public <T extends Subcontext>void removeSubcontext(Class<T> clazz) {
        Subcontext subcontext = getSubcontext(clazz, false);
        if (subcontext != null) {
            removeSubcontext(subcontext);
        }
    }
    
    /** {@inheritDoc} */
    public Iterator<Subcontext>  iterator() {
        return subcontexts.iterator();
    }
    
    /** {@inheritDoc} */
    public <T extends Subcontext> boolean containsSubcontext(Class<T> clazz) {
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
    protected <T extends Subcontext> T createSubcontext(Class<T> clazz) {
        Constructor<T> constructor;
        try {
            constructor = clazz.getConstructor(new Class[] {SubcontextContainer.class});
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
