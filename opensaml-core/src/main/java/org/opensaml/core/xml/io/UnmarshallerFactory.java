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

package org.opensaml.core.xml.io;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.xml.DOMTypeSupport;
import net.shibboleth.utilities.java.support.xml.QNameSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * This thread-safe factory creates {@link org.opensaml.core.xml.io.Unmarshaller}s that can be used to convert W3C DOM
 * elements into {@link org.opensaml.core.xml.XMLObject}s. Unmarshallers are stored and retrieved by a
 * {@link javax.xml.namespace.QName} key. This key is either the XML Schema Type or element QName of the XML element
 * being unmarshalled.
 */
public class UnmarshallerFactory {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(UnmarshallerFactory.class);

    /** Map of unmarshallers to the elements they are for. */
    private final Map<QName, Unmarshaller> unmarshallers;

    /**
     * Constructor.
     */
    public UnmarshallerFactory() {
        unmarshallers = new ConcurrentHashMap<>();
    }

    /**
     * Gets the Unmarshaller for a particular element or null if no unmarshaller is registered for an element.
     * 
     * @param key the key the unmarshaller was registered under
     * 
     * @return the Unmarshaller
     */
    @Nullable public Unmarshaller getUnmarshaller(@Nullable final QName key) {
        if (key == null) {
            return null;
        }

        return unmarshallers.get(key);
    }

    /**
     * Retrieves the unmarshaller for the given element. The schema type, if present, is tried first as the key with the
     * element QName used if no schema type is present or does not have a unmarshaller registered under it.
     * 
     * @param domElement the element to retrieve the unmarshaller for
     * 
     * @return the unmarshaller for the XMLObject the given element can be unmarshalled into
     */
    @Nullable public Unmarshaller getUnmarshaller(@Nullable final Element domElement) {
        Unmarshaller unmarshaller;

        unmarshaller = getUnmarshaller(DOMTypeSupport.getXSIType(domElement));

        if (unmarshaller == null) {
            unmarshaller = getUnmarshaller(QNameSupport.getNodeQName(domElement));
        }

        return unmarshaller;
    }

    /**
     * Gets an immutable listing of all the Unarshallers currently registered.
     * 
     * @return a listing of all the Unmarshallers currently registered
     */
    @Nonnull public Map<QName, Unmarshaller> getUnmarshallers() {
        return Collections.unmodifiableMap(unmarshallers);
    }

    /**
     * Registers an Unmarshaller with this factory. If an Unmarshaller exist for the Qname given it is replaced with the
     * given unmarshaller.
     * 
     * @param key the key the unmarshaller was registered under
     * @param unmarshaller the Unmarshaller
     */
    public void registerUnmarshaller(@Nonnull final QName key, @Nonnull final Unmarshaller unmarshaller) {
        Constraint.isNotNull(key, "Unmarshaller key cannot be null");
        Constraint.isNotNull(unmarshaller, "Unmarshaller cannot be null");
        log.debug("Registering unmarshaller, {}, for object type, {}", unmarshaller.getClass().getName(), key);
        
        unmarshallers.put(key, unmarshaller);
    }

    /**
     * Deregisters the unmarshaller for the given element.
     * 
     * @param key the key the unmarshaller was registered under
     * 
     * @return the Unmarshaller previously registered or null
     */
    @Nullable public Unmarshaller deregisterUnmarshaller(@Nonnull final QName key) {
        log.debug("Deregistering marshaller for object type {}", key);
        if (key != null) {
            return unmarshallers.remove(key);
        }

        return null;
    }
}