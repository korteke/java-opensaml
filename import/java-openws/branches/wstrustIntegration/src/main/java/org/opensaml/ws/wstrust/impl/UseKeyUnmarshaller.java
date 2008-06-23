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
package org.opensaml.ws.wstrust.impl;


import org.opensaml.ws.wstrust.UseKey;
import org.opensaml.xml.AbstractElementExtensibleXMLObjectUnmarshaller;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;
import org.w3c.dom.Attr;

/**
 * Unmarshaller for the &lt;wst:UseKey&gt; element.
 * 
 * @see UseKey
 * 
 */
public class UseKeyUnmarshaller extends
        AbstractElementExtensibleXMLObjectUnmarshaller {

    /**
     * Default constructor.
     * <p>
     * {@inheritDoc}
     */
    public UseKeyUnmarshaller() {
    }

    /**
     * Unmarshalls the &lt;@Sig&gt; attribute.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void processAttribute(XMLObject xmlObject, Attr attribute)
            throws UnmarshallingException {
        String attrName= attribute.getLocalName();
        if (UseKey.SIG_ATTR_LOCAL_NAME.equals(attrName)) {
            UseKey useKey= (UseKey) xmlObject;
            String sig= attribute.getValue();
            useKey.setSig(sig);
        }
        else {
            super.processAttribute(xmlObject, attribute);
        }
    }
}
