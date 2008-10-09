/*
Copyright 2008 Members of the EGEE Collaboration.
Copyright 2008 University Corporation for Advanced Internet Development,
Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package org.opensaml.xacml.ctx.impl;

import org.opensaml.xacml.XACMLObjectBuilder;
import org.opensaml.xacml.ctx.StatusDetailType;
import org.opensaml.xml.AbstractXMLObjectBuilder;

/** Builder for {@link StatusDetailType} objects. */
public class StatusDetailTypeImplBuilder extends AbstractXMLObjectBuilder<StatusDetailType> implements
        XACMLObjectBuilder<StatusDetailType> {

    /** Constructor. */
    public StatusDetailTypeImplBuilder() {

    }

    /** {@inheritDoc} */
    public StatusDetailType buildObject() {
        return null;
    }

    /** {@inheritDoc} */
    public StatusDetailType buildObject(String namespaceURI, String localName, String namespacePrefix) {
        return new StatusDetailTypeImpl(namespaceURI, localName, namespacePrefix);
    }
}