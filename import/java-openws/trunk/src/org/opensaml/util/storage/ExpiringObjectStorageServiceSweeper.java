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

package org.opensaml.util.storage;

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple task that periodically sweeps over a {@link StorageService} and removes expired entries.
 */
public class ExpiringObjectStorageServiceSweeper extends TimerTask {

    /** Storage service whose entries will be periodically checked. */
    private StorageService<Object, ? extends ExpiringObject> store;

    /**
     * Constructor. Registers this task with the given timer.
     * 
     * @param taskTimer timer that will sweep the given storage service
     * @param sweptStore storage service that will be swept
     * @param sweepInterval interval, in milliseconds, that the storage service will be swept
     */
    public ExpiringObjectStorageServiceSweeper(Timer taskTimer,
            StorageService<Object, ? extends ExpiringObject> sweptStore, long sweepInterval) {
        store = sweptStore;
        taskTimer.schedule(this, sweepInterval);
    }

    /** {@inheritDoc} */
    public void run() {
        Collection<Object> storeKeys = store.getKeys();
        if (storeKeys != null) {
            ExpiringObject value;
            for (Object key : storeKeys) {
                value = store.get(key);
                if (value.isExpired()) {
                    store.remove(key);
                }
            }
        }
    }
}