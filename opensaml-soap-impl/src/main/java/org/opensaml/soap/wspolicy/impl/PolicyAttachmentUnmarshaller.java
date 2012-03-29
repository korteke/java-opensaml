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

package org.opensaml.soap.wspolicy.impl;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.soap.wspolicy.AppliesTo;
import org.opensaml.soap.wspolicy.Policy;
import org.opensaml.soap.wspolicy.PolicyAttachment;
import org.opensaml.soap.wspolicy.PolicyReference;
import org.w3c.dom.Attr;

/**
 * Unmarshaller for PolicyAttachment.
 */
public class PolicyAttachmentUnmarshaller extends AbstractWSPolicyObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processAttribute(XMLObject xmlObject, Attr attribute) throws UnmarshallingException {
        PolicyAttachment pa = (PolicyAttachment) xmlObject;
        XMLObjectSupport.unmarshallToAttributeMap(pa.getUnknownAttributes(), attribute);
    }

    /** {@inheritDoc} */
    protected void processChildElement(XMLObject parentXMLObject, XMLObject childXMLObject)
            throws UnmarshallingException {
        PolicyAttachment pa = (PolicyAttachment) parentXMLObject;
        
        if (childXMLObject instanceof AppliesTo) {
            pa.setAppliesTo((AppliesTo) childXMLObject);
        } else if (childXMLObject instanceof Policy) {
            pa.getPolicies().add((Policy) childXMLObject);
        } else if (childXMLObject instanceof PolicyReference) {
            pa.getPolicyReferences().add((PolicyReference) childXMLObject);
        } else {
            pa.getUnknownXMLObjects().add(childXMLObject);
        }
    }

}
