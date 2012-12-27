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
 * <p>Implementations of {@link org.opensaml.security.EvaluableCriteria} where the parameterized 
 * evaluation target type is {@link org.opensaml.security.credential.Credential}. This is also represented
 * by the marker interface {@link org.opensaml.security.credential.criteria.EvaluableCredentialCriterion}.</p> 
 * 
 * <p>Also contains {@link org.opensaml.security.credential.criteria.EvaluableCredentialCriteriaRegistry},
 * which is capable of looking up and returning a particular implementation of EvaluableCredentialCriteria
 * which should be used to evaluate a given (non-evaluable) {@link org.opensaml.security.Criteria} against
 * a Credential.</p>
 */
package org.opensaml.security.credential.criteria.impl;