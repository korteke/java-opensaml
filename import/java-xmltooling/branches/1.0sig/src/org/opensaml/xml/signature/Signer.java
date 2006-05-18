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

package org.opensaml.xml.signature;

import java.util.List;

/**
 * This class is responsible for creating the digital signatures for the given signable XMLObjects.
 * 
 * This must be done as a seperate step because in order to support the following cases:
 * <ul>
 * <li>Multiple signable objects appear in the DOM tree, in which case the order that the objects should be signed in
 * is not known (e.g. object 1 could appear first in the tree, but contain a reference to signable object 2)</li>
 * <li>The DOM tree resulting from marshalling of the XMLObject tree is grafted onto another DOM tree which may cause
 * element ID conflicts that would invalidate the signature</li>
 * </ul>
 */
public class Signer {

    /**
     * Signs the given XMLObject in the order provided.
     * 
     * @param xmlObjects an orderded list of XMLObject to be signed
     */
    public static void signObjects(List<SignableXMLObject> xmlObjects) {
        for(SignableXMLObject xmlObject : xmlObjects){
            signObject(xmlObject);
        }
    }

    /**
     * Signs a single XMLObject.
     * 
     * @param xmlObject the object to be signed
     */
    public static void signObject(SignableXMLObject xmlObject) {

    }
}