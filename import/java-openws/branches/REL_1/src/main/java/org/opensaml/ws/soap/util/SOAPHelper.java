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
import org.opensaml.xml.util.XMLHelper;

/**
 * Helper methods for working with SOAP.
 */
public final class SOAPHelper {

    /**
     * Private constructor.
     */
    private SOAPHelper() {
    }

    /**
     * Adds a <code>soap11:mustUnderstand</code> attribute to the given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * @param mustUnderstand whether mustUnderstand is true or false
     */
    public static void addSOAP11MustUnderstandAttribute(XMLObject soapObject, boolean mustUnderstand) {
        if (soapObject instanceof MustUnderstandBearing) {
            ((MustUnderstandBearing) soapObject).setSOAP11MustUnderstand(new XSBooleanValue(mustUnderstand, true));
        } else if (soapObject instanceof AttributeExtensibleXMLObject) {
            ((AttributeExtensibleXMLObject) soapObject).getUnknownAttributes().put(
                    MustUnderstandBearing.SOAP11_MUST_UNDERSTAND_ATTR_NAME,
                    new XSBooleanValue(mustUnderstand, true).toString());
        } else {
            throw new IllegalArgumentException("Specified object was neither MustUnderBearing nor AttributeExtensible");
        }
    }

    /**
     * Get the <code>soap11:mustUnderstand</code> attribute from a given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * 
     * @return value of the mustUnderstand attribute, or false if not present
     */
    public static boolean getSOAP11MustUnderstandAttribute(XMLObject soapObject) {
        if (soapObject instanceof MustUnderstandBearing) {
            XSBooleanValue value = ((MustUnderstandBearing) soapObject).isSOAP11MustUnderstandXSBoolean();
            if (value != null) {
                return value.getValue();
            }
        }
        if (soapObject instanceof AttributeExtensibleXMLObject) {
            String value = DatatypeHelper.safeTrimOrNullString(((AttributeExtensibleXMLObject) soapObject)
                    .getUnknownAttributes().get(MustUnderstandBearing.SOAP11_MUST_UNDERSTAND_ATTR_NAME));
            return DatatypeHelper.safeEquals("1", value);
        }
        return false;
    }

    /**
     * Adds a <code>soap11:actor</code> attribute to the given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * @param actorURI the URI of the actor
     */
    public static void addSOAP11ActorAttribute(XMLObject soapObject, String actorURI) {
        if (soapObject instanceof ActorBearing) {
            ((ActorBearing) soapObject).setSOAP11Actor(actorURI);
        } else if (soapObject instanceof AttributeExtensibleXMLObject) {
            ((AttributeExtensibleXMLObject) soapObject).getUnknownAttributes().put(ActorBearing.SOAP11_ACTOR_ATTR_NAME,
                    actorURI);
        } else {
            throw new IllegalArgumentException("Specified object was neither ActorBearing nor AttributeExtensible");
        }
    }

    /**
     * Gets the <code>soap11:actor</code> attribute from a given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * 
     * @return the value of the actor attribute, or null if not present
     */
    public static String getSOAP11ActorAttribute(XMLObject soapObject) {
        String value = null;
        if (soapObject instanceof ActorBearing) {
            value = DatatypeHelper.safeTrimOrNullString(((ActorBearing) soapObject).getSOAP11Actor());
            if (value != null) {
                return value;
            }
        }
        if (soapObject instanceof AttributeExtensibleXMLObject) {
            value = DatatypeHelper.safeTrimOrNullString(((AttributeExtensibleXMLObject) soapObject)
                    .getUnknownAttributes().get(ActorBearing.SOAP11_ACTOR_ATTR_NAME));
            return value;
        }
        return null;
    }

