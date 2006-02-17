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

package org.opensaml.xml;

import java.util.HashMap;

import javax.xml.namespace.QName;

import org.apache.xml.security.Init;
import org.custommonkey.xmlunit.XMLTestCase;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.mock.SimpleXMLObject;
import org.opensaml.xml.mock.SimpleXMLObjectMarshaller;
import org.opensaml.xml.parse.ParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureMarshaller;
import org.opensaml.xml.util.XMLConstants;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;

public class XMLObjectBaseTestCase extends XMLTestCase {

    protected ParserPool parserPool;
    
    protected MarshallerFactory<QName, Marshaller<XMLObject>> marshallerFactory;
    
    protected UnmarshallerFactory<QName, Unmarshaller<XMLObject>> unmarshallerFactory;
    
    protected void setUp() throws Exception {
        Init.init();
        
        HashMap<String, Boolean> features = new HashMap<String, Boolean>();
        features.put("http://apache.org/xml/features/validation/schema/normalized-value", Boolean.FALSE);
        features.put("http://apache.org/xml/features/dom/defer-node-expansion", Boolean.TRUE);
        
        parserPool = new ParserPool(true, null, features);
        
        marshallerFactory = new MarshallerFactory<QName, Marshaller<XMLObject>>();
        marshallerFactory.registerMarshaller(new QName(SimpleXMLObject.NAMESAPACE, SimpleXMLObject.LOCAL_NAME), new SimpleXMLObjectMarshaller(marshallerFactory));
        marshallerFactory.registerMarshaller(new QName(XMLConstants.XMLSIG_NS, Signature.LOCAL_NAME), new SignatureMarshaller());
        
        unmarshallerFactory = new UnmarshallerFactory<QName, Unmarshaller<XMLObject>>();
    }
    
    protected Document parse(String resourceID) throws XMLParserException {
        return parserPool.parse(new InputSource(XMLObjectBaseTestCase.class
                    .getResourceAsStream(resourceID)));
    }
    
    public String elementToString(Element domElement) {
        DOMImplementation domImpl = domElement.getOwnerDocument().getImplementation();
        DOMImplementationLS domImplLS = (DOMImplementationLS) domImpl.getFeature("LS", "3.0");
        LSSerializer serializer = domImplLS.createLSSerializer();
        return serializer.writeToString(domElement);
    }
}