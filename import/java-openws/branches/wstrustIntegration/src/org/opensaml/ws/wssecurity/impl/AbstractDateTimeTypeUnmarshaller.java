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
import org.opensaml.ws.wssecurity.DateTimeType;
import org.opensaml.xml.XMLObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AbstractDateTimeMarshaller
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision$
 */
public abstract class AbstractDateTimeTypeUnmarshaller extends
        AbstractWSSecurityObjectUnmarshaller {

    /** Logger */
    private final Logger log= LoggerFactory.getLogger(AbstractDateTimeTypeUnmarshaller.class);

    /**
     * Constructor.
     * <p>
     * {@inheritDoc}
     */
    protected AbstractDateTimeTypeUnmarshaller(String targetNamespaceURI,
            String targetLocalName) {
        super(targetNamespaceURI, targetLocalName);
    }

    /**
     * Unmarshalls the date time element content.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void processElementContent(XMLObject xmlObject,
            String elementContent) {
        if (log.isDebugEnabled()) {
            log.debug("parse DateTime {}", elementContent);
        }
        if (elementContent != null) {
            DateTimeType dateTimeObject= (DateTimeType) xmlObject;
            DateTime dateTime= AbstractDateTimeType.FORMATTER.parseDateTime(elementContent);
            dateTimeObject.setDateTime(dateTime);
        }
    }
}
