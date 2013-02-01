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

package org.opensaml.messaging.decoder.servlet;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import org.opensaml.messaging.context.BaseContext;

/**
 * Context for holding the current {@link HttpServletRequest}.
 */
public class HttpServletRequestContext extends BaseContext {
    
    /** The HttpServletRequest. */
    private HttpServletRequest httpServletRequest;

    /**
     * Get the HttpServletRequest. 
     * 
     * @return Returns the httpServletRequest.
     */
    @Nullable public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    /**
     * Set the HttpServletRequest. 
     * 
     * @param httpServletRequest The httpServletRequest to set.
     */
    public void setHttpServletRequest(@Nullable final HttpServletRequest newHttpServletRequest) {
        httpServletRequest = newHttpServletRequest;
    }

}
