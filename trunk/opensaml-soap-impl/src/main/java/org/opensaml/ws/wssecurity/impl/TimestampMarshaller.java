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

package org.opensaml.ws.wssecurity.impl;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.soap.wssecurity.Timestamp;
import org.w3c.dom.Element;

import com.google.common.base.Strings;

/**
 * TimestampMarshaller.
 * 
 */
public class TimestampMarshaller extends AbstractWSSecurityObjectMarshaller {

    /** {@inheritDoc} */
    protected void marshallAttributes(XMLObject xmlObject, Element domElement) throws MarshallingException {
        Timestamp timestamp = (Timestamp) xmlObject;
        
        if (!Strings.isNullOrEmpty(timestamp.getWSUId())) {
            XMLObjectSupport.marshallAttribute(Timestamp.WSU_ID_ATTR_NAME, timestamp.getWSUId(), domElement, true);
        }
        
        XMLObjectSupport.marshallAttributeMap(timestamp.getUnknownAttributes(), domElement);
    }

}
