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

package org.opensaml.ws.soap.util;

import java.util.List;

import org.opensaml.ws.soap.soap11.ActorBearing;
import org.opensaml.ws.soap.soap11.EncodingStyleBearing;
import org.opensaml.ws.soap.soap11.MustUnderstandBearing;
import org.opensaml.xml.AttributeExtensibleXMLObject;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.schema.XSBooleanValue;
import org.opensaml.xml.util.AttributeMap;
import org.opensaml.xml.util.DatatypeHelper;
import org.opensaml.xml.util.LazyList;

/**
 * Helper methods for working with SOAP.
 */
public class SOAPHelper {

    /**
     * Private constructor.
     */
    private SOAPHelper() {
    }

    /**
     * Adds a "mustUnderstand" attribute to the given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * @param mustUnderstand whether mustUnderstand is true or false
     */
    public static void addMustUnderstandAttribute(XMLObject soapObject, boolean mustUnderstand) {
        if (soapObject instanceof MustUnderstandBearing) {
            ((MustUnderstandBearing)soapObject).setSOAP11MustUnderstand(new XSBooleanValue(mustUnderstand, true));
        } else if (soapObject instanceof AttributeExtensibleXMLObject) {
            ((AttributeExtensibleXMLObject)soapObject).getUnknownAttributes()
                .put(MustUnderstandBearing.SOAP11_MUST_UNDERSTAND_ATTR_NAME, 
                        new XSBooleanValue(mustUnderstand, true).toString());
        } else {
            throw new IllegalArgumentException("Specified object was neither MustUnderBearing nor AttributeExtensible");
        }
    }

    /**
     * Adds an "actor" attribute to the given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * @param actorURI the URI of the actor
     */
    public static void addActorAttribute(XMLObject soapObject, String actorURI) {
        if (soapObject instanceof ActorBearing) {
            ((ActorBearing)soapObject).setSOAP11Actor(actorURI);
        } else if (soapObject instanceof AttributeExtensibleXMLObject) {
            ((AttributeExtensibleXMLObject)soapObject).getUnknownAttributes()
                .put(ActorBearing.SOAP11_ACTOR_ATTR_NAME, actorURI);
        } else {
            throw new IllegalArgumentException("Specified object was neither ActorBearing nor AttributeExtensible");
        }
    }

    /**
     * Adds a single encoding style to the given SOAP object. If existing encodingStyles are present, the given style
     * will be added to the existing list.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * @param encodingStyle the encoding style to add
     */
    public static void addEncodingStyle(XMLObject soapObject, String encodingStyle) {
        if (soapObject instanceof EncodingStyleBearing) {
            EncodingStyleBearing esb = (EncodingStyleBearing) soapObject;
            List<String> list = esb.getSOAP11EncodingStyles();
            if (list == null) {
                list = new LazyList<String>();
                esb.setSOAP11EncodingStyles(list);
            }
            list.add(encodingStyle);
        } else if (soapObject instanceof AttributeExtensibleXMLObject) {
            AttributeMap am =  ((AttributeExtensibleXMLObject)soapObject).getUnknownAttributes();
            String list = am.get(EncodingStyleBearing.SOAP11_ENCODING_STYLE_ATTR_NAME);
            if (list == null) {
                list = encodingStyle;
            } else {
                list = list + " " + encodingStyle;
            }
            am.put(EncodingStyleBearing.SOAP11_ENCODING_STYLE_ATTR_NAME, list);
        } else {
            throw new IllegalArgumentException("Specified object was neither EncodingStyleBearing nor AttributeExtensible");
        }
    }

    /**
     * Adds an "encodingStyle" attribute to the given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * @param encodingStyles the list of encoding styles to add
     */
    public static void addEncodingStyles(XMLObject soapObject, List<String> encodingStyles) {
        if (soapObject instanceof EncodingStyleBearing) {
            ((EncodingStyleBearing)soapObject).setSOAP11EncodingStyles(encodingStyles);
        } else if (soapObject instanceof AttributeExtensibleXMLObject) {
            ((AttributeExtensibleXMLObject)soapObject).getUnknownAttributes()
                .put(EncodingStyleBearing.SOAP11_ENCODING_STYLE_ATTR_NAME, 
                        DatatypeHelper.listToStringValue(encodingStyles, " "));
        } else {
            throw new IllegalArgumentException("Specified object was neither EncodingStyleBearing nor AttributeExtensible");
        }
    }
}
