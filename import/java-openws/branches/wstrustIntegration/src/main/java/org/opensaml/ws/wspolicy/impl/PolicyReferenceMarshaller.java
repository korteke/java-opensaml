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

import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.opensaml.ws.wspolicy.PolicyReference;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Marshaller for the &lt;wsp:PolicyReference&gt; element.
 * 
 * @see PolicyReference
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class PolicyReferenceMarshaller extends AbstractWSPolicyObjectMarshaller {

    /**
     * Marshalls the <code>URI</code>, the <code>Digest</code>, the <code>DigestAlgoritm</code> and the
     * <code>xs:anyAttribute</code> attributes.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void marshallAttributes(XMLObject xmlObject, Element domElement) throws MarshallingException {
        Document document = domElement.getOwnerDocument();
        PolicyReference policyReference = (PolicyReference) xmlObject;
        String uri = policyReference.getURI();
        if (uri != null) {
            Attr attribute = XMLHelper.constructAttribute(document, PolicyReference.URI_ATTR_NAME);
            attribute.setValue(uri);
            domElement.setAttributeNodeNS(attribute);
        }
        String digest = policyReference.getDigest();
        if (digest != null) {
            Attr attribute = XMLHelper.constructAttribute(document, PolicyReference.DIGEST_ATTR_NAME);
            attribute.setValue(digest);
            domElement.setAttributeNodeNS(attribute);
        }
        String digestAlgoritm = policyReference.getDigestAlgorithm();
        if (digestAlgoritm != null) {
            Attr attribute = XMLHelper.constructAttribute(document, PolicyReference.DIGEST_ALGORITHM_ATTR_NAME);
            attribute.setValue(digestAlgoritm);
            domElement.setAttributeNodeNS(attribute);
        }
        // xs:anyAttribute
        Attr attribute;
        for (Entry<QName, String> entry : policyReference.getUnknownAttributes().entrySet()) {
            attribute = XMLHelper.constructAttribute(document, entry.getKey());
            attribute.setValue(entry.getValue());
            domElement.setAttributeNodeNS(attribute);
            if (Configuration.isIDAttribute(entry.getKey())
                    || policyReference.getUnknownAttributes().isIDAttribute(entry.getKey())) {
                attribute.getOwnerElement().setIdAttributeNode(attribute, true);
            }
        }

        super.marshallAttributes(xmlObject, domElement);
    }
}
