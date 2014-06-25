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

package org.opensaml.saml.common.xml;

import java.io.InputStream;
import java.lang.ref.SoftReference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.validation.Schema;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.xml.ClasspathResolver;
import net.shibboleth.utilities.java.support.xml.SchemaBuilder;

import org.xml.sax.SAXException;

/**
 * A convenience builder for creating {@link Schema}s for validating SAML 1.0, 1.1, and 2.0.
 * 
 * <p>Additional schemas may be included in the resulting object by supplying their locations
 * to a injected {@link SchemaBuilder} object.</p>
 */
@ThreadSafe
public class SAMLSchemaBuilder {

    /** Classpath relative location of basic XML schemas. */
    @Nonnull @NonnullElements @NotEmpty private static String[] baseXMLSchemas = {
        "/schema/xml.xsd",
        "/schema/XMLSchema.xsd",
        "/schema/xmldsig-core-schema.xsd",
        "/schema/xenc-schema.xsd",
        "/schema/xmldsig11-schema.xsd",
        "/schema/xenc11-schema.xsd",
        };

    /** Classpath relative location of SOAP 1_1 schemas. */
    @Nonnull @NonnullElements @NotEmpty private static String[] soapSchemas = {
        "/schema/soap-envelope.xsd",
        };

    /** Classpath relative location of SAML 1_0 schemas. */
    @Nonnull @NonnullElements @NotEmpty private static String[] saml10Schemas = {
        "/schema/cs-sstc-schema-assertion-01.xsd",
        "/schema/cs-sstc-schema-protocol-01.xsd",
        };

    /** Classpath relative location of SAML 1_1 schemas. */
    @Nonnull @NonnullElements @NotEmpty private static String[] saml11Schemas = {
        "/schema/cs-sstc-schema-assertion-1.1.xsd",
        "/schema/cs-sstc-schema-protocol-1.1.xsd",
        };

    /** Classpath relative location of SAML 2_0 schemas. */
    @Nonnull @NonnullElements @NotEmpty private static String[] saml20Schemas = { 
        "/schema/saml-schema-assertion-2.0.xsd",
        "/schema/saml-schema-protocol-2.0.xsd",
        "/schema/saml-schema-metadata-2.0.xsd",
        "/schema/saml-schema-authn-context-2.0.xsd",
        "/schema/saml-schema-authn-context-auth-telephony-2.0.xsd",
        "/schema/saml-schema-authn-context-ip-2.0.xsd",
        "/schema/saml-schema-authn-context-ippword-2.0.xsd",
        "/schema/saml-schema-authn-context-kerberos-2.0.xsd",
        "/schema/saml-schema-authn-context-mobileonefactor-reg-2.0.xsd",
        "/schema/saml-schema-authn-context-mobileonefactor-unreg-2.0.xsd",
        "/schema/saml-schema-authn-context-mobiletwofactor-reg-2.0.xsd",
        "/schema/saml-schema-authn-context-mobiletwofactor-unreg-2.0.xsd",
        "/schema/saml-schema-authn-context-nomad-telephony-2.0.xsd",
        "/schema/saml-schema-authn-context-personal-telephony-2.0.xsd",
        "/schema/saml-schema-authn-context-pgp-2.0.xsd",
        "/schema/saml-schema-authn-context-ppt-2.0.xsd",
        "/schema/saml-schema-authn-context-pword-2.0.xsd",
        "/schema/saml-schema-authn-context-session-2.0.xsd",
        "/schema/saml-schema-authn-context-smartcard-2.0.xsd",
        "/schema/saml-schema-authn-context-smartcardpki-2.0.xsd",
        "/schema/saml-schema-authn-context-softwarepki-2.0.xsd",
        "/schema/saml-schema-authn-context-spki-2.0.xsd",
        "/schema/saml-schema-authn-context-srp-2.0.xsd",
        "/schema/saml-schema-authn-context-sslcert-2.0.xsd",
        "/schema/saml-schema-authn-context-telephony-2.0.xsd",
        "/schema/saml-schema-authn-context-timesync-2.0.xsd",
        "/schema/saml-schema-authn-context-x509-2.0.xsd",
        "/schema/saml-schema-authn-context-xmldsig-2.0.xsd",
        "/schema/saml-schema-dce-2.0.xsd",
        "/schema/saml-schema-ecp-2.0.xsd",
        "/schema/saml-schema-x500-2.0.xsd",
        "/schema/saml-schema-xacml-2.0.xsd",
        };

