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

package org.opensaml.xml.schema;

import org.opensaml.xml.util.DatatypeHelper;

/**
 * A class representing a boolean attribute. This class tracks the usage of the literals {true, false, 1, 0} to ensure
 * proper roundtripping when unmarshalling/marshalling.
 */
public class XSBooleanValue {

    /** Whether to use the numeric representation of the lexical one */
    private boolean numericRepresentation;

    /** Value of this boolean */
    private Boolean value;

    /**
     * Constructor. Uses lexical representation and sets value to null.
     */
    public XSBooleanValue() {
        numericRepresentation = false;
        value = null;
    }

    /**
     * Constructor
     * 
     * @param value the value
     * @param numericRepresentation whether to use a numeric or lexical representation
     */
    public XSBooleanValue(Boolean value, boolean numericRepresentation) {
        this.numericRepresentation = numericRepresentation;
        this.value = value;
    }

    /**
     * Gets the boolean value.
     * 
     * @return the boolean value
     */
    public Boolean getValue() {
        return value;
    }

    /**
     * Sets the boolean value.
     * 
     * @param value the boolean value
     */
    public void setValue(Boolean value) {
        this.value = value;
    }

    /**
     * Gets whether to use the numeric or lexical representation.
     * 
     * @return whether to use the numeric or lexical representation
     */
    public boolean isNumericRepresentation() {
        return numericRepresentation;
    }

    /**
     * Sets whether to use the numeric or lexical representation.
     * 
     * @param numericRepresentation whether to use the numeric or lexical representation
     */
    public void setNumericRepresentation(boolean numericRepresentation) {
        this.numericRepresentation = numericRepresentation;
    }
    
    /** {@inheritDoc} */
    public int hashCode(){
        if(numericRepresentation){
            if(value == null){
                return 0;
            }else if(value.booleanValue()){
                return 1;
            }else {
                return 3;
            }
        }else{
            if(value == null){
                return 4;
            }else if(value.booleanValue()){
                return 5;
            }else {
                return 6;
            }
        }
    }
    
    /** {@inheritDoc} */
    public boolean equals(Object obj){
        if(obj == null){
            return false;
        }
        
        if(obj instanceof XSBooleanValue){
            XSBooleanValue otherValue = (XSBooleanValue) obj;
            return hashCode() == otherValue.hashCode();
        }
        
        return false;
    }

    /**
     * Converts this to a string. See {@link #toString(Boolean, boolean)}.
     */
    public String toString() {
        return toString(value, numericRepresentation);
    }

    /**
     * Converts a boolean value into a string. If using the numeric representations and the value is true then "1" is
     * returned or "0" if the value is false. If lexical representation is used "true" and "false" will be returned. If
     * the given value is null, then "false" is returned.
     * 
     * @param value the boolean value
     * @param numericRepresentation whether to use numeric of lexical representation
     * 
     * @return the textual representation
     */
    public static String toString(Boolean value, boolean numericRepresentation) {
        if (value == null) {
            return "false";
        }

        if (numericRepresentation) {
            if (value.booleanValue()) {
                return "1";
            } else {
                return "0";
            }
        } else {
            return value.toString();
        }
    }

    /**
     * Parses a string meant to represent a boolean. If the string is "1" or "0" the returned object will use a numeric
     * representation and have a value of TRUE or FALSE, respectively. If the string is "true" the returned object will
     * use a lexical representation and have a value of TRUE. If the string is anything else the returned object will
     * use a lexical representation and have a value of FALSE.
     * 
     * @param booleanString the string to parse
     * 
     * @return the boolean value
     */
    public static XSBooleanValue valueOf(String booleanString) {
        booleanString = DatatypeHelper.safeTrimOrNullString(booleanString);
        if (booleanString != null) {
            if (booleanString.equals("1")) {
                return new XSBooleanValue(Boolean.TRUE, true);
            } else if (booleanString.equals("0")) {
                return new XSBooleanValue(Boolean.FALSE, true);
            } else if (booleanString.equals("true")) {
                return new XSBooleanValue(Boolean.TRUE, false);
            }
        }

        return new XSBooleanValue(Boolean.FALSE, false);
    }
}