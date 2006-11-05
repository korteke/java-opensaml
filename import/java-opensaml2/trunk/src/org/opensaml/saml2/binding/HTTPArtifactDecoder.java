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

package org.opensaml.saml2.binding;

import javax.servlet.http.HttpServletRequest;

import org.bouncycastle.util.encoders.Base64;
import org.opensaml.common.SAMLVersion;
import org.opensaml.common.binding.BindingException;
import org.opensaml.common.binding.SAMLArtifact;
import org.opensaml.common.binding.SAMLArtifactFactory;
import org.opensaml.common.binding.impl.AbstractHTTPMessageDecoder;
import org.opensaml.xml.util.DatatypeHelper;

/**
 * SAML 2 Artifact Binding decoder, support both HTTP GET and POST.
 */
public class HTTPArtifactDecoder extends AbstractHTTPMessageDecoder {

    /** Factory for building artifacts */
    private SAMLArtifactFactory artifactFactory;

    /** Artifact generated for the given SAML message */
    private SAMLArtifact artifact;

    /**
     * Gets the artifact factory used to create artifacts for this encoder.
     * 
     * @return artifact factory used to create artifacts for this encoder
     */
    public SAMLArtifactFactory getArtifactFactory() {
        return artifactFactory;
    }

    /**
     * Sets the artifact factory used to create artifacts for this encoder.
     * 
     * @param artifactFactory artifact factory used to create artifacts for this encoder
     */
    public void setArtifactFactory(SAMLArtifactFactory artifactFactory) {
        this.artifactFactory = artifactFactory;
    }

    /**
     * Gets the artifact created for the given SAML message.
     * 
     * @return artifact created for the given SAML message
     */
    public SAMLArtifact getArtifact() {
        return artifact;
    }

    /** {@inheritDoc} */
    public void decode() throws BindingException {
        HttpServletRequest request = getRequest();

        if (getSecurityPolicy() != null) {
            getSecurityPolicy().evaluate(getRequest(), null);
        }

        setHttpMethod(request.getMethod());

        if (!DatatypeHelper.isEmpty(request.getParameter("RelayState"))) {
            setRelayState(request.getParameter("RelayState"));
        }

        artifact = decodeArtifact(request);
    }
    
    /**
     * Extracts the Base64 encoded from the request, decodes it, and builds a {@link SAMLArtifact} from it.
     * 
     * @param request request containing the SAML artifact
     * 
     * @return the SAML artifact
     * 
     * @throws BindingException thrown if the SAML artifact could not be created
     */
    protected SAMLArtifact decodeArtifact(HttpServletRequest request) throws BindingException{
        String samlArt = request.getParameter("SAMLArt");
        if (DatatypeHelper.isEmpty(samlArt)) {
            throw new BindingException("Request does not contain a SAML artifact");
        } else {
            byte[] artifactBytes = Base64.decode(samlArt);
            return getArtifactFactory().buildArtifact(SAMLVersion.VERSION_20, artifactBytes);
        }
    }
}