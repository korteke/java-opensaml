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

package org.opensaml.storage.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.annotation.constraint.Positive;
import net.shibboleth.utilities.java.support.collection.Pair;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.storage.AbstractStorageService;
import org.opensaml.storage.StorageRecord;
import org.opensaml.storage.VersionMismatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Implementation of {@link org.opensaml.storage.StorageService} that uses JPA to persist to a database.
 */
public class JPAStorageService extends AbstractStorageService {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(JPAStorageService.class);

    /** Entity manager factory. */
    private EntityManagerFactory entityManagerFactory;

    /**
     * Creates a new JPA storage service.
     * 
     * @param factory entity manager factory
     */
    public JPAStorageService(@Nonnull final EntityManagerFactory factory) {
        entityManagerFactory = Constraint.isNotNull(factory, "EntityManagerFactory cannot be null");

        setContextSize(JPAStorageRecord.CONTEXT_SIZE);
        setKeySize(JPAStorageRecord.KEY_SIZE);
        setValueSize(JPAStorageRecord.VALUE_SIZE);
    }

    /** {@inheritDoc} */
    @Override protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
    }

    /** {@inheritDoc} */
    @Override protected void doDestroy() {
        if (entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
        super.doDestroy();
    }

    /** {@inheritDoc} */
    @Override public boolean create(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key,
            @Nonnull @NotEmpty final String value, @Nullable @Positive final Long expiration) throws IOException {
        EntityManager manager = null;
        EntityTransaction transaction = null;
        try {
            manager = entityManagerFactory.createEntityManager();
            JPAStorageRecord entity = manager.find(JPAStorageRecord.class, new JPAStorageRecord.RecordId(context, key));
            if (entity != null) {
                // Not yet expired?
                final Long exp = entity.getExpiration();
                if (exp == null || System.currentTimeMillis() < exp) {
                    return false;
                }

                // It's dead, so we can just delete it.
                delete(context, key);
            }

            transaction = manager.getTransaction();
            transaction.begin();
            entity = new JPAStorageRecord();
            entity.setContext(context);
            entity.setKey(key);
            entity.setValue(value);
            entity.setExpiration(expiration);
            manager.persist(entity);
            transaction.commit();

            log.debug("Inserted record '{}' in context '{}' with expiration '{}'", new Object[] {key, context,
                    expiration,});
            return true;
        } catch (Exception e) {
            log.error("Error creating record '{}' in context '{}' with expiration '{}'", key, context, expiration, e);
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            if (manager != null && manager.isOpen()) {
                manager.close();
            }
        }
    }

    /**
     * Returns all records from the store.
     * 
     * @return all records or an empty list
     * @throws IOException if errors occur in the read process
     */
    @Nonnull public List<StorageRecord> readAll() throws IOException {
        EntityManager manager = null;
        try {
            manager = entityManagerFactory.createEntityManager();
            return executeNamedQuery(manager, "JPAStorageRecord.findAll", null, StorageRecord.class);
        } finally {
            if (manager != null && manager.isOpen()) {
                manager.close();
            }
        }
    }

    /**
     * Returns all records from the store for the supplied context.
     * 
     * @param context a storage context label
     * 
     * @return all records in the context or an empty list
     * @throws IOException if errors occur in the read process
     */
    @Nonnull public List<StorageRecord> readAll(@Nonnull @NotEmpty final String context) throws IOException {
        EntityManager manager = null;
        try {
            manager = entityManagerFactory.createEntityManager();
            final Map<String, Object> params = Maps.newHashMap();
            params.put("context", context);
            return executeNamedQuery(manager, "JPAStorageRecord.findByContext", params, StorageRecord.class);
        } finally {
            if (manager != null && manager.isOpen()) {
                manager.close();
            }
        }
    }

    /**
     * Returns all contexts from the store.
     * 
     * @return all contexts or an empty list
     * @throws IOException if errors occur in the read process
     */
    @Nonnull public List<String> readContexts() throws IOException {
        EntityManager manager = null;
        try {
            manager = entityManagerFactory.createEntityManager();
            return executeNamedQuery(manager, "JPAStorageRecord.findAllContexts", null, String.class);
        } finally {
            if (manager != null && manager.isOpen()) {
                manager.close();
            }
        }
    }

    /** {@inheritDoc} */
    @Override @Nullable public StorageRecord read(@Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key) throws IOException {
        return readImpl(context, key, null).getSecond();
    }

    /** {@inheritDoc} */
    @Override @Nonnull public Pair<Integer, StorageRecord> read(@Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key, @Positive final int version) throws IOException {
        return readImpl(context, key, version);
    }

    /**
     * Reads the record matching the supplied parameters. Returns an empty pair if the record cannot be found or is
     * expired.
     * 
     * @param context to search for
     * @param key to search for
     * @param version to match
     * 
     * @return pair of version and storage record
     * @throws IOException if errors occur in the read process
     */
    @Nonnull protected Pair<Integer, StorageRecord> readImpl(@Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key, @Positive final Integer version) throws IOException {
        EntityManager manager = null;
        try {
            manager = entityManagerFactory.createEntityManager();
            final JPAStorageRecord entity =
                    manager.find(JPAStorageRecord.class, new JPAStorageRecord.RecordId(context, key));
            if (entity == null) {
                log.debug("Read failed, key '{}' not found in context '{}'", key, context);
                return new Pair<>();
            } else {
                final Long exp = entity.getExpiration();
                if (exp != null && System.currentTimeMillis() >= exp) {
                    log.debug("Read failed, key '{}' expired in context '{}'", key, context);
                    return new Pair();
                }
            }
            if (version != null && entity.getVersion() == version) {
                // Nothing's changed, so just echo back the version.
                return new Pair(version, null);
            }
            return new Pair<Integer, StorageRecord>(entity.getVersion(), entity);
        } catch (Exception e) {
            log.error("Error reading record '{}' in context '{}'", key, context, e);
            return new Pair<>();
        } finally {
            if (manager != null && manager.isOpen()) {
                manager.close();
            }
        }
    }

    /** {@inheritDoc} */
    @Override @Nullable public Integer update(@Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key, @Nonnull @NotEmpty final String value,
            @Nullable @Positive final Long expiration) throws IOException {
        try {
            return updateImpl(null, context, key, value, expiration);
        } catch (VersionMismatchException e) {
            throw new IllegalStateException("Unexpected exception thrown by update.", e);
        }
    }

    /** {@inheritDoc} */
    @Override @Nullable public Integer updateWithVersion(@Positive final int version,
            @Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key,
            @Nonnull @NotEmpty final String value, @Nullable @Positive final Long expiration) throws IOException,
            VersionMismatchException {
        return updateImpl(version, context, key, value, expiration);
    }

    /** {@inheritDoc} */
    @Override @Nullable public Integer updateExpiration(@Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key, @Nullable @Positive final Long expiration) throws IOException {
        try {
            return updateImpl(null, context, key, null, expiration);
        } catch (VersionMismatchException e) {
            throw new IllegalStateException("Unexpected exception thrown by update.", e);
        }
    }

    /**
     * Updates the record matching the supplied parameters. Returns null if the record cannot be found or is expired.
     * 
     * @param version to check
     * @param context to search for
     * @param key to search for
     * @param value to update
     * @param expiration to update
     * 
     * @return whether the record was updated
     * @throws IOException if errors occur in the update process
     * @throws VersionMismatchException if the record found contains a version that does not match the parameter
     */
    @Nullable protected Integer updateImpl(@Nullable final Integer version, @Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key, @Nonnull @NotEmpty final String value,
            @Nullable @Positive final Long expiration) throws IOException, VersionMismatchException {
        EntityManager manager = null;
        EntityTransaction transaction = null;
        try {
            manager = entityManagerFactory.createEntityManager();
            final JPAStorageRecord entity =
                    manager.find(JPAStorageRecord.class, new JPAStorageRecord.RecordId(context, key));
            if (entity == null) {
                log.debug("Update failed, key '{}' not found in context '{}'", key, context);
                return null;
            } else {
                final Long exp = entity.getExpiration();
                if (exp != null && System.currentTimeMillis() >= exp) {
                    log.debug("Update failed, key '{}' expired in context '{}'", key, context);
                    return null;
                }
            }

            if (version != null && entity.getVersion() != version) {
                // Caller is out of sync.
                throw new VersionMismatchException();
            }

            transaction = manager.getTransaction();
            transaction.begin();
            if (value != null) {
                entity.setValue(value);
                entity.incrementVersion();
            }
            entity.setExpiration(expiration);
            manager.merge(entity);
            transaction.commit();
            log.debug("Updated record '{}' in context '{}' with expiration '{}'", new Object[] {key, context,
                    expiration,});
            return entity.getVersion();
        } catch (VersionMismatchException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating record '{}' in context '{}'", key, context, e);
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            return null;
        } finally {
            if (manager != null && manager.isOpen()) {
                manager.close();
            }
        }
    }

    /** {@inheritDoc} */
    @Override public boolean deleteWithVersion(@Positive final int version, @Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key) throws IOException, VersionMismatchException {
        return deleteImpl(version, context, key);
    }

    /** {@inheritDoc} */
    @Override public boolean delete(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key)
            throws IOException {
        try {
            return deleteImpl(null, context, key);
        } catch (VersionMismatchException e) {
            throw new IllegalStateException("Unexpected exception thrown by delete.", e);
        }
    }

    /**
     * Deletes the record matching the supplied parameters.
     * 
     * @param version to check
     * @param context to search for
     * @param key to search for
     * 
     * @return whether the record was deleted
     * @throws IOException if errors occur in the delete process
     * @throws VersionMismatchException if the record found contains a version that does not match the parameter
     */
    protected boolean deleteImpl(@Nullable @Positive final Integer version, @Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key) throws IOException, VersionMismatchException {
        EntityManager manager = null;
        EntityTransaction transaction = null;
        try {
            manager = entityManagerFactory.createEntityManager();
            final JPAStorageRecord entity =
                    manager.find(JPAStorageRecord.class, new JPAStorageRecord.RecordId(context, key));
            if (entity == null) {
                log.debug("Deleting record '{}' in context '{}'....key not found", key, context);
                return false;
            } else if (version != null && entity.getVersion() != version) {
                throw new VersionMismatchException();
            } else {
                transaction = manager.getTransaction();
                transaction.begin();
                manager.remove(entity);
                transaction.commit();
                log.debug("Deleted record '{}' in context '{}'", key, context);
                return true;
            }
        } catch (VersionMismatchException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting record '{}' in context '{}'", key, context, e);
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            if (manager != null && manager.isOpen()) {
                manager.close();
            }
        }
    }

    /** {@inheritDoc} */
    @Override public void updateContextExpiration(@Nonnull @NotEmpty final String context,
            @Nullable @Positive final Long expiration) throws IOException {
        EntityManager manager = null;
        EntityTransaction transaction = null;
        try {
            manager = entityManagerFactory.createEntityManager();
            final Map<String, Object> params = Maps.newHashMap();
            params.put("context", context);
            params.put("now", System.currentTimeMillis());
            final List<JPAStorageRecord> entities =
                    executeNamedQuery(manager, "JPAStorageRecord.findActiveByContext", params, JPAStorageRecord.class);
            if (!entities.isEmpty()) {
                transaction = manager.getTransaction();
                transaction.begin();
                for (JPAStorageRecord entity : entities) {
                    entity.setExpiration(expiration);
                }
                transaction.commit();
                log.debug("Updated expiration of valid records in context '{}' to '{}'", context, expiration);
            }
        } catch (Exception e) {
            log.error("Error updating context expiration in context '{}'", context, e);
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        } finally {
            if (manager != null && manager.isOpen()) {
                manager.close();
            }
        }
    }

    /** {@inheritDoc} */
    @Override public void deleteContext(@Nonnull @NotEmpty final String context) throws IOException {
        deleteContextImpl(context, null);
        log.debug("Deleted all entities in context '{}'", context);
    }

    /** {@inheritDoc} */
    @Override public void reap(@Nonnull @NotEmpty final String context) throws IOException {
        deleteContextImpl(context, System.currentTimeMillis());
        log.debug("Reaped all entities in context '{}'", context);
    }

    /**
     * Deletes every record with the supplied context. If expiration is supplied, only records with an expiration before
     * the supplied expiration will be removed.
     * 
     * @param context to delete
     * @param expiration (optional) to require for deletion
     * 
     * @throws IOException if errors occur in the delete process
     */
    protected void deleteContextImpl(@Nonnull @NotEmpty final String context, @Nonnull final Long expiration)
            throws IOException {
        EntityManager manager = null;
        EntityTransaction transaction = null;
        try {
            manager = entityManagerFactory.createEntityManager();
            final Map<String, Object> params = Maps.newHashMap();
            params.put("context", context);
            final List<JPAStorageRecord> entities =
                    executeNamedQuery(manager, "JPAStorageRecord.findByContext", params, JPAStorageRecord.class);
            if (!entities.isEmpty()) {
                transaction = manager.getTransaction();
                transaction.begin();
                for (JPAStorageRecord entity : entities) {
                    if (expiration == null ||
                        (entity.getExpiration() != null && entity.getExpiration() <= expiration)) {
                        manager.remove(entity);
                        log.trace("Deleted entity {}", entity);
                    }
                }
                transaction.commit();
            }
        } catch (Exception e) {
            log.error("Error deleting context '{}'", context, e);
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        } finally {
            if (manager != null && manager.isOpen()) {
                manager.close();
            }
        }
    }

    /**
     * Executes the supplied named query.
     * 
     * @param <T> type of entity to return
     * @param manager to execute the query
     * @param query to execute
     * @param params parameters for the query
     * @param clazz type of entity to return
     * 
     * @return query results or an empty list
     */
    private <T> List<T> executeNamedQuery(@Nonnull final EntityManager manager, @Nonnull @NotEmpty final String query,
            @Nonnull final Map<String, Object> params, @Nonnull final Class<T> clazz) {
        final List<T> results = Lists.newArrayList();
        try {
            final Query queryResults = manager.createNamedQuery(query, clazz);
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    queryResults.setParameter(entry.getKey(), entry.getValue());
                }
            }
            results.addAll(queryResults.getResultList());
        } catch (Exception e) {
            log.error("Error executing named query", e);
        }
        return results;
    }

    /** {@inheritDoc} */
    @Override @Nullable protected TimerTask getCleanupTask() {
        return new TimerTask() {
            /** {@inheritDoc} */
            @Override public void run() {
                log.info("Running cleanup task");
                final Long now = System.currentTimeMillis();
                List<String> contexts = null;
                try {
                    contexts = readContexts();
                } catch (IOException e) {
                    log.error("Error reading contexts", e);
                }
                if (contexts != null && !contexts.isEmpty()) {
                    for (String context : contexts) {
                        try {
                            deleteContextImpl(context, now);
                        } catch (IOException e) {
                            log.error("Error deleting records in context '{}' for timestamp '{}'", context, now, e);
                        }
                    }
                }
            }
        };
    }

}