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

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;

import org.opensaml.storage.MutableStorageRecord;

/**
 * Implementation of {@link MutableStorageRecord} annotated for JPA.
 */
@Entity
@Table(name = "StorageRecords")
@NamedQueries({
    @NamedQuery(name = "JPAStorageRecord.findAll",
            query = "SELECT r FROM JPAStorageRecord r"),
    @NamedQuery(name = "JPAStorageRecord.findAllContexts",
            query = "SELECT distinct r.context FROM JPAStorageRecord r"),
    @NamedQuery(name = "JPAStorageRecord.findByContext",
            query = "SELECT r FROM JPAStorageRecord r WHERE r.context = :context"),
    @NamedQuery(name = "JPAStorageRecord.findActiveByContext",
            query = "SELECT r FROM JPAStorageRecord r WHERE r.context = :context AND r.expiration >= :now")})
@IdClass(JPAStorageRecord.RecordId.class)
public class JPAStorageRecord extends MutableStorageRecord {

    /** Length of the context column. */
    public static final int CONTEXT_SIZE = 255;

    /** Length of the key column. */
    public static final int KEY_SIZE = 255;

    /** Length of the value column. */
    public static final int VALUE_SIZE = 255;

    /** Context string. */
    private String context;

    /** Key string. */
    private String key;

    /**
     * Creates a new JPA storage record. All properties initialized to null.
     */
    public JPAStorageRecord() {
        super(null, null);
    }

    /**
     * Returns the context.
     * 
     * @return context
     */
    @Id @Nonnull public String getContext() {
        return context;
    }

    /**
     * Sets the context.
     * 
     * @param ctx to set
     */
    public void setContext(@Nonnull @NotEmpty final String ctx) {
        context = ctx;
    }

    /**
     * Returns the key.
     * 
     * @return key
     */
    @Id @Nonnull public String getKey() {
        return key;
    }

    /**
     * Sets the key.
     * 
     * @param k to set
     */
    public void setKey(@Nonnull @NotEmpty final String k) {
        key = k;
    }

    /** {@inheritDoc} */
    @Column(length = VALUE_SIZE, nullable = false) @Nonnull @Override public String getValue() {
        return super.getValue();
    }

    /** {@inheritDoc} */
    @Column(nullable = true) @Nullable @Override public Long getExpiration() {
        return super.getExpiration();
    }

    /** {@inheritDoc} */
    @Column(nullable = false) @Override public int getVersion() {
        return super.getVersion();
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return String.format("%s@%d::context=%s, key=%s, value=%s, expiration=%s, version=%s", getClass().getName(),
                hashCode(), context, key, getValue(), getExpiration(), getVersion());
    }

    /** Composite key to represent the record id. */
    @Embeddable
    public static class RecordId implements Serializable {

        /** serial version UID. */
        private static final long serialVersionUID = -9149627192851655684L;

        /** Context string. */
        private String context;

        /** Key string. */
        private String key;

        /**
         * Default constructor.
         */
        public RecordId() {
        }

        /**
         * Creates a new record Id.
         * 
         * @param ctx context
         * @param k key
         */
        public RecordId(@Nonnull @NotEmpty final String ctx, @Nonnull @NotEmpty final String k) {
            context = ctx;
            key = k;
        }

        /**
         * Returns the context.
         * 
         * @return context
         */
        @Column(length = CONTEXT_SIZE, nullable = false) @Nonnull public String getContext() {
            return context;
        }

        /**
         * Sets the context.
         * 
         * @param ctx to set
         */
        public void setContext(@Nonnull @NotEmpty final String ctx) {
            context = ctx;
        }

        /**
         * Returns the key.
         * 
         * @return key
         */
        @Column(length = KEY_SIZE, nullable = false) @Nonnull public String getKey() {
            return key;
        }

        /**
         * Sets the key.
         * 
         * @param k to set
         */
        public void setKey(@Nonnull @NotEmpty final String k) {
            key = k;
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            int hc = 31;
            hc += context != null ? context.hashCode() : 0;
            hc += key != null ? key.hashCode() : 0;
            return hc;
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(final Object o) {
            return o != null && (this == o || getClass() == o.getClass() && hashCode() == o.hashCode());
        }
    }
}
