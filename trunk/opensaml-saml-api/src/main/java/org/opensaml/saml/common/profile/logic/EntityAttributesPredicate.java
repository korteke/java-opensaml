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

package org.opensaml.saml.common.profile.logic;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.joda.time.DateTime;
import org.opensaml.core.xml.AttributeExtensibleXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.ext.saml2mdattr.EntityAttributes;
import org.opensaml.saml.saml2.common.Extensions;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.core.xml.schema.XSBase64Binary;
import org.opensaml.core.xml.schema.XSBoolean;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.schema.XSDateTime;
import org.opensaml.core.xml.schema.XSInteger;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.core.xml.schema.XSURI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Predicate to determine whether an {@link EntityDescriptor} or its parent groups contain an {@EntityAttribute}
 * extension {@link Attribute} that matches the predicate's criteria. 
 */
public class EntityAttributesPredicate implements Predicate<EntityDescriptor> {

    /** QName of attribute used to signal regex evaluation. */
    private static final QName REGEX_ATTR_NAME = new QName(null, "regex");

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(EntityAttributesPredicate.class);

    /** Whether to trim the values in the metadata before comparison. */
    private final boolean trimTags;
    
    /** Set of {@link Attribute} criteria to check for. */
    @Nonnull @NonnullElements private final Collection<Attribute> tagCriteria;
    
    /**
     * Constructor.
     * 
     * @param tags the {@link Attribute} criteria to check for
     * @param trim true iff the values found in the metadata should be trimmed before comparison
     */
    public EntityAttributesPredicate(@Nonnull @NonnullElements final Collection<Attribute> tags, final boolean trim) {
        
        Constraint.isNotNull(tags, "Attribute collection cannot be null");
        
        tagCriteria = Lists.newArrayListWithExpectedSize(tags.size());
        for (final Attribute tag : Collections2.filter(tags, Predicates.notNull())) {
            if (!tag.getAttributeValues().isEmpty()) {
                tagCriteria.add(tag);
            }
        }
        
        trimTags = trim;
    }

// Checkstyle: CyclomaticComplexity OFF
    /** {@inheritDoc} */
    @Override
    public boolean apply(@Nullable final EntityDescriptor input) {
        if (input == null) {
            return false;
        }
        
        boolean extFound = false;

        // Check for a tag match in the EntityAttributes extension of the entity and its parent(s).
        Extensions exts = input.getExtensions();
        if (exts != null) {
            final List<XMLObject> children = exts.getUnknownXMLObjects(EntityAttributes.DEFAULT_ELEMENT_NAME);
            if (!children.isEmpty() && children.get(0) instanceof EntityAttributes
                    && !((EntityAttributes) children.get(0)).getAttributes().isEmpty()) {
                extFound = true;
                
                // If we find a matching tag, we win. Each tag is treated in OR fashion.
                final EntityAttributesMatcher matcher = new EntityAttributesMatcher((EntityAttributes) children.get(0));
                if (Iterables.tryFind(tagCriteria, matcher).isPresent()) {
                    return true;
                }
            }
        }

        EntitiesDescriptor group = (EntitiesDescriptor) input.getParent();
        while (group != null) {
            exts = group.getExtensions();
            if (exts != null) {
                final List<XMLObject> children = exts.getUnknownXMLObjects(EntityAttributes.DEFAULT_ELEMENT_NAME);
                if (!children.isEmpty() && children.get(0) instanceof EntityAttributes
                        && !((EntityAttributes) children.get(0)).getAttributes().isEmpty()) {
                    extFound = true;
                    
                    // If we find a matching tag, we win. Each tag is treated in OR fashion.
                    final EntityAttributesMatcher matcher =
                            new EntityAttributesMatcher((EntityAttributes) children.get(0));
                    if (Iterables.tryFind(tagCriteria, matcher).isPresent()) {
                        return true;
                    }
                }
            }
            group = (EntitiesDescriptor) group.getParent();
        }

        if (!extFound) {
            log.debug("no EntityAttributes extension found for {}", input.getEntityID());
        }

        return false;
    }
    
    /**
     * Determines whether an {@link Attribute} criterion is satisfied by at least one {@link Attribute}
     * in an {@link EntityAttributes} extension.
     */
    private class EntityAttributesMatcher implements Predicate<Attribute> {
        
