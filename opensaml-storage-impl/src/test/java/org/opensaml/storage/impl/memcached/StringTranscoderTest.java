/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package org.opensaml.storage.impl.memcached;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Unit test for {@link edu.vt.middleware.idp.storage.StringTranscoder}.
 */
public class StringTranscoderTest {

    private StringTranscoder transcoder = new StringTranscoder();

    @DataProvider(name = "testStrings")
    public static Object[][] testStrings() {
        return new Object[][] {
                new Object[] {"English"},
                new Object[] {"Российская aka Russian"},
                new Object[] {"官话 aka Mandarin"},
        };
    }

    @Test(dataProvider = "testStrings")
    public void testEncodeDecode(final String s) {
        assertEquals(transcoder.decode(transcoder.encode(s)), s);
    }
}