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

package org.opensaml.storage;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.annotation.constraint.Positive;
import net.shibboleth.utilities.java.support.annotation.constraint.ThreadSafeAfterInit;
import net.shibboleth.utilities.java.support.collection.Pair;
import net.shibboleth.utilities.java.support.component.DestructableComponent;
import net.shibboleth.utilities.java.support.component.IdentifiedComponent;
import net.shibboleth.utilities.java.support.component.InitializableComponent;

/**
 * Generic data storage facility. Implementations will vary in how much
 * persistence they can supply, and must support a {@link StorageCapabilities}
 * interface to describe storage limitations.
 *  
 * <p>Storage is divided into "contexts" identified by a string label.
 * Keys need to be unique only within a given context, so multiple
 * components can share a single store safely as long as they
 * use different labels.</p>
 * 
 * <p>The allowable sizes for contexts and keys can vary and be reported
 * by the implementation to callers, but MUST be at least 255 characters.</p>
 * 
 * <p>Expiration is expressed in milliseconds since the beginning of the epoch,
 * or a null can be used to signify no expiration.</p>
 */
@ThreadSafeAfterInit
public interface StorageService extends InitializableComponent, DestructableComponent, IdentifiedComponent {

    /**
     * Returns the capabilities of the underlying store.
     *
     * @return interface to access the service's capabilities
     */
    @Nonnull StorageCapabilities getCapabilities();

    /**
     * Creates a new record in the store with an expiration.
     * 
     * @param context       a storage context label
     * @param key           a key unique to context
     * @param value         value to store
     * @param expiration    expiration for record, or null
     * 
     * @return  true iff record was inserted, false iff a duplicate was found
     * @throws IOException  if fatal errors occur in the insertion process
     */
    boolean create(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key,
            @Nonnull @NotEmpty final String value, @Nullable @Positive final Long expiration) throws IOException;

    /**
     * Creates a new record in the store with an expiration, using a custom serialization
     * process for an arbitrary object.
     * 
     * @param context       a storage context label
     * @param key           a key unique to context
     * @param value         object to store
     * @param serializer    custom serializer for the object
     * @param expiration    expiration for record, or null
     * 
     * @return  true iff record was inserted, false iff a duplicate was found
     * @throws IOException  if fatal errors occur in the insertion process
     */
    boolean create(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key,
            @Nonnull final Object value, @Nonnull final StorageSerializer serializer,
            @Nullable @Positive final Long expiration) throws IOException;
    
    /**
     * Creates a new record in the store using an annotated object as the source.
     * 
     * <p>The individual parameters for the creation are extracted from the object using the
     * annotations in the org.opensaml.storage.annotation package. If any are missing, or
     * a field inaccessible, a runtime exception of some kind will occur.</p>
     * 
     * @param value         object to store
     * 
     * @return  true iff record was inserted, false iff a duplicate was found
     * @throws IOException  if fatal errors occur in the insertion process
     */
    boolean create(@Nonnull final Object value) throws IOException;
    
    /**
     * Returns an existing record from the store, if one exists.
     *
     * @param context       a storage context label
     * @param key           a key unique to context
     * 
     * @return  the record read back, if present, or null
     * @throws IOException  if errors occur in the read process 
     */
    @Nullable StorageRecord read(@Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key) throws IOException;

    /**
     * Returns an existing record from the store, if one exists, and uses it to
     * update the annotated fields of a target object.
     * 
     * <p>The context and key to look up are obtained from the target object, and the
     * value and expiration are written back, using the annotations in the
     * org.opensaml.storage.annotation package. If any are missing, or
     * a field inaccessible, a runtime exception of some kind will occur.</p>
     *
     * @param value         object to look up and populate
     * 
     * @return  the updated object passed into the method, or null if no record was found 
     * @throws IOException  if errors occur in the read process 
     */
    @Nullable Object read(@Nonnull final Object value) throws IOException;
    