    /**
     * Adds a single encoding style to the given SOAP object. If an existing <code>soap11:encodingStyle</code> attribute
     * is present, the given style will be added to the existing list.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * @param encodingStyle the encoding style to add
     */
    public static void addSOAP11EncodingStyle(XMLObject soapObject, String encodingStyle) {
        if (soapObject instanceof EncodingStyleBearing) {
            EncodingStyleBearing esb = (EncodingStyleBearing) soapObject;
            List<String> list = esb.getSOAP11EncodingStyles();
            if (list == null) {
                list = new LazyList<String>();
                esb.setSOAP11EncodingStyles(list);
            }
            list.add(encodingStyle);
        } else if (soapObject instanceof AttributeExtensibleXMLObject) {
            AttributeMap am = ((AttributeExtensibleXMLObject) soapObject).getUnknownAttributes();
            String list = am.get(EncodingStyleBearing.SOAP11_ENCODING_STYLE_ATTR_NAME);
            if (list == null) {
                list = encodingStyle;
            } else {
                list = list + " " + encodingStyle;
            }
            am.put(EncodingStyleBearing.SOAP11_ENCODING_STYLE_ATTR_NAME, list);
        } else {
            throw new IllegalArgumentException(
                    "Specified object was neither EncodingStyleBearing nor AttributeExtensible");
        }
    }

    /**
     * Adds a <code>soap11:encodingStyle</code> attribute to the given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * @param encodingStyles the list of encoding styles to add
     */
    public static void addSOAP11EncodingStyles(XMLObject soapObject, List<String> encodingStyles) {
        if (soapObject instanceof EncodingStyleBearing) {
            ((EncodingStyleBearing) soapObject).setSOAP11EncodingStyles(encodingStyles);
        } else if (soapObject instanceof AttributeExtensibleXMLObject) {
            ((AttributeExtensibleXMLObject) soapObject).getUnknownAttributes().put(
                    EncodingStyleBearing.SOAP11_ENCODING_STYLE_ATTR_NAME,
                    DatatypeHelper.listToStringValue(encodingStyles, " "));
        } else {
            throw new IllegalArgumentException(
                    "Specified object was neither EncodingStyleBearing nor AttributeExtensible");
        }
    }

    /**
     * Gets the list value of the <code>soap11:encodingStyle</code> attribute from the given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * 
     * @return the list of encoding styles, or null if not present
     */
    public static List<String> getSOAP11EncodingStyles(XMLObject soapObject) {
        if (soapObject instanceof EncodingStyleBearing) {
            List<String> value = ((EncodingStyleBearing) soapObject).getSOAP11EncodingStyles();
            if (value != null) {
                return value;
            }
        }
        if (soapObject instanceof AttributeExtensibleXMLObject) {
            String value = DatatypeHelper.safeTrimOrNullString(((AttributeExtensibleXMLObject) soapObject)
                    .getUnknownAttributes().get(EncodingStyleBearing.SOAP11_ENCODING_STYLE_ATTR_NAME));
            if (value != null) {
                DatatypeHelper.stringToList(value, XMLHelper.LIST_DELIMITERS);
            }
        }
        return null;
    }

    /**
     * Adds the <code>soap12:encodingStyle</code> attribute to the given soap object.
     * 
     * @param soapObject object to which the encoding style attribute should be added
     * @param style the encoding style
     */
    public static void addSOAP12EncodingStyleAttribute(XMLObject soapObject, String style) {
        if (soapObject instanceof org.opensaml.ws.soap.soap12.EncodingStyleBearing) {
            ((org.opensaml.ws.soap.soap12.EncodingStyleBearing) soapObject).setSOAP12EncodingStyle(style);
        } else if (soapObject instanceof AttributeExtensibleXMLObject) {
            ((AttributeExtensibleXMLObject) soapObject).getUnknownAttributes().put(
                    org.opensaml.ws.soap.soap12.EncodingStyleBearing.SOAP12_ENCODING_STYLE_ATTR_NAME, style);
        } else {
            throw new IllegalArgumentException(
                    "Specified object was neither EncodingStyleBearing nor AttribtueExtensible");
        }
    }

