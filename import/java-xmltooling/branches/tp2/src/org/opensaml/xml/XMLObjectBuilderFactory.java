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

import java.util.Map;

import javax.xml.namespace.QName;

import javolution.util.FastMap;

import org.apache.log4j.Logger;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Element;

/**
 * A factory for {@link org.opensaml.xml.XMLObjectBuilder}s. XMLObjectBuilders are stored and retrieved by a
 * {@link javax.xml.namespace.QName} key. This key is either the XML Schema Type or element QName of the XML element the
 * built XMLObject object represents.
 */
public class XMLObjectBuilderFactory {

    /** Logger */
    private static final Logger LOG = Logger.getLogger(XMLObjectBuilderFactory.class);

    /** Registered builders */
    private FastMap<QName, XMLObjectBuilder> builders;

    /** Constructor */
    public XMLObjectBuilderFactory() {
        builders = new FastMap<QName, XMLObjectBuilder>();
    }

    /**
     * Retrieves an {@link XMLObjectBuilder} using the key it was registered with.
     * 
     * @param key the key used to register the builder
     * 
     * @return the builder
     */
    public XMLObjectBuilder getBuilder(QName key) {
        if(key == null){
            return null;
        }
        return builders.get(key);
    }

    /**
     * Retrieves the XMLObject builder for the given element. The schema type, if present, is tried first as the key
     * with the element QName used if no schema type is present or does not have a builder registered under it.
     * 
     * @param domElement the element to retrieve the builder for
     * 
     * @return the builder for the XMLObject the given element can be unmarshalled into
     */
    public XMLObjectBuilder getBuilder(Element domElement) {
        XMLObjectBuilder builder;

        builder = getBuilder(XMLHelper.getXSIType(domElement));

        if (builder == null) {
            builder = getBuilder(XMLHelper.getNodeQName(domElement));
        }

        return builder;
    }

    /**
     * Gets an immutable list of all the builders currently registered.
     * 
     * @return list of all the builders currently registered
     */
    public Map<QName, XMLObjectBuilder> getBuilders() {
        return builders.unmodifiable();
    }

    /**
     * Registers a new builder for the given name.
     * 
     * @param builderKey the key used to retrieve this builder later
     * @param builder the builder
     */
    public void registerBuilder(QName builderKey, XMLObjectBuilder builder) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Registering builder, " + builder.getClass().getName() + " under key " + builderKey);
        }
        builders.put(builderKey, builder);
    }

    /**
     * Deregisters a builder.
     * 
     * @param builderKey the key for the builder to be deregistered
     * 
     * @return the builder that was registered for the given QName
     */
    public XMLObjectBuilder deregisterBuilder(QName builderKey) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Deregistering builder for object type " + builderKey);
        }
        return builders.remove(builders.get(builderKey));
    }
}