    /**
     * Returns an existing record from the store, along with its version.
     *
     * <p>The first member of the pair returned will contain the version of the record in the store,
     * or will be null if no record exists. The second member will contain the record read back.
     * If null, the record either didn't exist (if the first member was also null) or the record
     * was the same version as that supplied by the caller.</p>
     * 
     * @param context       a storage context label
     * @param key           a key unique to context
     * @param version       only return record if newer than supplied version
     * 
     * @return  a pair consisting of the version of the record read back, if any, and the record itself
     * @throws IOException  if errors occur in the read process 
     */
    @Nonnull Pair<Long, StorageRecord> read(@Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key, @Positive final long version) throws IOException;
    
    
    /**
     * Updates an existing record in the store.
     * 
     * @param context       a storage context label
     * @param key           a key unique to context
     * @param value         updated value
     * @param expiration    expiration for record, or null
     * 
     * @return true if the update succeeded, false if the record does not exist 
     * @throws IOException  if errors occur in the update process 
     */
    boolean update(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key,
            @Nonnull @NotEmpty final String value, @Nullable @Positive final Long expiration) throws IOException;
    
    /**
     * Updates an existing record in the store, if a version matches.
     * 
     * @param version       only update if the current version matches this value
     * @param context       a storage context label
     * @param key           a key unique to context
     * @param value         updated value
     * @param expiration    expiration for record, or null
     * 
     * @return the version of the record after update, null if no record exists
     * @throws IOException  if errors occur in the update process
     * @throws VersionMismatchException if the record has already been updated to a newer version
     */
    @Nullable Long updateWithVersion(@Positive final long version, @Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key, @Nonnull @NotEmpty final String value,
            @Nullable @Positive final Long expiration) throws IOException, VersionMismatchException;

    /**
     * Updates an existing record in the store using a custom serialization strategy.
     * 
     * @param context       a storage context label
     * @param key           a key unique to context
     * @param value         updated value
     * @param serializer    custom serializer
     * @param expiration    expiration for record, or null
     * 
     * @return true if the update succeeded, false if the record does not exist 
     * @throws IOException  if errors occur in the update process 
     */
    boolean update(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key,
            @Nonnull final Object value, @Nonnull final StorageSerializer serializer,
            @Nullable @Positive final Long expiration) throws IOException;
    
    /**
     * Updates an existing record in the store, if a version matches, using a custom serialization strategy.
     * 
     * @param version       only update if the current version matches this value
     * @param context       a storage context label
     * @param key           a key unique to context
     * @param value         updated value
     * @param serializer    custom serializer
     * @param expiration    expiration for record, or null
     * 
     * @return the version of the record after update, null if no record exists
     * @throws IOException  if errors occur in the update process
     * @throws VersionMismatchException if the record has already been updated to a newer version
     */
    // Checkstyle: ParameterNumber OFF
    @Nullable Long updateWithVersion(@Positive final long version, @Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key, @Nonnull final Object value,
            @Nonnull final StorageSerializer serializer, @Nullable @Positive final Long expiration)
                    throws IOException, VersionMismatchException;
    // Checkstyle: ParameterNumber ON

    /**
     * Updates an existing record in the store, using an annotated object as the source.
     * 
     * <p>The individual parameters for the update are extracted from the object using the
     * annotations in the org.opensaml.storage.annotation package. If any are missing, or
     * a field inaccessible, a runtime exception of some kind will occur.</p>
     * 
     * @param value         object to update from
     * 
     * @return true if the update succeeded, false if the record does not exist 
     * @throws IOException  if errors occur in the update process 
     */
    boolean update(@Nonnull final Object value) throws IOException;
    
    /**
     * Updates an existing record in the store, if a version matches, using an annotated object as the source.
     * 
     * <p>The individual parameters for the update are extracted from the object using the
     * annotations in the org.opensaml.storage.annotation package. If any are missing, or
     * a field inaccessible, a runtime exception of some kind will occur.</p>
     * 
     * @param version       only update if the current version matches this value
     * @param value         object to update from
     * 
     * @return the version of the record after update, null if no record exists
     * @throws IOException  if errors occur in the update process
     * @throws VersionMismatchException if the record has already been updated to a newer version
     */
    @Nullable Long updateWithVersion(@Positive final long version, @Nonnull final Object value)
            throws IOException, VersionMismatchException;
    
