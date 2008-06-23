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

package org.opensaml.ws.wssecurity.impl;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.opensaml.ws.wssecurity.DateTimeType;

/**
 * AbstractDateTimeType
 * 
 */
public abstract class AbstractDateTimeType extends AbstractWSSecurityObject implements DateTimeType {

    /** DateTime formatter */
    static protected DateTimeFormatter FORMATTER = DateTimeFormat.forPattern(DateTimeType.DEFAULT_DATETIME_FORMAT);

    /** DateTime object */
    private DateTime dateTime_ = null;

    /** XSString content */
    private String value_ = null;

    /**
     * Constructor.
     * 
     * @param namespaceURI namespace of the element
     * @param elementLocalName name of the element
     * @param namespacePrefix namespace prefix of the element
     */
    public AbstractDateTimeType(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.DateTimeType#getDateTime()
     */
    public DateTime getDateTime() {
        return dateTime_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.DateTimeType#setDateTime(org.joda.time.DateTime)
     */
    public void setDateTime(DateTime dateTime) {
        dateTime_ = dateTime;
        String formattedDateTime = FORMATTER.print(dateTime_);
        value_ = prepareForAssignment(value_, formattedDateTime);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.xml.schema.XSString#getValue()
     */
    public String getValue() {
        return value_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.xml.schema.XSString#setValue(java.lang.String)
     */
    public void setValue(String newValue) {
        value_ = prepareForAssignment(value_, newValue);
        dateTime_ = FORMATTER.parseDateTime(newValue);
    }

}
