/*
 * Copyright 2010 University Corporation for Advanced Internet Development, Inc.
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

package org.opensaml.util.xml;

/** Enum representing the allowed values of the xml:space attribute. */
public enum XmlSpace {

    /** xml:space value "default". */
    DEFAULT,

    /** xml:space value "preserve". */
    PRESERVE;

    // Unfortunately "default" is a reserved word in Java, so the enum value above has to be upper case
    // and we have the mess below.

    /** {@inheritDoc} */
    public String toString() {
        return super.toString().toLowerCase();
    }

    /**
     * Parse a string value into an XMLSpaceEnum.
     * 
     * <p>
     * The legal values are "default" and "preserve".
     * </p>
     * 
     * @param value the value to parse
     * @return the corresponding XMLSpaceEnum
     */
    public static XmlSpace parseValue(final String value) {
        return XmlSpace.valueOf(value.toUpperCase());
    }
}