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

/**
 * Interfaces and classes related to credentials and ways to represent them.
 * Developers will generally create and populate CredentialResolvers
 * during application initialization time. CredentialCriteria can then be
 * constructed to model the information the application has about a
 * particular credential and then provided to the CredentialResolver in
 * order to retrieve the previously loaded credential information. Here are a
 * couple of use cases where this approach might be used:
 * <ul>
 *     <li>An application wishes to decrypt a message from one of many
 *     peers. The encrypted message contains a KeyInfo which itself contains
 *     the public key used to encrypt the data. The application can then use
 *     the public key to lookup its appropriate private key and decrypt the
 *     message.</li>
 *     <li>An application uses client-cert authentication via TLS when
 *     communicating with a peer. Upon receipt of the peers certificate the
 *     application uses the provide entity certificate to lookup additional
 *     information associated with this credential, including CRLs, to
 *     determine if the credential should be trusted. It then looks up it's
 *     credential for the given peer and uses it to authenticate. Once
 *     completed the application stores the TLS session key in a
 *     CredentialResolver so that it may be used during encryption processes.
 *     </li>
 * </ul>
 */
package org.opensaml.security.credential;