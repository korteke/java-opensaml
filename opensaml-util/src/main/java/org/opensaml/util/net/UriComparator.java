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

package org.opensaml.util.net;

import javax.annotation.Nullable;


/**
 * Component for testing URI's as to equality.
 */
public interface UriComparator {
    
    /**
     * Compare two URI's (represented as strings) for equivalence.
     * 
     * @param uri1 first URI to compare
     * @param uri2 second URI to compare
     * 
     * @return true if the URI's are equivalent, false otherwise
     * 
     * @throws UriException if the URI's can not be successfully evaluated
     */
    public boolean compare(@Nullable final String uri1, @Nullable final String uri2) throws UriException;

}
