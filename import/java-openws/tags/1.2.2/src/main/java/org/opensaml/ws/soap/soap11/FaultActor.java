/*
 * Copyright [2006] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.ws.soap.soap11;

import javax.xml.namespace.QName;

import org.opensaml.ws.soap.common.SOAPObject;
import org.opensaml.ws.soap.util.SOAPConstants;
import org.opensaml.xml.schema.XSURI;

/**
 * SOAP 1.1 faultactor.
 */
public interface FaultActor extends SOAPObject, XSURI {

    /** Element local name. */
    public static final String DEFAULT_ELEMENT_LOCAL_NAME = "faultactor";
    
    /** Default element name. */
    public static final QName DEFAULT_ELEMENT_NAME = new QName(DEFAULT_ELEMENT_LOCAL_NAME);
}
