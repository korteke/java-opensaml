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

package org.opensaml.xml.parse;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A SAX entity resolver that resolves an entity's location within the classpath.
 * 
 * Entity URIs <strong>must</strong> begin with the prefix <code>classpath:</code> and be followed
 * by either an absolute or relative classpath.  Relative classpaths are relative to <strong>this</strong> class.
 * 
 * This resolver will <strong>not</strong> attempt to resolve any other URIs.
 */
public class ClasspathEntityResolver implements EntityResolver {

    /** UR scheme for classpath locations. */
    public static final String CLASSPATH_URI_SCHEME = "classpath:";
    
    /** Class logger. */
    private final Logger log = Logger.getLogger(ClasspathEntityResolver.class);

    /** {@inheritDoc} */
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {

        String resource = null;
        InputStream resourceIns = null;
        
        if(systemId.startsWith(CLASSPATH_URI_SCHEME)){
            if (log.isDebugEnabled()) {
                log.debug("Attempting to resolve, within the classpath, the entity with the following system id: "
                        + systemId);
            }
            
            resourceIns = getClass().getResourceAsStream(resource);
        }
        
        if (resourceIns == null && publicId != null && publicId.startsWith(CLASSPATH_URI_SCHEME)) {
            if (log.isDebugEnabled()) {
                log.debug("Attempting to resolve, within the classpath, the entity with the following public id: "
                        + resource);
            }
            resource = publicId.replaceFirst("classpath:", "");
            resourceIns = getClass().getResourceAsStream(resource);
        }

        if (resourceIns == null) {
            if (log.isDebugEnabled()) {
                log.debug("Entity was not resolved from classpath");
            }
            return null;
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Entity resolved from classpath");
            }
            return new InputSource(resourceIns);
        }
    }
}