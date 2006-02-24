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

package org.opensaml.xml.io;

import org.opensaml.xml.XMLObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Marshallers are used to marshall a {@link org.opensaml.xml.XMLObject} into 
 * a W3C DOM element.
 */
public interface Marshaller {
	
    /**
     * Marshall this element, and its children, and root them in a newly created Document.  The Document is 
     * created by a {@link javax.xml.parsers.DocumentBuilder} obtained from a {@link javax.xml.parsers.DocumentBuilderFactory} 
     * created without any additional parameters or properties set; that is the system defaults properties are used.
     * 
     * @param xmlObject the object to marshall
     * 
     * @return the W3C DOM element representing this SAML element
     * 
     * @throws MarshallingException thrown if there is a problem marshalling the given object
     */
    public Element marshall(XMLObject xmlObject) throws MarshallingException;
    
	/**
	 * Marshall this element, and its children, into a W3C DOM element.  If the given Document already has 
     * a document (root) element the returned Element and its children are adopted into the document but 
     * the returned element is not appended to any element already existing in the document.  It is the job 
     * of the caller to graft the returned element into the existing DOM.  If the given Document does not 
     * have a document (root) element the returned Element is set as such.
	 * 
	 * @param xmlObject the object to marshall
	 * @param document the DOM document the marshalled element will be placed in
	 * 
	 * @return the W3C DOM element representing this XMLObject
	 * 
	 * @throws MarshallingException thrown if there is a problem marshalling the given object
	 */
	public Element marshall(XMLObject xmlObject, Document document) throws MarshallingException;
}