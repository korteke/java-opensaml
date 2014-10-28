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

package org.opensaml.profile.action.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Mock message class impl.
 */
public class MockMessage {

    /** Mock message properties. */
    private HashMap<String, String> properties;

    /**
     * Constructor.
     */
    public MockMessage() {
        properties = new HashMap<>();
    }

    /**
     * Get the mock message properties.
     * 
     * @return properties
     */
    public Map<String, String> getProperties() {
        return properties;
    }

    /**
     * Get the encoded form of the message properties, which will be in the form 
     * "key1=value1&key2=value2&...&keyN=valueN", sorted ascending by key.
     * 
     * @return the encoded form of the message
     */
    public String getEncoded() {
        StringBuffer buffer = new StringBuffer();
        List<String> sortedKeys = sort(properties.keySet());
        for (String key : sortedKeys) {
            buffer.append(key);
            buffer.append("=");
            buffer.append(properties.get(key) != null ? properties.get(key) : "");
            buffer.append("&");
        }
        String s = buffer.toString();
        s = s.substring(0, s.length() - 1);
        return s;
    }

    /**
     * Convert a set into a sorted list.
     * 
     * @param messageKeys set of keys
     * @return a sorted list
     */
    private List<String> sort(Set<String> messageKeys) {
        ArrayList<String> list = new ArrayList<>(messageKeys);
        Collections.sort(list);
        return list;
    }

}