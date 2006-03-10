/*
 * Copyright [2005] [University Corporation for Advanced Internet Development, Inc.]
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

package org.opensaml.saml1.core.impl;

import org.apache.log4j.Logger;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml1.core.Action;
import org.opensaml.saml1.core.AuthorizationDecisionStatement;
import org.opensaml.saml1.core.DecisionType;
import org.opensaml.saml1.core.Evidence;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;
import org.w3c.dom.Attr;

/**
 * A thread-safe Unmarshaller for
 * {@link org.opensaml.saml1.core.impl.AuthorizationDecisionStatementImpl} objects.
 */
public class AuthorizationDecisionStatementUnmarshaller extends SubjectStatementUnmarshaller {

    /** Logger */
    private static Logger log = Logger.getLogger(AuthorizationDecisionStatementUnmarshaller.class);

    /**
     * Constructor
     */
    public AuthorizationDecisionStatementUnmarshaller() {
        super(SAMLConstants.SAML1_NS, AuthorizationDecisionStatement.LOCAL_NAME);
    }

    /*
     * @see org.opensaml.xml.io.AbstractXMLObjectUnmarshaller#processChildElement(org.opensaml.xml.XMLObject, org.opensaml.xml.XMLObject)
     */
    protected void processChildElement(XMLObject parentSAMLObject, XMLObject childSAMLObject)
            throws UnmarshallingException {

        AuthorizationDecisionStatement authorizationDecisionStatement;
        authorizationDecisionStatement = (AuthorizationDecisionStatement) parentSAMLObject;

        if (childSAMLObject instanceof Action) {
            authorizationDecisionStatement.getActions().add((Action) childSAMLObject);
        } else if (childSAMLObject instanceof Evidence) {
            authorizationDecisionStatement.setEvidence((Evidence) childSAMLObject);
        } else {
            super.processChildElement(parentSAMLObject, childSAMLObject);
        }
    }

    /*
     * @see org.opensaml.xml.io.AbstractXMLObjectUnmarshaller#processAttribute(org.opensaml.xml.XMLObject, org.w3c.dom.Attr)
     */
    protected void processAttribute(XMLObject samlObject, Attr attribute)
            throws UnmarshallingException {

        AuthorizationDecisionStatement authorizationDecisionStatement;
        authorizationDecisionStatement = (AuthorizationDecisionStatement) samlObject;

        if (AuthorizationDecisionStatement.DECISION_ATTRIB_NAME.equals(attribute.getLocalName())) {
            String value = attribute.getValue();
            if (value.equals(DecisionType.PERMIT.toString())) {
                authorizationDecisionStatement.setDecision(DecisionType.PERMIT);
            } else if (value.equals(DecisionType.DENY.toString())) {
                authorizationDecisionStatement.setDecision(DecisionType.DENY);
            } else  if (value.equals(DecisionType.INDETERMINATE.toString())) {
                authorizationDecisionStatement.setDecision(DecisionType.INDETERMINATE);
            }  else {
                log.error("Unknown value for DecisionType '" + value + "'");
                throw new UnmarshallingException("Unknown value for DecisionType '" + value + "'");
            }
        } else if (AuthorizationDecisionStatement.RESOURCE_ATTRIB_NAME.equals(attribute.getLocalName())) {
            authorizationDecisionStatement.setResource(attribute.getValue());
        } else {
            super.processAttribute(samlObject, attribute);
        }
    }
}