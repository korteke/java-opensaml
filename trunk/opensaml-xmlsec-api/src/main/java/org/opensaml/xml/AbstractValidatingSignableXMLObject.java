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

package org.opensaml.xml;

import java.util.Collections;
import java.util.List;

import org.opensaml.xml.signature.AbstractSignableXMLObject;
import org.opensaml.xml.util.LazyList;
import org.opensaml.xml.validation.ValidatingXMLObject;
import org.opensaml.xml.validation.ValidationException;
import org.opensaml.xml.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of {@link org.opensaml.xml.signature.AbstractSignableXMLObject} that implements
 * {@link org.opensaml.xml.validation.ValidatingXMLObject}.
 */
public abstract class AbstractValidatingSignableXMLObject extends AbstractSignableXMLObject implements
        ValidatingXMLObject {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(AbstractValidatingSignableXMLObject.class);

    /** Validators used to validate this XMLObject. */
    private List<Validator> validators;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected AbstractValidatingSignableXMLObject(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        validators = new LazyList<Validator>();
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
    @SuppressWarnings("unchecked")
    public void validate(boolean validateDescendants) throws ValidationException {
        for (Validator validator : validators) {
            log.debug("Validating {} using Validator class {}", getElementQName(), validator.getClass().getName());
            validator.validate(this);
        }

        if (validateDescendants) {
            log.debug("Validating descendants of {}", getElementQName());
            validateChildren(this);
        }
    }

    /**
     * Recursive method used to validate all the children of the given XMLObject that implement
     * {@link ValidatingXMLObject}. Note, this can be a very expensive operation.
     * 
     * @param xmlObject xmlObject whose descendants should be validated
     * 
     * @throws ValidationException thrown if one of the child objects do not validate
     */
    protected void validateChildren(XMLObject xmlObject) throws ValidationException {
        for (XMLObject childObject : xmlObject.getOrderedChildren()) {
            if (childObject == null) {
                continue;
            }

            if (childObject instanceof ValidatingXMLObject) {
                ((ValidatingXMLObject) childObject).validate(false);
            } else {
                log.debug("{} does not implement ValidatingXMLObject, ignoring it.", childObject.getElementQName());
            }

            if (childObject.hasChildren()) {
                validateChildren(childObject);
            }
        }
    }
}