    /** Classpath relative location of SAML extension schemas. */
    @Nonnull @NonnullElements @NotEmpty private static String[] baseExtSchemas = {
        "/schema/sstc-saml1x-metadata.xsd",
        "/schema/sstc-saml-idp-discovery.xsd",
        "/schema/sstc-saml-protocol-ext-thirdparty.xsd",
        "/schema/sstc-saml-metadata-ext-query.xsd",
        "/schema/sstc-saml-delegation.xsd",
        "/schema/sstc-saml-metadata-ui-v1.0.xsd",
        "/schema/sstc-metadata-attr.xsd",
        "/schema/saml-metadata-rpi-v1.0.xsd",
        "/schema/sstc-saml-channel-binding-ext-v1.0.xsd",
        "/schema/saml-async-slo-v1.0.xsd",
        "/schema/ietf-kitten-sasl-saml-ec.xsd",
        };

    /** Cached copy of the schema produced by the builder. */
    @Nullable private SoftReference<Schema> cachedSchema;

    /** Reference to SAML 1.x schemas to apply. */
    @Nonnull @NonnullElements @NotEmpty private String[] saml1xSchemas;
    
    /** The builder to use. */
    @Nonnull private SchemaBuilder schemaBuilder;
    
    /** Identifies which SAML 1.x version is in use. */
    public enum SAML1Version {
        
        /** SAML 1.0. */
        SAML_10,
        
        /** SAML 1.1. */
        SAML_11,
    }
    
    /**
     * Constructor.
     * 
     * <p>A default {@link SchemaBuilder} is constructed, and injected with a
     * {@link ClasspathResolver} for resolving supplementary schemas.
     * 
     * @param ver   the SAML 1.x version to use
     */
    public SAMLSchemaBuilder(@Nonnull final SAML1Version ver) {
        if (ver == SAML1Version.SAML_11) {
            saml1xSchemas = saml11Schemas;
        } else {
            saml1xSchemas = saml10Schemas;
        }
        schemaBuilder = new SchemaBuilder();
        schemaBuilder.setResourceResolver(new ClasspathResolver());
        configureBuilder();
    }
    
    /**
     * Set a custom {@link SchemaBuilder} to use.
     * 
     * @param builder   SchemaBuilder to use
     */
    public synchronized void setSchemaBuilder(@Nonnull final SchemaBuilder builder) {
        schemaBuilder = Constraint.isNotNull(builder, "SchemaBuilder cannot be null");
        configureBuilder();
        cachedSchema = null;
    }

    /**
     * Get a schema that can validate SAML 1.x, 2.0, and all registered extensions.
     * 
     * @return schema
     * 
     * @throws SAXException thrown if a schema object cannot be created
     */
    @Nonnull public synchronized Schema getSAMLSchema() throws SAXException {
        if (cachedSchema == null || cachedSchema.get() == null) {
            cachedSchema = new SoftReference<>(schemaBuilder.buildSchema());
        }

        return cachedSchema.get();
    }

    /**
     * Configure the appropriate {@link SchemaBuilder} with the right set of schemas.
     */
    @Nonnull private void configureBuilder() {
        
        Class<SAMLSchemaBuilder> clazz = SAMLSchemaBuilder.class;
        
        for (final String source : baseXMLSchemas) {
            final InputStream stream = clazz.getResourceAsStream(source);
            if (stream != null) {
                schemaBuilder.addSchema(stream);
            }
        }

        for (final String source : soapSchemas) {
            final InputStream stream = clazz.getResourceAsStream(source);
            if (stream != null) {
                schemaBuilder.addSchema(stream);
            }
        }

        for (final String source : saml1xSchemas) {
            final InputStream stream = clazz.getResourceAsStream(source);
            if (stream != null) {
                schemaBuilder.addSchema(stream);
            }
        }

        for (final String source : saml20Schemas) {
            final InputStream stream = clazz.getResourceAsStream(source);
            if (stream != null) {
                schemaBuilder.addSchema(stream);
            }
        }

        for (final String source : baseExtSchemas) {
            final InputStream stream = clazz.getResourceAsStream(source);
            if (stream != null) {
                schemaBuilder.addSchema(stream);
            }
        }
    }
    
}