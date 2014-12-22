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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.xml.XMLConstants;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.opensaml.core.xml.XMLObject;

/**
 * XMLObject that represents an XML Schema dateTime.
 */
public interface XSDateTime extends XMLObject {

    /** Local name of the XSI type. */
    public static final String TYPE_LOCAL_NAME = "dateTime"; 
        
    /** QName of the XSI type. */
    public static final QName TYPE_NAME = new QName(XMLConstants.XSD_NS, TYPE_LOCAL_NAME, XMLConstants.XSD_PREFIX);
    
    /**
     * Gets the dateTime value.
     * 
     * @return the dateTime value
     */
    @Nullable public DateTime getValue();
    
    /**
     * Sets the dateTime value.
     * 
     * @param newValue the dateTime value
     */
    public void setValue(@Nullable final DateTime newValue);
    
    /**
     * Get the {@link DateTimeFormatter} to be used when stringifying
     * the {@link DateTime} value.
     * 
     * <p>Defaults to the formatter constructed by calling:
     * <code>ISODateTimeFormat.dateTime().withChronology(org.joda.time.chrono.ISOChronology.getInstanceUTC())</code>
     * </p>
     * 
     * @return the currently configured formatter
     */
    @Nonnull public DateTimeFormatter getDateTimeFormatter();
    
    /**
     * Set the {@link DateTimeFormatter} to be used when stringifying
     * the {@link DateTime} value.
     * 
     * @param newFormatter the new formatter
     */
    public void setDateTimeFormatter(@Nonnull final DateTimeFormatter newFormatter);
}