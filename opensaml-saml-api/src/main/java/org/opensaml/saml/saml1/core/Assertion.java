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

package org.opensaml.saml.saml1.core;

import java.util.List;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

/**
 * This interface defines how the object representing a SAML 1 <code> Assertion </code> element behaves.
 */
public interface Assertion extends SignableSAMLObject, Evidentiary {

    /** Element name, no namespace. */
    public static final String DEFAULT_ELEMENT_LOCAL_NAME = "Assertion";

    /** Default element name. */
    public static final QName DEFAULT_ELEMENT_NAME = new QName(SAMLConstants.SAML1_NS, DEFAULT_ELEMENT_LOCAL_NAME,
            SAMLConstants.SAML1_PREFIX);

    /** Local name of the XSI type. */
    public static final String TYPE_LOCAL_NAME = "AssertionType";

    /** QName of the XSI type. */
    public static final QName TYPE_NAME =
            new QName(SAMLConstants.SAML1_NS, TYPE_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);

    /** Name for the attribute which defines Major Version (attribute's value must be 1). */
    public static final String MAJORVERSION_ATTRIB_NAME = "MajorVersion";

    /** Name for the attribute which defines Minor Version. */
    public static final String MINORVERSION_ATTRIB_NAME = "MinorVersion";

    /** Name for the attribute which defines Assertion ID. */
    public static final String ASSERTIONID_ATTRIB_NAME = "AssertionID";

    /** Name for the attribute which defines Issuer. */
    public static final String ISSUER_ATTRIB_NAME = "Issuer";

    /** Name for the attribute which defines the issue instant. */
    public static final String ISSUEINSTANT_ATTRIB_NAME = "IssueInstant";

    /** Name for the attribute which defines the Issue Instant. */
    public static final String ID_ATTRIB_NAME = "AssertionID";

    /* attributes */

    /**
     * Get the MajorVersion attribute.
     * 
     * @return The stored MajorVersion
     */
    public int getMajorVersion();

    /**
     * Get the MinorVersion attribute.
     * 
     * @return The stored MinorVersion
     */
    public int getMinorVersion();

    /**
     * Sets the SAML version of this assertion.
     * 
     * @param version the SAML version of this assertion
     */
    public void setVersion(SAMLVersion version);

    /**
     * Get the Issuer (which is an attribute) .
     * 
     * @return the Issuer
     */
    public String getIssuer();

    /**
     * Set the Issuer (attribute).
     * 
     * @param issuer the value to set
     */
    public void setIssuer(String issuer);

    /**
     * Get the IssueInstant (attribute).
     * 
     * @return the Issue Instant (as a Date)
     */
    public DateTime getIssueInstant();

    /** Set the ID.
     * @return the ID. */
    public String getID();

    /** Get the ID.
     * @param id what to set */
    public void setID(String id);

    /**
     * Set the IssueInstance (attribute).
     * 
     * @param issueInstant the issue instant value to set
     */
    public void setIssueInstant(DateTime issueInstant);

    /* Singleton Elements */

    /**
     * Return the (singleton) Object, representing the <code> Conditions </code> sub element.
     * 
     * @return the Conditions object.
     */
    public Conditions getConditions();

    /**
     * Set the Object representing the <code> Conditions </code> Sub element.
     * 
     * @param conditions the condition to List
     */
    public void setConditions(Conditions conditions);

    /**
     * advice is a (singleton) Object, representing the <code> Advice </code> sub element.
     * 
     * @return the advice object in this assertion
     */
    public Advice getAdvice();

    /**
     * Set the Object representing the <code> Advice </code> sub element.
     * 
     * @param advice the object to set
     */
    public void setAdvice(Advice advice);

    /* Multiple Elements */

    /**
     * Return the List representing all the <code> Statement </code> sub elements.
     * 
     * @return the List representing all the statements
     */
    public List<Statement> getStatements();

    /**
     * Return the List representing all the <code> Statement </code> sub elements with a given schema type or element
     * name.
     * 
     * @param typeOrName the schema type or element name
     * 
     * @return the List representing all the statements
     */
    public List<Statement> getStatements(QName typeOrName);

    /**
     * Return the List representing all the <code> SubjectStatement </code> sub elements.
     * 
     * @return all the SubjectStatements
     */
    public List<SubjectStatement> getSubjectStatements();

    /**
     * Return the List representing all the <code> AuthenticationStatement </code> sub elements.
     * 
     * @return all the AuthenticationStatements
     */
    public List<AuthenticationStatement> getAuthenticationStatements();

    /**
     * Return the List representing all the <code> AuthorizationStatement </code> sub elements.
     * 
     * @return all the authorizationDecisionStatements.
     */
    public List<AuthorizationDecisionStatement> getAuthorizationDecisionStatements();

    /**
     * Return all the <code> AttributeStatement </code> elements.
     * 
     * @return all the attributeStatements
     */
    public List<AttributeStatement> getAttributeStatements();
}