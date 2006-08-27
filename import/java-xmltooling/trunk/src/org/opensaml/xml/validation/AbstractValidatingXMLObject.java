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

package org.opensaml.xml.validation;

import java.util.Collections;
import java.util.List;

import javolution.util.FastList;

import org.apache.log4j.Logger;
import org.opensaml.xml.AbstractXMLObject;
import org.opensaml.xml.XMLObject;

/**
 * Extension of {@link org.opensaml.xml.AbstractXMLObject} that implements {@link org.opensaml.xml.validation.ValidatingXMLObject}
 */
public abstract class AbstractValidatingXMLObject extends AbstractXMLObject implements ValidatingXMLObject {
    
    /** Logger */
    private final Logger log = Logger.getLogger(AbstractValidatingXMLObject.class);

    /** Validators used to validate this XMLObject */
    private FastList<Validator> validators;
    
    /**
     * Constructor
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected AbstractValidatingXMLObject(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        validators = new FastList<Validator>();
    }

    /** {@inheritDoc} */
    public List<Validator> getValidators() {
        if (validators.size() > 0) {
            return Collections.unmodifiableList(validators);
        }

        return null;
    }

    /** {@inheritDoc} */
    public void registerValidator(Validator validator) {
        if (validator != null) {
            validators.add(validator);
        }
    }

    /** {@inheritDoc} */
    public void deregisterValidator(Validator validator) {
        validators.remove(validator);
    }

    /** {@inheritDoc} */
    public void validate(boolean validateDescendants) throws ValidationException {
        for (Validator validator : validators) {
            if (log.isDebugEnabled()) {
                log.debug("Validating " + getElementQName() + " using Validator class"
                                + validator.getClass().getName());
            }
            validator.validate(this);
        }

        if (validateDescendants) {
            if (log.isDebugEnabled()) {
                log.debug("Validating descendants of " + getElementQName());
            }
            validateChildren(this);
        }
    }

    /**
     * Recursive method used to validate all the children of the given XMLObject that implement
     * {@link ValidatingXMLObject}. Note, this can be a very expensive operation.
     * 
     * @param xmlObject xmlObject whose descendants should be validated
     * 
     * @throws ValidationException
     */
    protected void validateChildren(XMLObject xmlObject) throws ValidationException {
        for (XMLObject childObject : xmlObject.getOrderedChildren()) {
            if (childObject instanceof ValidatingXMLObject) {
                ((ValidatingXMLObject) childObject).validate(false);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug(childObject.getElementQName() + " does not implement ValidatingXMLObject, ignoring it.");
                }
            }

            if (childObject.hasChildren()) {
                validateChildren(childObject);
            }
        }
    }
}