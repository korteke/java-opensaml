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

package org.opensaml.log;

import org.apache.log4j.Logger;
import org.apache.log4j.or.ObjectRenderer;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Element;

/**
 * A Log4J renderer that converts {@link XMLObject}s into strings to be logged.
 */
public class XMLObjectRenderer implements ObjectRenderer {

    /** Class logger. */
    private static Logger log = Logger.getLogger(XMLObjectRenderer.class);

    /** {@inheritDoc} */
    public String doRender(Object arg0) {
        if (!(arg0 instanceof XMLObject)) {
            log.warn("Render only operates on XMLObjects not " + arg0.getClass().getName());
        }

        XMLObject xmlObject = (XMLObject) arg0;
        Marshaller marshaller = Configuration.getMarshallerFactory().getMarshaller(xmlObject);
        try {
            Element xml = marshaller.marshall(xmlObject);
            return XMLHelper.nodeToString(xml);
        } catch (MarshallingException e) {
            log.error("Unable to marshall xml object", e);
            return null;
        }
    }
}