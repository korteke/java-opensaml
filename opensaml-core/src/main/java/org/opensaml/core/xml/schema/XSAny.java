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

package org.opensaml.core.xml.schema;

import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.xml.XmlConstants;

import org.opensaml.core.xml.AttributeExtensibleXMLObject;
import org.opensaml.core.xml.ElementExtensibleXMLObject;

/**
 * Represents the schema Any type.
 */
public interface XSAny extends ElementExtensibleXMLObject, AttributeExtensibleXMLObject {

    /** Local name of the XSI type. */
    public static final String TYPE_LOCAL_NAME = "anyType"; 
        
    /** QName of the XSI type. */
    public static final QName TYPE_NAME = new QName(XmlConstants.XSD_NS, TYPE_LOCAL_NAME, XmlConstants.XSD_PREFIX);
    
    /**
     * Gets the text content for the DOM Element.
     * 
     * @return the text content for the DOM Element, or null
     */
    @Nullable public String getTextContent();

    /**
     * Sets the text content for the DOM Element.
     * 
     * @param newContent the text content for the DOM Element
     */
    public void setTextContent(@Nullable final String newContent);
}