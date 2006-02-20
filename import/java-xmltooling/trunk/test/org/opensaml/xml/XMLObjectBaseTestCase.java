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

import org.custommonkey.xmlunit.XMLTestCase;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.parse.ParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.w3c.dom.Document;

/**
 * Base test case class for tests that operate on XMLObjects.
 */
public class XMLObjectBaseTestCase extends XMLTestCase {

    /** Parser pool */
    protected static ParserPool parserPool;

    /** XMLObject builder factory */
    protected static XMLObjectBuilderFactory builderFactory;

    /** XMLObject marshaller factory */
    protected static MarshallerFactory marshallerFactory;

    /** XMLObject unmarshaller factory */
    protected static UnmarshallerFactory unmarshallerFactory;

    /**
     * Constructor
     * 
     * @throws XMLParserException thrown if the configuration file can not be parsed
     * @throws ConfigurationException thrown if the configuration file is invalid or classes referenced within it can't
     *             be created
     */
    public XMLObjectBaseTestCase() throws XMLParserException, ConfigurationException {

    }

    static {
        HashMap<String, Boolean> features = new HashMap<String, Boolean>();
        features.put("http://apache.org/xml/features/validation/schema/normalized-value", Boolean.FALSE);
        features.put("http://apache.org/xml/features/dom/defer-node-expansion", Boolean.FALSE);

        parserPool = new ParserPool(true, null, features);

        try {
            Document configureation = parserPool.parse(XMLObjectBaseTestCase.class
                    .getResourceAsStream("/conf/xmltooling-config.xml"));
            Configuration.load(configureation);

            builderFactory = Configuration.getBuilderFactory();
            marshallerFactory = Configuration.getMarshallerFactory();
            unmarshallerFactory = Configuration.getUnmarshallerFactory();
        } catch (Exception e) {
            System.err.println("Can not initialize XMLObjectBaseTestCase");
        }
    }
}