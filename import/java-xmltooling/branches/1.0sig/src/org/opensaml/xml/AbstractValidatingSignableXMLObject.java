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

package org.opensaml.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.opensaml.xml.signature.AbstractSignableXMLObject;
import org.opensaml.xml.validation.ValidatingXMLObject;
import org.opensaml.xml.validation.ValidationException;
import org.opensaml.xml.validation.Validator;

/**
 * Extension of {@link org.opensaml.xml.signature.AbstractSignableXMLObject} that implements
 * {@link org.opensaml.xml.validation.ValidatingXMLObject}
 */
public abstract class AbstractValidatingSignableXMLObject
        extends AbstractSignableXMLObject
        implements ValidatingXMLObject {

    /** Logger */
    private final Logger log = Logger
            .getLogger(AbstractValidatingSignableXMLObject.class);

    /** Validators used to validate this XMLObject */
    private ArrayList<Validator> validators = new ArrayList<Validator>();

    /**
     * Constructor
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected AbstractValidatingSignableXMLObject(
            String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(
                namespaceURI, elementLocalName, namespacePrefix);
    }

    /*
     * @see org.opensaml.xml.ValidatingXMLObject#getValidators()
     */
    public List<Validator> getValidators() {
        if (validators
                .size() > 0) {
            return Collections
                    .unmodifiableList(validators);
        }

        return null;
    }

    /*
     * @see org.opensaml.xml.ValidatingXMLObject#registerValidator(org.opensaml.xml.Validator)
     */
    public void registerValidator(
            Validator validator) {
        if (validator != null) {
            validators
                    .add(validator);
        }
    }

    /*
     * @see org.opensaml.xml.ValidatingXMLObject#deregisterValidator(org.opensaml.xml.Validator)
     */
    public void deregisterValidator(
            Validator validator) {
        validators
                .remove(validator);
    }

    /*
     * @see org.opensaml.xml.ValidatingXMLObject#validate(boolean)
     */
    public void validate(
            boolean validateDescendants)
            throws ValidationException {
        for (Validator validator : validators) {
            if (log
                    .isDebugEnabled()) {
                log
                        .debug("Validating "
                                + getElementQName() + " using Validator class" + validator
                                        .getClass().getName());
            }
            validator
                    .validate(this);
        }

        if (validateDescendants) {
            if (log
                    .isDebugEnabled()) {
                log
                        .debug("Validating descendants of "
                                + getElementQName());
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
    protected void validateChildren(
            XMLObject xmlObject)
            throws ValidationException {
        for (XMLObject childObject : xmlObject
                .getOrderedChildren()) {
            if (childObject instanceof ValidatingXMLObject) {
                ((ValidatingXMLObject) childObject)
                        .validate(false);
            } else {
                if (log
                        .isDebugEnabled()) {
                    log
                            .debug(childObject
                                    .getElementQName()
                                    + " does not implement ValidatingXMLObject, ignoring it.");
                }
            }

            if (childObject
                    .hasChildren()) {
                validateChildren(childObject);
            }
        }
    }
}