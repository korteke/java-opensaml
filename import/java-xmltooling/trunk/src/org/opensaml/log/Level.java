/*
 * Copyright [2006] [University Corporation for Advanced Internet Development, Inc.]
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
package org.opensaml.log;

/**
 * Additional logging levels. 
 * 
 * <ul>
 * <li>CRITIAL - for messages that should always be logged but not be errors</li>
 * <li>TRAIL - equivalent to TRACE introduced in later versions of Log4j</li>
 * </ul>
 */
public class Level extends org.apache.log4j.Level {

    /** Integer representation of CRITICIAL logging level. */
    public static final int CRITICAL_INT = 60000;
    
    /** Integer representation of TRAIL logging level. */
    public static final int TRAIL_INT = 5000;
    
    /** Critical logging level. */
    public static final Level CRITICAL = new Level(CRITICAL_INT, "CRITICAL", 0);
    
    /** Trail logging level. */
    public static final Level TRAIL = new Level(TRAIL_INT, "TRACE", 7);
    
    /** Serial version UID. */
    private static final long serialVersionUID = -8920329210711643389L;
        
    /**
     * Constructor.
     *
     * @param level integer representation of the logging level
     * @param name human readable name for the logging level
     * @param sysLogLevel corresponding syslog level
     */
    protected Level(int level, String name, int sysLogLevel){
        super(level, name, sysLogLevel);
    }
}