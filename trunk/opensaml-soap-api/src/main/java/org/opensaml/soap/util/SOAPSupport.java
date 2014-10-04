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

package org.opensaml.soap.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.collection.LazyList;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.xml.XMLConstants;

import org.opensaml.core.xml.AttributeExtensibleXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.ActorBearing;
import org.opensaml.soap.soap11.Detail;
import org.opensaml.soap.soap11.EncodingStyleBearing;
import org.opensaml.soap.soap11.Envelope;
import org.opensaml.soap.soap11.Fault;
import org.opensaml.soap.soap11.FaultActor;
import org.opensaml.soap.soap11.FaultCode;
import org.opensaml.soap.soap11.FaultString;
import org.opensaml.soap.soap11.Header;
import org.opensaml.soap.soap11.MustUnderstandBearing;

import com.google.common.base.Objects;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 * Helper methods for working with SOAP.
 */
public final class SOAPSupport {

    /**
     * Private constructor.
     */
    private SOAPSupport() {
    }

    /**
     * Adds a <code>soap11:mustUnderstand</code> attribute to the given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * @param mustUnderstand whether mustUnderstand is true or false
     */
    public static void addSOAP11MustUnderstandAttribute(@Nonnull final XMLObject soapObject, boolean mustUnderstand) {
        if (soapObject instanceof MustUnderstandBearing) {
            ((MustUnderstandBearing) soapObject).setSOAP11MustUnderstand(new XSBooleanValue(mustUnderstand, true));
        } else if (soapObject instanceof AttributeExtensibleXMLObject) {
            ((AttributeExtensibleXMLObject) soapObject).getUnknownAttributes().put(
                    MustUnderstandBearing.SOAP11_MUST_UNDERSTAND_ATTR_NAME,
                    new XSBooleanValue(mustUnderstand, true).toString());
        } else {
            throw new IllegalArgumentException(
                    "Specified object was neither MustUnderstandBearing nor AttributeExtensible");
        }
    }

    /**
     * Get the <code>soap11:mustUnderstand</code> attribute from a given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * 
     * @return value of the mustUnderstand attribute, or false if not present
     */
    public static boolean getSOAP11MustUnderstandAttribute(@Nonnull final XMLObject soapObject) {
        if (soapObject instanceof MustUnderstandBearing) {
            XSBooleanValue value = ((MustUnderstandBearing) soapObject).isSOAP11MustUnderstandXSBoolean();
            if (value != null) {
                return value.getValue();
            }
        }
        if (soapObject instanceof AttributeExtensibleXMLObject) {
            String value = StringSupport.trimOrNull(((AttributeExtensibleXMLObject) soapObject)
                    .getUnknownAttributes().get(MustUnderstandBearing.SOAP11_MUST_UNDERSTAND_ATTR_NAME));
            return Objects.equal("1", value) || Objects.equal("true", value);
        }
        return false;
    }

