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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.opensaml.xml.XMLObject;

/**
 * A collection of validators that can be applied to an XMLObject and its children. These collections can represent
 * usage specific checks, such as those outlined in things like profiles for specific XML specification.
 * 
 * Registered {@link org.opensaml.xml.validation.Validator}s must be stateless. The xmlObjectTarget may be the
 * XMLObject's element QName retrieved by {@link org.opensaml.xml.XMLObject#getElementQName()} or schema type, retrieved
 * by {@link org.opensaml.xml.XMLObject#getSchemaType()}, with schema type registered checks performed first.
 */
public class ValidatorSuite {

    /** Logger */
    private final Logger log = Logger.getLogger(ValidatorSuite.class);

    /** Unique ID for this suite */
    private String id;

    /** Validators registered in this suite */
    private Map<QName, List<Validator>> validators;

    /**
     * Constructor
     * 
     * @param suiteId unique ID for this suite
     */
    public ValidatorSuite(String suiteId) {
        id = suiteId;
    }

    /**
     * Gets a unique ID for this suite.
     * 
     * @return a unique ID for this suite
     */
    public String getId() {
        return id;
    }

    /**
     * Evaluates the registered validators against the given XMLObject and it's children.
     * 
     * @param xmlObject the XMLObject to validate
     * 
     * @throws ValidationException thrown if the element is not valid
     */
    public void validate(XMLObject xmlObject) throws ValidationException {
        if(log.isDebugEnabled()){
            log.debug("Beginning to verify XMLObject " + xmlObject.getElementQName() + " and its children");
        }
        
        performValidation(xmlObject);
        
        List<XMLObject> children = xmlObject.getOrderedChildren();
        for(XMLObject child : children){
            validate(child);
        }
    }

    /**
     * Gets an immutable list of validators for a given XMLObject target.
     * 
     * @param xmlObjectTarget the XMLObject the returned list of validators operates on
     * 
     * @return the list of validators for the XMLObject
     */
    public List<Validator> getValidators(QName xmlObjectTarget) {
        return Collections.unmodifiableList(validators.get(xmlObjectTarget));
    }

    /**
     * Registers a new validator in the suite.
     * 
     * @param validator the validator
     * @param xmlObjectTarget the XMLObject the validator should operate on
     */
    public void registerValidator(QName xmlObjectTarget, Validator validator) {
        List<Validator> targetValidators = validators.get(xmlObjectTarget);

        if (targetValidators == null) {
            targetValidators = new ArrayList<Validator>();
            validators.put(xmlObjectTarget, targetValidators);
        }

        targetValidators.add(validator);
    }

    /**
     * Removes a validator from this suite.
     * 
     * @param xmlObjectTarget the XMLObject the validator is currently registered for
     * @param validator the validator to remove
     */
    public void deregisterValidator(QName xmlObjectTarget, Validator validator) {
        List<Validator> targetValidators = validators.get(xmlObjectTarget);

        if (targetValidators != null) {
            targetValidators.remove(validator);
        }
    }

    /**
     * Validates the given XMLObject. Does NOT validate its children.
     * 
     * @param xmlObject the XMLObject to validate.
     * 
     * @throws ValidationException thrown if the XMLObject does not validate
     */
    private void performValidation(XMLObject xmlObject) throws ValidationException {
        QName schemaType = xmlObject.getSchemaType();
        if (schemaType != null) {
            if (log.isDebugEnabled()) {
                log.debug("Validating XMLObject " + xmlObject.getElementQName()
                        + " against validators registered under its schema type, " + schemaType);
            }
            performValidation(schemaType, xmlObject);
        }
        
        if (log.isDebugEnabled()) {
            log.debug("Validating XMLObject " + xmlObject.getElementQName()
                    + " against validators registered under its element QName");
        }
        
        performValidation(xmlObject.getElementQName(), xmlObject);
    }

    /**
     * Validates the given XMLObject against the validators registered under the given key.
     * 
     * @param validatorSetKey the key to the list of validators
     * @param xmlObject the XMLObject to validate
     * 
     * @throws ValidationException thrown if any validations fail
     */
    private void performValidation(QName validatorSetKey, XMLObject xmlObject) throws ValidationException {
        List<Validator> elementQNameValidators = validators.get(validatorSetKey);
        if (elementQNameValidators != null) {
            for(Validator validator : elementQNameValidators){
                if(log.isDebugEnabled()){
                    log.debug("Validating XMLObject " + xmlObject.getElementQName() + " against Validator " + validator.getClass().getName());
                }
                validator.validate(xmlObject);
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("No validators registered for XMLObject " + xmlObject.getElementQName() + " under QName " + validatorSetKey);
            }
        }
    }
}