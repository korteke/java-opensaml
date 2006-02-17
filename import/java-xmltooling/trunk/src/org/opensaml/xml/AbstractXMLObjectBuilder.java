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

/**
 * 
 */
package org.opensaml.xml;

/**
 * Base implementation of {@link org.opensaml.xml.XMLObjectBuilder} that automatically 
 * invoked {@link #resetState()} after the XMLObject is built.
 */
public abstract class AbstractXMLObjectBuilder implements XMLObjectBuilder {

    /**
     * Builds the XMLObject, by delegating to {@link #doBuildObject()} and then 
     * invokes {@link #resetState()} before returning the build object.
     */
    public XMLObject buildObject() {
        XMLObject builtObject = doBuildObject();
        resetState();
        return builtObject;
    }

    /*
     * @see org.opensaml.xml.XMLObjectBuilder#resetState()
     */
    public void resetState() {

    }
    
    /**
     * Delegated call that build the XMLObject prior to a state reset.
     * 
     * @return the build XMLObject
     */
    public abstract XMLObject doBuildObject();
}