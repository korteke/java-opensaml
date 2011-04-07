/*
 * Copyright 2010 University Corporation for Advanced Internet Development, Inc.
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

package org.opensaml.util.xml;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.opensaml.util.resource.Resource;
import org.opensaml.util.resource.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/** A helper class for building {@link Schema} from a set of input. */
public final class SchemaBuilder {

    /** Language of the schema files. */
    public static enum SchemaLanguage {

        /** W3 XML Schema. */
        XML("xsd"),

        /** OASIS RELAX NG Schema. */
        RELAX("rng");

        /** File extension used for the schema files. */
        private String schemaFileExtension;

        /**
         * Constructor.
         * 
         * @param extension file extension used for the schema files
         */
        private SchemaLanguage(String extension) {
            schemaFileExtension = extension;
        }

        /**
         * Gets the file extension used for the schema files.
         * 
         * @return file extension used for the schema files
         */
        public String getSchemaFileExtension() {
            return schemaFileExtension;
        }
    };

    /** Class logger. */
    private static final Logger LOG = LoggerFactory.getLogger(SchemaBuilder.class);

    /** Constructor. */
    private SchemaBuilder() {
    }

    /**
     * Builds a schema from the given schema sources.
     * 
     * @param lang schema language, must not be null
     * @param schemaFilesOrDirectories files or directories which contains schema sources
     * 
     * @return the constructed schema
     * 
     * @throws SAXException thrown if there is a problem converting the schema sources in to a schema
     */
    public static Schema buildSchema(final SchemaLanguage lang, final String... schemaFilesOrDirectories)
            throws SAXException {
        if (schemaFilesOrDirectories == null || schemaFilesOrDirectories.length == 0) {
            return null;
        }

        return buildSchema(lang, schemaFilesOrDirectories);
    }

    /**
     * Builds a schema from the given schema sources.
     * 
     * @param lang schema language, must not be null
     * @param schemaFilesOrDirectories files or directories which contains schema sources
     * 
     * @return the constructed schema
     * 
     * @throws SAXException thrown if there is a problem converting the schema sources in to a schema
     */
    public static Schema buildSchema(final SchemaLanguage lang, final File... schemaFilesOrDirectories)
            throws SAXException {
        if (schemaFilesOrDirectories == null || schemaFilesOrDirectories.length == 0) {
            return null;
        }

        final ArrayList<File> schemaFiles = new ArrayList<File>();
        getSchemaFiles(lang, schemaFiles, schemaFilesOrDirectories);

        if (schemaFiles.isEmpty()) {
            return null;
        }

        final ArrayList<Source> schemaSources = new ArrayList<Source>();
        for (File schemaFile : schemaFiles) {
            schemaSources.add(new StreamSource(schemaFile));
        }
        return buildSchema(lang, schemaSources.toArray(new Source[] {}));
    }

    /**
     * Builds a schema from the given schema sources.
     * 
     * @param lang schema language, must not be null
     * @param schemaSources schema source resources
     * 
     * @return the constructed schema
     * 
     * @throws SAXException thrown if there is a problem converting the schema sources in to a schema
     */
    public static Schema buildSchema(final SchemaLanguage lang, final Resource... schemaSources) throws SAXException {
        if (schemaSources == null || schemaSources.length == 0) {
            return null;
        }

        final ArrayList<Source> sourceStreams = new ArrayList<Source>();
        for (Resource schemaSource : schemaSources) {
            try {
                sourceStreams.add(new StreamSource(schemaSource.getInputStream(), schemaSource.getLocation()));
            } catch (ResourceException e) {
                throw new SAXException("Unable to read schema resource " + schemaSource.getLocation(), e);
            }
        }
        return buildSchema(lang, sourceStreams.toArray(new Source[sourceStreams.size()]));
    }

    /**
     * Builds a schema from the given schema sources.
     * 
     * @param lang schema language, must not be null
     * @param schemaSources schema sources
     * 
     * @return the constructed schema
     * 
     * @throws SAXException thrown if there is a problem converting the schema sources in to a schema
     */
    public static Schema buildSchema(final SchemaLanguage lang, final InputStream... schemaSources) throws SAXException {
        if (schemaSources == null || schemaSources.length == 0) {
            return null;
        }

        final ArrayList<StreamSource> sources = new ArrayList<StreamSource>();
        for (InputStream schemaSource : schemaSources) {
            if (schemaSource == null) {
                continue;
            }
            sources.add(new StreamSource(schemaSource));
        }

        if (sources.isEmpty()) {
            return null;
        }

        return buildSchema(lang, sources.toArray(new Source[] {}));
    }

    /**
     * Gets all of the schema files in the given set of readable files, directories or subdirectories.
     * 
     * @param lang schema language, must not be null
     * @param schemaFilesOrDirectories files and directories which may contain schema files
     * @param accumulatedSchemaFiles list that accumulates the schema files
     */
    protected static void getSchemaFiles(final SchemaLanguage lang, final List<File> accumulatedSchemaFiles,
            final File... schemaFilesOrDirectories) {

        if (lang == null) {
            throw new IllegalArgumentException("Schema language may not be null");
        }

        if (schemaFilesOrDirectories == null || schemaFilesOrDirectories.length == 0) {
            return;
        }

        for (File handle : schemaFilesOrDirectories) {
            if (handle == null) {
                continue;
            }

            if (!handle.canRead()) {
                LOG.debug("Ignoring '{}', no read permission", handle.getAbsolutePath());
            }

            if (handle.isFile() && handle.getName().endsWith(lang.getSchemaFileExtension())) {
                LOG.debug("Added schema source '{}'", handle.getAbsolutePath());
                accumulatedSchemaFiles.add(handle);
            }

            if (handle.isDirectory()) {
                getSchemaFiles(lang, accumulatedSchemaFiles, handle.listFiles());
            }
        }
    }

    /**
     * Builds a schema from the given schema sources.
     * 
     * @param lang schema language, must not be null
     * @param schemaSources schema sources, must not be null
     * 
     * @return the constructed schema
     * 
     * @throws SAXException thrown if there is a problem converting the schema sources in to a schema
     */
    protected static Schema buildSchema(final SchemaLanguage lang, final Source... schemaSources) throws SAXException {
        if (lang == null) {
            throw new IllegalArgumentException("Schema language may not be null");
        }

        if (schemaSources == null) {
            throw new IllegalArgumentException("Schema sources may not be null");
        }

        final SchemaFactory schemaFactory;
        if (lang == SchemaLanguage.XML) {
            schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        } else {
            schemaFactory = SchemaFactory.newInstance(XMLConstants.RELAXNG_NS_URI);
        }

        schemaFactory.setErrorHandler(new LoggingErrorHandler(LoggerFactory.getLogger(SchemaBuilder.class)));
        return schemaFactory.newSchema(schemaSources);
    }
}