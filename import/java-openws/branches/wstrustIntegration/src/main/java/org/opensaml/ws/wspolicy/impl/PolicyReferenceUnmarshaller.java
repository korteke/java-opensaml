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

package org.opensaml.ws.wspolicy.impl;

import javax.xml.namespace.QName;

import org.opensaml.ws.wspolicy.PolicyReference;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Attr;

/**
 * Unmarshaller for the &lt;wsp:PolicyReference&gt; element.
 * 
 * @see PolicyReference
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class PolicyReferenceUnmarshaller extends AbstractWSPolicyObjectUnmarshaller {

    /**
     * Default constructor.
     * <p>
     * {@inheritDoc}
     */
    public PolicyReferenceUnmarshaller() {
        super();
    }

    /**
     * Unmarshalls the <code>URI</code>, the <code>Digest</code>, the <code>DigestAlgoritm</code> and the
     * <code>xs:anyAttribute</code> attributes.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void processAttribute(XMLObject xmlObject, Attr attribute) throws UnmarshallingException {
        PolicyReference policyReference = (PolicyReference) xmlObject;
        String attrName = attribute.getLocalName();
        if (PolicyReference.URI_ATTR_LOCAL_NAME.equals(attrName)) {
            String value = attribute.getValue();
            policyReference.setURI(value);
        } else if (PolicyReference.DIGEST_ATTR_LOCAL_NAME.equals(attrName)) {
            String value = attribute.getValue();
            policyReference.setDigest(value);
        } else if (PolicyReference.DIGEST_ALGORITHM_ATTR_LOCAL_NAME.equals(attrName)) {
            String value = attribute.getValue();
            policyReference.setDigestAlgorithm(value);
        }
        // xs:anyAttribute
        else {
            QName attribQName = XMLHelper.constructQName(attribute.getNamespaceURI(), attribute.getLocalName(),
                    attribute.getPrefix());
            if (attribute.isId()) {
                policyReference.getUnknownAttributes().registerID(attribQName);
            }
            policyReference.getUnknownAttributes().put(attribQName, attribute.getValue());
        }
    }

}
