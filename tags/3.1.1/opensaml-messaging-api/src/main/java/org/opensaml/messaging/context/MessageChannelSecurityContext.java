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

/**
 * A subcontext which carries information about whether the message channel in use is considered
 * to have the active properties of confidentiality and integrity.
 * 
 * <p>
 * This data is typically used to determine whether additional application- or protocol-specific signing
 * and/or encryption operations are necessary.
 * </p>
 */
public class MessageChannelSecurityContext extends BaseContext {
    
    /** Message channel confidentiality flag. */
    private boolean confidentialityActive;
    
    /** Message channel integrity flag. */
    private boolean integrityActive;

    /**
     * Get whether message channel confidentiality is active.
     * 
     * @return Returns the confidentialityActive.
     */
    public boolean isConfidentialityActive() {
        return confidentialityActive;
    }

    /**
     * Set whether message channel confidentiality is active.
     * 
     * @param flag The confidentialityActive to set.
     */
    public void setConfidentialityActive(boolean flag) {
        confidentialityActive = flag;
    }

    /**
     * Get whether message channel integrity is active.
     * 
     * @return Returns the integrityActive.
     */
    public boolean isIntegrityActive() {
        return integrityActive;
    }

    /**
     * Set whether message channel integrity is active.
     * 
     * @param flag The integrityActive to set.
     */
    public void setIntegrityActive(boolean flag) {
        integrityActive = flag;
    }

}
