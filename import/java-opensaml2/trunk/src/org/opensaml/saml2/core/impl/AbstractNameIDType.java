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

package org.opensaml.saml2.core.impl;

import java.util.List;

import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.NameIDType;
import org.opensaml.xml.XMLObject;

/**
 * Abstract implementation of {@link org.opensaml.saml2.core.NameIDType}
 */
public class AbstractNameIDType extends AbstractAssertionSAMLObject implements NameIDType {

    /** Name of the Name ID */
    private String name;
    
    /** Name Qualifier of the Name ID */
    private String nameQualifier;

    /** SP Name Qualifier of the Name ID */
    private String spNameQualifier;

    /** Format of the Name ID */
    private String format;

    /** SP Proivder ID of the NameID */
    private String spProviderID;

    /**
     * Constructor. Sets namespace to {@link SAMLConstants#SAML20_NS} and prefix to
     * {@link SAMLConstants#SAML20_PREFIX}.
     * 
     * @param localName the local name of the element
     */
    protected AbstractNameIDType(String localName) {
        super(localName);
    }

    /**
     * Constructor
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected AbstractNameIDType(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /*
     * @see org.opensaml.saml2.core.NameID#getName()
     */
    public String getValue() {
        return name;
    }
    
    /*
     * @see org.opensaml.saml2.core.NameID#setName(java.lang.String)
     */
    public void setValue(String newName) {
        this.name = prepareForAssignment(this.name, newName);
    }
    
    /*
     * @see org.opensaml.saml2.core.NameID#getNameQualifier()
     */
    public String getNameQualifier() {
        return nameQualifier;
    }

    /*
     * @see org.opensaml.saml2.core.NameID#setNameQualifier(java.lang.String)
     */
    public void setNameQualifier(String newNameQualifier) {
        this.nameQualifier = prepareForAssignment(this.nameQualifier, newNameQualifier);
    }

    /*
     * @see org.opensaml.saml2.core.NameID#getSPNameQualifier()
     */
    public String getSPNameQualifier() {
        return spNameQualifier;
    }

    /*
     * @see org.opensaml.saml2.core.NameID#setSPNameQualifier(java.lang.String)
     */
    public void setSPNameQualifier(String newSPNameQualifier) {
        this.spNameQualifier = prepareForAssignment(this.spNameQualifier, newSPNameQualifier);
    }

    /*
     * @see org.opensaml.saml2.core.NameID#getFormat()
     */
    public String getFormat() {
        return format;
    }

    /*
     * @see org.opensaml.saml2.core.NameID#setFormat(java.lang.String)
     */
    public void setFormat(String newFormat) {
        this.format = prepareForAssignment(this.format, newFormat);
    }

    /*
     * @see org.opensaml.saml2.core.NameID#getSPProviderID()
     */
    public String getSPProviderID() {
        return spProviderID;
    }

    /*
     * @see org.opensaml.saml2.core.NameID#setSPProviderID(java.lang.String)
     */
    public void setSPProviderID(String newSPProviderID) {
        this.spProviderID = prepareForAssignment(this.spProviderID, newSPProviderID);
    }

    /*
     * @see org.opensaml.xml.XMLObject#getOrderedChildren()
     */
    public List<XMLObject> getOrderedChildren() {
        return null;
    }
}