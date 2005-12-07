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

package org.opensaml.common.io;

import org.opensaml.common.SAMLObject;
import org.w3c.dom.Element;

/**
 * Unmarshallers are used to unmarshall a W3C DOM element into a 
 * {@link org.opensaml.common.SAMLObject}.
 */
public interface Unmarshaller {

	/**
	 * Unmarshalls the given W3C DOM element into a SAML Element
	 *  
	 * @param element the SAML Element
	 * 
	 * @return the unmarshalled SAML element
     * 
     * @throws UnmarshallingException thrown if an error occurs unmarshalling the DOM element into the SAMLElement
     * @throws UnknownAttributeException thrown if an attribute that the unmarshaller does not understand is encountered
     * @throws UnknownElementException thrown if an element that the unmarshaller does not understand is encountered
	 */
	public SAMLObject unmarshall(Element element) throws UnmarshallingException, UnknownAttributeException, UnknownElementException;
}
