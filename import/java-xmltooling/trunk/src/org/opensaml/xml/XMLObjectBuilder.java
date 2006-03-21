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

import org.w3c.dom.Element;

/**
 * A builder for XMLObjects.
 */
public interface XMLObjectBuilder {

    /**
     * Creates an empty XMLObject.
     * 
     * @return the empty XMLObject
     */
    public XMLObject buildObject();
    
    /**
     * Creates an empty XMLObject using information from the given DOM element.  This method is used 
     * by abstract unmarshalling code included with xmltooling.
     * 
     * @param element the DOM Element containing information about the object to be built.
     * 
     * @return the empty XMLObject
     */
    public XMLObject buildObject(Element element);
}