    /**
     * Gets the <code>soap12:encodingStyle</code>.
     * 
     * @param soapObject the SOAP object which may contain the encoding style
     * 
     * @return the encoding style or null if it is not set on the object
     */
    public static String getSOAP12EncodingStyleAttribute(XMLObject soapObject) {
        String style = null;
        if (soapObject instanceof org.opensaml.ws.soap.soap12.EncodingStyleBearing) {
            style = ((org.opensaml.ws.soap.soap12.EncodingStyleBearing) soapObject).getSOAP12EncodingStyle();
        }

        if (style == null && soapObject instanceof AttributeExtensibleXMLObject) {
            style = DatatypeHelper.safeTrimOrNullString(((AttributeExtensibleXMLObject) soapObject)
                    .getUnknownAttributes().get(
                            org.opensaml.ws.soap.soap12.EncodingStyleBearing.SOAP12_ENCODING_STYLE_ATTR_NAME));
        }

        return style;
    }

    /**
     * Adds a <code>soap12:mustUnderstand</code> attribute to the given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * @param mustUnderstand whether mustUnderstand is true or false
     */
    public static void addSOAP12MustUnderstandAttribute(XMLObject soapObject, boolean mustUnderstand) {
        if (soapObject instanceof org.opensaml.ws.soap.soap12.MustUnderstandBearing) {
            ((org.opensaml.ws.soap.soap12.MustUnderstandBearing) soapObject)
                    .setSOAP12MustUnderstand(new XSBooleanValue(mustUnderstand, true));
        } else if (soapObject instanceof AttributeExtensibleXMLObject) {
            ((AttributeExtensibleXMLObject) soapObject).getUnknownAttributes().put(
                    org.opensaml.ws.soap.soap12.MustUnderstandBearing.SOAP12_MUST_UNDERSTAND_ATTR_NAME,
                    new XSBooleanValue(mustUnderstand, true).toString());
        } else {
            throw new IllegalArgumentException("Specified object was neither MustUnderstandBearing nor AttributeExtensible");
        }
    }

    /**
     * Get the <code>soap12:mustUnderstand</code> attribute from a given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * 
     * @return value of the mustUnderstand attribute, or false if not present
     */
    public static boolean getSOAP12MustUnderstandAttribute(XMLObject soapObject) {
        if (soapObject instanceof org.opensaml.ws.soap.soap12.MustUnderstandBearing) {
            XSBooleanValue value = ((org.opensaml.ws.soap.soap12.MustUnderstandBearing) soapObject)
                    .isSOAP12MustUnderstandXSBoolean();
            if (value != null) {
                return value.getValue();
            }
        }
        if (soapObject instanceof AttributeExtensibleXMLObject) {
            String value = DatatypeHelper.safeTrimOrNullString(((AttributeExtensibleXMLObject) soapObject)
                    .getUnknownAttributes().get(
                            org.opensaml.ws.soap.soap12.MustUnderstandBearing.SOAP12_MUST_UNDERSTAND_ATTR_NAME));
            return DatatypeHelper.safeEquals("1", value);
        }
        return false;
    }

    /**
     * Adds a <code>soap12:relay</code> attribute to the given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * @param relay whether relay is true or false
     */
    public static void addSOAP12RelayAttribute(XMLObject soapObject, boolean relay) {
        if (soapObject instanceof org.opensaml.ws.soap.soap12.RelayBearing) {
            ((org.opensaml.ws.soap.soap12.RelayBearing) soapObject).setSOAP12Relay(new XSBooleanValue(relay, true));
        } else if (soapObject instanceof AttributeExtensibleXMLObject) {
            ((AttributeExtensibleXMLObject) soapObject).getUnknownAttributes().put(
                    org.opensaml.ws.soap.soap12.RelayBearing.SOAP12_RELAY_ATTR_NAME,
                    new XSBooleanValue(relay, true).toString());
        } else {
            throw new IllegalArgumentException("Specified object was neither RelyBearing nor AttributeExtensible");
        }
    }

