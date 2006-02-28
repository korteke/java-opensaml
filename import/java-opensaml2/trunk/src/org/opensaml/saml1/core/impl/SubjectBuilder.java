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

package org.opensaml.saml1.core.impl;

import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBuilder;

/**
 * A class to create SubjectImpl Objects
 */
public class SubjectBuilder implements XMLObjectBuilder {

    /**
     * Constructor
     */
    public SubjectBuilder() {

    }

    /*
     * @see org.opensaml.xml.XMLObjectBuilder#buildObject()
     */
    public XMLObject buildObject() {
        return new SubjectImpl();
    }
}