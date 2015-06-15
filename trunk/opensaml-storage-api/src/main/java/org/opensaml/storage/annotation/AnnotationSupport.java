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

package org.opensaml.storage.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;

import org.joda.time.Instant;
import org.joda.time.ReadableInstant;

/**
 * Support class that reads and writes fields decorated with storage annotations.
 */
public final class AnnotationSupport {

    /** Simple cache of fields we have seen. */
    private static final Map<String, Field> FIELD_CACHE = new ConcurrentHashMap<>();
    
    /** Private constructor of utility class. */
    private AnnotationSupport() {
        
    }
    
    /**
     * Gets the value of the field indicated by the {@link Context} annotation on the given object.
     * 
     * <p>If the value is not a {@link String}, then it is converted to one
     * by calling {@link Object#toString()} on the object.<p>
     *
     * @param target object from which to get context
     * 
     * @return Context field value
     * @throws IllegalArgumentException if the target object doesn't declare a {@link Context} annotation
     * @throws RuntimeException if the field cannot be read on the target object
     */
    @Nonnull @NotEmpty public static String getContext(@Nonnull final Object target) {
        final Context ctxField = getAnnotation(target, Context.class);
        final Object value = getFieldValue(target, ctxField.value());
        if (value instanceof String) {
            return (String) value;
        }
        return value.toString();
    }

    /**
     * Sets the value of the field indicated by the {@link Context} annotation on the given object.
     *
     * @param target Object on which to set context
     * @param context Context value
     *
     * @throws IllegalArgumentException if the target object doesn't declare a {@link Context} annotation
     * @throws RuntimeException if the field cannot be set on the target object
     */
    public static void setContext(@Nonnull final Object target, @Nonnull @NotEmpty final String context) {
        final Context ctxField = getAnnotation(target, Context.class);

        setFieldValue(target, ctxField.value(), context);
    }
    
    /**
     * Gets the value of the field indicated by the {@link Key} annotation on the given object.
     * 
     * <p>If the value is not a {@link String}, then it is converted to one
     * by calling {@link Object#toString()} on the object.<p>
     *
     * @param target object from which to get key
     * 
     * @return Key field value
     * @throws IllegalArgumentException if the target object doesn't declare a {@link Key} annotation
     * @throws RuntimeException if the field cannot be read on the target object
     */
    @Nonnull @NotEmpty public static String getKey(@Nonnull final Object target) {
        final Key keyField = getAnnotation(target, Key.class);
        final Object value = getFieldValue(target, keyField.value());
        if (value instanceof String) {
            return (String) value;
        }
        return value.toString();
    }

    /**
     * Sets the value of the field indicated by the {@link Key} annotation on the given object.
     *
     * @param target Object on which to set key
     * @param key Key value
     *
     * @throws IllegalArgumentException if the target object doesn't declare a {@link Key} annotation
     * @throws RuntimeException if the field cannot be set on the target object
     */
    public static void setKey(@Nonnull final Object target, @Nonnull @NotEmpty final String key) {
        final Key keyField = getAnnotation(target, Key.class);

        setFieldValue(target, keyField.value(), key);
    }

    /**
     * Gets the value of the field indicated by the {@link Value} annotation on the given object.
     * 
     * <p>If the value is not a {@link String}, then it is converted to one
     * by calling {@link Object#toString()} on the object.<p>
     *
     * @param target Object from which to get value
     * 
     * @return Value field value
     * @throws IllegalArgumentException if the target object doesn't declare a {@link Value} annotation
     * @throws RuntimeException if the field cannot be read on the target object
     */
    @Nonnull @NotEmpty public static String getValue(@Nonnull final Object target) {
        final Value valueField = getAnnotation(target, Value.class);
        final Object value = getFieldValue(target, valueField.value());
        if (value instanceof String) {
            return (String) value;
        }
        return value.toString();
    }

    /**
     * Sets the value of the field indicated by the {@link Value} annotation on the given object.
     *
     * @param target object on which to set value
     * @param value Value field value
     *
     * @throws IllegalArgumentException if the target object doesn't declare a {@link Value} annotation
     * @throws RuntimeException if the field cannot be set on the target object
     */
    public static void setValue(@Nonnull final Object target, @Nonnull @NotEmpty final String value) {
        final Value valueField = getAnnotation(target, Value.class);
        
        setFieldValue(target, valueField.value(), value);
    }

