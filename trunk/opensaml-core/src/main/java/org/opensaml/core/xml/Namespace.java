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

package org.opensaml.core.xml;

import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.xml.XmlConstants;

import com.google.common.base.Objects;

/** Data structure for representing XML namespace attributes. */
public class Namespace {

    /** URI of the namespace. */
    private String namespaceURI;

    /** Prefix of the namespace. */
    private String namespacePrefix;

    /** String representation of this namespace. */
    private String nsStr;

    /**
     * Constructor.
     * 
     * @param uri the URI of the namespace
     * @param prefix the prefix of the namespace
     */
    public Namespace(String uri, String prefix) {
        namespaceURI = StringSupport.trimOrNull(uri);
        namespacePrefix = StringSupport.trimOrNull(prefix);
        nsStr = null;
    }

    /**
     * Gets the prefix of the namespace.
     * 
     * @return the prefix of the namespace, may be null if this is a default namespace
     */
    public String getNamespacePrefix() {
        return namespacePrefix;
    }

    /**
     * Gets the URI of the namespace.
     * 
     * @return the URI of the namespace
     */
    public String getNamespaceURI() {
        return namespaceURI;
    }

    /** {@inheritDoc} */
    public String toString() {
        if (nsStr == null) {
            constructStringRepresentation();
        }

        return nsStr;
    }

    /** {@inheritDoc} */
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + toString().hashCode();
        return hash;
    }

    /**
     * Checks if the given object is the same as this Namespace. This is true if:
     * <ul>
     * <li>The given object is of type {@link Namespace}</li>
     * <li>The given object's namespace URI is the same as this object's namespace URI</li>
     * <li>The given object's namespace prefix is the same as this object's namespace prefix</li>
     * </ul>
     * 
     * @param obj {@inheritDoc}
     * 
     * @return {@inheritDoc}
     */
    public boolean equals(Object obj) {    
        if(obj == this){
            return true;
        }
        
        if (obj instanceof Namespace) {
            Namespace otherNamespace = (Namespace) obj;
            if (Objects.equal(otherNamespace.getNamespaceURI(), getNamespaceURI())){
                if (Objects.equal(otherNamespace.getNamespacePrefix(), getNamespacePrefix())){
                    return true;
                }
            }
        }

        return false;
    }

    /** Constructs an XML namespace declaration string representing this namespace. */
    protected void constructStringRepresentation() {
        StringBuffer stringRep = new StringBuffer();

        stringRep.append(XmlConstants.XMLNS_PREFIX);

        if (namespacePrefix != null) {
            stringRep.append(":");
            stringRep.append(namespacePrefix);
        }

        stringRep.append("=\"");

        if (namespaceURI != null) {
            stringRep.append(namespaceURI);
        }

        stringRep.append("\"");

        nsStr = stringRep.toString();
    }
}