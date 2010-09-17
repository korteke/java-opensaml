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

package org.opensaml.util;

import java.io.Closeable;
import java.io.IOException;

/** Utility for closing {@link Closeable}s. */
public final class CloseableSupport {

    /** Constructor. */
    private CloseableSupport() {
    }

    /**
     * Closes a stream and swallows any thrown {@link IOException}.
     * 
     * @param closebale the stream to be closed
     */
    public static void closeQuietly(final Closeable closebale) {
        if(closebale == null){
            return;
        }
        
        try {
            closebale.close();
        } catch (IOException e) {
            // swallow error
            return;
        }
    }
}