        /** Class logger. */
        @Nonnull private final Logger log = LoggerFactory.getLogger(EntityAttributesPredicate.class);
        
        /** Candidate to evaluate for a match. */
        private final EntityAttributes candidate;
        
        /**
         * Constructor.
         *
         * @param ext candidate to evaluate for a match
         */
        public EntityAttributesMatcher(@Nonnull final EntityAttributes ext) {
            candidate = Constraint.isNotNull(ext, "Candidate extension cannot be null");
        }
                
        /** {@inheritDoc} */
        @Override
        public boolean apply(@Nonnull final Attribute input) {
            final List<Attribute> attrs = candidate.getAttributes();
            final List<XMLObject> tagvals = input.getAttributeValues();

            // Track whether we've found every tag value.
            final boolean[] flags = new boolean[tagvals.size()];

            // Check each attribute/tag in the candidate.
            for (final Attribute a : attrs) {
                // Compare Name and NameFormat for a matching tag.
                if (a.getName() != null && a.getName().equals(input.getName())
                        && (input.getNameFormat() == null || Attribute.UNSPECIFIED.equals(input.getNameFormat())
                            || input.getNameFormat().equals(a.getNameFormat()))) {

                    // Check each tag value's simple content for a match.
                    for (int tagindex = 0; tagindex < tagvals.size(); ++tagindex) {
                        final XMLObject tagval = tagvals.get(tagindex);
                        final String tagvalstr = xmlObjectToString(tagval);

                        Pattern regexp = null;
                        
                        // Check for a regex flag.
                        if (tagval instanceof AttributeExtensibleXMLObject) {
                            final String reflag =
                                    ((AttributeExtensibleXMLObject) tagval).getUnknownAttributes().get(REGEX_ATTR_NAME);
                            if (reflag != null && XSBooleanValue.valueOf(reflag).getValue()) {
                                try {
                                    regexp = Pattern.compile(tagvalstr);
                                } catch (final PatternSyntaxException e) {
                                    log.error("Exception compiling expression {}", tagvalstr, e);
                                }
                            }
                        }
                        
                        final List<XMLObject> cvals = a.getAttributeValues();
                        for (final XMLObject cval : cvals) {
                            final String cvalstr = xmlObjectToString(cval);
                            if (tagvalstr != null && cvalstr != null) {
                                if (regexp != null) {
                                    if (regexp.matcher(cvalstr).matches()) {
                                        flags[tagindex] = true;
                                        break;
                                    }
                                } else if (tagvalstr.equals(cvalstr)) {
                                    flags[tagindex] = true;
                                    break;
                                } else if (trimTags) {
                                    if (tagvalstr.equals(cvalstr.trim())) {
                                        flags[tagindex] = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            for (final boolean flag : flags) {
                if (!flag) {
                    return false;
                }
            }
            
            return true;
        }
     
        /**
         * Convert an XMLObject to a String if the type of recognized.
         * 
         * @param object object to convert
         * @return the converted value, or null
         */
        @Nullable private String xmlObjectToString(@Nonnull final XMLObject object) {
            String toMatch = null;
            if (object instanceof XSString) {
                toMatch = ((XSString) object).getValue();
            } else if (object instanceof XSURI) {
                toMatch = ((XSURI) object).getValue();
            } else if (object instanceof XSBoolean) {
                toMatch = ((XSBoolean) object).getValue().getValue() ? "1" : "0";
            } else if (object instanceof XSInteger) {
                toMatch = ((XSInteger) object).getValue().toString();
            } else if (object instanceof XSDateTime) {
                final DateTime dt = ((XSDateTime) object).getValue();
                if (dt != null) {
                    toMatch = ((XSDateTime) object).getDateTimeFormatter().print(dt);
                }
            } else if (object instanceof XSBase64Binary) {
                toMatch = ((XSBase64Binary) object).getValue();
            } else if (object instanceof XSAny) {
                final XSAny wc = (XSAny) object;
                if (wc.getUnknownAttributes().isEmpty() && wc.getUnknownXMLObjects().isEmpty()) {
                    toMatch = wc.getTextContent();
                }
            }
            if (toMatch != null) {
                return toMatch;
            }
            log.warn("Unrecognized XMLObject type ({}), unable to convert to a string for comparison",
                    object.getClass().getName());
            return null;
        }
    }
// Checkstyle: CyclomaticComplexity OFF

}