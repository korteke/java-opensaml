/*
 * Copyright 2008 Members of the EGEE Collaboration.
 * Copyright 2008 University Corporation for Advanced Internet Development, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opensaml.ws.wstrust;

/**
 * The WS-Trust 1.3 constants.
 * 
 * @see "WS-Trust 1.3 Specification"
 * 
 */
public class WSTrustConstants {

    //
    // WS-Trust
    //
    /** WS-Trust version */
    public final static String WST_VERSION= "1.3";

    /** WS-Trust Profile 1.3 identifier */
    public final static String WST_PROFILE_ID= "urn:mace:switch.ch:doc:wst:profile:200711";

    /** WS-Trust QName prefix */
    public final static String WST_PREFIX= "wst";

    /** WS-Trust 1.3 namespace */
    public final static String WST_NS= "http://docs.oasis-open.org/ws-sx/ws-trust/200512";

    /** Request type suffixes */
    protected final static String REQUESTTYPE_ISSUE= "/Issue";

    protected final static String REQUESTTYPE_CANCEL= "/Cancel";

    protected final static String REQUESTTYPE_VALIDATE= "/Validate";

    protected final static String REQUESTTYPE_RENEW= "/Renew";

    //
    // WS-Addressing
    //
    /** WS-Addressing RequestSecurityToken (RST) action URIs */
    public final static String WSA_ACTION_RST_ISSUE= WST_NS + "/RST"
            + REQUESTTYPE_ISSUE;

    public final static String WSA_ACTION_RST_CANCEL= WST_NS + "/RST"
            + REQUESTTYPE_CANCEL;

    public final static String WSA_ACTION_RST_VALIDATE= WST_NS + "/RST"
            + REQUESTTYPE_VALIDATE;

    public final static String WSA_ACTION_RST_RENEW= WST_NS + "/RST"
            + REQUESTTYPE_RENEW;

    /** WS-Addressing RequestSecurityTokenResponse (RSTR) action URIs */
    public final static String WSA_ACTION_RSTR_ISSUE= WST_NS + "/RSTR"
            + REQUESTTYPE_ISSUE;

    public final static String WSA_ACTION_RSTR_CANCEL= WST_NS + "/RSTR"
            + REQUESTTYPE_CANCEL;

    public final static String WSA_ACTION_RSTR_VALIDATE= WST_NS + "/RSTR"
            + REQUESTTYPE_VALIDATE;

    public final static String WSA_ACTION_RSTR_RENEW= WST_NS + "/RSTR"
            + REQUESTTYPE_RENEW;

    /** WS-Addressing RequestSecurityTokenResponse (RSTRC) final action URIs */
    public final static String WSA_ACTION_RSTRC_ISSUE= WST_NS + "/RSTRC"
            + REQUESTTYPE_ISSUE;

    public final static String WSA_ACTION_RSTRC_CANCEL= WST_NS + "/RSTRC"
            + REQUESTTYPE_CANCEL;

    public final static String WSA_ACTION_RSTRC_VALIDATE= WST_NS + "/RSTRC"
            + REQUESTTYPE_VALIDATE;

    public final static String WSA_ACTION_RSTRC_RENEW= WST_NS + "/RSTRC"
            + REQUESTTYPE_RENEW;

}
