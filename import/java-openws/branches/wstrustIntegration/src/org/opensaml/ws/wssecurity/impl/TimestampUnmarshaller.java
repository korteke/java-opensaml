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


import org.opensaml.ws.wssecurity.AttributedId;
import org.opensaml.ws.wssecurity.Created;
import org.opensaml.ws.wssecurity.Expires;
import org.opensaml.ws.wssecurity.Timestamp;
import org.opensaml.xml.AbstractExtensibleXMLObjectUnmarshaller;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;
import org.w3c.dom.Attr;

/**
 * TimestampUnmarshaller
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class TimestampUnmarshaller extends
        AbstractExtensibleXMLObjectUnmarshaller {

    /** Logging */
    // private final Logger log=
    // LoggerFactory.getLogger(TimestampUnmarshaller.class);
    /**
     * Default constructor.
     */
    public TimestampUnmarshaller() {
        super(Timestamp.ELEMENT_NAME.getNamespaceURI(),
              Timestamp.ELEMENT_NAME.getLocalPart());
    }

    /**
     * Unmarshalls the &lt;wsu:Created&gt; and the &lt;wsu:Expries&gt; child
     * elements.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void processChildElement(XMLObject parentXMLObject,
            XMLObject childXMLObject) throws UnmarshallingException {
        Timestamp timestamp= (Timestamp) parentXMLObject;
        if (childXMLObject instanceof Created) {
            Created created= (Created) childXMLObject;
            timestamp.setCreated(created);
        }
        else if (childXMLObject instanceof Expires) {
            Expires expires= (Expires) childXMLObject;
            timestamp.setExpires(expires);
        }
        else {
            // unmarshalls xs:any element
            super.processChildElement(parentXMLObject, childXMLObject);
        }
    }

    /**
     * Unmarshalls the &lt;wsu:Id&gt; attribute.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void processAttribute(XMLObject xmlObject, Attr attribute)
            throws UnmarshallingException {
        // log.debug("attribute {}", attribute.getLocalName());
        String attrName= attribute.getLocalName();
        if (AttributedId.ID_ATTR_LOCAL_NAME.equals(attrName)) {
            AttributedId attributedId= (AttributedId) xmlObject;
            String id= attribute.getValue();
            attributedId.setId(id);
        }
        else {
            // unmarshalls xs:anyAttribute
            super.processAttribute(xmlObject, attribute);
        }
    }

}
