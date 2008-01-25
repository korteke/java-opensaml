/*
 * Copyright 2008 Members of the EGEE Collaboration.
 * Copyright 2008 University Corporation for Advanced Internet Development, Inc.
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

package org.opensaml.xacml.policy.impl;

import org.opensaml.xacml.XACMLConstants;
import org.opensaml.xacml.policy.SubjectMatchType;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.AbstractXMLObjectMarshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.util.DatatypeHelper;
import org.w3c.dom.Element;

/** Marshaller of {@link SubjectMatchType} objects. */
public class SubjectMatchTypeMarshaller extends AbstractXMLObjectMarshaller {

    /** Constructor. */
    public SubjectMatchTypeMarshaller() {
        super(XACMLConstants.XACML20_NS, SubjectMatchType.DEFAULT_ELEMENT_LOCAL_NAME);
    }

    /** {@inheritDoc} */
    protected void marshallAttributes(XMLObject xmlObject, Element domElement) throws MarshallingException {
        SubjectMatchType matchType = (SubjectMatchType) xmlObject;

        if (!DatatypeHelper.isEmpty(matchType.getMatchId())) {
            domElement.setAttribute(SubjectMatchType.MATCH_ID_ATTRIB_NAME, matchType.getMatchId());
        }
    }

    /** {@inheritDoc} */
    protected void marshallElementContent(XMLObject xmlObject, Element domElement) throws MarshallingException {

    }
}