    /**
     * Updates expiration of an existing record in the store.
     * 
     * @param context       a storage context label
     * @param key           a key unique to context
     * @param expiration    expiration for record, or null
     * 
     * @return true if the update succeeded, false if the record does not exist 
     * @throws IOException  if errors occur in the update process 
     */
    boolean updateExpiration(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key,
            @Nullable @Positive final Long expiration) throws IOException;

    /**
     * Updates expiration of an existing record in the store, using an annotated object as the source.
     * 
     * <p>The individual parameters for the update are extracted from the object using the
     * annotations in the org.opensaml.storage.annotation package. If any are missing, or
     * a field inaccessible, a runtime exception of some kind will occur.</p>
     * 
     * @param value         object to update from
     * 
     * @return true if the update succeeded, false if the record does not exist 
     * @throws IOException  if errors occur in the update process
     */
    boolean updateExpiration(@Nonnull final Object value) throws IOException;
    
    
    /**
     * Deletes an existing record from the store.
     * 
     * @param context       a storage context label
     * @param key           a key unique to context
     * 
     * @return true iff the record existed and was deleted
     * @throws IOException  if errors occur in the deletion process 
     */
    boolean delete(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key)
            throws IOException;    

    /**
     * Deletes an existing record from the store if it currently has a specified version.
     * 
     * @param version       record version to delete
     * @param context       a storage context label
     * @param key           a key unique to context
     * 
     * @return true iff the record existed and was deleted
     * @throws IOException  if errors occur in the deletion process
     * @throws VersionMismatchException if the record has already been updated to a newer version
     */
    boolean deleteWithVersion(@Positive final long version, @Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key) throws IOException, VersionMismatchException;
    
    /**
     * Deletes an existing record from the store, using an annotated object as the source.
     * 
     * <p>The individual parameters for the deletion are extracted from the object using the
     * annotations in the org.opensaml.storage.annotation package. If any are missing, or
     * a field inaccessible, a runtime exception of some kind will occur.</p>
     * 
     * @param value         object to delete
     * 
     * @return true iff the record existed and was deleted
     * @throws IOException  if errors occur in the deletion process 
     */
    boolean delete(@Nonnull final Object value) throws IOException;

    /**
     * Deletes an existing record from the store, using an annotated object as the source, if it
     * currently has a specified version.
     * 
     * <p>The individual parameters for the deletion are extracted from the object using the
     * annotations in the org.opensaml.storage.annotation package. If any are missing, or
     * a field inaccessible, a runtime exception of some kind will occur.</p>
     * 
     * @param version       record version to delete
     * @param value         object to delete
     * 
     * @return true iff the record existed and was deleted
     * @throws IOException  if errors occur in the deletion process
     * @throws VersionMismatchException if the record has already been updated to a newer version
     */
    boolean deleteWithVersion(@Positive final long version, @Nonnull final Object value)
            throws IOException, VersionMismatchException;
    
    /**
     * Manually trigger a cleanup of expired records. The method <strong>MAY</strong> return without guaranteeing
     * that cleanup has already occurred.
     * 
     * @param context       a storage context label
     * 
     * @throws IOException  if errors occur in the cleanup process
     */
    void reap(@Nonnull @NotEmpty final String context) throws IOException;
    
    /**
     * Updates the expiration time of all records in the context.
     * 
     * @param context       a storage context label
     * @param expiration    a new expiration timestamp, or null
     * 
     * @throws IOException  if errors occur in the cleanup process
     */
    void updateContextExpiration(@Nonnull @NotEmpty final String context, @Nullable final Long expiration)
            throws IOException;

    /**
     * Forcibly removes all records in a given context along with any
     * associated resources devoted to maintaining the context.
     * 
     * @param context       a storage context label
     * 
     * @throws IOException  if errors occur in the cleanup process
     */
    void deleteContext(@Nonnull @NotEmpty final String context) throws IOException;
    
}