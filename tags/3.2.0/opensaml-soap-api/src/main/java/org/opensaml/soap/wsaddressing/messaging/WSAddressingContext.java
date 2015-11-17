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

package org.opensaml.soap.wsaddressing.messaging;

import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.messaging.context.BaseContext;

/**
 * A subcontext that carries information related to WS-Addressing processing.
 */
public class WSAddressingContext extends BaseContext {
    
    //TODO implement support for remaining items of WS-Addressing data model
    
    /** The Action URI value. */
    private String actionURI;
    
    /** The Fault Action URI value. */
    private String faultActionURI;
    
    /** The MessageID URI value. */
    private String messageIDURI;
    
    /** The RelatesTo URI value. */
    private String relatesToURI;
    
    /** The RelatesTo RelationshipType attribute value. */
    private String relatesToRelationshipType;
    
    /**
     * Get the Action URI value.
     * 
     * @return the action URI.
     */
    public String getActionURI() {
        return actionURI;
    }

    /**
     * Set the Action URI value.
     * 
     * @param uri the new Action URI value
     */
    public void setActionURI(String uri) {
        actionURI = StringSupport.trimOrNull(uri);
    }
    
    /**
     * Get the Fault Action URI value.
     * 
     * @return the fault action URI.
     */
    public String getFaultActionURI() {
        return faultActionURI;
    }

    /**
     * Set the Fault Action URI value.
     * 
     * @param uri the new Fault Action URI value
     */
    public void setFaultActionURI(String uri) {
        faultActionURI = StringSupport.trimOrNull(uri);
    }
    
    /**
     * Get the MessageID URI value.
     * 
     * @return the MessageID URI
     */
    public String getMessageIDURI() {
        return messageIDURI;
    }

    /**
     * Set the MessageID URI value.
     * 
     * @param uri the new MessageID URI value
     */
    public void setMessageIDURI(String uri) {
        messageIDURI = StringSupport.trimOrNull(uri);
    }

    /**
     * Get the RelatesTo URI value.
     * 
     * @return the RelatesTo URI
     */
    public String getRelatesToURI() {
        return relatesToURI;
    }

    /**
     * Set the RelatesTo URI value.
     * 
     * @param uri the RelatesTo URI value
     */
    public void setRelatesToURI(String uri) {
        relatesToURI = StringSupport.trimOrNull(uri);
    }

    /**
     * Get the RelatesTo RelationshipType attribute value.
     * 
     * @return the RelatesTo RelationshipType attribute value
     */
    public String getRelatesToRelationshipType() {
        return relatesToRelationshipType;
    }

    /**
     * Get the RelatesTo RelationshipType attribute value.
     * 
     * @param value the RelatesTo RelationshipType attribute value
     */
    public void setRelatesToRelationshipType(String value) {
        relatesToRelationshipType = StringSupport.trimOrNull(value);
    }

}
