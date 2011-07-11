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

package org.opensaml.util.net;

import java.util.List;

import org.opensaml.util.Pair;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Unit test for {@link HttpSupport}. */
public class UriSupportTest {

    @Test
    public void testGetRawQueryStringParameter() {
        String param = UriSupport.getRawQueryStringParameter(null, null);
        Assert.assertEquals(param, null);

        param = UriSupport.getRawQueryStringParameter("", null);
        Assert.assertEquals(param, null);

        param = UriSupport.getRawQueryStringParameter("", "");
        Assert.assertEquals(param, null);

        param = UriSupport.getRawQueryStringParameter("foo", null);
        Assert.assertEquals(param, null);

        param = UriSupport.getRawQueryStringParameter("foo", "");
        Assert.assertEquals(param, null);

        String queryString = "paramName1=paramValue1&paramName%3D=paramValue%26&paramName2";
        param = UriSupport.getRawQueryStringParameter(queryString, "paramName1");
        Assert.assertEquals(param, "paramName1=paramValue1");

        param = UriSupport.getRawQueryStringParameter(queryString, "paramName=");
        Assert.assertEquals(param, "paramName%3D=paramValue%26");

        param = UriSupport.getRawQueryStringParameter(queryString, "paramName2");
        Assert.assertEquals(param, "paramName2");

        queryString = "?paramName1=paramValue1&paramName%3D=paramValue%26&paramName2#";
        param = UriSupport.getRawQueryStringParameter(queryString, "paramName1");
        Assert.assertEquals(param, "paramName1=paramValue1");

        param = UriSupport.getRawQueryStringParameter(queryString, "paramName=");
        Assert.assertEquals(param, "paramName%3D=paramValue%26");

        param = UriSupport.getRawQueryStringParameter(queryString, "paramName2");
        Assert.assertEquals(param, "paramName2");
    }

    @Test
    public void testParseQueryString() {
        List<Pair<String, String>> params = UriSupport.parseQueryString(null);
        Assert.assertTrue(params.isEmpty());

        params = UriSupport.parseQueryString("");
        Assert.assertTrue(params.isEmpty());

        String queryString = "paramName1=paramValue1&paramName%3D=paramValue%26&paramName2";
        params = UriSupport.parseQueryString(queryString);
        Assert.assertTrue(params.contains(new Pair("paramName1", "paramValue1")));
        Assert.assertTrue(params.contains(new Pair("paramName=", "paramValue&")));
        Assert.assertTrue(params.contains(new Pair("paramName2", null)));

        queryString = "?paramName1=paramValue1&paramName%3D=paramValue%26&paramName2#";
        params = UriSupport.parseQueryString(queryString);
        Assert.assertTrue(params.contains(new Pair("paramName1", "paramValue1")));
        Assert.assertTrue(params.contains(new Pair("paramName=", "paramValue&")));
        Assert.assertTrue(params.contains(new Pair("paramName2", null)));
    }
}