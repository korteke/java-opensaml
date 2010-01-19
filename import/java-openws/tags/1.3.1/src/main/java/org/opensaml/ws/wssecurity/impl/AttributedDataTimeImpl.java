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
import org.opensaml.ws.wssecurity.AttributedDateTime;
import org.opensaml.xml.util.AttributeMap;

/**
 * AbstractDateTimeType.
 * 
 */
public class AttributedDataTimeImpl extends AbstractWSSecurityObject implements AttributedDateTime {

    /** DateTime formatter. */
    private DateTimeFormatter formatter;

    /** DateTime object. */
    private DateTime dateTimeValue;

    /** String dateTime representation. */
    private String stringValue;
    
    /** wsu:id attribute value. */
    private String id;
    
    /** Wildcard attributes. */
    private AttributeMap unknownAttributes;

    /**
     * Constructor.
     * 
     * @param namespaceURI namespace of the element
     * @param elementLocalName name of the element
     * @param namespacePrefix namespace prefix of the element
     */
    public AttributedDataTimeImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        formatter = DateTimeFormat.forPattern(AttributedDateTime.DEFAULT_DATETIME_FORMAT);
        unknownAttributes = new AttributeMap(this);
    }

    /** {@inheritDoc} */
    public DateTime getDateTime() {
        return dateTimeValue;
    }

    /** {@inheritDoc} */
    public void setDateTime(DateTime newDateTime) {
        dateTimeValue = newDateTime;
        String formattedDateTime = formatter.print(dateTimeValue);
        stringValue = prepareForAssignment(stringValue, formattedDateTime);
    }

    /** {@inheritDoc} */
    public String getValue() {
        return stringValue;
    }

    /** {@inheritDoc} */
    public void setValue(String newValue) {
        stringValue = prepareForAssignment(stringValue, newValue);
        dateTimeValue = formatter.parseDateTime(stringValue);
    }

    /** {@inheritDoc} */
    public String getWSUId() {
        return id;
    }

    /** {@inheritDoc} */
    public void setWSUId(String newId) {
        String oldID = id;
        id = prepareForAssignment(id, newId);
        registerOwnID(oldID, id);
    }

    /** {@inheritDoc} */
    public AttributeMap getUnknownAttributes() {
        return unknownAttributes;
    }

}
