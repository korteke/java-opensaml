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

package org.opensaml.xmlsec.signature.validator;

import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.validation.ValidationException;
import org.opensaml.core.xml.validation.Validator;
import org.opensaml.xmlsec.signature.PGPData;
import org.opensaml.xmlsec.signature.PGPKeyID;
import org.opensaml.xmlsec.signature.PGPKeyPacket;
import org.opensaml.xmlsec.signature.SignatureConstants;

/**
 * Checks {@link org.opensaml.xmlsec.signature.PGPData} for Schema compliance. 
 */
public class PGPDataSchemaValidator implements Validator<PGPData> {
    
    /** QNames corresponding to the valid children. */
    private static final Set<QName> VALID_DS_CHILD_NAMES;

    /** {@inheritDoc} */
    public void validate(PGPData xmlObject) throws ValidationException {
        validateChildrenPresence(xmlObject);
        validateChildrenNamespaces(xmlObject);
    }
    
    /**
     * Get the QNames corresponding to the valid children
     * defined in the XML Signature namespace.
     * 
     * @return list of valid child QNames
     */
    protected static Set<QName> getValidDSChildNames() {
        return VALID_DS_CHILD_NAMES;
    }

    /**
     * Validate that at least one mandatory child is present.
     * 
     * @param xmlObject the object to validate
     * @throws ValidationException  thrown if the object is invalid
     */
    protected void validateChildrenPresence(PGPData xmlObject) throws ValidationException {
        if (xmlObject.getPGPKeyID() == null && xmlObject.getPGPKeyPacket() == null) {
            throw new ValidationException("PGPData must contain at least one of PGPKeyID or PGPKeyPacket");
        }
    }
    
    /**
     * Validate that all children are either ones defined within the XML Signature schema,
     * or are from another namespace.
     * 
     * @param xmlObject the object to validate
     * @throws ValidationException thrown if the object is invalid
     */
    protected void validateChildrenNamespaces(PGPData xmlObject) throws ValidationException {
        // Validate that any unknown children are from another namespace.
        for (XMLObject child : xmlObject.getUnknownXMLObjects()) {
            QName childName = child.getElementQName();
            if (! getValidDSChildNames().contains(childName) 
                    && SignatureConstants.XMLSIG_NS.equals(childName.getNamespaceURI())) {
                throw new ValidationException("PGPData contains an illegal child extension element: " + childName);
            }
        }
    }
    
    static {
        VALID_DS_CHILD_NAMES = new HashSet<QName>(5);
        VALID_DS_CHILD_NAMES.add(PGPKeyID.DEFAULT_ELEMENT_NAME);
        VALID_DS_CHILD_NAMES.add(PGPKeyPacket.DEFAULT_ELEMENT_NAME);
    }
}
