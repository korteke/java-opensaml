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

package org.opensaml.core.xml.io;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.w3c.dom.Attr;

/**
 * Base class for {@link Unmarshaller} classes.
 * 
 * This base class provides no-op implementations of the methods {@link #processAttribute(XMLObject, Attr)},
 * {@link #processChildElement(XMLObject, XMLObject)}, and {@link #processElementContent(XMLObject, String)}.
 * Developers extending this class may need to override one or both of these.
 */
public abstract class BaseXMLObjectUnmarshaller extends AbstractXMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {

    }

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentXMLObject,
            @Nonnull final XMLObject childXMLObject) throws UnmarshallingException {

    }

    /** {@inheritDoc} */
    @Override
    protected void processElementContent(@Nonnull final XMLObject xmlObject, @Nonnull final String elementContent) {

    }
}