    /**
     * Get the <code>soap12:relay</code> attribute from a given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * 
     * @return value of the relay attribute, or false if not present
     */
    public static boolean getSOAP12RelayAttribute(XMLObject soapObject) {
        if (soapObject instanceof org.opensaml.ws.soap.soap12.RelayBearing) {
            XSBooleanValue value = ((org.opensaml.ws.soap.soap12.RelayBearing) soapObject).isSOAP12RelayXSBoolean();
            if (value != null) {
                return value.getValue();
            }
        }
        if (soapObject instanceof AttributeExtensibleXMLObject) {
            String value = DatatypeHelper.safeTrimOrNullString(((AttributeExtensibleXMLObject) soapObject)
                    .getUnknownAttributes().get(org.opensaml.ws.soap.soap12.RelayBearing.SOAP12_RELAY_ATTR_LOCAL_NAME));
            return DatatypeHelper.safeEquals("1", value);
        }
        return false;
    }
    
    /**
     * Adds the <code>soap12:role</code> attribute to the given soap object.
     * 
     * @param soapObject object to which the rol attribute should be added
     * @param role the role
     */
    public static void addSOAP12RoleAttribute(XMLObject soapObject, String role) {
        if (soapObject instanceof org.opensaml.ws.soap.soap12.RoleBearing) {
            ((org.opensaml.ws.soap.soap12.RoleBearing) soapObject).setSOAP12Role(role);
        } else if (soapObject instanceof AttributeExtensibleXMLObject) {
            ((AttributeExtensibleXMLObject) soapObject).getUnknownAttributes().put(
                    org.opensaml.ws.soap.soap12.RoleBearing.SOAP12_ROLE_ATTR_NAME, role);
        } else {
            throw new IllegalArgumentException(
                    "Specified object was neither RoleBearing nor AttribtueExtensible");
        }
    }

    /**
     * Gets the <code>soap12:role</code>.
     * 
     * @param soapObject the SOAP object which may contain the role
     * 
     * @return the role or null if it is not set on the object
     */
    public static String getSOAP12RoleAttribute(XMLObject soapObject) {
        String role = null;
        if (soapObject instanceof org.opensaml.ws.soap.soap12.RoleBearing) {
            role = ((org.opensaml.ws.soap.soap12.RoleBearing) soapObject).getSOAP12Role();
        }

        if (role == null && soapObject instanceof AttributeExtensibleXMLObject) {
            role = DatatypeHelper.safeTrimOrNullString(((AttributeExtensibleXMLObject) soapObject)
                    .getUnknownAttributes().get(
                            org.opensaml.ws.soap.soap12.RoleBearing.SOAP12_ROLE_ATTR_LOCAL_NAME));
        }

        return role;
    }

    /**
     * Adds a <code>soap11:actor</code> attribute to the given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * @param actorURI the URI of the actor
     * 
     * @deprecated use instead {@link #addSOAP11ActorAttribute(XMLObject, String)}.
     */
    public static void addActorAttribute(XMLObject soapObject, String actorURI) {
        addSOAP11ActorAttribute(soapObject, actorURI);
    }

    /**
     * Adds a single encoding style to the given SOAP object. If an existing <code>soap11:encodingStyle</code> attribute
     * is present, the given style will be added to the existing list.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * @param encodingStyle the encoding style to add
     * 
     * @deprecated use instead {@link #addSOAP11EncodingStyle(XMLObject, String)}.
     */
    public static void addEncodingStyle(XMLObject soapObject, String encodingStyle) {
        addSOAP11EncodingStyle(soapObject, encodingStyle);
    }

    /**
     * Adds a <code>soap11:encodingStyle</code> attribute to the given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * @param encodingStyles the list of encoding styles to add
     * 
     * @deprecated use instead {@link #addSOAP11EncodingStyles(XMLObject, List)}.
     */
    public static void addEncodingStyles(XMLObject soapObject, List<String> encodingStyles) {
        addSOAP11EncodingStyles(soapObject, encodingStyles);
    }

    /**
     * Adds a <code>soap11:mustUnderstand</code> attribute to the given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * @param mustUnderstand whether mustUnderstand is true or false
     * 
     * @deprecated use instead {@link #addSOAP11MustUnderstandAttribute(XMLObject, boolean)}.
     */
    public static void addMustUnderstandAttribute(XMLObject soapObject, boolean mustUnderstand) {
        addSOAP11MustUnderstandAttribute(soapObject, mustUnderstand);
    }
}