    /**
     * Gets the value of the field indicated by the {@link Expiration} annotation on the given object,
     * or null if none.
     * 
     * <p>The value is returned as a long, in milliseconds since the beginning of the Unix epoch.
     * The following data types are supported:
     *  <ul>
     *     <li><code>long</code></li>
     *     <li>{@link Date}</li>
     *     <li>{@link ReadableInstant}</li>
     *  </ul>
     * </p>
     *
     * @param target Object from which to get expiration
     * 
     * @return Expiration field value, or null
     * @throws IllegalArgumentException if the target object doesn't declare a {@link Expiration} annotation
     * @throws RuntimeException if the field cannot be read on the target object or if it is an unsupported data type
     */
    @Nullable public static Long getExpiration(@Nonnull final Object target) {
        final Expiration expField = getAnnotation(target, Expiration.class);
        final Object value = getFieldValue(target, expField.value());
        if (value == null) {
            return null;
        } else if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof Date) {
            return ((Date) value).getTime();
        } else if (value instanceof ReadableInstant) {
            return ((ReadableInstant) value).getMillis();
        }
        throw new RuntimeException(value + " is an unsupported data type for an expiration field.");
    }

    /**
     * Sets the value of the field indicated by the {@link Expiration} annotation on the given object.
     *
     * <p>The expiration is expressed in milliseconds since the beginning of the Unix epoch.
     * The following data types are supported:
     *  <ul>
     *     <li><code>long</code></li>
     *     <li>{@link Date}</li>
     *     <li>{@link ReadableInstant}</li>
     *  </ul>
     * </p>
     *
     * @param target object on which to set expiration
     * @param expiration value to set
     *
     * @throws IllegalArgumentException if the target object doesn't declare a {@link Expiration} annotation
     * @throws RuntimeException if the field cannot be set on the target object or if it is an unsupported data type
     */
    public static void setExpiration(@Nonnull final Object target, @Nullable final Long expiration) {
        final Expiration expField = getAnnotation(target, Expiration.class);
        if (expiration == null) {
            setFieldValue(target, expField.value(), null);
        }
        
        final Class<?> type = getField(target, expField.value()).getType();
        if (Long.class.isAssignableFrom(type)) {
            setFieldValue(target, expField.value(), expiration);
        } else if (Date.class.isAssignableFrom(type)) {
            setFieldValue(target, expField.value(), new Date(expiration));
        } else if (ReadableInstant.class.isAssignableFrom(type)) {
            setFieldValue(target, expField.value(), new Instant(expiration));
        } else {
            throw new RuntimeException(type + " is an unsupported data type for an expiration field.");
        }
    }

    /**
     * Returns an annotation of a specified type attached to a target object.
     * 
     * @param <T> type of annotation
     * @param target target object to examine
     * @param annotationType class type of annotation to find
     * 
     * @return  the annotation found on the object
     * @throws IllegalArgumentException if the target object doesn't declare the annotation
     */
    @Nonnull private static <T extends Annotation> T getAnnotation(@Nonnull final Object target,
            @Nonnull final Class<T> annotationType) {
        final Class<?> targetClass = target.getClass();
        final T keyField = targetClass.getAnnotation(annotationType);
        if (keyField == null) {
            throw new IllegalArgumentException("Key annotation not found on " + target);
        }
        return keyField;
    }

    /**
     * Returns the value of a field from an object using reflection.
     * 
     * @param target target object to examine
     * @param fieldName name of the field to retrieve
     * 
     * @return  the value of the field
     * @throws RuntimeException if the field cannot be read
     */
    @Nullable private static Object getFieldValue(@Nonnull final Object target, @Nonnull final String fieldName) {
        try {
            return getField(target, fieldName).get(target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Field " + fieldName + " cannot be read on " + target);
        }
    }

    /**
     * Returns the value of a field from an object using reflection.
     * 
     * @param target target object to update
     * @param fieldName name of the field to set
     * @param fieldValue value to set
     * 
     * @throws RuntimeException if the field cannot be set
     */
    private static void setFieldValue(@Nonnull final Object target, @Nonnull final String fieldName,
            @Nullable final Object fieldValue) {
        try {
            getField(target, fieldName).set(target, fieldValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Field " + fieldName + " cannot be set on " + target);
        }
    }
    
    /**
     * Returns a {@link Field} from a target object. 
     * 
     * @param target target object to examine
     * @param fieldName name of the field to retrieve
     * 
     * @return the specified field from the object
     * @throws RuntimeException if the target object doesn't declare the annotation
     */
    @Nonnull private static Field getField(@Nonnull final Object target, @Nonnull final String fieldName) {
        final Class<?> targetClass = target.getClass();
        final String key = targetClass.getName() + "." + fieldName;
        Field field = FIELD_CACHE.get(key);
        if (field == null) {
            try {
                field = targetClass.getDeclaredField(fieldName);
                if (!field.isAccessible()) {
                    // Try to make it accessible
                    field.setAccessible(true);
                }
                FIELD_CACHE.put(key, field);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("Field " + fieldName + " does not exist on " + target);
            }
        }
        return field;
    }

}