/*
 * Copyright [2007] [University Corporation for Advanced Internet Development, Inc.]
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

package org.opensaml.xml.util;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

/**
 * A factory class which returns the singleton instance of an output class based on an input class instance.
 * 
 * The class implements the singleton pattern in that it ensures that exactly one instance
 * of the output class exists for each instance of the input class.  A {@link WeakHashMap}
 * is used as the underlying store to ensure that if the input class instance become otherwise
 * unused, the input class instance key used within the factory will not prevent the input class
 * from being garbage-collected. Value instances of the map are also wrapped in a 
 * {@link WeakReference} to allow for the possibility that the output class may hold a 
 * reference to the input class key, without preventing the described weak reference-based garbage collection.
 *
 * @param <Input> the factory input class type
 * @param <Output> the factory output class type
 */
public abstract class AbstractSingletonFactory<Input, Output> {
    
    /** Storage for the factory. */
    private WeakHashMap<Input, WeakReference<Output>> map = new WeakHashMap<Input, WeakReference<Output>>();
    
    /**
     * Constructor.
     *
     */
    public AbstractSingletonFactory() {
        map = new WeakHashMap<Input, WeakReference<Output>>();
    }
    
    /**
     * Obtain an instance of the output class based on an input class instance.
     * 
     * @param input the input class instance
     * @return an output class instance
     */
    public synchronized Output getInstance(Input input) {
        WeakReference<Output> outputRef = map.get(input);
        if (outputRef != null) {
            if (outputRef.get() != null) {
                return outputRef.get();
            } else {
                map.remove(input);
            }
        }
        Output output = createNewInstance(input);
        map.put(input, new WeakReference<Output>(output));
        return output;
    }

    /**
     * Create a new instance of the output class based on the input
     * class instance.
     * 
     * @param input the input class instance
     * @return an output class instance
     */
    protected abstract Output createNewInstance(Input input);

}
