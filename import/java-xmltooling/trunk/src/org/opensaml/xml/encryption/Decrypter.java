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

package org.opensaml.xml.encryption;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.Key;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.encryption.XMLEncryptionException;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.BasicParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.credential.CredentialCriteriaSet;
import org.opensaml.xml.security.keyinfo.KeyInfoCredentialCriteria;
import org.opensaml.xml.security.keyinfo.KeyInfoCredentialResolver;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Supports decryption of XMLObjects which represent data encrypted according to the 
 * XML Encryption specification, version 20021210.
 * 
 * <p>XML Encryption can encrypt either a single {@link Element} or the contents of an Element.  The caller
 * of this class must select the decryption method which is most appropriate for their specific use case.</p>
 * 
 * <p>Note that the type of plaintext data contained by an {@link EncryptedData} can be checked prior to decryption
 * by examining that element's <code>type</code> attribute ({@link EncryptedData#getType}).  This (optional)
 * attribute may contain one of the following two constant values to aid in the decryption process:
 * {@link EncryptionConstants#TYPE_ELEMENT} or {@link EncryptionConstants#TYPE_CONTENT}.</p>
 * 
 * <p>By nature the fundamental output of XML decryption is a DOM {@link DocumentFragment} with 1 or more
 * immediate top-level DOM {@link Node} children.  This case is reflected in the method
 * {@link #decryptDataToDOM(EncryptedData)}.  It is up to the caller to properly process 
 * the DOM Nodes which are the children of this document fragment. The DocumentFragment 
 * and its Node children will be owned by the DOM {@link Document} which owned the 
 * original EncryptedData before decryption.  Note, however, that the Node children will not be a part
 * of the tree of Nodes rooted at that Document's document element.</p>
 * 
 * <p>A typical use case will be that the content which was encrypted contained solely {@link Element}
 * nodes.  For this use case a convenience method is provided as {@link #decryptDataToList(EncryptedData)},
 * which returns a list of {@link XMLObject}'s which are the result of unmarshalling each of the child Elements
 * of the decrypted DocumentFragment.</p>
 * 
 * <p>Another typical use case is that the content which was encrypted was a single Element.  For this use 
 * case a convenience method is provided as {@link #decryptData(EncryptedData)}, which returns a single
 * XMLObject which was the result of unmarshalling this decrypted Element.</p>
 * 
 * <p>In both of these cases the underlying DOM Element which is represented by each of the returned XMLObjects
 * will be owned by the DOM Document which also owns the original EncrytpedData Element.  However, note that
 * these cached DOM Elements are <strong>not</strong> part of the tree of Nodes rooted at that 
 * Document's document element.  If these returned XMLObjects are then inserted as the children 
 * of other XMLObjects, it is up to the caller to ensure that the XMLObject tree is then remarshalled 
 * if the relationship of the cached DOM nodes is important (e.g. resolution of ID-typed attributes
 * via {@link Document#getElementById(String)}).</p>
 * 
 * <p>For some use cases where the returned XMLObjects will not necessarily be stored back as children of
 * another parent XMLObject, it may still necessary for the DOM Elements of the resultant XMLObjects 
 * to exist within the tree of Nodes rooted at a DOM Document's document element (e.g. signature verification 
 * on the standalone decrypted XMLObject).  For these cases these method variants may be used:
 * {@link #decryptDataToList(EncryptedData, boolean)} and {@link #decryptData(EncryptedData, boolean)}.
 * If the boolean parameter <code>rootInNewDocument</code> is true, then for each top-level child Element of the
 * decrypted DocumentFragment, the following will occur:
 * 
 * <ol>
 *  <li>A new DOM Document will be created.</li> 
 *  <li>The Element will be adopted into that Document.</li> 
 *  <li>The Element will be made the root element of the Document.</li>
 *  <li>The Element will be unmarshalled into an XMLObject as in the single argument variant.</li>
 * </ol>
 * 
 * <p>Note that new Document creation, node adoption and rooting the new document element are 
 * potentially very expensive. This should only be done where the caller's use case really requires it.</p>
 * 
 */
public class Decrypter {
    
    /** ParserPool used in parsing decrypted data. */
    private final BasicParserPool parserPool;
    
    /** Unmarshaller factory, used in decryption of EncryptedData objects. */
    private UnmarshallerFactory unmarshallerFactory;
    
    /** Load-and-Save DOM Implementation singleton. */
    //private DOMImplementationLS domImplLS;
    
    /** Class logger. */
    private Logger log = Logger.getLogger(Decrypter.class);
    
    /** Resolver for data encryption keys. */
    private KeyInfoCredentialResolver resolver;
    
    /** Resolver for key encryption keys. */
    private KeyInfoCredentialResolver kekResolver;
    
    /**
     * Constructor.
     *
     * @param newKEKResolver resolver for key encryption keys.
     * @param newResolver resolver for data encryption keys.
     */
    public Decrypter(KeyInfoCredentialResolver newKEKResolver, KeyInfoCredentialResolver newResolver) {
        kekResolver = newKEKResolver;
        resolver = newResolver;
        
        parserPool = new BasicParserPool();
        parserPool.setNamespaceAware(true);
        
        HashMap<String, Boolean> features = new HashMap<String, Boolean>();
        features.put("http://apache.org/xml/features/dom/defer-node-expansion", Boolean.FALSE); 
        parserPool.setFeatures(features);
        
        
        unmarshallerFactory = Configuration.getUnmarshallerFactory();
    }
    
    public Decrypter(KeyInfoCredentialResolver newKEKResolver, KeyInfoCredentialResolver newResolver, BasicParserPool pool){
        kekResolver = newKEKResolver;
        resolver = newResolver;
        parserPool = pool;
    }
    
    /**
     * Set a new key encryption key credential resolver.
     * 
     * @param newKEKResolver the new key encryption key resolver
     */
    public void setKEKREsolver(KeyInfoCredentialResolver newKEKResolver) {
        this.kekResolver = newKEKResolver;
    }
    
    /**
     * Set a new data encryption key credential resolver.
     * 
     * @param newResolver the new data encryption key resolver
     */
    public void setKeyResolver(KeyInfoCredentialResolver newResolver) {
        this.resolver = newResolver;
    }
    
    /**
     * Decrypts the supplied EncryptedData and returns the resulting XMLObject.
     * 
     * This will only succeed if the decrypted EncryptedData contains exactly one
     * DOM Node of type Element.
     * 
     * @param encryptedData encrypted data element containing the data to be decrypted
     * @return the decrypted XMLObject
     * @throws DecryptionException exception indicating a decryption error, possibly because
     *          the decrypted data contained more than one top-level Element, or some
     *          non-Element Node type.
     */
    public XMLObject decryptData(EncryptedData encryptedData) throws DecryptionException {
        return decryptData(encryptedData, false);
    }
    
    /**
     * Decrypts the supplied EncryptedData and returns the resulting XMLObject.
     * 
     * This will only succeed if the decrypted EncryptedData contains exactly one
     * DOM Node of type Element.
     * 
     * @param encryptedData encrypted data element containing the data to be decrypted
     * @param rootInNewDocument if true, root the underlying Element of the returned XMLObject
     *           in a new Document as described in {@link Decrypter}
     * @return the decrypted XMLObject
     * @throws DecryptionException exception indicating a decryption error, possibly because
     *          the decrypted data contained more than one top-level Element, or some
     *          non-Element Node type.
     */
    public XMLObject decryptData(EncryptedData encryptedData, boolean rootInNewDocument) throws DecryptionException {
        
        List<XMLObject> xmlObjects = decryptDataToList(encryptedData, rootInNewDocument);
        if (xmlObjects.size() != 1) {
            throw new DecryptionException("The decrypted data contained more than one XMLObject child");
        }
        
        return xmlObjects.get(0);
    }
    
    /**
     * Decrypts the supplied EncryptedData and returns the resulting list of XMLObjects.
     * 
     * This will succeed only if the decrypted EncryptedData contains at the top-level
     * only DOM Elements (not other types of DOM Nodes).
     * 
     * @param encryptedData encrypted data element containing the data to be decrypted
     * @return the list decrypted top-level XMLObjects
     * @throws DecryptionException exception indicating a decryption error, possibly because
     *          the decrypted data contained DOM nodes other than type of Element
     */
    public List<XMLObject> decryptDataToList(EncryptedData encryptedData) throws DecryptionException {
        return decryptDataToList(encryptedData, false);
    }
    
    /**
     * Decrypts the supplied EncryptedData and returns the resulting list of XMLObjects.
     * 
     * This will succeed only if the decrypted EncryptedData contains at the top-level
     * only DOM Elements (not other types of DOM Nodes).
     * 
     * @param encryptedData encrypted data element containing the data to be decrypted
     * @param rootInNewDocument if true, root the underlying Elements of the returned XMLObjects
     *           in a new Document as described in {@link Decrypter}
     * @return the list decrypted top-level XMLObjects
     * @throws DecryptionException exception indicating a decryption error, possibly because
     *          the decrypted data contained DOM nodes other than type of Element
     */
    public List<XMLObject> decryptDataToList(EncryptedData encryptedData, boolean rootInNewDocument)
        throws DecryptionException {
        List<XMLObject> xmlObjects = new LinkedList<XMLObject>();
        
        DocumentFragment docFragment = decryptDataToDOM(encryptedData);
        
        XMLObject xmlObject;
        Node node;
        Element element;
        
        NodeList children = docFragment.getChildNodes();
        for (int i=0; i<children.getLength(); i++) {
            node = children.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                throw new DecryptionException("Top-level node was not of type Element: " + node.getNodeType());
            } else {
                element = (Element) node;
                if (rootInNewDocument) {
                    Document newDoc = null;
                    try {
                        newDoc = parserPool.newDocument();
                    } catch (XMLParserException e) {
                        throw new DecryptionException("Error creating new DOM Document", e);
                    }
                    newDoc.adoptNode(element);
                    newDoc.appendChild(element);
                }
            }
            
            try {
                xmlObject = unmarshallerFactory.getUnmarshaller(element).unmarshall(element);
            } catch (UnmarshallingException e) {
                throw new DecryptionException("Unmarshalling error during decryption", e);
            }
            
            xmlObjects.add(xmlObject);
        }
        
        return xmlObjects;
    }
    /**
     * Decrypts the supplied EncryptedData and returns the resulting DOM {@link DocumentFragment}.
     * 
     * @param encryptedData encrypted data element containing the data to be decrypted
     * @return the decrypted DOM {@link DocumentFragment}
     * @throws DecryptionException exception indicating a decryption error
     */
    public DocumentFragment decryptDataToDOM(EncryptedData encryptedData) throws DecryptionException {
        if (resolver == null && kekResolver == null) {
            throw new DecryptionException("Unable to decrypt EncryptedData, no key resolvers are set");
        }
        
        // TODO - Until Xerces supports LSParser.parseWithContext(), or we come up with another solution
        // to parse a bytestream into a DocumentFragment, we can only support encryption of Elements,
        // not content.
        if (encryptedData.getType().equals(EncryptionConstants.TYPE_CONTENT)) {
            throw new DecryptionException("Decryption of EncryptedData elements of type 'Content' " 
                    + "is not currently supported");
        }
        
        // This is the key that will ultimately be used to decrypt the EncryptedData
        Key dataEncKey = resolveDataDecryptionKey(encryptedData);
        
        if (dataEncKey == null) {
            throw new DecryptionException("Unable to resolve the data encryption key");
        }
        
        Element targetElement = encryptedData.getDOM();
        if (targetElement == null) {
           Marshaller marshaller = 
               Configuration.getMarshallerFactory().getMarshaller(EncryptedData.DEFAULT_ELEMENT_NAME);
           try {
               targetElement = marshaller.marshall(encryptedData);
           } catch (MarshallingException e) {
               throw new DecryptionException("Error marshalling EncryptedData for decryption", e);
           }
        }
        
        XMLCipher xmlCipher;
        try {
            xmlCipher = XMLCipher.getInstance();
            xmlCipher.init(XMLCipher.DECRYPT_MODE, dataEncKey);
        } catch (XMLEncryptionException e) {
            throw new DecryptionException("Error initialzing cipher instance on data decryption", e);
        }
        
        byte[] bytes = null;
        try {
            bytes = xmlCipher.decryptToByteArray(targetElement);
        } catch (XMLEncryptionException e) {
            throw new DecryptionException("Error decrypting the encrypted data element", e);
        }
        if (bytes == null) {
            throw new DecryptionException("EncryptedData could not be decrypted");
        }
 
        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        DocumentFragment docFragment = parseInputStream(input, encryptedData.getDOM().getOwnerDocument());
        
        return docFragment;
    }
    
    /**
     * Decrypts the supplied EncryptedKey and returns the resulting Java security Key object.
     * The algorithm of the decrypted key must be supplied by the caller based on knowledge of
     * the associated EncryptedData information.
     * 
     * @param encryptedKey encrypted key element containing the encrypted key to be decrypted
     * @param algorithm  the algorithm associated with the decrypted key
     * @return the decrypted key
     * @throws DecryptionException exception indicating a decryption error
     */
    public Key decryptKey(EncryptedKey encryptedKey, String algorithm) throws DecryptionException {
        if (kekResolver == null) {
            throw new DecryptionException("Unable to decrypt EncryptedKey, no key encryption key resolver is set");
        }
        
        Element targetElement = encryptedKey.getDOM();
        if (targetElement == null) {
           Marshaller marshaller = 
               Configuration.getMarshallerFactory().getMarshaller(EncryptedKey.DEFAULT_ELEMENT_NAME);
           try {
               targetElement = marshaller.marshall(encryptedKey);
           } catch (MarshallingException e) {
               throw new DecryptionException("Error marshalling EncryptedKey for decryption", e);
           }
        }
        
        //TODO new credential-based algo needs to be reevaluated, for now just getting code to compile again
        Key kek = null;
        Credential kekCred = null;
        try {
            
            CredentialCriteriaSet criteriaSet = 
                new CredentialCriteriaSet( new KeyInfoCredentialCriteria(encryptedKey.getKeyInfo()) );
            kekCred = kekResolver.resolveCredential(criteriaSet);
        } catch (SecurityException e) {
            throw new DecryptionException("Error resolving the key encryption key credential", e);
        }
        if (kekCred.getPrivateKey() != null) {
            kek = kekCred.getPrivateKey();
        } else if (kekCred.getSecretKey() != null) {
            kek = kekCred.getSecretKey();
        }
        if (kek == null) {
            throw new DecryptionException("Failed to resolve key encryption key");
        }
        
        XMLCipher xmlCipher;
        try {
            xmlCipher = XMLCipher.getInstance();
            xmlCipher.init(XMLCipher.UNWRAP_MODE, kek);
        } catch (XMLEncryptionException e) {
            throw new DecryptionException("Error initialzing cipher instance on key decryption", e);
        }
        
        org.apache.xml.security.encryption.EncryptedKey encKey; 
        try {
            encKey = xmlCipher.loadEncryptedKey(targetElement.getOwnerDocument(), targetElement);
        } catch (XMLEncryptionException e) {
            throw new DecryptionException("Error when loading library native encrypted key representation", e);
        }
        
        Key key = null;
        try {
            key = xmlCipher.decryptKey(encKey, algorithm);
        } catch (XMLEncryptionException e) {
            throw new DecryptionException("Error decrypting kek encryption key", e);
        }
        if (key == null) {
            throw new DecryptionException("Key could not be decrypted");
        }
        return key;
    }
    
    /**
     * Resolve the data decryption key for the specified EncryptedData element.
     * 
     * @param encryptedData the EncryptedData which is to be decrypted
     * @return the data decryption key
     * @throws DecryptionException exception indicating a decryption error
     */
    private Key resolveDataDecryptionKey(EncryptedData encryptedData) throws DecryptionException {
        Key dataEncKey = null;
        Credential dataEncKeyCred = null;
        
        //TODO this needs work and re-eval based on new credential-based resolvers,
        // just getting the code to compile again for now.
        
        // This logic is pretty much straight from the C++ decrypter...
        
        // First try and resolve the data encryption key directly
        if (resolver != null) {
            try {
                CredentialCriteriaSet criteriaSet = 
                    new CredentialCriteriaSet( new KeyInfoCredentialCriteria(encryptedData.getKeyInfo()) );
                dataEncKeyCred = resolver.resolveCredential(criteriaSet);
                dataEncKey = dataEncKeyCred.getSecretKey();
            } catch (SecurityException e) {
                throw new DecryptionException("Error resolving data encryption key", e);
            }
        }
        
        // If no key was resolved and have a KEK resolver, try to resolve keys from
        // KeyInfo/EncryptedKey's obtained from directly within this EncryptedData.
        if (dataEncKey == null && kekResolver != null) {
            String algorithm = encryptedData.getEncryptionMethod().getAlgorithm();
            if (algorithm == null || algorithm.equals("")) {
                throw new DecryptionException("No EncryptionMethod/@Algorithm set, key decryption cannot proceed.");
            }
           
            if (encryptedData.getKeyInfo() != null) {
                List<EncryptedKey> encryptedKeys = encryptedData.getKeyInfo().getEncryptedKeys();
                for (EncryptedKey encryptedKey: encryptedKeys) {
                    try {
                        dataEncKey = decryptKey(encryptedKey, algorithm);
                        if (dataEncKey != null) {
                            break;
                        }
                    } catch (DecryptionException e) {
                       log.warn(e.getMessage());
                    }
                }
            }
            
            //TODO rewrite based on credential resolver.
            // If no key was resolved, and our data encryption key resolver is an EncryptedKey resolver
            // specialization capable of resolving EncryptedKey's from another context, try that.
            /*
            if (dataEncKey == null && resolver instanceof EncryptedKeyInfoResolver) {
                EncryptedKeyInfoResolver ekr = (EncryptedKeyInfoResolver) resolver;
                EncryptedKey encryptedKey = null;
                try {
                    encryptedKey = ekr.resolveKey(encryptedData);
                } catch (KeyException e) {
                    throw new DecryptionException("Error in EncryptedKeyInfoResolver", e);
                }
                if (encryptedKey != null) {
                    try {
                        dataEncKey = decryptKey(encryptedKey, algorithm); 
                    } catch (DecryptionException e) {
                        log.warn(e.getMessage());
                    }
                }
            }
            */
        }
        
        return dataEncKey;
    }

    /* NOTE: this currently won't work because Xerces doesn't implement LSParser.parseWithContext().
     * Hopefully they will in the future.
     */
    /*
    private DocumentFragment parseInputStreamLS(InputStream input, Document owningDocument)
            throws DecryptionException {
        
        DOMImplementationLS domImpl = getDOMImplemenationLS();
        
        LSParser parser = domImpl.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, null);
        if (parser == null) {
            throw new DecryptionException("LSParser was null");
        }
        
        //DOMConfiguration config=parser.getDomConfig();
        //DOMErrorHandlerImpl errorHandler=new DOMErrorHandlerImpl();
        //config.setParameter("error-handler", errorHandler);
        
        LSInput lsInput = domImpl.createLSInput();
        if (lsInput == null) {
            throw new DecryptionException("LSInput was null");
        }
        lsInput.setByteStream(input);
        
        DocumentFragment container = owningDocument.createDocumentFragment();
        //TODO Xerces currently doesn't support LSParser.parseWithContext()
        parser.parseWithContext(lsInput, container, LSParser.ACTION_REPLACE_CHILDREN);
        
        return container;
    }
    */
    
    /*
    private DOMImplementationLS getDOMImplemenationLS() throws DecryptionException {
        if (domImplLS != null) {
            return domImplLS;
        }

        // get an instance of the DOMImplementation registry
        DOMImplementationRegistry registry;
        try {
            registry = DOMImplementationRegistry.newInstance();
        } catch (ClassCastException e) {
            throw new DecryptionException("Error creating new error of DOMImplementationRegistry", e);
        } catch (ClassNotFoundException e) {
            throw new DecryptionException("Error creating new error of DOMImplementationRegistry", e);
        } catch (InstantiationException e) {
            throw new DecryptionException("Error creating new error of DOMImplementationRegistry", e);
        } catch (IllegalAccessException e) {
            throw new DecryptionException("Error creating new error of DOMImplementationRegistry", e);
        }

        // get a new instance of the DOM Level 3 Load/Save implementation
        DOMImplementationLS newDOMImplLS = (DOMImplementationLS) registry.getDOMImplementation("LS 3.0");
        if (newDOMImplLS == null) {
            throw new DecryptionException("No LS DOMImplementation could be found");
        } else {
            domImplLS = newDOMImplLS;
        }

        return domImplLS;
    }
    */

    
    /**
     * Parse the specified input stream in a DOM DocumentFragment, owned by the specified
     * Document.
     * 
     * @param input the InputStream to parse
     * @param owningDocument the Document which will own the returned DocumentFragment
     * @return a DocumentFragment
     * @throws DecryptionException thrown if there is an error parsing the input stream
     */
    private DocumentFragment parseInputStream(InputStream input, Document owningDocument) throws DecryptionException {
        // Since Xerces currently seems not to handle parsing into a DocumentFragment
        // without a bit hackery,  use this to simulate, so we can keep the API 
        // the way it hopefully will look in the future.  Obviously this only works for 
        // input streams containing valid XML instances, not fragments.
        
        Document newDocument = null;
        try {
            newDocument = parserPool.parse(input);
        } catch (XMLParserException e) {
            throw new DecryptionException("Error parsing decrypted input stream", e);
        }
        
        Element element = newDocument.getDocumentElement();
        owningDocument.adoptNode(element);
        
        DocumentFragment container = owningDocument.createDocumentFragment();
        container.appendChild(element);

        return container;
    }
}