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

package org.opensaml.saml.saml1.core.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.saml.common.AbstractSignableSAMLObject;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml1.core.Advice;
import org.opensaml.saml.saml1.core.Assertion;
import org.opensaml.saml.saml1.core.AttributeStatement;
import org.opensaml.saml.saml1.core.AuthenticationStatement;
import org.opensaml.saml.saml1.core.AuthorizationDecisionStatement;
import org.opensaml.saml.saml1.core.Conditions;
import org.opensaml.saml.saml1.core.Statement;
import org.opensaml.saml.saml1.core.SubjectStatement;

/**
 * This class implements the SAML 1 <code> Assertion </code> statement.
 */
public class AssertionImpl extends AbstractSignableSAMLObject implements Assertion {

    /** The <code> AssertionID </code> attrribute. */
    private String id;
    
    /** SAML version of this assertion. */
    private SAMLVersion version;
    
    /** Object version of the <code> Issuer </code> attribute. */
    private String issuer;

    /** Object version of the <code> IssueInstant </code> attribute. */
    private DateTime issueInstant;

    /** (Possibly null) Singleton object version of the <code> Conditions </code> element. */
    private Conditions conditions;

    /** (Possibly null) Singleton object version of the <code> Advice </code> element. */
    private Advice advice;

    /** Object representation of all the <code>Statement</code> elements. */
    private final IndexedXMLObjectChildrenList<Statement> statements;
    
    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected AssertionImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        statements = new IndexedXMLObjectChildrenList<>(this);
        version = SAMLVersion.VERSION_11;
    }
    
    /** {@inheritDoc} */
    public int getMajorVersion(){
        return version.getMajorVersion();
    }
    
    /** {@inheritDoc} */
    public int getMinorVersion() {
        return version.getMinorVersion();
    }
    
    /** {@inheritDoc} */
    public void setVersion(SAMLVersion newVersion){
        version = prepareForAssignment(version, newVersion);
    }

    /** {@inheritDoc} */
    public String getID() {
        return id;
    }

    /** {@inheritDoc} */
    public void setID(String newID) {
        String oldID = id;
        id = prepareForAssignment(id, newID);   
        registerOwnID(oldID, id);
    }

    /** {@inheritDoc} */
    public String getIssuer() {
        return this.issuer;
    }

    /** {@inheritDoc} */
    public void setIssuer(String iss) {
        issuer = prepareForAssignment(issuer, iss);
    }

    /** {@inheritDoc} */
    public DateTime getIssueInstant() {
        return this.issueInstant;
    }

    /** {@inheritDoc} */
    public void setIssueInstant(DateTime instant) {
        issueInstant = prepareForAssignment(issueInstant, instant);
    }

    /** {@inheritDoc} */
    public Conditions getConditions() {
        return conditions;
    }

    /** {@inheritDoc} */
    public void setConditions(Conditions c) {
        conditions = prepareForAssignment(conditions, c);
    }

    /** {@inheritDoc} */
    public Advice getAdvice() {
        return advice;
    }

    /** {@inheritDoc} */
    public void setAdvice(Advice adv) {
        advice = prepareForAssignment(advice, adv);
    }

    /** {@inheritDoc} */
    public List<Statement> getStatements() {
        return statements;
    }

    /** {@inheritDoc} */
    public List<Statement> getStatements(QName typeOrName) {
        return (List<Statement>) statements.subList(typeOrName);
    }

    /** {@inheritDoc} */
    public List<SubjectStatement> getSubjectStatements() {
        QName statementQName = new QName(SAMLConstants.SAML1_NS, SubjectStatement.DEFAULT_ELEMENT_LOCAL_NAME);
        return (List<SubjectStatement>) statements.subList(statementQName);
    }

    /** {@inheritDoc} */
    public List<AuthenticationStatement> getAuthenticationStatements() {
        QName statementQName = new QName(SAMLConstants.SAML1_NS, AuthenticationStatement.DEFAULT_ELEMENT_LOCAL_NAME);
        return (List<AuthenticationStatement>) statements.subList(statementQName);
    }

    /** {@inheritDoc} */
    public List<AuthorizationDecisionStatement> getAuthorizationDecisionStatements() {
        QName statementQName =
                new QName(SAMLConstants.SAML1_NS, AuthorizationDecisionStatement.DEFAULT_ELEMENT_LOCAL_NAME);
        return (List<AuthorizationDecisionStatement>) statements.subList(statementQName);
    }

    /** {@inheritDoc} */
    public List<AttributeStatement> getAttributeStatements() {
        QName statementQName = new QName(SAMLConstants.SAML1_NS, AttributeStatement.DEFAULT_ELEMENT_LOCAL_NAME);
        return (List<AttributeStatement>) statements.subList(statementQName);
    }
    
    /** {@inheritDoc} */
    public String getSignatureReferenceID(){
        return id;
    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {

        ArrayList<XMLObject> children = new ArrayList<>();

        if (conditions != null) {
            children.add(conditions);
        }

        if (advice != null) {
            children.add(advice);
        }

        children.addAll(statements);
        
        if(getSignature() != null){
            children.add(getSignature());
        }

        if (children.size() == 0) {
            return null;
        }
        return Collections.unmodifiableList(children);
    }
}