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

package org.opensaml.core.config.provider;

/**
 * Concrete implementation of filesystem configuration properties source which obtains
 * the filename value from a system property.
 */
public class SystemPropertyFilesystemConfigurationPropertiesSource extends
        AbstractFilesystemConfigurationPropertiesSource {
    
    /** The system property name for the filename to use. */
    public static final String PROPERTY_FILE_NAME = "opensaml.config.fileName";

    /** {@inheritDoc} */
    protected String getFilename() {
        return System.getProperty(PROPERTY_FILE_NAME);
    }
    
}
