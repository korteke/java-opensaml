/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.util.xml;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.opensaml.util.Assert;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.w3c.dom.ls.LSSerializerFilter;

/** Set of helper functions for serializing/writing DOM nodes. */
@Deprecated
public final class SerializeSupport {

    /** Constructor. */
    private SerializeSupport() {

    }

    /**
     * Converts a Node into a String using the DOM, level 3, Load/Save serializer.
     * 
     * @param node the node to be written to a string
     * 
     * @return the string representation of the node
     */
    public static String nodeToString(final Node node) {
        Assert.isNotNull(node, "Node may not be null");

        final ByteArrayOutputStream baout = new ByteArrayOutputStream();
        writeNode(node, baout);
        try {
            return new String(baout.toByteArray(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // all VMs are required to support UTF-8, if it's not something is really wrong
            throw new RuntimeException(e);
        }
    }

    /**
     * Pretty prints the XML node.
     * 
     * @param node xml node to print
     * 
     * @return pretty-printed xml
     */
    public static String prettyPrintXML(final Node node) {
        Assert.isNotNull(node, "Node may not be null");

        final TransformerFactory tfactory = TransformerFactory.newInstance();
        try {
            final Transformer serializer = tfactory.newTransformer();
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");

            final StringWriter output = new StringWriter();
            serializer.transform(new DOMSource(node), new StreamResult(output));
            return output.toString();
        } catch (TransformerException e) {
            // this is fatal, just dump the stack and throw a runtime exception
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Writes a Node out to a Writer using the DOM, level 3, Load/Save serializer. The written content is encoded using
     * the encoding specified in the writer configuration.
     * 
     * @param node the node to write out
     * @param output the output stream to write the XML to
     */
    public static void writeNode(final Node node, final OutputStream output) {
        Assert.isNotNull(node, "Node may not be null");
        Assert.isNotNull(output, "Outputstream may not be null");

        final DOMImplementation domImpl;
        if (node instanceof Document) {
            domImpl = ((Document) node).getImplementation();
        } else {
            domImpl = node.getOwnerDocument().getImplementation();
        }

        final DOMImplementationLS domImplLS = (DOMImplementationLS) domImpl.getFeature("LS", "3.0");
        final LSSerializer serializer = domImplLS.createLSSerializer();
        serializer.setFilter(new LSSerializerFilter() {

            public short acceptNode(Node arg0) {
                return FILTER_ACCEPT;
            }

            public int getWhatToShow() {
                return SHOW_ALL;
            }
        });

        final LSOutput serializerOut = domImplLS.createLSOutput();
        serializerOut.setByteStream(output);

        serializer.write(node, serializerOut);
    }

    /**
     * Obtain a the DOM, level 3, Load/Save serializer {@link LSSerializer} instance from the given
     * {@link DOMImplementationLS} instance.
     * 
     * <p>
     * The serializer instance will be configured with the parameters passed as the <code>serializerParams</code>
     * argument. It will also be configured with an {@link LSSerializerFilter} that shows all nodes to the filter, and
     * accepts all nodes shown.
     * </p>
     * 
     * @param domImplLS the DOM Level 3 Load/Save implementation to use
     * @param serializerParams parameters to pass to the {@link DOMConfiguration} of the serializer instance, obtained
     *            via {@link LSSerializer#getDomConfig()}. May be null.
     * 
     * @return a new LSSerializer instance
     */
    public static LSSerializer getLsSerializer(final DOMImplementationLS domImplLS,
            final Map<String, Object> serializerParams) {
        final LSSerializer serializer = domImplLS.createLSSerializer();

        serializer.setFilter(new LSSerializerFilter() {

            public short acceptNode(Node arg0) {
                return FILTER_ACCEPT;
            }

            public int getWhatToShow() {
                return SHOW_ALL;
            }
        });

        if (serializerParams != null) {
            final DOMConfiguration serializerDOMConfig = serializer.getDomConfig();
            for (String key : serializerParams.keySet()) {
                serializerDOMConfig.setParameter(key, serializerParams.get(key));
            }
        }

        return serializer;
    }

    /**
     * Gets the DOM, level 3, Load/Store implementation associated with the given node.
     * 
     * @param node the node, never null
     * 
     * @return the Load/Store implementation, never null
     */
    public static DOMImplementationLS getDomLsImplementation(final Node node) {
        final DOMImplementation domImpl;
        if (node instanceof Document) {
            domImpl = ((Document) node).getImplementation();
        } else {
            domImpl = node.getOwnerDocument().getImplementation();
        }

        return (DOMImplementationLS) domImpl.getFeature("LS", "3.0");
    }
}