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

package org.opensaml.soap.messaging.context;

import java.util.Set;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.collection.LazySet;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.messaging.context.BaseContext;

/**
 * A subcontext holding information related to processing of an inbound SOAP message.
 */
public class InboundSOAPContext extends BaseContext {
    
    /** The set of actor URI's under which this SOAP node is operating. */
    private LazySet<String> nodeActors;
    
    /** Flag indicating whether the node is the final destination for the current 
     * message processing context. Defaults to: true*/
    private boolean finalDestination;
    
    /** The set of headers that have been understood. */
    private LazySet<XMLObject> understoodHeaders;
    
    /** Constructor. */
    public InboundSOAPContext() {
        super();
        nodeActors = new LazySet<String>();
        understoodHeaders = new LazySet<XMLObject>();
        finalDestination = true;
    }
    
    /** 
     * Get the (modifiable) set of actor URI's under which this SOAP node is operating.
     * 
     * @return the set of node actor URI's
     * */
    @Nonnull public Set<String> getNodeActors() {
        return nodeActors;
    }
    
    /** 
     * Get the (modifiable) set of headers which have been understood.
     * 
     * @return the set of node actor URI's
     * */
    @Nonnull public Set<XMLObject> getUnderstoodHeaders() {
        return understoodHeaders;
    }
    
    /**
     * Get the flag indicating whether the node is the final destination for the current 
     * message processing context.
     * 
     * <p>
     * Defaults to: true.
     * </p>
     * 
     * @return true if is the final destination, false otherwise
     * 
     */
    public boolean isFinalDestination() {
        return finalDestination;
    }
    
    /**
     * Set the flag indicating whether the node is the final destination for the current 
     * message processing context.
     * 
     * <p>
     * Defaults to: true.
     * </p>
     * 
     * @param newValue the new flag value
     * 
     */
    public void setFinalDestination(boolean newValue) {
        finalDestination = newValue;
    }

}
