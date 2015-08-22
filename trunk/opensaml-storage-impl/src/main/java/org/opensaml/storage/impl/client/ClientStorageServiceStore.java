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

package org.opensaml.storage.impl.client;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;

import org.opensaml.storage.MutableStorageRecord;

import net.shibboleth.utilities.java.support.annotation.constraint.Live;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.collection.Pair;

/**
 * Implements a session-bound backing store and locking mechanism for the {@link ClientStorageService}.
 */
public class ClientStorageServiceStore {
    
    /** The underlying map of data records. */
    private final Map<String, Map<String, MutableStorageRecord>> contextMap;
    
    /** Dirty bit. */
    private boolean dirty;
    
    /** Constructor. */
    public ClientStorageServiceStore() {
        contextMap = new HashMap<>();
    }
    
    /**
     * Get the map of contexts to manipulate during operations.
     * 
     * @return map of contexts to manipulate
     */
    @Nonnull @NonnullElements @Live public Map<String, Map<String, MutableStorageRecord>> getContextMap() {
        return contextMap;
    }

    /**
     * Set the dirty bit for the current data.
     * 
     * @param flag  dirty bit to set
     */
    public void setDirty(final boolean flag) {
        dirty = flag;
    }

    /**
     * Get the dirty bit for the current data.
     * 
     * @return  status of dirty bit
     */
    public boolean isDirty() {
        return dirty;
    }
    
    /**
     * Reconstitute stored data, replacing any existing data.
     * 
     * <p>The dirty bit is set based on the result. If successful, the bit is cleared,
     * but if an error occurs, it will be set.</p>
     * 
     * @param raw serialized data to load
     * 
     * @throws IOException  if an error occurs reconstituting the data
     */
    protected void load(@Nonnull @NotEmpty final String raw) throws IOException {
        contextMap.clear();
        
        try {
            final JsonReader reader = Json.createReader(new StringReader(raw));
            final JsonStructure st = reader.read();
            if (!(st instanceof JsonObject)) {
                throw new IOException("Found invalid data structure while parsing context map");
            }
            final JsonObject obj = (JsonObject) st;
            
            for (final Map.Entry<String,JsonValue> context : obj.entrySet()) {
                if (context.getValue().getValueType() != JsonValue.ValueType.OBJECT) {
                    contextMap.clear();
                    throw new IOException("Found invalid data structure while parsing context map");
                }
                
                // Create new context if necessary.
                Map<String,MutableStorageRecord> dataMap = contextMap.get(context);
                if (dataMap == null) {
                    dataMap = new HashMap<>();
                    contextMap.put(context.getKey(), dataMap);
                }
                
                final JsonObject contextRecords = (JsonObject) context.getValue();
                for (Map.Entry<String,JsonValue> record : contextRecords.entrySet()) {
                
                    final JsonObject fields = (JsonObject) record.getValue();
                    Long exp = null;
                    if (fields.containsKey("x")) {
                        exp = fields.getJsonNumber("x").longValueExact();
                    }
                    
                    dataMap.put(record.getKey(), new MutableStorageRecord(fields.getString("v"), exp));
                }
            }
            setDirty(false);
        } catch (final NullPointerException | ClassCastException | ArithmeticException | JsonException e) {
            contextMap.clear();
            // Setting this should force corrupt data in the client to be overwritten.
            setDirty(true);
            throw new IOException("Found invalid data structure while parsing context map", e);
        }
    }

    /**
     * Serialize current state of stored data.
     * 
     * @return the serialized data, or a null if the data is empty,
     *  along with the maximum record expiration encountered (expressed in milliseconds since the epoch)
     * 
     * @throws IOException if an error occurs
     */
    @Nullable protected Pair<String,Long> save() throws IOException {
        
        if (contextMap.isEmpty()) {
            return new Pair<>(null, 0L);
        }

        long exp = 0L;
        final long now = System.currentTimeMillis();
        boolean empty = true;

        try {
            final StringWriter sink = new StringWriter(128);
            final JsonGenerator gen = Json.createGenerator(sink);
            
            gen.writeStartObject();
            for (final Map.Entry<String,Map<String, MutableStorageRecord>> context : contextMap.entrySet()) {
                gen.writeStartObject(context.getKey());
                for (Map.Entry<String,MutableStorageRecord> entry : context.getValue().entrySet()) {
                    final MutableStorageRecord record = entry.getValue();
                    final Long recexp = record.getExpiration();
                    if (recexp == null || recexp > now) {
                        empty = false;
                        gen.writeStartObject(entry.getKey())
                            .write("v", record.getValue());
                        if (recexp != null) {
                            gen.write("x", record.getExpiration());
                            exp = Math.max(exp, recexp);
                        }
                        gen.writeEnd();
                    }
                }
                gen.writeEnd();
            }
            gen.writeEnd().close();

            if (empty) {
                return new Pair<>(null, 0L);
            }
            
            return new Pair<>(sink.toString(), exp);
        } catch (final JsonException e) {
            throw new IOException(e);
        }
    }
    
}