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

package org.opensaml.soap.wsaddressing.messaging.context;

import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.messaging.context.BaseContext;

/**
 * A subcontext that carries information related to WS-Addressing processing.
 */
public class WSAddressingContext extends BaseContext {
    
    //TODO implement support for remaining items of WS-Addressing data model
    
    /** The Action URI value. */
    private String actionURI;
    
    /** The MessageID URI value. */
    private String messageIDURI;
    
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

}
