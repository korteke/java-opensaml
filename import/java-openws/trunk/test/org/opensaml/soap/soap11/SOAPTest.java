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

package org.opensaml.soap.soap11;

import org.opensaml.soap.BaseTestCase;

/**
 * Tests marshalling and unmarshalling SOAP messages.
 */
public class SOAPTest extends BaseTestCase {

    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    /**
     * Test unmarshalling a SOAP message, dropping its DOM representation and then remarshalling it.
     */
    public void testSOAPMessage(){
        
    }
    
    /**
     * Test unmarshalling a SOAP fault, dropping its DOM representation and then remarshalling it.
     */
    public void testSOAPFault(){
        
    }
}