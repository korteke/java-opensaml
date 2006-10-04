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

import org.opensaml.common.SAMLArtifact;

/**
 * SAML 2 Artifact. SAML 2 artifacts contains a 2 byte type code with a value of 4 follwed by a 2 byte endpoint index
 * followed by a 20 byte source ID followed by a 20 byte message handle.
 */
public class SAML2Artifact extends SAMLArtifact {

    /** SAML 2 artifact type code (0x0004) */
    public final static byte[] TYPE_CODE = { 0, 4 };

    /** 2 byte artifact endpoint index */
    private byte[] endpointIndex;
    
    /** 20 byte artifact source ID */
    private byte[] sourceID;

    /**
     * Constructor
     * 
     * @param endpointIndex 2 byte endpoint index of the artifact
     * @param sourceID 20 byte source ID of the artifact
     * @param messageHandle 20 byte message handle of the artifact
     * 
     * @throws IllegalArgumentException thrown if the endpoint index, source ID, or message handle arrays are not of the
     *             right size
     */
    public SAML2Artifact(byte[] endpointIndex, byte[] sourceID, byte[] messageHandle) throws IllegalArgumentException {
        super(TYPE_CODE);
        setTypeCode(TYPE_CODE);
        setEndpointIndex(endpointIndex);
        setSourceID(sourceID);
        setMessageHandle(messageHandle);
    }

    /**
     * Constructs a SAML 2 artifact from it's byte array representation.
     * 
     * @param artifact the byte array representing the artifact
     * 
     * @throws IllegalArgumentException thrown if the artifact is not the right type or lenght (44 bytes)
     */
    public SAML2Artifact(byte[] artifact) throws IllegalArgumentException {
        super(TYPE_CODE);
        
        if (artifact.length != 44) {
            throw new IllegalArgumentException("Artifact length must be 44 bytes it was " + artifact.length + "bytes");
        }

        byte[] typeCode = { artifact[0], artifact[1] };
        if (!(typeCode[0] == TYPE_CODE[0] && typeCode[1] == TYPE_CODE[1])) {
            throw new IllegalArgumentException("Illegal artifact type code");
        }
        setTypeCode(typeCode);

        byte[] endpointIndex = { artifact[2], artifact[3] };
        setEndpointIndex(endpointIndex);

        byte[] sourceID = new byte[20];
        for (int i = 0, j = 4; j < 24; i++, j++) {
            sourceID[i] = artifact[j];
        }
        setSourceID(sourceID);

        byte[] messageHandle = new byte[20];
        for (int i = 0, j = 24; j < 44; i++, j++) {
            messageHandle[i] = artifact[j];
        }
        setMessageHandle(messageHandle);
    }

    /** {@inheritDoc} */
    public byte[] getArtifactBytes() {
        byte[] artifact = new byte[44];

        byte[] typeCode = getTypeCode();
        byte[] endpointIndex = getEndpointIndex();
        byte[] sourceID = getSourceID();
        byte[] messageHandle = getMessageHandle();

        artifact[0] = typeCode[0];
        artifact[1] = typeCode[1];
        artifact[2] = endpointIndex[0];
        artifact[3] = endpointIndex[1];

        for (int i = 4, j = 0; i < 24; i++, j++) {
            artifact[i] = sourceID[j];
        }

        for (int i = 24, j = 0; i < 44; i++, j++) {
            artifact[i] = messageHandle[j];
        }

        return artifact;
    }

    /**
     * Gets the 2 byte endpoint index for this artifact.
     * 
     * @return 2 byte endpoint index for this artifact
     */
    public byte[] getEndpointIndex() {
        return endpointIndex;
    }

    /**
     * Sets the 2 byte endpoint index for this artifact.
     * 
     * @param newIndex 2 byte endpoint index for this artifact
     * 
     * @throws IllegalArgumentException thrown if the given index is not 2 bytes
     */
    protected void setEndpointIndex(byte[] newIndex) throws IllegalArgumentException {
        if (newIndex.length != 2) {
            throw new IllegalArgumentException("Artifact endpoint index must be two bytes long");
        }

        endpointIndex = newIndex;
    }

    /**
     * Gets the 20 byte source ID of the artifact.
     * 
     * @return the source ID of the artifact
     */
    public byte[] getSourceID(){
        return sourceID;
    }
    
    /**
     * Sets the 20 byte source ID of the artifact.
     * 
     * @param newSourceID 20 byte source ID of the artifact
     * 
     * @throws IllegalArgumentException thrown if the given source ID is not 20 bytes
     */
    protected void setSourceID(byte[] newSourceID) throws IllegalArgumentException{
        if(newSourceID.length != 20){
            throw new IllegalArgumentException("Artifact source ID must be 20 bytes long");
        }
        sourceID = newSourceID;
    }
}