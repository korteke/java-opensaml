/*
 * Copyright 2008 Members of the EGEE Collaboration.
 * Copyright 2008 University Corporation for Advanced Internet Development, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opensaml.ws.wspolicy.impl;

import org.opensaml.ws.wspolicy.AppliesTo;

/**
 * Builder for the AppliesTo element.
 * 
 * @see AppliesTo
 * 
 */
public class AppliesToBuilder extends AbstractWSPolicyObjectBuilder<AppliesTo> {

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wspolicy.impl.AbstactWSPolicyObjectBuilder#buildObject()
     */
    @Override
    public AppliesTo buildObject() {
        return buildObject(AppliesTo.ELEMENT_NAME);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.xml.AbstractXMLObjectBuilder#buildObject(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @Override
    public AppliesTo buildObject(String namespaceURI, String localName,
            String namespacePrefix) {
        return new AppliesToImpl(namespaceURI, localName, namespacePrefix);
    }

}
