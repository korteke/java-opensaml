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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.annotation.constraint.Positive;
import net.shibboleth.utilities.java.support.collection.Pair;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.storage.AbstractStorageService;
import org.opensaml.storage.StorageRecord;
import org.opensaml.storage.VersionMismatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link org.opensaml.storage.StorageService} that uses JPA to persist to a database.
 */
public class JPAStorageService extends AbstractStorageService {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(JPAStorageService.class);

    /** Entity manager factory. */
    @Nonnull private final EntityManagerFactory entityManagerFactory;

    /**
     * Creates a new JPA storage service.
     * 
     * @param factory entity manager factory
     */
    public JPAStorageService(@Nonnull final EntityManagerFactory factory) {
        entityManagerFactory = Constraint.isNotNull(factory, "EntityManagerFactory cannot be null");

        setContextSize(JPAStorageRecord.CONTEXT_SIZE);
        setKeySize(JPAStorageRecord.KEY_SIZE);
        setValueSize(Integer.MAX_VALUE);
    }

    /** {@inheritDoc} */
    @Override protected void doDestroy() {
        if (entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
        super.doDestroy();
    }

    // Checkstyle: CyclomaticComplexity OFF
    /** {@inheritDoc} */
    @Override public boolean create(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key,
            @Nonnull @NotEmpty final String value, @Nullable @Positive final Long expiration) throws IOException {
        EntityManager manager = null;
        EntityTransaction transaction = null;
        try {
            manager = entityManagerFactory.createEntityManager();
            transaction = manager.getTransaction();
            transaction.begin();
            JPAStorageRecord entity =
                    manager.find(JPAStorageRecord.class, new JPAStorageRecord.RecordId(context, key),
                            LockModeType.PESSIMISTIC_WRITE);
            if (entity != null) {
                // Not yet expired?
                final Long exp = entity.getExpiration();
                if (exp == null || System.currentTimeMillis() < exp) {
                    log.debug("Duplicate record '{}' in context '{}' with expiration '{}'", key, context, expiration);
                    return false;
                }

                // It's dead, reset the version for merge.
                entity.resetVersion();
            } else {
                entity = new JPAStorageRecord();
                entity.setContext(context);
                entity.setKey(key);
            }

            entity.setValue(value);
            entity.setExpiration(expiration);
            manager.merge(entity);
            log.debug("Merged record '{}' in context '{}' with expiration '{}'", new Object[] {key, context,
                    expiration,});
            return true;
        } catch (final EntityExistsException e) {
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (Exception ex) {
                    log.error("Error rolling back transaction", e);
                }
            }
            log.debug("Duplicate record '{}' in context '{}' with expiration '{}'", key, context, expiration);
            return false;
        } catch (final Exception e) {
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (Exception ex) {
                    log.error("Error rolling back transaction", e);
                }
            }
            log.error("Error creating record '{}' in context '{}' with expiration '{}'", key, context, expiration, e);
            throw new IOException(e);
        } finally {
            if (transaction != null && transaction.isActive() && !transaction.getRollbackOnly()) {
                try {
                    transaction.commit();
                } catch (Exception e) {
                    log.error("Error committing transaction", e);
                }
            }
            if (manager != null && manager.isOpen()) {
                try {
                    manager.close();
                } catch (Exception e) {
                    log.error("Error closing entity manager", e);
                }
            }
        }
    }

    // Checkstyle: CyclomaticComplexity ON

    /**
     * Returns all records from the store.
     * 
     * @return all records or an empty list
     * @throws IOException if errors occur in the read process
     */
    @Nonnull @NonnullElements public List<StorageRecord> readAll() throws IOException {
        EntityManager manager = null;
        try {
            manager = entityManagerFactory.createEntityManager();
            return executeNamedQuery(manager, "JPAStorageRecord.findAll", null, StorageRecord.class,
                    LockModeType.PESSIMISTIC_READ);
        } finally {
            if (manager != null && manager.isOpen()) {
                try {
                    manager.close();
                } catch (Exception e) {
                    log.error("Error closing entity manager", e);
                }
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
    @Nonnull @NonnullElements public List<StorageRecord> readAll(@Nonnull @NotEmpty final String context)
            throws IOException {
        EntityManager manager = null;
        try {
            manager = entityManagerFactory.createEntityManager();
            final Map<String, Object> params = new HashMap<>();
            params.put("context", context);
            return executeNamedQuery(manager, "JPAStorageRecord.findByContext", params, StorageRecord.class,
                    LockModeType.PESSIMISTIC_READ);
        } finally {
            if (manager != null && manager.isOpen()) {
                try {
                    manager.close();
                } catch (Exception e) {
                    log.error("Error closing entity manager", e);
                }
            }
        }
    }

    /**
     * Returns all contexts from the store.
     * 
     * @return all contexts or an empty list
     * @throws IOException if errors occur in the read process
     */
    @Nonnull @NonnullElements public List<String> readContexts() throws IOException {
        EntityManager manager = null;
        try {
            manager = entityManagerFactory.createEntityManager();
            // this query uses the distinct keyword, it must use optimistic locking
            return executeNamedQuery(manager, "JPAStorageRecord.findAllContexts", null, String.class,
                    LockModeType.OPTIMISTIC);
        } finally {
            if (manager != null && manager.isOpen()) {
                try {
                    manager.close();
                } catch (Exception e) {
                    log.error("Error closing entity manager", e);
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override @Nullable public StorageRecord read(@Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key) throws IOException {
        return readImpl(context, key, null).getSecond();
    }

    /** {@inheritDoc} */
    @Override @Nonnull public Pair<Long, StorageRecord> read(@Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key, @Positive final long version) throws IOException {
        return readImpl(context, key, version);
    }

    // Checkstyle: CyclomaticComplexity OFF
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
    @Nonnull protected Pair<Long, StorageRecord> readImpl(@Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key, @Positive final Long version) throws IOException {
        EntityManager manager = null;
        EntityTransaction transaction = null;
        try {
            manager = entityManagerFactory.createEntityManager();
            transaction = manager.getTransaction();
            transaction.begin();
            final JPAStorageRecord entity =
                    manager.find(JPAStorageRecord.class, new JPAStorageRecord.RecordId(context, key),
                            LockModeType.PESSIMISTIC_READ);
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
            return new Pair<Long, StorageRecord>(entity.getVersion(), entity);
        } catch (final Exception e) {
            log.error("Error reading record '{}' in context '{}'", key, context, e);
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (Exception ex) {
                    log.error("Error rolling back transaction", e);
                }
            }
            throw new IOException(e);
        } finally {
            if (transaction != null && transaction.isActive() && !transaction.getRollbackOnly()) {
                try {
                    transaction.commit();
                } catch (Exception e) {
                    log.error("Error committing transaction", e);
                }
            }
            if (manager != null && manager.isOpen()) {
                try {
                    manager.close();
                } catch (Exception e) {
                    log.error("Error closing entity manager", e);
                }
            }
        }
    }

    // Checkstyle: CyclomaticComplexity ON

    /** {@inheritDoc} */
    @Override public boolean update(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key,
            @Nonnull @NotEmpty final String value, @Nullable @Positive final Long expiration) throws IOException {
        try {
            return updateImpl(null, context, key, value, expiration) != null;
        } catch (final VersionMismatchException e) {
            throw new IllegalStateException("Unexpected exception thrown by update.", e);
        }
    }

    /** {@inheritDoc} */
    @Override @Nullable public Long updateWithVersion(@Positive final long version,
            @Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key,
            @Nonnull @NotEmpty final String value, @Nullable @Positive final Long expiration) throws IOException,
            VersionMismatchException {
        return updateImpl(version, context, key, value, expiration);
    }

    /** {@inheritDoc} */
    @Override public boolean updateExpiration(@Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key, @Nullable @Positive final Long expiration) throws IOException {
        try {
            return updateImpl(null, context, key, null, expiration) != null;
        } catch (final VersionMismatchException e) {
            throw new IllegalStateException("Unexpected exception thrown by update.", e);
        }
    }

    // Checkstyle: CyclomaticComplexity OFF
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
    @Nullable protected Long updateImpl(@Nullable final Long version, @Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key, @Nonnull @NotEmpty final String value,
            @Nullable @Positive final Long expiration) throws IOException, VersionMismatchException {
        EntityManager manager = null;
        EntityTransaction transaction = null;
        try {
            manager = entityManagerFactory.createEntityManager();
            transaction = manager.getTransaction();
            transaction.begin();
            final JPAStorageRecord entity =
                    manager.find(JPAStorageRecord.class, new JPAStorageRecord.RecordId(context, key),
                            LockModeType.PESSIMISTIC_WRITE);
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

            if (value != null) {
                entity.setValue(value);
                entity.incrementVersion();
            }
            entity.setExpiration(expiration);
            manager.merge(entity);
            log.debug("Merged record '{}' in context '{}' with expiration '{}'", new Object[] {key, context,
                    expiration,});
            return entity.getVersion();
        } catch (final VersionMismatchException e) {
            throw e;
        } catch (final Exception e) {
            log.error("Error updating record '{}' in context '{}'", key, context, e);
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (Exception ex) {
                    log.error("Error rolling back transaction", e);
                }
            }
            throw new IOException(e);
        } finally {
            if (transaction != null && transaction.isActive() && !transaction.getRollbackOnly()) {
                try {
                    transaction.commit();
                } catch (Exception e) {
                    log.error("Error committing transaction", e);
                }
            }
            if (manager != null && manager.isOpen()) {
                try {
                    manager.close();
                } catch (Exception e) {
                    log.error("Error closing entity manager", e);
                }
            }
        }
    }

    // Checkstyle: CyclomaticComplexity ON

    /** {@inheritDoc} */
    @Override public boolean deleteWithVersion(@Positive final long version, @Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key) throws IOException, VersionMismatchException {
        return deleteImpl(version, context, key);
    }

    /** {@inheritDoc} */
    @Override public boolean delete(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key)
            throws IOException {
        try {
            return deleteImpl(null, context, key);
        } catch (final VersionMismatchException e) {
            throw new IllegalStateException("Unexpected exception thrown by delete.", e);
        }
    }

    // Checkstyle: CyclomaticComplexity OFF
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
    protected boolean deleteImpl(@Nullable @Positive final Long version, @Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key) throws IOException, VersionMismatchException {
        EntityManager manager = null;
        EntityTransaction transaction = null;
        try {
            manager = entityManagerFactory.createEntityManager();
            transaction = manager.getTransaction();
            transaction.begin();
            final JPAStorageRecord entity =
                    manager.find(JPAStorageRecord.class, new JPAStorageRecord.RecordId(context, key),
                            LockModeType.PESSIMISTIC_WRITE);
            if (entity == null) {
                log.debug("Deleting record '{}' in context '{}'....key not found", key, context);
                return false;
            } else if (version != null && entity.getVersion() != version) {
                throw new VersionMismatchException();
            } else {
                manager.remove(entity);
                log.debug("Deleted record '{}' in context '{}'", key, context);
                return true;
            }
        } catch (final VersionMismatchException e) {
            throw e;
        } catch (final Exception e) {
            log.error("Error deleting record '{}' in context '{}'", key, context, e);
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (Exception ex) {
                    log.error("Error rolling back transaction", e);
                }
            }
            throw new IOException(e);
        } finally {
            if (transaction != null && transaction.isActive() && !transaction.getRollbackOnly()) {
                try {
                    transaction.commit();
                } catch (Exception e) {
                    log.error("Error committing transaction", e);
                }
            }
            if (manager != null && manager.isOpen()) {
                try {
                    manager.close();
                } catch (Exception e) {
                    log.error("Error closing entity manager", e);
                }
            }
        }
    }

    // Checkstyle: CyclomaticComplexity ON

    // Checkstyle: CyclomaticComplexity OFF
    /** {@inheritDoc} */
    @Override public void updateContextExpiration(@Nonnull @NotEmpty final String context,
            @Nullable @Positive final Long expiration) throws IOException {
        EntityManager manager = null;
        EntityTransaction transaction = null;
        try {
            manager = entityManagerFactory.createEntityManager();
            transaction = manager.getTransaction();
            transaction.begin();
            final Query queryResults =
                    manager.createNamedQuery("JPAStorageRecord.findActiveByContext", JPAStorageRecord.class);
            queryResults.setLockMode(LockModeType.PESSIMISTIC_WRITE);
            queryResults.setParameter("context", context);
            queryResults.setParameter("now", System.currentTimeMillis());
            final List<JPAStorageRecord> entities = queryResults.getResultList();
            if (!entities.isEmpty()) {
                for (final JPAStorageRecord entity : entities) {
                    entity.setExpiration(expiration);
                }
                log.debug("Updated expiration of valid records in context '{}' to '{}'", context, expiration);
            }
        } catch (final Exception e) {
            log.error("Error updating context expiration in context '{}'", context, e);
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (Exception ex) {
                    log.error("Error rolling back transaction", e);
                }
            }
            throw new IOException(e);
        } finally {
            if (transaction != null && transaction.isActive() && !transaction.getRollbackOnly()) {
                try {
                    transaction.commit();
                } catch (Exception e) {
                    log.error("Error committing transaction", e);
                }
            }
            if (manager != null && manager.isOpen()) {
                try {
                    manager.close();
                } catch (Exception e) {
                    log.error("Error closing entity manager", e);
                }
            }
        }
    }

    // Checkstyle: CyclomaticComplexity ON

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

    // Checkstyle: CyclomaticComplexity OFF
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
            transaction = manager.getTransaction();
            transaction.begin();
            final Query queryResults =
                    manager.createNamedQuery("JPAStorageRecord.findByContext", JPAStorageRecord.class);
            queryResults.setParameter("context", context);
            queryResults.setLockMode(LockModeType.PESSIMISTIC_WRITE);
            final List<JPAStorageRecord> entities = queryResults.getResultList();

            if (!entities.isEmpty()) {
                for (final JPAStorageRecord entity : entities) {
                    if (expiration == null || (entity.getExpiration() != null &&
                            entity.getExpiration() <= expiration)) {
                        manager.remove(entity);
                        log.trace("Deleted entity {}", entity);
                    }
                }
            }
        } catch (final Exception e) {
            log.error("Error deleting context '{}'", context, e);
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (Exception ex) {
                    log.error("Error rolling back transaction", e);
                }
            }
            throw new IOException(e);
        } finally {
            if (transaction != null && transaction.isActive() && !transaction.getRollbackOnly()) {
                try {
                    transaction.commit();
                } catch (Exception e) {
                    log.error("Error committing transaction", e);
                }
            }
            if (manager != null && manager.isOpen()) {
                try {
                    manager.close();
                } catch (Exception e) {
                    log.error("Error closing entity manager", e);
                }
            }
        }
    }

    // Checkstyle: CyclomaticComplexity ON

    // Checkstyle: CyclomaticComplexity OFF
    /**
     * Executes the supplied named query.
     * 
     * @param <T> type of entity to return
     * @param manager to execute the query
     * @param query to execute
     * @param params parameters for the query
     * @param clazz type of entity to return
     * @param lockMode of the transaction
     * 
     * @return query results or an empty list
     * @throws IOException if an error occurs executing the query
     */
    private <T> List<T> executeNamedQuery(@Nonnull final EntityManager manager, @Nonnull @NotEmpty final String query,
            @Nonnull final Map<String, Object> params, @Nonnull final Class<T> clazz,
            @Nonnull final LockModeType lockMode) throws IOException {
        final List<T> results = new ArrayList<>();
        EntityTransaction transaction = null;
        try {
            transaction = manager.getTransaction();
            transaction.begin();
            final Query queryResults = manager.createNamedQuery(query, clazz);
            queryResults.setLockMode(lockMode);
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    queryResults.setParameter(entry.getKey(), entry.getValue());
                }
            }
            results.addAll(queryResults.getResultList());
        } catch (final Exception e) {
            log.error("Error executing named query", e);
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (Exception ex) {
                    log.error("Error rolling back transaction", e);
                }
            }
            throw new IOException(e);
        } finally {
            if (transaction != null && transaction.isActive() && !transaction.getRollbackOnly()) {
                try {
                    transaction.commit();
                } catch (Exception e) {
                    log.error("Error committing transaction", e);
                }
            }
        }
        return results;
    }

    // Checkstyle: CyclomaticComplexity ON

    /** {@inheritDoc} */
    @Override @Nullable protected TimerTask getCleanupTask() {
        return new TimerTask() {

            /** {@inheritDoc} */
            @Override public void run() {
                log.debug("Running cleanup task");
                final Long now = System.currentTimeMillis();
                List<String> contexts = null;
                try {
                    contexts = readContexts();
                } catch (final IOException e) {
                    log.error("Error reading contexts", e);
                }
                if (contexts != null && !contexts.isEmpty()) {
                    for (final String context : contexts) {
                        try {
                            deleteContextImpl(context, now);
                        } catch (final IOException e) {
                            log.error("Error deleting records in context '{}' for timestamp '{}'", context, now, e);
                        }
                    }
                }
            }
        };
    }
}
