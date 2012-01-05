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

package org.opensaml.saml2.metadata.impl;

import net.shibboleth.utilities.java.support.xml.XmlConstants;

import org.opensaml.common.impl.AbstractSAMLObjectUnmarshaller;
import org.opensaml.saml2.metadata.LocalizedName;
import org.opensaml.xml.LangBearing;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;
import org.w3c.dom.Attr;

/**
 * A thread-safe unmarshaller for {@link org.opensaml.saml2.metadata.LocalizedName} objects.
 */
public class LocalizedURIUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /**
     * {@inheritDoc}
     */
    protected void processAttribute(XMLObject samlObject, Attr attribute) throws UnmarshallingException {
        if (attribute.getLocalName().equals(LangBearing.XML_LANG_ATTR_LOCAL_NAME)
                && XmlConstants.XML_NS.equals(attribute.getNamespaceURI())) {
            LocalizedName name = (LocalizedName) samlObject;

            name.setXMLLang(attribute.getValue());
        }
    }

    /** {@inheritDoc} */
    protected void processElementContent(XMLObject samlObject, String elementContent) {
        LocalizedName name = (LocalizedName) samlObject;

        name.setValue(elementContent);
    }
}