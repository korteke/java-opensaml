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

package org.opensaml.saml.common.messaging.context;

/**
 * Abstract base class for subcontexts that carry information about a SAML entity which may be authenticated.
 * This context will often contain subcontexts, whose data is construed to be scoped to that entity.
 */
public abstract class AbstractAuthenticatableSAMLEntityContext extends AbstractSAMLEntityContext {
    
    /** Flag indicating whether the SAML peer entity has been authenticated. */
    private boolean authenticated;

    /**
     * Gets the flag indicating whether the SAML peer entity has been authenticated.
     * 
     * @return Returns the authenticated flag.
     */
    public boolean isAuthenticated() {
        return authenticated;
    }

    /**
     * Sets the flag indicating whether the SAML peer entity has been authenticated.
     * 
     * @param flag The flag to set.
     */
    public void setAuthenticated(final boolean flag) {
        authenticated = flag;
    }

}