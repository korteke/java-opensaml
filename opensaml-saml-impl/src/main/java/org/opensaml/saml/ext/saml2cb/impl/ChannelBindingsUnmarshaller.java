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

package org.opensaml.saml.ext.saml2cb.impl;

import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.xml.QNameSupport;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.schema.impl.XSBase64BinaryUnmarshaller;
import org.opensaml.saml.ext.saml2cb.ChannelBindings;
import org.w3c.dom.Attr;

/**
 * Unmarshaller for instances of {@link ChannelBindings}.
 */
public class ChannelBindingsUnmarshaller extends XSBase64BinaryUnmarshaller {

    /** {@inheritDoc} */
    protected void processAttribute(XMLObject xmlObject, Attr attribute) throws UnmarshallingException {
        ChannelBindings cb = (ChannelBindings) xmlObject;
        
        QName attrName = QNameSupport.getNodeQName(attribute);
        if (attribute.getLocalName().equals(ChannelBindings.TYPE_ATTRIB_NAME)) {
            cb.setType(attribute.getValue());
        } else if (ChannelBindings.SOAP11_MUST_UNDERSTAND_ATTR_NAME.equals(attrName)) {
            cb.setSOAP11MustUnderstand(XSBooleanValue.valueOf(attribute.getValue()));
        } else if (ChannelBindings.SOAP11_ACTOR_ATTR_NAME.equals(attrName)) {
            cb.setSOAP11Actor(attribute.getValue()); 
        } else {
            super.processAttribute(xmlObject, attribute);
        }       
    }

}