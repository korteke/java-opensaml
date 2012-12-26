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
 * Classes which model lookup criteria used as input to a  {@link org.opensaml.security.Resolver}.
 * Criteria are typically used by resolvers in a resolver-specific manner to either lookup or extract
 * information from a source, or to constrain or filter the type of information that will be returned.
 * 
 * <p>This package provides some implementations of {@link org.opensaml.security.Criteria} which
 * may have general applicability throughout the library. Criteria implementations which are more
 * specialized in nature may be found in other packages, such as {@link org.opensaml.security.x509}.</p>
 */
package org.opensaml.security.criteria;