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

package org.opensaml.xml.encryption;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.xml.security.Init;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.encryption.XMLEncryptionException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 */
public class EncryptedDataMarshaller implements Marshaller {
    
    //  TODO this will all change when schema XMLObjects are implemented.
    
    /** Logger */
    private static Logger log = Logger.getLogger(EncryptedDataMarshaller.class);

    /**
     * Constructor
     *
     */
    public EncryptedDataMarshaller() {
        if (!Init.isInitialized()) {
            if (log.isDebugEnabled()) {
                log.debug("Initializing XML security library");
            }
            Init.init();
        }
    }
    
    /** {@inheritDoc} */
    public Element marshall(XMLObject xmlObject) throws MarshallingException {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            return marshall(xmlObject, document);
        } catch (ParserConfigurationException e) {
            throw new MarshallingException("Unable to create Document to place marshalled elements in", e);
        }
    }

    /** {@inheritDoc} */
    public Element marshall(XMLObject xmlObject, Element parentElement) throws MarshallingException {
        Element encDataElement = createEncDataElement((EncryptedData) xmlObject, parentElement.getOwnerDocument());
        XMLHelper.appendChildElement(parentElement, encDataElement);
        return encDataElement;
    }

    /** {@inheritDoc} */
    public Element marshall(XMLObject xmlObject, Document document) throws MarshallingException {
        Element encDataElement = createEncDataElement((EncryptedData) xmlObject, document);
        
        Element documentRoot = document.getDocumentElement();
        if (documentRoot != null) {
            document.replaceChild(documentRoot, encDataElement);
        } else {
            document.appendChild(encDataElement);
        }
        
        return encDataElement;
    }

    private Element createEncDataElement(EncryptedData xmlObject, Document ownerDocument) throws MarshallingException {
        org.apache.xml.security.encryption.EncryptedData xmlEncData = xmlObject.getXMLEncData();
        Element encDataElement;
        try {
            XMLCipher xmlCipher = XMLCipher.getInstance();
            encDataElement = xmlCipher.martial(ownerDocument, xmlEncData);
        } catch (XMLEncryptionException e) {
            throw new MarshallingException("XML Security exception when marshalling wrapped EncryptedData object: " + e);
        }
        //TODO is this correct?  And why isn't XMLCipher.martial() properly setting the owner Document?
        XMLHelper.adoptElement(encDataElement, ownerDocument);
        return encDataElement;
    }

}