    /**
     * Adds a <code>soap11:actor</code> attribute to the given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * @param actorURI the URI of the actor
     */
    public static void addSOAP11ActorAttribute(@Nonnull final XMLObject soapObject, @Nonnull final String actorURI) {
        String value = Constraint.isNotNull(StringSupport.trimOrNull(actorURI), "Actor URI cannot be null or empty");
        if (soapObject instanceof ActorBearing) {
            ((ActorBearing) soapObject).setSOAP11Actor(value);
        } else if (soapObject instanceof AttributeExtensibleXMLObject) {
            ((AttributeExtensibleXMLObject) soapObject).getUnknownAttributes().put(ActorBearing.SOAP11_ACTOR_ATTR_NAME,
                    value);
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
    @Nullable public static String getSOAP11ActorAttribute(@Nonnull final XMLObject soapObject) {
        String value = null;
        if (soapObject instanceof ActorBearing) {
            value = StringSupport.trimOrNull(((ActorBearing) soapObject).getSOAP11Actor());
            if (value != null) {
                return value;
            }
        }
        if (soapObject instanceof AttributeExtensibleXMLObject) {
            value = StringSupport.trimOrNull(((AttributeExtensibleXMLObject) soapObject)
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
    public static void addSOAP11EncodingStyle(@Nonnull final XMLObject soapObject,
            @Nonnull final String encodingStyle) {
        String value = Constraint.isNotNull(StringSupport.trimOrNull(encodingStyle),
                "Encoding style to add cannot be null or empty");
        if (soapObject instanceof EncodingStyleBearing) {
            EncodingStyleBearing esb = (EncodingStyleBearing) soapObject;
            List<String> list = esb.getSOAP11EncodingStyles();
            if (list == null) {
                list = new LazyList<String>();
                esb.setSOAP11EncodingStyles(list);
            }
            list.add(value);
        } else if (soapObject instanceof AttributeExtensibleXMLObject) {
            AttributeMap am = ((AttributeExtensibleXMLObject) soapObject).getUnknownAttributes();
            String list = am.get(EncodingStyleBearing.SOAP11_ENCODING_STYLE_ATTR_NAME);
            if (list == null) {
                list = value;
            } else {
                list = list + " " + value;
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
    public static void addSOAP11EncodingStyles(@Nonnull final XMLObject soapObject,
            @Nonnull final List<String> encodingStyles) {
        Constraint.isNotEmpty(encodingStyles, "Encoding styles list cannot be empty");
        
        if (soapObject instanceof EncodingStyleBearing) {
            ((EncodingStyleBearing) soapObject).setSOAP11EncodingStyles(encodingStyles);
        } else if (soapObject instanceof AttributeExtensibleXMLObject) {
            ((AttributeExtensibleXMLObject) soapObject).getUnknownAttributes().put(
                    EncodingStyleBearing.SOAP11_ENCODING_STYLE_ATTR_NAME,
                    StringSupport.listToStringValue(encodingStyles, " "));
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
    @Nullable public static List<String> getSOAP11EncodingStyles(@Nonnull final XMLObject soapObject) {
        if (soapObject instanceof EncodingStyleBearing) {
            List<String> value = ((EncodingStyleBearing) soapObject).getSOAP11EncodingStyles();
            if (value != null) {
                return value;
            }
        }
        if (soapObject instanceof AttributeExtensibleXMLObject) {
            String value = StringSupport.trimOrNull(((AttributeExtensibleXMLObject) soapObject)
                    .getUnknownAttributes().get(EncodingStyleBearing.SOAP11_ENCODING_STYLE_ATTR_NAME));
            if (value != null) {
                StringSupport.stringToList(value, XMLConstants.LIST_DELIMITERS);
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
    public static void addSOAP12EncodingStyleAttribute(@Nonnull final XMLObject soapObject,
            @Nonnull final String style) {
        String value = Constraint.isNotNull(StringSupport.trimOrNull(style),
                "Encoding style to add cannot be null or empty");
        
        if (soapObject instanceof org.opensaml.soap.soap12.EncodingStyleBearing) {
            ((org.opensaml.soap.soap12.EncodingStyleBearing) soapObject).setSOAP12EncodingStyle(value);
        } else if (soapObject instanceof AttributeExtensibleXMLObject) {
            ((AttributeExtensibleXMLObject) soapObject).getUnknownAttributes().put(
                    org.opensaml.soap.soap12.EncodingStyleBearing.SOAP12_ENCODING_STYLE_ATTR_NAME, value);
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
    @Nullable public static String getSOAP12EncodingStyleAttribute(@Nonnull final XMLObject soapObject) {
        String style = null;
        if (soapObject instanceof org.opensaml.soap.soap12.EncodingStyleBearing) {
            style = ((org.opensaml.soap.soap12.EncodingStyleBearing) soapObject).getSOAP12EncodingStyle();
        }

        if (style == null && soapObject instanceof AttributeExtensibleXMLObject) {
            style = StringSupport.trimOrNull(((AttributeExtensibleXMLObject) soapObject)
                    .getUnknownAttributes().get(
                            org.opensaml.soap.soap12.EncodingStyleBearing.SOAP12_ENCODING_STYLE_ATTR_NAME));
        }

        return style;
    }

    /**
     * Adds a <code>soap12:mustUnderstand</code> attribute to the given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * @param mustUnderstand whether mustUnderstand is true or false
     */
    public static void addSOAP12MustUnderstandAttribute(@Nonnull final XMLObject soapObject, boolean mustUnderstand) {
        if (soapObject instanceof org.opensaml.soap.soap12.MustUnderstandBearing) {
            ((org.opensaml.soap.soap12.MustUnderstandBearing) soapObject)
                    .setSOAP12MustUnderstand(new XSBooleanValue(mustUnderstand, false));
        } else if (soapObject instanceof AttributeExtensibleXMLObject) {
            ((AttributeExtensibleXMLObject) soapObject).getUnknownAttributes().put(
                    org.opensaml.soap.soap12.MustUnderstandBearing.SOAP12_MUST_UNDERSTAND_ATTR_NAME,
                    new XSBooleanValue(mustUnderstand, false).toString());
        } else {
            throw new IllegalArgumentException(
                    "Specified object was neither MustUnderstandBearing nor AttributeExtensible");
        }
    }

    /**
     * Get the <code>soap12:mustUnderstand</code> attribute from a given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * 
     * @return value of the mustUnderstand attribute, or false if not present
     */
    public static boolean getSOAP12MustUnderstandAttribute(@Nonnull final XMLObject soapObject) {
        if (soapObject instanceof org.opensaml.soap.soap12.MustUnderstandBearing) {
            XSBooleanValue value = ((org.opensaml.soap.soap12.MustUnderstandBearing) soapObject)
                    .isSOAP12MustUnderstandXSBoolean();
            if (value != null) {
                return value.getValue();
            }
        }
        if (soapObject instanceof AttributeExtensibleXMLObject) {
            String value = StringSupport.trimOrNull(((AttributeExtensibleXMLObject) soapObject)
                    .getUnknownAttributes().get(
                            org.opensaml.soap.soap12.MustUnderstandBearing.SOAP12_MUST_UNDERSTAND_ATTR_NAME));
            return Objects.equal("1", value) || Objects.equal("true", value);
        }
        return false;
    }

    /**
     * Adds a <code>soap12:relay</code> attribute to the given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * @param relay whether relay is true or false
     */
    public static void addSOAP12RelayAttribute(@Nonnull final XMLObject soapObject, boolean relay) {
        if (soapObject instanceof org.opensaml.soap.soap12.RelayBearing) {
            ((org.opensaml.soap.soap12.RelayBearing) soapObject).setSOAP12Relay(new XSBooleanValue(relay, false));
        } else if (soapObject instanceof AttributeExtensibleXMLObject) {
            ((AttributeExtensibleXMLObject) soapObject).getUnknownAttributes().put(
                    org.opensaml.soap.soap12.RelayBearing.SOAP12_RELAY_ATTR_NAME,
                    new XSBooleanValue(relay, false).toString());
        } else {
            throw new IllegalArgumentException("Specified object was neither RelayBearing nor AttributeExtensible");
        }
    }

    /**
     * Get the <code>soap12:relay</code> attribute from a given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * 
     * @return value of the relay attribute, or false if not present
     */
    public static boolean getSOAP12RelayAttribute(@Nonnull final XMLObject soapObject) {
        if (soapObject instanceof org.opensaml.soap.soap12.RelayBearing) {
            XSBooleanValue value = ((org.opensaml.soap.soap12.RelayBearing) soapObject).isSOAP12RelayXSBoolean();
            if (value != null) {
                return value.getValue();
            }
        }
        if (soapObject instanceof AttributeExtensibleXMLObject) {
            String value = StringSupport.trimOrNull(((AttributeExtensibleXMLObject) soapObject)
                    .getUnknownAttributes().get(org.opensaml.soap.soap12.RelayBearing.SOAP12_RELAY_ATTR_LOCAL_NAME));
            return Objects.equal("1", value) || Objects.equal("true", value);
        }
        return false;
    }
    
    /**
     * Adds the <code>soap12:role</code> attribute to the given soap object.
     * 
     * @param soapObject object to which the rol attribute should be added
     * @param role the role
     */
    public static void addSOAP12RoleAttribute(@Nonnull final XMLObject soapObject, @Nonnull final String role) {
        String value = Constraint.isNotNull(StringSupport.trimOrNull(role), "Role cannot be null or empty");
        
        if (soapObject instanceof org.opensaml.soap.soap12.RoleBearing) {
            ((org.opensaml.soap.soap12.RoleBearing) soapObject).setSOAP12Role(value);
        } else if (soapObject instanceof AttributeExtensibleXMLObject) {
            ((AttributeExtensibleXMLObject) soapObject).getUnknownAttributes().put(
                    org.opensaml.soap.soap12.RoleBearing.SOAP12_ROLE_ATTR_NAME, value);
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
    @Nullable public static String getSOAP12RoleAttribute(@Nonnull final XMLObject soapObject) {
        String role = null;
        if (soapObject instanceof org.opensaml.soap.soap12.RoleBearing) {
            role = ((org.opensaml.soap.soap12.RoleBearing) soapObject).getSOAP12Role();
        }

        if (role == null && soapObject instanceof AttributeExtensibleXMLObject) {
            role = StringSupport.trimOrNull(((AttributeExtensibleXMLObject) soapObject)
                    .getUnknownAttributes().get(
                            org.opensaml.soap.soap12.RoleBearing.SOAP12_ROLE_ATTR_LOCAL_NAME));
        }

        return role;
    }

    /**
     * Add a header block to the SOAP envelope contained within the specified message context's
     * SOAP subcontext.
     * 
     * @param messageContext the message context being processed
     * @param headerBlock the header block to add
     */
    public static void addHeaderBlock(@Nonnull final MessageContext messageContext,
            @Nonnull final XMLObject headerBlock) {
        Constraint.isNotNull(messageContext, "Message context cannot be null");
        
        // SOAP 1.1 Envelope
        final SOAP11Context soap11 = messageContext.getSubcontext(SOAP11Context.class);
        if (soap11 != null && soap11.getEnvelope() != null) {
            addSOAP11HeaderBlock(soap11.getEnvelope(), headerBlock);
        } else {
            //TODO SOAP 1.2 support when object providers are implemented
            throw new IllegalArgumentException("Message context did not contain a SOAP Envelope");
        }
    }

    /**
     * Add a header to the SOAP 1.1 Envelope.
     * 
     * @param envelope the SOAP 1.1 envelope to process
     * @param headerBlock the header to add
     */
    public static void addSOAP11HeaderBlock(@Nonnull final Envelope envelope, @Nonnull final XMLObject headerBlock) {
        Constraint.isNotNull(envelope, "Envelope cannot be null");
        Constraint.isNotNull(headerBlock, "Header block cannot be null");
        
        Header envelopeHeader = envelope.getHeader();
        if (envelopeHeader == null) {
            envelopeHeader = (Header) XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(
                    Header.DEFAULT_ELEMENT_NAME).buildObject(Header.DEFAULT_ELEMENT_NAME);
            envelope.setHeader(envelopeHeader);
        }
        
        envelopeHeader.getUnknownXMLObjects().add(headerBlock);
    }

    /**
     * Get a header block from the SOAP envelope contained within the specified message context's
     * SOAP subcontext.
     * 
     * @param messageContext the message context being processed
     * @param headerName the name of the header block to return 
     * @param targetNodes the explicitly specified SOAP node actors (1.1) or roles (1.2) for which the header is desired
     * @param isFinalDestination true specifies that headers targeted for message final destination should be returned,
     *          false means they should not be returned
     * @return the list of matching header blocks
     */
    @Nonnull public static List<XMLObject> getInboundHeaderBlock(
            @Nonnull final MessageContext messageContext, @Nonnull final QName headerName,
            @Nullable Set<String> targetNodes, boolean isFinalDestination) {
        Constraint.isNotNull(messageContext, "Message context cannot be null");
        
        final SOAP11Context soap11 = messageContext.getSubcontext(SOAP11Context.class);
        
        // SOAP 1.1 Envelope
        if (soap11 != null && soap11.getEnvelope() != null) {
            return getSOAP11HeaderBlock(soap11.getEnvelope(), headerName, targetNodes, isFinalDestination);
        }
        
        //TODO SOAP 1.2 support when object providers are implemented
        return Collections.emptyList();
    }

    /**
     * Get a header block from the SOAP 1.1 envelope.
     * 
     * @param envelope the SOAP 1.1 envelope to process 
     * @param headerName the name of the header block to return 
     * @param targetNodes the explicitly specified SOAP node actors for which the header is desired
     * @param isFinalDestination true specifies that headers targeted for message final destination should be returned,
     *          false specifies they should not be returned
     * @return the list of matching header blocks
     */
    @Nonnull public static List<XMLObject> getSOAP11HeaderBlock(@Nonnull final Envelope envelope,
            @Nonnull final QName headerName, @Nullable final Set<String> targetNodes, boolean isFinalDestination) {
        Constraint.isNotNull(envelope, "Envelope cannot be null");
        Constraint.isNotNull(headerName, "Header name cannot be null");
        
        Header envelopeHeader = envelope.getHeader();
        if (envelopeHeader == null) {
            return Collections.emptyList();
        }
        
        LazyList<XMLObject> headers = new LazyList<XMLObject>();
        for (XMLObject header : envelopeHeader.getUnknownXMLObjects(headerName)) {
            if (isSOAP11HeaderTargetedToNode(header, targetNodes, isFinalDestination)) {
                headers.add(header);
            }
        }
        
        return headers;
    }
    
    /**
     * Evaluate whether the specified header block is targeted to a SOAP 1.1 node given the specified 
     * parameters.
     * 
     * @param header the header to evaluate
     * @param nodeActors the explicitly specified node actors for which the header is desired
     * @param isFinalDestination true specifies that headers targeted for message final destination should be returned,
     *          false specifies they should not be returned
     * @return the list of matching header blocks
     */
    public static boolean isSOAP11HeaderTargetedToNode(@Nonnull final XMLObject header,
            @Nullable final Set<String> nodeActors, boolean isFinalDestination) {
        String headerActor = getSOAP11ActorAttribute(header);
        if (headerActor == null) {
            if (isFinalDestination) {
                return true;
            }
        } else if (ActorBearing.SOAP11_ACTOR_NEXT.equals(headerActor)) {
            return true;
        } else if (nodeActors != null && nodeActors.contains(headerActor)) {
            return true;
        }
        return false;
    }

    /**
     * Determine whether the message represented by the message context 
     * contains a SOAP Envelope.
     * 
     * @param messageContext the current message context
     * @return true iff the message context contains a SOAP Envelope
     */
    public static boolean isSOAPMessage(@Nonnull final MessageContext<? extends XMLObject> messageContext) {
        Constraint.isNotNull(messageContext, "Message context cannot be null");
        
        XMLObject inboundMessage = messageContext.getMessage();
        if (inboundMessage == null) {
            return false;
        }
        // SOAP 1.1 Envelope
        if (inboundMessage instanceof Envelope) {
            return true;
        }
        //TODO SOAP 1.2 support when object providers are implemented
        return false;
    }

// Checkstyle: CyclomaticComplexity OFF
    /**
     * Build a SOAP 1.1. Fault element.
     * 
     * @param faultCode the 'faultcode' QName (required)
     * @param faultString the 'faultstring' value (required)
     * @param faultActor the 'faultactor' value (may be null)
     * @param detailChildren the 'detail' child elements
     * @param detailAttributes the 'detail' element attributes
     * @return the new Fault element object
     */
    public static Fault buildSOAP11Fault(@Nonnull final QName faultCode, @Nonnull final String faultString,
            @Nullable final String faultActor, @Nullable final List<XMLObject> detailChildren,
            @Nullable final Map<QName, String> detailAttributes) {
        Constraint.isNotNull(faultCode, "faultcode cannot be null");
        Constraint.isNotNull(faultString, "faultstring cannot be null");
        
        XMLObjectBuilderFactory builderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory(); 
        
        final Fault faultObj =  (Fault) builderFactory.getBuilder(Fault.DEFAULT_ELEMENT_NAME)
            .buildObject(Fault.DEFAULT_ELEMENT_NAME);
        final FaultCode faultCodeObj =  (FaultCode) builderFactory.getBuilder(FaultCode.DEFAULT_ELEMENT_NAME)
            .buildObject(FaultCode.DEFAULT_ELEMENT_NAME);
        final FaultString faultStringObj =  (FaultString) builderFactory.getBuilder(FaultString.DEFAULT_ELEMENT_NAME)
            .buildObject(FaultString.DEFAULT_ELEMENT_NAME);
        
        faultCodeObj.setValue(faultCode);
        faultObj.setCode(faultCodeObj);

        faultStringObj.setValue(faultString);
        faultObj.setMessage(faultStringObj);
        
        if (faultActor != null) {
            final FaultActor faultActorObj =  (FaultActor) builderFactory.getBuilder(FaultActor.DEFAULT_ELEMENT_NAME)
                .buildObject(FaultActor.DEFAULT_ELEMENT_NAME);
            faultActorObj.setValue(faultActor);
            faultObj.setActor(faultActorObj);
        }
            
        Detail detailObj = null;
        if (detailChildren != null && !detailChildren.isEmpty()) {
            detailObj = (Detail) builderFactory.getBuilder(Detail.DEFAULT_ELEMENT_NAME)
                .buildObject(Detail.DEFAULT_ELEMENT_NAME);
            for (XMLObject xo : Iterables.filter(detailChildren, Predicates.notNull())) {
                detailObj.getUnknownXMLObjects().add(xo);
            }
        }
        if (detailAttributes != null && !detailAttributes.isEmpty()) {
            if (detailObj == null) {
                detailObj = (Detail) builderFactory.getBuilder(Detail.DEFAULT_ELEMENT_NAME)
                    .buildObject(Detail.DEFAULT_ELEMENT_NAME);
            }
            for (final Entry<QName,String> entry : detailAttributes.entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null) {
                    detailObj.getUnknownAttributes().put(entry.getKey(), entry.getValue());
                }
            }
        }
        if (detailObj != null && 
                (!detailObj.getUnknownXMLObjects().isEmpty() || !detailObj.getUnknownAttributes().isEmpty())) {
            faultObj.setDetail(detailObj);
        }
        
        return faultObj;
    }
// Checkstyle: CyclomaticComplexity ON
    
}