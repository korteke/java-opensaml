/*
 * Copyright 2008 University Corporation for Advanced Internet Development, Inc.
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

package org.opensaml.ws.soap.client;

import org.opensaml.ws.message.BaseMessageContext;
import org.opensaml.ws.soap.client.SOAPClient.SOAPRequestParameters;

/** Basic {@link SOAPMessageContext} implementation. */
public class BasicSOAPMessageContext extends BaseMessageContext implements SOAPMessageContext {

    /** Binding/transport-specific SOAP request parameters. */
    private SOAPRequestParameters requestParameters;

    /** {@inheritDoc} */
    public SOAPRequestParameters getSOAPRequestParameters() {
        return requestParameters;
    }

    /** {@inheritDoc} */
    public void setSOAPRequestParameters(SOAPRequestParameters parameters) {
        requestParameters = parameters;
    }

}
